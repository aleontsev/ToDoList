package doitcompany.startup.todolist;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import doitcompany.startup.todolist.AddToDoActivity;
import doitcompany.startup.todolist.DatabaseOpenHelper;
import doitcompany.startup.todolist.MyAdapter;
import doitcompany.startup.todolist.ToDoItem;


public class ToDoMain extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private AdView mAdView;

    private RecyclerView               mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext = this;
    private ToDoItem mCurrentItem;
    private int mPosition;
    private Date     mDeadLineDate   = new Date();
    private final String Log_TAG = "LogMe";

    //Item list
    private ArrayList<ToDoItem> mItems = new ArrayList<ToDoItem>();
    public static final int ADD_TODO_ITEM_REQUEST = 0;
    public static final int EDIT_TODO_ITEM_REQUEST = 1;

    //Playing Do It
    MediaPlayer mSound;

    //Create database object
    DatabaseOpenHelper myDB;

    //Create adapter for database query
    private RecyclerView.Adapter databaseAdapter;

    //Creating Main Menu Items+
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.i(Log_TAG,"Enter on onCreateOptionsMenu method");

        getMenuInflater().inflate(R.menu.menu_layout, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        // Configure the search info and add any event listeners
        return super.onCreateOptionsMenu(menu);
    }
    //Creating Main Menu Items-

    @Override
    protected void onPause() {
        Log.i(Log_TAG,"Enter on onPause method");
        super.onPause();
        if (mSound!=null) {
            mSound.release();
        }

        //ad block+
        if (mAdView != null) {
            mAdView.pause();
        }
        //ad block-
    }

    //Actions on selected menu items+
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.motivate_me:

                //play randomly one of two audio files
                Random r = new Random();
                int i1 = r.nextInt(2);
                if (i1==0){
                    mSound = MediaPlayer.create(this,R.raw.test1);
                    mSound.start();
                }
                else{
                    mSound = MediaPlayer.create(this,R.raw.test2);
                    mSound.start();
                }

                return true;

            case R.id.delete_completed_tasks:

                //make dialog
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

                // Add the buttons
                builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    //delete completed tasks
                    public void onClick(DialogInterface dialog, int id) {
                        mItems = mAdapter.getTasksFromDB(mContext);
                        for (int i=0;i<mItems.size();i++){
                            if (mItems.get(i).getStatus()) {
                                mAdapter.remove(mItems.get(i));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        //delete completed tasks
                    }
                });
                builder1.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });

                //set dialog question
                builder1.setMessage(R.string.dialog_message_delete_complete_tasks);

                //create dialog
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                return true;


            case R.id.clear_all_tasks:

                //delete all tasks
                //make dialog
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

                // Add the buttons
                builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    //delete completed tasks
                    public void onClick(DialogInterface dialog, int id) {
                        mAdapter.clear();
                        mAdapter.notifyDataSetChanged();

                    }
                });
                builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });

                //set dialog question
                builder2.setMessage(R.string.dialog_message_delete_all_tasks);

                //create dialog
                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    //Actions on selected menu items-

    //restore mItems+
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mItems  = savedInstanceState.getParcelableArrayList("MineKey");
    }
    //restore mItems-

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Log_TAG,"Enter on onCreate method");

        super.onCreate(savedInstanceState);

        if (savedInstanceState!=null){
            mItems  = savedInstanceState.getParcelableArrayList("MineKey");
        }

        setContentView(R.layout.activity_to_do_main);
        //ad+
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, String.valueOf(R.string.app_ad_id));
        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        //ad-

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.fab);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new MyAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        //Attach listener to addButton
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //create an Intent to create new Task
                Intent intent = new Intent(getApplicationContext(), AddToDoActivity.class);
                startActivityForResult(intent, ADD_TODO_ITEM_REQUEST);
            }
        });

    }

    private Cursor GetTasks() {

        return myDB.getWritableDatabase().query(DatabaseOpenHelper.TABLE_NAME,
                DatabaseOpenHelper.columns, null, new String[] {}, null, null,
                null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check result code and request code
        // if user submitted a new ToDoItem
        // Create a new ToDoItem from the data Intent
        // and then add it to the adapter
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_TODO_ITEM_REQUEST) {

                ToDoItem newItem = new ToDoItem(data);
                mAdapter.add(newItem);

            }
            if (requestCode == EDIT_TODO_ITEM_REQUEST ) {

                //find element and modify it.
                mPosition       = data.getIntExtra(ToDoItem.POSITION, 0);
                mCurrentItem = (ToDoItem)mAdapter.getItem(mPosition);
                Boolean mDelete         = data.getBooleanExtra(ToDoItem.DELETE,false);

                if (mDelete){
                    mAdapter.remove(mCurrentItem);
                }
                else{
                //modify current item
                    String  mTitle          = data.getStringExtra(ToDoItem.TITLE);
                    String  mDescription    = data.getStringExtra(ToDoItem.DESCRIPTION);
                    Boolean mCompleteStatus = data.getBooleanExtra(ToDoItem.STATUS,false);

                    try {
                        mDeadLineDate = ToDoItem.FORMAT.parse(data.getStringExtra(ToDoItem.DATE));
                    } catch (ParseException e) {
                        mDeadLineDate = new Date();
                    }

                    mAdapter.modifyItem(mCurrentItem, mTitle, mDescription, mCompleteStatus, mDeadLineDate);
                }

            }
        }
        mAdapter.order(mItems);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if ( TextUtils.isEmpty ( newText ) ) {
            mAdapter.getFilter().filter("");
        } else {
            mAdapter.getFilter().filter(newText.toString());
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("MineKey", mItems);

    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy(){
        Log.i(Log_TAG,"Enter on onDestroy() method");
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }
}

