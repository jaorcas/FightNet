package com.jaorcas.fightnet.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthProvider {

    private FirebaseAuth auth;

    public AuthProvider(){ auth = FirebaseAuth.getInstance();}

    public Task<AuthResult> register(String email, String password){
        return auth.createUserWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> login(String email, String password){
        return auth.signInWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> googleLogin(String googleSignInAccount){
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount, null);
        return auth.signInWithCredential(credential);
    }

    public String getUid(){

        if(auth.getCurrentUser()!=null){
            return auth.getCurrentUser().getUid();
        }else {
            return null;
        }
    }

    public FirebaseUser getUserSession(){

        if(auth.getCurrentUser()!=null){
            return auth.getCurrentUser();
        }else {
            return null;
        }
    }

    public String getEmail(){
        if(auth.getCurrentUser()!=null){
            return auth.getCurrentUser().getEmail();
        }else {
            return null;
        }
    }

    public void logOut(){
        if(auth!=null){
            auth.signOut();
        }

    }


}
