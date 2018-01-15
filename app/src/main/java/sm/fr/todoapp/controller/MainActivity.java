package sm.fr.todoapp.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sm.fr.todoapp.R;
import sm.fr.todoapp.model.DatabaseHandler;
import sm.fr.todoapp.model.Task;
import sm.fr.todoapp.model.TaskDAO;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final int TASK_FORM = 1;

    private ListView taskListView;
    private List<Task> taskList;
    private Spinner spinnerStatus;
    private DatabaseHandler db;
    private TaskDAO dao;

    /**
     * Création de l'activité
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DatabaseHandler(this);
        this.dao = new TaskDAO(this.db);

        this.dao.insertTodo(this.db.getWritableDatabase());

        taskListView = findViewById(R.id.todoListView);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        spinnerStatus.setOnItemSelectedListener(this);

        //this.taskList = this.dao.findAll();
        initTaskList();

    }

    /**
     * Initialisation de la liste des tâches
     * dans un composant ListView
     * Les données présentées dépendes de la sélection du statuts
     */
    private void initTaskList(){
        //Récupération du statut séléctionné
        String status = this.spinnerStatus.getSelectedItem().toString();

        //Récupération d'une liste de tâches
        //En fonction du statut
        if(status.equals("Toutes")){
            this.taskList = this.dao.findAll();
        } else if(status.equals("En cours")){
            this.taskList = this.dao.findAllPendingTasks();
        } else {
            this.taskList = this.dao.findAllDoneTasks();
        }

        //Instanciation de l'adapter
        TaskArrayAdapter adapter = new TaskArrayAdapter(
                this
                ,R.layout.list_view_task
                ,this.taskList);
        //Liaison entre l'adapter et la ListView
        this.taskListView.setAdapter(adapter);
    }

    /**
     * Ouverture du formulaire de Création d'une tâche
     * @param v
     */
    public void onNewTask(View v){
        Intent intentNewTask = new Intent(this, TaskFormActivity.class);
        startActivityForResult(intentNewTask, TASK_FORM);
    }

    /**
     * Coche d'une case dans la ListView
     * Met à jour l'entité et persiste celle ci en base de données
     * @param v
     */
    public void onCheck(View v){
        //Récupération de la position taguée
        int position = (int)v.getTag();

        //Récupération de l'état de la case à cocher
        CheckBox check = (CheckBox) v;
        boolean done = check.isChecked();

        //Récupération et modification de l'entité
        Task task = this.taskList.get(position);
        task.setDone(done);

        //Persistence des données
        this.dao.persist(task);
    }

    /**
     * Retour du formulaire de création de tâche
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TASK_FORM && resultCode == RESULT_OK){
            //this.taskList = this.dao.findAll();
            initTaskList();
        }
    }

    /**
     * Changement de statut
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        initTaskList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * Confirmation de la suppression
     * @param id
     * @return
     */
    private AlertDialog getConfirmDeleteDialog(final Long id){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Voulez-vous vraiment supprimer cette tâche ?");
        //Gestion de la confirmation OK
        dialogBuilder.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dao.deleteOneById(id);
                initTaskList();
                dialog.dismiss();
            }
        });

        //Gestion de la confirmation KO
        dialogBuilder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        return dialogBuilder.create();
    }

    /**
     * Clic sur le bouton supprimer
     * @param v
     */
    public void onDelete(View v){
        //Récupération de la position taguée
        int position = (int) v.getTag();
        Task task = this.taskList.get(position);
        //Affichage de la confirmation
        AlertDialog dialog = getConfirmDeleteDialog(task.getId());
        dialog.show();
    }

    /**
     * ArrayAdapter pour la liste des tâche
     */
    private class TaskArrayAdapter extends ArrayAdapter<Task> {
        List<Task> data;
        int layout;
        Activity context;

        /**
         * Constructeur de l'adapter
         * @param context
         * @param resource
         * @param data
         */
        public TaskArrayAdapter(@NonNull Activity context, int resource, @NonNull List<Task> data) {
            super(context, resource, data);
            this.context = context;
            this.layout = resource;
            this.data = data;

        }

        /**
         * Affichage d'une ligne
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Obtention de la vue
            LayoutInflater inflater = this.context.getLayoutInflater();
            View v = inflater.inflate(this.layout, parent, false);

            //Récupération de l'entité
            Task currentTask = this.data.get(position);

            //Affichage du texte de la tâche
            TextView textView = v.findViewById(R.id.textViewTaskName);
            textView.setText(currentTask.getTaskName());

            //Affichage de la case à cocher
            CheckBox checkDone = v.findViewById(R.id.checkboxTaskDone);
            checkDone.setChecked(currentTask.isDone());

            //Référence au bouton supprimer
            ImageView deleteButton = v.findViewById(R.id.deleteButton);

            //Le tag permet de transmettre une information au gestionnaire d'événement
            deleteButton.setTag(position);
            checkDone.setTag(position);

            return v;
        }
    }
}
