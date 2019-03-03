package noritoshi_scarlett.postarium.libraries;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import noritoshi_scarlett.postarium.R;

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;
    private boolean isChar;

    private Context context;

    public TextDrawable(Context context, String text, Float textsize) {

        this.text = text;
        this.context = context;
        this.isChar = false;
        this.paint = new Paint();

        paint.setColor(ContextCompat.getColor(context, R.color.textPrimary));
        paint.setTextSize(textsize);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        //paint.setShadowLayer(4, 0, 0, ContextCompat.getColor(context, R.color.transparentMegaDark));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        Typeface fontFamily  = Typeface.createFromAsset(context.getAssets(),"fonts/ShadowsIntoLight.ttf");
        paint.setTypeface(fontFamily);

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Get the screen's density scale
        int dp = 18;
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixInDp = (int) (dp * scale + 0.5f);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * pixInDp / bounds.width();

        // Set the paint for that size.
        if (desiredTextSize < (32 * scale)) {
            paint.setTextSize(desiredTextSize);
        }

    }

    /**
     * Draw text for ForumActivity
     * @param context
     * @param text
     * @param colorText
     * @param textsize
     */
    public TextDrawable(Context context, String text, int colorText, Float textsize) {

        this.text = text;
        this.context = context;
        this.isChar = true;
        this.paint = new Paint();

        paint.setColor(colorText);
        paint.setTextSize(textsize);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface fontFamily  = Typeface.createFromAsset(context.getAssets(),"fonts/ShadowsIntoLight.ttf");
        paint.setTypeface(fontFamily);

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //int yPos = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent()) / 3)) ;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        //canvas.drawText(text, 0, yPos, paint);
        //int yPos = (int) ((canvas.getHeight()) - ((paint.descent() + paint.ascent()))) ;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        if (isChar) {
            canvas.drawText(text, paint.descent(), (canvas.getHeight() / 2), paint);
        } else {
            Paint circlePaint = new Paint();
            circlePaint.setColor(ContextCompat.getColor(context, R.color.backgroundRed));
            circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

            Paint strokePaint = new Paint();
            strokePaint.setColor(ContextCompat.getColor(context, R.color.textPrimary));
            strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

            // Get the screen's density scale
            int dp = 36;
            final float scale = context.getResources().getDisplayMetrics().density;
            int pixInDp = (int) (dp * scale + 0.5f);
            int radius = pixInDp/2;

            //canvas.drawCircle(pixInDp / 2 , pixInDp / 2, radius, strokePaint);
            //canvas.drawCircle(pixInDp / 2, pixInDp / 2, radius-2, circlePaint);
            canvas.drawText(text, paint.measureText(text) / 2, (canvas.getHeight() / 2), paint);


        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}