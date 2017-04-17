package com.example.luisle.equiz.MyFramework;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.luisle.equiz.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by LuisLe on 2/11/2017.
 */

public class MyEssential {

    // region APP CONSTANTS
    public static String SERVER_SEND_TOKEN_URL = "http://192.168.1.3:8080/equiz/register.php";
    public static String SERVER_PUSH_NOTIFICATION = "http://192.168.1.3:8080/equiz/push.php";
    // region Activities Codes
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_GALLERY = 2;
    // endregion
    public static Integer MAX_CHOICE = 5;
    public static Integer MIN_CHOICE = 4;
    public static Integer MAX_ANSWER = 3;
    public static Integer MIN_ANSWER = 1;
    public static Integer MIN_QUESTTION = 5;
    public static Integer MAX_QUESTION = 30;
    public static Integer choiceID = 1;

    //region Firebase Child
    public static final String USERS_CHILD = "USER_CHILD";
    public static final String QUESTION_CHILD = "QUESTION_CHILD";
    public static final String EXAM_CHILD = "EXAM_CHILD";
    public static final String RESULT_CHILD = "RESULT_CHILD";
    public static final String COMMENT_CHILD = "COMMENT_CHILD";
    public static final String USER_AVATAR = "User_Avatar/";
    // endregion

    public static boolean isAdmin = false;
    public static boolean isDelete = false;
    public static boolean dialogOnScreen = false;
    public static boolean inAddExamDialog = false;
    public static boolean inHomeFrag = false;
    public static boolean allowMaintain = false;

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

    public static AlertDialog.Builder createAlertDialog(Context context, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.create();
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    public static Dialog createDialog(Context context, Integer layout, String title) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(layout);
        dialog.setTitle(title);
        return dialog;
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

    public static void registerToken(String token) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token",token)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_SEND_TOKEN_URL)
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushNotification(String userID, String title, String message) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("UserID", userID)
                .add("Title",title)
                .add("Message", message)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_PUSH_NOTIFICATION)
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openDateDialog(Context context, final EditText editText) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // i là trả về năm, i1 trả về tháng, i2 trả về ngày

                calendar.set(i,i1,i2);
                editText.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }
                ,calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.DATE));

        datePickerDialog.show();
    }

    public static void openTimeDialog(Context context, final EditText editText) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm ");
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                // i trả về giờ, i1 trả về phút
                // để 3 số 0 đầu tương đương với việc không truyền vào 3 tham số giờ phút giây
                calendar.set(0,0,0,i,i1);
                editText.setText(simpleDateFormat.format(calendar.getTime()));

            }
        }
                ,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    // endregion

}


