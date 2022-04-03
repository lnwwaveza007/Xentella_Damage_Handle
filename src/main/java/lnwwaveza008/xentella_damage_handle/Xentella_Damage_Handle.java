package lnwwaveza008.xentella_damage_handle;

import lnwwaveza008.xentella_damage_handle.api.*;
import lnwwaveza008.xentella_damage_handle.api.Element.*;
import lnwwaveza008.xentella_damage_handle.api.ElementRes.*;
import lnwwaveza008.xentella_damage_handle.api.Misc.*;
import lnwwaveza008.xentella_damage_handle.api.Reaction.*;
import lnwwaveza008.xentella_damage_handle.commands.command;
import lnwwaveza008.xentella_damage_handle.event.TimeManager;
import lnwwaveza008.xentella_damage_handle.event.configload;
import lnwwaveza008.xentella_damage_handle.event.damage;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public final class Xentella_Damage_Handle extends JavaPlugin {

    TimeManager timemanager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "=====================================================");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "                XDH just started!");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "                  Version : 1.0");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "=====================================================");
        // Commands
        getCommand("XentellaDamageHandle").setExecutor(new command());
        // Listener
        getServer().getPluginManager().registerEvents(new damage(), this);
        // MMOitems
        MMOItems.plugin.getStats().register(new DefIgnore("DEF_IGNORE", Material.SHIELD, "Defense Ignore", new String[]{"Defense Ignore","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new DefReduction("DEF_REDUCTION", Material.SHIELD, "Defense Reduction", new String[]{"Defense Reduction","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new PhysicalRes("PHYSICAL_RES", Material.IRON_SWORD, "Physical Resistance", new String[]{"Physical Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new MagicalRes("MAGICAL_RES", Material.POTION, "Magical Resistance", new String[]{"Magical Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new DamageReduction("DMG_REDUCTION", Material.IRON_CHESTPLATE, "Damage Reduction", new String[]{"Damage Reduction","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new PvERes("PVE_RES", Material.IRON_CHESTPLATE, "Pve Resistance", new String[]{"Pve Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new Evasion("EVASION", Material.LEATHER_BOOTS, "Evasion", new String[]{"Evasion","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new HiddenEvasion("HIDE_EVASION", Material.LEATHER_BOOTS, "Hidden Evasion", new String[]{"Hidden Evasion","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new Accuracy("ACCURACY", Material.BOW, "Accuracy", new String[]{"Accuracy","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new HiddenAccuracy("HIDE_ACCURACY", Material.BOW, "Hidden Accuracy", new String[]{"Hidden Accuracy","","Use for : Xentella Damage Handler"}));
        // Element Damage Bonus
        MMOItems.plugin.getStats().register(new AllElementBonus("XDH_ALLELEMENT_BONUS", Material.PUFFERFISH, "All Elements Damage Bonus", new String[]{"All Elements Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new AnemoBonus("XDH_ANEMO_BONUS", Material.FEATHER, "Anemo Damage Bonus", new String[]{"Anemo Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new CryoBonus("XDH_CYRO_BONUS", Material.ICE, "Cyro Damage Bonus", new String[]{"Cyro Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new DendroBonus("XDH_DENDRO_BONUS", Material.OAK_LEAVES, "Dendro Damage Bonus", new String[]{"Dendro Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ElectroBonus("XDH_ELECTRO_BONUS", Material.IRON_BLOCK, "Electro Damage Bonus", new String[]{"Electro Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new GeoBonus("XDH_GEO_BONUS", Material.STONE, "Geo Damage Bonus", new String[]{"Geo Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new HydroBonus("XDH_HYDRO_BONUS", Material.WATER_BUCKET, "Hydro Damage Bonus", new String[]{"Hydro Damage Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new PyroBonus("XDH_PYRO_BONUS", Material.BLAZE_POWDER, "Pyro Damage Bonus", new String[]{"Pyro Damage Bonus","","Use for : Xentella Damage Handler"}));
        // Element Resistance
        MMOItems.plugin.getStats().register(new AnemoRes("XDH_ANEMO_RES", Material.FEATHER, "Anemo Resistance", new String[]{"Anemo Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new CryoRes("XDH_CRYO_RES", Material.ICE, "Cryo Resistance", new String[]{"Cryo Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new DendroRes("XDH_DENDRO_RES", Material.OAK_LEAVES, "Dendro Resistance", new String[]{"Dendro Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ElectroRes("XDH_ELECTRO_RES", Material.IRON_BLOCK, "Electro Resistance", new String[]{"Electro Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new GeoRes("XDH_GEO_RES", Material.STONE, "Geo Resistance", new String[]{"Geo Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new HydroRes("XDH_HYDRO_RES", Material.WATER_BUCKET, "Hydro Resistance", new String[]{"Hydro Resistance","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new PyroRes("XDH_PYRO_RES", Material.BLAZE_POWDER, "Pyro Resistance", new String[]{"Pyro Resistance","","Use for : Xentella Damage Handler"}));
        // Reaction
        MMOItems.plugin.getStats().register(new ElementalMastery("XDH_EM", Material.PUFFERFISH, "Elemental Mastery", new String[]{"Elemental Mastery","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new VaporizeBonus("XDH_VAPORIZE_BONUS", Material.WATER_BUCKET, "Vaporize Bonus", new String[]{"Vaporize Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new MeltBonus("XDH_MELT_BONUS", Material.LAVA_BUCKET, "Melt Bonus", new String[]{"Melt Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new FrozenBonus("XDH_FROZEN_BONUS", Material.PACKED_ICE, "Frozen Bonus", new String[]{"Frozen Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new DustBonus("XDH_DUST_BONUS", Material.SAND, "Dust Bonus", new String[]{"Dust Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new DustBonus("XDH_MUD_BONUS", Material.DIRT, "Mud Bonus", new String[]{"Mud Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new BlizzardBonus("XDH_BLIZZARD_BONUS", Material.SNOW, "Blizzard Bonus", new String[]{"Blizzard Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new CrystallizeShieldBonus("XDH_CRYSTALLIZE_SHIELD_BONUS", Material.SHIELD, "Crystallize Shield Bonus", new String[]{"Crystallize Shield Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ElectroChargedBonus("XDH_ELECTROCHARGED_BONUS", Material.GOLD_BLOCK, "Electro Charged Bonus", new String[]{"Electro Charged Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new BurnBonus("XDH_BURN_BONUS", Material.BLAZE_POWDER, "Burn Bonus", new String[]{"Burn Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new PoisonBonus("XDH_POISON_BONUS", Material.POTION, "Potion Bonus", new String[]{"Potion Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new MagmaBonus("XDH_MAGMA_BONUS", Material.MAGMA_BLOCK, "Magma Bonus", new String[]{"Magma Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new MagmaBonus("XDH_OVERLOADED_BONUS", Material.RED_CONCRETE, "Magma Bonus", new String[]{"Magma Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new SuperConductBonus("XDH_SUPERCONDUCT_BONUS", Material.ICE, "SuperConduct Bonus", new String[]{"SuperConduct Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new SwirlBonus("XDH_SWIRL_BONUS", Material.TNT, "Swirl Bonus", new String[]{"Swirl Bonus","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ThornBonus("XDH_THORN_BONUS", Material.COBWEB, "Thorn Bonus", new String[]{"Thorn Bonus","","Use for : Xentella Damage Handler"}));
        // Misc
        MMOItems.plugin.getStats().register(new ItemCoef("XDH_ITEMCOEF", Material.IRON_SWORD, "Item Coef", new String[]{"Item Coef","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ItemId("XDH_ITEMID", Material.IRON_SWORD, "Item Id", new String[]{"Item Id","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ItemTypeGroup("XDH_ITEMTYPEGROUP", Material.IRON_SWORD, "Item Type Group", new String[]{"Item Type Group","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new ItemTier("XDH_TIER", Material.IRON_SWORD, "Item Tier", new String[]{"Item Tier","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new SubStatsTier("XDH_SUBSTATSTIER", Material.IRON_SWORD, "Substats Tier", new String[]{"Substats Tier","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new MaxRealDurability("XDH_MAXREALDURABILITY", Material.IRON_SWORD, "Max Real Durability", new String[]{"Max Real Durability","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new RestoreMaxPercentDurability("XDH_RESTOREPERCENTMAXDURABILITY", Material.IRON_SWORD, "Restore Percent Max Durability", new String[]{"Restore Percent Max Durability","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new RestoreMaxDurability("XDH_RESTOREMAXDURABILITY", Material.IRON_SWORD, "Restore Max Durability", new String[]{"Restore Max Durability","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new LevelUpgrade("XDH_LEVELUPGRADE", Material.IRON_SWORD, "Level Upgrade", new String[]{"Level Upgrade","","Use for : Xentella Damage Handler"}));
        MMOItems.plugin.getStats().register(new EtherDriveBonus("XDH_ETHER_DRIVE_BONUS", Material.IRON_SWORD, "Ether Drive Bonus", new String[]{"Ether Drive Bonus","","Use for : Xentella Damage Handler"}));
        // Config
        this.getConfig().options().copyDefaults();
        saveDefaultConfig();
        configload cfload = new configload();
        cfload.onReload();
        // Scorebroad Setup
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        board.registerNewTeam("HYDROSHIELD").setColor(ChatColor.AQUA);
        board.registerNewTeam("CRYOSHIELD").setColor(ChatColor.WHITE);
        board.registerNewTeam("ELECTROSHIELD").setColor(ChatColor.DARK_PURPLE);
        board.registerNewTeam("PYROSHIELD").setColor(ChatColor.RED);
        board.registerNewTeam("GEOSHIELD").setColor(ChatColor.YELLOW);
        board.registerNewTeam("ANEMOSHIELD").setColor(ChatColor.GREEN);
        board.registerNewTeam("DENDROSHIELD").setColor(ChatColor.DARK_GREEN);
        // TimeManager
        timemanager = new TimeManager(this);
        // Placeholders
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Unregister Teams
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        board.getTeam("HYDROSHIELD").unregister();
        board.getTeam("CRYOSHIELD").unregister();
        board.getTeam("ELECTROSHIELD").unregister();
        board.getTeam("PYROSHIELD").unregister();
        board.getTeam("GEOSHIELD").unregister();
        board.getTeam("ANEMOSHIELD").unregister();
        board.getTeam("DENDROSHIELD").unregister();
        // Unregister Teams
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "=====================================================");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "                XDH just stopped!");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "                  Version : 1.0");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "=====================================================");
    }

    public TimeManager getTimemanager () {
        return timemanager;
    }
}
