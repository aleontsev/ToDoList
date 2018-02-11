package doitcompany.startup.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    //database name and database columns
    final static String TABLE_NAME   = "tasks";
    final static String _ID          = "_id";
    final static String TITLE        = "title";
    final static String DESCRIPTION  = "description";
    final static String STATUS       = "status";
    final static String DATECREATION = "date_creation";  //date of creation
    final static String DATEDEADLINE = "date_deadline";  //deadline date

    final static String ALARM       = "alarm";
    //private static final String COMMA_SEP = ",";



    final static String[] columns = {_ID, TITLE, DESCRIPTION, STATUS, DATECREATION, DATEDEADLINE, ALARM};

    //request creating database
    final private static String CREATE_CMD =

            "CREATE TABLE "+ TABLE_NAME+" (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TITLE + " TEXT, "
                    + DESCRIPTION + " TEXT, "
                    + STATUS + " INTEGER, "
                    + DATECREATION + " TEXT, "
                    + DATEDEADLINE + " TEXT, "
                    + ALARM + " INTEGER )";

    //fields for database constructor
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";
    final private Context mContext;

    //database constructor
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    //creating SQL database using request
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     ///
    }

    public void deleteDatabase(){
        mContext.deleteDatabase(DATABASE_NAME);
    }

}
