package com.robertx22.database.stat_types.traits.bases;

import com.robertx22.saveclasses.Unit;
import com.robertx22.stats.IAffectsOtherStats;
import com.robertx22.stats.Trait;

public abstract class TraitWithRequirement extends Trait implements IAffectsOtherStats {

    @Override
    public void TryAffectOtherStats(Unit unit) {

	if (this.requirement(unit)) {
	    this.affectStats(unit);
	}

    }

    public abstract void affectStats(Unit unit);

    public abstract boolean requirement(Unit unit);

    public abstract String descPrefix();

    public abstract String descSuffix();

    @Override
    public String Description() {
	return this.descPrefix() + this.descSuffix();
    }

}
