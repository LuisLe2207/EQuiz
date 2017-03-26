package com.example.luisle.equiz.MyFramework;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.example.luisle.equiz.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by LuisLe on 2/11/2017.
 */

public class MyEssential {

    // region APP CONSTANTS
    // region Activities Codes
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_GALLERY = 2;
    // endregion

    //region Firebase Child
    public static final String USERS_CHILD = "USER_CHILD";
    public static final String QUESTION_CHILD = "QUESTION_CHILD";
    public static final String EXAM_CHILD = "EXAM_CHILD";
    public static final String RESULT_CHILD = "RESULT_CHILD";
    public static final String USER_AVATAR = "User_Avatar/";
    // endregion

    public static boolean isAdmin = false;

    // endregion

    //  region Firebase Global Variables
    public static FirebaseDatabase eQuizDatabase;
    public static DatabaseReference eQuizRef;
    public static FirebaseStorage eQuizStorage;
    public static StorageReference eQUizStorageRef;
    // endregion

    // region Global Function
    /**
     *
     * @param context   Activity
     * @param message   toast message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void openAvatarActionDialog(final AppCompatActivity activity) {
        Dialog avatarActionDialog = new Dialog(activity);
        avatarActionDialog.setContentView(R.layout.dialog_choose_avatar_action);
        avatarActionDialog.setTitle("Choose");
        CardView cardVCamera = (CardView) avatarActionDialog.findViewById(R.id.cardVCamera);
        CardView cardVGallery = (CardView) avatarActionDialog.findViewById(R.id.cardVGallery);
        cardVCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        cardVGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getPictureIntent = new Intent(Intent.ACTION_PICK);
                getPictureIntent.setType("image/*");
                activity.startActivityForResult(getPictureIntent, REQUEST_IMAGE_GALLERY);
            }
        });
        avatarActionDialog.show();
    }


    public static byte[] convertImageViewToByte(RoundedImageView userAvatar){

        RoundedDrawable drawable = (RoundedDrawable) userAvatar.getDrawable();
        Bitmap bmp = drawable.getSourceBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // endregion

}


