package noritoshi_scarlett.postarium.libraries;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html.ImageGetter;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public class URLImageParser implements ImageGetter {

    private final Context mContext;
    private final TextView mTextView;
    private final ArrayList<Target<Drawable>> targets;

    public URLImageParser(View container, Context context) {
        this.mContext = context;
        this.mTextView = (TextView) container;
        this.targets = new ArrayList<>();
        mTextView.setTag(this);
    }

    @Override
    public Drawable getDrawable(String url) {

        final UrlDrawable urlDrawable = new UrlDrawable();
        final Uri mUrl = Uri.parse(url);
        final Target<Drawable> target = new GifTarget(urlDrawable);
        targets.add(target);

        Glide.with(mContext)
                .load(mUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop())
                .into(target);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anime();
            }
        });

        return urlDrawable;
    }

    public static void clear(TextView view) {
        Object tag = view.getTag();
        if (tag instanceof URLImageParser) {
            ((URLImageParser)tag).clear();
        }
    }
    private void clear() {
        Drawable gifDrawable;
        for (Target target : targets) {
            gifDrawable = ((GifTarget) target).getDrawable();
            if (gifDrawable != null && gifDrawable instanceof Animatable) {
                ((GifDrawable) gifDrawable).stop();
            }
            Glide.with(mContext).clear(target);
        }
    }

    private void anime() {
        Drawable gifDrawable;
        for (Target target : targets) {
            gifDrawable = ((GifTarget) target).getDrawable();
            if (gifDrawable != null && gifDrawable instanceof Animatable) {
                if (((GifDrawable) gifDrawable).isRunning()) {
                    ((GifDrawable) gifDrawable).stop();
                } else {
                    ((GifDrawable) gifDrawable).setLoopCount(GifDrawable.LOOP_FOREVER);
                    ((GifDrawable) gifDrawable).start();
                }
            }
        }
    }

    private class GifTarget extends SimpleTarget<Drawable> implements Drawable.Callback {

        private Drawable drawable;
        private Request request;
        private final UrlDrawable urlDrawable;

        public GifTarget(UrlDrawable drawable) {
            this.urlDrawable = drawable;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        @Override
        public void setRequest(@Nullable Request request) {
            this.request = request;
        }
        @Nullable
        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {

            if (resource instanceof Animatable) {
                ((GifDrawable) resource).stop();
            }
            drawable = resource;

            int hh = drawable.getIntrinsicHeight();
            int ww = drawable.getIntrinsicWidth();
            Rect rect = new Rect(0, 0, ww, hh);
            drawable.setBounds(rect);
            urlDrawable.setDrawable(drawable);
            urlDrawable.setBounds(rect);
            drawable.setCallback(this);

            mTextView.setText(mTextView.getText());
            mTextView.invalidate();
        }

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            mTextView.invalidate();
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {}

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {}
    }

    class UrlDrawable extends BitmapDrawable {

        private Drawable drawable;

        @SuppressWarnings("deprecation")
        public UrlDrawable() {
        }

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null)
                drawable.draw(canvas);
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }
}