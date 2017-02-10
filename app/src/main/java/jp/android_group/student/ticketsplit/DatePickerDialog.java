package jp.android_group.student.ticketsplit;

import android.app.Dialog;
import android.app.DialogFragment;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;

/**
 * Created by hiyuki on 2017/02/10.
 */

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(getActivity(), (MainActivity)getActivity(), year, month, dayOfMonth);	//初期化
		return datePickerDialog;
	}

	public void onDateSet(DatePicker view, int year, int month, int day){

	}
}

