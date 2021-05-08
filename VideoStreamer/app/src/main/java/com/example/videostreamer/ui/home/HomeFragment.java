package com.example.videostreamer.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.example.videostreamer.MainActivity;
import com.example.videostreamer.R;
import com.example.videostreamer.RegisterActivity;
import com.example.videostreamer.SinglePostActivity;
import com.example.videostreamer.Vlog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseRecyclerAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Snackbar.make(container,"Loading Posts...",Snackbar.LENGTH_LONG).show();
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager((new LinearLayoutManager(getContext())));
        recyclerView.setHasFixedSize(true);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Vlog");
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Vlog")
                .limitToLast(10);
        FirebaseRecyclerOptions<Vlog> options =
                new FirebaseRecyclerOptions.Builder<Vlog>()
                        .setQuery(query, Vlog.class)
                        .build();
        adapter=new FirebaseRecyclerAdapter<Vlog, VlogViewHolder>(options){
            @Override
            public VlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_items, parent, false);
                return new VlogViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(final VlogViewHolder viewHolder, int position, Vlog model) {
                final String post_key=getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setVideoUrl(getContext(), model.getVideoUrl());
                viewHolder.setUserName(model.getUsername());
                viewHolder.setProfileImageUrl(getContext(),model.getProfileImageUrl());
                viewHolder.mView.findViewById(R.id.t1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.click(v,post_key);
                    }
                });
                viewHolder.mView.findViewById(R.id.t2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.click(v,post_key);
                    }
                });
                viewHolder.mView.findViewById(R.id.t3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.click(v,post_key);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        return root;
};
    public class VlogViewHolder extends ViewHolder{
        public View mView;
        public VlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setTitle(String title){
            TextView post_title = mView.findViewById(R.id.t2);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = mView.findViewById(R.id.t3);
            post_desc.setText(desc);
        }
        public void setVideoUrl(Context ctx, String videoUrl){
            VideoView post_video = mView.findViewById(R.id.videoView);
            MediaController mc=new MediaController(post_video.getContext());
            post_video.setMediaController(mc);
            post_video.setVideoURI(Uri.parse(videoUrl));
            //post_video.requestFocus();
            //post_video.start();
            //post_video.pause();
        }
        public void setProfileImageUrl(Context ctx,String profileImageUrl){
            ImageView post_profileImage=mView.findViewById(R.id.profileImage);
            if(post_profileImage!=null) {
                Glide.with(post_profileImage.getContext())
                        .load(profileImageUrl)
                        .circleCrop()
                        .into(post_profileImage);
                }

        }
        public void setUserName(String userName){
            TextView postUserName = mView.findViewById(R.id.t1);
            postUserName.setText(userName);
        }
        public void click(View view,String post_key){
            Intent singleActivity=new Intent(view.getContext(), SinglePostActivity.class);
            singleActivity.putExtra("PostID",post_key);
            startActivity(singleActivity);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
