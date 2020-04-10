package com.myapp.tonypsr.to_doapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NewTaskBottomSheetFragment.BottomSheetListener, DeepAnalysisBottomSheetFragment.BottomSheetListener{
    //calendar variables
    Calendar myCurrentDate;
    int day=0,month=0,year=0;
    String current_date;

    ListView myListView;

    @Override
    public void refreshDB() {
        updateListView();
    }

    ToDoDatabaseHelper taskDatabaseHelper;
    SQLiteDatabase taskDb;
    TextView currentDate;
    FloatingActionButton floatingActionButton;

    ArrayList<String> taskArrayList = new ArrayList<>();
    ArrayList<String> dayArrayList = new ArrayList<>();

    //custom adapter
    TaskCheckListArrayAdapter taskCheckListArrayAdapter;
    TaskCheckList taskCheckList;
    ArrayList<TaskCheckList> taskCheckListArrayList;

    static boolean isTodayView = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Today");



        updateListView();


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);

                DeepAnalysisBottomSheetFragment bottomSheetDialog = new DeepAnalysisBottomSheetFragment();

                TaskCheckList taskCheckListTemp = taskCheckListArrayList.get(i);

                //sending date
                Bundle bundle = new Bundle();
                bundle.putString("current_date", current_date);
                bundle.putString("itemClickedLabel", taskCheckListTemp.getTask());
                bottomSheetDialog.setArguments(bundle);

                bottomSheetDialog.show(getSupportFragmentManager(), "deepanalysisfragment");
            }
        });

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NewTaskBottomSheetFragment bottomSheetDialog = new NewTaskBottomSheetFragment();
                bottomSheetDialog.show(getSupportFragmentManager(), "newTaskBottomSheet");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.toString().equals("About")){
            Intent aboutIntent = new Intent(getApplicationContext(), activity_about.class);
            startActivity(aboutIntent);
        }else {
            isTodayView = !isTodayView;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isTodayView){
            finish();
        }else{
            Intent tempIntent = new Intent(getApplicationContext(), MainActivity.class);
            isTodayView = !isTodayView;
            finish();
            startActivity(tempIntent);
        }
        return;
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }



    public void updateListView(){
        myListView = (ListView) findViewById(R.id.myListView);
        currentDate = (TextView) findViewById(R.id.currentDate);

        Typeface productSans = Typeface.createFromAsset(getAssets(), "fonts/ProductSansRegular.ttf");
        Typeface productSansBold = Typeface.createFromAsset(getAssets(), "fonts/ProductSansBold.ttf");
        Typeface productSansItalic = Typeface.createFromAsset(getAssets(), "fonts/ProductSansItalic.ttf");
        Typeface productSansBoldItalic = Typeface.createFromAsset(getAssets(), "fonts/ProductSansBoldItalic.ttf");
        currentDate.setTypeface(productSansBold);


        //getting current date
        myCurrentDate = Calendar.getInstance();
        day = myCurrentDate.get(Calendar.DATE);
        month = myCurrentDate.get(Calendar.MONTH) + 1;
        year = myCurrentDate.get(Calendar.YEAR);
        current_date = DateFormat.getDateInstance().format(myCurrentDate.getTime());

        taskDatabaseHelper = new ToDoDatabaseHelper(getApplicationContext(), "taskDataBase.db");
        taskDb = taskDatabaseHelper.getReadableDatabase();

        String[] columns = {taskDatabaseHelper.getTABLE_TASK(),
                taskDatabaseHelper.getTABLE_DATE(),
                taskDatabaseHelper.getTABLE_ID(),
                taskDatabaseHelper.getTABLE_STATUS()
        };

        Cursor cursor = taskDb.query(taskDatabaseHelper.getTABLE_NAME(), columns, null, null, null, null, null);
        cursor.moveToFirst();

        taskCheckListArrayList = new ArrayList<>();


        String recordStatus;
        String recordDate;

        if (isTodayView) {
            setTitle("Today");

            //setting the current date
            currentDate.setText(current_date);

            while (!cursor.isAfterLast()) {
                recordStatus = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_STATUS()));
                recordDate = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE()));
                if ((current_date.equals(recordDate) && recordStatus.equals("NOT DONE"))) {
                    taskArrayList.add(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())));
                    dayArrayList.add(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE())));
                    taskCheckList = new TaskCheckList(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())));
                    taskCheckListArrayList.add(0, taskCheckList);
                }else if ((current_date.equals(recordDate) && recordStatus.equals("DONE"))) {
                    taskArrayList.add(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())));
                    dayArrayList.add(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE())));
                    taskCheckList = new TaskCheckList(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())));
                    taskCheckListArrayList.add(taskCheckList);
                }
                cursor.moveToNext();
            }

            cursor.moveToFirst();
        } else {
            setTitle("All Tasks");
            currentDate.setText("Dream Big.");
            while (!cursor.isAfterLast()) {
                taskArrayList.add(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())));
                dayArrayList.add(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE())));
                taskCheckList = new TaskCheckList(cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())));
                taskCheckListArrayList.add(taskCheckList);
                cursor.moveToNext();
            }
        }


        cursor.close();


        taskCheckListArrayAdapter = new TaskCheckListArrayAdapter(this, R.layout.adapter_view_layout, taskCheckListArrayList);
        myListView.setAdapter(taskCheckListArrayAdapter);
    }

}




