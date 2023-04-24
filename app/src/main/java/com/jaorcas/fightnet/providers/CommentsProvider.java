package com.jaorcas.fightnet.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jaorcas.fightnet.models.Comment;

public class CommentsProvider {

    CollectionReference collection;

    public CommentsProvider(){
        collection = FirebaseFirestore.getInstance().collection("Comments");
    }

    public Task<Void> create(Comment comment){

        return collection.document().set(comment);
    }

    //BUSCAMOS TODOS LOS POST DONDE EL idUser DEL POST SEA EL MISMO QUE EL USERID DEL USUARIO QUE PASAMOS POR PARAMETRO
    public Query getCommentsByPostID(String postID){
        return collection.whereEqualTo("idPost", postID);
    }
}
