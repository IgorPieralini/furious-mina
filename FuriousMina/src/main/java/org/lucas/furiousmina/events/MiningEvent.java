package org.lucas.furiousmina.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.lucas.furiousmina.FuriousMina;
import org.lucas.furiousmina.Utils.Utils;
import org.lucas.furiousmina.model.CustomFortune;
import org.lucas.furiousmina.model.CustomPlayer;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import static org.lucas.furiousmina.Utils.Utils.addFortuneEnchantment;
import static org.lucas.furiousmina.Utils.Utils.getCustomFortune;

public class MiningEvent implements Listener {

    private final FuriousMina furiousMina;

    public MiningEvent(FuriousMina furiousMina) {
        this.furiousMina = furiousMina;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) throws SQLException {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
            if ((event.getBlock().getType().equals(Material.STONE)
                    || event.getBlock().getType().name().equals("DIORITE")
                    || event.getBlock().getType().name().equals("ANDESITE")
                    || event.getBlock().getType().name().equals("GRANITE"))
                    && event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE)) {

                CustomPlayer databasePlayer = furiousMina.getDb().getPlayer(player.getUniqueId().toString());
                databasePlayer.setBlocosQuebrados(databasePlayer.getBlocosQuebrados() + 1);
                furiousMina.getDb().updatePlayer(databasePlayer);

                event.setCancelled(true);
                Bukkit.getWorld(player.getWorld().getName()).getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);

                Map<String, Number> oreChances = furiousMina.getMinaConfig().getOresChances();
                String oreSelected = Utils.getRandomWeightedElement(oreChances);

                if(player.getLocation().getY() > furiousMina.getMinaConfig().getCamadaDiamante()){
                    while(oreSelected.equals("DIAMOND")){
                        oreSelected = Utils.getRandomWeightedElement(oreChances);
                    }
                }

                if(furiousMina.getMinaConfig().getEmeraldForestHills()){
                    if(!player.getLocation().getBlock().getBiome().equals(Biome.FOREST_HILLS)){
                        while(oreSelected.equals("EMERALD")){
                            oreSelected = Utils.getRandomWeightedElement(oreChances);
                        }
                    }
                };

                ItemStack pickaxe = player.getItemInHand();
                Map<Enchantment, Integer> enchantments = pickaxe.getEnchantments();

                Map<Number, Number> blocosPorFortuna = furiousMina.getMinaConfig().getBlocosPorFortuna();

                ItemMeta pickMeta = pickaxe.getItemMeta();
                for (Map.Entry<Number, Number> entry : blocosPorFortuna.entrySet()) {
                    if (databasePlayer.getBlocosQuebrados() == entry.getValue().intValue()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getMensagens().get("uparFortuna")));
                        addFortuneEnchantment(databasePlayer.getBlocosQuebrados(), pickMeta, blocosPorFortuna);
                        break;
                    }
                }
                pickaxe.setItemMeta(pickMeta);
                int fortuneLevel = enchantments.get(Enchantment.LOOT_BONUS_BLOCKS) == null ? 0 : enchantments.get(Enchantment.LOOT_BONUS_BLOCKS);

                int amount = getAmountOfOres(fortuneLevel);
                if (oreSelected.equalsIgnoreCase("lapis_lazuli")) {
                    player.getInventory().addItem(new ItemStack(Material.INK_SACK, amount, (short) 4));
                    return;
                }
                player.getInventory().addItem(new ItemStack(Material.getMaterial(oreSelected), amount));


                if(player.getInventory().firstEmpty() == -1){
                    player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Inventário cheio");
                }

            }else{
                event.setCancelled(true);
                Bukkit.getWorld(player.getWorld().getName()).getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
            }
        }
    }


    private int getAmountOfOres(int fortuneLevel) {
        int multiplier = 1; // Default multiplier
        double chance;
        int baseDrops = 1;


        switch (fortuneLevel) {
            case 1:
                if (new Random().nextDouble() < 1.0 / 3.0) {
                    multiplier = 2;
                }
                break;
            case 2:
                chance = new Random().nextDouble();
                if (chance < 1.0 / 4.0) {
                    multiplier = 2;
                } else if (chance < 2.0 / 4.0) {
                    multiplier = 3;
                }
                break;
            case 3:
                chance = new Random().nextDouble();
                if (chance < 1.0 / 5.0) {
                    multiplier = 2;
                } else if (chance < 2.0 / 5.0) {
                    multiplier = 3;
                } else if (chance < 3.0 / 5.0) {
                    multiplier = 4;
                }
                break;
            case 4:
                chance = new Random().nextDouble();
                if (chance < 1.0 / 6.0) {
                    multiplier = 2;
                } else if (chance < 2.0 / 6.0) {
                    multiplier = 3;
                } else if (chance < 3.0 / 6.0) {
                    multiplier = 4;
                } else if (chance < 4.0 / 6.0) {
                    multiplier = 5;
                }
                break;
            case 5:
                chance = new Random().nextDouble();
                if (chance < 1.0 / 7.0) {
                    multiplier = 2;
                } else if (chance < 2.0 / 7.0) {
                    multiplier = 3;
                } else if (chance < 3.0 / 7.0) {
                    multiplier = 4;
                } else if (chance < 4.0 / 7.0) {
                    multiplier = 5;
                } else if (chance < 5.0 / 7.0) {
                    multiplier = 6;
                }
                break;
            case 6:
                chance = new Random().nextDouble();
                if (chance < 1.0 / 8.0) {
                    multiplier = 2;
                } else if (chance < 2.0 / 8.0) {
                    multiplier = 3;
                } else if (chance < 3.0 / 8.0) {
                    multiplier = 4;
                } else if (chance < 4.0 / 8.0) {
                    multiplier = 5;
                } else if (chance < 5.0 / 8.0) {
                    multiplier = 6;
                } else if (chance < 6.0 / 8.0) {
                    multiplier = 7;
                }
                break;
            case 7:
                chance = new Random().nextDouble();
                if (chance < 1.0 / 9.0) {
                    multiplier = 2;
                } else if (chance < 2.0 / 9.0) {
                    multiplier = 3;
                } else if (chance < 3.0 / 9.0) {
                    multiplier = 4;
                } else if (chance < 4.0 / 9.0) {
                    multiplier = 5;
                } else if (chance < 5.0 / 9.0) {
                    multiplier = 6;
                } else if (chance < 6.0 / 9.0) {
                    multiplier = 7;
                } else if (chance < 7.0 / 9.0) {
                    multiplier = 8;
                }
                break;
            default:
                // No fortune or invalid fortune level, multiplier stays at 1
                break;
        }

        return baseDrops * multiplier;
    }
}
