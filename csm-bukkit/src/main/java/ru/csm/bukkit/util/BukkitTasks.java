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

package ru.csm.bukkit.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class BukkitTasks {

    private static Plugin plugin;

    private BukkitTasks(){}

    public static void setPlugin(Plugin p){
        plugin = p;
    }

    public static void runTask(Runnable runnable){
        getRunnable(runnable).runTask(plugin);
    }

    public static void runTaskAsync(Runnable runnable){
        getRunnable(runnable).runTaskAsynchronously(plugin);
    }

    public static void runTaskLater(Runnable runnable, long delay){
        getRunnable(runnable).runTaskLater(plugin, delay);
    }

    public static void runTaskLaterAsync(Runnable runnable, long delay){
        getRunnable(runnable).runTaskLaterAsynchronously(plugin, delay);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long period){
        getRunnable(runnable).runTaskTimer(plugin, delay, period);
    }

    public static void runTaskTimerAsync(Runnable runnable, long delay, long period){
        getRunnable(runnable).runTaskTimerAsynchronously(plugin, delay, period);
    }

    private static BukkitRunnable getRunnable(Runnable runnable){
        return new BTask(runnable);
    }

    public static class BTask extends BukkitRunnable {

        private final Runnable runnable;

        public BTask(Runnable runnable){
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }
}
