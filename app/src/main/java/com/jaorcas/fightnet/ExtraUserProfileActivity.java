package com.jaorcas.fightnet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaorcas.fightnet.adapters.UserPostsAdapter;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExtraUserProfileActivity extends AppCompatActivity {

    //UI
    CircleImageView circleImageProfile;
    ImageView imageviewBanner;
    TextView textViewUsername;
    TextView textViewEmail;
    TextView textViewPostNumber;
    TextView textViewUserDescription;
    TextView textViewPostExists;
    Toolbar toolbar;

    //PROVIDERS
    AuthProvider authProvider;
    UsersProvider usersProvider;
    PostProvider postProvider;

    String extraUserID;

    //LOS POSTS DE LOS USUARIOS
    RecyclerView recyclerView;
    UserPostsAdapter userPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_user_profile);

        //UI
        circleImageProfile = findViewById(R.id.circleImageProfile);
        imageviewBanner = findViewById(R.id.imageViewBanner);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPostNumber = findViewById(R.id.textViewPostNumber);
        textViewUserDescription = findViewById(R.id.textViewUserDescription);
        textViewPostExists = findViewById(R.id.textViewPostExists);

        //TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewUserPosts);
        //ESTO ES PARA QUE EL RECYCLER NOS MUESTRE LAS LINEAS UNA DEBAJO DE OTRA
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExtraUserProfileActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //PROVIDERS
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        postProvider = new PostProvider();

        extraUserID = getIntent().getStringExtra("idUser");

        getUserInfo();
        getPostNumber();

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = postProvider.getPostsByUserOrderedByTimestamp(extraUserID);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query.orderBy("timestamp", Query.Direction.DESCENDING), Post.class).build();

        userPostsAdapter = new UserPostsAdapter(options, ExtraUserProfileActivity.this);
        recyclerView.setAdapter(userPostsAdapter);
        userPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userPostsAdapter.stopListening();
    }


    //ACTUALIZAMOS VISUALMENTE LA INFORMACIÓN PARA QUE COINCIDA CON LA DE LA BASE DE DATOS
    private void getUserInfo(){

        usersProvider.getUser(extraUserID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //NOS ASEGURAMOS QUE EL USUARIO EXISTA
                if(documentSnapshot.exists()){

                    //ESTAS COMPROBACIONES SIRVEN PARA QUE NO PETE LA APLICACION EN CASO DE HABER ALGUN PROBLEMA
                    //COMO POR EJEMPLO QUE SE BORRE UNA IMAGEN DE LA BASE DE DATOS
                    if(documentSnapshot.contains("username")) {
                        textViewUsername.setText(documentSnapshot.getString("username"));
                    }

                    textViewEmail.setText(documentSnapshot.getString("email"));

                    //PICASSO ES PARA ASIGNAR LAS 2 IMAGENES DE LA BASE DE DATOS A LOS CONTENEDORES
                    if(documentSnapshot.contains("imageProfile") && documentSnapshot.getString("imageProfile")!=null){
                        String urlImageProfile = documentSnapshot.getString("imageProfile");
                        //SI LA URL ES VACÍA, NO LE ASIGNAMOS LA IMAGEN PARA EVITAR ERRORES
                        if(urlImageProfile!=null) Picasso.get().load(urlImageProfile).into(circleImageProfile);
                    }

                    if(documentSnapshot.contains("imageBanner") && documentSnapshot.getString("imageBanner")!=null){
                        String urlImageBanner = documentSnapshot.getString("imageBanner");
                        if(urlImageBanner!=null) Picasso.get().load(urlImageBanner).into(imageviewBanner);
                    }

                    if(documentSnapshot.contains("userDescription")){
                        textViewUserDescription.setText(documentSnapshot.getString("userDescription"));
                    }
                }

                getPostNumber();
            }
        });

    }

    //OBTENEMOS LA CANTIDAD DE ELEMENTOS QUE TIENE LA CONSULTA
    private void getPostNumber(){
        postProvider.getPostsByUser(extraUserID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPost = queryDocumentSnapshots.size();
                textViewPostNumber.setText(String.valueOf(numberPost));

                //CAMBIAMOS EL TEXTO QUE NOS MUESTRA SI HAY PUBLICACIONES
                if(numberPost>0){
                    textViewPostExists.setText("Publicaciones:");
                }else{
                    textViewPostExists.setText("No hay publicaciones");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) finish();
        return true;
    }
}