package fr.cobaltians.cobalt.fragments;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class HTMLDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

	public static final String ARG_YEAR = "YEAR";
	public static final String ARG_MONTH = "MONTH";
	public static final String ARG_DAY = "DAY";
	public static final String ARG_CALLBACK_ID = "ARG_CALLBACK_ID";
	
    final Calendar cal = Calendar.getInstance();
    private HTMLFragment mListener;
    private String mCallbackId;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			int year = args.getInt(ARG_YEAR);
			int month = args.getInt(ARG_MONTH);
			int day = args.getInt(ARG_DAY);
			mCallbackId = args.getString(ARG_CALLBACK_ID);
			
			if (year != 0
				&& month >= 0
				&& day != 0) {
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
			}
		}

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, 
        							cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public void setListener(HTMLFragment listener) {
		mListener = listener;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
    	cal.set(Calendar.YEAR, year);
    	cal.set(Calendar.MONTH, month);
    	cal.set(Calendar.DAY_OF_MONTH, day);
		
		if (mListener != null) {
			mListener.sendDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), mCallbackId);
		}
	}

}
