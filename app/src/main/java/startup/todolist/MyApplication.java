package startup.todolist;

import android.app.Application;
import android.content.Context;

/**
 * Created by Lenovo on 29.05.2016.
 */
public class MyApplication extends Application {
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }

}
