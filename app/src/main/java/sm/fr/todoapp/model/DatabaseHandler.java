package sm.fr.todoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Création de la structure de la base de données
 * et gestion de la connexion
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TASK_TABLE_SQL = "CREATE TABLE tasks(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "task_name TEXT NOT NULL," +
            "done INTEGER NOT NULL)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TASK_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tasks");
        this.onCreate(sqLiteDatabase);
    }
}
