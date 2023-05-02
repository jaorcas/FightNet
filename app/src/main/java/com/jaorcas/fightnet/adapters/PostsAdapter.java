package com.jaorcas.fightnet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaorcas.fightnet.ExtraUserProfileActivity;
import com.jaorcas.fightnet.PostInfoActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.Like;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.models.User;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.LikesProvider;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post,PostsAdapter.ViewHolder> {

    UsersProvider usersProvider;
    LikesProvider likesProvider;
    AuthProvider authProvider;
    TextView textViewNumberPosts;
    Context context;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
    }

    //ESTA CLASE SE USA PARA CUANDO SE FILTRA QUE MUESTRE CUANTOS RESULTADOS HAY CON ESE FILTRO
    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textViewNumberPosts){
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
        this.textViewNumberPosts = textViewNumberPosts;
    }


    //ES EL CONTENIDO QUE QUIERO SE MUESTRE EN CADA UNA DE LAS VISTAS
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        //CON ESTO OBTENEMOS LA INFORMACIÓN COMPLETA DEL POST
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postID = document.getId();

        //CUANDO FILTRAMOS, PASAMOS POR PARAMETRO EL TEXTO PARA QUE NOS DIGA CUANTOS POSTS HAY DE ESE JUEGO
        if(textViewNumberPosts !=null){
            textViewNumberPosts.setText(String.valueOf(getSnapshots().size()));
        }

        holder.title.setText(post.getGameTitle());
        holder.description.setText(post.getDescription());

        //ELEGIMOS SI ES UNA IMAGEN O UN VÍDEO

        //IMAGEN
        if(post.getImage()!=null && !post.getImage().isEmpty()){
            Picasso.get().load(post.getImage()).into(holder.postImage);
        //VIDEO
        }else if(post.getVideo()!=null && !post.getVideo().isEmpty()){
            showVideoHideImage(holder);

            Glide.with(context)
                    .asFile()
                    .load(post.getVideo())
                    .into(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            holder.postVideo.setVideoPath(resource.getAbsolutePath());
                            holder.postVideo.setFocusable(false);
                            holder.postVideo.start();
                        }
                    });
        }

        //LE ASIGNAMOS A LA IMAGEN FUNCIONALIDAD SI LE HACEMOS CLICK
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostInfoActivity.class);
                intent.putExtra("idPost", postID);
                context.startActivity(intent);
            }
        });

        //LE ASIGNAMOS AL VIDEO FUNCIONALIDAD SI LE HACEMOS CLICK
        holder.postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostInfoActivity.class);
                intent.putExtra("idPost", postID);
                context.startActivity(intent);
            }
        });

        //LE ASIGNAMOS A LA FOTO DEL USUARIO FUNCIONALIDAD SI LE HACEMOS CLICK
        holder.imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExtraUserProfileActivity.class);
                intent.putExtra("idUser", post.getIdUser());
                context.startActivity(intent);
            }
        });

        //LE ASIGNAMOS AL LIKE FUNCIONALIDAD SI LE HACEMOS CLICK
        holder.imageViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setUserID(authProvider.getUid());
                like.setPostID(postID);
                like.setTimestamp(new Date().getTime());
                likesProvider.like(like,holder.imageViewLikes);
            }
        });


        getUserInfo(post.getIdUser(), holder);
        likesProvider.getNumberOfLikes(postID, holder.textViewLikes);
        likesProvider.checkLikeExists(postID,authProvider.getUid(), holder.imageViewLikes);

    }

    private void getUserInfo(String idUser, ViewHolder holder){

        usersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    holder.username.setText(documentSnapshot.getString("username"));
                    holder.email.setText(documentSnapshot.getString("email"));

                    String imageProfile = documentSnapshot.getString("imageProfile");
                    if(imageProfile!=null){
                        Picasso.get().load(imageProfile).into(holder.imageUserProfile);
                    }

                }
            }
        });
    }



    //ESTA ES LA VISTA QUE QUEREMOS USAR
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    //LA PLANTILLA DE UN POST
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView description;
        ImageView postImage;
        VideoView postVideo;
        TextView username;
        TextView email;
        CircleImageView imageUserProfile;
        TextView textViewLikes;
        ImageView imageViewLikes;
        View viewHolder;
        RelativeLayout videoContainer;

        public ViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.titlePost);
            description = view.findViewById(R.id.descriptionPost);
            postImage = view.findViewById(R.id.imageViewPost);
            postVideo = view.findViewById(R.id.videoViewPost);
            username = view.findViewById(R.id.textViewUsername);
            email = view.findViewById(R.id.textViewEmail);
            imageUserProfile = view.findViewById(R.id.imageViewUserProfile);
            imageViewLikes = view.findViewById(R.id.imageViewLike);
            textViewLikes = view.findViewById(R.id.textViewLike);
            videoContainer = view.findViewById(R.id.video_container);
            viewHolder = view;
        }

    }

    private void showVideoHideImage(ViewHolder holder){
        holder.videoContainer.setVisibility(View.VISIBLE);
        holder.postImage.setVisibility(View.GONE);
    }


}
