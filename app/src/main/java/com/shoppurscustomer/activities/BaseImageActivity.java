package com.shoppurscustomer.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseImageActivity extends NetworkBaseActivity {

    private int PICK_IMAGE_REQUEST=1;
    private int REQUEST_CAMERA = 0;
    private Uri mHighQualityImageUri;
    protected String imagePath="";
    private String fileName="";
    private Uri filePath;
    private Bitmap bitmap;
    private String userChoosenTask,flag;
    private boolean successfullyUpdated,isNewRider;
    protected String imageBase64;
    protected AlertDialog alertDialog;
    protected RequestOptions requestOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(false);

    }

    protected void selectImage(){
        int view=R.layout.select_image_dialog_layout;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setView(view);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        // final TextView textHeader=(TextView) alertDialog.findViewById(R.id.text_header);
        final ImageView imageCancel=(ImageView) alertDialog.findViewById(R.id.image_close);
        final Button btnGallery=(Button) alertDialog.findViewById(R.id.btn_gallery);
        final Button btnCamera=(Button) alertDialog.findViewById(R.id.btn_camera);
        imageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userChoosenTask = "Camera";
                boolean result = Utility.verifyCameraPermissions(BaseImageActivity.this);
                if (result)
                    cameraIntent();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userChoosenTask = "Gallery";
                boolean result = Utility.verifyStorageOnlyPermissions(BaseImageActivity.this);
                if (result)
                    galleryIntent();
            }
        });

        alertDialog.show();
    }

    private void cameraIntent(){
        mHighQualityImageUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", getFile());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mHighQualityImageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onSelectFromGalleryResult(Intent data){
        filePath = data.getData();
        // imagePath = filePath.getPath();
        // textViewImageStatus.setVisibility(View.VISIBLE);
        //  textViewImageStatus.setText("Pan card image added.");
        //  imagePath=getPath(filePath);
        try {
            //   bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            bitmap=getBitmapFromUri(filePath);
            saveBitmap(bitmap);
            //convertToBase64(new File(imagePath));
            imageAdded();
            //galleryAddPic();
            //  imageByte=AppHelper.getFileDataFromDrawable(this,bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCaptureImageResult(){
        //convertToBase64(new File(imagePath));
        try {
            File file = new File(imagePath);
            if(file.exists()){
                imageAdded();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        //galleryAddPic();
    }

    public void saveBitmap(Bitmap bitmap){
        FileOutputStream out = null;
        File filename = getFile();

        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile() {
        String date=new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        String time=new SimpleDateFormat("HH:mm:ss").format(new Date());

        String root="";
        if(isExternalStorageAvailable()){
            root = Environment.getExternalStoragePublicDirectory("").toString();
        }
        File myDir = new File(root+"/Shoppurs/Shoppurs photos");
        myDir.mkdirs();
        String fname="";
        fname = date+time+".jpg";

        imagePath=root+"/Shoppurs/Shoppurs photos/"+fname;
        File file = new File (myDir, fname);
        return file;

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public String convertToBase64(File file){
        String base64 = "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            // imageBase64 = imageBase64.replaceAll("\n","");
            Log.i("PanCard","Image converted into base64");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            base64 = "";
            Log.i("PanCard","Image conversion not successful");
        }

        return base64;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG,"Request Code "+requestCode+" "+userChoosenTask);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Camera"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Gallery"))
                        galleryIntent();

                } else {
                    //code for deny
                }
                break;
            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Camera"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(alertDialog != null)
            alertDialog.dismiss();

        //  Log.i(TAG,"requestCode code "+requestCode+" "+resultCode+" "+RESULT_OK);

        if (requestCode == PICK_IMAGE_REQUEST){
            if(data != null){
                onSelectFromGalleryResult(data);
            }
        }else if (requestCode == REQUEST_CAMERA){
            Log.i(TAG,"data "+data);
            Log.i(TAG,"image captured "+imagePath);
            onCaptureImageResult();
        }
    }

    protected void imageAdded(){

    }

}
