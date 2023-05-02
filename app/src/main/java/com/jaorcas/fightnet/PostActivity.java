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
import android.widget.AdapterView;
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
import com.jaorcas.fightnet.adapters.CharacterAdapter;
import com.jaorcas.fightnet.adapters.GamesAdapter;
import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.enums.EnumMedia;
import com.jaorcas.fightnet.models.Character;
import com.jaorcas.fightnet.models.Game;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.CharactersProvider;
import com.jaorcas.fightnet.providers.GamesProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.jaorcas.fightnet.providers.UploadMediaProvider;
import com.jaorcas.fightnet.utils.FileUtil;

import java.io.File;
import java.util.Date;
import java.util.List;


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
    CharactersProvider charactersProvider;

    //ELEMENTOS UI
    TextInputEditText textInputTitle;
    TextInputEditText textInputDescription;
    Button buttonPost;
    CircleImageView circleBack;
    Spinner spinnerGames;
    Spinner spinnerCharacters;

    String gameTitle = "";
    String description = "";
    EnumGames gameSelected = EnumGames.DRAGON_BALL_FIGHTERZ;
    String characterSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //PROVIDERS
        uploadMediaProvider = new UploadMediaProvider();
        postProvider = new PostProvider();
        authProvider = new AuthProvider();
        gamesProvider = new GamesProvider();
        charactersProvider = new CharactersProvider();

        //ELEMENTOS UI
        textInputDescription = findViewById(R.id.textInputDescription);
        circleBack = findViewById(R.id.circleArrowBack);
        spinnerGames = findViewById(R.id.spinnerGames);
        spinnerCharacters = findViewById(R.id.spinnerCharacters);

        //SPINNER DE JUEGOS
        GamesAdapter gamesAdapter = new GamesAdapter(this,gamesProvider.getAllGamesList());
        gamesAdapter.setDropDownViewResource(R.layout.my_dropdown_item);
        spinnerGames.setAdapter(gamesAdapter);

        //SPINNER PERSONAJES
        //OBTENEMOS LA LISTA DE LOS PERSONAJES
        List<Character> listCharacters;
        listCharacters = charactersProvider.getCharacterListByEnum(this,gameSelected);

        CharacterAdapter characterAdapter = new CharacterAdapter(this,listCharacters);
        spinnerCharacters.setAdapter(characterAdapter);

        //SPINNERGAMES LISTENER
        spinnerGames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Game game = (Game) parent.getItemAtPosition(position);
                gameTitle = game.getName();
                List<Character> allCharacters = charactersProvider.getCharacterListByEnum(PostActivity.this, game.getEnumGame());
                characterAdapter.clear();
                characterAdapter.addAll(allCharacters);
                spinnerCharacters.setSelection(0);
                characterAdapter.notifyDataSetChanged();
                gameSelected = game.getEnumGame();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //OBTENEMOS EL PERSONAJE SELECCIONADO
        spinnerCharacters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Character character = (Character) parent.getItemAtPosition(position);

                if(character.getName().equals("-"))
                    characterSelected = null;
                else
                    characterSelected = character.getName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


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

                clickPost();

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
        buttonPost.setEnabled(false);
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
                            post.setGameTitle(gameTitle);
                            post.setDescription(description);
                            post.setDescriptionLowCase(description.toLowerCase());
                            post.setTimestamp(new Date().getTime());

                            if(characterSelected != null) post.setCharacter(characterSelected);

                            postProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> _task) {
                                    clearForm();
                                    if(_task.isSuccessful())
                                        System.out.println("El post se almacenó correctamente");
                                    else
                                        System.out.println("El post NO se almacenó correctamente");

                                }
                            });
                        }
                    });


                    Toast.makeText(PostActivity.this, "La imagen se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    buttonPost.setEnabled(true);
                    Toast.makeText(PostActivity.this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveVideo(){
        buttonPost.setEnabled(false);
        uploadMediaProvider.saveVideo(PostActivity.this,videoFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                            post.setVideo(url);
                            post.setGameTitle(gameTitle);
                            post.setDescription(description);
                            post.setDescriptionLowCase(description.toLowerCase());
                            post.setTimestamp(new Date().getTime());
                            if(characterSelected != null) post.setCharacter(characterSelected);

                            postProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> _task) {
                                    clearForm();
                                    if(_task.isSuccessful())
                                        System.out.println("El post se almacenó correctamente");
                                    else
                                        System.out.println("El post NO se almacenó correctamente");

                                }
                            });
                        }
                    });


                    Toast.makeText(PostActivity.this, "El video se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    buttonPost.setEnabled(true);
                    Toast.makeText(PostActivity.this, "Error al guardar el video", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void clickPost(){

        //EL TITULO SERÁ ELEGIDO POR EL SPINNER EN LA PARTE SUPERIOR

        description = textInputDescription.getText().toString();

        if(!description.isEmpty()){
            if(imageFile!=null){
                saveImage();
            }else if(videoFile!=null){
                saveVideo();
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
        characterSelected = null;

        //UNA VEZ LIMPIAMOS EL FORMULARIO, VOLVEMOS A LA ACTIVIDAD PRINCIPAL
        finish();

    }


}