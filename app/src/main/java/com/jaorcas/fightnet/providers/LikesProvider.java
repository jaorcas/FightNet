package com.jaorcas.fightnet.providers;

import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.adapters.PostsAdapter;
import com.jaorcas.fightnet.models.Like;

public class LikesProvider {

    CollectionReference collection;
    AuthProvider authProvider;

    public LikesProvider(){
        collection = FirebaseFirestore.getInstance().collection("Likes");
        authProvider = new AuthProvider();
    }

    public Task<Void> create(Like like){
        DocumentReference document = collection.document();
        String id = document.getId();
        like.setId(id);

        return document.set(like);
    }

    //AL HACER CLICK NO PARAN DE CREARSE LIKES CON DISTINTOS IDS
    //CON ESTA FUNCION PODEMOS SABER SI YA HAY ALGUNO HECHO CON EL ID DEL POST Y DEL USUARIO LOGGEADO
    public Query getLikeByPostAndUser(String postID, String userID){
        return collection.whereEqualTo("postID", postID).whereEqualTo("userID", userID);

    }

    public Task<Void> delete(String id){
        return collection.document(id).delete();
    }

    public Query getLikesByPostID(String postID){
        return collection.whereEqualTo("postID", postID);
    }

    //LA FUNCION A LA QUE LLAMAN LOS BOTONES DE LIKE
    public void like(Like like, ImageView imageViewLike){
        //AQUI ES IMPORTANTE QUE PASEMOS EL ID DE LA SESIÓN ACTUAL, Y NO EL ID DEL QUE HIZO EL POST
        getLikeByPostAndUser(like.getPostID(),authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberLikes = queryDocumentSnapshots.size();

                //SI EL NUMERO DE LIKES ES 0, SIGNIFICA QUE EL USUARIO YA LE HA DADO LIKE AL POST
                if(numberLikes>0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    imageViewLike.setImageResource(R.drawable.corazon_gris);
                    delete(idLike);

                    //SI ES 0, AGREGAMOS EL LIKE
                }else{
                    imageViewLike.setImageResource(R.drawable.corazon_rosa);
                    create(like);
                }
            }
        });

    }

    //AQUI ES IMPORTANTE QUE PASEMOS EL ID DE LA SESIÓN ACTUAL, Y NO EL ID DEL QUE HIZO EL POST
    public void checkLikeExists(String postID, String userID, ImageView imageViewLike){

        getLikeByPostAndUser(postID,userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberLikes = queryDocumentSnapshots.size();

                //EN ESTE CASO EL ORDEN SERÍA A LA INVERSA, YA QUE SI HAY ALGUN LIKE LO MOSTRAMOS DE COLOR
                if(numberLikes>0){
                    imageViewLike.setImageResource(R.drawable.corazon_rosa);
                }else{
                    imageViewLike.setImageResource(R.drawable.corazon_gris);
                }
            }
        });

    }

    //USAMOS EL SNAPSHOTLISTENER QUE ESTÁ ESCUCHANDO T0DO EL RATO
    public void getNumberOfLikes(String idPost, TextView textViewLikes){
        getLikesByPostID(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if(queryDocumentSnapshots!=null){
                    int numberLikes =  queryDocumentSnapshots.size();
                    textViewLikes.setText(numberLikes + " Me gusta");
                }

            }
        });
    }

}
