package com.example.myapplication;

import android.graphics.Bitmap;

public class separeBitmap {

    private static Bitmap bitmap1;
    private static Bitmap bitmap2;
    private static Bitmap bitmap3;
    private static Bitmap bitmap4;

    public static Bitmap prendBitmap() {
    return drawingview.getBitmap();
    }

    public static int taille(Bitmap bitmap){
        return bitmap.getWidth()/2;
    }

    public static Bitmap getBitmap1(){
        bitmap1 = Bitmap.createBitmap(prendBitmap(), 0, 0, taille(prendBitmap()), taille(prendBitmap()));
        bitmap1 = Bitmap.createScaledBitmap(bitmap1,28,28, false);
        return bitmap1;
    }

    public static Bitmap getBitmap2() {
        bitmap2 = Bitmap.createBitmap(prendBitmap(), taille(prendBitmap()), 0,
                taille(prendBitmap()), taille(prendBitmap()));
        bitmap2 = Bitmap.createScaledBitmap(bitmap2,28,28, false);
        return bitmap2;
    }

    public static Bitmap getBitmap3(){
        bitmap3 = Bitmap.createBitmap(prendBitmap(), 0, taille(prendBitmap()),
                taille(prendBitmap()), taille(prendBitmap()));
        bitmap3 = Bitmap.createScaledBitmap(bitmap3,28,28, false);
        return bitmap3;
    }

    public static Bitmap getBitmap4() {
        bitmap4 = Bitmap.createBitmap(prendBitmap(), taille(prendBitmap()), taille(prendBitmap()),
                taille(prendBitmap()), taille(prendBitmap()));
        bitmap4 = Bitmap.createScaledBitmap(bitmap4,28,28, false);
        return bitmap4;
    }
}
