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

package ru.csm.bungee.util;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public final class BungeeTasks {

    private static Plugin plugin;

    private BungeeTasks(){}

    public static void init(Plugin pl){
        plugin = pl;
    }

    public static ScheduledTask runTaskLater(Runnable runnable, long delay){
        return plugin.getProxy().getScheduler()
                .schedule(plugin, runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledTask runRepeatTask(Runnable runnable, long delay, long period){
        return plugin.getProxy().getScheduler()
                .schedule(plugin, runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public static ScheduledTask runAsync(Runnable runnable){
        return plugin.getProxy().getScheduler().runAsync(plugin, runnable);
    }

    public static void cancel(ScheduledTask task){
        plugin.getProxy().getScheduler().cancel(task);
    }

    public static void cancel(int taskId){
        plugin.getProxy().getScheduler().cancel(taskId);
    }

    public static void cancelAll(){
        plugin.getProxy().getScheduler().cancel(plugin);
    }

}
