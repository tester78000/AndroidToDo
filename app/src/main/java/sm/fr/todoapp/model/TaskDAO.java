package sm.fr.todoapp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant d'accès à la table des tâches
 */
public class TaskDAO implements DAOInterface<Task> {

    //Gestionnaire de connexion
    private DatabaseHandler db;

    public TaskDAO(DatabaseHandler db) {
        this.db = db;
    }

    /**
     * Récupération d'une entité Task en fonction de sa clef primaire (id)
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

    /**
     * Hydratation d'une entité Task en fonction des donnée's d'un curseur
     * @param cursor
     * @return
     */
    private Task hydrateTask(Cursor cursor) {
        Task Task = new Task();

        Task.setId(cursor.getLong(0));
        Task.setTaskName(cursor.getString(1));
        Task.setDone(! cursor.getString(2).equals("0"));

        return Task;
    }

    /**
     * Requête sur l'ensemble des tâches en base de données
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
            //Remplissage de la liste
            TaskList.add(this.hydrateTask(cursor));
        }

        //Fermeture du curseur
        cursor.close();

        return TaskList;
    }

    /**
     * Requête sur les tâches en fonction du statut
     * @param done
     * @return List<Task> Liste de tâches
     * @throws SQLiteException
     */
    private List<Task> findAllByDoneStatus(Boolean done) throws SQLiteException{
        //Instanciation de la liste des Tasks
        List<Task> TaskList = new ArrayList<>();

        //Exécution de la requête sql
        String sql = "SELECT id, task_name, done FROM tasks WHERE done=?";
        String[] params = {done?"1":"0"};
        Cursor cursor = this.db.getReadableDatabase().rawQuery(sql, params);
        //Boucle sur le curseur
        while(cursor.moveToNext()){
            TaskList.add(this.hydrateTask(cursor));
        }

        //Fermeture du curseur
        cursor.close();

        return TaskList;
    }


    public List<Task> findAllPendingTasks(){
        return this.findAllByDoneStatus(false);
    }

    public List<Task> findAllDoneTasks(){
        return this.findAllByDoneStatus(true);
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

    /**
     * Persistence d'une entité
     * @param entity
     */
    @Override
    public void persist(Task entity){
        if(entity.getId() == null){
            this.insert(entity);
        } else {
            this.update(entity);
        }
    }

    /**
     * Constitution d'un tableau de colonnes / valeurs
     * pour l'insertion ou la mise à jour de la table tasks
     * @param entity
     * @return
     */
    private ContentValues getContentValuesFromEntity(Task entity){
        ContentValues values = new ContentValues();
        values.put("task_name", entity.getTaskName());
        values.put("done", entity.getDoneAsInteger());

        return values;
    }

    /**
     * Insertion dans la base de données
     * @param entity
     */
    private void insert(Task entity) {
        Long id = this.db.getWritableDatabase().insert(
                "tasks", null,
                this.getContentValuesFromEntity(entity)
        );
        entity.setId(id);
    }

    /**
     * Mise à jour d'une ligne de la table tasks
     * @param entity
     */
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
