package org.lucas.furiousmina.config;

import org.lucas.furiousmina.FuriousMina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinaConfig extends Config{
    public MinaConfig(FuriousMina plugin, String fileName) {
        super(plugin, fileName);
    }

    public Map<String,Number> getOresChances(){
        return listToMap((List<Map<String, Number>>) getCustomConfig().getList("minerios"));
    }

    public Map<Number, Number> getBlocosPorFortuna(){
        return listToMap((List<Map<Number, Number>>) getCustomConfig().getList("fortunas"));
    }

    public Map<String, String> getMensagens(){
        return listToMap((List<Map<String, String>>) getCustomConfig().getList("mensagens"));
    }

    public Map<String, String> getScoreboardText(){
        return listToMap((List<Map<String, String>>) getCustomConfig().getList("scoreboard"));
    }

    public Map<String,Number> getPrecoMinerios(){
        return listToMap((List<Map<String, Number>>) getCustomConfig().getList("precoMinerios"));
    }

    public int getDelay(){
        return getCustomConfig().getInt("delay");
    }

    public int getCamadaDiamante(){
        return getCustomConfig().getInt("camadaDiamante");
    }

    public boolean getEmeraldForestHills(){
        return getCustomConfig().getBoolean("esmeraldaSomenteNoForestHills");
    }

    public <K, V> Map<K, V> listToMap(List<Map<K,V>> list){
        Map<K,V> toReturn = new HashMap<>();
        for(Map<K,V> map : list){
            toReturn.putAll(map);
        };
        return toReturn;
    }
}
