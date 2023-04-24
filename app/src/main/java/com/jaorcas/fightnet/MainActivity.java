package com.jaorcas.fightnet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.User;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.UsersProvider;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    TextView textViewRegister;
    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;
    Button button;

    AuthProvider authProvider;

    ImageView buttonGoogle;

    //GOOGLE SIGN IN
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    UsersProvider usersProvider;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //IDs
        textViewRegister = findViewById(R.id.textViewRegister);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        button = findViewById(R.id.buttonLogin);
        buttonGoogle = findViewById(R.id.buttonGoogle);

        //PROVIDERS
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        //VENTANA DE "ESPERE UN MOMENTO"
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        //GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //BOTÓN DE INICIO DE SESION
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


        //BOTON DE LOG IN CON GOOGLE
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //BOTÓN DE REGISTRO (TEXTO INFERIOR)
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login(){
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, introduce el email y la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();

        authProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    //EN CASO DE QUE EL LOGIN SEA CORRECTO, LO LLEVAMOS A LA ACTIVIDAD CORRESPONDIENTE
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "El email o la contraseña no son correctos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //SI LA SESION DEL USUARIO EXISTE, ES DECIR, SE CONECTÓ ANTERIORMENTE, IRÁ A HOME
        if(authProvider.getUserSession()!=null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //GOOGLE SIGN IN ↓

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        dialog.show();
        authProvider.googleLogin(idToken).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //SI LA TAREA FUE EXITOSA, VAMOS A LA HOMEACTIVITY
                        if (task.isSuccessful()) {

                            //PERO ANTES, COMPROBAMOS PRIMERO SI EXISTE EL USUARIO
                            String userID = authProvider.getUid();
                            checkUserExits(userID);

                            Log.d(TAG, "signInWithCredential:success");

                        } else {
                            dialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
        // AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        //  auth.signInWithCredential(credential)
    }
    // [END auth_with_google]

    //COMPROBAMOS SI EXISTE EL USUARIO
    private void checkUserExits(String userID) {

        usersProvider.getUser(userID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //SI EL DOCUMENTO EXISTE, SIGNIFICA QUE USUARIO YA HABÍA INICIADO SESIÓN AL MENOS UNA VEZ
                if(documentSnapshot.exists()){
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                //SI ES LA PRIMERA VEZ QUE INICIA SESIÓN, DEBEMOS AÑADIR LA INFORMACION A LA BASE DE DATOS
                else{
                    String email = authProvider.getEmail();

                    //USUARIO
                    User user = new User();
                    user.setEmail(email);
                    user.setId(userID);

                    usersProvider.createUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        //UNA VEZ LO AÑADIMOS, COMPROBAMOS QUE SE HA AÑADIDO CORRECTAMENTE
                        //SI HA SIDO ASÍ VAMOS A LA ACTIVIDAD Home, SI NO ES ASÍ LO MOSTRAMOS POR PANTALLA
                        public void onComplete(@NonNull Task<Void> task) {

                            dialog.dismiss();
                            //COMO VAMOS A PEDIRLE MÁS INFORMACIÓN, VAMOS A MANDARLO A COMPLETEACTIVITY, EN VEZ DE HOMEACTIVITY
                            if(task.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, "N", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

}

