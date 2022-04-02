package lnwwaveza008.xentella_damage_handle.event;

import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;

public class TimeManager {

    private HashMap<Entity, Double> elementshield  = new HashMap<>();;

    public TimeManager(Xentella_Damage_Handle cd){
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Iterator<Entity> it = elementshield.keySet().iterator(); it.hasNext();){
                    //while (iter.hasNext()){
                    Entity entity = it.next();
                    //cd.getServer().getConsoleSender().sendMessage(elementshield.get(entity).toString());
                    if(elementshield.get(entity) == 1){
                        if (entity.getPersistentDataContainer().has(new NamespacedKey(cd, "xdh_elementshield"), PersistentDataType.STRING)){
                            if (entity instanceof Player){
                                configload cfload = new configload();
                                String msg = cfload.getElementShieldRemoveMSG().replace("{x}", entity.getPersistentDataContainer().get(new NamespacedKey(cd, "xdh_elementshield"), PersistentDataType.STRING));
                                msg = msg.replace("{y}", entity.getPersistentDataContainer().get(new NamespacedKey(cd, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                                entity.sendMessage(msg);
                            }
                            entity.getPersistentDataContainer().remove(new NamespacedKey(cd, "xdh_elementshield"));
                            entity.getPersistentDataContainer().remove(new NamespacedKey(cd, "xdh_elementshieldA"));
                            elementshield.put(entity,elementshield.get(entity)-1);
                        }
                        continue;
                    }else if (elementshield.get(entity) <= 0){
                        it.remove();
                    }else{
                        elementshield.put(entity,elementshield.get(entity)-1);
                    }
                }
            }
        }.runTaskTimer(cd, 0, 20);
    }

    public void AddEntityToTime(Entity e, Double time){
        elementshield.put(e, time);
    }

    public void RemoveEntityFromTime(Entity e){
        elementshield.remove(e);
    }
}
