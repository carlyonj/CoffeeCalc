package com.jordan.coffeecalc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.jordan.coffeecalc.LogItem.EarliestFirst;
import com.jordan.coffeecalc.LogItem.LatestFirst;
import com.jordan.coffeecalc.LogItem.RatingsHigh;
import com.jordan.coffeecalc.LogItem.RatingsLow;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressLint("ResourceAsColor")
public class LogFragment extends Fragment
        implements OnItemClickListener, OnItemLongClickListener, TextWatcher {

    List<LogItem> mList;
    SharedPreferences settings;
    ListView mListView;
    TextView tvTitle;
    TextView tvDetails;
    TextView tvNumbers;
    TextView tvDate;
    TextView tvIcon;
    EditText mSearchText;
    List<LogItem> mSearchList;
    MyAdapter customAdapter;
    Spinner mSortSpinner;
    public static final int FRENCH = 0;
    public static final int POUR = 1;
    public static final int MOKA = 2;
    public static final int AERO = 3;
    public static final String[] BREW_SPINNER_OPTIONS = {"French Press", "Pour Over", "Moka Pot",
            "Aeropress"};
    public static final String[] SORT_SPINNER_OPTIONS = {"Date ascending", "Date descending",
            "Rating ascending", "Rating descending"};
    public static final String[] WATER_SPINNER_OPTIONS = {"US fl oz", "UK fl oz", "mL"};
    public static final String[] BEAN_SPINNER_OPTIONS = {"Grams", "Scoops"};
    public static final String[] TEMPERATURE_SPINNER_OPTIONS = {"Farenheit", "Celcius", "Kelvin",
            "Not used"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle save) {
        View root = inflater.inflate(R.layout.fragment_log, parent, false);
        mListView = (ListView) root.findViewById(R.id.log_list);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mSearchText = (EditText) root.findViewById(R.id.log_search);
        mSearchText.addTextChangedListener(this);

        mSortSpinner = (Spinner) root.findViewById(R.id.log_sort);
        ArrayAdapter<String> sortSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,
                SORT_SPINNER_OPTIONS);
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortSpinnerAdapter);

        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                updateList();
            }

            public void onNothingSelected1(AdapterView<?> arg0) {
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        return root;
    }

    @SuppressLint("ResourceAsColor")
    public class MyAdapter extends BaseAdapter {
        public MyAdapter(Context context, int resource) {

        }

        public MyAdapter(Context context, int resource, List<LogItem> list) {

        }

        public MyAdapter(LogFragment logFragment, int fragmentLog,
                         List<LogItem> mList) {
            // TODO Auto-generated constructor stub
        }

        public void search(CharSequence search) {
            Iterator<LogItem> itr = mSearchList.iterator();
            LogItem itm;
            String srch = search.toString();
            while (itr.hasNext()) {
                itm = (LogItem) itr.next();
                if (!itm.getTitle().toLowerCase().contains(srch.toLowerCase())
                        && !BREW_SPINNER_OPTIONS[itm.getBrew()].toLowerCase().contains(srch.toLowerCase())) {
                    itr.remove();
                }
            }
        }


        public void remove(int position) {
            mList.remove(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public LogItem getItem(int pos) {
            return mList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(int pos, View conv, ViewGroup parent) {
            View view;
            if (conv == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.log_row_item, parent, false);
            } else {
                view = conv;
            }

            LogItem item = getItem(pos);

            if (item != null) {
                tvIcon = (TextView) view.findViewById(R.id.log_image);
                tvTitle = (TextView) view.findViewById(R.id.log_item_title);
                tvDetails = (TextView) view.findViewById(R.id.log_item_info);
                tvNumbers = (TextView) view.findViewById(R.id.log_item_numbers);
                tvDate = (TextView) view.findViewById(R.id.log_item_date);
                tvDetails.setVisibility(View.GONE);
                tvNumbers.setVisibility(View.GONE);
                if (tvIcon != null) {
                    switch (item.getBrew()) {
                        case AERO:
                            tvIcon.setText("A");
                            tvIcon.setTextColor(Color.parseColor("#e049cb"));
                            break;
                        case FRENCH:
                            tvIcon.setText("F");
                            tvIcon.setTextColor(Color.parseColor("#e05e49"));
                            break;
                        case POUR:
                            tvIcon.setText("P");
                            tvIcon.setTextColor(Color.parseColor("#49e05e"));
                            break;
                        case MOKA:
                            tvIcon.setText("M");
                            tvIcon.setTextColor(Color.parseColor("#49cbe0"));
                            break;
                        default:

                    }
                }
                if (tvTitle != null) {
                    tvTitle.setText(item.getTitle());
                }
                if (tvDetails != null) {
                    tvDetails.setText(item.getDetails());
                }
                if (tvNumbers != null) {
                    if (item.getBrew() == FRENCH) {
                        tvNumbers.setText("\n" + item.getWater() + " "
                                + WATER_SPINNER_OPTIONS[item.getWaterUnits()] + " of coffee \n"
                                + "Ratio: " + String.format("%3.2f", item.getRatio())
                                + "grams of water to grams of beans\n" +
                                String.format("%3.2f", item.getBeanAmount())
                                + " " + BEAN_SPINNER_OPTIONS[item.getBeanUnits()] + " of beans \n"
                                + item.getTemperature()
                                + " " + TEMPERATURE_SPINNER_OPTIONS[item.getTemperatureUnits()]
                                + " for " + item.getTime() + " seconds \n");
                    }
                    if (item.getBrew() == AERO) {
                        tvNumbers.setText("\n" + item.getWater() + " "
                                + WATER_SPINNER_OPTIONS[item.getWaterUnits()] + " of coffee \n" +
                                "Ratio: " + String.format("%3.2f", item.getRatio())
                                + "grams of water to grams of beans\n" +
                                String.format("%3.2f", item.getBeanAmount())
                                + " " + BEAN_SPINNER_OPTIONS[item.getBeanUnits()] + " of beans \n"
                                + item.getTemperature()
                                + " " + TEMPERATURE_SPINNER_OPTIONS[item.getTemperatureUnits()]
                                + " for " + item.getTime() + " seconds \n");

                    }
                    if (item.getBrew() == POUR) {
                        tvNumbers.setText("\n" + item.getWater() + " " + WATER_SPINNER_OPTIONS[item.getWaterUnits()]
                                + " of coffee \n" +
                                "Ratio: " + String.format("%3.2f", item.getRatio())
                                + "grams of water to grams of beans\n"
                                + String.format("%3.2f", item.getBeanAmount())
                                + " " + BEAN_SPINNER_OPTIONS[item.getBeanUnits()] + " of beans \n"
                                + item.getTemperature() + " "
                                + TEMPERATURE_SPINNER_OPTIONS[item.getTemperatureUnits()] + " for "
                                + item.getTime() + " seconds \n");

                    } else {
                        //implement more types later
                    }
                }
                if (tvDate != null) {
                    Log.d(null, "TIMESTAMP: " + item.getTimeStamp());
                    tvDate.setText(displayDate(item.getTimeStamp())
                            + "               Rating: " + item.getRating() + "/10");
                }
            }


            return view;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
        mSearchList = mList;

    }

    int mLastPosition = -1;

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, final View view, final int pos,
                                   long arg3) {

        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.log_edit_dialog, null);
        final EditText editTitle = (EditText) layout.findViewById(R.id.log_edit_title);
        final EditText editDetails = (EditText) layout.findViewById(R.id.log_edit_details);
        final NumberPicker ratingPicker = (NumberPicker) layout.findViewById(R.id.log_rating_picker);
        ratingPicker.setMaxValue(10); // max value 100
        ratingPicker.setMinValue(0);   // min value 0
        ratingPicker.setValue(mList.get(pos).getRating());
        editTitle.setText(mList.get(pos).getTitle());
        editDetails.setText(mList.get(pos).getDetails());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        builder.setTitle("Edit details or delete log entry");
        builder.setPositiveButton("Save to log", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mList.get(pos).setTitle(editTitle.getText().toString());
                mList.get(pos).setDetails(editTitle.getText().toString());
                mList.get(pos).setRating(ratingPicker.getValue());
                customAdapter.notifyDataSetChanged();
                save();

            }
        });

        builder.setNeutralButton("Delete item", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                customAdapter.remove(pos);
                //updateList();
                customAdapter.notifyDataSetChanged();
                save();

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
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> list, View clickedView, int position, long id) {

        View lastClickedView = mListView.getChildAt(mLastPosition - mListView.getFirstVisiblePosition());
        if (mLastPosition != position) {
            TextView tvDetails = (TextView) clickedView.findViewById(R.id.log_item_info);
            TextView tvNumbers = (TextView) clickedView.findViewById(R.id.log_item_numbers);
            tvNumbers.setVisibility(View.VISIBLE);
            tvDetails.setVisibility(View.VISIBLE);
            if (lastClickedView != null) {
                tvDetails = (TextView) lastClickedView.findViewById(R.id.log_item_info);
                tvNumbers = (TextView) lastClickedView.findViewById(R.id.log_item_numbers);
                tvNumbers.setVisibility(View.GONE);
                tvDetails.setVisibility(View.GONE);
            }
        } else {
            TextView tvDetails = (TextView) clickedView.findViewById(R.id.log_item_info);
            TextView tvNumbers = (TextView) clickedView.findViewById(R.id.log_item_numbers);
            int visibility = tvNumbers.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            tvNumbers.setVisibility(visibility);
            tvDetails.setVisibility(visibility);
        }
        mLastPosition = position;
    }

    public List<LogItem> buildList() {
        ArrayList<LogItem> list = new ArrayList<LogItem>();
        settings = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        try {
            JSONArray jsonArr = new JSONArray(settings.getString("log_list", ""));
            for (int i = 0, n = jsonArr.length(); i < n; i++) {
                list.add(new LogItem(jsonArr.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(null, "JSON EXCEPTOPINNNN");
        }
        Log.e(null, "List length: " + list.size());
        return list;
    }


    public List<LogItem> buildDescendingList() {
        List<LogItem> lst = buildList();
        Collections.sort(lst, new LatestFirst());
        return lst;
    }

    public List<LogItem> buildAscescendingList() {
        List<LogItem> lst = buildList();
        Collections.sort(lst, new EarliestFirst());
        return lst;
    }

    public List<LogItem> buildRatingAscendingList() {
        List<LogItem> lst = buildList();
        Collections.sort(lst, new RatingsHigh());
        return lst;
    }

    public List<LogItem> buildRatingDescendingList() {
        List<LogItem> lst = buildList();
        Collections.sort(lst, new RatingsLow());
        return lst;
    }


    public void updateList() {

        if (mSortSpinner.getSelectedItemPosition() == 0) {
            mList = buildAscescendingList();
        }
        if (mSortSpinner.getSelectedItemPosition() == 1) {
            mList = buildDescendingList();
        }
        if (mSortSpinner.getSelectedItemPosition() == 2) {
            mList = buildRatingAscendingList();
        }
        if (mSortSpinner.getSelectedItemPosition() == 3) {
            mList = buildRatingDescendingList();
        }
        customAdapter = new MyAdapter(this, R.layout.fragment_log, mList);
        mListView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        mSearchList = mList;
    }


    public String displayDate(long time) {
        String date = "";
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(time);
        date = date + calendar.get(Calendar.DAY_OF_MONTH) + " ";

        switch (calendar.get(Calendar.MONTH)) {
            case 0:
                date = date + "Jan";
                break;
            case 1:
                date = date + "Feb";
                break;
            case 2:
                date = date + "Mar";
                break;
            case 3:
                date = date + "Apr";
                break;
            case 4:
                date = date + "May";
                break;
            case 5:
                date = date + "Jun";
                break;
            case 6:
                date = date + "Jul";
                break;
            case 7:
                date = date + "Aug";
                break;
            case 8:
                date = date + "Sep";
                break;
            case 9:
                date = date + "Oct";
                break;
            case 10:
                date = date + "Nov";
                break;
            case 11:
                date = date + "Dec";
                break;
        }
        date = date + " " + calendar.get(Calendar.YEAR);
        return date;

    }

    public void save() {
        JSONArray jsonArr;
        SharedPreferences.Editor editor = settings.edit();
        try {
            jsonArr = new JSONArray();
            for (int n = 0; n < mList.size(); n++) {
                jsonArr.put(mList.get(n).toJSON(mList.get(n)));
            }
            editor.putString("log_list", jsonArr.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(null, "JSON EXCEPTION ?!?!?!?!!?");
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            customAdapter.search(s);
            customAdapter = new MyAdapter(this, R.layout.fragment_log, mSearchList);
            mListView.setAdapter(customAdapter);
            Log.e(null, "M SEARCH LIST SIZE: " + mSearchList.size());
        } else {
            updateList();
        }
    }


}
