package com.example.videocalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationsActivity extends AppCompatActivity {

    //오브젝트 선언
    private RecyclerView notifications_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //컴포넌트 연결
        notifications_list = findViewById(R.id.notification_list);
        notifications_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    //find_friend_design 오브젝트 및 컴포넌트 관리
    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        //오브젝트 선언
        TextView userNameTxt;
        Button acceptBtn, cancelBtn;
        ImageView profileImageView;
        RelativeLayout cardView;

        //컴포넌트 연결
        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.name_notification);
            acceptBtn = itemView.findViewById(R.id.request_accept_btn);
            cancelBtn = itemView.findViewById(R.id.request_cancel_btn);
            profileImageView = itemView.findViewById(R.id.image_notification);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}