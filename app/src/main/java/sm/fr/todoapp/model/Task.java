package sm.fr.todoapp.model;

/**
 * Entité modèlisant une tâche
 */
public class Task {

    private String taskName;
    private Boolean done = false;
    private Long id;

    public Task() {
    }

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public Task(String taskName, Boolean done) {
        this.taskName = taskName;
        this.done = done;
    }

    public String getTaskName() {
        return taskName;
    }

    public Task setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public Boolean isDone() {
        return done;
    }

    public Task setDone(Boolean done) {
        this.done = done;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Task setId(Long id) {
        this.id = id;
        return this;
    }

    public int getDoneAsInteger(){
        if(this.done){
            return 1;
        } else {
            return 0;
        }
    }
}
