package com.jaorcas.fightnet.utils.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaorcas.fightnet.MainActivity;
import com.jaorcas.fightnet.PostActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.adapters.PostsAdapter;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.mancj.materialsearchbar.MaterialSearchBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment  implements  MaterialSearchBar.OnSearchActionListener{

    View view;
    FloatingActionButton fab;
    Toolbar toolbar;
    AuthProvider authProvider;
    RecyclerView recyclerView;
    PostProvider postProvider;
    PostsAdapter postsAdapter;
    PostsAdapter postsAdapterSearch;
    MaterialSearchBar searchBar;

    BottomNavigationView bottomNavigation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        authProvider = new AuthProvider();
        fab = view.findViewById(R.id.actionButton);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        postProvider = new PostProvider();

        //SEARCH BAR
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);


        //ESTO ES PARA QUE NOS MUESTRE LOS ELEMENTOS UNO DEBAJO DEL OTRO
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        /*
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Inicio");
        setHasOptionsMenu(false);
*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPost();
            }
        });

        return view;
    }

    private void goToPost(){
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.itemLogOut){
            logOut();
        }

        return true;
    }

    private void logOut(){
        authProvider.logOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
*/

    @Override
    public void onStart() {
        super.onStart();
        getAllPosts();
    }

    //CUANDO LA APLICACION PASA A SEGUNDO PLANO
    @Override
    public void onStop() {
        super.onStop();
        postsAdapter.stopListening();
        if(postsAdapterSearch!=null){ postsAdapterSearch.stopListening();}
    }

    //METODOS DEL SEARCH BAR
    @Override
    public void onSearchStateChanged(boolean enabled) {

        if(!enabled){
            getAllPosts();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Toast.makeText(getContext(), "Tu busqueda fue: " + text.toString(), Toast.LENGTH_SHORT).show();
        searchByDescription(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    private void searchByDescription(String description){

        super.onStart();
        Query query = postProvider.getPostByDescription(description);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        postsAdapterSearch = new PostsAdapter(options, getContext());
        postsAdapterSearch.notifyDataSetChanged();
        recyclerView.setAdapter(postsAdapterSearch);
        postsAdapterSearch.startListening();
    }

    private void getAllPosts(){
        Query query = postProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class).build();

        postsAdapter = new PostsAdapter(options, getContext());
        postsAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(postsAdapter);
        postsAdapter.startListening();
    }
}