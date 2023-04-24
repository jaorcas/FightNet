package com.jaorcas.fightnet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.jaorcas.fightnet.models.User;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.UsersProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {


    TextInputEditText textInputUsername;
    Button buttonConfirm;

    AuthProvider authProvider;
    UsersProvider usersProvider;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        //IDs
        textInputUsername = findViewById(R.id.textInputUsername);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        //PROVIDERS
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        //VENTANA DE "ESPERE UN MOMENTO"
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register(){

        String username = textInputUsername.getText().toString();

        if(username.isEmpty()){
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        updateUser(username);

    }

    //EN ESTA FUNCIÓN VAMOS A AÑADIR LA INFORMACIÓN EXTRA QUE QUEREMOS DEL USUARIO
    private void updateUser(String username){
        String userID = authProvider.getUid();

        Map<String,Object> map = new HashMap<>();
        map.put("username", username);

        User user = new User();
        user.setUsername(username);
        user.setId(userID);

        dialog.show();

        //COMO ESTAMOS AÑADIENDO INFORMACIÓN A LA BASE DE DATOS, EN VEZ DE "SET" USAREMOS "UPDATE"
        usersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(CompleteProfileActivity.this, "No se ha podido crear el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}