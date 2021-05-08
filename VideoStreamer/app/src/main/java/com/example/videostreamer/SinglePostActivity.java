package com.example.videostreamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class SinglePostActivity extends AppCompatActivity {
    private VideoView videoView;
    private TextView singleTitle, singleDesc;
    String post_key = null;
    private DatabaseReference mDatabase;
    private Button deleteBtn;
    private FirebaseAuth mAuth;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        videoView= findViewById(R.id.videoView3);
        singleTitle = (TextView)findViewById(R.id.t4);
        singleDesc = (TextView)findViewById(R.id.t5);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Vlog");
        post_key = getIntent().getExtras().getString("PostID");
        deleteBtn = (Button)findViewById(R.id.b2);
        backBtn=findViewById(R.id.backBtn);
        mAuth = FirebaseAuth.getInstance();
        deleteBtn.setVisibility(View.INVISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SinglePostActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SinglePostActivity.this,"Deleting...",Toast.LENGTH_LONG).show();
                mDatabase.child(post_key).removeValue();

                Intent mainintent = new Intent(SinglePostActivity.this, MainActivity.class);
                startActivity(mainintent);
            }
        });


        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String post_title = (String) snapshot.child("title").getValue();
                String post_desc = (String) snapshot.child("desc").getValue();
                String post_video = (String) snapshot.child("videoUrl").getValue();
                String post_uid = (String) snapshot.child("uid").getValue();

                singleTitle.setText(post_title);
                singleDesc.setText(post_desc);
                MediaController mc=new MediaController(videoView.getContext());
                videoView.setMediaController(mc);
                videoView.setVideoURI(Uri.parse(post_video));
                videoView.requestFocus();
                videoView.start();
                if (mAuth.getCurrentUser().getUid().equals(post_uid)){
                    deleteBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}