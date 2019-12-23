package com.halloween.journote.ui.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class CenterImageSpan extends ImageSpan {

    public CenterImageSpan(Drawable drawable) {
        super(drawable);
    }
    public CenterImageSpan(Context context, Bitmap bitmap) {
        super(context,bitmap);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        Drawable drawable = getDrawable();
        int transY = ((bottom-top) - drawable.getBounds().bottom)/2+top;
        canvas.save();
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}


