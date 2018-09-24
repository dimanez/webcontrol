package net.webcontrol.app.siteparserfinal.databases;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * Created by Дима on 26.03.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //Имя базы данных
    private static final String DATABASE_NAME = "wc.database";
    //Версия бд
    private static final int DATABASE_VERSION = 7;
    //Имя таблицы
    private static final String DATABASE_TABLE = "site_result";
    //Название столбцов
    public static final String SITE_NAME    = "site_name";
    public static final String SITE_REQUEST = "request";
    public static final String SITE_RESULT  = "result";
    public static final String DATE         = "date";
    public static final String URL          = "url";
    public static final String S_ID         = "search_id";
    public static final String I_KEY        = "i_key";

    //Константа создвния БД. для удобства(реализуется в onCreated)
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + SITE_NAME
            + " text not null, " + SITE_REQUEST + " text not null, " + SITE_RESULT
            + " integer not null, " + DATE + " datetime, " + URL + " text not null, " + S_ID + " text not null, " + I_KEY + " text not null);";

    public DatabaseHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        // Создаём новую таблицу
        onCreate(db);
    }
    public void dropDataBase(SQLiteDatabase db){
        db.delete(DATABASE_TABLE,null, null);

    }
}
