package org.lucas.furiousmina.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.lucas.furiousmina.FuriousMina;
import org.lucas.furiousmina.model.CustomFortune;

import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

public class Utils {
    public static String getRandomWeightedElement(Map<String, Number> objects) {
        double totalWeight = 0.0;
        for (Number weight : objects.values()) {
            totalWeight += weight.doubleValue();
        }
        Random random = new Random();
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;
        for (Map.Entry<String, Number> entry : objects.entrySet()) {
            cumulativeWeight += entry.getValue().doubleValue();
            if (cumulativeWeight >= randomValue) {
                return entry.getKey();
            }
        }
        return "";
    }


    public static void addFortuneEnchantment(int blocosQuebrados, ItemMeta itemMeta, Map<Number, Number> fortuneMap) {
        CustomFortune customFortune = getCustomFortune(blocosQuebrados, fortuneMap);
        itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, customFortune.getFortuneLevel(), true);
    }

    public static CustomFortune getCustomFortune(int blocosQuebrados, Map<Number, Number> fortuneMap) {
        int fortuneLevel = 1;
        int nextRequiredBlocks = Integer.MAX_VALUE;

        for (Map.Entry<Number, Number> entry : fortuneMap.entrySet()) {
            int level = entry.getKey().intValue();
            int requiredBlocks = entry.getValue().intValue();

            if (blocosQuebrados >= requiredBlocks) {
                fortuneLevel = level;
                nextRequiredBlocks = entry.getValue().intValue();
            }

            if ((fortuneLevel < 5 && fortuneLevel >= 1) && blocosQuebrados < fortuneMap.get(fortuneLevel + 1).intValue()) {
                nextRequiredBlocks = fortuneMap.get(fortuneLevel + 1).intValue();
            }

            if (fortuneLevel == 5) {
                nextRequiredBlocks = -1;
            }
        }
        return new CustomFortune(fortuneLevel, nextRequiredBlocks);
    }


    public static void updateScoreboard(Player player, FuriousMina furiousMina) throws SQLException {
        if (player != null) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(furiousMina, () -> {
                try {
                    CustomFortune customFortune = getCustomFortune(furiousMina.getDb().getPlayer(player.getUniqueId().toString()).getBlocosQuebrados(), furiousMina.getMinaConfig().getBlocosPorFortuna());
                    if(player.getScoreboard().getTeam("blocosQuebrados") != null){
                        player.getScoreboard().getTeam("blocosQuebrados").setPrefix(ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getScoreboardText().get("blocos")).replace("$blocosQuebrados", furiousMina.getDb().getPlayer(player.getUniqueId().toString()).getBlocosQuebrados() + "").split("/")[0] + "/ ");
                        player.getScoreboard().getTeam("blocosQuebrados").setSuffix(
                                customFortune.getNextRequiredBlocks() != -1 ?
                                        ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getScoreboardText().get("blocos")).replace("$proximaMeta", customFortune.getNextRequiredBlocks() + "").split("/")[1]
                                        : ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getMensagens().get("upgradeFortunaMaximo")
                        ));
                    }

                    if(player.getScoreboard().getTeam("coinsTeam") != null) player.getScoreboard().getTeam("coinsTeam").setSuffix(ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getScoreboardText().get("coins").split(":")[1]).replace("$coins", furiousMina.getPlayerCoins().get(player.getUniqueId()).toString()));


                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, 20, 20 * 5);
        }
    }
}