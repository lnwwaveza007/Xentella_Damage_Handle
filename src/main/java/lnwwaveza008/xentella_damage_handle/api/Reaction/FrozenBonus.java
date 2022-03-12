package lnwwaveza008.xentella_damage_handle.api.Reaction;

import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;


public class FrozenBonus extends DoubleStat {

    public FrozenBonus(String id, Material mat, String name, String[] lore) {
        super(id, mat, name, lore);
    }

    @Override
    public void whenApplied(@NotNull ItemStackBuilder item, @NotNull StatData data){
        double value = ((DoubleData) data).getValue();

        String format = ItemStat.translate("xdh-frozen-bonus");

        item.getLore().insert("xdh-frozen-bonus", format.replace("#",""+value));

        // Add tags
        item.addItemTag(getAppliedNBT(data));

    }
}
