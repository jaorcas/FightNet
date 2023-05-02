package com.jaorcas.fightnet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.jaorcas.fightnet.adapters.CommentsAdapter;
import com.jaorcas.fightnet.adapters.SliderAdapter;
import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.models.Character;
import com.jaorcas.fightnet.models.Comment;
import com.jaorcas.fightnet.models.Like;
import com.jaorcas.fightnet.models.SliderItem;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.CharactersProvider;
import com.jaorcas.fightnet.providers.CommentsProvider;
import com.jaorcas.fightnet.providers.GamesProvider;
import com.jaorcas.fightnet.providers.LikesProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.jaorcas.fightnet.utils.RelativeTime;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostInfoActivity extends AppCompatActivity {

    SliderView sliderView;
    SliderAdapter sliderAdapter;

    List<SliderItem> listSliderItem = new ArrayList<>();
    String postID;
    String gameTitle = "";

    RecyclerView recyclerView;
    CommentsAdapter commentsAdapter;

    //PROVIDERS
    PostProvider postProvider;
    UsersProvider usersProvider;
    GamesProvider gamesProvider;
    CommentsProvider commentsProvider;
    AuthProvider authProvider;
    LikesProvider likesProvider;
    CharactersProvider charactersProvider;

    //UI
    CircleImageView circleImageUserProfile;
    TextView textViewUsername;
    TextView textViewEmail;
    TextView textViewGameTitle;
    TextView textViewPostDescription;
    ImageView imageViewGameIcon;
    VideoView videoViewPost;
    TextView textRelativeTime;
    ImageView imageViewLike;
    TextView textViewLike;
    RelativeLayout videoContainer;
    CardView cardViewCharacter;

    TextInputEditText textCommentary;
    Button buttonCommentary;

    String userID = "";
    EnumGames gameEnum = null;
    TextView textViewCharacterName;
    String characterName = null;
    ImageView imageViewCharacterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        sliderView = findViewById(R.id.imageSlider);

        //PROVIDERS
        usersProvider = new UsersProvider();
        postProvider = new PostProvider();
        gamesProvider = new GamesProvider();
        commentsProvider = new CommentsProvider();
        authProvider = new AuthProvider();
        likesProvider = new LikesProvider();
        charactersProvider = new CharactersProvider();

        postID = getIntent().getStringExtra("idPost");

        //UI
        circleImageUserProfile = findViewById(R.id.circleImageUserProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewGameTitle = findViewById(R.id.textViewGameTitle);
        textViewPostDescription = findViewById(R.id.textViewPostDescription);
        imageViewGameIcon = findViewById(R.id.imageViewGameIcon);
        textRelativeTime = findViewById(R.id.textRelativeTime);
        imageViewLike = findViewById(R.id.imageViewLike);
        textViewLike = findViewById(R.id.textViewLike);
        videoViewPost = findViewById(R.id.videoViewPost);
        videoContainer = findViewById(R.id.video_container);
        textViewCharacterName = findViewById(R.id.textViewCharacterName);
        imageViewCharacterIcon = findViewById(R.id.imageViewCharacterIcon);

        //CARDVIEW CHARACTER
        cardViewCharacter = findViewById(R.id.cardViewCharacter);

        //RECYCLERVIEW
        recyclerView = findViewById(R.id.recyclerViewComments);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostInfoActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //ESCRIBIR COMENTARIO
        textCommentary = findViewById(R.id.textInputCommentary);
        textCommentary.addTextChangedListener(textWatcher);
        buttonCommentary = findViewById(R.id.buttonCommentary);

        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Post");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        circleImageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserProfile();
            }
        });

        buttonCommentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createComment(textCommentary.getText().toString());
            }
        });

        imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setUserID(authProvider.getUid());
                like.setPostID(postID);
                like.setTimestamp(new Date().getTime());
                likesProvider.like(like,imageViewLike);
            }
        });

        cardViewCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCharacterInfo();
            }
        });

        getPost();
        likesProvider.getNumberOfLikes(postID,textViewLike);
        likesProvider.checkLikeExists(postID,authProvider.getUid(),imageViewLike);
    }


    //OBTENEMOS LA INFORMACIÓN DEL POST
    private void getPost(){
        postProvider.getPostByPostID(postID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    //AÑADIMOS A NUESTRA LISTA DE IMAGENES LA IMAGEN 1, EN CASO DE QUE LUEGO QUERAMOS AÑADIR MAS
                   if(documentSnapshot.contains("image") && documentSnapshot.getString("image") != null) {
                       String image = documentSnapshot.getString("image");
                       SliderItem item = new SliderItem();
                       item.setImageURL(image);
                       listSliderItem.add(item);

                   }//SI ES UN VÍDEO
                   else if(documentSnapshot.contains("video") && documentSnapshot.getString("video") != null){
                       String videoURL = documentSnapshot.getString("video");
                       showVideoHideImage();

                       VideoView videoViewPost = findViewById(R.id.videoViewPost);

                       Glide.with(PostInfoActivity.this)
                               .asFile()
                               .load(videoURL)
                               .into(new SimpleTarget<File>() {
                                   @Override
                                   public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                       videoViewPost.setVideoPath(resource.getAbsolutePath());
                                       videoViewPost.start();

                                   }
                               });

                       MediaController mediaController = new MediaController(PostInfoActivity.this);
                       mediaController.setAnchorView(videoViewPost);
                       videoViewPost.setMediaController(mediaController);

                   }

                    if(documentSnapshot.contains("description"))
                        textViewPostDescription.setText(documentSnapshot.getString("description"));


                    if(documentSnapshot.contains("gameTitle")){
                        gameTitle = documentSnapshot.getString("gameTitle");
                        textViewGameTitle.setText(gameTitle);
                        imageViewGameIcon.setImageResource(gamesProvider.getImageByCategory(gameTitle));
                        gameEnum = gamesProvider.getEnumByStringGameName(gameTitle);
                    }

                    //OBTENEMOS LA INFORMACIÓN DEL USUARIO
                    if(documentSnapshot.contains("idUser")){
                        userID = documentSnapshot.getString("idUser");
                        getUserInfo(userID);
                    }

                    if(documentSnapshot.contains("timestamp")){

                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostInfoActivity.this);
                        textRelativeTime.setText(relativeTime);

                    }

                    if(documentSnapshot.getString("character")==null)
                        cardViewCharacter.setVisibility(View.GONE);
                    else{
                        cardViewCharacter.setVisibility(View.VISIBLE);
                        characterName = documentSnapshot.getString("character");
                        textViewCharacterName.setText(characterName);
                        Character character = charactersProvider.getCharacterByEnumAndCharacterName2(PostInfoActivity.this, gameEnum,characterName);
                        String url = character.getImageURL();
                        Picasso.get().load(url).into(imageViewCharacterIcon);
                    }




                   instanceSlider();
                }
            }
        });
    }

    //OBTENEMOS LA INFORMACIÓN DEL USUARIO
    public void getUserInfo(String idUser){
        usersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    textViewUsername.setText(documentSnapshot.getString("username"));
                    textViewEmail.setText(documentSnapshot.getString("email"));
                    Picasso.get().load(documentSnapshot.getString("imageProfile")).into(circleImageUserProfile);
                }
            }
        });

    }

    //VAMOS AL PERFIL DEL USUARIO PULSANDO EN SU ICONO
    private void goToUserProfile(){

        if(userID==null){
            return;
        }

        Intent intent = new Intent(PostInfoActivity.this, ExtraUserProfileActivity.class);
        intent.putExtra("idUser", userID);
        startActivity(intent);
    }

    private void goToCharacterInfo(){
        Intent intent = new Intent(PostInfoActivity.this, gamesProvider.getCharacterActivityByEnum(gameEnum));
        intent.putExtra("character", characterName);
        startActivity(intent);
    }

    //ESTO ES PARA QUE EL BOTON ACTUÉ EN FUNCION DEL TEXTO
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String text = textCommentary.getText().toString().trim();
            if(!text.isEmpty()) {buttonCommentary.setEnabled(true);} else {buttonCommentary.setEnabled(false);}

        }
        @Override
        public void afterTextChanged(Editable editable) { }
    };


    //FUNCION PARA CREAR UN COMENTARIO
    private void createComment(String commentText){

        Comment comment = new Comment();
        comment.setCommentText(commentText);
        comment.setIdPost(postID);
        comment.setIdUser(authProvider.getUid());
        comment.setTimestamp(new Date().getTime());

        commentsProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    textCommentary.setText("");
                    Toast.makeText(PostInfoActivity.this, "Se ha añadido el comentario", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostInfoActivity.this, "No se pudo añadir el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Query query = commentsProvider.getCommentsByPostID(postID);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class).build();

        commentsAdapter = new CommentsAdapter(options, PostInfoActivity.this);
        recyclerView.setAdapter(commentsAdapter);
        commentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentsAdapter.stopListening();
    }

    //CONFIGURAMOS EL SLIDER
    private void instanceSlider(){

        sliderAdapter = new SliderAdapter(PostInfoActivity.this,listSliderItem);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.NONE);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setInfiniteAdapterEnabled(false);
        sliderView.setScrollTimeInSec(2);
    }

    private void showVideoHideImage(){
        videoContainer.setVisibility(View.VISIBLE);
        sliderView.setVisibility(View.GONE);
    }


}