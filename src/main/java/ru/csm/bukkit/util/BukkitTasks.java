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
        new BukkitRunnable(){
            public void run(){
                runnable.run();
            }
        }.runTask(plugin);
    }

    public static void runTaskAsync(Runnable runnable){
        new BukkitRunnable(){
            public void run(){
                runnable.run();
            }
        }.runTaskAsynchronously(plugin);
    }

    public static void runTaskLater(Runnable runnable, long delay){
        new BukkitRunnable(){
            public void run(){
                runnable.run();
            }
        }.runTaskLater(plugin, delay);
    }

    public static void runTaskLaterAsync(Runnable runnable, long delay){
        new BukkitRunnable(){
            public void run(){
                runnable.run();
            }
        }.runTaskLaterAsynchronously(plugin, delay);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long period){
        new BukkitRunnable(){
            public void run(){
                runnable.run();
            }
        }.runTaskTimer(plugin, delay, period);
    }

    public static void runTaskTimerAsync(Runnable runnable, long delay, long period){
        new BukkitRunnable(){
            public void run(){
                runnable.run();
            }
        }.runTaskTimerAsynchronously(plugin, delay, period);
    }

}
