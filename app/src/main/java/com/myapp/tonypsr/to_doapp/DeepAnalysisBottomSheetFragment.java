package com.myapp.tonypsr.to_doapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class DeepAnalysisBottomSheetFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener {
    private BottomSheetListener bottomSheetListener;


    TextView task;
    TextView date;
    RadioGroup doneOrNotRadioGroup;
    RadioButton doneButton, notDoneButtion;
    Button deleteButton, saveButton, snoozeButton;

    Calendar taskCalendar = Calendar.getInstance();

    String current_task;
    String current_date;
    String snooze_date;
    String current_status;

    ToDoDatabaseHelper taskDatabaseHelper;
    SQLiteDatabase taskDb;

    String itemClicked;
    String currentDate;

    //for geting variables from main activity
    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_deep_analysis, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        task = (TextView) v.findViewById(R.id.activityDeepTaskTextView);
        date = (TextView) v.findViewById(R.id.DateTextView);
        deleteButton = (Button)v.findViewById(R.id.cancelButton);
        saveButton = (Button)v.findViewById(R.id.saveButton);
        doneOrNotRadioGroup = (RadioGroup) v.findViewById(R.id.doneOrNotRadioGroup);
        doneButton = (RadioButton) v.findViewById(R.id.doneButton);
        notDoneButtion = (RadioButton) v.findViewById(R.id.notDoneButton);
        snoozeButton = (Button) v.findViewById(R.id.snoozeDate);

        Typeface productSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansRegular.ttf");
        Typeface productSansBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansBold.ttf");
        Typeface productSansItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansItalic.ttf");
        Typeface productSansBoldItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansBoldItalic.ttf");
        date.setTypeface(productSansBold);
        task.setTypeface(productSans);
        saveButton.setTypeface(productSansBold);
        deleteButton.setTypeface(productSansBold);
        doneButton.setTypeface(productSansItalic);
        notDoneButtion.setTypeface(productSansItalic);
        snoozeButton.setTypeface(productSansBold);


        bundle = this.getArguments();

        taskDatabaseHelper = new ToDoDatabaseHelper(getActivity(), "taskDataBase.db");
        taskDb = taskDatabaseHelper.getWritableDatabase();


        itemClicked = bundle.getString("itemClickedLabel","Nothing clicked");
        currentDate = bundle.getString("current_date","no date :-(");

        String[] columns = {taskDatabaseHelper.getTABLE_TASK(),
                taskDatabaseHelper.getTABLE_DATE(),
                taskDatabaseHelper.getTABLE_ID(),
                taskDatabaseHelper.getTABLE_STATUS(),
        };


        //accessing the selected record
        Cursor cursor= taskDb.query(taskDatabaseHelper.getTABLE_NAME(), columns, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            current_task = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK()));
            current_date = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE()));
            snooze_date = current_date;
            current_status = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_STATUS()));

            if(current_task.equals(itemClicked)){
                task.setText(current_task);
                date.setText(current_date);
                if(current_status.equals("DONE")){
                    doneOrNotRadioGroup.check(doneButton.getId());
                }else {
                    doneOrNotRadioGroup.check(notDoneButtion.getId());
                }
                break;
            }
            cursor.moveToNext();
        }


        cursor.close();


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDb.execSQL("DELETE FROM " + taskDatabaseHelper.getTABLE_NAME() + " WHERE " + taskDatabaseHelper.getTABLE_TASK() + "= '" + current_task + "'");
                bottomSheetListener.refreshDB();
                dismiss();
                Toast.makeText(getActivity(), itemClicked+ " is deleted", Toast.LENGTH_SHORT).show();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //online code, get the checked button id from radio group, then use that id to locate radio button checked
                int radioButtonCheckedId = doneOrNotRadioGroup.getCheckedRadioButtonId();
                RadioButton radioButtonChecked = (RadioButton) v.findViewById(radioButtonCheckedId);

                if(!current_date.equals(snooze_date)){
                    String sql = "UPDATE "+taskDatabaseHelper.getTABLE_NAME() +" SET " +
                            taskDatabaseHelper.getTABLE_DATE()+ " = '"+snooze_date+"' WHERE "
                            +taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''")+ " = \""+current_task+"\"";
                    taskDb.execSQL(sql);
                }

                if(radioButtonChecked.getText().equals("DONE")){
                    String sql = "UPDATE "+taskDatabaseHelper.getTABLE_NAME() +" SET " +
                            taskDatabaseHelper.getTABLE_STATUS()+ " = 'DONE' WHERE "
                            +taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''")+ " = \""+current_task+"\"";
                    taskDb.execSQL(sql);
                }else{
                    String sql = "UPDATE "+taskDatabaseHelper.getTABLE_NAME() +" SET " +
                            taskDatabaseHelper.getTABLE_STATUS()+ " = 'NOT DONE' WHERE "
                            +taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''")+ " = \""+current_task+"\"";
                    taskDb.execSQL(sql);
                }
                bottomSheetListener.refreshDB();
                dismiss();
            }
        });

        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(view);
            }
        });



        return v;
    }


    // for datepicker
    public void changeDate(View view){

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setTargetFragment(DeepAnalysisBottomSheetFragment.this, 0);
        datePicker.show(getActivity().getSupportFragmentManager(), "date picker");

    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        taskCalendar = Calendar.getInstance();
        taskCalendar.set(Calendar.YEAR, year);
        taskCalendar.set(Calendar.MONTH, month);
        taskCalendar.set(Calendar.DAY_OF_MONTH, day);

        snooze_date = DateFormat.getDateInstance().format(taskCalendar.getTime());
        date.setText(snooze_date);
    }


    public interface BottomSheetListener {
        void refreshDB();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            bottomSheetListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
