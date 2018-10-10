package com.robertx22.Stats;

import java.util.ArrayList;
import com.robertx22.Enums.*;
import com.robertx22.Units.Unit;

public abstract class Stat {
	
	public abstract String Name();
	public abstract boolean ScalesToLevel();
	public abstract Elements Element();
	public abstract StatRefs StatRef();
	
	
	public Double GetValue(Unit Source) {
		
		return null;		
	}
	
	public ArrayList<IStatEffect> Effects;

	
}
