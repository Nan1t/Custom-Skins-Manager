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

package ru.csm.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public final class VelocityTasks {

    private static Object plugin;
    private static ProxyServer server;

    private VelocityTasks(){}

    public static void init(Object pl, ProxyServer s){
        plugin = pl;
        server = s;
    }

    public static ScheduledTask run(Runnable task){
        return server.getScheduler()
                .buildTask(plugin, task)
                .schedule();
    }

    public static ScheduledTask runDelay(Runnable task, long delayMillis){
        return server.getScheduler()
                .buildTask(plugin, task)
                .delay(delayMillis, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public static ScheduledTask runRepeat(Runnable task, long repeatMillis){
        return server.getScheduler()
                .buildTask(plugin, task)
                .repeat(repeatMillis, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public static ScheduledTask runDelayRepeat(Runnable task, long delayMillis, long repeatMillis){
        return server.getScheduler()
                .buildTask(plugin, task)
                .delay(delayMillis, TimeUnit.MILLISECONDS)
                .repeat(repeatMillis, TimeUnit.MILLISECONDS)
                .schedule();
    }
}
