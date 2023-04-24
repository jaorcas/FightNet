package com.jaorcas.fightnet.providers;

import android.content.Context;

import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.enums.EnumGames;
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
                String name = (String) object.get("nombre");
                String url = (String) object.get("imagen");

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
                String name = (String) object.get("nombre");
                String url = (String) object.get("imagen");

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



}
