package com.jordan.coffeecalc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class TimerDialogMaker implements DialogInterface.OnClickListener {
	NumberPicker mNumberPicker;
	TimerStartListener mListener;
	public void showTimerDialog(Context context, TimerStartListener listener){
		mListener = listener;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View timerLayout = inflateView(context, R.layout.timer_dialog);
		builder.setView(timerLayout);
		mNumberPicker = (NumberPicker) timerLayout.findViewById(R.id.timer_picker);
		builder.setPositiveButton("Start", this);
		builder.setNegativeButton("Cancel", this);
		builder.setTitle("Choose brew time");
		builder.create().show();
		
	}

	private View inflateView(Context context, int resId){
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(resId, null);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case DialogInterface.BUTTON_POSITIVE:
			mListener.onStart(dialog, mNumberPicker.getValue());
			break;
		default:
			dialog.dismiss();
			break;
		}
		mNumberPicker = null;
		mListener = null;
	}
	
	public static interface TimerStartListener{
		public void onStart(DialogInterface dialog, int value);
	}
	
	
}
