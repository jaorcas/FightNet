package com.jaorcas.fightnet.providers;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaorcas.fightnet.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class UploadMediaProvider {

    StorageReference mstorage;

    public UploadMediaProvider(){

        mstorage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask saveImage(Context context, File file){

        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(),500, 500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child(new Date() + ".png");
        mstorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public UploadTask saveVideo(Context context, File file){
        Uri uri = Uri.fromFile(file);
        StorageReference storage = mstorage.child(new Date() + ".mp4");
        mstorage = storage;
        UploadTask task = storage.putFile(uri);
        return task;
    }

    public StorageReference getStorage(){
        return mstorage;
    }


}
