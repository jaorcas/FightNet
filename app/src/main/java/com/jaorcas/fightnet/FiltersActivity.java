package com.jaorcas.fightnet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.jaorcas.fightnet.adapters.PostsAdapter;
import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.GamesProvider;
import com.jaorcas.fightnet.providers.PostProvider;

public class FiltersActivity extends AppCompatActivity {

    EnumGames extraGame;
    String characterSelectedGame;

    GamesProvider gamesProvider;
    AuthProvider authProvider;
    RecyclerView recyclerView;
    PostProvider postProvider;
    PostsAdapter postsAdapter;
    Toolbar toolbar;
    TextView textviewNumberPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        //OBTENEMOS LOS DATOS EXTRA DEL INTENT
        extraGame = (EnumGames) getIntent().getSerializableExtra("game");
        characterSelectedGame = getIntent().getStringExtra("character");

        //UI
        textviewNumberPosts = findViewById(R.id.textviewNumberPost);

        //PROVIDERS
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        gamesProvider = new GamesProvider();

        //TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.recyclerViewFilterPosts);

        //ESTO ES PARA QUE NOS MUESTRE LOS ELEMENTOS UNO DEBAJO DEL OTRO
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);


        //COMPROBAMOS SI HA ELEGIDO ALGÚN PERSONAJE O NO
        if (characterSelectedGame.equals("-")) characterSelectedGame = null;

        if(characterSelectedGame == null){
            Toast.makeText(this, "El juego seleccionado ha sido " + extraGame, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "El juego seleccionado ha sido " + extraGame +  "con el pj " + characterSelectedGame, Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = postProvider.getPostByCategoryAndTimestamp(gamesProvider.getGameNameByEnum(extraGame));
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        postsAdapter = new PostsAdapter(options, FiltersActivity.this, textviewNumberPosts);
        recyclerView.setAdapter(postsAdapter);
        postsAdapter.startListening();
    }
}