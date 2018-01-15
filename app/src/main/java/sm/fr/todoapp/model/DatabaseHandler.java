package sm.fr.todoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

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

    private Boolean isNew = false;
    private Boolean isUpdated = false;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Création de la base de données si inexistante
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Création de la table
        sqLiteDatabase.execSQL(TASK_TABLE_SQL);
        this.isNew = true;
    }

    /**
     * Mise à jour de la base de données
     * si la version sur le téléphone est inférieure à la version en cours
     * @param sqLiteDatabase
     * @param oldVersionNumber
     * @param newVersionNumber
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersionNumber, int newVersionNumber) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tasks");
        this.onCreate(sqLiteDatabase);
        this.isUpdated = true;
    }

    public Boolean isNew() {
        return isNew;
    }

    public Boolean isUpdated() {
        return isUpdated;
    }
}
