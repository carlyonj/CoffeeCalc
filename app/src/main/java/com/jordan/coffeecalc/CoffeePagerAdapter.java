package com.jordan.coffeecalc;


import android.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CoffeePagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_PAGES = 2;
    public static final int CALCULATOR = 0;
    public static final int LOG = 1;
    LogFragment mLogFragment;
    CalcFragment mCalcFragment;


    public CoffeePagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public android.support.v4.app.Fragment getItem(int pos) {

        switch (pos) {
            case CALCULATOR:
                mCalcFragment = new CalcFragment();
                mCalcFragment.mLogFragment = mLogFragment;
                return mCalcFragment;
            case LOG:
                mLogFragment = new LogFragment();
                mCalcFragment.mLogFragment = mLogFragment;
                return mLogFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }


}
