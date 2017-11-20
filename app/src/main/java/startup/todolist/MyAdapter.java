package startup.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.support.v4.app.ActivityCompat.*;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

import android.database.Cursor;
import android.database.DataSetObserver;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {

    private ArrayList<ToDoItem> mItems = new ArrayList<ToDoItem>();
    private ArrayList<ToDoItem> orig;
    private Context  mContext;
    private Cursor mCursor;
    //Create database object
    DatabaseOpenHelper myDB;
    SQLiteDatabase db;
    Date mDateofCreation;



    private Activity mActivity;
    int currentItemPosition = 0;

    // Constructor using context
    public MyAdapter(Context context) {
        mContext = context;
    }

    // Constructor using DataSet Context
//    public MyAdapter(ArrayList<ToDoItem>  myDataset, Context context) {
//        this.mItems = myDataset;
//        this.mContext = context;
//    }

    // Constructor using Context + cursor
    public MyAdapter(ArrayList<ToDoItem>  myDataset, Context context) {
        this.mItems = getTasksFromDB(context, myDataset);
        this.mContext = context;//
    }

    private ArrayList<ToDoItem> getTasksFromDB(Context context, ArrayList<ToDoItem>  myDataset) {

        ArrayList<ToDoItem> mTasks = new ArrayList<ToDoItem>();
        myDB = new DatabaseOpenHelper(context);
        db = myDB.getWritableDatabase();
        //query all tasks from DB
        mCursor = db.query(DatabaseOpenHelper.TABLE_NAME, null, null, null, null, null, null);
        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()){
            ToDoItem mItem = new ToDoItem();
            mItem.setTitle(mCursor.getString(mCursor.getColumnIndex(DatabaseOpenHelper.TITLE)));
            mItem.setDescription(mCursor.getString(mCursor.getColumnIndex(DatabaseOpenHelper.DESCRIPTION)));
            //status 0-false 1-true
            mItem.setStatus(mCursor.getInt(mCursor.getColumnIndex(DatabaseOpenHelper.STATUS))>0);
            //getting and parsing date of creation from database
            String DateOfCreation = mCursor.getString(mCursor.getColumnIndex(DatabaseOpenHelper.DATECREATION));
            mItem.setDateOfCreation(ConvertStringToDate(DateOfCreation));
            String DeadlineDate = mCursor.getString(mCursor.getColumnIndex(DatabaseOpenHelper.DATEDEADLINE));
            mItem.setDeadLineDate(ConvertStringToDate(DeadlineDate));
            mTasks.add(mItem);
            mCursor.moveToNext();
        }
        mCursor.close();
        db.close();
        return mTasks;
    }

    private Date ConvertStringToDate(String DateOfCreation) {
        try {
            mDateofCreation = ToDoItem.FORMAT.parse(DateOfCreation);
        } catch (ParseException e) {
            mDateofCreation = new Date();
        }
        return mDateofCreation;
    }

    //Setup filter on items. Filtering by item title.
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<ToDoItem> results = new ArrayList<ToDoItem>();
                if (orig == null)
                    orig = mItems;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final ToDoItem g : orig) {
                            if (g.getTitle().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems = (ArrayList<ToDoItem>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder   {
        // each data item is just a string in this case
        public TextView mTitle;
        public CheckBox mStatus;
        public TextView mDateView;
        public CardView mCardView;

        public ViewHolder(View v) {
            super(v);
            mTitle    = (TextView) v.findViewById(R.id.titleView);
            mStatus   = (CheckBox) v.findViewById(R.id.statusCheckBox);
            mDateView = (TextView) v.findViewById(R.id.dateView);
            mCardView = (CardView) v.findViewById(R.id.CardView);

        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //get current ToDoItem


        //notifyDataSetChanged();
        final ToDoItem toDoItem = mItems.get(position);

        //set Title rom current ToDoItem
        holder.mTitle.setText((CharSequence) toDoItem.getTitle());

        //reset the previous listener
        holder.mStatus.setOnCheckedChangeListener(null);
        //get current Status
        holder.mStatus.setChecked(toDoItem.getStatus());
        if(toDoItem.getStatus()){
            holder.mCardView.setBackgroundColor(Color.parseColor("#B5B4B4"));
        }
        else{
            holder.mCardView.setBackgroundColor(Color.parseColor("#DCEDC8"));
        }

        //get current Deadline Date
        holder.mDateView.setText(ToDoItem.FORMAT1.format(toDoItem.getDeadLineDate()));

        //Set checker listener. Change color of task depending on task status
        holder.mStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked ){

                    toDoItem.setStatus(true);
                    holder.mCardView.setBackgroundColor(Color.parseColor("#B5B4B4"));

                }
                else {

                    toDoItem.setStatus(false);
                    holder.mCardView.setBackgroundColor(Color.parseColor("#DCEDC8"));

                }

                order(mItems);
                notifyDataSetChanged();
            }

        });
        //set title listener
        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create an Intent to create new Task
                Intent mIntent = new Intent(mContext, EditToDoActivity.class);
                ToDoItem.packageIntent(mIntent, toDoItem.getTitle(), toDoItem.getDescription(),
                        toDoItem.getStatus(), toDoItem.getStringDeadLineDate());
                mIntent.putExtra(ToDoItem.class.getCanonicalName(), toDoItem);
                int currentItemPosition = mItems.indexOf(toDoItem);
                mIntent.putExtra(ToDoItem.POSITION, currentItemPosition);
                //mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //post toDoItem for edit activity
                EventBus.getDefault().post(toDoItem);
                ((AppCompatActivity)mContext).startActivityForResult(mIntent, ToDoMain.EDIT_TODO_ITEM_REQUEST);


            }
        });

    }

    // Return the size of dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // Add a ToDoItem to the adapter and DATABASE
    // Order items in array list
    // Notify observers that the data set has changed

    public void add(ToDoItem item) {

        myDB = new DatabaseOpenHelper(mContext);
        db = myDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.TITLE,       item.getTitle());
        values.put(DatabaseOpenHelper.DESCRIPTION, item.getDescription());
        values.put(DatabaseOpenHelper.STATUS, (item.getStatus()?1:0));
        values.put(DatabaseOpenHelper.DATECREATION, GetStringDate(item.getDateOfCreation()));
        values.put(DatabaseOpenHelper.DATEDEADLINE, GetStringDate(item.getDeadLineDate()));
        db.insert(DatabaseOpenHelper.TABLE_NAME, null, values);
        db.close();

        mItems.add(item);
        order(mItems);
        notifyDataSetChanged();
    }

    private String GetStringDate(Date DateCreation){
        Calendar c = Calendar.getInstance();
        c.setTime(DateCreation);
        return setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH))
                + " " + setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.MILLISECOND));

    }

    private static String setDateString(int year, int monthOfYear, int dayOfMonth) {

        // Increment monthOfYear for Calendar/Date -> Time Format setting
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        return (year + "-" + mon + "-" + day);
    }

    private static String setTimeString(int hourOfDay, int minute, int mili) {
        String hour = "" + hourOfDay;
        String min = "" + minute;

        if (hourOfDay < 10)
            hour = "0" + hourOfDay;
        if (minute < 10)
            min = "0" + minute;

        return (hour + ":" + min);
    }


    // Clears the list adapter of all items.
    public void clear() {

        myDB = new DatabaseOpenHelper(mContext);
        db = myDB.getWritableDatabase();
        db.delete(DatabaseOpenHelper.TABLE_NAME, null, null);
        db.close();

        mItems.clear();
        notifyDataSetChanged();

    }

    public void remove(ToDoItem item){
        //delete item from DB
        myDB = new DatabaseOpenHelper(mContext);
        db = myDB.getWritableDatabase();

//        try
//        {
            db.delete(DatabaseOpenHelper.TABLE_NAME, DatabaseOpenHelper.TITLE + "=" +"'"+ item.getTitle() +"'", null);
//        }
//        catch(Exception e)
//        {
//            messageBox("doStuff", e.getMessage());
//        }

        db.close();

        mItems.remove(item);
        notifyDataSetChanged();
    }

    //*********************************************************
    //generic dialog, takes in the method name and error message
    //*********************************************************
    private void messageBox(String method, String message)
    {
        Log.d("EXCEPTION: " + method,  message);

        AlertDialog.Builder messageBox = new AlertDialog.Builder(mContext);
        messageBox.setTitle(method);
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

    //modifying sqlite database item
    public void modifyItem(ToDoItem item, String mTitle, String mDescription, Boolean mCompleteStatus, Date mDeadLineDate) {
        myDB = new DatabaseOpenHelper(mContext);
        db = myDB.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseOpenHelper.TITLE, mTitle);
        cv.put(DatabaseOpenHelper.DESCRIPTION, mDescription);
        cv.put(DatabaseOpenHelper.STATUS, mCompleteStatus);
        cv.put(DatabaseOpenHelper.DATEDEADLINE,GetStringDate(mDeadLineDate));
        db.update(DatabaseOpenHelper.TABLE_NAME, cv, DatabaseOpenHelper.TITLE + "=" +"'"+item.getTitle()+"'", null);
        db.close();
        item.setTitle(mTitle);
        item.setDescription(mDescription);
        item.setStatus(mCompleteStatus);
        item.setDeadLineDate(mDeadLineDate);
    }


    // Retrieve Item by position in Array
    public Object getItem(int pos) {

        return mItems.get(pos);

    }

    //Object Comparator ArrayList<ToDoItem> mItems
    public static void order(ArrayList<ToDoItem> Items) {

        Collections.sort(Items, new Comparator() {

            public int compare(Object o1, Object o2) {

                Boolean x1 = ((ToDoItem) o1).getStatus();
                Boolean x2 = ((ToDoItem) o2).getStatus();
                int sComp = x1.compareTo(x2);

                if (sComp != 0)
                {
                    return sComp;
                }
                else
                {
                    Date x3 = ((ToDoItem) o1).getDeadLineDate();
                    Date x4 = ((ToDoItem) o2).getDeadLineDate();
                    return x3.compareTo(x4);
                }
            }
        });
    }

}

