package com.example.fmodule;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogLoading {
    private Dialog loading(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);

        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);

        Dialog loadingDialog = new Dialog(context);
        loadingDialog.setContentView(layout);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        Window window = loadingDialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setGravity(Gravity.CENTER);
//        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
        return loadingDialog;
    }
}
