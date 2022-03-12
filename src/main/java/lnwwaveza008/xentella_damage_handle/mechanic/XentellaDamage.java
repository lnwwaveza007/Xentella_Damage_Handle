package lnwwaveza008.xentella_damage_handle.mechanic;

import com.google.common.cache.CacheStats;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.SkillAdapter;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.commands.command;
import lnwwaveza008.xentella_damage_handle.event.variablecontrol;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;

public class XentellaDamage extends SkillMechanic implements ITargetedEntitySkill {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);
    protected final String damagetype;
    protected final String element;
    protected final PlaceholderDouble amount;

    public XentellaDamage(MythicLineConfig config) {
        super(config.getLine(), config);
        this.setAsyncSafe(false);
        this.setTargetsCreativePlayers(false);
        this.amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0, new String[0]);
        this.damagetype = config.getString(new String[] {"damagetype"});
        this.element = config.getString(new String[] {"element"});
    }

    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (BukkitAdapter.adapt(target) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(target);
            Entity bukkitcaster = data.getCaster().getEntity().getBukkitEntity();
            variablecontrol vc = new variablecontrol();
            if (element != null) {
                vc.AddElement(bukkitcaster, element);
            }
            // Do Damage
            if (bukkitcaster instanceof Player){
                PlayerData playerData = PlayerData.get(bukkitcaster.getUniqueId());
                DamageMetadata damage = null;
                if (damagetype == null){
                    damage = new DamageMetadata(amount.get(data));
                }else {
                    if (damagetype.equalsIgnoreCase("MAGIC")) {
                        damage = new DamageMetadata(amount.get(data), DamageType.MAGIC);
                    } else if (damagetype.equalsIgnoreCase("PHYSICAL")) {
                        damage = new DamageMetadata(amount.get(data), DamageType.PHYSICAL);
                    }
                }
                StatMap statMap = playerData.getMMOPlayerData().getStatMap();
                PlayerMetadata playerMetadata = new PlayerMetadata(statMap, EquipmentSlot.MAIN_HAND);
                AttackMetadata attack = new AttackMetadata(damage, playerMetadata);
                MythicLib.plugin.getDamage().damage(attack, (LivingEntity) bukkittarget, false); // finally deal damage
            }else{
                if (damagetype != null){
                    vc.AddDamageType(bukkitcaster, damagetype);
                }
                ((LivingEntity) bukkittarget).damage(amount.get(data), bukkitcaster);
            }
            //PersistentDataContainer elementdata = bukkittarget.getPersistentDataContainer();
            //elementdata.set(new NamespacedKey(pl, "xdh_element"),PersistentDataType.STRING,element);
        }
        return true;
    }
}
