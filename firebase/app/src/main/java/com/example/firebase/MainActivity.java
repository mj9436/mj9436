package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final int GALLERY_CODE = 10;
    ImageView photo;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView gifImg = findViewById(R.id.gif);

        Glide.with(this).load(R.drawable.homegif).into(gifImg);

        findViewById(R.id.imageView).setOnClickListener(onClickListener);
        photo=(ImageView)findViewById(R.id.imageView);

        //Firebase 연동
        storage = FirebaseStorage.getInstance();
    }

    //이미지 클릭 시 내 앨범 내에서 이미지 불러게하는 함수 호출
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView:
                    loadAlbum();
                    break;
            }
        }
    };

    //앨범에서 이미지 가져오기
    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }

    //갤러리에서 가져온 사진을 firebase 데이터베이스에 저장
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE){
            Uri file = data.getData();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("photo/1.png");
            UploadTask uploadTask = riversRef.putFile(file);

            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                photo.setImageBitmap(img);
            } catch (Exception e){
                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception exception){
                    Toast.makeText(MainActivity.this, "사진이 정상적으로 업로드 되지 않았습니다." , Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    Toast.makeText(MainActivity.this, "사진이 정상적으로 업로드 되었습니다." ,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}