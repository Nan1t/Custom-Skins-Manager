package ru.csm.api.threads;

public class ThreadWorker {

    private static ThreadPool pool = new ThreadPool(4);

    public static void execute(Runnable task){
        pool.execute(task);
    }

    public static void shutdown(){
        pool.shutdown();
    }

}
