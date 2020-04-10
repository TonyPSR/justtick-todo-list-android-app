package com.myapp.tonypsr.to_doapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class NewTaskBottomSheetFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener {
    private BottomSheetListener bottomSheetListener;

    Button changeDateButton;
    TextView dateDisplay, toBeRemindedTextView;
    EditText taskEditText;
    Intent mainActivityIntent;
    Calendar taskCalendar = Calendar.getInstance();

    ToDoDatabaseHelper taskDatabaseHelper;
    SQLiteDatabase taskDb;

    String date;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_new_task, container, false);

        taskEditText = (EditText) v.findViewById(R.id.TaskEditText);
        dateDisplay = (TextView) v.findViewById(R.id.dateTextView);
        toBeRemindedTextView = v.findViewById(R.id.toBeRemindedTextView);
        changeDateButton = (Button) v.findViewById(R.id.changeDateButton);

        taskEditText.setFocusableInTouchMode(true);
        taskEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        Typeface productSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansRegular.ttf");
        Typeface productSansBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansBold.ttf");
        Typeface productSansItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProductSansItalic.ttf");

        dateDisplay.setTypeface(productSansBold);
        taskEditText.setTypeface(productSans);
        changeDateButton.setTypeface(productSansItalic);
        toBeRemindedTextView.setTypeface(productSans);


        taskDatabaseHelper = new ToDoDatabaseHelper(getActivity(), "taskDataBase.db");
        taskDb = taskDatabaseHelper.getWritableDatabase();

        mainActivityIntent = new Intent(getActivity(), MainActivity.class);

        date = DateFormat.getDateInstance().format(taskCalendar.getTime());
        dateDisplay.setText(date);


        taskEditText.setOnEditorActionListener(editorListener);


        changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(view);
            }
        });


        return v;
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            switch (i){
                case EditorInfo.IME_ACTION_DONE:
                    if(taskEditText.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(), "Enter a Task!", Toast.LENGTH_SHORT).show();
                    }else if(dateDisplay.toString().equals("")){
                        Toast.makeText(getActivity(), "Select a date", Toast.LENGTH_SHORT).show();
                    }else {
                        writeDataToDatabase();
                        bottomSheetListener.refreshDB();
                        dismiss();
                    }
                    break;
            }
            return false;
        }
    };




    public void writeDataToDatabase(){
        long flag;
        long id = 0;
        String[] column = {"_id"};

        Cursor cursor= taskDb.query(taskDatabaseHelper.getTABLE_NAME(), column, null, null, null, null, null);
        cursor.moveToLast();
        try{
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_ID())));
        } catch (Exception e){
            id = 0;
        }

        cursor.close();

        String task = taskEditText.getText().toString();

        ContentValues values = new ContentValues();
        values.put(taskDatabaseHelper.getTABLE_TASK(),task);
        values.put(taskDatabaseHelper.getTABLE_DATE(), dateDisplay.getText().toString());
        values.put(taskDatabaseHelper.getTABLE_ID(), String.valueOf(id + 1));
        values.put(taskDatabaseHelper.getTABLE_STATUS(), "NOT DONE");

        flag = taskDb.insert(taskDatabaseHelper.getTABLE_NAME(),null,values);

        if(flag != -1){
            Toast.makeText(getActivity(),"Data inserted successfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(),"Data insertion failed",Toast.LENGTH_LONG).show();
        }
    }

    // for datepicker
    public void changeDate(View view){

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setTargetFragment(NewTaskBottomSheetFragment.this, 0);
        datePicker.show(getActivity().getSupportFragmentManager(), "date picker");

    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        taskCalendar = Calendar.getInstance();
        taskCalendar.set(Calendar.YEAR, year);
        taskCalendar.set(Calendar.MONTH, month);
        taskCalendar.set(Calendar.DAY_OF_MONTH, day);

        datePicker.setBackgroundColor(1);

        String newDate = DateFormat.getDateInstance().format(taskCalendar.getTime());
        dateDisplay.setText(newDate);
    }

    public interface BottomSheetListener{
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
