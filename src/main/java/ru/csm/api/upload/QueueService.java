package ru.csm.api.upload;

import ru.csm.api.upload.entity.SkinRequest;
import java.util.LinkedList;
import java.util.Queue;

public abstract class QueueService {

    private volatile Queue<SkinRequest> requestQueue = new LinkedList<>();

    private long requestPeriod;

    QueueService(long requestPeriod){
        this.requestPeriod = requestPeriod;
    }

    public synchronized long getRequestPeriod(){
        return requestPeriod;
    }

    public synchronized Queue<SkinRequest> getRequestQueue(){
        return requestQueue;
    }

    public long getWaitSeconds(){
        int size = getRequestQueue().size();
        return ((getRequestPeriod()/1000)*size)+3;
    }

    public synchronized void addRequest(SkinRequest request){
        requestQueue.offer(request);
    }

    public void addRequestFirst(SkinRequest request){
        requestQueue.add(request);
    }

    public abstract void start();

}
