package com.shoppurs.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shoppurs.interfaces.FirebaseImageUploadListener;

import java.io.File;
import java.util.List;

public class FirebaseImageUploadService {

    private String TAG = "FirebaseUpload";
    private SharedPreferences sharedPreferences;
    FirebaseStorage storage;
    StorageReference storageRef;
    private Context context;
    private static FirebaseImageUploadService firebaseImageUploadService;

    private FirebaseImageUploadService(){

    }

    public static FirebaseImageUploadService getInstance() {
        if(firebaseImageUploadService == null){
            firebaseImageUploadService = new FirebaseImageUploadService();
        }
        return firebaseImageUploadService;
    }

    private FirebaseImageUploadListener firebaseImageUploadListener;



    public void setFirebaseImageUploadListener(FirebaseImageUploadListener firebaseImageUploadListener) {
        this.firebaseImageUploadListener = firebaseImageUploadListener;
    }

    public void uploadImage(String dir,final String path){
        Log.d("dir "+dir, " path "+path);
        //sharedPreferences = context.getSharedPreferences(Constants.MYPREFERENCEKEY,context.MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Log.i(TAG,"uploading image to firebase..");

        Uri uri = Uri.fromFile(new File(path));
        final StorageReference imageRef = storageRef.child("images/"+dir);
        UploadTask uploadTask = imageRef.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    firebaseImageUploadListener.onImageFailed("0");
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                Log.i(TAG,"downloading url..."+path);
                return imageRef.getDownloadUrl();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseImageUploadListener.onImageFailed("0");
                Log.i(TAG,"failed "+e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    firebaseImageUploadListener.onImageUploaded("0",downloadUri.toString());
                    Log.i(TAG,"url "+downloadUri);
                    Log.i(TAG,"url "+downloadUri.getPath());
                } else {
                    firebaseImageUploadListener.onImageFailed("0");
                   // Log.i(TAG,"failed "+task.getResult());
                }
            }
        });

    }

    public void uploadProdImage(String prodId,String shopCode, List<String> imageList){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Log.i(TAG,"uploading images to firebase..");
        int i = 1;
        String name = null;
        for(String imagePath : imageList){

            if(!imagePath.equals("no")){
                if(i==1){
                    name="1.jpg";
                }else if(i==2){
                    name="2.jpg";
                }else if(i==3){
                    name="3.jpg";
                }
                new UploadFile().execute(""+i,imagePath,name,prodId,shopCode);
            }

            i++;
        }
    }

    private class UploadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            final String position = strings[0];
            String path = strings[1];
            final String fileName = strings[2];
            final String prodId = strings[3];
            final String shopCode = strings[4];
            Uri uri = Uri.fromFile(new File(path));
            String dir = shopCode+"/products/"+prodId+"/"+fileName;
            final StorageReference imageRef = storageRef.child("images/"+dir);
            UploadTask uploadTask = imageRef.putFile(uri);

            Log.i(TAG,"In async task "+fileName);

            //imageRef.delete().addOnSuccessListener()

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        firebaseImageUploadListener.onImageFailed(position);
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    Log.i(TAG,"downloading url..."+fileName);
                    return imageRef.getDownloadUrl();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    firebaseImageUploadListener.onImageFailed(position);
                    Log.i(TAG,"failed "+e.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        firebaseImageUploadListener.onImageUploaded(position,downloadUri.toString());
                        Log.i(TAG,"position  "+position);
                        Log.i(TAG,"url "+downloadUri);
                        Log.i(TAG,"url "+downloadUri.getPath());
                    } else {
                        firebaseImageUploadListener.onImageFailed(position);
                        Log.i(TAG,"failed "+task.getResult());
                    }
                }
            });
            return null;
        }
    }
}
