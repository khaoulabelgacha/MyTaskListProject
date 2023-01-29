package com.codurance.training.tasks;

public final class Task {
    public final long id;
    public final String description;
    public boolean done;

    public Task(long id, String description, boolean done) {
        this.id = id;
        this.description = description;
        this.done = done;
    }
}
