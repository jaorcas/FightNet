package com.jaorcas.fightnet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.Character;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class CharacterAdapter extends ArrayAdapter<Character> {

    Context context;
    private List<Character> characters;

    public CharacterAdapter(Context context, List<Character> characters){
        super(context, R.layout.my_selected_item, characters);
        this.context = context;
        this.characters = characters;

    }

    //ESTA FUNCION CREA LA "VISTA" DE CADA COLUMNA, CON SU IMAGEN Y SU TEXTO
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dropdown_characters, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.imgDropDown);
        TextView textView = view.findViewById(R.id.textViewDropdown);

        Character character = characters.get(position);
        textView.setText(character.getName());

        // Carga la imagen desde la URL con Glide
        Glide.with(context)
                .load(character.getImageURL())
                .placeholder(R.drawable.character_empty)
                .into(imageView);

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
