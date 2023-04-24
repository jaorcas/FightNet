package com.jaorcas.fightnet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.slider.Slider;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.SliderItem;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context, List<SliderItem> sliderItems) {
        this.context = context;
        mSliderItems = sliderItems;
    }

    //ESTE ES EL LAYOUT QUE VAMOS A UTILIZAR
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    //AQUI ESTABLECEMOS LOS VALORES QUE QUIERO MOSTRAR EN CADA UNO DE LOS LAYOUT
    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        SliderItem sliderItem = mSliderItems.get(position);

        if(sliderItem.getImageURL()!=null){
            Picasso.get().load(sliderItem.getImageURL()).into(viewHolder.imageViewSlider);
        }


    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewSlider;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewSlider = itemView.findViewById(R.id.imageViewSlider);

            this.itemView = itemView;
        }
    }

}