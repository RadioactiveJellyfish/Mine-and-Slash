package com.robertx22.uncommon.capability;

import java.util.UUID;

import com.robertx22.customitems.gearitems.bases.IWeapon;
import com.robertx22.customitems.gearitems.bases.WeaponMechanic;
import com.robertx22.database.rarities.MobRarity;
import com.robertx22.db_lists.Rarities;
import com.robertx22.mmorpg.Main;
import com.robertx22.mmorpg.ModConfig;
import com.robertx22.mmorpg.Ref;
import com.robertx22.network.UnitPackage;
import com.robertx22.onevent.combat.OnHealDecrease;
import com.robertx22.onevent.player.OnLogin;
import com.robertx22.saveclasses.MapItemData;
import com.robertx22.saveclasses.PlayerMapKillsData;
import com.robertx22.saveclasses.Unit;
import com.robertx22.uncommon.AttackUtils;
import com.robertx22.uncommon.CLOC;
import com.robertx22.uncommon.SLOC;
import com.robertx22.uncommon.capability.WorldData.IWorldData;
import com.robertx22.uncommon.capability.bases.ICommonCapability;
import com.robertx22.uncommon.datasaving.Load;
import com.robertx22.uncommon.enumclasses.EntitySystemChoice;
import com.robertx22.uncommon.utilityclasses.HealthUtils;

import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EntityData {

    @CapabilityInject(UnitData.class)
    public static final Capability<UnitData> Data = null;

    private static final String LEVEL = "level";
    private static final String RARITY = "rarity";
    private static final String EXP = "exp";
    private static final String UUID = "uuid";
    private static final String NAME = "name";
    private static final String MOB_SAVED_ONCE = "mob_saved_once";
    private static final String UNIT_OBJECT = "unit_object";
    private static final String KILLS_OBJECT = "kils_object";
    private static final String MANA = "current_mana";
    private static final String ENERGY = "current_energy";

    public interface UnitData extends ICommonCapability {

	int getLevel();

	void setLevel(int lvl, EntityLivingBase entity);

	int getExp();

	void setExp(int exp);

	int GiveExp(EntityPlayer player, int i);

	int GetExpRequiredForLevelUp();

	boolean CheckIfCanLevelUp();

	boolean LevelUp(EntityPlayer player);

	boolean CheckLevelCap();

	void SetMobLevel(IWorldData data, int lvl, EntityLivingBase entity);

	Unit getUnit();

	void setUnit(Unit unit, EntityLivingBase entity);

	void setRarity(int rarity);

	int getRarity();

	String getUUID();

	void setUUID(UUID id);

	String getName(EntityLivingBase entity);

	void HandleCloneEvent(UnitData old);

	void recalculateStats(EntityLivingBase entity, IWorldData world);

	void forceSetUnit(Unit unit);

	boolean tryUseWeapon(EntityLivingBase entity, WeaponMechanic iwep, ItemStack weapon);

	void attackWithWeapon(EntityLivingBase source, EntityLivingBase target, ItemStack weapon);

	void onMobKill(IWorldData world);

	int getLootBonusPerAffixKills(MapItemData map);

	void onLogin(EntityPlayer player);

	float getCurrentMana();

	float getCurrentEnergy();

	void setCurrentEnergy(float i);

	void setCurrentMana(float i);

	boolean hasEnoughMana(float i);

	boolean hasEnoughEnergy(float i);

	void restoreMana(float i);

	void restoreEnergy(float i);

	void consumeMana(float i);

	void consumeEnergy(float i);

	void heal(EntityLivingBase entity, int healthrestored);
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
	@SubscribeEvent
	public static void onEntityConstruct(AttachCapabilitiesEvent<Entity> event) {

	    boolean can = false;

	    if (ModConfig.Server.ENTITIES_UNDER_SYSTEM.equals(EntitySystemChoice.All_Entities)
		    && event.getObject() instanceof EntityLivingBase) {
		can = true;
	    }

	    if (ModConfig.Server.ENTITIES_UNDER_SYSTEM.equals(EntitySystemChoice.Mobs_And_Players)) {
		if (event.getObject() instanceof EntityPlayer || event.getObject() instanceof EntityMob
			|| event.getObject() instanceof IMob) {
		    can = true;
		}
	    }

	    if (can) {

		event.addCapability(new ResourceLocation(Ref.MODID, "EntityData"),
			new ICapabilitySerializable<NBTTagCompound>() {
			    UnitData inst = new DefaultImpl();

			    @Override
			    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				return capability == Data;

			    }

			    @Override
			    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				return capability == Data ? Data.<T>cast(inst) : null;
			    }

			    @Override
			    public NBTTagCompound serializeNBT() {
				return (NBTTagCompound) Data.getStorage().writeNBT(Data, inst, null);
			    }

			    @Override
			    public void deserializeNBT(NBTTagCompound nbt) {
				Data.getStorage().readNBT(Data, inst, null, nbt);
			    }

			});

	    }
	}

    }

    public static class Storage implements IStorage<UnitData> {
	@Override
	public NBTBase writeNBT(Capability<UnitData> capability, UnitData instance, EnumFacing side) {

	    return instance.getNBT();
	}

	@Override
	public void readNBT(Capability<UnitData> capability, UnitData instance, EnumFacing side, NBTBase nbt) {

	    instance.setNBT((NBTTagCompound) nbt);

	}
    }

    public static class DefaultImpl implements UnitData {
	private NBTTagCompound nbt = new NBTTagCompound();

	Unit unit = null;
	PlayerMapKillsData kills = new PlayerMapKillsData();
	int level = 1;
	int exp = 0;
	int rarity = 0;
	String uuid = "";
	String name = "";

	float energy;
	float mana;

	@Override
	public NBTTagCompound getNBT() {
	    nbt.setFloat(MANA, mana);
	    nbt.setFloat(ENERGY, energy);
	    nbt.setInteger(LEVEL, level);
	    nbt.setInteger(EXP, exp);
	    nbt.setInteger(RARITY, rarity);
	    nbt.setString(UUID, uuid);
	    nbt.setString(NAME, name);
	    nbt.setBoolean(MOB_SAVED_ONCE, true);

	    if (unit != null) {
		NBTTagCompound unitnbt = new NBTTagCompound();
		Writer.write(unitnbt, unit);
		nbt.setTag(UNIT_OBJECT, unitnbt);
	    }
	    if (kills != null) {
		NBTTagCompound killsnbt = new NBTTagCompound();
		Writer.write(killsnbt, kills);
		nbt.setTag(KILLS_OBJECT, killsnbt);
	    }

	    return nbt;

	}

	@Override
	public void setNBT(NBTTagCompound value) {
	    this.nbt = value;
	    this.level = value.getInteger(LEVEL);
	    this.exp = value.getInteger(EXP);
	    this.rarity = value.getInteger(RARITY);
	    this.uuid = value.getString(UUID);
	    this.name = value.getString(NAME);
	    this.energy = value.getFloat(ENERGY);
	    this.mana = value.getFloat(MANA);

	    NBTTagCompound object_nbt = (NBTTagCompound) this.nbt.getTag(UNIT_OBJECT);
	    if (object_nbt != null) {
		unit = new Unit();
		Reader.read(object_nbt, unit);
	    }

	    NBTTagCompound kills_nbt = (NBTTagCompound) this.nbt.getTag(KILLS_OBJECT);
	    if (kills_nbt != null) {
		kills = new PlayerMapKillsData();
		Reader.read(kills_nbt, kills);
	    }

	}

	@Override
	public int GetExpRequiredForLevelUp() {

	    int lvl = getLevel();

	    int tens = lvl / 10;

	    if (lvl < 5) {
		return 250 * lvl;
	    }

	    return lvl * 750 + (tens * 2500);

	}

	@Override
	public void SetMobLevel(IWorldData data, int lvl, EntityLivingBase entity) {

	    if (lvl < 1) {
		lvl = 1;

	    }
	    if (data != null && !data.isMapWorld()) {
		if (lvl > ModConfig.Server.MAXIMUM_NORMAL_WORLD_MOB_LEVEL) {
		    lvl = ModConfig.Server.MAXIMUM_NORMAL_WORLD_MOB_LEVEL;
		}
	    }

	    if (lvl > ModConfig.Server.MAXIMUM_PLAYER_LEVEL) {
		lvl = ModConfig.Server.MAXIMUM_PLAYER_LEVEL;
	    }

	    setLevel(lvl, entity);
	}

	@Override
	public int GiveExp(EntityPlayer player, int i) {

	    i *= ModConfig.Server.EXPERIENCE_MULTIPLIER;

	    setExp(exp + i);

	    if (exp > this.GetExpRequiredForLevelUp()) {

		setExp(this.GetExpRequiredForLevelUp());

		if (ModConfig.Server.LEVEL_UPS_COST_TOKEN == false) {

		    if (this.CheckIfCanLevelUp() && this.CheckLevelCap()) {
			this.LevelUp(player);
		    }
		}
		return i;
	    }

	    return i;

	}

	@Override
	public boolean CheckIfCanLevelUp() {

	    return getExp() >= GetExpRequiredForLevelUp();

	}

	@Override
	public boolean CheckLevelCap() {
	    return getLevel() + 1 <= ModConfig.Server.MAXIMUM_PLAYER_LEVEL;
	}

	@Override
	public boolean LevelUp(EntityPlayer player) {

	    if (!CheckIfCanLevelUp()) {
		player.sendMessage(
			new TextComponentString(TextFormatting.RED + "You don't have enough experience to Level Up."));
	    } else if (!CheckLevelCap()) {
		player.sendMessage(
			new TextComponentString(TextFormatting.RED + "You have already reached maximum level."));
	    }

	    if (CheckIfCanLevelUp() && CheckLevelCap()) {

		this.setLevel(level + 1, player);
		setExp(0);

		player.sendMessage(SLOC.chat("levelup_success"));

		return true;
	    }
	    return false;
	}

	@Override
	public int getLevel() {

	    return level;

	}

	@Override
	public void setLevel(int lvl, EntityLivingBase entity) {
	    level = lvl;
	}

	@Override
	public int getExp() {
	    return exp;
	}

	@Override
	public void setExp(int exp) {
	    this.exp = exp;
	}

	@Override
	public void syncToClient(EntityPlayer player) {
	    if (unit != null) {
		UnitPackage packet = new UnitPackage(this.getNBT());
		Main.Network.sendTo(packet, (EntityPlayerMP) player);
	    }
	}

	@Override
	public Unit getUnit() {
	    return unit;

	}

	@Override
	public void setUnit(Unit unit, EntityLivingBase entity) {

	    this.unit = unit;

	}

	@Override
	public void setRarity(int rarity) {
	    this.rarity = rarity;

	}

	@Override
	public int getRarity() {
	    return rarity;
	}

	@Override
	public String getUUID() {
	    return uuid;
	}

	@Override
	public void setUUID(UUID id) {
	    uuid = id.toString();
	}

	@Override
	public String getName(EntityLivingBase entity) {
	    MobRarity rarity = Rarities.Mobs.get(getRarity());
	    String rarityprefix = "";
	    String name = "";

	    if (entity instanceof EntityPlayer) {
		name = ((EntityPlayer) entity).getDisplayNameString();

	    } else {
		name = entity.getName();
		rarityprefix = CLOC.rarityName(rarity);

	    }

	    name = TextFormatting.YELLOW + "[Lv:" + this.getLevel() + "] " + rarity.Color() + rarityprefix + " " + name;

	    return name;
	}

	@Override
	public void HandleCloneEvent(UnitData old) {
	    this.setNBT(old.getNBT());
	}

	@Override
	public void recalculateStats(EntityLivingBase entity, IWorldData world) {

	    unit.RecalculateStats(entity, this, level, world);

	}

	@Override
	public void forceSetUnit(Unit unit) {
	    this.unit = unit;
	}

	@Override
	public boolean tryUseWeapon(EntityLivingBase entity, WeaponMechanic iwep, ItemStack weapon) {

	    float energyCost = iwep.GetEnergyCost();

	    if (hasEnoughEnergy(energyCost) == false) {

		AttackUtils.NoEnergyMessage(entity);
		return false;

	    } else {
		consumeEnergy(energyCost);
		weapon.damageItem(1, entity);
		return true;

	    }

	}

	public void attackWithWeapon(EntityLivingBase source, EntityLivingBase target, ItemStack weapon) {

	    UnitData targetData = Load.Unit(target);

	    if (weapon != null && !weapon.isEmpty() && weapon.getItem() instanceof IWeapon) {

		WeaponMechanic iWep = ((IWeapon) weapon.getItem()).mechanic();

		iWep.Attack(source, target, this, targetData);

	    }
	}

	@Override
	public void onMobKill(IWorldData world) {

	    Runnable noteThread = new Runnable() {
		@Override
		public void run() {
		    try {

			kills.onKill(world.getMap());

		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

	    };
	    FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(noteThread);

	}

	@Override
	public int getLootBonusPerAffixKills(MapItemData map) {

	    return kills.getLootMulti(map);
	}

	@Override
	public void onLogin(EntityPlayer player) {

	    try {

		// check if newbie
		if (unit == null) {
		    unit = new Unit();
		    unit.InitPlayerStats();
		    OnLogin.GiveStarterItems(player);
		} else {
		    getUnit().InitPlayerStats();
		    recalculateStats(player, Load.World(player));
		}

		kills.init();

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	@Override
	public float getCurrentMana() {
	    return mana;
	}

	@Override
	public float getCurrentEnergy() {
	    return energy;
	}

	@Override
	public void setCurrentEnergy(float i) {
	    energy = i;

	}

	@Override
	public void setCurrentMana(float i) {
	    mana = i;

	}

	@Override
	public boolean hasEnoughMana(float i) {
	    return mana >= i;
	}

	@Override
	public boolean hasEnoughEnergy(float i) {
	    return energy >= i;
	}

	@Override
	public void restoreMana(float i) {
	    float max = unit.manaData().Value;

	    mana += i;
	    if (mana > max) {
		mana = (int) max;
	    }

	}

	@Override
	public void restoreEnergy(float i) {
	    float max = unit.energyData().Value;

	    energy += i;
	    if (energy > max) {
		energy = (int) max;
	    }

	}

	@Override
	public void consumeMana(float i) {
	    mana -= i;
	    if (mana < 0) {
		mana = 0;
	    }

	}

	@Override
	public void consumeEnergy(float i) {
	    energy -= i;
	    if (energy < 0) {
		energy = 0;
	    }

	}

	@Override
	public void heal(EntityLivingBase entity, int healthrestored) {
	    entity.heal(HealthUtils.DamageToMinecraftHealth(healthrestored * OnHealDecrease.HEAL_DECREASE, entity));
	}

    }

}
