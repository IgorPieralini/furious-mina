package org.lucas.furiousmina.config;

import org.lucas.furiousmina.FuriousMina;

public class DatabaseConfig extends Config{
    public DatabaseConfig(FuriousMina plugin, String fileName) {
        super(plugin, fileName);
    }


    public String getUser(){
        return getCustomConfig().getString("user");
    }
    public String getPassword(){
        return getCustomConfig().getString("password");
    }

    public String getHost(){
        return getCustomConfig().getString("host");
    }

    public int getPort(){
        return getCustomConfig().getInt("port");
    }

    public String getDatabaseName(){
        return getCustomConfig().getString("database");
    }
}
