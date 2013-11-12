package com.app.util;



import com.example.slidingmenudemo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AvatarDialog extends AlertDialog{

        private Button uploadByCameraBt;
        private Button uploadByAlbumBt;
        
        public AvatarDialog(Context context, int theme) {
            super(context, theme);
        }

        public AvatarDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.avatar_dialog);
            uploadByCameraBt = (Button)this.findViewById(R.id.uploadbycamera);
            uploadByAlbumBt = (Button)this.findViewById(R.id.uploadbyalbum);
        }
        
        public void setCameraButtonListener(View.OnClickListener l)
        {
            uploadByCameraBt.setOnClickListener(l);
        }
        
        public void setAlbumButtonListener(View.OnClickListener l)
        {
            uploadByAlbumBt.setOnClickListener(l);
        }
      
}
