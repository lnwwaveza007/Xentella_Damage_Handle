package lnwwaveza008.xentella_damage_handle.event;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageType;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import lnwwaveza008.xentella_damage_handle.mechanic.ElementShield;
import lnwwaveza008.xentella_damage_handle.mechanic.XentellaDamage;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.Random;

public class damage implements Listener {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);

    @EventHandler
    public static void onAttack(PlayerAttackEvent e) {
        // Attacker = e.getPlayer().getName()
        // Victim = e.getEntity()
        configload cfload = new configload();
        //================ Dodge Calcuate =========================
        // Player -> Player
        BukkitAPIHelper mythicMobsAPI = new BukkitAPIHelper();
        if (e.getEntity() instanceof Player){
            //Calcuate Accuracy
            Double Accuracy = PlayerData.get(e.getPlayer()).getStats().getMap().getStat("ACCURACY");
            Double HiddenAccuracy = PlayerData.get(e.getPlayer()).getStats().getMap().getStat("HIDE_ACCURACY");
            Double AccuracyLevel = Double.valueOf(PlayerData.get(e.getPlayer()).getLevel());
            Double AccuracyTotal = Accuracy + HiddenAccuracy * 0.1102 * (Math.pow(1.12,AccuracyLevel/30));
            //Calcuate Evasion
            Double Evasion = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("EVASION");
            Double HiddenEvasion = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("HIDE_EVASION");
            Double EvasionLevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
            Double EvasionTotal = Evasion + HiddenEvasion * 0.1102 * (Math.pow(1.12,EvasionLevel/30));
            //Calculate HitRate
            Double HitRate = 0.2624 * AccuracyTotal - 0.2101 * EvasionTotal + 60.713;
            //Check HitRate
            if (HitRate < cfload.getMinHitRate()){
                HitRate = cfload.getMinHitRate();
            }else if (HitRate > cfload.getMaxHitRate()){
                HitRate = cfload.getMaxHitRate();
            }
            //Random Hit Rate
            Random random = new Random();
            Integer randomnum = random.nextInt(99) + 1;
            //If Miss
            if (randomnum > HitRate){
                e.setCancelled(true);
                if (e.getPlayer().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING)) {
                    e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                }
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf(cfload.getMissSound()), 1, 1);
                return;
            }
            //Player -> Monster
        }else{
            //Calcuate Accuracy
            Double Accuracy = PlayerData.get(e.getPlayer()).getStats().getMap().getStat("ACCURACY");
            Double HiddenAccuracy = PlayerData.get(e.getPlayer()).getStats().getMap().getStat("HIDE_ACCURACY");
            Double AccuracyLevel = Double.valueOf(PlayerData.get(e.getPlayer()).getLevel());
            Double AccuracyTotal = Accuracy + HiddenAccuracy * 0.1102 * (Math.pow(1.12,AccuracyLevel/30));
            //Calcuate Evasion
            Double Evasion = 0.0;
            Double HiddenEvasion = 0.0;
            Double EvasionLevel = 1.0;
            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Evasion") != null) {
                    Evasion = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Evasion").get().toString());
                }
                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("HiddenEvasion") != null) {
                    HiddenEvasion = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("HiddenEvasion").get().toString());
                }
                EvasionLevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
            }
            Double EvasionTotal = Evasion + HiddenEvasion * 0.1102 * (Math.pow(1.12,EvasionLevel/30));
            //Calculate HitRate
            Double HitRate = 0.2624 * AccuracyTotal - 0.2101 * EvasionTotal + 60.713;
            //Check HitRate
            if (HitRate < cfload.getMinHitRate()){
                HitRate = cfload.getMinHitRate();
            }else if (HitRate > cfload.getMaxHitRate()){
                HitRate = cfload.getMaxHitRate();
            }
            //Random Hit Rate
            Random random = new Random();
            Integer randomnum = random.nextInt(99) + 1;
            //If Miss
            if (randomnum > HitRate){
                e.setCancelled(true);
                if (e.getPlayer().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING)) {
                    e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                }
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf(cfload.getMissSound()), 1, 1);
                return;
            }
        }
        //================ Damage Calcuate =========================
        // Player -> Player
        if (e.getEntity() instanceof Player){
            // Check Element
            if (!(e.getPlayer().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING))) {
                Player player = e.getPlayer();
                // Set Level Victim
                Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                // Set Level Attacker
                Double attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                // ==================== Get MutiDef ====================
                Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                Double MutiDef = 1.0;
                //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                // ================= Get MutiRes ====================
                Double Restotal = 0.0;
                // Get Damage Type
                if (e.getAttack().getDamage().collectTypes().contains(DamageType.PHYSICAL)) {
                    Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("PHYSICAL_RES");
                } else if (e.getAttack().getDamage().collectTypes().contains(DamageType.MAGIC)) {
                    Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("MAGICAL_RES");
                }
                // Calculate MutiRes
                Double MutiRes = 1.0;
                if (Restotal < 0.0) {
                    //MutiRes = 1 - (Restotal/2);
                    MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                } else if (Restotal <= 0.75) {
                    MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                } else if (Restotal > 0.75) {
                    MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                }
                // ================= Get Muti DMG Reduction ====================
                //Double def = victimlevel*7+150*(victimlevel/10);
                Double def = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE");
                Double DMGReduction = 1.0;
                DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                // ================= Level Diff ====================
                Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                Double mutipy = 0.0;
                Double finaldmg = 0.0;
                if (attackerlevel >= victimlevel) {
                    mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                    finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                } else {
                    mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                    finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                }
                if (finaldmg < 1) {
                    finaldmg = 1.0;
                }
                // ============ Deal Damage ===================
                e.getAttack().getDamage().multiplicativeModifier(0);
                if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)){
                    Double Shield = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE);
                    finaldmg = finaldmg * 1/4;
                    if (Shield - finaldmg <= 0){
                        // Send Message
                        String msg = cfload.getElementShieldRemoveMSG().replace("{x}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
                        msg = msg.replace("{y}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                        e.getEntity().sendMessage(msg);
                        // Send Message
                        e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
                        e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
                        e.setCancelled(true);
                    }else{
                        e.getEntity().getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Shield-finaldmg);
                        e.setCancelled(true);
                    }
                }else {
                    e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                }
            }else{
                // If victim didn't have element
                if (!(e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING)) || e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
                    if (e.getEntity() instanceof Player) {
                        Player player = e.getPlayer();
                        // Set Level Victim
                        Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                        // Set Level Attacker
                        Double attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                        // ==================== Get MutiDef ====================
                        Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                        Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                        Double MutiDef =  cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                        // ================ Get MutiBouns ====================
                        String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                        Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                        Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS");
                        Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                        // ================= Get MutiRes ====================
                        Double Restotal = 0.0;
                        // Get Damage Type
                        Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES");
                        // Calculate MutiRes
                        Double MutiRes = 1.0;
                        if (Restotal < 0.0) {
                            //MutiRes = 1 - (Restotal/2);
                            MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal <= 0.75) {
                            MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal > 0.75) {
                            MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                        }
                        // ================= Get Muti DMG Reduction ====================
                        //Double def = victimlevel*7+150*(victimlevel/10);
                        Double def = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE");
                        Double DMGReduction = 1.0;
                        DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                        //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                        Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                        // ================= Level Diff ====================
                        Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiRes * MutiDMGReduction * MutiBonus;
                        //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                        //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                        Double mutipy = 0.0;
                        Double finaldmg = 0.0;
                        if (attackerlevel >= victimlevel) {
                            mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        } else {
                            mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        }
                        if (finaldmg < 1) {
                            finaldmg = 1.0;
                        }
                        // ============ Deal Damage ===================
                        e.getAttack().getDamage().multiplicativeModifier(0);
                        if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
                            Double Shield = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE);
                            //Check element Type
                            if (e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING).equalsIgnoreCase(element)){
                                finaldmg = finaldmg * 1/3;
                            }
                            if (Shield - finaldmg <= 0){
                                // Send Message
                                String msg = cfload.getElementShieldRemoveMSG().replace("{x}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
                                msg = msg.replace("{y}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                                e.getEntity().sendMessage(msg);
                                // Send Message
                                e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
                                e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
                                e.setCancelled(true);
                            }else{
                                e.getEntity().getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Shield-finaldmg);
                                e.setCancelled(true);
                            }
                        }else {
                            e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                            variablecontrol variablecontrol = new variablecontrol();
                            variablecontrol.AddElementToTarget(e.getEntity(), element);
                        }
                        e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                    }
                }else{ // If have go Reaction!!!!!
                    ReactionCalculate reactionCalculate = new ReactionCalculate();
                    String reactiontype = reactionCalculate.CheckReactionType(e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING), e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING));
                    //Check It's will have reaction?
                    if (reactiontype == null){
                        Player player = e.getPlayer();
                        // Set Level Victim
                        Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                        // Set Level Attacker
                        Double attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                        // ==================== Get MutiDef ====================
                        Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                        Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                        Double MutiDef =  cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                        // ================ Get MutiBouns ====================
                        String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                        Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                        Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS");
                        Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                        // ================= Get MutiRes ====================
                        Double Restotal = 0.0;
                        // Get Damage Type
                        Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES");
                        // Calculate MutiRes
                        Double MutiRes = 1.0;
                        if (Restotal < 0.0) {
                            //MutiRes = 1 - (Restotal/2);
                            MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal <= 0.75) {
                            MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal > 0.75) {
                            MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                        }
                        // ================= Get Muti DMG Reduction ====================
                        //Double def = victimlevel*7+150*(victimlevel/10);
                        Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                        Double DMGReduction = 1.0;
                        DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                        //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                        Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                        // ================= Level Diff ====================
                        Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiRes * MutiDMGReduction * MutiBonus;
                        //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                        //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                        Double mutipy = 0.0;
                        Double finaldmg = 0.0;
                        if (attackerlevel >= victimlevel) {
                            mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        } else {
                            mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        }
                        if (finaldmg < 1) {
                            finaldmg = 1.0;
                        }
                        // ============ Deal Damage ===================
                        e.getAttack().getDamage().multiplicativeModifier(0);
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf(finaldmg));
                        e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                        variablecontrol variablecontrol = new variablecontrol();
                        variablecontrol.AddElementToTarget(e.getEntity(),element);
                        e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                    }else{
                        //================ Check Reaction Type =========================
                        if (reactiontype.contains("AMPLIFYING")){
                            Player player = e.getPlayer();
                            // Set Level Victim
                            Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                            // Set Level Attacker
                            Double attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                            // ==================== Get MutiDef ====================
                            Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                            Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                            Double MutiDef =  cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                            //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                            // ================ Get MutiBouns ====================
                            String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                            String element2 = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING);
                            Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                            Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS") + PlayerData.get(player).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_BONUS");
                            Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                            // ================= Get MutiRes ====================
                            Double Restotal = 0.0;
                            // Get Damage Type
                            Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES") + PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_RES");
                            // Calculate MutiRes
                            Double MutiRes = 1.0;
                            if (Restotal < 0.0) {
                                //MutiRes = 1 - (Restotal/2);
                                MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal <= 0.75) {
                                MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal > 0.75) {
                                MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                            }
                            // ================= Get Muti DMG Reduction ====================
                            //Double def = victimlevel*7+150*(victimlevel/10);
                            Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                            Double DMGReduction = 1.0;
                            DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                            //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                            Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                            // ================= Level Diff ====================
                            Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiRes * MutiDMGReduction * MutiBonus;
                            //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                            //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                            Double mutipy = 0.0;
                            Double finaldmg = 0.0;
                            if (attackerlevel >= victimlevel) {
                                mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            } else {
                                mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            }
                            if (finaldmg < 1) {
                                finaldmg = 1.0;
                            }
                            // Get MultReaction
                            Double MultReaction = reactionCalculate.DamageCal(e.getPlayer(), reactiontype, e.getEntity());
                            finaldmg = finaldmg * MultReaction;
                            // ============ Deal Damage ===================
                            e.getAttack().getDamage().multiplicativeModifier(0);
                            //pl.getServer().getConsoleSender().sendMessage(String.valueOf(finaldmg));
                            e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                            e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                        }
                        if (reactiontype.contains("CRYSTALLIZE")) {
                            Player player = e.getPlayer();
                            // Set Level Victim
                            Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                            // Set Level Attacker
                            Double attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                            // ==================== Get MutiDef ====================
                            Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                            Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                            Double MutiDef =  cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                            //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                            // ================ Get MutiBouns ====================
                            String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                            String element2 = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING);
                            Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                            Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS") + PlayerData.get(player).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_BONUS");
                            Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                            // ================= Get MutiRes ====================
                            Double Restotal = 0.0;
                            // Get Damage Type
                            Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES") + PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_RES");
                            // Calculate MutiRes
                            Double MutiRes = 1.0;
                            if (Restotal < 0.0) {
                                //MutiRes = 1 - (Restotal/2);
                                MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal <= 0.75) {
                                MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal > 0.75) {
                                MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                            }
                            // ================= Get Muti DMG Reduction ====================
                            //Double def = victimlevel*7+150*(victimlevel/10);
                            Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                            Double DMGReduction = 1.0;
                            DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                            //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                            Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                            // ================= Level Diff ====================
                            Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiRes * MutiDMGReduction * MutiBonus;
                            //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                            //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                            Double mutipy = 0.0;
                            Double finaldmg = 0.0;
                            if (attackerlevel >= victimlevel) {
                                mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            } else {
                                mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            }
                            if (finaldmg < 1) {
                                finaldmg = 1.0;
                            }
                            // Get MultReaction
                            Double MultReaction = reactionCalculate.DamageCal(e.getPlayer(), reactiontype, e.getEntity());
                            finaldmg = finaldmg * MultReaction;
                            // ============ Deal Damage ===================
                            e.getAttack().getDamage().multiplicativeModifier(0);
                            e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                            e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                        }
                        if (reactiontype.contains("TRANSFORMATIVE")){
                            e.setCancelled(true);
                            Double MultReaction = reactionCalculate.DamageCal(e.getPlayer(), reactiontype, e.getEntity());
                        }
                    }
                    //Double DamageTotal = reactionCalculate.CheckReaction((Entity) e.getPlayer(), e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING), e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING));
                }
            }
        }
        // Player -> Monster
        else {
            // Check Attacker have element?
            if (!(e.getPlayer().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING))) {
                Player player = e.getPlayer();
                // Set Level Victim
                Double victimlevel = 1.0;
                if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                    victimlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                }
                // Set Level Attacker
                Double attackerlevel = 1.0;
                attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                // ==================== Get MutiDef ====================
                Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                Double MutiDef = 1.0;
                //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                // ================= Get MutiRes ====================
                Double Restotal = 0.0;
                // Get Damage Type
                if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                    if (e.getAttack().getDamage().collectTypes().contains(DamageType.PHYSICAL)) {
                        if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("ResistancePhysical") != null) {
                            Restotal = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("ResistancePhysical").get().toString());
                            if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_superconduct"), PersistentDataType.STRING)){
                                Restotal = Restotal * 0.5;
                            }
                        }
                    } else if (e.getAttack().getDamage().collectTypes().contains(DamageType.MAGIC)) {
                        if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("ResistanceMagic") != null) {
                            Restotal = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("ResistanceMagic").get().toString());
                        }
                    }
                }
                // Calculate MutiRes
                Double MutiRes = 1.0;
                if (Restotal < 0.0) {
                    //MutiRes = 1 - (Restotal/2);
                    MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                } else if (Restotal <= 0.75) {
                    MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                } else if (Restotal > 0.75) {
                    MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                }
                // ================= Get Muti DMG Reduction ====================
                Double def = cfload.getDefenseEXP().setVariable("x", victimlevel).evaluate();
                if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                    def = def - (def * 0.10);
                }
                Double DMGReduction = 1.0;
                if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                    if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction") != null) {
                        DMGReduction = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction").get().toString());
                    }
                }
                //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                //pl.getServer().getConsoleSender().sendMessage(def+"");
                // ================= Level Diff ====================
                Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                Double mutipy = 0.0;
                Double finaldmg = 0.0;
                if (attackerlevel >= victimlevel) {
                    mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                    finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                } else {
                    mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                    finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                }
                if (finaldmg < 1) {
                    finaldmg = 1.0;
                }
                // ============ Deal Damage ===================
                e.getAttack().getDamage().multiplicativeModifier(0);
                if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)){
                    Double Shield = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE);
                    finaldmg = finaldmg * 1/4;
                    if (Shield - finaldmg <= 0){
                        // Send Message
                        String msg = cfload.getElementShieldRemoveMSG().replace("{x}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
                        msg = msg.replace("{y}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                        e.getEntity().sendMessage(msg);
                        // Send Message
                        e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
                        e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
                        e.setCancelled(true);
                    }else{
                        e.getEntity().getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Shield-finaldmg);
                        e.setCancelled(true);
                    }
                }else {
                    e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                }
            }else{ // ===================== Element Calcuate ========================
                // If victim didn't have element
                if (!(e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING))) {
                    Player player = e.getPlayer();
                    // Set Level Victim
                    Double victimlevel = 1.0;
                    if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                        victimlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                    }
                    // Set Level Attacker
                    Double attackerlevel = 1.0;
                    attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                    // ==================== Get MutiDef ====================
                    Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                    Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                    Double MutiDef = 1.0;
                    //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                    MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                    //pl.getServer().getConsoleSender().sendMessage(defignore+" "+defreduce+" "+victimlevel+" "+attackerlevel+" = "+MutiDef);
                    // ================ Get MutiBouns ====================
                    String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                    Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                    Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS");
                    Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                    // ================= Get MutiRes ====================
                    Double Restotal = 0.0;
                    // Get Damage Type
                    if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                        if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()) != null) {
                            Restotal = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()).get().toString());
                        }
                    }
                    // Calculate MutiRes
                    Double MutiRes = 1.0;
                    if (Restotal < 0.0) {
                        //MutiRes = 1 - (Restotal/2);
                        MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                    } else if (Restotal <= 0.75) {
                        MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                    } else if (Restotal > 0.75) {
                        MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                    }
                    // ================= Get Muti DMG Reduction ====================
                    Double def = cfload.getDefenseEXP().setVariable("x", victimlevel).evaluate();
                    if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                        def = def - (def * 0.10);
                    }
                    Double DMGReduction = 1.0;
                    if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                        if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction") != null) {
                            DMGReduction = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction").get().toString());
                        }
                    }
                    //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                    Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                    // ================= Level Diff ====================
                    Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiBonus * MutiDMGReduction * MutiRes;
                    //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                    //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                    Double mutipy = 0.0;
                    Double finaldmg = 0.0;
                    if (attackerlevel >= victimlevel) {
                        mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                        finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                    } else {
                        mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                        finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                    }
                    if (finaldmg < 1) {
                        finaldmg = 1.0;
                    }
                    // ============ Deal Damage ===================
                    e.getAttack().getDamage().multiplicativeModifier(0);
                    if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
                        Double Shield = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE);
                        //Check element Type
                        if (e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING).equalsIgnoreCase(element)){
                            finaldmg = finaldmg * 1/3;
                        }
                        if (Shield - finaldmg <= 0){
                            // Send Message
                            String msg = cfload.getElementShieldRemoveMSG().replace("{x}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
                            msg = msg.replace("{y}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                            e.getEntity().sendMessage(msg);
                            // Send Message
                            e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
                            e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
                            e.setCancelled(true);
                        }else{
                            e.getEntity().getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Shield-finaldmg);
                            e.setCancelled(true);
                        }
                    }else {
                        e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                        variablecontrol variablecontrol = new variablecontrol();
                        variablecontrol.AddElementToTarget(e.getEntity(), element);
                    }
                    e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                }else{ //Go reaction!!!!
                    ReactionCalculate reactionCalculate = new ReactionCalculate();
                    String reactiontype = reactionCalculate.CheckReactionType(e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING), e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING));
                    //Check It's will have reaction?
                    if (reactiontype == null){
                        Player player = e.getPlayer();
                        // Set Level Victim
                        Double victimlevel = 1.0;
                        if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                            victimlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                        }
                        // Set Level Attacker
                        Double attackerlevel = 1.0;
                        attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                        // ==================== Get MutiDef ====================
                        Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                        Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                        Double MutiDef = 1.0;
                        //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                        MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                        // ================ Get MutiBouns ====================
                        String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                        Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                        Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS");
                        Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                        // ================= Get MutiRes ====================
                        Double Restotal = 0.0;
                        // Get Damage Type
                        if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                            if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()) != null) {
                                Restotal = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()).get().toString());
                            }
                        }
                        // Calculate MutiRes
                        Double MutiRes = 1.0;
                        if (Restotal < 0.0) {
                            //MutiRes = 1 - (Restotal/2);
                            MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal <= 0.75) {
                            MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal > 0.75) {
                            MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                        }
                        // ================= Get Muti DMG Reduction ====================
                        Double def = cfload.getDefenseEXP().setVariable("x", victimlevel).evaluate();
                        if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                            def = def - (def * 0.10);
                        }
                        Double DMGReduction = 1.0;
                        if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                            if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction") != null) {
                                DMGReduction = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction").get().toString());
                            }
                        }
                        //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                        Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                        // ================= Level Diff ====================
                        Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiBonus * MutiDMGReduction * MutiRes;
                        //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                        //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                        Double mutipy = 0.0;
                        Double finaldmg = 0.0;
                        if (attackerlevel >= victimlevel) {
                            mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        } else {
                            mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        }
                        if (finaldmg < 1) {
                            finaldmg = 1.0;
                        }
                        // ============ Deal Damage ===================
                        e.getAttack().getDamage().multiplicativeModifier(0);
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf(finaldmg));
                        e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                        variablecontrol variablecontrol = new variablecontrol();
                        variablecontrol.AddElementToTarget(e.getEntity(),element);
                        e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                    }else{
                        //================ Check Reaction Type =========================
                        if (reactiontype.contains("AMPLIFYING")){
                            Player player = e.getPlayer();
                            // Set Level Victim
                            Double victimlevel = 1.0;
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                victimlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                            }
                            // Set Level Attacker
                            Double attackerlevel = 1.0;
                            attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                            // ==================== Get MutiDef ====================
                            Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                            Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                            Double MutiDef = 1.0;
                            //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                            MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                            //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                            // ================ Get MutiBouns ====================
                            String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                            String element2 = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING);
                            Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                            Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS") + PlayerData.get(player).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_BONUS");
                            Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                            // ================= Get MutiRes ====================
                            Double Restotal = 0.0;
                            // Get Damage Type
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()) != null) {
                                    Restotal = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()).get().toString());
                                }
                                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element2.toUpperCase()) != null) {
                                    Restotal = Restotal + Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element2.toUpperCase()).get().toString());
                                }
                            }
                            // Calculate MutiRes
                            Double MutiRes = 1.0;
                            if (Restotal < 0.0) {
                                //MutiRes = 1 - (Restotal/2);
                                MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal <= 0.75) {
                                MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal > 0.75) {
                                MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                            }
                            // ================= Get Muti DMG Reduction ====================
                            Double def = cfload.getDefenseEXP().setVariable("x", victimlevel).evaluate();
                            if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                                def = def - (def * 0.10);
                            }
                            Double DMGReduction = 1.0;
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction") != null) {
                                    DMGReduction = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction").get().toString());
                                }
                            }
                            //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                            Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                            // ================= Level Diff ====================
                            Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiBonus * MutiDMGReduction * MutiRes;
                            //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                            //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                            Double mutipy = 0.0;
                            Double finaldmg = 0.0;
                            if (attackerlevel >= victimlevel) {
                                mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            } else {
                                mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            }
                            if (finaldmg < 1) {
                                finaldmg = 1.0;
                            }
                            // Get MultReaction
                            Double MultReaction = reactionCalculate.DamageCal(e.getPlayer(), reactiontype, e.getEntity());
                            finaldmg = finaldmg * MultReaction;
                            // ============ Deal Damage ===================
                            e.getAttack().getDamage().multiplicativeModifier(0);
                            e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                            e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                        }
                        if (reactiontype.contains("CRYSTALLIZE")){
                            Player player = e.getPlayer();
                            // Set Level Victim
                            Double victimlevel = 1.0;
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                victimlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                            }
                            // Set Level Attacker
                            Double attackerlevel = 1.0;
                            attackerlevel = Double.valueOf(PlayerData.get(player).getLevel());
                            // ==================== Get MutiDef ====================
                            Double defignore = PlayerData.get(player).getStats().getMap().getStat("DEF_IGNORE");
                            Double defreduce = PlayerData.get(player).getStats().getMap().getStat("DEF_REDUCTION");
                            Double MutiDef = 1.0;
                            //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                            MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                            //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                            // ================ Get MutiBouns ====================
                            String element = e.getPlayer().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                            String element2 = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING);
                            Double AllElement = PlayerData.get(player).getStats().getMap().getStat("XDH_ALLELEMENT_BONUS");
                            Double ElementBonus = PlayerData.get(player).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_BONUS") + PlayerData.get(player).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_BONUS");
                            Double MutiBonus = cfload.getMutiBonusExp().setVariable("x",ElementBonus).setVariable("y",AllElement).evaluate();
                            // ================= Get MutiRes ====================
                            Double Restotal = 0.0;
                            // Get Damage Type
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()) != null) {
                                    Restotal = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element.toUpperCase()).get().toString());
                                }
                                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element2.toUpperCase()) != null) {
                                    Restotal = Restotal + Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Resistance"+element2.toUpperCase()).get().toString());
                                }
                            }
                            // Calculate MutiRes
                            Double MutiRes = 1.0;
                            if (Restotal < 0.0) {
                                //MutiRes = 1 - (Restotal/2);
                                MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal <= 0.75) {
                                MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal > 0.75) {
                                MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                            }
                            // ================= Get Muti DMG Reduction ====================
                            Double def = cfload.getDefenseEXP().setVariable("x", victimlevel).evaluate();
                            if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_blizzard"), PersistentDataType.STRING)){
                                def = def - (def * 0.10);
                            }
                            Double DMGReduction = 1.0;
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                if (mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction") != null) {
                                    DMGReduction = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("DMGReduction").get().toString());
                                }
                            }
                            //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                            Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                            // ================= Level Diff ====================
                            Double damgetotal = e.getDamage().getDamage() * MutiDef * MutiBonus * MutiDMGReduction * MutiRes;
                            //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage().getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                            //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                            Double mutipy = 0.0;
                            Double finaldmg = 0.0;
                            if (attackerlevel >= victimlevel) {
                                mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            } else {
                                mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            }
                            if (finaldmg < 1) {
                                finaldmg = 1.0;
                            }
                            // Get MultReaction
                            Double MultReaction = reactionCalculate.DamageCal(e.getPlayer(), reactiontype, e.getEntity());
                            finaldmg = finaldmg * MultReaction;
                            // ============ Deal Damage ===================
                            e.getAttack().getDamage().multiplicativeModifier(0);
                            e.getAttack().getDamage().add(finaldmg, e.getDamage().collectTypes().toArray(new DamageType[0]));
                            e.getPlayer().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                        }
                        if (reactiontype.contains("TRANSFORMATIVE")){
                            e.setCancelled(true);
                            Double MultReaction = reactionCalculate.DamageCal(e.getPlayer(), reactiontype, e.getEntity());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onDamaged(EntityDamageByEntityEvent e){
        // Check Npc
        if (e.getDamager() instanceof Player || e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            if (victim.hasMetadata("NPC")) {
                return;
            }
            if (e.getDamager().hasMetadata("NPC")){
                return;
            }
        }
        configload cfload = new configload();
        BukkitAPIHelper mythicMobsAPI = new BukkitAPIHelper();
        //========================== Dodge ======================
        if (e.getEntity() instanceof Player){
            //Calcuate Accuracy
            Double Accuracy = 0.0;
            Double HiddenAccuracy = 0.0;
            Double AccuracyLevel = 1.0;
            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                if (mythicMobsAPI.getMythicMobInstance(e.getDamager()).getVariables().get("Accuracy") != null) {
                    Accuracy = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("Accuracy").get().toString());
                }
                if (mythicMobsAPI.getMythicMobInstance(e.getDamager()).getVariables().get("HiddenAccuracy") != null) {
                    HiddenAccuracy = Double.valueOf(mythicMobsAPI.getMythicMobInstance(e.getEntity()).getVariables().get("HiddenAccuracy").get().toString());
                }
                AccuracyLevel = mythicMobsAPI.getMythicMobInstance(e.getDamager()).getLevel();
            }
            Double AccuracyTotal = Accuracy + HiddenAccuracy * 0.1102 * (Math.pow(1.12,AccuracyLevel/30));
            //Calcuate Evasion
            Double Evasion = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("EVASION");
            Double HiddenEvasion = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("HIDE_EVASION");
            Double EvasionLevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
            Double EvasionTotal = Evasion + HiddenEvasion * 0.1102 * (Math.pow(1.12,EvasionLevel/30));
            //Calculate HitRate
            Double HitRate = 0.2624 * AccuracyTotal - 0.2101 * EvasionTotal + 60.713;
            //Check HitRate
            if (HitRate < cfload.getMinHitRate()){
                HitRate = cfload.getMinHitRate();
            }else if (HitRate > cfload.getMaxHitRate()){
                HitRate = cfload.getMaxHitRate();
            }
            //Random Hit Rate
            Random random = new Random();
            Integer randomnum = random.nextInt(99) + 1;
            //If Miss
            if (randomnum > HitRate){
                e.setCancelled(true);
                if (e.getDamager().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING)) {
                    e.getDamager().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                }
                return;
                //e.get().playSound(e.getPlayer().getLocation(), Sound.valueOf(cfload.getMissSound()), 1, 1);
            }
        }
        // Check is player ?
        if (!(e.getDamager() instanceof Player)) {
            // Check is arrow or not.
            if (e.getDamager() instanceof Arrow){
                Arrow arrow = (Arrow)e.getDamager();
                // Shoot from player?
                if (arrow.getShooter() instanceof Player){
                    return;
                }
            }
            if (e.getEntity() instanceof Player) {
                // ================ Check Element ================
                if (!(e.getDamager().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING))) {
                    // Set Level Victim
                    Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                    // Set Level Attacker
                    Double attackerlevel = 1.0;
                    if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                        attackerlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                    }
                    // ==================== Get MutiDef ====================
                    Double defignore = cfload.GetMDEFIgnore().setVariable("x", attackerlevel).evaluate();
                    Double defreduce = cfload.GetMDEFReduct().setVariable("x", attackerlevel).evaluate();
                    Double MutiDef = 1.0;
                    //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                    MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                    //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                    // ================= Get MutiRes ====================
                    Double Restotal = 0.0;
                    // Get Damage Type
                    if (e.getDamager().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_damagetype"), PersistentDataType.STRING)) {
                        String damagetypedata = e.getDamager().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_damagetype"), PersistentDataType.STRING);
                        if (damagetypedata.equalsIgnoreCase("MAGIC")) {
                            Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("MAGICAL_RES");
                        } else if (damagetypedata.equalsIgnoreCase("PHYSICAL")) {
                            Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("PHYSICAL_RES");
                        } else {
                            Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("PVE_RES");
                        }
                        e.getDamager().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_damagetype"));
                    } else {
                        Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("PVE_RES");
                    }
                    // Calculate MutiRes
                    Double MutiRes = 1.0;
                    if (Restotal < 0.0) {
                        //MutiRes = 1 - (Restotal/2);
                        MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                    } else if (Restotal <= 0.75) {
                        MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                    } else if (Restotal > 0.75) {
                        MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                    }
                    // ================= Get Muti DMG Reduction ====================
                    //Double def = victimlevel*7+150*(victimlevel/10);
                    Double def = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE");
                    Double DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                    //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                    Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                    if (MutiDMGReduction == 0) {
                        MutiDMGReduction = 1.0;
                    }
                    // ================= Level Diff ====================
                    DecimalFormat format = new DecimalFormat("0.00");
                    MutiDef = Double.valueOf(format.format(MutiDef));
                    MutiRes = Double.valueOf(format.format(MutiRes));
                    MutiDMGReduction = Double.valueOf(format.format(MutiDMGReduction));
                    Double damgetotal = e.getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                    //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                    //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                    Double mutipy = 0.0;
                    Double finaldmg = 0.0;
                    if (attackerlevel >= victimlevel) {
                        mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                        finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                    } else {
                        mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                        finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                    }
                    if (finaldmg < 1) {
                        finaldmg = 1.0;
                    }
                    // ============ Deal Damage ===================
                    if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)){
                        Double Shield = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE);
                        finaldmg = finaldmg * 1/4;
                        if (Shield - finaldmg <= 0){
                            // Send Message
                            String msg = cfload.getElementShieldRemoveMSG().replace("{x}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
                            msg = msg.replace("{y}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                            e.getEntity().sendMessage(msg);
                            // Send Message
                            e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
                            e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
                            e.setCancelled(true);
                        }else{
                            e.getEntity().getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Shield-finaldmg);
                            e.setCancelled(true);
                        }
                    }else {
                        e.setDamage(finaldmg);
                    }
                    //pl.getServer().getConsoleSender().sendMessage(String.valueOf(finaldmg));
                }else{
                    // Check Victim have Element or not?
                    if (!(e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING)) || e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
                        // Set Level Victim
                        Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                        // Set Level Attacker
                        Double attackerlevel = 1.0;
                        if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                            attackerlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                        }
                        // ==================== Get MutiDef ====================
                        Double defignore = cfload.GetMDEFIgnore().setVariable("x", attackerlevel).evaluate();
                        Double defreduce = cfload.GetMDEFReduct().setVariable("x", attackerlevel).evaluate();
                        Double MutiDef = 1.0;
                        //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                        MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                        // ================= Get MutiRes ====================
                        String element = e.getDamager().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                        Double Restotal = 0.0;
                        // Get Damage Type
                        Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES");
                        // Calculate MutiRes
                        Double MutiRes = 1.0;
                        if (Restotal < 0.0) {
                            //MutiRes = 1 - (Restotal/2);
                            MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal <= 0.75) {
                            MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                        } else if (Restotal > 0.75) {
                            MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                        }
                        // ================= Get Muti DMG Reduction ====================
                        //Double def = victimlevel*7+150*(victimlevel/10);
                        Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                        Double DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                        //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                        Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                        if (MutiDMGReduction == 0) {
                            MutiDMGReduction = 1.0;
                        }
                        // ================= Level Diff ====================
                        DecimalFormat format = new DecimalFormat("0.00");
                        MutiDef = Double.valueOf(format.format(MutiDef));
                        MutiRes = Double.valueOf(format.format(MutiRes));
                        MutiDMGReduction = Double.valueOf(format.format(MutiDMGReduction));
                        Double damgetotal = e.getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                        //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                        //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                        Double mutipy = 0.0;
                        Double finaldmg = 0.0;
                        if (attackerlevel >= victimlevel) {
                            mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        } else {
                            mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                            finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                        }
                        if (finaldmg < 1) {
                            finaldmg = 1.0;
                        }
                        // ============ Deal Damage ===================
                        if (e.getEntity().getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
                            Double Shield = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE);
                            //Check element Type
                            if (e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING).equalsIgnoreCase(element)){
                                finaldmg = finaldmg * 1/3;
                            }
                            if (Shield - finaldmg <= 0){
                                // Send Message
                                String msg = cfload.getElementShieldRemoveMSG().replace("{x}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING));
                                msg = msg.replace("{y}", e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE).toString());
                                e.getEntity().sendMessage(msg);
                                // Send Message
                                e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
                                e.getEntity().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
                                e.setCancelled(true);
                            }else{
                                e.getEntity().getPersistentDataContainer().set(new NamespacedKey(pl, "xdh_elementshieldA"), PersistentDataType.DOUBLE, Shield-finaldmg);
                                e.setCancelled(true);
                            }
                        }else {
                            e.setDamage(finaldmg);
                            variablecontrol variablecontrol = new variablecontrol();
                            variablecontrol.AddElementToTarget(e.getEntity(),element);
                        }
                        e.getDamager().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                        //pl.getServer().getConsoleSender().sendMessage(String.valueOf(finaldmg));
                    }else{ // Go Reaction
                        ReactionCalculate reactionCalculate = new ReactionCalculate();
                        String reactiontype = reactionCalculate.CheckReactionType(e.getDamager().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING), e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING));
                        //Check It's will have reaction?
                        if (reactiontype == null){
                            // Set Level Victim
                            Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                            // Set Level Attacker
                            Double attackerlevel = 1.0;
                            if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                attackerlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                            }
                            // ==================== Get MutiDef ====================
                            Double defignore = cfload.GetMDEFIgnore().setVariable("x", attackerlevel).evaluate();
                            Double defreduce = cfload.GetMDEFReduct().setVariable("x", attackerlevel).evaluate();
                            Double MutiDef = 1.0;
                            //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                            MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                            //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                            // ================= Get MutiRes ====================
                            String element = e.getDamager().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                            Double Restotal = 0.0;
                            // Get Damage Type
                            Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES");
                            // Calculate MutiRes
                            Double MutiRes = 1.0;
                            if (Restotal < 0.0) {
                                //MutiRes = 1 - (Restotal/2);
                                MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal <= 0.75) {
                                MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                            } else if (Restotal > 0.75) {
                                MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                            }
                            // ================= Get Muti DMG Reduction ====================
                            //Double def = victimlevel*7+150*(victimlevel/10);
                            Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                            Double DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                            //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                            Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                            if (MutiDMGReduction == 0) {
                                MutiDMGReduction = 1.0;
                            }
                            // ================= Level Diff ====================
                            DecimalFormat format = new DecimalFormat("0.00");
                            MutiDef = Double.valueOf(format.format(MutiDef));
                            MutiRes = Double.valueOf(format.format(MutiRes));
                            MutiDMGReduction = Double.valueOf(format.format(MutiDMGReduction));
                            Double damgetotal = e.getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                            //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                            //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                            Double mutipy = 0.0;
                            Double finaldmg = 0.0;
                            if (attackerlevel >= victimlevel) {
                                mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            } else {
                                mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                            }
                            if (finaldmg < 1) {
                                finaldmg = 1.0;
                            }
                            // ============ Deal Damage ===================
                            e.setDamage(finaldmg);
                            variablecontrol variablecontrol = new variablecontrol();
                            variablecontrol.AddElementToTarget(e.getEntity(),element);
                            e.getDamager().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                        }else{
                            //================ Check Reaction Type =========================
                            if (reactiontype.contains("AMPLIFYING")){
                                // Set Level Victim
                                Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                                // Set Level Attacker
                                Double attackerlevel = 1.0;
                                if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                    attackerlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                                }
                                // ==================== Get MutiDef ====================
                                Double defignore = cfload.GetMDEFIgnore().setVariable("x", attackerlevel).evaluate();
                                Double defreduce = cfload.GetMDEFReduct().setVariable("x", attackerlevel).evaluate();
                                Double MutiDef = 1.0;
                                //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                                MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                                //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                                // ================= Get MutiRes ====================
                                String element = e.getDamager().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                                String element2 = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING);
                                Double Restotal = 0.0;
                                // Get Damage Type
                                Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES") + PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_RES");
                                // Calculate MutiRes
                                Double MutiRes = 1.0;
                                if (Restotal < 0.0) {
                                    //MutiRes = 1 - (Restotal/2);
                                    MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                                } else if (Restotal <= 0.75) {
                                    MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                                } else if (Restotal > 0.75) {
                                    MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                                }
                                // ================= Get Muti DMG Reduction ====================
                                //Double def = victimlevel*7+150*(victimlevel/10);
                                Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                                Double DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                                //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                                Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                                if (MutiDMGReduction == 0) {
                                    MutiDMGReduction = 1.0;
                                }
                                // ================= Level Diff ====================
                                DecimalFormat format = new DecimalFormat("0.00");
                                MutiDef = Double.valueOf(format.format(MutiDef));
                                MutiRes = Double.valueOf(format.format(MutiRes));
                                MutiDMGReduction = Double.valueOf(format.format(MutiDMGReduction));
                                Double damgetotal = e.getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                                //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                                //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                                Double mutipy = 0.0;
                                Double finaldmg = 0.0;
                                if (attackerlevel >= victimlevel) {
                                    mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                    finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                                } else {
                                    mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                    finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                                }
                                if (finaldmg < 1) {
                                    finaldmg = 1.0;
                                }
                                // Get MultReaction
                                Double MultReaction = reactionCalculate.DamageCal(e.getEntity(), reactiontype, e.getEntity());
                                finaldmg = finaldmg * MultReaction;
                                e.getDamager().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                                // ============ Deal Damage ===================
                                e.setDamage(finaldmg);
                            }
                            // =============== CRYSTALLIZE ====================
                            if (reactiontype.contains("CRYSTALLIZE")){
                                // Set Level Victim
                                Double victimlevel = Double.valueOf(PlayerData.get((Player) e.getEntity()).getLevel());
                                // Set Level Attacker
                                Double attackerlevel = 1.0;
                                if (mythicMobsAPI.isMythicMob(e.getEntity())) {
                                    attackerlevel = mythicMobsAPI.getMythicMobInstance(e.getEntity()).getLevel();
                                }
                                // ==================== Get MutiDef ====================
                                Double defignore = cfload.GetMDEFIgnore().setVariable("x", attackerlevel).evaluate();
                                Double defreduce = cfload.GetMDEFReduct().setVariable("x", attackerlevel).evaluate();
                                Double MutiDef = 1.0;
                                //MutiDef = (attackerlevel + 100)/((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80);
                                MutiDef = cfload.getMutiDefExp().setVariable("x", attackerlevel).setVariable("y", victimlevel).setVariable("z", defignore).setVariable("a", defreduce).evaluate();
                                //pl.getServer().getConsoleSender().sendMessage(String.valueOf((1-defignore)*(1-defreduce)*(victimlevel+100)+attackerlevel+80));
                                // ================= Get MutiRes ====================
                                String element = e.getDamager().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING);
                                String element2 = e.getEntity().getPersistentDataContainer().get(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING);
                                Double Restotal = 0.0;
                                // Get Damage Type
                                Restotal = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element.toUpperCase()+"_RES") + PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_"+element2.toUpperCase()+"_RES");
                                // Calculate MutiRes
                                Double MutiRes = 1.0;
                                if (Restotal < 0.0) {
                                    //MutiRes = 1 - (Restotal/2);
                                    MutiRes = cfload.getMutiRes1ExP().setVariable("x", Restotal).evaluate();
                                } else if (Restotal <= 0.75) {
                                    MutiRes = cfload.getMutiRes2ExP().setVariable("x", Restotal).evaluate();
                                } else if (Restotal > 0.75) {
                                    MutiRes = cfload.getMutiRes3ExP().setVariable("x", Restotal).evaluate();
                                }
                                // ================= Get Muti DMG Reduction ====================
                                //Double def = victimlevel*7+150*(victimlevel/10);
                                Double def = cfload.getDefFormular().setVariable("x",PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("XDH_DEFENSE")).setVariable("y",victimlevel).evaluate();
                                Double DMGReduction = PlayerData.get((Player) e.getEntity()).getStats().getMap().getStat("DMG_REDUCTION");
                                //Double MutiDMGReduction = def/(def + (1-DMGReduction) * (victimlevel+100));
                                Double MutiDMGReduction = cfload.getMutiDMGReductionExP().setVariable("x", def).setVariable("y", DMGReduction).setVariable("z", victimlevel).evaluate();
                                if (MutiDMGReduction == 0) {
                                    MutiDMGReduction = 1.0;
                                }
                                // ================= Level Diff ====================
                                DecimalFormat format = new DecimalFormat("0.00");
                                MutiDef = Double.valueOf(format.format(MutiDef));
                                MutiRes = Double.valueOf(format.format(MutiRes));
                                MutiDMGReduction = Double.valueOf(format.format(MutiDMGReduction));
                                Double damgetotal = e.getDamage() * MutiDef * MutiRes * MutiDMGReduction;
                                //pl.getServer().getConsoleSender().sendMessage(""+e.getDamage()," * ",""+MutiDef," * ",""+MutiRes+" * "+""+MutiDMGReduction);
                                //pl.getServer().getConsoleSender().sendMessage(""+damgetotal);
                                Double mutipy = 0.0;
                                Double finaldmg = 0.0;
                                if (attackerlevel >= victimlevel) {
                                    mutipy = cfload.getMultipyLevelHigher().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                    finaldmg = cfload.getLevelHigher().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                                } else {
                                    mutipy = cfload.getMultipyLevelLower().setVariable("x", attackerlevel).setVariable("y", victimlevel).evaluate();
                                    finaldmg = cfload.getLevelLower().setVariable("x", damgetotal).setVariable("y", mutipy).setVariable("z", def).evaluate();
                                }
                                if (finaldmg < 1) {
                                    finaldmg = 1.0;
                                }
                                // Get MultReaction
                                Double MultReaction = reactionCalculate.DamageCal(e.getEntity(), reactiontype, e.getEntity());
                                finaldmg = finaldmg * MultReaction;
                                e.getDamager().getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
                                // ============ Deal Damage ===================
                                e.setDamage(finaldmg);
                            }
                            if (reactiontype.contains("TRANSFORMATIVE")){
                                e.setCancelled(true);
                                Double MultReaction = reactionCalculate.DamageCal(e.getDamager(), reactiontype, e.getEntity());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if(event.getMechanicName().equalsIgnoreCase("XentellaDamage"))	{
            event.register(new XentellaDamage(event.getConfig()));
        }
        if(event.getMechanicName().equalsIgnoreCase("ElementShield"))	{
            event.register(new ElementShield(event.getConfig()));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        // Remove Element
        if (player.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_element"), PersistentDataType.STRING)) {
            player.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_element"));
        }
        if (player.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementvictim"), PersistentDataType.STRING)) {
            player.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementvictim"));
        }
        // Remove Shield
        if (player.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
            player.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshield"));
            player.getPersistentDataContainer().remove(new NamespacedKey(pl, "xdh_elementshieldA"));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if (player.getPersistentDataContainer().has(new NamespacedKey(pl, "xdh_elementshield"), PersistentDataType.STRING)) {
            pl.getTimemanager().RemoveEntityFromTime(player);
        }
        if (player.isGlowing()){
            player.setGlowing(false);
        }
    }

    // Shield Pickup
    @EventHandler
    public void onPickupShield(PlayerPickupItemEvent e){
        if (e.getItem().getPersistentDataContainer().has(new NamespacedKey(pl, "EShield"), PersistentDataType.STRING)){
            variablecontrol vc = new variablecontrol();
            vc.AddShield(e.getPlayer(), e.getItem().getPersistentDataContainer().get(new NamespacedKey(pl, "EShield"), PersistentDataType.STRING), e.getItem().getPersistentDataContainer().get(new NamespacedKey(pl, "EShieldA"), PersistentDataType.DOUBLE), e.getItem().getPersistentDataContainer().get(new NamespacedKey(pl, "EShieldT"), PersistentDataType.LONG));
            e.setCancelled(true);
            e.getItem().remove();
        }
    }
}
