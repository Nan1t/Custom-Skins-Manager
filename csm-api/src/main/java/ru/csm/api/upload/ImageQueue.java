/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.upload;

import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ImageQueue implements Runnable {

    private final Queue<Request> queue = new ConcurrentLinkedQueue<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> task;
    private int period;

    protected Optional<Request> pop(){
        return Optional.ofNullable(queue.poll());
    }

    public int getSize(){
        return queue.size();
    }

    public int getWaitSeconds(){
        return queue.size() * period;
    }

    public void push(SkinPlayer player, String url, SkinModel model){
        queue.offer(new Request(player, url, model));
    }

    public void start(int period){
        this.period = period;
        if (task != null) stop();
        task = executor.scheduleAtFixedRate(this, 0, period, TimeUnit.SECONDS);
    }

    public void stop(){
        task.cancel(true);
    }

    static class Request {

        private final SkinPlayer player;
        private final String url;
        private final SkinModel model;

        public Request(SkinPlayer player, String url, SkinModel model){
            this.player = player;
            this.url = url;
            this.model = model;
        }

        public SkinPlayer getPlayer() {
            return player;
        }

        public String getUrl() {
            return url;
        }

        public SkinModel getModel() {
            return model;
        }
    }
}
