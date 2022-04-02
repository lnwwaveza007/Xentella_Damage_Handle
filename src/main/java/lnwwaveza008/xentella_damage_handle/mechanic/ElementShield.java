package lnwwaveza008.xentella_damage_handle.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.event.variablecontrol;
import org.bukkit.entity.Entity;

public class ElementShield implements ITargetedEntitySkill {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);
    protected final String element;
    protected final PlaceholderDouble amount;
    protected final PlaceholderDouble time;

    public ElementShield(MythicLineConfig config) {
        this.amount = config.getPlaceholderDouble(new String[] {"amount", "a"}, 0, new String[0]);
        this.time = config.getPlaceholderDouble(new String[] {"time", "t"}, 0, new String[0]);
        this.element = config.getString(new String[] {"element"});
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (BukkitAdapter.adapt(target) != null) {
            Entity bukkittarget = BukkitAdapter.adapt(target);
            variablecontrol variablecontrol = new variablecontrol();
            Long timel = (long) time.get(data);
            variablecontrol.AddShield(bukkittarget, element, amount.get(data), timel);
        }
        return SkillResult.SUCCESS;
    }
}
