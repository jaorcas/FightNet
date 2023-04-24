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
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.jaorcas.fightnet.adapters.GamesAdapter;
import com.jaorcas.fightnet.enums.EnumMedia;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.GamesProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.jaorcas.fightnet.providers.UploadMediaProvider;
import com.jaorcas.fightnet.utils.FileUtil;

import java.io.File;
import java.util.Date;


import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageView imageViewUpload;
    VideoView videoViewUpload;

    private EnumMedia mediaSelected;

    AlertDialog dialog;

    private final int GALLERY_REQUEST_CODE = 1;
    File imageFile, videoFile;

    //PROVIDERS
    UploadMediaProvider uploadMediaProvider;
    PostProvider postProvider;
    AuthProvider authProvider;
    GamesProvider gamesProvider;

    //ELEMENTOS UI
    TextInputEditText textInputTitle;
    TextInputEditText textInputDescription;
    Button buttonPost;
    CircleImageView circleBack;
    Spinner spinner;

    String title = "";
    String description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //PROVIDERS
        uploadMediaProvider = new UploadMediaProvider();
        postProvider = new PostProvider();
        authProvider = new AuthProvider();
        gamesProvider = new GamesProvider();

        //ELEMENTOS UI
        textInputDescription = findViewById(R.id.textInputDescription);
        circleBack = findViewById(R.id.circleArrowBack);

        //SPINNER DE JUEGOS
        spinner = findViewById(R.id.spinnerGames);

        GamesAdapter adapter = new GamesAdapter(getApplicationContext(), gamesProvider.getAllGamesList());
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        spinner.setAdapter(adapter);


        //VENTANA DE "ESPERE UN MOMENTO"
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        //VOLVER HACIA ATRÁS
        circleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //IMAGEN
        imageViewUpload = findViewById(R.id.imageViewUpload);
        imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //VIDEO
        videoViewUpload = findViewById(R.id.videoViewUpload);
        videoViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //BOTÓN DE PUBLICAR
        buttonPost = findViewById(R.id.buttonPost);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mediaSelected==null){
                    Toast.makeText(PostActivity.this, "Inserta alguna imagen/video", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.show();

                switch (mediaSelected){
                    case IMAGE:
                        clickPost();
                    break;
                    case VIDEO:
                        saveVideo();
                    break;
                    default:
                }

            }
        });


    }

    //ABRIMOS LA GALERIA DEL DISPOSITIVO
    private void openGallery(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //ESTO ES PARA QUE NOS DEJE ELEGIR TANTO IMAGENES COMO VIDEOS
        intent.setType("*/*");

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {

                //SI HEMOS ELEGIDO UNA IMAGEN
                if(data.getData().toString().contains("image")){

                    mediaSelected = EnumMedia.IMAGE;

                    imageViewUpload.setVisibility(View.VISIBLE);
                    videoViewUpload.setVisibility(View.INVISIBLE);

                    imageFile = FileUtil.from(PostActivity.this, data.getData());
                    imageViewUpload.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                    Toast.makeText(PostActivity.this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();

                //SI HEMOS ELEGIDO UN VÍDEO
                }else if(data.getData().toString().contains("video")){

                    mediaSelected = EnumMedia.VIDEO;

                    imageViewUpload.setVisibility(View.INVISIBLE);
                    videoViewUpload.setVisibility(View.VISIBLE);

                    videoFile = FileUtil.from(PostActivity.this, data.getData());
                    videoViewUpload.setVideoPath(videoFile.getAbsolutePath());
                    videoViewUpload.start();
                    Toast.makeText(PostActivity.this, "Video seleccionado", Toast.LENGTH_SHORT).show();
                }


            }catch (Exception e){
                Log.d("ERROR", "Se ha producido un error " + e.getMessage());
                Toast.makeText(PostActivity.this, "Se ha producido un error " +e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void saveImage(){

        uploadMediaProvider.saveImage(PostActivity.this,imageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dialog.dismiss();
                if(task.isSuccessful()){

                    uploadMediaProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //ESTA ES LA URL DE LA IMAGEN QUE HEMOS SUBIDO
                            String url = uri.toString();

                            Post post = new Post();
                            post.setIdUser(authProvider.getUid());
                            post.setImage(url);
                            post.setGameTitle(title);
                            post.setDescription(description);
                            post.setTimestamp(new Date().getTime());

                            postProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> _task) {
                                    clearForm();
                                    if(_task.isSuccessful()){
                                        Toast.makeText(PostActivity.this, "El post se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(PostActivity.this, "No se ha podido almacenar la informacion", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                    Toast.makeText(PostActivity.this, "La imagen se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostActivity.this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveVideo(){

        uploadMediaProvider.saveVideo(PostActivity.this,videoFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(PostActivity.this, "El vídeo se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostActivity.this, "Error al guardar el video", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void clickPost(){

        //EL TITULO SERÁ EL JUEGO ELEGIDO EN EL SPINNER
        title = spinner.getSelectedItem().toString();
        description = textInputDescription.getText().toString();

        if(!description.isEmpty()){
            if(imageFile!=null){
                saveImage();
            }else{
                //POR AHORA VAMOS A OBLIGAR AL USUARIO QUE PONGA UNA IMAGEN
                Toast.makeText(this, "Por favor interte una imagen", Toast.LENGTH_SHORT).show();
            }

        }else{
            dialog.dismiss();
            Toast.makeText(this, "Por favor complete los campos y la categoría", Toast.LENGTH_SHORT).show();
        }

    }

    //LIMPIAMOS EL FORMULARIO
    private void clearForm(){

        textInputDescription.setText("");
        imageViewUpload.setImageResource(R.drawable.ic_media);
        imageViewUpload.setVisibility(View.VISIBLE);
        videoViewUpload.setVisibility(View.INVISIBLE);
        description = "";
        imageFile = null;
        videoFile = null;

    }


}