package com.jaorcas.fightnet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.jaorcas.fightnet.providers.AuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResetPasswordActivity extends AppCompatActivity implements AuthProvider.EmailExistsCallback {

    CircleImageView circleImageView;
    TextInputEditText textInputEmail;
    Button buttonResetPassword;

    AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        //IDs
        circleImageView = findViewById(R.id.circleArrowBack);
        textInputEmail = findViewById(R.id.textInputEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);

        authProvider = new AuthProvider();

        //VOLVER ATRÁS
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Finish es para volver a la pantalla anterior
                finish();
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register(){

        String email = textInputEmail.getText().toString();

        //COMPROBAMOS QUE TODOS LOS CAMPOS ESTAN RELLENOS
        if(email.isEmpty()){
            Toast.makeText(this, "Por favor, introduce el email", Toast.LENGTH_SHORT).show();
            return;
        }

        //COMPROBAMOS SI EL EMAIL ES VÁLIDO
        if(!isEmailValid(email)){
            Toast.makeText(this, "El email no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        authProvider.checkEmailExists(email, new AuthProvider.EmailExistsCallback() {
            @Override
            public void onEmailExists(boolean exists) {
                if (exists) {
                    authProvider.sendPasswordResetEmail(email);
                    Toast.makeText(getApplicationContext(), "Se ha enviado un email para restablecer tu contraseña.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Ocurrió un error al enviar el email de restablecimiento de contraseña
                    Toast.makeText(getApplicationContext(), "El email introducido no existe.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onEmailExists(boolean exists) {

    }
}