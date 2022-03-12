package lnwwaveza008.xentella_damage_handle.event;

import com.google.errorprone.annotations.Var;
import com.sun.org.apache.xpath.internal.operations.Mult;
import lnwwaveza008.xentella_damage_handle.Xentella_Damage_Handle;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.ChatColor;

public class configload {
    private static Xentella_Damage_Handle pl = Xentella_Damage_Handle.getPlugin(Xentella_Damage_Handle.class);
    public static Long ElementDelay;
    public static Double MaxHitRate;
    public static Double MinHitRate;
    public static String MissSound;
    public static Expression MDEFIgnore;
    public static Expression MDEFReduct;
    public static Expression MDMGReduction;
    public static Expression MultipyLevelHigher;
    public static Expression MultipyLevelLower;
    public static Expression LevelHigher;
    public static Expression LevelLower;
    public static Expression MutiDefExp;
    public static Expression MutiDMGReductionExP;
    public static Expression MutiRes1ExP;
    public static Expression MutiRes2ExP;
    public static Expression MutiRes3ExP;
    public static Expression DefenseEXP;
    public static Expression MutiBonusExp;
    public static Expression DefFormular;
    // For Element
    public static Double AmplifyingK;
    public static Double AmplifyingC;
    public static Double CrystallizeK;
    public static Double CrystallizeC;
    public static Double TransformativeC;
    public static Double TransformativeK;
    public static Expression EMBonusFormular;
    public static Expression MultAmplifyingFormular;
    public static Expression CrystallizeShieldFormular;
    public static Expression CCFormular;
    public static Expression EmEXP;
    public static Expression MultLevel;
    public static Expression MultTransformative;
    public static String ElementShieldReciveMSG;
    public static String ElementShieldRemoveMSG;
    public static String DustReaction;
    public static String FrozenReaction;
    public static String MudReaction;
    public static String BlizzardReaction;
    public static String MeltReaction;
    public static String VaporizeReaction;
    public static String ElectroCharged;
    public static String BurnReaction;
    public static String PoisonReaction;
    public static String MagmaReaction;
    public static String OverloadedReaction;

    public Expression GetMDEFIgnore(){
        return MDEFIgnore;
    }

    public Expression GetMDEFReduct(){
        return MDEFReduct;
    }

    public Expression GetMDMGReduction(){
        return MDMGReduction;
    }

    public Expression getMultipyLevelHigher(){
        return MultipyLevelHigher;
    }

    public Expression getMultipyLevelLower(){
        return MultipyLevelLower;
    }

    public Expression getLevelHigher(){
        return LevelHigher;
    }

    public Expression getLevelLower(){
        return LevelLower;
    }

    public Expression getMutiDefExp(){
        return MutiDefExp;
    }

    public Expression getMutiDMGReductionExP(){
        return MutiDMGReductionExP;
    }

    public Expression getMutiRes1ExP(){
        return MutiRes1ExP;
    }

    public Expression getMutiRes2ExP(){
        return MutiRes2ExP;
    }

    public Expression getMutiRes3ExP(){
        return MutiRes3ExP;
    }

    public Expression getDefenseEXP(){
        return DefenseEXP;
    }

    public Expression getMutiBonusExp(){
        return MutiBonusExp;
    }
    public Expression getDefFormular(){
        return DefFormular;
    }

    public Double getMaxHitRate(){ return MaxHitRate; }
    public Double getMinHitRate(){ return MinHitRate; }
    public Long getElementDelay(){ return ElementDelay; }
    public String getMissSound(){ return MissSound; }
    // Element
    public Expression getEMBonusFormular(){ return EMBonusFormular; }
    public Expression getMultAmplifyingFormular(){ return MultAmplifyingFormular; }
    public Expression getCrystallizeShieldFormular(){ return CrystallizeShieldFormular; }
    public Expression getCCFormular(){ return CCFormular; }
    public Expression getEmEXP(){ return EmEXP; }
    public Expression getMultLevel(){ return MultLevel; }
    public Expression getMultTransformative(){ return MultTransformative; }
    public Double getAmplifyingK(){ return AmplifyingK; }
    public Double getAmplifyingC(){ return AmplifyingC; }
    public Double getCrystallizeK(){ return CrystallizeK; }
    public Double getCrystallizeC(){ return CrystallizeC; }
    public Double getTransformativeK(){ return CrystallizeK; }
    public Double getTransformativeC(){ return CrystallizeC; }
    public String getElementShieldReciveMSG(){ return ElementShieldReciveMSG; }
    public String getElementShieldRemoveMSG(){ return ElementShieldRemoveMSG; }
    public String getDustReaction(){ return DustReaction; }
    public String getFrozenReaction(){ return FrozenReaction; }
    public String getMudReaction(){ return MudReaction; }
    public String getBlizzardReaction(){ return BlizzardReaction; }
    public String getMeltReaction(){ return MeltReaction; }
    public String getVaporizeReaction(){ return VaporizeReaction; }
    public String getElectroCharged(){ return ElectroCharged; }
    public String getBurnReaction(){ return BurnReaction; }
    public String getPoisonReaction(){ return PoisonReaction; }
    public String getMagmaReaction(){ return MagmaReaction; }
    public String getOverloadedReaction(){ return OverloadedReaction; }

    public void onReload(){
        MaxHitRate = pl.getConfig().getDouble("HitRate.Max");
        MinHitRate = pl.getConfig().getDouble("HitRate.Min");
        MissSound = pl.getConfig().getString("HitRate.MissSound");
        ElementDelay = pl.getConfig().getLong("Element.Delay");
        // EXP
        MDEFIgnore = new ExpressionBuilder(pl.getConfig().getString("MonsterFormula.Variable.DEFIgnore")).variables("x").build();
        MDEFReduct = new ExpressionBuilder(pl.getConfig().getString("MonsterFormula.Variable.DEFReduct")).variables("x").build();
        MutiRes1ExP = new ExpressionBuilder(pl.getConfig().getString("Formular.MutiRes1")).variables("x").build();
        MutiRes2ExP = new ExpressionBuilder(pl.getConfig().getString("Formular.MutiRes2")).variables("x").build();
        MutiRes3ExP = new ExpressionBuilder(pl.getConfig().getString("Formular.MutiRes3")).variables("x").build();
        DefenseEXP = new ExpressionBuilder(pl.getConfig().getString("MonsterFormula.Variable.Defense")).variables("x").build();
        MutiBonusExp = new ExpressionBuilder(pl.getConfig().getString("Formular.MutiBonus")).variables("x","y").build();
        DefFormular = new ExpressionBuilder(pl.getConfig().getString("Formular.DefFormular")).variables("x","y").build();
        MDMGReduction = new ExpressionBuilder(pl.getConfig().getString("MonsterFormula.Variable.DMGReduction")).variables("x","y").build();
        MultipyLevelHigher = new ExpressionBuilder(pl.getConfig().getString("LevelDiff.MultipyLevelHigher")).variables("x","y").build();
        MultipyLevelLower = new ExpressionBuilder(pl.getConfig().getString("LevelDiff.MultipyLevelLower")).variables("x","y").build();
        LevelHigher = new ExpressionBuilder(pl.getConfig().getString("LevelDiff.LevelHigher")).variables("x","y","z").build();
        LevelLower = new ExpressionBuilder(pl.getConfig().getString("LevelDiff.LevelLower")).variables("x","y","z").build();
        MutiDefExp = new ExpressionBuilder(pl.getConfig().getString("Formular.MutiDef")).variables("x","y","z","a").build();
        MutiDMGReductionExP = new ExpressionBuilder(pl.getConfig().getString("Formular.MutiDMGReduction")).variables("x","y","z").build();
        // Element
        EMBonusFormular = new ExpressionBuilder(pl.getConfig().getString("Element.EMBonusFormular")).variables("x","k","c").build();
        MultAmplifyingFormular = new ExpressionBuilder(pl.getConfig().getString("Element.MultAmplifyingFormular")).variables("x","y","z").build();
        CrystallizeShieldFormular = new ExpressionBuilder(pl.getConfig().getString("Element.CrystallizeShieldFormular")).variables("x","y","z").build();
        CCFormular = new ExpressionBuilder(pl.getConfig().getString("Element.CrystallizeReaction.CCFormular")).variables("x","y").build();
        MultLevel = new ExpressionBuilder(pl.getConfig().getString("Element.MultLevel")).variables("x").build();
        MultTransformative = new ExpressionBuilder(pl.getConfig().getString("Element.MultTransformative")).variables("x","y","z","a").build();
        AmplifyingC = pl.getConfig().getDouble("Element.AmplifyingC");
        AmplifyingK = pl.getConfig().getDouble("Element.AmplifyingK");
        CrystallizeC = pl.getConfig().getDouble("Element.CrystallizeC");
        CrystallizeK = pl.getConfig().getDouble("Element.CrystallizeK");
        TransformativeC = pl.getConfig().getDouble("Element.TransformativeC");
        TransformativeK = pl.getConfig().getDouble("Element.TransformativeK");
        EmEXP = new ExpressionBuilder(pl.getConfig().getString("MonsterFormula.Variable.EM")).variables("x").build();
        ElementShieldReciveMSG = ChatColor.translateAlternateColorCodes('&',pl.getConfig().getString("Element.ElementShieldRecive"));
        ElementShieldRemoveMSG = ChatColor.translateAlternateColorCodes('&',pl.getConfig().getString("Element.ElementShieldRemove"));
        DustReaction = pl.getConfig().getString("Element.CrystallizeReaction.DustReaction");
        FrozenReaction = pl.getConfig().getString("Element.CrystallizeReaction.FrozenReaction");
        MudReaction = pl.getConfig().getString("Element.CrystallizeReaction.MudReaction");
        BlizzardReaction = pl.getConfig().getString("Element.CrystallizeReaction.BlizzardReaction");
        MeltReaction = pl.getConfig().getString("Element.AmplifyingReaction.Melt");
        VaporizeReaction = pl.getConfig().getString("Element.AmplifyingReaction.Vaporize");
        ElectroCharged = pl.getConfig().getString("Element.TransformativeReaction.ElectroCharged");
        BurnReaction = pl.getConfig().getString("Element.TransformativeReaction.Burn");
        PoisonReaction = pl.getConfig().getString("Element.TransformativeReaction.Poison");
        MagmaReaction = pl.getConfig().getString("Element.TransformativeReaction.Magma");
        OverloadedReaction = pl.getConfig().getString("Element.TransformativeReaction.Overloaded");
    }
}
