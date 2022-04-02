package lnwwaveza008.xentella_damage_handle.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.event.variablecontrol;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class XentellaDamage implements ITargetedEntitySkill {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);
    protected final String damagetype;
    protected final String element;
    protected final PlaceholderDouble amount;

    public XentellaDamage(MythicLineConfig config) {
        this.amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0, new String[0]);
        this.damagetype = config.getString(new String[] {"damagetype"});
        this.element = config.getString(new String[] {"element"});
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
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
        return SkillResult.SUCCESS;
    }
}
