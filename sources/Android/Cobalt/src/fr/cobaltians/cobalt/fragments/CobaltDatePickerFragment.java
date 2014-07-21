/**
 *
 * CobaltDatePickerFragment
 * Cobalt
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Cobaltians
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fr.cobaltians.cobalt.fragments;

import fr.cobaltians.cobalt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import java.util.Calendar;

public class CobaltDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

	public static final String ARG_YEAR = "YEAR";
	public static final String ARG_MONTH = "MONTH";
	public static final String ARG_DAY = "DAY";
	public static final String ARG_CALLBACK_ID = "ARG_CALLBACK_ID";
	public static final String ARG_TITLE = "TITLE";
	public static final String ARG_DELETE = "DELETE";
	public static final String ARG_CANCEL = "CANCEL";
	public static final String ARG_VALIDATE = "VALIDATE";
	
	protected Context mContext;
    final Calendar cal = Calendar.getInstance();
    private CobaltFragment mListener;
    private String mCallbackId, mDelete, mCancel, mValidate, mTitle;
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mContext = activity;
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			int year = args.getInt(ARG_YEAR);
			int month = args.getInt(ARG_MONTH);
			int day = args.getInt(ARG_DAY);
			mCallbackId = args.getString(ARG_CALLBACK_ID);
			mTitle = args.getString(ARG_TITLE);
			mDelete = args.getString(ARG_DELETE);
			mCancel = args.getString(ARG_CANCEL);
			mValidate = args.getString(ARG_VALIDATE);
			
			if (year != 0
				&& month >= 0
				&& day != 0) {
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
			}
		}
		
		if (mDelete == null && mCancel == null && mValidate == null && mTitle == null) {
	        return new DatePickerDialog(mContext, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		}
		
		else {
			LayoutInflater inflater = (LayoutInflater) ((Activity) mContext).getLayoutInflater();
			final DatePicker datePicker = (DatePicker) inflater.inflate(R.layout.date_picker_cobalt, null);
			AlertDialog.Builder datePickerBuilder = new AlertDialog.Builder(mContext);
			datePickerBuilder.setView(datePicker);

			/*
			// Init the datePicker with mindate under 1900
			//TODO test for under HoneyComb
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.FRANCE);

                Calendar minDate = Calendar.getInstance();
                try {
                    minDate.setTime(formatter.parse("01.01.1800"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                datePicker.setMinDate(minDate.getTimeInMillis());
            }
			else {
                datePicker.init(1800, 01, 01, new OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, day);
                    }
                });
            }*/
			
			 // View settings
	        int year = cal.get(Calendar.YEAR);
	        int month = cal.get(Calendar.MONTH);
	        int day = cal.get(Calendar.DAY_OF_MONTH);
	        
	        if (mTitle != null) {
	        	datePickerBuilder.setTitle(mTitle);
	        }
	        
	        // Buttons
	        datePickerBuilder.setNegativeButton(
	            mDelete, 
	            new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	cal.clear();
	                	if (mListener != null) {
	            			mListener.sendDate(-1, -1, -1, mCallbackId);
	            		}
	                }
	            }
	        );
	        
	        datePickerBuilder.setNeutralButton(
	        	mCancel,
	        	new DialogInterface.OnClickListener() {

	        			@Override
	        			public void onClick(DialogInterface dialog, int arg1) {
	        				dialog.dismiss();
	        			}
	        		});

	        datePickerBuilder.setPositiveButton(
	            mValidate, 
	            new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                    cal.set(
	                        datePicker.getYear(), 
	                        datePicker.getMonth(), 
	                        datePicker.getDayOfMonth()
	                    );
	                    if (mListener != null) {
	            			mListener.sendDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), mCallbackId);
	            		}
	                    dialog.dismiss();
	                }
	            }
	        );
	        final AlertDialog dialog = datePickerBuilder.create();
	        
	        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
				
				@Override
				public void onDateChanged(DatePicker view, int year, int month,
						int day) {
					cal.set(Calendar.YEAR, year);
					cal.set(Calendar.MONTH, month);
					cal.set(Calendar.DAY_OF_MONTH, day);
				}
			});

	        return dialog;
		}		
    }

    public void setListener(CobaltFragment listener) {
		mListener = listener;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
    	cal.set(Calendar.YEAR, year);
    	cal.set(Calendar.MONTH, month);
    	cal.set(Calendar.DAY_OF_MONTH, day);
		
		if (mListener != null) {
			mListener.sendDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), mCallbackId);
		}
	}

}
