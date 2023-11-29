package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView galletaImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        galletaImageView = findViewById(R.id.muslo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        }, 1000);//1000 ms
    }

    private void startAnimation() {
        //Animacion del muslo desapareciendo y encogiendo
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(galletaImageView, "scaleX", 1.0f, 0.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(galletaImageView, "scaleY", 1.0f, 0.0f);


        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(galletaImageView, "alpha", 1.0f, 0.0f);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.setDuration(1200);//Duracion
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());


        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent nIntent = new Intent(MainActivity.this, registrar.class);
                startActivity(nIntent);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animatorSet.start();
    }
}