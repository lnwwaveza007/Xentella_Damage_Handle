package lnwwaveza008.xentella_damage_handle.event;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.variables.Variable;
import io.lumine.xikage.mythicmobs.skills.variables.VariableRegistry;
import io.lumine.xikage.mythicmobs.skills.variables.VariableScope;
import io.lumine.xikage.mythicmobs.skills.variables.VariableType;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

import static io.lumine.xikage.mythicmobs.skills.variables.VariableMechanic.getVariableManager;

public class ReactionCalculate {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);

    public Double DamageCal(Entity entity, String Type, Entity victim){
        configload cfload = new configload();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            //================= Amplifying Reaction =================
            // Calculate
            if (Type.contains("AMPLIFYING")) {
                Double MultReaction = 0.0;
                Double ReactionBonus = 0.0;
                if (Type.contains("VAPORIZE")){
                    MultReaction = 1.5;
                    ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_VAPORIZE_BONUS");
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMeltReaction(), victim.getLocation());
                }else if (Type.contains("MELT")){
                    MultReaction = 2.0;
                    ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_MELT_BONUS");
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getVaporizeReaction(), victim.getLocation());
                }
                Double K = cfload.getAmplifyingK();
                Double C = cfload.getAmplifyingC();
                Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                //Double MultAmplifying = MultReaction * (1 + EMbonus + ReactionBonus);
                Double MultAmplifying = cfload.getMultAmplifyingFormular().setVariable("x",MultReaction).setVariable("y",EMbonus).setVariable("z",ReactionBonus).evaluate();
                return MultAmplifying;
            }
            else if (Type.contains("CRYSTALLIZE")) { //================= Crystallize Reaction =================
                if (Type.contains("HYDRO") || Type.contains("ELECTRO") || Type.contains("CRYO") || Type.contains("PYRO")){
                    variablecontrol vc = new variablecontrol();
                    Material material = null;
                    String element = "";
                    if (Type.contains("HYDRO")){
                        material =  Material.LIGHT_BLUE_CONCRETE;
                        element = "HYDRO";
                        Double MudBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_MUD_BONUS");
                        Integer CCTotal = (int) ((cfload.getCCFormular().setVariable("x",5).setVariable("y",MudBonus).evaluate()) * 20);
                        getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putInt("ReactionDuration", CCTotal);
                        if (victim instanceof Player) {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMudReaction() + "-HUMAN", victim.getLocation());
                        }else {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMudReaction() + "-AHUMAN", victim.getLocation());
                            //  Slow
                            Double oldspeed = ((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                            Double speed = oldspeed - (((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 0.05);
                            Double oldatkspeed = ((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue();
                            Double atkspeed = oldatkspeed - (((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue() * 0.05);
                            ((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
                            ((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(atkspeed);
                            // Cooldown
                            new BukkitRunnable() {
                                @Override
                                public void run(){
                                    if (victim != null) {
                                        ((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(oldspeed);
                                        ((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(oldatkspeed);
                                    }
                                }
                            }.runTaskLater(pl, CCTotal);
                        }
                    }else if (Type.contains("ELECTRO")){
                        material =  Material.PURPLE_CONCRETE;
                        element = "ELECTRO";
                    }else if (Type.contains("CRYO")){
                        material =  Material.WHITE_CONCRETE;
                        element = "CRYO";
                        if (victim instanceof Player) {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getShatterReaction() + "-HUMAN", victim.getLocation());
                        }else {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getShatterReaction() + "-AHUMAN", victim.getLocation());
                        }
                    }else if (Type.contains("PYRO")){
                        material =  Material.RED_CONCRETE;
                        element = "PYRO";
                        Double K = cfload.getTransformativeK();
                        Double C = cfload.getTransformativeC();
                        Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                        Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                        Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                        Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_MAGMA_BONUS");
                        Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",1.4).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                        getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMagmaReaction(), victim.getLocation());
                    }
                    // Calculate EmBonus
                    Double K = cfload.getCrystallizeK();
                    Double C = cfload.getCrystallizeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double ShieldBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_CRYSTALLIZE_SHIELD_BONUS");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    // Shield Calcutate
                    Double ShieldA = cfload.getCrystallizeShieldFormular().setVariable("x",player.getHealth()).setVariable("y",EMbonus).setVariable("z", ShieldBonus).evaluate();
                    if (Type.contains("PYRO") || Type.contains("CRYO"))  {
                        vc.DropShield(victim, material, element, ShieldA, 10);
                    }else{
                        vc.DropShield(victim, material, element, ShieldA, 30);
                    }
                    return 1.0;
                }else{
                    if (Type.contains("FROZEN")){
                        Double FrozenBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_FROZEN_BONUS");
                        Integer CCTotal = (int) ((cfload.getCCFormular().setVariable("x",5).setVariable("y",FrozenBonus).evaluate()) * 20);
                        getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putInt("ReactionDuration", CCTotal);
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getFrozenReaction(), victim.getLocation());
                    }
                    if (Type.contains("DUST")){
                        Double DustBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_DUST_BONUS");
                        Integer CCTotal = (int) (cfload.getCCFormular().setVariable("x",4).setVariable("y",DustBonus).evaluate());
                        getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putInt("ReactionDuration", CCTotal);
                        MythicMobs.inst().getAPIHelper().castSkill(player, cfload.getDustReaction(), victim.getLocation());
                        //Push
                        Vector direction = victim.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
                        direction.multiply(CCTotal/2);
                        victim.setVelocity(direction);
                    }
                }
            }
            else if (Type.contains("TRANSFORMATIVE")) { //================= Transformative Reaction =================
                //
                if (Type.contains("ELECTRO-CHARGED")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_ELECTROCHARGED_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.5).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getElectroCharged(), victim.getLocation());
                }
                if (Type.contains("BURN")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_BURN_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.6).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getBurnReaction(), victim.getLocation());
                }
                if (Type.contains("POISON")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_POISON_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",1.2).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getPoisonReaction(), victim.getLocation());
                }
                if (Type.contains("OVERLOADED")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_OVERLOADED_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",4).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getOverloadedReaction(), victim.getLocation());
                }
                if (Type.contains("THORN")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_THORN_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.7).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getThornReaction(), victim.getLocation());
                }
                if (Type.contains("SUPERCONDUCT")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_SUPERCONDUCT_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.5).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    if (entity instanceof Player) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSuperConductReaction()+"-HUMAN", victim.getLocation());
                    }else{
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSuperConductReaction()+"-AHUMAN", victim.getLocation());
                        victim.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_superconduct"), PersistentDataType.STRING, "true");
                        // Countdown
                        new BukkitRunnable() {
                            @Override
                            public void run(){
                                if (victim != null) {
                                    if (victim.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_superconduct"), PersistentDataType.STRING)){
                                        victim.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_superconduct"));
                                    }
                                }
                            }
                        }.runTaskLater(pl, 80);
                    }
                }
                // Swirl
                if (Type.contains("CRYO") || Type.contains("ELECTRO") || Type.contains("HYDRO") || Type.contains("PYRO")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double EM = PlayerData.get(player).getStats().getMap().getStat("XDH_EM");
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",Double.valueOf(PlayerData.get(player).getLevel())).evaluate();
                    Double ReactionBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_SWIRL_BONUS");
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.6).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    if (Type.contains("CRYO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlCryoReaction(), victim.getLocation());
                        Double BlizzardBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_BLIZZARD_BONUS");
                        Integer CCTotal = (int) ((cfload.getCCFormular().setVariable("x",10).setVariable("y",BlizzardBonus).evaluate()) * 20);
                        getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putInt("ReactionDuration", CCTotal);
                        if (victim instanceof Player) {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getBlizzardReaction()+"-HUMAN", victim.getLocation());
                        }else {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getBlizzardReaction()+"-AHUMAN", victim.getLocation());
                            victim.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING, "true");
                            // Countdown
                            new BukkitRunnable() {
                                @Override
                                public void run(){
                                    if (victim != null) {
                                        if (victim.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                                            victim.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_blizzard"));
                                        }
                                    }
                                }
                            }.runTaskLater(pl, CCTotal);
                        }
                    }
                    if (Type.contains("ELECTRO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlElectroReaction(), victim.getLocation());
                    }
                    if (Type.contains("HYDRO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlHydroReaction(), victim.getLocation());
                    }
                    if (Type.contains("PYRO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlPyroReaction(), victim.getLocation());
                    }
                }
            }
            //================= Another Reaction =================
        }else{
            //================= Amplifying Reaction =================
            // Calculate
            if (Type.contains("AMPLIFYING")) {
                Double MultReaction = 0.0;
                Double ReactionBonus = 0.0;
                if (Type.contains("VAPORIZE")){
                    MultReaction = 1.5;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("VaporizeBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("VaporizeBonus").get().toString());
                        }
                    }
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMeltReaction(), victim.getLocation());
                }else if (Type.contains("MELT")){
                    MultReaction = 2.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("MeltBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("MeltBonus").get().toString());
                        }
                    }
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getVaporizeReaction(), victim.getLocation());
                }
                Double K = cfload.getAmplifyingK();
                Double C = cfload.getAmplifyingC();
                Double victimlevel = 1.0;
                if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                    victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                }
                Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                //Double MultAmplifying = MultReaction * (1 + EMbonus + ReactionBonus);
                Double MultAmplifying = cfload.getMultAmplifyingFormular().setVariable("x",MultReaction).setVariable("y",EMbonus).setVariable("z",ReactionBonus).evaluate();
                return MultAmplifying;
            }
            else if (Type.contains("CRYSTALLIZE")) { //================= Crystallize Reaction =================
                if (Type.contains("HYDRO") || Type.contains("ELECTRO") || Type.contains("CRYO") || Type.contains("PYRO")){
                    variablecontrol vc = new variablecontrol();
                    Material material = null;
                    String element = "";
                    if (Type.contains("HYDRO")){
                        material =  Material.LIGHT_BLUE_CONCRETE;
                        element = "HYDRO";
                        Double MudBonus = 0.0;
                        if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                            if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("MudBonus") != null) {
                                MudBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("MudBonus").get().toString());
                            }
                        }
                        Integer CCTotal = (int) ((cfload.getCCFormular().setVariable("x",5).setVariable("y",MudBonus).evaluate()) * 20);
                        VariableRegistry variables = getVariableManager().getRegistry(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity));
                        variables.putInt("ReactionDuration", CCTotal);
                        if (victim instanceof Player) {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMudReaction() + "-HUMAN", victim.getLocation());
                        }else{
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMudReaction() + "-AHUMAN", victim.getLocation());
                            //  Slow
                            Double oldspeed = ((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                            Double speed = oldspeed - (((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() * 0.05);
                            Double oldatkspeed = ((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue();
                            Double atkspeed = oldatkspeed - (((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue() * 0.05);
                            ((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
                            ((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(atkspeed);
                            // Cooldown
                            new BukkitRunnable() {
                                @Override
                                public void run(){
                                    if (entity != null) {
                                        ((LivingEntity) victim).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(oldspeed);
                                        ((LivingEntity) victim).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(oldatkspeed);
                                    }
                                }
                            }.runTaskLater(pl, CCTotal);
                        }
                    }else if (Type.contains("ELECTRO")){
                        material =  Material.PURPLE_CONCRETE;
                        element = "ELECTRO";
                    }else if (Type.contains("CRYO")){
                        material =  Material.WHITE_CONCRETE;
                        element = "CRYO";
                        if (victim instanceof Player) {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getShatterReaction() + "-HUMAN", victim.getLocation());
                        }else {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getShatterReaction() + "-AHUMAN", victim.getLocation());
                        }
                    }else if (Type.contains("PYRO")){
                        material =  Material.RED_CONCRETE;
                        element = "PYRO";
                        Double K = cfload.getTransformativeK();
                        Double C = cfload.getTransformativeC();
                        Double victimlevel = 1.0;
                        if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                            victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                        }
                        Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                        Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                        Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                        Double ReactionBonus = 0.0;
                        if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                            if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("MagmaBonus") != null) {
                                ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("MagmaBonus").get().toString());
                            }
                        }
                        Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",1.4).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                        getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMagmaReaction(), victim.getLocation());
                    }
                    // Calculate EmBonus
                    Double K = cfload.getCrystallizeK();
                    Double C = cfload.getCrystallizeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double ShieldBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("CrystallizeShieldBonus") != null) {
                            ShieldBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("CrystallizeShieldBonus").get().toString());
                        }
                    }
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    // Shield Calcutate
                    Double ShieldA = cfload.getCrystallizeShieldFormular().setVariable("x",((LivingEntity) entity).getHealth()).setVariable("y",EMbonus).setVariable("z", ShieldBonus).evaluate();
                    if (Type.contains("PYRO") || Type.contains("CRYO"))  {
                        vc.DropShield(victim, material, element, ShieldA, 10);
                    }else{
                        vc.DropShield(victim, material, element, ShieldA, 30);
                    }
                    return 1.0;
                }else{
                    if (Type.contains("FROZEN")){
                        Double FrozenBonus = 0.0;
                        if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                            if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("FrozenBonus") != null) {
                                FrozenBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("FrozenBonus").get().toString());
                            }
                        }
                        Integer CCTotal = (int) ((cfload.getCCFormular().setVariable("x",5).setVariable("y",FrozenBonus).evaluate()) * 20);
                        VariableRegistry variables = getVariableManager().getRegistry(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity));
                        variables.putInt("ReactionDuration", CCTotal);
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getFrozenReaction(), victim.getLocation());
                    }
                    if (Type.contains("DUST")){
                        Double DustBonus = 0.0;
                        if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                            if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("DustBonus") != null) {
                                DustBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("DustBonus").get().toString());
                            }
                        }
                        Integer CCTotal = (int) (cfload.getCCFormular().setVariable("x",4).setVariable("y",DustBonus).evaluate());
                        VariableRegistry variables = getVariableManager().getRegistry(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity));
                        variables.putInt("ReactionDuration", CCTotal);
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getDustReaction(), victim.getLocation());
                        //Push
                        Vector direction = victim.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
                        direction.multiply(CCTotal/2);
                        victim.setVelocity(direction);
                    }
                }
            }
            else if (Type.contains("TRANSFORMATIVE")) { //================= Transformative Reaction =================
                //
                if (Type.contains("ELECTRO-CHARGED")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("ElectroChargedBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("ElectroChargedBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.5).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getElectroCharged(), victim.getLocation());
                }
                if (Type.contains("BURN")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("BurnBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("BurnBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.6).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getBurnReaction(), victim.getLocation());
                }
                if (Type.contains("POISON")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("PoisonBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("PoisonBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",1.2).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getPoisonReaction(), victim.getLocation());
                }
                if (Type.contains("OVERLOADED")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("OverloadedBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("OverloadedBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",4).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getMagmaReaction(), victim.getLocation());
                }
                if (Type.contains("SUPERCONDUCT")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("SuperConductBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("SuperConductBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.5).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    if (entity instanceof Player) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSuperConductReaction()+"-HUMAN", victim.getLocation());
                    }else{
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSuperConductReaction()+"-AHUMAN", victim.getLocation());
                        victim.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_superconduct"), PersistentDataType.STRING, "true");
                        // Countdown
                        new BukkitRunnable() {
                            @Override
                            public void run(){
                                if (victim != null) {
                                    if (victim.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_superconduct"), PersistentDataType.STRING)){
                                        victim.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_superconduct"));
                                    }
                                }
                            }
                        }.runTaskLater(pl, 80);
                    }
                }
                // Thorn
                if (Type.contains("THORN")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("ThornBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("ThornBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.7).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getThornReaction(), victim.getLocation());
                }
                // Swirl
                if (Type.contains("CRYO") || Type.contains("ELECTRO") || Type.contains("HYDRO") || Type.contains("PYRO")){
                    Double K = cfload.getTransformativeK();
                    Double C = cfload.getTransformativeC();
                    Double victimlevel = 1.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        victimlevel = MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getLevel();
                    }
                    Double EM = cfload.getEmEXP().setVariable("x",victimlevel).evaluate();
                    Double EMbonus = cfload.getEMBonusFormular().setVariable("x",EM).setVariable("k",K).setVariable("c",C).evaluate();
                    Double MultLevel = cfload.getMultLevel().setVariable("x",victimlevel).evaluate();
                    Double ReactionBonus = 0.0;
                    if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                        if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("SwirlBonus") != null) {
                            ReactionBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("SwirlBonus").get().toString());
                        }
                    }
                    Float MultTransformative = (float) cfload.getMultTransformative().setVariable("x",0.6).setVariable("y",MultLevel).setVariable("z",EMbonus).setVariable("a",ReactionBonus).evaluate();
                    getVariableManager().getRegistry(VariableScope.TARGET, BukkitAdapter.adapt(entity)).putFloat("ReactionDamage", MultTransformative);
                    if (Type.contains("CRYO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlCryoReaction(), victim.getLocation());
                        Double BlizzardBonus = 0.0;
                        if (MythicMobs.inst().getAPIHelper().isMythicMob(entity)) {
                            if (MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("BlizzardBonus") != null) {
                                BlizzardBonus = Double.valueOf(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity).getVariables().get("BlizzardBonus").get().toString());
                            }
                        }
                        Integer CCTotal = (int) ((cfload.getCCFormular().setVariable("x",10).setVariable("y",BlizzardBonus).evaluate()) * 20);
                        VariableRegistry variables = getVariableManager().getRegistry(MythicMobs.inst().getAPIHelper().getMythicMobInstance(entity));
                        variables.putInt("ReactionDuration", CCTotal);
                        if (victim instanceof Player) {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getBlizzardReaction()+"-HUMAN", victim.getLocation());
                        }else {
                            MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getBlizzardReaction()+"-AHUMAN", victim.getLocation());
                            victim.getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING, "true");
                            // Countdown
                            new BukkitRunnable() {
                                @Override
                                public void run(){
                                    if (victim != null) {
                                        if (victim.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                                            victim.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_blizzard"));
                                        }
                                    }
                                }
                            }.runTaskLater(pl, CCTotal);
                        }
                    }
                    if (Type.contains("ELECTRO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlElectroReaction(), victim.getLocation());
                    }
                    if (Type.contains("HYDRO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlHydroReaction(), victim.getLocation());
                    }
                    if (Type.contains("PYRO")) {
                        MythicMobs.inst().getAPIHelper().castSkill(entity, cfload.getSwirlPyroReaction(), victim.getLocation());
                    }
                }
            }
        }
        return 1.0;
    }

    public String CheckReactionType(String element, String oldelement){
        String mixelement = element.toUpperCase()+","+oldelement.toUpperCase();
        // Check Vaporize
        if (mixelement.contains("HYDRO") && mixelement.contains("PYRO")) {
            return "AMPLIFYING=VAPORIZE";
        }
        // Check Melt
        if (mixelement.contains("CRYO") && mixelement.contains("PYRO")) {
            return "AMPLIFYING=MELT";
        }
        //================= Crystallize Reaction =================
        // Geo Shield
        if (mixelement.contains("CRYO") && mixelement.contains("GEO")) {
            return "CRYSTALLIZE=CRYO";
        }
        if (mixelement.contains("ELECTRO") && mixelement.contains("GEO")) {
            return "CRYSTALLIZE=ELECTRO";
        }
        if (mixelement.contains("HYDRO") && mixelement.contains("GEO")) {
            return "CRYSTALLIZE=HYDRO";
        }
        if (mixelement.contains("PYRO") && mixelement.contains("GEO")) {
            return "CRYSTALLIZE=PYRO";
        }
        // Dust
        if (mixelement.contains("ANEMO") && mixelement.contains("GEO")) {
            return "CRYSTALLIZE=DUST";
        }
        // Frozen
        if (mixelement.contains("CRYO") && mixelement.contains("HYDRO")) {
            return "CRYSTALLIZE=FROZEN";
        }
        //================= Transformative Reaction =================
        // Superconduct
        if (mixelement.contains("CRYO") && mixelement.contains("ELECTRO")) {
            return "TRANSFORMATIVE=SUPERCONDUCT";
        }
        // Superconduct
        if (mixelement.contains("CRYO") && mixelement.contains("ELECTRO")) {
            return "TRANSFORMATIVE=SUPERCONDUCT";
        }
        // ELECTRO-CHARGED
        if (mixelement.contains("HYDRO") && mixelement.contains("ELECTRO")) {
            return "TRANSFORMATIVE=ELECTRO-CHARGED";
        }
        // SHATTER
        if (mixelement.contains("GEO") && mixelement.contains("CRYO")) {
            return "TRANSFORMATIVE=SHATTER";
        }
        // OVERLOADED
        if (mixelement.contains("PYRO") && mixelement.contains("ELECTRO")) {
            return "TRANSFORMATIVE=OVERLOADED";
        }
        // POISON
        if (mixelement.contains("DENDRO") && mixelement.contains("HYDRO")) {
            return "TRANSFORMATIVE=POISON";
        }
        // BURN
        if (mixelement.contains("DENDRO") && mixelement.contains("PYRO")) {
            return "TRANSFORMATIVE=BURN";
        }
        // MAGMA
        if (mixelement.contains("GEO") && mixelement.contains("PYRO")) {
            return "TRANSFORMATIVE=MAGMA";
        }
        // THORN
        if (mixelement.contains("GEO") && mixelement.contains("DENDRO")) {
            return "TRANSFORMATIVE=THORN";
        }
        // Swirl
        if (mixelement.contains("ANEMO") && mixelement.contains("CRYO")) {
            return "TRANSFORMATIVE=CRYO";
        }
        if (mixelement.contains("ANEMO") && mixelement.contains("ELECTRO")) {
            return "TRANSFORMATIVE=ELECTRO";
        }
        if (mixelement.contains("ANEMO") && mixelement.contains("HYDRO")) {
            return "TRANSFORMATIVE=HYDRO";
        }
        if (mixelement.contains("ANEMO") && mixelement.contains("PYRO")) {
            return "TRANSFORMATIVE=PYRO";
        }
        return null;
    }
}
