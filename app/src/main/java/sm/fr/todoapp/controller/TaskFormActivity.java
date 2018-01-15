package sm.fr.todoapp.controller;

import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import sm.fr.todoapp.R;
import sm.fr.todoapp.model.DatabaseHandler;
import sm.fr.todoapp.model.Task;
import sm.fr.todoapp.model.TaskDAO;

public class TaskFormActivity extends AppCompatActivity {

    EditText editTextTaskName;

    /**
     * Création de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_form);

        editTextTaskName = findViewById(R.id.editTextTask);

        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Validation du formulaire
     * @param view
     */
    public void onValidForm(View view) {
        String taskName = this.editTextTaskName.getText().toString();

        if (taskName.trim().equals("")) {
            String message = "La tâche ne peut être vide";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Task task = new Task(taskName);
            this.processForm(task);
        }
    }

    /**
     * Traitement des données du formulaire
     * @param task
     */
    private void processForm(Task task){
        DatabaseHandler db = new DatabaseHandler(this);
        TaskDAO dao = new TaskDAO(db);
        String message;

        try {
            dao.persist(task);
            setResult(RESULT_OK);
            message = "Tâche enregistrée";
        } catch (SQLiteException ex) {
            setResult(RESULT_CANCELED);
            Log.d("DEBUG", ex.getMessage());
            message = "Impossible d'enregistrer la tâche";

        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
