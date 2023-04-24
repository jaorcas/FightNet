package com.jaorcas.fightnet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jaorcas.fightnet.PostInfoActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.Comment;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentsAdapter.ViewHolder> {


    Context context;
    UsersProvider usersProvider;

    public CommentsAdapter(FirestoreRecyclerOptions<Comment> options, Context context){
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
    }

    //ES EL CONTENIDO QUE QUIERO SE MUESTRE EN CADA UNA DE LAS VISTAS
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        //CON ESTO OBTENEMOS LA INFORMACIÓN COMPLETA DEL POST
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String commentID = document.getId();

        //OBTENEMOS EL ID DEL USUARIO QUE HA PUESTO EL COMENTARIO
        String idUser = document.getString("idUser");

        holder.comment.setText(comment.getCommentText());
        getUserInfo(idUser,holder);

    }

    private void getUserInfo(String idUser, ViewHolder holder){
        usersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    if(documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.username.setText(username);
                    }

                    if(documentSnapshot.contains("imageProfile")){

                        String imageProfile = documentSnapshot.getString("imageProfile");

                        if(imageProfile!=null){

                            Picasso.get().load(imageProfile).into(holder.usernameImage);
                        }

                    }
                }
            }
        });
    }

    //ESTA ES LA VISTA QUE QUEREMOS USAR
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    //LA PLANTILLA DE UN POST
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView comment;
        CircleImageView usernameImage;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            username = view.findViewById(R.id.textViewUsername);
            comment = view.findViewById(R.id.textViewComment);
            usernameImage = view.findViewById(R.id.circleImageUserProfile);
            viewHolder = view;
        }

    }

}
