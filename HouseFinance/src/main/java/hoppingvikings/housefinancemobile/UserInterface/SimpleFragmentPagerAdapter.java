package hoppingvikings.housefinancemobile.UserInterface;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import hoppingvikings.housefinancemobile.R;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.BillsFragment;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.ShoppingListFragment;
import hoppingvikings.housefinancemobile.UserInterface.Fragments.StatisticsFragment;

/**
 * Created by Josh on 24/09/2016.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] {"BILLS", "SHOPPING", "STATISTICS"};
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:
                return new BillsFragment();
            case 1:
                return new ShoppingListFragment();
            case 2:
                return new StatisticsFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }

    public View getTabView(int pos)
    {
        View tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) tab.findViewById(R.id.customtext);
        tv.setText(tabTitles[pos]);
        return tab;
    }
}
