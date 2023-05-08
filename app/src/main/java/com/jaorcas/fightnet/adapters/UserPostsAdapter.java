package com.jaorcas.fightnet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaorcas.fightnet.ExtraUserProfileActivity;
import com.jaorcas.fightnet.HomeActivity;
import com.jaorcas.fightnet.PostInfoActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.models.Like;
import com.jaorcas.fightnet.models.Post;
import com.jaorcas.fightnet.models.SliderItem;
import com.jaorcas.fightnet.models.User;
import com.jaorcas.fightnet.providers.AuthProvider;
import com.jaorcas.fightnet.providers.LikesProvider;
import com.jaorcas.fightnet.providers.PostProvider;
import com.jaorcas.fightnet.providers.UsersProvider;
import com.jaorcas.fightnet.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPostsAdapter extends FirestoreRecyclerAdapter<Post, UserPostsAdapter.ViewHolder> {

    UsersProvider usersProvider;
    LikesProvider likesProvider;
    AuthProvider authProvider;
    PostProvider postProvider;
    Context context;

    String urlImage = null;
    String urlVideo = null;

    public UserPostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
    }

    //ES EL CONTENIDO QUE QUIERO SE MUESTRE EN CADA UNA DE LAS VISTAS
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        //CON ESTO OBTENEMOS LA INFORMACIÓN COMPLETA DEL POST
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postID = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(),context);


        //ASIGNAMOS EL TITULO Y EL RELATIVETIME DEL POST
        holder.title.setText(post.getDescription());
        holder.relativeTime.setText(relativeTime);

        if(post.getImage()!=null)
            urlImage = post.getImage();
        else if(post.getVideo()!=null)
            urlVideo = post.getVideo();

        if(post.getImage()!=null && !post.getImage().isEmpty()){
            Picasso.get().load(post.getImage()).into(holder.postImage);
        }//VIDEO
        else if(post.getVideo()!=null && !post.getVideo().isEmpty()){
        showVideoHideImage(holder);

            Glide.with(context)
                    .asFile()
                    .load(post.getVideo())
                    .into(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            holder.postVideo.setVideoPath(resource.getAbsolutePath());
                            holder.postVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    // Desactivamos el sonido
                                    mp.setVolume(0.0f, 0.0f);

                                    mp.setLooping(true);
                                    holder.postVideo.start();
                                }
                            });
                            holder.postVideo.setFocusable(false);
                        }
                    });


/*
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

 */
    }



        //LE ASIGNAMOS AL POST FUNCIONALIDAD SI LE HACEMOS CLICK
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

        /*
        //SI HACEMOS CLICK EN EL POST NOS LLEVA A MAS DETALLES
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExtraUserProfileActivity.class);
                intent.putExtra("idUser", post.getIdUser());
                context.startActivity(intent);
            }
        });
*/
        holder.imageViewPostOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view,postID);
            }
        });

        //ESTA VALIDACIÓN ES PARA QUE NO MUESTRE EL BOTÓN DE BORRAR CUANDO HACEMOS CLICK EN ALGÚN PERFIL QUE NO ES EL NUESTRO
        if(post.getIdUser().equals(authProvider.getUid())){
            holder.imageViewPostOptions.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewPostOptions.setVisibility(View.GONE);
        }

        getUserInfo(post.getIdUser(), holder);
    }

    private void showMenu(View view, String postID){
        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
        popupMenu.inflate(R.menu.profile_post_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemDeletePost:
                        deleteMedia(postID);
                        deletePost(view, postID);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //MOSTRAR EL POP UP
        popupMenu.show();
    }

    private void getUserInfo(String idUser, ViewHolder holder){

        usersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_posts, parent, false);
        return new ViewHolder(view);
    }

    //LA PLANTILLA DE UN POST
    public class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener, PopupMenu.OnMenuItemClickListener */{
        TextView title;
        TextView relativeTime;
        ImageView postImage;
        VideoView postVideo;
        CircleImageView imageUserProfile;
        ImageView imageViewPostOptions;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.textviewPostTitle);
            relativeTime = view.findViewById(R.id.textViewRelativeTime);
            postImage = view.findViewById(R.id.imageViewPost);
            postVideo = view.findViewById(R.id.videoViewPost);
            imageUserProfile = view.findViewById(R.id.circleImageUserPost);
            imageViewPostOptions = view.findViewById(R.id.imageViewPostOptions);
            // imageViewPostOptions.setOnClickListener(this);
            viewHolder = view;
        }

    }

    private void showVideoHideImage(UserPostsAdapter.ViewHolder holder){
        holder.postVideo.setVisibility(View.VISIBLE);
        holder.postImage.setVisibility(View.GONE);
    }

    private void deleteMedia(String postID){

        //AHORA INTENTAMOS BORRAR LA IMAGEN DE LA BASE DE DATOS
        String url = "";
       if(urlImage !=null){
           url = urlImage;
       }else if(urlVideo!=null){
           url = urlVideo;
       }

        try{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference urlRef = storage.getReferenceFromUrl(url);


            urlRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "La imagen/video se borró", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "La imagen/video no se borró", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void deletePost(View view,String postID){

        postProvider.delete(postID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    //VISTO QUE NO PUEDO ACTUALIZAR EL ADAPTER CON LA CONSULTA QUERY CON 2 PARÁMETROS,
                    //VAMOS A HACER QUE NOS LLEVE AL HOMEACTIVITY, SOLUCION CHAPUZERA PERO POR AHORA FUNCIONA
                    Intent intent = new Intent(view.getContext(), HomeActivity.class);
                    view.getContext().startActivity(intent);
                    Toast.makeText(context, "Se ha borrado el post", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "No se ha podido borrar el post", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}