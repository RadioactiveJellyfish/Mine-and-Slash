package com.robertx22.mine_and_slash.saveclasses.gearitem.gear_parts;

import com.robertx22.mine_and_slash.database.data.StatModifier;
import com.robertx22.mine_and_slash.database.data.stats.Stat;
import com.robertx22.mine_and_slash.database.data.stats.types.generated.WeaponDamage;
import com.robertx22.mine_and_slash.database.data.stats.types.offense.CriticalHit;
import com.robertx22.exiled_lib.registry.SlashRegistry;
import com.robertx22.mine_and_slash.saveclasses.ExactStatData;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_bases.*;
import com.robertx22.mine_and_slash.saveclasses.item_classes.GearItemData;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import com.robertx22.mine_and_slash.uncommon.enumclasses.ModType;
import com.robertx22.mine_and_slash.uncommon.localization.CLOC;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.NumberUtils;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.RandomUtils;
import com.robertx22.mine_and_slash.uncommon.wrappers.SText;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Storable
public class BaseStatsData implements IRerollable, IStatsContainer, IGearPartTooltip {

    @Store
    public List<Integer> percents = new ArrayList<Integer>();

    @Store
    public String gear_type = "";

    @Override
    public void RerollFully(GearItemData gear) {

        percents = new ArrayList<>();
        this.gear_type = gear.gear_type;

        gear.GetBaseGearType()
            .baseStats()
            .forEach(x -> percents.add(getMinMax(gear).random()));

        gear.rare_prefix = RandomUtils.randomFromList(new ArrayList<>(RareItemAffixNames.prefixAny
            .keySet()));
        gear.rare_suffix = RandomUtils.randomFromList(new ArrayList<>(RareItemAffixNames.getSuffixMap(gear.GetBaseGearType())
            .keySet()));

    }

    @Override
    public void RerollNumbers(GearItemData gear) {
        RerollFully(gear);
    }

    static TextFormatting NUMBER_COLOR = TextFormatting.BLUE;
    static TextFormatting TEXT_COLOR = TextFormatting.GRAY;

    @Override
    public List<ITextComponent> GetTooltipString(TooltipInfo info, GearItemData gear) {

        List<ExactStatData> all = GetAllStats(gear);

        List<ITextComponent> list = new ArrayList<ITextComponent>();
        list.add(new StringTextComponent(" "));

        ITextComponent physdmg = null;
        ITextComponent eledmg = null;
        ITextComponent critchance = null;

        float totalDmg = 0;

        String eleDmgs = "";

        for (ExactStatData exactStatData : all) {
            Stat stat = exactStatData.getStat();

            String perc = stat.IsPercent() || !exactStatData.getType()
                .isFlat() ? "%" : "";

            if (stat instanceof WeaponDamage) {
                if (stat.getElement() == Elements.Physical) {
                    physdmg = new SText(TEXT_COLOR + CLOC.translate(stat.locName()) + ": " + NUMBER_COLOR +
                        NumberUtils.format(exactStatData.getFirstValue()) + "-" + NumberUtils.format(exactStatData.getSecondValue()));

                } else {

                    String dot = ",";
                    if (eleDmgs.length() == 0) {
                        dot = "";
                    }
                    eleDmgs += dot + " " + stat.getElement().format +
                        NumberUtils.format(exactStatData.getFirstValue()) + "-" + NumberUtils.format(exactStatData.getSecondValue());
                }

                totalDmg += exactStatData.getAverageValue();

            } else {

                TextFormatting color = NUMBER_COLOR;

                if (stat instanceof CriticalHit) {
                    color = TextFormatting.YELLOW;
                }

                ITextComponent comp = new SText(TEXT_COLOR + CLOC.translate(stat.locName()) + ": " + color + NumberUtils.format(exactStatData.getFirstValue()) + perc);

                if (stat instanceof CriticalHit) {
                    critchance = comp;
                } else {
                    list.add(comp);
                }

            }
        }
        if (eleDmgs.length() > 0) {
            eledmg = new SText(TEXT_COLOR + "Elemental Damage:" + eleDmgs);
        }

        if (physdmg != null) {
            list.add(physdmg);
        }
        if (eledmg != null) {
            list.add(eledmg);
        }
        if (critchance != null) {
            list.add(critchance);
        }
        if (gear.GetBaseGearType()
            .isMeleeWeapon()) {

            float atk_per_sec = gear.GetBaseGearType()
                .getAttacksPerSecondForTooltip(gear);

            list.add(new SText(TEXT_COLOR + "Attacks per Second: " + TextFormatting.GREEN + NumberUtils.formatForTooltip(atk_per_sec)));
            // unsure if i want dps on tooltip//list.add(new SText(TEXT_COLOR + "DPS: " + NUMBER_COLOR + NumberUtils.formatForTooltip(totalDmg * atk_per_sec)));

        }

        return list;

    }

    @Override
    public boolean isBaseStats() {
        return true;
    }

    @Override
    public List<ExactStatData> GetAllStats(GearItemData gear) {

        List<ExactStatData> local = new ArrayList<>();
        List<ExactStatData> all = gear.GetAllStats(false, true);

        int i = 0;

        for (StatModifier mod : SlashRegistry.GearTypes()
            .get(gear_type)
            .baseStats()) {
            local.add(mod.ToExactStat(percents.get(i), gear.level));
            i++;
        }

        // add up flats first
        all.forEach(x -> {

            if (x.shouldBeAddedToLocalStats(gear) && x.getType()
                .isFlat()) {

                Optional<ExactStatData> opt = local.stream()
                    .filter(t -> t.getStat() == x.getStat())
                    .findFirst();

                if (opt.isPresent()) {
                    opt.get()
                        .add(x);
                } else {
                    local.add(x);
                }
            }
        });

        // now increase all flats by local increases
        all.stream()
            .filter(x ->
                x.shouldBeAddedToLocalStats(gear) && x.getType()
                    == ModType.LOCAL_INCREASE)
            .forEach(s -> {

                ExactStatData flatLocal = local.stream()
                    .filter(x -> x.getStat()
                        .GUID()
                        .equals(s.getStat()
                            .GUID()))
                    .findFirst()
                    .get();

                flatLocal.percentIncrease += s.getFirstValue();

            });

        local.forEach(x -> x.increaseByAddedPercent());

        return local;

    }

    @Override
    public Part getPart() {
        return Part.BASE_STATS;
    }
}
