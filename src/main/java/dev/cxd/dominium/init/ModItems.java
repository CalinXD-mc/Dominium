package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.item.*;
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

//    public static final Item HUMAN_BONE = registerItem("human_bone",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64)));
//    public static final Item BONE_PILE = registerItem("bone_pile",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64)));
//    public static final Item BONE_DUST = registerItem("bone_dust",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64).fireproof()));
//    public static final Item WITHERED_BONE = registerItem("withered_bone",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64).fireproof()));
//    public static final Item ANCIENT_SCRAP = registerItem("ancient_scrap",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64).fireproof()));
//    public static final Item ANCIENT_NETHERITE = registerItem("ancient_netherite",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64).fireproof()));
//    public static final Item NETHER_GOLD = registerItem("nether_gold",
//            new Item(new Item.Settings().rarity(Rarity.COMMON).maxCount(64).fireproof()));
    public static final Item ANCIENT_BONE_ALLOY = registerItem("ancient_bone_alloy",
            new CustomRarityItem(new Item.Settings().maxCount(64).fireproof(), ModRarities.DOMINIC));
    public static final Item ANCIENT_BONE_ALLOY_CHUNK = registerItem("ancient_bone_alloy_chunk",
            new CustomRarityItem(new Item.Settings().maxCount(64).fireproof(), ModRarities.DOMINIC));

//    public static final Item LUCKY_DICE = registerItem("lucky_dice",
//            new LuckyDiceItem(new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1)));
//    public static final Item SINFUL_DICE = registerItem("sinful_dice",
//            new SinfulDiceItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1)));

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

    public static final Item DOMINIC_ORB = registerItem("dominic_orb",
            new DominicOrbItem(new Item.Settings().maxCount(16).fireproof(), ModRarities.DOMINIC));
    public static final Item SOUL_ORB = registerItem("soul_orb",
            new SoulOrbItem(new Item.Settings().maxCount(16).fireproof(), ModRarities.CONTRACT));

    public static final Item DOMINIC_EFFIGY = registerItem("dominic_effigy",
            new DominicEffigyItem(new FabricItemSettings().maxCount(1).fireproof(), ModRarities.DOMINIC_EFFIGY));

    public static final Item SOULTRAP_SPETUM = registerItem("soultrap_spetum",
            new SoultrapSpetumItem(ModToolMaterials.DOMINIC, 9, -2.9F ,new FabricItemSettings().maxCount(1), ModRarities.DOMINIC));
    public static final Item DOMINIC_DAGGER = registerItem("dominic_dagger",
            new DominicDaggerItem(ModToolMaterials.DOMINIC, 4, -1.0F ,new FabricItemSettings().maxCount(1), ModRarities.DOMINIC));
    public static final Item BROKEN_DOMINIC_DAGGER_PIECE = registerItem("broken_dominic_dagger",
            new CustomRarityItem(new FabricItemSettings().maxCount(1), ModRarities.FIXED_DOMINIC));
    public static final Item FIXED_DOMINIC_DAGGER = registerItem("fixed_dominic_dagger",
            new FixedDominicDaggerItem(ModToolMaterials.DOMINIC, 2, -1.2F ,new FabricItemSettings().maxCount(1), ModRarities.FIXED_DOMINIC));
    public static final Item CONVENANT_OF_THE_PROVIDENT_SIGNED = registerItem("convenant_of_the_provident_signed",
            new ProvedientConvenantItem(new FabricItemSettings().maxCount(1), ModRarities.DOMINIC));

    public static final Item GILDED_ONYX = registerItem("gilded_onyx",
            new CustomRaritySwordItem(new FabricItemSettings().maxCount(1), 10, -3.0f, ModToolMaterials.DOMINIC ,ModRarities.DOMINIC));

    public static final Item ITEM_MARKER = registerItem("item_marker",
            new MarkerItem(new Item.Settings().maxCount(1), " "));
//    public static final Item SF_MARK = registerItem("sf_mark_misc",
//            new MarkerItem(new Item.Settings().maxCount(1), "Shardfell"));
//    public static final Item CE_MARK = registerItem("ce_mark_misc",
//            new MarkerItem(new Item.Settings().maxCount(1), "<CONTENT ERASED>"));

//    public static final Item PARTICLE_SPAWNER = registerItem("particle_spawner",
//            new LodestoneDI.ParticleSpawnerItem(new Item.Settings().maxCount(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Dominium.MOD_ID, name), item);
    }

    public static void initialize() {
        Dominium.LOGGER.info("Initializing Mod Items for " + Dominium.MOD_ID);
    }

}
