package noritoshi_scarlett.postarium.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.ForumPostsMsgsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitle = new ArrayList<>();
    private float pageWidth;

    private Context context;

    public ViewPagerAdapter(FragmentManager manager, Context context, String[] titles, Float pageWidth) {
        super(manager);
        this.context = context;
        this.pageWidth = pageWidth;
        Collections.addAll(mFragmentTitle, titles);
    }

    @Override
    public Fragment getItem(int position) { return mFragmentList.get(position); }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public float getPageWidth(int position) {
        return pageWidth;
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    public void setAdapterDataForFragment(int position, ArrayList<?> forumPostsMsgs) {
        if (forumPostsMsgs != null) {
            ((ForumPostsMsgsFragment) mFragmentList.get(position)).changePostsList(forumPostsMsgs, position);
        }
    }

    /**
     * Customizacja layutu: czcionka, kolor, tekst
     * @param position pozycja w tablayout
     * @return custom view
     */
    public View getTabIconView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.layout);
        title.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Dosis-Light.ttf"));
        if (position == 0) {
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.INVISIBLE);
        }
        title.setText(getPageTitle(position));
        icon.setVisibility(View.VISIBLE);
        TypedArray icons = context.getResources().obtainTypedArray(R.array.forum_tab_menu_icons);
        icon.setImageDrawable(icons.getDrawable(position));
        return view;
    }

    /**
     * Customizacja layutu: czcionka, kolor, tekst
     * @param position pozycja w tablayout
     * @return custom view
     */
    public View getTabDetailView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.layout);
        title.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Dosis-Light.ttf"));
        title.setText(getPageTitle(position));
        icon.setVisibility(View.GONE);
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitle.get(position);
    }
}
