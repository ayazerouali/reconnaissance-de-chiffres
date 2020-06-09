package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class drawingview extends View {
    private static final float TOUCH_TOLERANCE = 4;

    //creation des bitmaps
    private static Bitmap bitmap;
    private static Bitmap bitmap2;
    private static Bitmap bitmap3;
    private static Bitmap bitmap4;

    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint paint;
    private boolean drawMode;
    private float x, y;
    private float penSize = 40;

    //taille des cases
    private float taille;
    private float tailleLigne;

    public drawingview(Context c) {
        this(c, null);
    }

    public drawingview(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public drawingview(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        init();
    }


    private void init() {
        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(penSize);
        drawMode = true;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        taille = w/2f;
        tailleLigne = w/80f;



        //creation des cases



        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);

        paint.setColor( Color.WHITE);
        paint.setStrokeWidth(tailleLigne);
        canvas.drawLine( taille, 0, taille, 2*taille, paint );
        canvas.drawLine( 0, taille, 2*taille, taille, paint );

        paint.setStrokeWidth(penSize);
    }

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        this.x = x;
        this.y = y;
        canvas.drawPath(path, paint);
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - this.x);
        float dy = Math.abs(y - this.y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
            this.x = x;
            this.y = y;
        }
        canvas.drawPath(path, paint);
    }

    private void touchUp() {
        path.lineTo(x, y);
        canvas.drawPath(path, paint);
        path.reset();
        if (drawMode) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!drawMode) {
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                } else {
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
                }
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                if (!drawMode) {
                    path.lineTo(this.x, this.y);
                    path.reset();
                    path.moveTo(x, y);
                }
                canvas.drawPath(path, paint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }


    public void clear() {
        canvas.drawColor(Color.BLACK);
        invalidate();
    }

    public static Bitmap getBitmap() {
        return bitmap;
    }

}
