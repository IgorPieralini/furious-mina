package org.lucas.furiousmina;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.lucas.furiousmina.Utils.Database;
import org.lucas.furiousmina.Utils.Utils;
import org.lucas.furiousmina.commands.ReloadPluginCommand;
import org.lucas.furiousmina.commands.SellCommand;
import org.lucas.furiousmina.config.DatabaseConfig;
import org.lucas.furiousmina.config.MinaConfig;
import org.lucas.furiousmina.events.MiningEvent;
import org.lucas.furiousmina.events.OnPlayerJoinEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public final class FuriousMina extends JavaPlugin {
    MinaConfig minaConfig;
    DatabaseConfig databaseConfig;
    Database db;
    private Economy economy;

    private Map<UUID, Number> playerCoins = new HashMap<>();
    private Map<UUID, Boolean> playerInDelay = new HashMap<>();

    @Override
    public void onEnable() {
        minaConfig = new MinaConfig(this, "mina.yml");
        databaseConfig = new DatabaseConfig(this, "database.yml");
        setupDatabase(databaseConfig);
        setupEconomy();
        registerEvent(new MiningEvent(this));
        registerEvent(new OnPlayerJoinEvent(this));
        getCommand("rlmina").setExecutor(new ReloadPluginCommand(this, minaConfig));
        getCommand("vender").setExecutor(new SellCommand(this));
        updateScoreboard();
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[FuriousMina] Plugin ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        minaConfig.reloadConfig();
        databaseConfig.reloadConfig();
        db.disconnect();
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[FuriousMina] desativado com sucesso!");
    }

    public void setupDatabase(DatabaseConfig databaseConfig) {
        db = new Database(
                databaseConfig.getHost(),
                databaseConfig.getPort(),
                databaseConfig.getDatabaseName(),
                databaseConfig.getUser(),
                databaseConfig.getPassword()
        );

        try {
            db.connect();
        } catch (SQLException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[FuriousMina] A conexão com o banco de dados falhou!");
            e.printStackTrace();
        }
    }


    private void registerEvent(Listener event) {
        Bukkit.getPluginManager().registerEvents(event, this);
    }

    private void updateScoreboard() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                try {
                    Utils.updateScoreboard(player, this);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }, 20L, 20L * 5);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
