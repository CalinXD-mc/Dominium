package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.item.*;
import dev.cxd.dominium.item.ban_items.*;
import dev.cxd.dominium.item.experimental.MarkerItem;
import dev.cxd.dominium.item.necklaces.EtherealNecklaceItem;
import dev.cxd.dominium.item.necklaces.SpiderNecklaceItem;
import dev.cxd.dominium.item.necklaces.UndeadNecklaceItem;
import dev.cxd.dominium.item.signable.*;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import dev.cxd.dominium.utils.ModToolMaterials;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    //Materials
    public static final Item ANCIENT_BONE_ALLOY = registerItem("ancient_bone_alloy",
            new CustomRarityItem(new Item.Settings().maxCount(64).fireproof(), ModRarities.DOMINIC));

    public static final Item ANCIENT_BONE_ALLOY_CHUNK = registerItem("ancient_bone_alloy_chunk",
            new CustomRarityItem(new Item.Settings().maxCount(64).fireproof(), ModRarities.DOMINIC));

    public static final Item ETERNAL_DIVINITY_PIECE1 = registerItem("broken_piece_1",
            new CustomRarityItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC));

    public static final Item ETERNAL_DIVINITY_PIECE2 = registerItem("broken_piece_2",
            new CustomRarityItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC));

    public static final Item ETERNAL_DIVINITY_PIECE3 = registerItem("broken_piece_3",
            new CustomRarityItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC));


    //Signable
    public static final Item CONTRACT = registerItem("contract",
            new ContractUnsignedItem(new Item.Settings().maxCount(1), ModRarities.CONTRACT));

    public static final Item CONTRACT_SIGNED = registerItem("contract_signed",
            new ContractSigned(new Item.Settings().maxCount(1), ModRarities.CONTRACT));

    public static final Item SOULBOUND_CONTRACT = registerItem("contract_soulbound",
            new SoulboundContractUnsignedItem(new Item.Settings().maxCount(1).fireproof(), ModRarities.SOULBOUND_CONTRACT));

    public static final Item SOULBOUND_CONTRACT_SIGNED = registerItem("contract_signed_soulbound",
            new SoulboundContractSigned(new Item.Settings().maxCount(1).fireproof(), ModRarities.SOULBOUND_CONTRACT));

    public static final Item WANDERER_TABLET = registerItem("wanderer_tablet",
            new WandererTabletUnsigned(new Item.Settings().maxCount(1), ModRarities.CONTRACT));

    public static final Item SIGNED_WANDERER_TABLET = registerItem("wanderer_tablet_signed",
            new WandererTabletSigned(new Item.Settings().maxCount(1), ModRarities.CONTRACT));


    //Orbs
    public static final Item DOMINIC_ORB = registerItem("dominic_orb",
            new DominicOrbItem(new Item.Settings().maxCount(16).fireproof(), ModRarities.DOMINIC));

    public static final Item SOUL_ORB = registerItem("soul_orb",
            new SoulOrbItem(new Item.Settings().maxCount(16).fireproof(), ModRarities.CONTRACT));


    //Relics
    public static final Item DOMINIC_EFFIGY = registerItem("dominic_effigy",
            new DominicEffigyItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC_EFFIGY));

    public static final Item CONVENANT_OF_THE_PROVIDENT = registerItem("convenant_of_the_provident",
            new ProvedientConvenantItem(new FabricItemSettings().maxCount(1), ModRarities.DOMINIC));

    public static final Item CONVENANT_OF_THE_PROVIDENT_SIGNED = registerItem("convenant_of_the_provident_signed",
            new ProvedientConvenantItem(new FabricItemSettings().maxCount(1), ModRarities.DOMINIC));

    public static final Item UNDEAD_NECKLACE = registerItem("necklace_of_the_undead",
            new UndeadNecklaceItem(new FabricItemSettings().maxCount(1), ModRarities.UNDEAD));

    public static final Item SPIDER_NECKLACE = registerItem("necklace_of_the_arachnid",
            new SpiderNecklaceItem(new FabricItemSettings().maxCount(1), ModRarities.ARACHNID));

    public static final Item ETHEREAL_NECKLACE = registerItem("necklace_of_the_ethereal",
            new EtherealNecklaceItem(new FabricItemSettings().maxCount(1), ModRarities.ETHEREAL));


    //Weapons
    public static final Item SOULTRAP_SPETUM = registerItem("soultrap_spetum",
            new SoultrapSpetumItem(
                    ModToolMaterials.DOMINIC,
                    9,
                    -2.9F,
                    new FabricItemSettings().maxCount(1),
                    ModRarities.DOMINIC
            ));

    public static final Item DOMINIC_DAGGER = registerItem("dominic_dagger",
            new DominicDaggerItem(
                    ModToolMaterials.DOMINIC,
                    4,
                    -1.0F,
                    new FabricItemSettings().maxCount(1),
                    ModRarities.DOMINIC
            ));

    public static final Item GILDED_ONYX = registerItem("gilded_onyx",
            new GildedOnyxItem(
                    new FabricItemSettings().maxCount(1),
                    10,
                    -3.0f,
                    ModToolMaterials.DOMINIC,
                    ModRarities.DOMINIC
            ));

    public static final Item ETERNAL_DIVINITY = registerItem("eternal_divinity",
            new EternalDivinityItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC));

    public static final Item BROKEN_ETERNAL_DIVINITY = registerItem("broken_eternal_divinity",
            new BrokenEternalDivinityItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC));


    //Misc
    public static final Item ITEM_MARKER = registerItem("item_marker",
            new MarkerItem(new Item.Settings().maxCount(1), " "));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Dominium.MOD_ID, name), item);
    }

    public static void initialize() {
        Dominium.LOGGER.info("Initializing Mod Items for " + Dominium.MOD_ID);
    }

}
