package com.jordan.coffeecalc;

import java.util.Comparator;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class LogItem {
	private double mRatio, mWater, mBeanAmount;
	private int mRating, mTemperature, mWaterUnits, mBeanUnits, mTemperatureUnits, mTime, mBrewOption;
	private long mTimeStamp;
	private String mTitle, mDetails;
	
	LogItem(double ratio, double water, double beanAmount, int brewOption, int temperature, int waterUnits, int beanUnits, int temperatureUnits, int time, int rating, String title, String details){
		mRatio = ratio;
		mWater = water;
		mBeanAmount = beanAmount;
		mTemperature = temperature;
		mTemperatureUnits = temperatureUnits;
		mBeanUnits = beanUnits;
		mWaterUnits = waterUnits;
		mTimeStamp = System.currentTimeMillis();		
		mTime = time;
		mTitle = title;
		mDetails = details;
		mRating = rating;
		mBrewOption = brewOption;
	}
	LogItem(JSONObject json) throws JSONException{
		mRatio = json.getDouble("ratio");
		mWater = json.getDouble("water");
		mBeanAmount = json.getDouble("beanAmount");
		mTemperature = json.getInt("temperature");
		mWaterUnits = json.getInt("waterUnits");
		mBeanUnits = json.getInt("beanUnits");
		mTemperatureUnits = json.getInt("temperatureUnits");
		mTime = json.getInt("time");
		mTitle = json.getString("title");
		mDetails = json.getString("details");
		mTimeStamp = json.getLong("time_stamp");
		mRating = json.getInt("rating");
		mBrewOption = json.getInt("brew");
	}
	
	
	public JSONObject toJSON(LogItem item) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("ratio", item.getRatio());
		json.put("water", item.getWater());
		json.put("beanAmount", item.getBeanAmount());
		json.put("temperature", item.getTemperature());
		json.put("waterUnits", item.getWaterUnits());
		json.put("beanUnits", item.getBeanUnits());
		json.put("temperatureUnits", item.getTemperatureUnits());
		json.put("time", item.getTime());
		json.put("details", item.getDetails());
		json.put("title", item.getTitle());
		json.put("time_stamp", item.getTimeStamp());
		json.put("rating", item.getRating());
		json.put("brew", item.getBrew());
		
		return json;
	}
	
	public int getBrew(){
		return mBrewOption;
	}
	public void setBrew(int brew){
		mBrewOption = brew;
	}
	public void setRating(int rating){
		mRating = rating;
	}
	public int getRating(){
		return mRating;
	}
	public void setTitle(String title){
		mTitle = title;
	}
	public String getTitle(){
		return mTitle;
	}
	public String getDetails(){
		return mDetails;
	}
	public void setDetails(String details){
		mDetails = details;
	}
	
	public double getRatio(){
		return mRatio;
	}
	
	public double getWater(){
		return mWater;
	}
	
	public double getBeanAmount(){
		return mBeanAmount;
	}
	
	public int getTemperature(){
		return mTemperature;
	}
	public int getBeanUnits(){
		return mBeanUnits;
	}
	
	public int getTemperatureUnits(){
		return mTemperatureUnits;
	}
	
	public int getWaterUnits(){
		return mWaterUnits;
	}
	
	public long getTimeStamp(){
		return mTimeStamp;
	}
	public int getTime(){
		return mTime;
	}
	public void setTime(int time){
		mTime = time;
	}
	
	
	public static class LatestFirst implements Comparator<LogItem>{
		@Override
		public int compare(LogItem a, LogItem b) {
			if (b.mTimeStamp < a.mTimeStamp){
				return 1;
			}else if (b.mTimeStamp > a.mTimeStamp){
				return -1;
			}else{
				return 0;
			}
		}
	}

	public static class EarliestFirst implements Comparator<LogItem>{
		@Override
		public int compare(LogItem a, LogItem b) {
			if (b.mTimeStamp < a.mTimeStamp){
				return -1;
			}else if (b.mTimeStamp > a.mTimeStamp){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	public static class RatingsHigh implements Comparator<LogItem>{
		@Override
		public int compare(LogItem a, LogItem b) {
			if (b.mRating < a.mRating){
				return 1;
			}else if (b.mRating > a.mRating){
				return -1;
			}else{
				return 0;
			}
		}
	}
	
	public static class RatingsLow implements Comparator<LogItem>{
		@Override
		public int compare(LogItem a, LogItem b) {
			if (b.mRating < a.mRating){
				return -1;
			}else if (b.mRating > a.mRating){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	
	
}
