package com.avoscloud.chat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.widget.EditText;

import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.R;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.util.Log;
import android.widget.ImageView;

public class PublishActivity extends Activity {

    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;


    @InjectView(R.id.activity_publish_et_name)
    public EditText userNameView;

    @InjectView(R.id.activity_publish_et_age)
    public EditText ageView;

    @InjectView(R.id.publish_avatar_view)
    public ImageView imageView;

    @OnClick(R.id.publish_avatar_view)
    public void onAvatarClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.inject(this);
    }


    private void refresh(String path) {
//        LeanchatUser curUser = (LeanchatUser)AVUser.getCurrentUser();
//        userNameView.setText(curUser.getUsername());
        ImageLoader.getInstance().displayImage(path+".png", imageView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                startImageCrop(uri, 200, 200, CROP_REQUEST);
            } else if (requestCode == CROP_REQUEST) {
                final String path = saveCropAvatar(data);
//                Log.i(null, "image path = "+path, null);
                Log.i("image = ", path);
                refresh(path);

//                LeanchatUser user = (LeanchatUser) AVUser.getCurrentUser();
//                user.saveAvatar(path, null);
            }
        }
    }

    public Uri startImageCrop(Uri uri, int outputX, int outputY,
                              int requestCode) {
        Intent intent = null;
        intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        String outputPath = PathUtils.getAvatarTmpPath();
        Uri outputUri = Uri.fromFile(new File(outputPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // face detection
        startActivityForResult(intent, requestCode);
        return outputUri;
    }

    private String saveCropAvatar(Intent data) {
        Bundle extras = data.getExtras();
        String path = null;
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                bitmap = PhotoUtils.toRoundCorner(bitmap, 10);
                path = PathUtils.getAvatarCropPath();
                Log.i("bitmap Path = ", path);
                PhotoUtils.saveBitmap(path, bitmap);
                if (bitmap != null && bitmap.isRecycled() == false) {
                    bitmap.recycle();
                }
            }
        }
        return path;
    }
}