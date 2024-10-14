package org.lucas.furiousmina.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lucas.furiousmina.FuriousMina;
import org.lucas.furiousmina.config.MinaConfig;

public class ReloadPluginCommand implements CommandExecutor {

    private final FuriousMina furiousMina;
    private final MinaConfig minaConfig;

    public ReloadPluginCommand(FuriousMina furiousMina, MinaConfig minaConfig) {
        this.furiousMina = furiousMina;
        this.minaConfig = minaConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.isOp()) {
                minaConfig.reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Arquivo do FuriousMina recarregado.");
            }
        }else{
            minaConfig.reloadConfig();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Arquivo do FuriousMina recarregado.");
        }
        return false;
    }
}
