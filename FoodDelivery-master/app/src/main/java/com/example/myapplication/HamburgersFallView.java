package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class HamburgersFallView extends View {
    //Numero de hamburguesas
    private static final int NUM_HAMBURGERS = 25;
    private final Hamburger[] hamburgers = new Hamburger[NUM_HAMBURGERS];
    private int viewWidth, viewHeight;
    private Drawable hamburgerDrawable;

    public HamburgersFallView(Context context) {
        super(context);
        init();
    }

    public HamburgersFallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        hamburgerDrawable = getResources().getDrawable(R.drawable.images);
        for (int i = 0; i < NUM_HAMBURGERS; i++) {
            hamburgers[i] = new Hamburger();
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        for (Hamburger hamburger : hamburgers) {
            hamburger.reset();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);

        for (Hamburger hamburger : hamburgers) {
            hamburger.draw(canvas);
        }
        postInvalidateOnAnimation();
    }

    private class Hamburger {
        private float x;
        private float y;
        private final Random random;

        public Hamburger() {
            random = new Random();
        }

        public void reset() {
            x = random.nextInt(viewWidth);
            y = -random.nextInt(viewHeight);
        }

        public void draw(Canvas canvas) {
            // Dibuja la imagen de la hamburguesa en la posición actual
            hamburgerDrawable.setBounds((int) x, (int) y, (int) x + hamburgerDrawable.getIntrinsicWidth(), (int) y + hamburgerDrawable.getIntrinsicHeight());
            hamburgerDrawable.draw(canvas);

            y += 15; // Ajusta la velocidad
            if (y > viewHeight) {
                reset();
            }
        }
    }
}
