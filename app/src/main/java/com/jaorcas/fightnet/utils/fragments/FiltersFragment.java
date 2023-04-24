package com.jaorcas.fightnet.utils.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.jaorcas.fightnet.FiltersActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.adapters.CharacterAdapter;
import com.jaorcas.fightnet.adapters.GamesAdapter;
import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.models.Character;
import com.jaorcas.fightnet.models.Game;
import com.jaorcas.fightnet.providers.CharactersProvider;
import com.jaorcas.fightnet.providers.GamesProvider;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltersFragment extends Fragment {

    GamesProvider gamesProvider;
    CharactersProvider charactersProvider;
    EnumGames gameSelected = EnumGames.DRAGON_BALL_FIGHTERZ;
    Button buttonFilter;
    String characterSelectedName;

    View view;
    Spinner spinnerGames;
    Spinner spinnerCharacters;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FiltersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FiltersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltersFragment newInstance(String param1, String param2) {
        FiltersFragment fragment = new FiltersFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filters, container, false);

        gamesProvider = new GamesProvider();
        charactersProvider = new CharactersProvider();
        buttonFilter = view.findViewById(R.id.buttonFilter);

        spinnerGames = view.findViewById(R.id.spinnerGames);
        spinnerCharacters = view.findViewById(R.id.spinnerCharacters);


        //SPINNER DE JUEGOS
        GamesAdapter gamesAdapter = new GamesAdapter(getContext(),gamesProvider.getAllGamesList());
        gamesAdapter.setDropDownViewResource(R.layout.my_dropdown_item);
        spinnerGames.setAdapter(gamesAdapter);

        //SPINNER PERSONAJES

        //OBTENEMOS LA LISTA DE LOS PERSONAJES
        List<Character> listCharacters;
        listCharacters = charactersProvider.getCharacterListByEnum(getContext(),gameSelected);

        CharacterAdapter characterAdapter = new CharacterAdapter(getContext(),listCharacters);
        spinnerCharacters.setAdapter(characterAdapter);


        //SPINNERGAMES LISTENER
        spinnerGames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Game game = (Game) parent.getItemAtPosition(position);
                List<Character> allCharacters = charactersProvider.getCharacterListByEnum(getContext(), game.getEnumGame());
                characterAdapter.clear();
                characterAdapter.addAll(allCharacters);
                spinnerCharacters.setSelection(0);
                characterAdapter.notifyDataSetChanged();
                gameSelected = game.getEnumGame();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //GUARDAMOS EL PERSONAJE ELEGIDO, POR SI LUEGO HACEMOS DICHA FUNCIONALIDAD
        spinnerCharacters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Character character = (Character) parent.getItemAtPosition(position);
                characterSelectedName = character.getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        //FUNCIONALIDAD DEL BOTON
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFiltersActivity(gameSelected);
            }
        });


        return view;
    }

    private void goToFiltersActivity(EnumGames enumGames){
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("game",enumGames);
        intent.putExtra("character", characterSelectedName);

        startActivity(intent);
    }


}