package lnwwaveza008.xentella_damage_handle.event;

import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class variablecontrol {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);

    public void AddElement(Entity entity,String element){
        entity.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING, element);
    }

    public void AddDamageType(Entity entity,String type){
        entity.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_damagetype"), PersistentDataType.STRING, type);
    }

    public void AddElementToTarget(Entity entity,String element){
        //Set Glow to Entity
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(element.toUpperCase()+"SHIELD");
        team.addEntry(entity.getUniqueId().toString());
        entity.setGlowing(true);
        //
        entity.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING, element);
        configload cfload = new configload();
        new BukkitRunnable() {
            @Override
            public void run(){
                if (entity != null) {
                    if (entity.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING)) {
                        entity.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementvictim"));
                        if (team.hasEntry(entity.getUniqueId().toString())) {
                            team.removeEntry(entity.getUniqueId().toString());
                        }
                        entity.setGlowing(false);
                    }
                }
            }
        }.runTaskLater(pl, 20 * cfload.getElementDelay());
    }

    public void AddShield(Entity entity,String element,Double Amount,Long Time){
        // Check if player already have shield same id.
        entity.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING, element);
        entity.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Amount);
        pl.getTimemanager().AddEntityToTime(entity, Double.valueOf(Time));
        // Send Message
        if (entity instanceof Player) {
            configload cfload = new configload();
            String msg = cfload.getElementShieldReciveMSG().replace("{x}", entity.getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
            msg = msg.replace("{y}", entity.getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
            msg = msg.replace("{z}", String.valueOf(Time));
            entity.sendMessage(msg);
        }
        // Send Message
    }

    public void DropShield(Entity entity,Material material,String element,Double amount,Integer timeD){
        Location loc = entity.getLocation();
        ItemStack item = new ItemStack(material);
        Entity dropitem = loc.getWorld().dropItem(entity.getLocation(), item);
        dropitem.setGlowing(true);
        ((Item) dropitem).setPickupDelay(30);
        //Set Color of Entity
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam(element+"SHIELD");
        team.addEntry(dropitem.getUniqueId().toString());
        //Add Some Variable to Item
        Long time = (long) timeD;
        dropitem.getPersistentDataContainer().set(new NamespacedKey(pl, "EShield"), PersistentDataType.STRING, element);
        dropitem.getPersistentDataContainer().set(new NamespacedKey(pl, "EShieldA"), PersistentDataType.DOUBLE, amount);
        dropitem.getPersistentDataContainer().set(new NamespacedKey(pl, "EShieldT"), PersistentDataType.LONG, time);
    }

}
