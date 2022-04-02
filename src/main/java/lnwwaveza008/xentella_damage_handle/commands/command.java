package lnwwaveza008.xentella_damage_handle.commands;

import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.event.configload;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
