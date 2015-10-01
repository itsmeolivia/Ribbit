package com.itsmeolivia.ribbit.UI;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.itsmeolivia.ribbit.R;
import com.itsmeolivia.ribbit.UI.FriendFragment;
import com.itsmeolivia.ribbit.UI.InboxFragment;

import java.util.Locale;

/**
 * Created by olivia on 9/3/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }
}
