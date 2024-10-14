package org.lucas.furiousmina.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lucas.furiousmina.FuriousMina;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SellCommand implements CommandExecutor {
    private Economy economy;
    private final FuriousMina furiousMina;

    public SellCommand(FuriousMina furiousMina) {
        this.economy = furiousMina.getEconomy();
        this.furiousMina = furiousMina;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(!furiousMina.getPlayerInDelay().get(player.getUniqueId())){
                furiousMina.getPlayerInDelay().replace(player.getUniqueId(), true);
                AtomicReference<Double> precoInventario = new AtomicReference<>(0.0);

                Arrays.asList(player.getInventory().getContents()).forEach(itemStack -> {
                    if(itemStack != null && !itemStack.getType().equals(Material.DIAMOND_PICKAXE)){
                        switch (itemStack.getType()) {
                            case IRON_INGOT:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() * furiousMina.getMinaConfig().getPrecoMinerios().get("IRON_INGOT").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                            case DIAMOND:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() *  furiousMina.getMinaConfig().getPrecoMinerios().get("DIAMOND").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                            case REDSTONE:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() *  furiousMina.getMinaConfig().getPrecoMinerios().get("REDSTONE").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                            case GOLD_INGOT:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() *  furiousMina.getMinaConfig().getPrecoMinerios().get("GOLD_INGOT").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                            case EMERALD:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() *  furiousMina.getMinaConfig().getPrecoMinerios().get("EMERALD").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                            case COAL:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() *  furiousMina.getMinaConfig().getPrecoMinerios().get("COAL").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                            case INK_SACK:
                                precoInventario.updateAndGet(v -> v + itemStack.getAmount() *  furiousMina.getMinaConfig().getPrecoMinerios().get("LAPIS_LAZULI").doubleValue());
                                player.getInventory().remove(itemStack);
                                break;
                        }
                    }
                });

                if(precoInventario.get() != 0) player.sendMessage(ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getMensagens().get("vendaMinerios").replace("$valor", precoInventario.get().toString())));
                else player.sendMessage(ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getMensagens().get("semMineriosParaVender")));
                furiousMina.getPlayerCoins().replace(player.getUniqueId(), furiousMina.getPlayerCoins().get(player.getUniqueId()).doubleValue() + precoInventario.get());
                economy.depositPlayer(player, precoInventario.get());

                Bukkit.getScheduler().runTaskLater(furiousMina, ()-> {
                    furiousMina.getPlayerInDelay().replace(player.getUniqueId(), false);
                }, 20L * furiousMina.getMinaConfig().getDelay());
            }else{
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',furiousMina.getMinaConfig().getMensagens().get("mensagemDelay")));
            }
        }
        return false;
    }
}
