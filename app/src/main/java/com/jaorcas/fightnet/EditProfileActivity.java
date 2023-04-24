package com.jaorcas.fightnet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.jaorcas.fightnet.enums.EnumProfileEditImage;
import com.jaorcas.fightnet.models.User;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.UploadMediaProvider;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.jaorcas.fightnet.utils.FileUtil;
import com.jaorcas.fightnet.utils.fragments.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView circleImageProfile;
    ImageView imageviewBanner;
    TextInputEditText textUsername;
    CircleImageView backButton;
    Button buttonEditProfile;
    TextInputEditText textViewUserDescription;

    AlertDialog dialog;
    //IMAGENES
    private final int GALLERY_REQUEST_CODE_PROFILE = 1; //REQUEST CODE PARA LA FOTO DE PEFIL
    private final int GALLERY_REQUEST_CODE_BANNER = 2; //REQUEST CODE PARA LA FOTO DEL BANNER
    File imageFileProfile, imageFileBanner;

    AuthProvider authProvider;
    UploadMediaProvider uploadMediaProvider;
    UsersProvider usersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        circleImageProfile = findViewById(R.id.circleImageProfile);
        imageviewBanner = findViewById(R.id.imageViewBanner);
        textUsername = findViewById(R.id.textInputUsername);
        backButton = findViewById(R.id.backButton);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        textViewUserDescription = findViewById(R.id.textInputUserDescription);

        authProvider = new AuthProvider();
        uploadMediaProvider = new UploadMediaProvider();
        usersProvider = new UsersProvider();

        //VENTANA DE "ESPERE UN MOMENTO"
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        //ACTUALIZAMOS VISUALMENTE LOS CAMPOS QUE YA TENEMOS DEL USUARIO
        getUserInfo();

        circleImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(GALLERY_REQUEST_CODE_PROFILE);
            }
        });

        imageviewBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(GALLERY_REQUEST_CODE_BANNER);
            }
        });

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUpdateProfile();
            }
        });


        //PULSAMOS EN EL BOTON HACIA ATRÁS
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //ABRIMOS LA GALERIA DEL DISPOSITIVO
    private void openGallery(int galleryCode){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //ESTO ES PARA QUE NOS DEJE ELEGIR TANTO IMAGENES COMO VIDEOS
        intent.setType("image/*");

        startActivityForResult(intent, galleryCode);
    }

    private void clickUpdateProfile(){

        String username = textUsername.getText().toString();

        if (username.isEmpty()){
            Toast.makeText(this, "Escribe un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();
        //HACEMOS UNAS COMPROBACIONES PARA VER A QUE FUNCION LLAMAMOS
        //ESTAS COMPROBACIONES NO ME PARECEN OPTIMAS, PERO NO HE ENCONTRADO SOLUCION A LOS IMAGEFILE NULOS
        if(imageFileProfile!=null && imageFileBanner!=null){
            saveMultiplesImages();
        }else if(imageFileProfile!=null && imageFileBanner == null){
            saveOneImage(imageFileProfile, EnumProfileEditImage.PROFILE);
        }else if(imageFileProfile==null && imageFileBanner != null){
            saveOneImage(imageFileBanner, EnumProfileEditImage.BANNER);
        //SI LLEGAMOS AQUI, SIGNIFICA QUE NO HA ELEGIDO NINGUNA IMAGEN NUEVA
        }else{
            User user = new User();
            user.setId(authProvider.getUid());
            user.setUsername(textUsername.getText().toString());
            user.setUserDescription(textViewUserDescription.getText().toString());
            updateInfo(user);
        }


    }


    //ESTO ES PARA ELEGIR LAS IMAGENES DEL PERFIL Y DEL BANNER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //FOTO PERFIL
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            try {

                imageFileProfile = FileUtil.from(EditProfileActivity.this, data.getData());
                circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(imageFileProfile.getAbsolutePath()));

            } catch (Exception e) {
                Log.d("ERROR", "Se ha producido un error " + e.getMessage());
                Toast.makeText(EditProfileActivity.this, "Se ha producido un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        //BANNER
        } else if (requestCode == GALLERY_REQUEST_CODE_BANNER && resultCode == RESULT_OK) {
            try {

                imageFileBanner = FileUtil.from(EditProfileActivity.this, data.getData());
                imageviewBanner.setImageBitmap(BitmapFactory.decodeFile(imageFileBanner.getAbsolutePath()));


            } catch (Exception e) {
                Log.d("ERROR", "Se ha producido un error " + e.getMessage());
                Toast.makeText(EditProfileActivity.this, "Se ha producido un error " + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }

    private void saveOneImage(File imageFile, EnumProfileEditImage imageType){

        dialog.show();
        //EMPEZAMOS CON LA IMAGEN DE PERFIL
        uploadMediaProvider.saveImage(EditProfileActivity.this,imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){

                    uploadMediaProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //ESTA ES LA URL DEl PERFIL
                            String url = uri.toString();

                            User user = new User();
                            user.setId(authProvider.getUid());
                            user.setUsername(textUsername.getText().toString());
                            user.setUserDescription(textViewUserDescription.getText().toString());

                            //AQUI ES DONDE FILTRAMOS SI NOS HA VENIDO LA IMAGEN DE PERFIL O EL BANNER
                            switch (imageType){
                                case PROFILE:
                                    user.setImageProfile(url);
                                break;
                                case BANNER:
                                    user.setImageBanner(url);
                                break;
                            }

                            updateInfo(user);
                        }
                    });
                }else{
                    dialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //SI HEMOS SELECCIONADO LAS 2 IMAGENES, USAREMOS ESTA FUNCION
    private void saveMultiplesImages(){
        dialog.show();
        //EMPEZAMOS CON LA IMAGEN DE PERFIL
        uploadMediaProvider.saveImage(EditProfileActivity.this,imageFileProfile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){

                    uploadMediaProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //ESTA ES LA URL DEl PERFIL
                            String urlProfile = uri.toString();

                            //A PARTIR DE AQUI ES PARA LA IMAGEN DE BANNER
                            uploadMediaProvider.saveImage(EditProfileActivity.this,imageFileBanner).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){

                                        uploadMediaProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri){
                                                String urlBanner = uri.toString();

                                                //UNA VEZ TENEMOS LAS 2 URLS, ACTUALIZAMOS LOS DATOS DE USUARIO
                                                User user = new User();
                                                user.setId(authProvider.getUid());
                                                user.setUsername(textUsername.getText().toString());
                                                user.setUserDescription(textViewUserDescription.getText().toString());
                                                user.setImageProfile(urlProfile);
                                                user.setImageBanner(urlBanner);

                                                updateInfo(user);
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    });
                }else{
                    dialog.dismiss();

                    Toast.makeText(EditProfileActivity.this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //ACTUALIZAMOS LA BASE DE DATOS CON LOS DATOS OBTENIDOS
    private void updateInfo(User user){

        usersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "La información se actualizo de forma correcta", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(EditProfileActivity.this, "La imagen no se ha podido actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //ACTUALIZAMOS VISUALMENTE LA INFORMACIÓN PARA QUE COINCIDA CON LA DE LA BASE DE DATOS
    private void getUserInfo(){
        usersProvider.getUser(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //NOS ASEGURAMOS QUE EL USUARIO EXISTA
                if(documentSnapshot.exists()){

                    //ESTAS COMPROBACIONES SIRVEN PARA QUE NO PETE LA APLICACION EN CASO DE HABER ALGUN PROBLEMA
                    //COMO POR EJEMPLO QUE SE BORRE UNA IMAGEN DE LA BASE DE DATOS
                    if(documentSnapshot.contains("username")) {
                        textUsername.setText(documentSnapshot.getString("username"));
                    }

                    if(documentSnapshot.contains("userDescription")) {
                        textViewUserDescription.setText(documentSnapshot.getString("userDescription"));
                    }

                    //PICASSO ES PARA ASIGNAR LAS 2 IMAGENES DE LA BASE DE DATOS A LOS CONTENEDORES
                    if(documentSnapshot.contains("imageProfile")){
                        String urlImageProfile = documentSnapshot.getString("imageProfile");
                        //SI LA URL ES VACÍA, NO LE ASIGNAMOS LA IMAGEN PARA EVITAR ERRORES
                        if(urlImageProfile!=null) Picasso.get().load(urlImageProfile).into(circleImageProfile);
                    }
                    if(documentSnapshot.contains("imageBanner")){
                        String urlImageBanner = documentSnapshot.getString("imageBanner");
                        if(urlImageBanner!=null) Picasso.get().load(urlImageBanner).into(imageviewBanner);
                    }
                }
            }
        });

    }

}