package org.lucas.furiousmina.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.lucas.furiousmina.FuriousMina;
import org.lucas.furiousmina.model.CustomFortune;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.lucas.furiousmina.Utils.Utils.addFortuneEnchantment;
import static org.lucas.furiousmina.Utils.Utils.getCustomFortune;

public class OnPlayerJoinEvent implements Listener {

    private final FuriousMina furiousMina;

    public OnPlayerJoinEvent(FuriousMina furiousMina) {
        this.furiousMina = furiousMina;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        furiousMina.getDb().addPlayer(player, 0);
        furiousMina.getPlayerCoins().put(player.getUniqueId(), 0);
        furiousMina.getPlayerInDelay().put(player.getUniqueId(), false);

        setMinaScoreboard(player, furiousMina);
        giveEffects(player);
        givePickaxe(player);
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        e.setCancelled(true);
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        e.setCancelled(true);
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            p.setHealth(20);
        }
    }

    @EventHandler
    public void onDropPickaxe(PlayerDropItemEvent e) throws SQLException {
        Player p = e.getPlayer();
        if (e.getItemDrop().getItemStack().equals(createPickaxe(p))) {
            e.setCancelled(true);
        }
    }

    private void givePickaxe(Player player) throws SQLException {

        ItemStack pickaxe = createPickaxe(player);
        AtomicBoolean hasPickaxe = new AtomicBoolean(false);
        Arrays.stream(player.getInventory().getContents()).forEach(item -> {
            if (item != null) {
                if (item.equals(pickaxe)) hasPickaxe.set(true);
            }

        });
        if (!hasPickaxe.get()) player.getInventory().addItem(pickaxe);
    }

    public ItemStack createPickaxe(Player player) throws SQLException {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta pickMeta = pickaxe.getItemMeta();
        pickMeta.addEnchant(Enchantment.DIG_SPEED, 5, true);
        pickMeta.addEnchant(Enchantment.DURABILITY, 1000, true);

        int blocosQuebradosPlayer = furiousMina.getDb().getPlayer(player.getUniqueId().toString()).getBlocosQuebrados();

        Map<Number, Number> blocosPorFortuna = furiousMina.getMinaConfig().getBlocosPorFortuna();

        addFortuneEnchantment(blocosQuebradosPlayer, pickMeta, blocosPorFortuna);

        pickaxe.setItemMeta(pickMeta);
        return pickaxe;
    }


    private void giveEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000000, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000000, 2, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000000, 10, false, false));
    }

    private void setMinaScoreboard(Player player, FuriousMina furiousMina) throws SQLException {
        Map<String, String> scoreboardText = furiousMina.getMinaConfig().getScoreboardText();
        Scoreboard minaScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = minaScoreboard.registerNewObjective("Mina", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', scoreboardText.get("tittle")));

        Score website = obj.getScore(ChatColor.translateAlternateColorCodes('&', scoreboardText.get("site")));
        website.setScore(1);

        Score space = obj.getScore("");
        space.setScore(2);


        Score playersOnline = obj.getScore(ChatColor.translateAlternateColorCodes('&', scoreboardText.get("playersOnline")).replace("$playersOnline", Bukkit.getServer().getOnlinePlayers().size() + ""));
        playersOnline.setScore(3);

        Score space3 = obj.getScore("  ");
        space3.setScore(5);

        Team coinsTeam = minaScoreboard.registerNewTeam("coinsTeam");
        String coinsText= ChatColor.translateAlternateColorCodes('&', scoreboardText.get("coins"));
        coinsTeam.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
        coinsTeam.setPrefix(coinsText.split(":")[0] + ":");
        coinsTeam.setSuffix(coinsText.split(":")[1].replace("$coins", furiousMina.getPlayerCoins().get(player.getUniqueId()).toString()));
        obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(9);

        CustomFortune customFortune = getCustomFortune(furiousMina.getDb().getPlayer(player.getUniqueId().toString()).getBlocosQuebrados(), furiousMina.getMinaConfig().getBlocosPorFortuna());
        Team blocosQuebradosTeam = minaScoreboard.registerNewTeam("blocosQuebrados");
        String blocosQuebradosText = ChatColor.translateAlternateColorCodes('&', scoreboardText.get("blocos")).replace("$blocosQuebrados", furiousMina.getDb().getPlayer(player.getUniqueId().toString()).getBlocosQuebrados() + "").replace("$proximaMeta", customFortune.getNextRequiredBlocks() + "");
        String blocosQuebrados = blocosQuebradosText.split("/")[0];

        Score space4 = obj.getScore("    ");
        space4.setScore(7);

        String proximaMeta = customFortune.getNextRequiredBlocks() != -1 ?
                blocosQuebradosText.split("/")[1]
                :  ChatColor.translateAlternateColorCodes('&', furiousMina.getMinaConfig().getMensagens().get("upgradeFortunaMaximo"));


        blocosQuebradosTeam.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);
        blocosQuebradosTeam.setPrefix(blocosQuebrados + "/ ");
        blocosQuebradosTeam.setSuffix(proximaMeta);
        obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE).setScore(6);

        player.setScoreboard(minaScoreboard);
    }

}
