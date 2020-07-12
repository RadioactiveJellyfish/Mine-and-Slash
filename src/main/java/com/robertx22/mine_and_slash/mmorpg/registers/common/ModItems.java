package com.robertx22.mine_and_slash.mmorpg.registers.common;

import com.robertx22.mine_and_slash.database.IGUID;
import com.robertx22.mine_and_slash.database.currency.*;
import com.robertx22.mine_and_slash.database.currency.base.CurrencyItem;
import com.robertx22.mine_and_slash.database.gearitemslots.bases.GearItemSlot;
import com.robertx22.mine_and_slash.database.gearitemslots.curios.LifeNecklace;
import com.robertx22.mine_and_slash.database.gearitemslots.curios.OccultNecklace;
import com.robertx22.mine_and_slash.database.gearitemslots.curios.OccultRing;
import com.robertx22.mine_and_slash.database.gearitemslots.offhand.Buckler;
import com.robertx22.mine_and_slash.database.gearitemslots.offhand.SpiritShield;
import com.robertx22.mine_and_slash.database.gearitemslots.offhand.TowerShield;
import com.robertx22.mine_and_slash.database.gearitemslots.plate.PlateBoots;
import com.robertx22.mine_and_slash.database.gearitemslots.plate.PlateChest;
import com.robertx22.mine_and_slash.database.gearitemslots.plate.PlateHelmet;
import com.robertx22.mine_and_slash.database.gearitemslots.plate.PlatePants;
import com.robertx22.mine_and_slash.database.gearitemslots.weapons.*;
import com.robertx22.mine_and_slash.items.gearitems.armor.plate.PlateBootsItem;
import com.robertx22.mine_and_slash.items.gearitems.armor.plate.PlateChestItem;
import com.robertx22.mine_and_slash.items.gearitems.armor.plate.PlateHelmetItem;
import com.robertx22.mine_and_slash.items.gearitems.armor.plate.PlatePantsItem;
import com.robertx22.mine_and_slash.items.gearitems.baubles.ItemNecklace;
import com.robertx22.mine_and_slash.items.gearitems.baubles.ItemRing;
import com.robertx22.mine_and_slash.items.gearitems.offhands.NormalShield;
import com.robertx22.mine_and_slash.items.gearitems.weapons.ItemAxe;
import com.robertx22.mine_and_slash.items.gearitems.weapons.ItemBow;
import com.robertx22.mine_and_slash.items.gearitems.weapons.ItemStaff;
import com.robertx22.mine_and_slash.items.gearitems.weapons.ItemSword;
import com.robertx22.mine_and_slash.items.misc.IdentifyTomeItem;
import com.robertx22.mine_and_slash.items.misc.JewelItem;
import com.robertx22.mine_and_slash.items.misc.MagicEssenceItem;
import com.robertx22.mine_and_slash.items.misc.RareMagicEssence;
import com.robertx22.mine_and_slash.mmorpg.Ref;
import com.robertx22.mine_and_slash.uncommon.utilityclasses.ItemUtils;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ModItems {

    public static DeferredRegister<Item> REG = new DeferredRegister<>(ForgeRegistries.ITEMS, Ref.MODID);

    public static RegistryObject<Item> PLATE_BOOTS = of(() -> new PlateBootsItem(0), PlateBoots.INSTANCE);
    public static RegistryObject<Item> PLATE_HELMET = of(() -> new PlateHelmetItem(0), PlateHelmet.INSTANCE);
    public static RegistryObject<Item> PLATE_CHEST = of(() -> new PlateChestItem(0), PlateChest.INSTANCE);
    public static RegistryObject<Item> PLATE_PANTS = of(() -> new PlatePantsItem(0), PlatePants.INSTANCE);

    public static RegistryObject<Item> SWORD = of(() -> new ItemSword(0), Sword.INSTANCE);
    public static RegistryObject<Item> AXE = of(() -> new ItemAxe(0), Axe.INSTANCE);
    public static RegistryObject<Item> STAFF = of(() -> new ItemStaff(0), Staff.INSTANCE);
    public static RegistryObject<Item> BOW = of(() -> new ItemBow(0), HunterBow.INSTANCE);
    public static RegistryObject<Item> CROSSBOW = of(() -> new CrossbowItem(ItemUtils.getDefaultGearProperties()), Crossbow.INSTANCE);
    public static RegistryObject<Item> TOWER_SHIELD = of(() -> new NormalShield(TowerShield.INSTANCE), TowerShield.INSTANCE);
    public static RegistryObject<Item> SPIRIT_SHIELD = of(() -> new NormalShield(SpiritShield.INSTANCE), SpiritShield.INSTANCE);
    public static RegistryObject<Item> BUCKLER = of(() -> new NormalShield(Buckler.INSTANCE), Buckler.INSTANCE);

    public static RegistryObject<Item> HEALTH_NECKLACE = of(() -> new ItemNecklace(0), LifeNecklace.INSTANCE);
    public static RegistryObject<Item> MANA_REG_NECKLACE = of(() -> new ItemNecklace(0), OccultNecklace.INSTANCE);
    public static RegistryObject<Item> MANA_RING = of(() -> new ItemRing(0), OccultRing.INSTANCE);

    // todo register all gears like this.

    public static List<RegistryObject<CurrencyItem>> currencies = new ArrayList<>();

    public static RegistryObject<CurrencyItem> ORB_OF_TRANSMUTATION = of(() -> new OrbOfTransmutationItem());

    public static RegistryObject<CurrencyItem> ORB_OF_DISORDER = of(() -> new OrbOfDisorder());

    public static RegistryObject<CurrencyItem> ORB_OF_TURBULENCE = of(() -> new OrbOfTurbulence());

    public static RegistryObject<CurrencyItem> STONE_OF_HOPE = of(() -> new StoneOfHopeItem());
    public static RegistryObject<CurrencyItem> LEAF_OF_CHANGE = of(() -> new LeafOfChangeItem());
    public static RegistryObject<CurrencyItem> ORB_OF_BLESSING = of(() -> new OrbOfBlessingItem());
    public static RegistryObject<CurrencyItem> ORB_OF_UNIQUE_BLESSING = of(() -> new OrbOfUniqueBlessingItem());

    public static RegistryObject<IdentifyTomeItem> IDENTIFY_TOME = item(() -> new IdentifyTomeItem(), "identify_tome");
    public static RegistryObject<Item> SKILL_GEM = item(() -> new Item(new Item.Properties().maxStackSize(1)
        .maxDamage(0)), "skill_gem");

    public static RegistryObject<Item> MAGIC_ESSENCE = item(() -> new MagicEssenceItem(), "magic_essence");
    public static RegistryObject<Item> RARE_MAGIC_ESSENCE = item(() -> new RareMagicEssence(), "rare_magic_essence");

    public static RegistryObject<JewelItem> BLUE_JEWEL = item(() -> new JewelItem(), "jewels/blue");
    public static RegistryObject<JewelItem> GREEN_JEWEL = item(() -> new JewelItem(), "jewels/green");

    public static List<RegistryObject<JewelItem>> ALL_JEWELS = new ArrayList<>();

    static RegistryObject<JewelItem> jewel(Supplier<JewelItem> c, String id) {
        RegistryObject<JewelItem> wrap = REG.register(id, c);
        ALL_JEWELS.add(wrap);
        return wrap;
    }

    static <T extends Item & IGUID> RegistryObject<T> item(Supplier<T> c) {

        RegistryObject<T> wrap = REG.register(c.get()
            .GUID(), c);

        return wrap;

    }

    static <T extends Item> RegistryObject<T> item(Supplier<T> c, String id) {
        RegistryObject<T> wrap = REG.register(id, c);
        return wrap;
    }

    static RegistryObject<Item> of(Supplier<Item> c, GearItemSlot slot) {
        RegistryObject<Item> wrap = REG.register(slot.family()
            .name()
            .toLowerCase(Locale.ROOT) + "/" + slot.GUID(), c);
        return wrap;

    }

    static RegistryObject<CurrencyItem> of(Supplier<CurrencyItem> c) {

        RegistryObject<CurrencyItem> wrap = REG.register(c.get()
            .GUID(), c);

        currencies.add(wrap);

        return wrap;

    }

}
