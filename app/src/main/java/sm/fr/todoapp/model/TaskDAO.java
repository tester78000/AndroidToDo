package sm.fr.todoapp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO implements DAOInterface<Task> {

    private DatabaseHandler db;

    public TaskDAO(DatabaseHandler db) {
        this.db = db;
    }

    /**
     * Récupération d'un Task en fonction de sa clef primaire (id)
     * @param id
     * @return
     */
    @Override

    public Task findOneById(int id) throws SQLiteException{
        //Exécution de la requête
        String[] params = {String.valueOf(id)};
        String sql = "SELECT id, task_name, done FROM tasks WHERE id=?";
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, params);
        //Instanciation d'un Task
        Task Task = new Task();

        //Hydratation du Task
        if(cursor.moveToNext()){
            Task = hydrateTask(cursor);
        }

        //Fermeture du curseur
        cursor.close();

        return Task;
    }

    private Task hydrateTask(Cursor cursor) {
        Task Task = new Task();

        Task.setId(cursor.getLong(0));
        Task.setTaskName(cursor.getString(1));
        Task.setDone(cursor.getString(2) != "0");

        return Task;
    }

    /**
     *
     * @return List<Task> une liste de Tasks
     */
    @Override
    public List<Task> findAll() throws SQLiteException{
        //Instanciation de la liste des Tasks
        List<Task> TaskList = new ArrayList<>();

        //Exécution de la requête sql
        String sql = "SELECT id, task_name, done FROM tasks";
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, null);
        //Boucle sur le curseur
        while(cursor.moveToNext()){
            TaskList.add(this.hydrateTask(cursor));
        }

        //Fermeture du curseur
        cursor.close();

        return TaskList;
    }

    /**
     * Suppression d'un tâche en fonction de sa clef primaire
     * @param id
     * @throws SQLiteException
     */
    @Override
    public void deleteOneById(Long id) throws SQLiteException{
        String[] params = {id.toString()};
        String sql = "DELETE FROM tasks WHERE id=?";
        this.db.getWritableDatabase().execSQL(sql, params);
    }

    @Override
    public void persist(Task entity){
        if(entity.getId() == null){
            this.insert(entity);
        } else {
            this.update(entity);
        }
    }

    private ContentValues getContentValuesFromEntity(Task entity){
        ContentValues values = new ContentValues();
        values.put("taskName", entity.getTaskName());
        values.put("done", entity.getDoneAsInteger());

        return values;
    }

    private void insert(Task entity) {
        Long id = this.db.getWritableDatabase().insert(
                "tasks", null,
                this.getContentValuesFromEntity(entity)
        );
        entity.setId(id);
    }

    private void update(Task entity){
        String[] params = {entity.getId().toString()};
        this.db.getWritableDatabase().update(
                "tasks",
                this.getContentValuesFromEntity(entity),
                "id=?",
                params
        );
    }
}
