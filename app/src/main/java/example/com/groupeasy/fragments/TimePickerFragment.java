package example.com.groupeasy.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import example.com.groupeasy.activities.CreateEventActivity;


public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

//    private final TimePicker mTimePicker;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        int hour,min;
        final Calendar c = Calendar.getInstance();
        min = c.get(Calendar.MINUTE);
        hour = c.get(Calendar.HOUR_OF_DAY);

   //     intent.putExtra(hour,min);
        startActivity(intent);

    }

    public void onTimeChanged (TimePicker view, int hourOfDay, int minute){

    }

    public void updateTime(int hourOfDay, int minuteOfHour) {
//        mTimePicker.setCurrentHour(hourOfDay);
//        mTimePicker.setCurrentMinute(minuteOfHour);
    }
}

