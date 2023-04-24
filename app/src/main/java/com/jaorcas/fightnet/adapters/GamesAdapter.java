package com.jaorcas.fightnet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.Game;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class GamesAdapter extends ArrayAdapter<Game> {

    Context context;


    //IMPORTANTE NO CAMBIAR ESTE ORDEN
    List<Game> gamesList;


    public GamesAdapter(Context context, List<Game> gamesList){
        super(context, R.layout.my_selected_item, gamesList);
        this.context = context;
        this.gamesList = gamesList;
    }

    // Override these methods and instead return our custom view (with image and text)
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    //ESTA FUNCION CREA LA "VISTA" DE CADA COLUMNA, CON SU IMAGEN Y SU TEXTO
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.my_dropdown_item, parent, false);

        //IMAGEN Y TEXTO

        ImageView gameIcon = view.findViewById(R.id.img);

        //OBTENEMOS LAS IMAGENES DE LOS JUEGOS
        int drawable;

        //ESTE ORDEN NO PUEDE CAMBIARSE
        switch (position){
            case 0: drawable = R.drawable.logo_dbfz;
                break;
            case 1: drawable = R.drawable.logo_ggstrive;
                break;
            case 2: drawable = R.drawable.logo_sf5;
                break;
            case 3: drawable = R.drawable.logo_sf6;
                break;
            case 4: drawable = R.drawable.logo_gbversus;
                break;
            case 5: drawable = R.drawable.logo_tekken7;
                break;
            default: drawable = R.drawable.logo_dbfz;

        }

        gameIcon.setImageResource(drawable);

        return view;
    }


}
