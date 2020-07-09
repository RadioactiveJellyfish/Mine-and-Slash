package com.robertx22.mine_and_slash.saveclasses;

import com.google.gson.JsonObject;
import com.robertx22.mine_and_slash.database.StatModifier;
import com.robertx22.mine_and_slash.database.stats.Stat;
import com.robertx22.mine_and_slash.onevent.data_gen.ISerializable;
import com.robertx22.mine_and_slash.registry.SlashRegistry;
import com.robertx22.mine_and_slash.saveclasses.gearitem.BaseStatContainer;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_bases.TooltipInfo;
import com.robertx22.mine_and_slash.saveclasses.item_classes.tooltips.TooltipStatInfo;
import com.robertx22.mine_and_slash.uncommon.capability.entity.EntityCap;
import com.robertx22.mine_and_slash.uncommon.enumclasses.StatModTypes;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

@Storable
public class ExactStatData extends BaseStatContainer implements ISerializable<ExactStatData> {

    public static ExactStatData EMPTY = new ExactStatData();

    public ExactStatData() {

    }

    public ExactStatData(StatModifier mod, int percent) {

        this.first_val = (mod.firstMin + (mod.firstMax - mod.firstMin) * percent / 100);
        this.second_val = (mod.secondMin + (mod.secondMax - mod.secondMin) * percent / 100);

        this.type = mod.getModType();
        this.stat_id = mod.stat;
    }

    public ExactStatData(float value, StatModTypes type, String stat_id) {
        this.first_val = value;
        this.second_val = value;
        this.type = type;
        this.stat_id = stat_id;
    }

    public ExactStatData(float value, StatModTypes type, Stat stat) {
        this.first_val = value;
        this.second_val = value;

        this.type = type;
        this.stat_id = stat.GUID();
    }

    public ExactStatData(float v1, float v2, StatModTypes type, String stat) {
        this.first_val = v1;
        this.second_val = v2;

        this.type = type;
        this.stat_id = stat;
    }

    public ExactStatData(float value, Stat stat) {
        this.first_val = value;
        this.second_val = value;

        this.type = StatModTypes.Flat;
        this.stat_id = stat.GUID();
    }

    public void setValue(float val) {
        this.first_val = val;
        this.second_val = val;

    }

    @Store
    private float first_val = 0;
    @Store
    private float second_val = 0;
    @Store
    private StatModTypes type = StatModTypes.Flat;

    @Store
    private String stat_id = "";

    public float getFirstValue() {
        return first_val;
    }

    public float getSecondValue() {
        return second_val;
    }

    public StatModTypes getType() {
        return type;
    }

    public Stat getStat() {
        return SlashRegistry.Stats()
            .get(stat_id);
    }

    @Override
    public void applyStats(EntityCap.UnitData data) {
        data.getUnit()
            .getCreateStat(stat_id)
            .add(this);
    }

    @Override
    public List<ITextComponent> GetTooltipString(TooltipInfo info) {

        if (first_val == 0) {
            // return new ArrayList<>();
        }

        Stat stat = getStat();
        TooltipStatInfo statInfo = new TooltipStatInfo(this, info);
        return new ArrayList<>(stat.getTooltipList(statInfo));

    }

    @Override
    public JsonObject toJson() {

        JsonObject json = new JsonObject();

        json.addProperty("first_val", this.first_val);
        json.addProperty("second_val", this.second_val);
        json.addProperty("type", this.type.name());
        json.addProperty("stat", this.stat_id);

        return json;
    }

    @Override
    public ExactStatData fromJson(JsonObject json) {

        float first = json.get("first_val")
            .getAsFloat();
        float second = json.get("second_val")
            .getAsFloat();

        String stat = json.get("stat")
            .getAsString();

        StatModTypes type = StatModTypes.valueOf(json.get("type")
            .getAsString());

        return new ExactStatData(first, second, type, stat);

    }
}
