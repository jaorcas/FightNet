package com.jaorcas.fightnet.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaorcas.fightnet.models.User;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference collection;

    public UsersProvider(){
        collection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String userID){

        return collection.document(userID).get();
    }

    public Task<Void> createUser(User user){
        return collection.document(user.getId()).set(user);
    }

    public Task<Void> update(User user){
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("userDescription", user.getUserDescription());
        map.put("timestamp", new Date().getTime());
        if(user.getImageProfile()!=null)    map.put("imageProfile", user.getImageProfile());
        if(user.getImageBanner()!=null)     map.put("imageBanner", user.getImageBanner());

        return collection.document(user.getId()).update(map);
    }

}
