package com.robertx22.mine_and_slash.data_generation.unique_gears;

import com.robertx22.mine_and_slash.database.data.StatModifier;
import com.robertx22.mine_and_slash.database.data.gearitemslots.bases.BaseGearType;
import com.robertx22.mine_and_slash.database.data.unique_items.IUnique;
import com.robertx22.exiled_lib.registry.SlashRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SerializableUniqueGear implements IUnique {

    List<StatModifier> uniqueStats;
    int tier;
    int weight;

    String guid;
    String langNameID;
    String langDescID;
    String gearType;
    ResourceLocation itemID;

    public SerializableUniqueGear(List<StatModifier> uniqueStats, int tier, int weight, String guid, String langNameID, String langDescID, String gearType, ResourceLocation itemID) {
        this.uniqueStats = uniqueStats;
        this.tier = tier;
        this.weight = weight;
        this.guid = guid;
        this.langNameID = langNameID.contains("item.") ? langNameID : "item." + langNameID;
        this.langDescID = langDescID.contains("item.") ? langDescID : "item." + langDescID;
        this.gearType = gearType;
        this.itemID = itemID;
    }

    @Override
    public ResourceLocation getResourceLocForItem() {
        return itemID;
    }

    @Override
    public Item getUniqueItem() {
        return ForgeRegistries.ITEMS.getValue(itemID);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public List<StatModifier> uniqueStats() {
        return this.uniqueStats;
    }

    @Override
    public String locDescForLangFile() {
        return "";
    }

    @Override
    public String locNameForLangFile() {
        return "";
    }

    @Override
    public String locDescLangFileGUID() {
        return langDescID;
    }

    @Override
    public String locNameLangFileGUID() {
        return langNameID;
    }

    @Override
    public String GUID() {
        return guid;
    }

    @Override
    public BaseGearType getBaseGearType() {
        return SlashRegistry.GearTypes()
            .get(gearType);
    }
}
