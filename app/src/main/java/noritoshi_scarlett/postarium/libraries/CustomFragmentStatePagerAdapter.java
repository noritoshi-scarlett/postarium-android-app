package noritoshi_scarlett.postarium.libraries;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.LibraryFragment;
import noritoshi_scarlett.postarium.fragments.LibraryShelfFragment;

public abstract class CustomFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private Context context;

    public CustomFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    protected CustomFragmentStatePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * Customizacja layutu: czcionka, kolor, tekst
     * @param position pozycja w tablayout
     * @return custom view
     */
    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.layout);
        title.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Dosis-Light.ttf"));
        title.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
        title.setText(getPageTitle(position));
        return view;
    }
}
