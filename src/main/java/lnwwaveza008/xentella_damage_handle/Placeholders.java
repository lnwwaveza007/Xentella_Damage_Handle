package lnwwaveza008.xentella_damage_handle;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.OfflinePlayer;

public class Placeholders extends PlaceholderExpansion {
    private final Xentella_Damage_Handle plugin;

    public Placeholders(Xentella_Damage_Handle plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "lnwwaveza008";
    }

    @Override
    public String getIdentifier() {
        return "xdh";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.contains("mmoitems")){
            String text = params.replace("mmoitems_","");
            text = text.toUpperCase();
            return String.valueOf(PlayerData.get(player).getStats().getMap().getStat(text));
        }
        return null;
    }
}
