package com.example.videostreamer;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    ImageButton imgbtn;
    EditText title;
    EditText desc;
    Button postBtn;
    ImageButton backButton;
    private static final int GALLERY_REQUEST_CODE = 2;
    private StorageReference storage;
    private DatabaseReference databaseRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;
    private Uri uri=null;
    private String profImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postBtn=(Button)findViewById(R.id.b1);
        title=(EditText)findViewById(R.id.t1);
        desc=(EditText)findViewById(R.id.t2);
        imgbtn=(ImageButton)findViewById(R.id.imageButton);
        backButton=findViewById(R.id.backButton);
        storage= FirebaseStorage.getInstance().getReference();
        databaseRef=database.getInstance().getReference().child("Vlog");
        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        imgbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent videoIntent=new Intent(Intent.ACTION_GET_CONTENT);
                videoIntent.setType("video/*");
                startActivityForResult(videoIntent,GALLERY_REQUEST_CODE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent=new Intent(PostActivity.this,MainActivity.class);
                startActivity(backIntent);
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Posting...",Snackbar.LENGTH_LONG).show();
                final String Title=title.getText().toString();
                final String Description=desc.getText().toString();
                //check if any field is empty
                if(!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(Description)){
                    final StorageReference filepath=storage.child("post_videos").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloader=uri;
                                    //for storing new post contents
                                    final DatabaseReference newPost=databaseRef.push();
                                    //adding post to database
                                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            newPost.child("title").setValue(Title);
                                            newPost.child("desc").setValue(Description);
                                            newPost.child("videoUrl").setValue(downloader.toString());
                                            newPost.child("uid").setValue(mCurrentUser.getUid());
                                            DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
                                            mDatabaseUsers.child("image").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    profImageUrl = snapshot.getValue().toString();
                                                    newPost.child("profileImageUrl").setValue(profImageUrl);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            newPost.child("username").setValue(snapshot.child("name").getValue())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Intent intent=new Intent(PostActivity.this,MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(PostActivity.this,"An Error Occurred...Please Try Again.",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });

                        }
                    });
                }

            }
        });
    }

    //image from gallery result
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK){
            uri=data.getData();
            Toast.makeText(PostActivity.this,"Video Selected...",Toast.LENGTH_SHORT).show();
            imgbtn.setImageURI(uri);
        }
    }
}