package ru.csm.api.storage;

import ninja.leaping.modded.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.modded.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.modded.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {

    private final Path file;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;

    /**
     * Create empty .conf file in directory, or load if file already exist
     * @param name Name of configuration file with extension .conf
     * @param directory Directory to file create
     * */
    public Configuration(String name, Path directory) throws IOException{
        file = Paths.get(directory.toString() + File.separator + name);
        loader = HoconConfigurationLoader.builder().setPath(file).build();

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        if (!Files.exists(file)) {
            Files.createFile(file);
        }

        load();
    }

    /**
     * Copy .conf file from jar resources if file not exist
     * @param name Name of config file with extension .conf
     * @param directory Directory to copy file
     * @param plugin Plugin instance
     * */
    public Configuration(String name, Path directory, Object plugin) throws IOException{
        String realName = name.split("/")[name.split("/").length-1];

        file = Paths.get(directory.toString() + File.separator + realName);
        loader = HoconConfigurationLoader.builder().setPath(file).build();

        InputStream in = plugin.getClass().getResourceAsStream("/" + name);

        if(!Files.exists(directory)){
            Files.createDirectory(directory);
        }

        if (!Files.exists(file)) {
            Files.copy(in, file);
        }

        load();
    }

    /**
     * Reload config in memory from file
     * */
    public void load(){
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save all changes in file
     * */
    public void save(){
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get root node
     * */
    public CommentedConfigurationNode get(){
        return this.rootNode;
    }

    /**
     * Get file path
     * */
    public Path getFile(){
        return file;
    }
}
