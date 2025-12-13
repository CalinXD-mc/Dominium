package dev.cxd.dominium.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModReferenceEnglishLangProvider extends FabricLanguageProvider {
    public ModReferenceEnglishLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        //Blocks
        translationBuilder.add("block.dominium.ancienter_debris", "Ancienter Debris");

        translationBuilder.add("block.dominium.soul_sandstone", "Soul Sandstone");

        translationBuilder.add("block.dominium.smooth_soul_sandstone", "Smooth Soul Sandstone");
        translationBuilder.add("block.dominium.smooth_soul_sandstone_stairs", "Smooth Soul Sandstone Stairs");
        translationBuilder.add("block.dominium.smooth_soul_sandstone_slab", "Smooth Soul Sandstone Slab");
        translationBuilder.add("block.dominium.smooth_soul_sandstone_wall", "Smooth Soul Sandstone Wall");

        translationBuilder.add("block.dominium.suspicious_bone_block", "Bone Block");
        translationBuilder.add("block.dominium.suspicious_bone_block.alt", "Bone Block?");

        translationBuilder.add("block.dominium.soul_glass", "Soul Glass");
        translationBuilder.add("block.dominium.soul_candle", "Soul Candle");

        translationBuilder.add("block.dominium.waystone", "Waystone");
        translationBuilder.add("block.dominium.idol", "Idol");
        translationBuilder.add("block.dominium.vessel", "Vessel");

        //Items
        translationBuilder.add("item.dominium.contract", "Contract");
        translationBuilder.add("item.dominium.contract_signed", "Contract");
        translationBuilder.add("item.dominium.contract_soulbound", "Soulbound Contract");
        translationBuilder.add("item.dominium.contract_signed_soulbound", "Soulbound Contract");

        translationBuilder.add("item.dominium.wanderer_tablet", "Wanderer's Tablet");
        translationBuilder.add("item.dominium.wanderer_tablet_signed", "Wanderer's Tablet");

        translationBuilder.add("item.dominium.bone_dust", "Bone Dust");
        translationBuilder.add("item.dominium.bone_pile", "Bone Pile");
        translationBuilder.add("item.dominium.human_bone", "Player Bone");
        translationBuilder.add("item.dominium.withered_bone", "Withered Bone");

        translationBuilder.add("item.dominium.ancient_scrap", "Ancient Scrap");
        translationBuilder.add("item.dominium.ancient_netherite", "Ancient Netherite Ingot");
        translationBuilder.add("item.dominium.nether_gold", "Nether's Gold");
        translationBuilder.add("item.dominium.ancient_bone_alloy", "Ancient Bone Alloy Ingot");
        translationBuilder.add("item.dominium.ancient_bone_alloy_chunk", "Ancient Bone Alloy Chunk");

        translationBuilder.add("item.dominium.dominic_orb", "Dominic Orb");
        translationBuilder.add("item.dominium.soul_orb", "Soul Orb");

        translationBuilder.add("item.dominium.dominic_effigy", "Dominic Effigy");

        translationBuilder.add("item.dominium.soultrap_spetum", "Soultrap Spetum");
        translationBuilder.add("item.dominium.dominic_dagger", "Dominic Dagger");
        translationBuilder.add("item.dominium.broken_dominic_dagger", "Broken Dominic Dagger Piece");
        translationBuilder.add("item.dominium.fixed_dominic_dagger", "Fixed Dominic Dagger");
        translationBuilder.add("item.dominium.convenant_of_the_provident_signed", "Convenant Of The Provident");

        translationBuilder.add("item.dominium.gilded_onyx", "Gilded Onyx");

        translationBuilder.add("item.dominium.eternal_divinity", "Eternal Divinity");
        translationBuilder.add("item.dominium.broken_piece_1", "A Piece of the Eternal Divinity");
        translationBuilder.add("item.dominium.broken_piece_2", "A Piece of the Eternal Divinity");
        translationBuilder.add("item.dominium.broken_piece_3", "A Piece of the Eternal Divinity");
        translationBuilder.add("item.dominium.broken_eternal_divinity", "Broken Eternal Divinity");

        //Damage Types
        translationBuilder.add("death.attack.disobedience", "%1$s's life was punished by the contract");
        translationBuilder.add("death.attack.disobedience.player", "%1$s's life was punished by the contract");
        translationBuilder.add("death.attack.dominic_blast", "%1$s was caught in the divine intervention");
        translationBuilder.add("death.attack.dominic_blast.player", "%1$s was caught in the divine intervention whilst fighting %2$s");

        //Effects
        translationBuilder.add("effect.dominium.damned", "Damned");
        translationBuilder.add("effect.dominium.soul_strain", "Soul Strain");

        //Sounds
        translationBuilder.add("subtitles.event.dominium_boom","Forfeit...");
        translationBuilder.add("subtitles.soul_glass.soul_glass_pling","Soul Glass Plings");
//        translationBuilder.add("subtitles.event.dominium_boom","Forfeit...");
//        translationBuilder.add("subtitles.event.dominium_boom","Forfeit...");

        //misc
        translationBuilder.add("itemGroup.dominium.dominium", "Dominium");
        translationBuilder.add("itemGroup.dominium.dominium_experimental", "Dominium (Experimental)");

        //config
        translationBuilder.add("dominium.midnightconfig.title", "Dominium");
        translationBuilder.add("dominium.midnightconfig.general", "General");
        translationBuilder.add("dominium.midnightconfig.soul_candle", "Soul Candle");
        translationBuilder.add("dominium.midnightconfig.experimental", "Experimental");
        translationBuilder.add("dominium.midnightconfig.broken_eternal_divinity", "Broken Eternal Divinity");
    }
}
