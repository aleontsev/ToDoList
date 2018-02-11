package doitcompany.startup.todolist;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;

//implements Parcelable interface to parcel object using intent
public class ToDoItem implements Parcelable {

    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String STATUS = "status";
    public final static String DATE = "date";
    public final static String POSITION = "position";
    public final static String DELETE = "delete";
    //for formatting data and time
    public final static SimpleDateFormat FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm", Locale.US);
    public final static SimpleDateFormat FORMAT1 = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.US);

    public enum Priority {
        LOW, MED, HIGH
    };

    //fields of ToDoItem
    private String   mTitle;
    private String   mDescription;
    private Boolean  mCompleteStatus;
    private Priority mPriority = Priority.LOW;
    private Boolean  mActive = true;
    private Date     mDateOfCreation = new Date();
    private Date     mDeadLineDate   = new Date();
    private Boolean  mAlarmMe = false;

    //trivial class constructor
    public ToDoItem(){
        super();
    }

    //simple class constructor
    public ToDoItem(String title, String description, Boolean completeStatus, Priority priority,
                    Boolean active, Boolean alarmMe){
        this.mTitle            = title;
        this.mDescription      = description;
        this.mCompleteStatus   = completeStatus;
        this.mPriority         = priority;
        this.mActive           = active;
        this.mAlarmMe          = alarmMe;
    }


    //intent class constructor
    public ToDoItem(Intent intent) {

        mTitle          = intent.getStringExtra(ToDoItem.TITLE);
        mDescription    = intent.getStringExtra(ToDoItem.DESCRIPTION);
        mCompleteStatus = intent.getBooleanExtra(ToDoItem.STATUS,false);


        try {
            mDeadLineDate = ToDoItem.FORMAT.parse(intent.getStringExtra(ToDoItem.DATE));
        } catch (ParseException e) {
            mDeadLineDate = new Date();
        }

    }

    //method to implement parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    //compose object to parcel it
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeValue(mCompleteStatus);
        dest.writeValue(mAlarmMe);
    }

    public static final Parcelable.Creator<ToDoItem> CREATOR = new Parcelable.Creator<ToDoItem>() {
        // распаковываем объект из Parcel
        public ToDoItem createFromParcel(Parcel in) {
            return new ToDoItem(in);
        }

        public ToDoItem[] newArray(int size) {
            return new ToDoItem[size];
        }
    };

    //parcel constructor
    // конструктор, считывающий данные из Parcel
    private ToDoItem(Parcel parcel) {

        mTitle              = parcel.readString();
        mDescription        = parcel.readString();
        mCompleteStatus     = (Boolean) parcel.readValue(null);
        mAlarmMe            = (Boolean) parcel.readValue(null);
    }


    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public boolean getStatus(){
        return mCompleteStatus;
    }

    public void setStatus(Boolean status){
        mCompleteStatus = status;
    }

    public Priority getPriority(){
        return mPriority;
    }

    public void setPriority(Priority priority){
        mPriority = priority;
    }

    public Boolean getActive(){
        return mActive;
    }

    public void setActive(Boolean active){
        mActive = active;
    }

    public Date getDateOfCreation(){
        return mDateOfCreation;
    }

    public void setDateOfCreation(Date DateOfCreation){
        mDateOfCreation = DateOfCreation;
    }

    public String getStringDeadLineDate(){
        return mDeadLineDate.toString();
    }


    public Date getDeadLineDate(){
        return mDeadLineDate;
    }
    public void setDeadLineDate(Date deadLineDate){
        mDeadLineDate = deadLineDate;
    }

    public Boolean getAlarmMe(){
        return mAlarmMe;
    }

   public void setAlarmMe(Boolean alarmMe){
       mAlarmMe = alarmMe;
   }

    // Take a set of String data values and
    // package them for transport in an Intent

    public static void packageIntent(Intent intent, String title, String description,
                                     Boolean status, String date) {

        intent.putExtra(ToDoItem.TITLE, title);
        intent.putExtra(ToDoItem.DESCRIPTION, description);
        intent.putExtra(ToDoItem.STATUS, status);
        intent.putExtra(ToDoItem.DATE, date);

    }
}
