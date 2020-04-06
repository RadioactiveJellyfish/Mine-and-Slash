package com.robertx22.mine_and_slash.database.spells.spell_classes.bases.configs;

import com.google.common.base.Preconditions;
import com.robertx22.mine_and_slash.database.spells.spell_classes.bases.BaseSpell;
import com.robertx22.mine_and_slash.database.spells.spell_classes.bases.level_based_numbers.LevelBased;
import com.robertx22.mine_and_slash.saveclasses.spells.IAbility;
import com.robertx22.mine_and_slash.saveclasses.spells.calc.SpellCalcData;
import com.robertx22.mine_and_slash.uncommon.capability.player.PlayerSpellCap;

import java.util.HashMap;

// this class should be easy to serialize as a config
// synergies add to these values.
public class PreCalcSpellConfigs {

    public PreCalcSpellConfigs() {
        set(SC.TIMES_TO_CAST, 1, 1);
    }

    private HashMap<SC, LevelBased> map = new HashMap<>();

    public int maxSpellLevel = 12;

    public SpellCalcData getCalc(PlayerSpellCap.ISpellsCap cap, IAbility ability) {
        return SpellCalcData.scaleWithAttack(
            get(SC.ATTACK_SCALE_VALUE).get(cap, ability),
            get(SC.BASE_VALUE).get(cap, ability)
        );
    }

    public boolean has(SC sc) {
        return map.containsKey(sc);
    }

    public HashMap<SC, LevelBased> getMap() {
        return map;
    }

    public void set(SC sc, float min, float max) {

        if (sc.errorIfOverridden) {
            if (map.containsKey(sc)) {

                try {
                    throw new RuntimeException("Trying to set an already set value!!!");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        map.put(sc, new LevelBased(min, max).min(sc.min));
    }

    public void setMaxLevel(int lvl) {
        this.maxSpellLevel = lvl;
    }

    public LevelBased get(SC sc) {

        if (!map.containsKey(sc)) {

            try {
                throw new RuntimeException("Trying to get non existent value!!!");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        LevelBased thing = map.get(sc);

        if (thing.isEmpty()) {
            try {
                throw new RuntimeException("Getting empty value!!!");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return thing;

    }

    private boolean modifiedBySynergies = false;

    public void modifyBySynergies(BaseSpell spell, PlayerSpellCap.ISpellsCap cap) {

        Preconditions.checkArgument(
            modifiedBySynergies == false,
            "Cannot modify same spell calc config instance twice with synergies!,Make sure you're returning new config instances on each method call!!!");

        spell.getAllocatedSynergies(cap)
            .forEach(x -> {
                PreCalcSpellConfigs sc = x.getConfigsAffectingSpell();

                sc.map.entrySet()
                    .forEach(e -> {
                        this.map.get(e.getKey())
                            .modifyBy(e.getValue());
                    });

            });

        this.modifiedBySynergies = true;

    }

}