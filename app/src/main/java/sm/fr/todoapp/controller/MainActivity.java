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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DatabaseHandler(this);
        this.dao = new TaskDAO(this.db);

        taskListView = findViewById(R.id.todoListView);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        spinnerStatus.setOnItemSelectedListener(this);

        this.taskList = this.dao.findAll();
        initTaskList();

    }

    private void initTaskList(){
        String status = this.spinnerStatus.getSelectedItem().toString();

        if(status.equals("Toutes")){
            this.taskList = this.dao.findAll();
        } else if(status.equals("En cours")){
            this.taskList = this.dao.findAllPendingTasks();
        } else {
            this.taskList = this.dao.findAllDoneTasks();
        }

        TaskArrayAdapter adapter = new TaskArrayAdapter(this,R.layout.list_view_task, this.taskList);
        this.taskListView.setAdapter(adapter);
    }

    public void onNewTask(View v){
        Intent intentNewTask = new Intent(this, TaskFormActivity.class);
        startActivityForResult(intentNewTask, TASK_FORM);
    }

    public void onCheck(View v){
        int position = (int)v.getTag();
        CheckBox check = (CheckBox) v;
        boolean done = check.isChecked();

        Task task = this.taskList.get(position);
        task.setDone(done);

        this.dao.persist(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TASK_FORM && resultCode == RESULT_OK){
            this.taskList = this.dao.findAll();
            initTaskList();
        }
    }

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
        dialogBuilder.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dao.deleteOneById(id);
                initTaskList();
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton("NON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        return dialogBuilder.create();
    }

    public void onDelete(View v){
        int position = (int) v.getTag();
        Task task = this.taskList.get(position);
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

        public TaskArrayAdapter(@NonNull Activity context, int resource, @NonNull List<Task> data) {
            super(context, resource, data);
            this.context = context;
            this.layout = resource;
            this.data = data;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = this.context.getLayoutInflater();
            View v = inflater.inflate(this.layout, parent, false);

            Task currentTask = this.data.get(position);

            TextView textView = v.findViewById(R.id.textViewTaskName);
            textView.setText(currentTask.getTaskName());

            CheckBox checkDone = v.findViewById(R.id.checkboxTaskDone);
            checkDone.setChecked(currentTask.isDone());

            ImageView deleteButton = v.findViewById(R.id.deleteButton);
            deleteButton.setTag(position);

            //Le tag permet de transmettre une information au gestionnaire d'événement
            checkDone.setTag(position);

            return v;
        }
    }
}
