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
import com.google.firebase.auth.AuthResult;
import com.jaorcas.fightnet.models.User;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.UsersProvider;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextInputEditText textInputUsername;
    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;
    TextInputEditText textInputConfirmPassword;
    Button buttonRegister;

    AuthProvider authProvider;
    UsersProvider usersProvider;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //IDs
        circleImageView = findViewById(R.id.circleArrowBack);
        textInputUsername = findViewById(R.id.textInputUsername);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        //PROVIDERS
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        //VOLVER ATRÁS
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Finish es para volver a la pantalla anterior
                finish();
            }
        });
    }

    private void register(){

        String username = textInputUsername.getText().toString();
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();
        String confirmPassword = textInputConfirmPassword.getText().toString();

        //COMPROBAMOS QUE TODOS LOS CAMPOS ESTAN RELLENOS
        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //COMPROBAMOS SI EL EMAIL ES VÁLIDO
        if(!isEmailValid(email)){
            Toast.makeText(this, "El email no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        //COMPROBAMOS SI LAS CONTRASEÑAS SON IGUALES
        if(password.equals(confirmPassword)){
            if(password.length()<6) {
                Toast.makeText(this, "La contraseña debe ser al menos de 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }


        //SI TODO LO DEMÁS SE CUMPLE, CREAREMOS EL USUARIO
        createUser(username, email,password);

    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void createUser(String username, String email, String password){

        dialog.show();
        authProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //COMPROBAMOS QUE SE HA CREADO CON EXITO
                if(task.isSuccessful()){

                    String userID = authProvider.getUid();

                    User user = new User();
                    user.setId(userID);
                    user.setEmail(email);
                    user.setUsername(username);
                    //ALMACENAMOS LA FECHA EN LA QUE SE CREO EL USUARIO
                    user.setTimestamp(new Date().getTime());

                    //AÑADIMOS LA INFORMACIÓN A LA BASE DE DATOS FIRESTONE
                    usersProvider.createUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                //ESTA LINEA ES PARA QUE CUANDO EL USUARIO LE DE HACIA ATRÁS, NO VUELVA A LA VENTANA DE REGISTRO
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                Toast.makeText(RegisterActivity.this, "El usuario se ha añadido a la base de datos", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "El usuario no se ha añadido a la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                //EN CASO NEGATIVO, MOSTRAMOS AL USUARIO QUE NO SE HA PODIDO REGISTRAR EL USUARIO
                }else{
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Ese email ya está en uso.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
