package com.jordan.coffeecalc;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.LauncherActivity.ListItem;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends Activity implements ActionBar.TabListener, OnPageChangeListener{

	ViewPager mPager;
	public static final int BEAN_GRAMS = 0;
	public static final int BEAN_SCOOPS = 1;
	public static final int WATER_GRAMS = 0;
	public static final int WATER_OUNCES = 1;
	public static final int WATER_CC = 2;
	int mBeanUnits = 0;
	int mWaterUnits = 0;
	int mTemperatureUnits = 0;
	int mTimerStatus;
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new CoffeePagerAdapter(getFragmentManager()));
		mPager.setOnPageChangeListener(this);
		
		ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ab.addTab(ab.newTab().setTabListener(this).setText("     Calc"));
		ab.addTab(ab.newTab().setTabListener(this).setText("     Log"));
		
		mPager.setCurrentItem(0);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		
		settings = getSharedPreferences("UserInfo", 0);
		editor = settings.edit();
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction transaction) {
	
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		mPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
	
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	
		
	}

	@Override
	public void onPageScrolled(int position, float offset, int arg2) {
		
		
	}

	@Override
	public void onPageSelected(int position) {
		ActionBar ab = getActionBar();
		ab.selectTab(ab.getTabAt(position));
		
	}
	
	public void setBeanUnits(int units){
		mBeanUnits = units;
		editor.putInt("Bean units", units);
	}
	public int getBeanUnits(){
		return mBeanUnits;
	}
	
	public void setWaterUnits(int units){
		mWaterUnits = units;
		editor.putInt("Water units", units);
	}
	public int getWaterUnits(){
		return mWaterUnits;
	}
	
	public void setTemperatureUnits(int units){
		mTemperatureUnits = units;
		editor.putInt("Temperature units", units);
	}
	public int getTemperatureUnits(){
		return mTemperatureUnits;
	}
	public int getTimerStatus(){
		return mTimerStatus;
	}
	public void setTimerStatus(int status){
		mTimerStatus = status;
	}
	
	
}
