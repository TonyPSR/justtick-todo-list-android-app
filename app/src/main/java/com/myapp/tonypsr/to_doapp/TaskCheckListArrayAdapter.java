package com.myapp.tonypsr.to_doapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TonyPSR on 09-06-2018.
 */

public class TaskCheckListArrayAdapter extends ArrayAdapter<TaskCheckList> {
    private Context mcontext;
    private int mresourse;
    private boolean isTrue = true;
    String task;

    public TaskCheckListArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<TaskCheckList> objects) {
        super(context, resource, objects);
        this.mcontext = context;
        this.mresourse = resource;
    }

    public class myViewHolder{
        TextView taskTextView;
        Button doneButton;
        ImageView imgCheckBox;

        public myViewHolder(View view) {
            this.taskTextView = (TextView)view.findViewById(R.id.taskTextView);
            this.doneButton = (Button)view.findViewById(R.id.doneButton);
            this.imgCheckBox = (ImageView)view.findViewById(R.id.imgCheckBox);
        }
    };

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final String task = getItem(position).getTask().replaceAll("'", "''");


        TaskCheckList taskCheckList = new TaskCheckList(task);

        myViewHolder holder = null;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(mresourse, parent, false);
            holder = new myViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (myViewHolder)convertView.getTag();
        }


        holder.doneButton.setAlpha(0);


        //fonts
        AssetManager getAssets = mcontext.getAssets();
        Typeface productSans = Typeface.createFromAsset(getAssets, "fonts/ProductSansRegular.ttf");


        holder.taskTextView.setText(task.replaceAll("''", "'"));
        holder.taskTextView.setTypeface(productSans);


        //assigning x and L
        ToDoDatabaseHelper taskDatabaseHelper = new ToDoDatabaseHelper(getContext(), "taskDataBase.db");
        SQLiteDatabase taskDb = taskDatabaseHelper.getWritableDatabase();

        String itemClicked = task;


        String[] columns = {taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''"),
                taskDatabaseHelper.getTABLE_DATE(),
                taskDatabaseHelper.getTABLE_ID(),
                taskDatabaseHelper.getTABLE_STATUS(),
        };


        //accessing the selected record
        Cursor cursor= taskDb.query(taskDatabaseHelper.getTABLE_NAME(), columns, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            String current_task = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())).replaceAll("'", "''");
            String current_date = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE()));
            String current_status = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_STATUS()));

            if(current_task.equals(itemClicked)){
                if(current_status.equals("DONE")){
                    holder.doneButton.setText("L");
                    holder.doneButton.setTextColor(holder.doneButton.getSolidColor());
                    holder.imgCheckBox.setImageResource(R.drawable.ic_check_box_black_24dp);
                    holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    holder.doneButton.setText("X");
                    holder.doneButton.setTextColor(holder.doneButton.getSolidColor());
                    holder.imgCheckBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                    holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                break;
            }
            cursor.moveToNext();
        }


        cursor.close();


        final myViewHolder finalViewHolder = holder;
        holder.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ToDoDatabaseHelper taskDatabaseHelper = new ToDoDatabaseHelper(getContext(), "taskDataBase.db");
                SQLiteDatabase taskDb = taskDatabaseHelper.getWritableDatabase();

                String itemClicked = task;
                String buttonStatus = "L";


                String[] columns = {taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''"),
                        taskDatabaseHelper.getTABLE_DATE(),
                        taskDatabaseHelper.getTABLE_ID(),
                        taskDatabaseHelper.getTABLE_STATUS(),
                };


                //accessing the selected record
                Cursor cursor= taskDb.query(taskDatabaseHelper.getTABLE_NAME(), columns, null, null, null, null, null);
                cursor.moveToFirst();

                while(!cursor.isAfterLast()){
                    String current_task = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_TASK())).replaceAll("'","''");
                    String current_date = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_DATE()));
                    String current_status = cursor.getString(cursor.getColumnIndex(taskDatabaseHelper.getTABLE_STATUS()));

                    if(current_task.equals(itemClicked)){
                        if(current_status.equals("DONE")){
                            finalViewHolder.doneButton.setText("L");
                            finalViewHolder.doneButton.setTextColor(finalViewHolder.doneButton.getSolidColor());
                            finalViewHolder.imgCheckBox.setImageResource(R.drawable.ic_check_box_black_24dp);
                            finalViewHolder.taskTextView.setPaintFlags(finalViewHolder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }else {
                            finalViewHolder.doneButton.setText("X");
                            finalViewHolder.doneButton.setTextColor(finalViewHolder.doneButton.getSolidColor());
                            finalViewHolder.imgCheckBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                            finalViewHolder.taskTextView.setPaintFlags(finalViewHolder.taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                        break;
                    }
                    cursor.moveToNext();
                }


                cursor.close();


                if(finalViewHolder.doneButton.getText().equals("X")){
                    finalViewHolder.doneButton.setText("L");
                    finalViewHolder.doneButton.setTextColor(finalViewHolder.doneButton.getSolidColor());
                    finalViewHolder.imgCheckBox.setImageResource(R.drawable.ic_check_box_black_24dp);
                    finalViewHolder.taskTextView.setPaintFlags(finalViewHolder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String sql = "UPDATE "+taskDatabaseHelper.getTABLE_NAME() +" SET " +
                            taskDatabaseHelper.getTABLE_STATUS()+ " = 'DONE' WHERE "
                            +taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''")+ " = '"+itemClicked+"'";
                    taskDb.execSQL(sql);
                }else {
                    finalViewHolder.doneButton.setText("X");
                    finalViewHolder.doneButton.setTextColor(finalViewHolder.doneButton.getSolidColor());
                    finalViewHolder.imgCheckBox.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                    finalViewHolder.taskTextView.setPaintFlags(finalViewHolder.taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    String sql = "UPDATE "+taskDatabaseHelper.getTABLE_NAME() +" SET " +
                            taskDatabaseHelper.getTABLE_STATUS()+ " = 'NOT DONE' WHERE "
                            +taskDatabaseHelper.getTABLE_TASK().replaceAll("'","''")+ " = '"+itemClicked+"'";
                    taskDb.execSQL(sql);
                }
            }
        });


        return convertView;
    }
}
