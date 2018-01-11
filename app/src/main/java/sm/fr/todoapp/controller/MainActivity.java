package sm.fr.todoapp.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import sm.fr.todoapp.R;
import sm.fr.todoapp.model.DatabaseHandler;
import sm.fr.todoapp.model.Task;
import sm.fr.todoapp.model.TaskDAO;

public class MainActivity extends AppCompatActivity {

    public static final int TASK_FORM = 1;

    private ListView taskListView;
    private List<Task> taskList;
    private DatabaseHandler db;
    private TaskDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DatabaseHandler(this);
        this.dao = new TaskDAO(this.db);

        taskListView = findViewById(R.id.todoListView);

        initTaskList();

    }

    private void initTaskList(){
        this.taskList = this.dao.findAll();
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
            initTaskList();
        }
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

            //Le tag permet de transmettre une information au gestionnaire d'événement
            checkDone.setTag(position);

            return v;
        }
    }
}
