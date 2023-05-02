package com.jaorcas.fightnet.providers;

import android.content.Context;

import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.models.Attack;
import com.jaorcas.fightnet.models.Character;
import com.jaorcas.fightnet.models.DBFZCharacter;
import com.jaorcas.fightnet.models.GGStriveCharacter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CharactersProvider {

    public CharactersProvider() {

    }

    //EL CONTEXTO LO NECESITAMOS PARA TRABAJAR CON JSONS
    public <T> List<T> getCharacterListByEnum(Context context, EnumGames enumGames) {

        List<T> characterList = new ArrayList<>();

        switch (enumGames){
            case DRAGON_BALL_FIGHTERZ:
                characterList = (List<T>) getAllDBFZCharacters(context);
                break;
            case GUILTY_GEAR_STRIVE:
                characterList = (List<T>) getAllGGStriveCharacters(context);
                break;
            default:
                System.out.println("ERROR: la lista está vacía");

        }
        return characterList;
    }

    private List<DBFZCharacter> getAllDBFZCharacters(Context context) {

        ArrayList<DBFZCharacter> allCharacters = new ArrayList<>();

        try {
            InputStream rowJSON = context.getResources().openRawResource(R.raw.json_dbfz);
            byte[] buffer = new byte[rowJSON.available()];
            rowJSON.read(buffer);
            rowJSON.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                //OBTENEMOS LA INFO DEL JSON
                JSONObject object = jsonArray.getJSONObject(i);
                String name = (String) object.get("name");
                String url = (String) object.get("image");

                //CREAMOS UN PERSONAJE CON ESTA INFO Y LO AÑADIMOS A LA LISTA
                DBFZCharacter character = new DBFZCharacter();
                character.setName(name);
                character.setImageURL(url);

                //AQUI NO NECESITAMOS LISTAR TODA LA INFORMACIÓN DEL PJ, CON EL NOMBRE Y LA URL NOS VALE

                allCharacters.add(character);

            }

            return allCharacters;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allCharacters;
    }

    //TEST PROBANDOLO DE OTRA FORMA
    private List<DBFZCharacter> getAllDBFZCharacters2(Context context, boolean getAllInfo) {

        ArrayList<DBFZCharacter> allCharacters = new ArrayList<>();

        try {
            InputStream rowJSON = context.getResources().openRawResource(R.raw.json_dbfz);
            byte[] buffer = new byte[rowJSON.available()];
            rowJSON.read(buffer);
            rowJSON.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            DBFZCharacter character = null;

            for (int i = 0; i < jsonArray.length(); i++) {

                character = new DBFZCharacter();
                JSONObject object = jsonArray.getJSONObject(i);

                character.setName(object.getString("name"));
                character.setImageURL(object.getString("image"));


                //AQUI NO NECESITAMOS LISTAR TODA LA INFORMACIÓN DEL PJ, CON EL NOMBRE Y LA URL NOS VALE

                allCharacters.add(character);

            }

            return allCharacters;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allCharacters;
    }



    private List<GGStriveCharacter> getAllGGStriveCharacters(Context context) {

        ArrayList<GGStriveCharacter> allCharacters = new ArrayList<>();

        try {
            InputStream rowJSON = context.getResources().openRawResource(R.raw.json_ggstrive);
            byte[] buffer = new byte[rowJSON.available()];
            rowJSON.read(buffer);
            rowJSON.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                //OBTENEMOS LA INFO DEL JSON
                JSONObject object = jsonArray.getJSONObject(i);
                String name = (String) object.get("name");
                String url = (String) object.get("image");

                //CREAMOS UN PERSONAJE CON ESTA INFO Y LO AÑADIMOS A LA LISTA
                GGStriveCharacter character = new GGStriveCharacter();
                character.setName(name);
                character.setImageURL(url);

                //AQUI NO NECESITAMOS LISTAR TODA LA INFORMACIÓN DEL PJ, CON EL NOMBRE Y LA URL NOS VALE

                allCharacters.add(character);

            }

            return allCharacters;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allCharacters;
    }


    //AQUI TENEMOS LA FUNCIONALIDAD PARA OBTENER UN PERSONAJE EN CONCRETO

    public Character getCharacterByEnumAndCharacterName(Context context, EnumGames gameEnum, String characterName){

        List<Character> allCharacters = getCharacterListByEnum(context, gameEnum);

        for(Character c : allCharacters){
            if(c.getName().equals(characterName)) return c;
        }

        return null;
    }

    public Character getCharacterByEnumAndCharacterName2(Context context, EnumGames enumGames, String characterName) {

        Character character = new Character();

        switch (enumGames) {
            case DRAGON_BALL_FIGHTERZ:
                character = getDBFZCharacter(context, characterName);
                break;
            case GUILTY_GEAR_STRIVE:
                //character = getAllGGStriveCharacters(context);
                break;
            default:
                System.out.println("ERROR: la lista está vacía");

        }
        return character;
    }

    private DBFZCharacter getDBFZCharacter(Context context, String characterName) {

        DBFZCharacter character = new DBFZCharacter();
        Attack attack = new Attack();

        try {
            InputStream rowJSON = context.getResources().openRawResource(R.raw.json_dbfz);
            byte[] buffer = new byte[rowJSON.available()];
            rowJSON.read(buffer);
            rowJSON.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);
            JSONObject statsObject;
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                //RECORREMOS EL JSON HASTA ENCONTRAR EL QUE SE LLAMA IGUAL QUE LO QUE PASAMOS POR PARAMETRO
                if (!object.getString("name").equals(characterName)) {
                    continue;
                }

                character.setName(object.getString("name"));
                character.setImageURL(object.getString("image"));
                character.setImageURL_fullbody(object.getString("image_fullbody"));

                statsObject = object.getJSONObject("5L");
                attack = new Attack("5L",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack5L(attack);

                statsObject = object.getJSONObject("5LL");
                attack = new Attack("5LL",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack5LL(attack);

                statsObject = object.getJSONObject("5LLL");
                attack = new Attack("5LLL",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack5LLL(attack);

                statsObject = object.getJSONObject("2L");
                attack = new Attack("2L",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack2L(attack);

                statsObject = object.getJSONObject("2M");
                attack = new Attack("2M",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack2M(attack);

                statsObject = object.getJSONObject("5M");
                attack = new Attack("5M",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack5M(attack);

                if(object.has("3M")){
                    statsObject = object.getJSONObject("3M");
                    attack = new Attack("3M",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack3M(attack);

                }
                statsObject = object.getJSONObject("5H");
                attack = new Attack("5H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack5H(attack);

                statsObject = object.getJSONObject("2H");
                attack = new Attack("2H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack2H(attack);

                if(object.has("3H")){
                    statsObject = object.getJSONObject("3H");
                    attack = new Attack("3H",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack3H(attack);
                }

                statsObject = object.getJSONObject("j5L");
                attack = new Attack("j5L",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj5L(attack);

                if(object.has("j2L")){
                    statsObject = object.getJSONObject("j2L");
                    attack = new Attack("j2L",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttackj2L(attack);
                }

                statsObject = object.getJSONObject("j5M");
                attack = new Attack("j5M",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj5M(attack);

                statsObject = object.getJSONObject("j5H");
                attack = new Attack("j5H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj5H(attack);

                statsObject = object.getJSONObject("j2H");
                attack = new Attack("j2H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj2H(attack);

                statsObject = object.getJSONObject("236L");
                attack = new Attack("236L",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack236L(attack);

                statsObject = object.getJSONObject("236M");
                attack = new Attack("236M",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack236M(attack);

                statsObject = object.getJSONObject("236H");
                attack = new Attack("236H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack236H(attack);

                statsObject = object.getJSONObject("214L");
                attack = new Attack("214L",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack214L(attack);

                statsObject = object.getJSONObject("214M");
                attack = new Attack("214M",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack214M(attack);

                statsObject = object.getJSONObject("214H");
                attack = new Attack("214H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack214H(attack);

                statsObject = object.getJSONObject("j214L");
                attack = new Attack("j214L",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj214L(attack);

                statsObject = object.getJSONObject("j214M");
                attack = new Attack("j214M",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj214M(attack);

                statsObject = object.getJSONObject("j214H");
                attack = new Attack("j214H",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj214H(attack);

                //SI EXISTE LA VERSION L, LAS DEMÁS TAMBIEN
                if(object.has("22L")){
                    statsObject = object.getJSONObject("22L");
                    attack = new Attack("22L",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack22L(attack);

                    statsObject = object.getJSONObject("22M");
                    attack = new Attack("22M",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack22M(attack);

                    statsObject = object.getJSONObject("22H");
                    attack = new Attack("22H",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack22H(attack);
                }


                statsObject = object.getJSONObject("5S");
                attack = new Attack("5S",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack5S(attack);

                if(object.has("2S")){
                    statsObject = object.getJSONObject("2S");
                    attack = new Attack("2S",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack2S(attack);
                }

                statsObject = object.getJSONObject("j5S");
                attack = new Attack("j5S",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj5S(attack);

                if(object.has("j2S")){
                    statsObject = object.getJSONObject("j2S");
                    attack = new Attack("j2S",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttackj2S(attack);
                }

                statsObject = object.getJSONObject("236S");
                attack = new Attack("236S",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttack236S(attack);

                statsObject = object.getJSONObject("j236S");
                attack = new Attack("j236S",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAttackj236S(attack);

                if(object.has("214S")){
                    statsObject = object.getJSONObject("214S");
                    attack = new Attack("214S",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttack214S(attack);
                }
                if(object.has("j214S")){
                    statsObject = object.getJSONObject("j214S");
                    attack = new Attack("j214S",statsObject.getString("startup"),statsObject.getString("block"));
                    character.setAttackj214S(attack);
                }

                statsObject = object.getJSONObject("Assist A");
                attack = new Attack("Assist A",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAssistA(attack);

                statsObject = object.getJSONObject("Assist B");
                attack = new Attack("Assist B",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAssistB(attack);

                statsObject = object.getJSONObject("Assist C");
                attack = new Attack("Assist C",statsObject.getString("startup"),statsObject.getString("block"));
                character.setAssistC(attack);


            }

            return character;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

}
