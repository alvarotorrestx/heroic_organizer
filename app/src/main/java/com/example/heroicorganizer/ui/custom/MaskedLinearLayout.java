package com.example.heroicorganizer.ui.custom;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MaskedLinearLayout extends LinearLayout {

    private Drawable maskedDrawable;

    public MaskedLinearLayout(Context context) {
        super(context);
        init();
    }

    public MaskedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setMaskedBackground(Drawable drawable) {
        this.maskedDrawable = drawable;
        invalidate();
    }


    /// adjusts the background of to match the view size
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (maskedDrawable == null) {
            super.dispatchDraw(canvas);
            return;
        }

        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);

        // Draw children first
        super.dispatchDraw(canvas);

        // Create paint with DST_IN or SRC_IN mode
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /// Definition: Background (SRC) , Layout (DST)
        /// Shows the bg(SRC) fitting the entire layout (DST)
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        //The source pixels are drawn behind the destination pixels.

        /// layout (DST) elements utilize the colors of the bg (SRC) based on alpha values of DST
        //maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // "Discards the destination pixels that are not covered by source pixels"


        // Draw masked drawable into a temp bitmap
        Bitmap maskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(maskBitmap);
        maskedDrawable.setBounds(0, 0, getWidth(), getHeight());
        maskedDrawable.draw(maskCanvas);

        // Apply the mask to the canvas using paint with xfermode
        canvas.drawBitmap(maskBitmap, 0, 0, maskPaint);

        canvas.restoreToCount(saveCount);
    }
}
