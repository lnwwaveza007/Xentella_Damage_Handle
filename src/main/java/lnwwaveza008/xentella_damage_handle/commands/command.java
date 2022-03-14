package lnwwaveza008.xentella_damage_handle.commands;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.variables.Variable;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.event.configload;
import lnwwaveza008.xentella_damage_handle.event.variablecontrol;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class command implements CommandExecutor {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0].equalsIgnoreCase("Reload")) {
            if (sender.isOp()) {
                pl.reloadConfig();
                configload cfload = new configload();
                cfload.onReload();
                sender.sendMessage("Reload Complete");
                //sender.sendMessage(String.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(((Player) sender).getTargetEntity(50)).getLevel()));
                //sender.sendMessage(MythicMobs.inst().getAPIHelper().getMythicMobInstance(((Player) sender).getTargetEntity(50)).getVariables().get("TEST").get().toString());
            }
        }
        return true;
    }
}
