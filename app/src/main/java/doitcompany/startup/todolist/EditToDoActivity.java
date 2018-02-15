package doitcompany.startup.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditToDoActivity extends AppCompatActivity {

    //class variables
    private String   mTitle;
    private String   mDescription;
    private Boolean  mCompleteStatus;
    private Boolean  mActive = true;
    private Date     mDateOfCreation = new Date();
    private Date     mDeadLineDate   = new Date();
    private Boolean  mAlarmMe = false;
    private Intent   mIntent;
    private ToDoItem mItem;
    private Integer  mPosition;

    //activity fields
    private EditText  mEditTitle;
    private EditText  mEditDescription;
    private CheckBox  mCheckBoxStatus;
    private TextView  mDate;
    private TextView  mTime;
    private static String timeString;
    private static String dateString;
    private static TextView dateView;
    private static TextView timeView;

    //for formatting data and time
    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_todo);

        //Set action bar
        Toolbar toolbar = findViewById(R.id.toolbar_edit_todo);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        //get data from intent that started this activity
        mIntent          = getIntent();
        mTitle           = mIntent.getStringExtra(ToDoItem.TITLE);
        mDescription     = mIntent.getStringExtra(ToDoItem.DESCRIPTION);
        mCompleteStatus  = mIntent.getBooleanExtra(ToDoItem.STATUS,false);
        mItem            = mIntent.getParcelableExtra(ToDoItem.class.getCanonicalName());
        mPosition        = mIntent.getIntExtra(ToDoItem.POSITION, 0);

        try {
            mDeadLineDate = FORMAT.parse(mIntent.getStringExtra(ToDoItem.DATE));
        } catch (ParseException e) {
            mDeadLineDate = new Date();
        }

        mEditTitle        = findViewById(R.id.title);
        mEditDescription  = findViewById(R.id.description);
        mCheckBoxStatus   = findViewById(R.id.completeCheckBox);
        dateView         =  findViewById(R.id.date);
        timeView         =  findViewById(R.id.time);

        mEditTitle.setText(mTitle);
        mEditDescription.setText(mDescription);
        mCheckBoxStatus.setChecked(mCompleteStatus);
        if (mDeadLineDate !=null){
            setDate(mDeadLineDate);}

        // OnClickListener for the Date button, calls showDatePickerDialog() to
        // show the Date dialog
        final Button datePickerButton = (Button) findViewById(R.id.date_picker);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // OnClickListener for the Time button, calls showTimePickerDialog() to
        // show the Time Dialog
        final Button timePickerButton = (Button) findViewById(R.id.time_picker);
        timePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // OnClickListener for the Delete Button,
        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent data = new Intent();
                Boolean mDelete = true;
                data.putExtra(ToDoItem.POSITION, mPosition);
                data.putExtra(ToDoItem.DELETE, mDelete);
                //return data Intent and finish
                setResult(RESULT_OK, data);
                finish();
            }
        });

        // OnClickListener for the Cancel Button,
        final Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the current Status
                Boolean status = getStatus();

                Boolean mDelete = false;

                //Get the current ToDoItem Title
                String titleString = getToDoTitle();

                //Get the current ToDoItem Description
                String descriptionString = getToDoDescription();

                // Construct fullDate string to save it in Item
                dateString = dateView.getText().toString();
                timeString = timeView.getText().toString();
                String fullDate = dateString +" "+ timeString;

                // Package ToDoItem data into an Intent
                Intent data = new Intent();
                ToDoItem.packageIntent(data, titleString, descriptionString, status, fullDate);
                data.putExtra(ToDoItem.POSITION, mPosition);
                data.putExtra(ToDoItem.DELETE, mDelete);
                //return data Intent and finish
                setResult(RESULT_OK, data);
                finish();

            }
        });

    }


    private Boolean getStatus() {

        return (mCheckBoxStatus.isChecked()==true);

    }

    private String getToDoTitle() {
        return mEditTitle.getText().toString();
    }

    private String getToDoDescription(){return mEditDescription.getText().toString();}

    public void setDate(Date dateToSet){

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToSet);

        String year    = String.valueOf(cal.get(Calendar.YEAR));
        String mon     = "" + String.valueOf(cal.get(Calendar.MONTH)+1);
        String day     = "" + String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String hour    = "" + String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute  = "" + String.valueOf(cal.get(Calendar.MINUTE));

        if (cal.get(Calendar.MONTH) < 10)
            mon = "0" + mon;
        if (cal.get(Calendar.DAY_OF_MONTH) < 10)
            day = "0" + day;

        String dateString = year + "-" + mon + "-" + day;
        dateView.setText(dateString);

        if (cal.get(Calendar.HOUR_OF_DAY) < 10)
            hour = "0" + hour;
        if (cal.get(Calendar.MINUTE) < 10)
            minute = "0" + minute;

        String timeString = hour + ":" + minute;
        timeView.setText(timeString);
    }

    // DialogFragment used to pick a ToDoItem deadline date
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);

            dateView.setText(dateString);
        }

    }

    // DialogFragment used to pick a ToDoItem deadline time
    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTimeString(hourOfDay, minute, 0);

            timeView.setText(timeString);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {

        // Increment monthOfYear for Calendar/Date -> Time Format setting
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        dateString = year + "-" + mon + "-" + day;
    }

    private static void setTimeString(int hourOfDay, int minute, int mili) {
        String hour = "" + hourOfDay;
        String min = "" + minute;

        if (hourOfDay < 10)
            hour = "0" + hourOfDay;
        if (minute < 10)
            min = "0" + minute;

        timeString = hour + ":" + min;
    }
}
