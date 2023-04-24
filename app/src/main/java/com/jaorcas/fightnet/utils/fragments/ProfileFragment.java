package com.jaorcas.fightnet.utils.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaorcas.fightnet.EditProfileActivity;
import com.jaorcas.fightnet.MainActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.adapters.PostsAdapter;
import com.jaorcas.fightnet.adapters.UserPostsAdapter;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    View view;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    PostProvider postProvider;

    //UI
    CircleImageView circleImageProfile;
    ImageView imageviewBanner;
    TextView textViewUsername;
    TextView textViewEmail;
    TextView textViewPostNumber;
    ImageView buttonMenu;
    TextView textViewUserDescription;
    TextView textViewPostExists;
    Toolbar toolbar;

    RecyclerView recyclerView;
    UserPostsAdapter userPostsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //UI
        circleImageProfile = view.findViewById(R.id.circleImageProfile);
        imageviewBanner = view.findViewById(R.id.imageViewBanner);
        textViewUsername = view.findViewById(R.id.textViewUsername);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewPostNumber = view.findViewById(R.id.textViewPostNumber);
        buttonMenu = view.findViewById(R.id.buttonMenu);
        textViewUserDescription = view.findViewById(R.id.textViewUserDescription);
        textViewPostExists = view.findViewById(R.id.textViewPostExists);

        recyclerView = view.findViewById(R.id.recyclerViewUserPosts);

        //TOOLBAR
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //ESTO ES PARA QUE EL RECYCLER NOS MUESTRE LAS LINEAS UNA DEBAJO DE OTRA
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        postProvider = new PostProvider();

        buttonMenu.setOnClickListener(view -> showMenu(buttonMenu));

        getUserInfo();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = postProvider.getPostByUser(authProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        userPostsAdapter = new UserPostsAdapter(options, getContext());
        recyclerView.setAdapter(userPostsAdapter);
        userPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userPostsAdapter.stopListening();
    }

    public void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(),v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.profile_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.itemEditProfile:
                goToEditProfile();
                return true;
            case R.id.itemLogOut:
                logOut();
                return true;
            default:
                return false;
        }
    }

    private void goToEditProfile(){
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);

    }
    private void logOut(){
        authProvider.logOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                        textViewUsername.setText(documentSnapshot.getString("username"));
                    }

                    textViewEmail.setText(documentSnapshot.getString("email"));

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

                    if(documentSnapshot.contains("userDescription")){
                        textViewUserDescription.setText(documentSnapshot.getString("userDescription"));
                    }


                    getPostNumber();
                }
            }
        });

    }

    //OBTENEMOS LA CANTIDAD DE ELEMENTOS QUE TIENE LA CONSULTA
    private void getPostNumber(){
        postProvider.getPostByUser(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

}