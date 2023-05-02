package com.jaorcas.fightnet.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jaorcas.fightnet.models.Post;

public class PostProvider {

    CollectionReference collection;

    public PostProvider(){
        collection = FirebaseFirestore.getInstance().collection(("Posts"));
    }

    public Task<Void> save(Post post){

        return collection.document().set(post);
    }

    //OBTENEMOS TODOS LOS POSTS ORDENADOS POR FECHA DE CREACION
    public Query getAll(){
        return collection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    //BUSCAMOS TODOS LOS POST DONDE EL idUser DEL POST SEA EL MISMO QUE EL USERID DEL USUARIO QUE PASAMOS POR PARAMETRO
    public Query getPostsByUser(String userID){
        return collection.whereEqualTo("idUser", userID);
    }

    public Query getPostsByUserOrderedByTimestamp(String userID) {
         return collection.whereEqualTo("idUser", userID).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    //LO MISMO PERO CON EL ID DEL POST
    public Task<DocumentSnapshot> getPostByPostID(String postID){
        return collection.document(postID).get(); }

    public Task<Void> delete(String id){
        return collection.document(id).delete();
    }

    public Query getPostByGame(String gameTitle){
        return collection.whereEqualTo("gameTitle",gameTitle).orderBy("timestamp",Query.Direction.DESCENDING);
    }

    public Query getPostByGameAndCharacter(String gameTitle, String character){
        return collection.whereEqualTo("gameTitle",gameTitle)
                         .whereEqualTo("character", character)
                         .orderBy("timestamp", Query.Direction.DESCENDING);
    }

    //ESTO SOLO FILTRA SI LA DESCRIPCION ES LA MISMA QUE LA QUE PASAMOS POR PARÁMETRO, HABRÍA QUE MEJORARLO EN EL FUTURO
    public Query getPostByDescription(String description){
        return collection.whereEqualTo("descriptionLowCase",description.toLowerCase() );
    }


}
