package com.example.dots;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class GameDialog {

    public void showDialog(final Activity activity, final int titleTextID, final int contentTextID,
                           final GameTimer gameTimer){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        if (Menu.IS_RIGHT_TO_LEFT)
            dialog.setContentView(R.layout.game_dialog_heb);
        else
            dialog.setContentView(R.layout.game_dialog);


        RelativeLayout mainLayout = dialog.findViewById(R.id.dialogMainLayout);
        LinearLayout dialogButtons = dialog.findViewById(R.id.dialogButtons);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(20);
        final TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView contentText = dialog.findViewById(R.id.dialogText);
        final ImageButton home = new ImageButton(activity);

        home.setLayoutParams(params);
        home.setBackgroundResource(R.drawable.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.HOME_BUTTON_CLICKED = true;
                dialog.dismiss();
                gameTimer.stopTimer();
                activity.finish();
            }
        });


        if (titleTextID == R.string.gamePausedTitle) {
            titleText.setText(R.string.gamePausedTitle);
            mainLayout.removeView(contentText);

            RelativeLayout.LayoutParams tempParams =
                    (RelativeLayout.LayoutParams) dialogButtons.getLayoutParams();

            tempParams.addRule(RelativeLayout.BELOW, R.id.logoLayout);


            final ImageButton volume = new ImageButton(activity);
            final ImageButton play = new ImageButton(activity);



            volume.setLayoutParams(params);
            play.setLayoutParams(params);
            play.setBackgroundResource(R.drawable.play);


            if (Menu.VOLUME.equals("unmuted"))
                volume.setBackgroundResource(R.drawable.mute);
            else
                volume.setBackgroundResource(R.drawable.unmute);

            volume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Menu.VOLUME.equals("unmuted")) {
                        Menu.mBoundService.mute();
                        volume.setBackgroundResource(R.drawable.unmute);
                        Menu.VOLUME = "mute";
                    } else {
                        volume.setBackgroundResource(R.drawable.mute);
                        Menu.mBoundService.unmute();
                        Menu.VOLUME = "unmuted";

                    }
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    gameTimer.resumeTimer();
                }
            });

            if (Menu.IS_RIGHT_TO_LEFT) {
                dialogButtons.addView(volume);
                dialogButtons.addView(play);
                dialogButtons.addView(home);
            } else {
                dialogButtons.addView(home);
                dialogButtons.addView(play);
                dialogButtons.addView(volume);
            }
        }

        else if (titleTextID == R.string.gameCompletedTitle){
            titleText.setText(titleTextID);
            contentText.setText(contentTextID);
            dialogButtons.addView(home);
        }

        else{
            titleText.setText(titleTextID);
            contentText.setText(contentTextID);

            final ImageButton replayOrPlayNext = new ImageButton(activity);
            replayOrPlayNext.setLayoutParams(params);

            if (titleTextID == R.string.gameOverTitle){
                replayOrPlayNext.setBackgroundResource(R.drawable.replay);
                replayOrPlayNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        activity.recreate();

                    }
                });
            }
            else{
                replayOrPlayNext.setBackgroundResource(R.drawable.play);
                replayOrPlayNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        activity.finish();
                    }
                });
            }
            if (Menu.IS_RIGHT_TO_LEFT) {
                dialogButtons.addView(replayOrPlayNext);
                dialogButtons.addView(home);
            } else {
                dialogButtons.addView(home);
                dialogButtons.addView(replayOrPlayNext);
            }
        }

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    if (titleTextID == R.string.gamePausedTitle)
                        gameTimer.resumeTimer();
                    else if (titleTextID == R.string.gameOverTitle || titleTextID == R.string.gameCompletedTitle)
                        activity.finish();
                }
                return true;
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}

