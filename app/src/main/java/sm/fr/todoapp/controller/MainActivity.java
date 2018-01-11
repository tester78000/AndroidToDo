package sm.fr.todoapp.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import sm.fr.todoapp.R;

public class MainActivity extends AppCompatActivity {

    public static final int TASK_FORM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onNewTask(View v){
        Intent intentNewTask = new Intent(this, TaskFormActivity.class);
        startActivityForResult(intentNewTask, TASK_FORM);
    }
}
