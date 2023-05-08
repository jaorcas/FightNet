package com.jaorcas.fightnet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.models.DBFZCharacter;
import com.jaorcas.fightnet.models.TableRowAttack;
import com.jaorcas.fightnet.providers.CharactersProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CharacterInfoDBFZActivity extends AppCompatActivity {

    DBFZCharacter character;
    CharactersProvider charactersProvider;
    AlertDialog dialog;

    //UIs
    TextView textViewCharacterName;
    ImageView imageViewCharacter;
    TableRow tableRowHeader;

    TextView textViewStartup;
    TextView textViewBlock;

    int numberOfRows = 0;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_info_dbfzactivity);

        //VENTANA DE "ESPERE UN MOMENTO"
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();



        //UIS
        textViewCharacterName = findViewById(R.id.textViewCharacterName);
        imageViewCharacter = findViewById(R.id.imageViewCharacter);
        tableRowHeader = findViewById(R.id.tableRowHeader);

        //PROVIDERS
        charactersProvider = new CharactersProvider();

        String characterName = getIntent().getStringExtra("character");
        character = (DBFZCharacter) charactersProvider.getCharacterByEnumAndCharacterName(this, EnumGames.DRAGON_BALL_FIGHTERZ, characterName);

        if(character.getImageURL_fullbody()!=null)
            dialog.show();

        //LE ASIGNAMOS EL NOMBRE Y LA IMAGEN
        textViewCharacterName.setText(character.getName().toUpperCase());

        if(character.getImageURL_fullbody()!=null){
            Picasso.get().load(character.getImageURL_fullbody()).into(imageViewCharacter, new Callback() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    dialog.dismiss();
                }
            });
        }

        tableRowHeader.setBackgroundColor(getResources().getColor(R.color.purple_dark));

        List<TableRowAttack> rows = new ArrayList<>();

        //PARA PODER MANEJAR LOS DATOS CORRECTAMENTE, HE CREADO UNA CLASE CON TABLEROW Y CHARACTER,
        //DE ESTA FORMA PUEDO ASIGNAR LOS VALORES QUE NECESITE SIN TENER QUE REPETIR CÓDIGO
        rows.add(new TableRowAttack(findViewById(R.id.tableRow5L),character.getAttack5L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow5LL),character.getAttack5LL()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow5LLL),character.getAttack5LLL()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow2L),character.getAttack2L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow5M),character.getAttack5M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow2M),character.getAttack2M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow3M),character.getAttack3M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow5H),character.getAttack5H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow2H),character.getAttack2H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow3H),character.getAttack3H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj5L),character.getAttackj5L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj2L),character.getAttackj2L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj5M),character.getAttackj5M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj5H),character.getAttackj5H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj2H),character.getAttackj2H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow236L),character.getAttack236L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow236M),character.getAttack236M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow236H),character.getAttack236H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj236L),character.getAttackj236L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj236M),character.getAttackj236M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj236H),character.getAttackj236H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow214L),character.getAttack214L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow214M),character.getAttack214M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow214H),character.getAttack214H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj214L),character.getAttackj214L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj214M),character.getAttackj214M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj214H),character.getAttackj214H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow22L),character.getAttack22L()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow22M),character.getAttack22M()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow22H),character.getAttack22H()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow5S),character.getAttack5S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow2S),character.getAttack2S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj5S),character.getAttackj5S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj2S),character.getAttackj2S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow236S),character.getAttack236S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRow214S),character.getAttack214S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj236S),character.getAttackj236S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowj214S),character.getAttackj214S()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowAssistA),character.getAssistA()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowAssistB),character.getAssistB()));
        rows.add(new TableRowAttack(findViewById(R.id.tableRowAssistC),character.getAssistC()));


        for (int i = 0; i < rows.size(); i++) {
            setFrameData(rows.get(i));
        }


    }

    private void setFrameData(TableRowAttack tableRowAttack){

        if(tableRowAttack.getAttack()==null) {
            tableRowAttack.getTableRow().setVisibility(View.GONE);
            return;
        }

        //START UP
        textViewStartup = (TextView) tableRowAttack.getTableRow().getChildAt(1);
        textViewStartup.setText(tableRowAttack.getAttack().getStartUp());
        //BLOCK
        if(!tableRowAttack.getAttack().getBlock().equals("-")){
            textViewBlock = (TextView) tableRowAttack.getTableRow().getChildAt(2);
            textViewBlock.setText(tableRowAttack.getAttack().getBlock());
        }

        if(Integer.parseInt(textViewBlock.getText().toString())>0) {
            textViewBlock.setTextColor(Color.GREEN);
        }else if(Integer.parseInt(textViewBlock.getText().toString())<0){
            textViewBlock.setTextColor(Color.RED);
        }

        if(numberOfRows%2 ==0)
            tableRowAttack.getTableRow().setBackgroundColor(getResources().getColor(R.color.dark_gray));
        else
            tableRowAttack.getTableRow().setBackgroundColor(getResources().getColor(R.color.purple_dark));

        numberOfRows++;
    }

}