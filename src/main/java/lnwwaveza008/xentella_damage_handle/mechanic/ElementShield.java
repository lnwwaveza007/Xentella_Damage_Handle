package lnwwaveza008.xentella_damage_handle.mechanic;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderDouble;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.event.variablecontrol;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ElementShield extends SkillMechanic implements ITargetedEntitySkill {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);
    protected final String element;
    protected final PlaceholderDouble amount;
    protected final PlaceholderDouble time;

    public ElementShield(MythicLineConfig config) {
        super(config.getLine(), config);
        this.setAsyncSafe(false);
        this.setTargetsCreativePlayers(false);
        this.amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0, new String[0]);
        this.time = config.getPlaceholderDouble(new String[] {"time", "t"}, 0, new String[0]);
        this.element = config.getString(new String[] {"element"});
    }

    @Override
    public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (BukkitAdapter.adapt(target) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(target);
            variablecontrol variablecontrol = new variablecontrol();
            Long timel = (long) time.get(data);
            variablecontrol.AddShield(bukkittarget, element, amount.get(data), timel);
        }
        return true;
    }
}
