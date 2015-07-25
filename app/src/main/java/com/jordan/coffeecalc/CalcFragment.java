package com.jordan.coffeecalc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.jordan.coffeecalc.CountDownService.CountDownServiceBinder;
import com.jordan.coffeecalc.TimerDialogMaker.TimerStartListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;


public class CalcFragment extends Fragment implements OnSeekBarChangeListener,
        NumberPicker.OnValueChangeListener, TimerStartListener {

    public static final String[] BREW_SPINNER_OPTIONS = {"French Press", "Pour Over",
            "Moka Pot", "Aeropress"};
    //French, pour over and aeropress need all
    //moka nothing
    //espresso coffee weight, no water no ratio no temperature timer that counts up but don't save
    public static final String[] WATER_SPINNER_OPTIONS = {"US fl oz", "UK fl oz", "mL"};
    public static final String[] BEAN_SPINNER_OPTIONS = {"Grams", "Scoops"};
    public static final String[] TEMPERATURE_SPINNER_OPTIONS = {"Farenheit", "Celcius",
            "Kelvin", "Not used"};
    public static final double OUNCES_WATER_IN_GRAM = (double) 29.5735296875;
    public static final double GRAMS_WATER_IN_OUNCE = (double) 0.03381402255891948;
    public static final double GRAMS_BEANS_IN_TSP = (double) 8.0;

    LogFragment mLogFragment;
    EditText editTitle;
    EditText editDetails;
    Spinner mBrewSpinner;
    Spinner mWaterSpinner;
    Spinner mBeanSpinner;
    Spinner mTempSpinner;
    SeekBar mRatioSlider;
    SeekBar mCoffeeSlider;
    SeekBar mTempSlider;
    String mTitle;
    String mDetails;

    public static final int FRENCH = 0;
    public static final int POUR = 1;
    public static final int MOKA = 2;
    public static final int AERO = 3;
    public static final int BEAN_GRAMS = 0;
    public static final int BEAN_SCOOPS = 1;
    public static final int WATER_USOZ = 0;
    public static final int WATER_UKOZ = 1;
    public static final int WATER_ML = 2;
    public static final int TEMP_F = 0;
    public static final int TEMP_C = 1;
    public static final int TEMP_K = 2;
    public static final int TEMP_NA = 3;

    double mRatio = (double) 15.000;
    double mBeanAmount = (double) 8.000;
    double mWaterAmount;

    int mBrewOption;
    int mTemperature;
    int mRatioMinutes;
    int mRatioSeconds;
    int mWaterUnits;
    int mBeanUnits;
    int mTempUnits;
    int mTime;
    int mRating;
    int mTempProgress;
    int mWaterProgress;

    Button mRatioButton;
    TextView mRatioText;
    TextView mRatioTitle;
    TextView mRatioInfo;
    TextView mCoffeeAmount;
    TextView mBeanAmountText;
    TextView mBeanAmountNumber;
    TextView mCoffeeAmountNumber;
    TextView mTempTitle;
    TextView mTimeText;
    Timer time;
    Handler timerHandler;
    Long mStartTime;
    TextView pickerText;
    SharedPreferences settings;
    Button mStartButton;
    Button mSaveButton;
    CountDownService mService;
    Boolean isRunning = false;

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - mStartTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            mTimeText.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
            isRunning = true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle save) {
        View view = inflater.inflate(R.layout.fragment_calc, parent, false);
        mBrewOption = 0;
        settings = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);


        mBrewSpinner = (Spinner) view.findViewById(R.id.brew_spinner);
        ArrayAdapter<String> spinnerBrewArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                BREW_SPINNER_OPTIONS);
        spinnerBrewArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBrewSpinner.setAdapter(spinnerBrewArrayAdapter);

        mWaterSpinner = (Spinner) view.findViewById(R.id.water_spinner);
        ArrayAdapter<String> spinnerWaterArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                WATER_SPINNER_OPTIONS);
        spinnerWaterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWaterSpinner.setAdapter(spinnerWaterArrayAdapter);
        mBeanSpinner = (Spinner) view.findViewById(R.id.bean_spinner);
        ArrayAdapter<String> spinnerBeanArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                BEAN_SPINNER_OPTIONS);
        spinnerBeanArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBeanSpinner.setAdapter(spinnerBeanArrayAdapter);
        mTempSpinner = (Spinner) view.findViewById(R.id.temp_spinner);
        ArrayAdapter<String> spinnerTempArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                TEMPERATURE_SPINNER_OPTIONS);


        mRatioText = (TextView) view.findViewById(R.id.ratio_text);
        mRatio = settings.getFloat("Ratio", (float) 26.67);
        mRatioText.setText("" + String.format("%3.2f", mRatio));
        mRatioText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatioPicker();
            }
        });
        mRatioTitle = (TextView) view.findViewById(R.id.ratio_title);
        mRatioTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatioPicker();
            }
        });
        mRatioInfo = (TextView) view.findViewById(R.id.ratio_info);
        mRatioInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatioPicker();
            }
        });

        spinnerTempArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTempSpinner.setAdapter(spinnerTempArrayAdapter);
        mTempSlider = (SeekBar) view.findViewById(R.id.temp_bar);
        mTempSlider.setOnSeekBarChangeListener(this);
        mTempSlider.setEnabled(true);
        mTempTitle = (TextView) view.findViewById(R.id.temp_title);
        //mTempTitle.setText(mTemperature + " degrees");

        mCoffeeSlider = (SeekBar) view.findViewById(R.id.coffee_amount_bar);
        mCoffeeSlider.setOnSeekBarChangeListener(this);
        mCoffeeAmount = (TextView) view.findViewById(R.id.coffee_amount_title);
        mCoffeeAmountNumber = (TextView) view.findViewById(R.id.coffee_amount_number);
        //mCoffeeAmountNumber.setText("" + mWaterAmount);

        mBeanAmountText = (TextView) view.findViewById(R.id.bean_amount);
        mBeanAmountNumber = (TextView) view.findViewById(R.id.bean_amount_number);
        //mBeanAmountNumber.setText("" + mBeanAmount);

        mTimeText = (TextView) (view).findViewById(R.id.timer_text);
        mStartTime = (long) 0;
        timerHandler = new Handler();


        mStartButton = (Button) view.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavePicker();
            }
        });
        mBeanSpinner.setSelection(settings.getInt("Bean units", 1));
        mWaterSpinner.setSelection(settings.getInt("Water units", 2));
        mTempSpinner.setSelection(settings.getInt("Temperature units", 2));
        mWaterAmount = settings.getFloat("Water amount", 12);
        mRatioMinutes = settings.getInt("Ratio minutes", 20);
        mRatioSeconds = settings.getInt("Ratio seconds", 30);
        mTempProgress = settings.getInt("Temperature", 75);
        mBeanAmount = settings.getFloat("Bean amount", 12);
        mWaterProgress = settings.getInt("Water progress", 50);
        mCoffeeSlider.setProgress(mWaterProgress);
        mTime = settings.getInt("Time", 300);
        mRatio = settings.getFloat("Ratio", (float) 26.67);
        mBeanAmountNumber.setText("" + String.format("%3.2f", mBeanAmount));
        mCoffeeAmountNumber.setText("" + mWaterAmount);
        mTempSlider.setProgress(mTempProgress);

        mSaveButton = (Button) view.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    showTimePicker();
                } else {
                    showTimerRunningPicker();
                }
            }
        });


        mWaterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int oldUnits = mWaterUnits;
                switch (arg2) {
                    case WATER_USOZ:
                        mWaterUnits = WATER_USOZ;
                        mCoffeeSlider.setMax(64);
                        mWaterUnitsChanged(oldUnits);
                        mCoffeeSlider.setProgress((int) mWaterAmount);
                        break;
                    case WATER_UKOZ:
                        mWaterUnits = WATER_UKOZ;
                        mCoffeeSlider.setMax(64);
                        mWaterUnitsChanged(oldUnits);
                        mCoffeeSlider.setProgress((int) mWaterAmount);
                        break;
                    case WATER_ML:
                        mWaterUnits = WATER_ML;
                        mCoffeeSlider.setMax(200); //if broken change to 2000
                        mWaterUnitsChanged(oldUnits);
                        mCoffeeSlider.setProgress((int) mWaterAmount);
                        break;
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Water units", mWaterUnits);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mBeanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int mUnit = mBeanUnits;
                switch (arg2) {
                    case BEAN_GRAMS:
                        mBeanUnits = BEAN_GRAMS;
                        mBeanAmount = (mUnit == BEAN_GRAMS) ? mBeanAmount : mBeanAmount * 8;
                        mBeanAmountNumber.setText("" +
                                String.format("%3.2f", mBeanAmount));
                        break;
                    case BEAN_SCOOPS:
                        mBeanUnits = BEAN_SCOOPS;
                        mBeanAmount = (mUnit == BEAN_GRAMS) ? mBeanAmount / 8 : mBeanAmount;
                        mBeanAmountNumber.setText("" +
                                String.format("%3.2f", mBeanAmount));
                        break;
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Bean units", mBeanUnits);
                editor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mTempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                switch (arg2) {
                    case TEMP_F:
                        mTempSlider.setAlpha((float) 1.0);
                        mTempSlider.setEnabled(true);
                        mTempSlider.setMax(32);
                        mTempSlider.setProgress(mTempProgress);
                        mTempUnits = 0;
                        break;
                    case TEMP_C:
                        mTempSlider.setMax(15);
                        mTempSlider.setAlpha((float) 1.0);
                        mTempSlider.setEnabled(true);
                        mTempSlider.setMax(10);
                        mTempSlider.setProgress(mTempProgress);
                        mTempUnits = 1;
                        break;
                    case TEMP_K:
                        mTempSlider.setMax(30);
                        mTempSlider.setAlpha((float) 1.0);
                        mTempSlider.setEnabled(true);
                        mTempSlider.setProgress(mTempProgress);
                        mTempUnits = 2;
                        break;
                    case TEMP_NA:
                        mTempUnits = 3;
                        mTempSlider.setAlpha((float) .2);
                        mTempSlider.setEnabled(false);
                        break;
                }
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Temperature units", mTempUnits);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mBrewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                       long arg3) {
                mBrewOption = arg2;
                switch (mBrewOption) {
                    case POUR:
                    case FRENCH:
                    case AERO:
                        mCoffeeAmount.setAlpha(1);
                        mCoffeeAmountNumber.setAlpha(1);
                        mBeanAmountNumber.setAlpha(1);
                        mBeanAmountText.setAlpha(1);
                        mTempTitle.setAlpha(1);
                        mRatioText.setClickable(true);
                        mRatioText.setAlpha((float) 1);
                        mRatioInfo.setClickable(true);
                        mRatioInfo.setAlpha((float) 1);
                        mRatioTitle.setClickable(true);
                        mRatioTitle.setAlpha((float) 1);
                        mCoffeeSlider.setEnabled(true);
                        mCoffeeSlider.setAlpha((float) 1);
                        mTempSlider.setEnabled(true);
                        mTempSlider.setAlpha((float) 1);
                        mBeanSpinner.setClickable(true);
                        mBeanSpinner.setAlpha((float) 1);
                        mTempSpinner.setClickable(true);
                        mTempSpinner.setAlpha((float) 1);
                        mWaterSpinner.setAlpha((float) 1);
                        mWaterSpinner.setClickable(true);
                        break;
                    case MOKA:
                        mCoffeeAmount.setAlpha((float) 0.5);
                        mCoffeeAmountNumber.setAlpha((float) 0.5);
                        mBeanAmountNumber.setAlpha((float) 0.5);
                        mBeanAmountText.setAlpha((float) 0.5);
                        mTempTitle.setAlpha((float) 0.5);
                        mRatioText.setClickable(false);
                        mRatioText.setAlpha((float) 0.5);
                        mRatioInfo.setClickable(false);
                        mRatioInfo.setAlpha((float) 0.5);
                        mRatioTitle.setClickable(false);
                        mRatioTitle.setAlpha((float) 0.5);
                        mCoffeeSlider.setEnabled(false);
                        mCoffeeSlider.setAlpha((float) 0.5);
                        mTempSlider.setEnabled(false);
                        mTempSlider.setAlpha((float) 0.5);
                        mBeanSpinner.setClickable(false);
                        mBeanSpinner.setAlpha((float) 0.5);
                        mTempSpinner.setClickable(false);
                        mTempSpinner.setAlpha((float) 0.5);
                        mWaterSpinner.setAlpha((float) 0.5);
                        mWaterSpinner.setClickable(false);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        mWaterSpinner.getSelectedItemPosition();
        mTempSpinner.getSelectedItemPosition();


        return view;
    }

    public void onResume() {
        super.onResume();
        if (mService != null) {
            mService.setConnected(true);
            if (!mService.onResumeUpdate()) {
                mSaveButton.setText("Finished");
                isRunning = false;
                mSaveButton.setClickable(true);
            }
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (seekBar == mCoffeeSlider) {
            mWaterProgress = progress;
            if (mWaterUnits != WATER_ML) { //if broken remove
                mWaterAmount = progress;
            } else {//if broken remove
                mWaterAmount = progress * 10; //if broken remove
            }//if broken remove
            mCoffeeAmountNumber.setText("" + String.format("%3.0f", mWaterAmount));
            mBeanAmount = (mBeanUnits == BEAN_GRAMS) ? convertWaterToGrams(mWaterAmount)
                    / mRatio : (convertWaterToGrams(mWaterAmount) / mRatio) / 8;
            mBeanAmountNumber.setText("" + String.format("%3.2f", mBeanAmount));
        }
        if (seekBar == mTempSlider) {
            mTempProgress = progress;
            switch (mTempSpinner.getSelectedItemPosition()) {
                case TEMP_F:
                    progress = progress + 180;
                    mTempTitle.setText("" + progress + " degrees ");
                    break;
                case TEMP_C:
                    progress = progress + 90;
                    mTempTitle.setText("" + progress + " degrees ");
                    break;
                case TEMP_K:
                    progress = progress + 350;
                    mTempTitle.setText("" + progress + " degrees ");
                    break;
                case TEMP_NA:
                    break;
            }
            mTemperature = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    public void mWaterUnitsChanged(int old) {
        if (old != mWaterUnits) {

            switch (old) {
                case WATER_ML:
                    if (mWaterUnits == WATER_USOZ) {
                        mWaterAmount = (double) (mWaterAmount / 29.5735295625);
                    } else {
                        mWaterAmount = (double) (mWaterAmount / 28.4130625);
                    }
                    break;
            }
            mBeanAmount = (mBeanUnits == BEAN_GRAMS) ? (double) convertWaterToGrams(mWaterAmount)
                    / mRatio : (double) (convertWaterToGrams(mWaterAmount) / mRatio) / 8;
            mBeanAmountNumber.setText("" + String.format("%3.2f", mBeanAmount));
        }
    }

    public double convertWaterToGrams(double unit) {
        double mUnit = unit;
        switch (mWaterUnits) {
            case WATER_USOZ:
                mUnit = (double) (mWaterAmount * 29.5735295625);
                break;
            case WATER_UKOZ:
                mUnit = (double) (mWaterAmount * 28.4130625);
                break;
            case WATER_ML:
                break;
        }
        return mUnit;
    }

    public double convertBeansToGrams(double unit) {
        double mUnit = unit;
        switch (mBeanUnits) {
            case (BEAN_GRAMS):
                break;
            case (BEAN_SCOOPS):
                mUnit = mUnit / 8;
        }
        return mUnit;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        if (mService != null) {
            mService.setConnected(false);
        }

    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        mTime = newVal;

    }


    @Override
    public void onStart(DialogInterface dialog, int time) {
        Intent startTimer = new Intent(getActivity(), CountDownService.class);
        startTimer.putExtra("action", CountDownService.ACTION_START);
        startTimer.putExtra("time", 10);
        getActivity().startService(startTimer);

    }

    public void showSavePicker() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.save_picker, null);
        builder.setView(view);
        builder.setTitle("Set details for log");
        editTitle = (EditText) view.findViewById(R.id.save_picker_title);
        editDetails = (EditText) view.findViewById(R.id.save_picker_details);
        final NumberPicker ratingPicker = (NumberPicker) view.findViewById(R.id.rating_picker);
        ratingPicker.setMaxValue(10); // max value 100
        ratingPicker.setMinValue(1);   // min value 0
        ratingPicker.setValue(5);

        builder.setPositiveButton("Save to log", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRating = ratingPicker.getValue();
                mTitle = editTitle.getText().toString();
                mDetails = editDetails.getText().toString();
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("Water amount", (float) mWaterAmount);
                editor.putFloat("Bean amount", (float) mBeanAmount);
                editor.putInt("Temperature", mTempProgress);
                editor.putInt("Water progress", mWaterProgress);
                editor.commit();
                LogItem item = new LogItem(mRatio, mWaterAmount, mBeanAmount, mBrewOption,
                        mTemperature, mWaterUnits, mBeanUnits, mTempUnits, mTime, mRating,
                        mTitle, mDetails);
                save(item);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cancelTimer = new Intent(getActivity(), CountDownService.class);
                cancelTimer.putExtra("action", CountDownService.ACTION_CANCEL);
                getActivity().startService(cancelTimer);

            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
        d.show();
    }

    public void showTimerRunningPicker() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_picker, null);
        builder.setView(view);
        builder.setTitle("Timer is Running!");
        builder.setPositiveButton("Cancel timer", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent countDown = new Intent(getActivity(), CountDownService.class);
                countDown.putExtra("action", CountDownService.ACTION_CANCEL);
                getActivity().startService(countDown);
                isRunning = false;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
        d.show();
    }

    public void showTimePicker() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_picker, null);
        builder.setView(view);
        builder.setTitle("Set Brew Time");
        final NumberPicker minutesPicker = (NumberPicker) view.findViewById(R.id.number_picker_minutes);
        final NumberPicker secondsPicker = (NumberPicker) view.findViewById(R.id.number_picker_seconds);
        minutesPicker.setMaxValue(60); // max value 100
        minutesPicker.setMinValue(0);   // min value 0
        minutesPicker.setWrapSelectorWheel(false);
        minutesPicker.setValue((mTime / 60));
        secondsPicker.setMaxValue(60);
        secondsPicker.setMinValue(0);
        secondsPicker.setValue(mTime % 60);
        builder.setPositiveButton("Start timer", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                isRunning = true;
                mTime = (minutesPicker.getValue() * 60) + (secondsPicker.getValue());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Time", mTime);
                editor.commit();

                ServiceConnection connection = new ServiceConnection() {

                    @Override
                    public void onServiceConnected(ComponentName name,
                                                   IBinder service) {
                        CountDownServiceBinder binder = (CountDownServiceBinder) service;
                        mService = binder.getService();
                        mService.startCountDown(mTime, mSaveButton);
                        mService.setConnected(true);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        mService.setConnected(false);
                        mService = null;
                    }

                };
                Intent countDown = new Intent(getActivity(), CountDownService.class);
                getActivity().bindService(countDown, connection, Context.BIND_AUTO_CREATE);


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
        d.show();
        d.show();


    }

    public void showRatioPicker() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.ratio_picker, null);
        builder.setView(view);
        builder.setTitle("Set Ratio");
        final NumberPicker minutesPicker = (NumberPicker) view.findViewById(R.id.ratio_picker_tens);
        final NumberPicker secondsPicker = (NumberPicker) view.findViewById(R.id.ratio_picker_tenths);
        minutesPicker.setMaxValue(100); // max value 100
        minutesPicker.setMinValue(1);   // min value 0
        minutesPicker.setWrapSelectorWheel(false);
        mRatioMinutes = settings.getInt("Ratio minutes", 20);
        mRatioSeconds = settings.getInt("Ratio seconds", 30);
        Log.e("", "RATIO SECONDS: " + mRatioSeconds);
        minutesPicker.setValue(mRatioMinutes);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(99);
        secondsPicker.setValue(mRatioSeconds);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRatioMinutes = minutesPicker.getValue();
                mRatioSeconds = secondsPicker.getValue();

                Log.e("", "RATIO SECONDS: " + mRatioSeconds);
                mRatio = (double) minutesPicker.getValue() + ((double) secondsPicker.getValue()) / 100;
                mRatioText.setText("" + String.format("%3.2f", mRatio));
                mBeanAmount = convertWaterToGrams(mWaterAmount) / mRatio;
                mBeanAmountNumber.setText("" +
                        String.format("%3.2f", mBeanAmount));
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Ratio minutes", mRatioMinutes);
                editor.putInt("Ratio seconds", mRatioSeconds);
                editor.putFloat("Ratio", (float) mRatio);
                editor.commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog d = builder.create();
        d.getWindow().getAttributes().windowAnimations = R.style.Animations_SmileWindow;
        d.show();


    }


    public void save(LogItem logItem) {
        JSONArray jsonArr;
        try {
            jsonArr = new JSONArray(settings.getString("log_list", "[]"));
            jsonArr.put(logItem.toJSON(logItem));
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("log_list", jsonArr.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(null, "JSON EXCEPTION ?!?!?!?!!?");
        }

        if (mLogFragment != null) {
            mLogFragment.updateList();
        }
    }

    public void getList(JSONObject json) {

    }


}
