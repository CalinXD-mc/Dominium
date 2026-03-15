package dev.cxd.dominium.datagen;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.init.ModBlocks;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.init.ModStatusEffects;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModAdvancementProvider extends FabricAdvancementProvider {

    public ModAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {

        Advancement root = Advancement.Builder.create()
                .display(
                        ModItems.SOUL_ORB,
                        Text.literal("Dominium"),
                        Text.literal("Thanks for Using Dominium!!!"),
                        new Identifier("textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementFrame.TASK,
                        false, false, false
                )
                .criterion("tick", TickCriterion.Conditions.createTick())
                .build(consumer, Dominium.MOD_ID + ":root");

        Advancement soulOrb = Advancement.Builder.create()
                .parent(root)
                .display(
                        ModItems.SOUL_ORB,
                        Text.literal("Thy Demise is Now"),
                        Text.literal("Obtain a Soul Orb by killing any Player (including you) on Soul Sand."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_soul_orb", InventoryChangedCriterion.Conditions.items(ModItems.SOUL_ORB))
                .build(consumer, Dominium.MOD_ID + ":soul_orb");

        Advancement waystone = Advancement.Builder.create()
                .parent(root)
                .display(
                        ModBlocks.WAYSTONE,
                        Text.literal("The Stone That Leads the Way"),
                        Text.literal("Obtain a Waystone."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_waystone", InventoryChangedCriterion.Conditions.items(ModBlocks.WAYSTONE))
                .build(consumer, Dominium.MOD_ID + ":waystone");

        Advancement wandererTablet = Advancement.Builder.create()
                .parent(waystone)
                .display(
                        ModItems.WANDERER_TABLET,
                        Text.literal("So That's How Wandering Trades Appear!"),
                        Text.literal("Obtain a Wanderer's Tablet."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_wanderer_tablet", InventoryChangedCriterion.Conditions.items(ModItems.WANDERER_TABLET))
                .build(consumer, Dominium.MOD_ID + ":wanderer_tablet");

        Advancement idol = Advancement.Builder.create()
                .parent(soulOrb)
                .display(
                        ModBlocks.IDOL,
                        Text.literal("False Idol"),
                        Text.literal("Obtain an Idol. An Idol protects you from anyone who holds a contract over you."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_idol", InventoryChangedCriterion.Conditions.items(ModBlocks.IDOL))
                .build(consumer, Dominium.MOD_ID + ":idol");

        Advancement soulSand = Advancement.Builder.create()
                .parent(root)
                .display(
                        net.minecraft.item.Items.SOUL_SAND,
                        Text.literal("Soul Rich Sand"),
                        Text.literal("Obtain Soul Sand."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_soul_sand", InventoryChangedCriterion.Conditions.items(net.minecraft.item.Items.SOUL_SAND))
                .build(consumer, Dominium.MOD_ID + ":soul_sand");

        Advancement soulSandstone = Advancement.Builder.create()
                .parent(soulSand)
                .display(
                        ModBlocks.SOUL_SANDSTONE,
                        Text.literal("Stylish Souls"),
                        Text.literal("Obtain Soul Sandstone."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_soul_sandstone", InventoryChangedCriterion.Conditions.items(ModBlocks.SOUL_SANDSTONE))
                .build(consumer, Dominium.MOD_ID + ":soul_sandstone");

        Advancement soulGlass = Advancement.Builder.create()
                .parent(soulSand)
                .display(
                        ModBlocks.SOUL_GLASS,
                        Text.literal("Blindfolds 0.5"),
                        Text.literal("Obtain Soul Glass."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_soul_glass", InventoryChangedCriterion.Conditions.items(ModBlocks.SOUL_GLASS))
                .build(consumer, Dominium.MOD_ID + ":soul_glass");

        Advancement netherRoof = Advancement.Builder.create()
                .parent(root)
                .display(
                        net.minecraft.item.Items.NETHERRACK,
                        Text.literal("You Shouldn't Be Here"),
                        Text.literal("Venture above the Nether Roof."),
                        null, AdvancementFrame.GOAL, true, true, false
                )
                .criterion("nether_roof", new ImpossibleCriterion.Conditions())
                .build(consumer, Dominium.MOD_ID + ":nether_roof");

        Advancement contract = Advancement.Builder.create()
                .parent(soulOrb)
                .display(
                        ModItems.CONTRACT,
                        Text.literal("Dealing in Souls"),
                        Text.literal("Obtain a Contract."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_contract", InventoryChangedCriterion.Conditions.items(ModItems.CONTRACT))
                .build(consumer, Dominium.MOD_ID + ":contract");

        Advancement dominicOrb = Advancement.Builder.create()
                .parent(soulOrb)
                .display(
                        ModItems.DOMINIC_ORB,
                        Text.literal("Forbidden Magic"),
                        Text.literal("Obtain a Dominic Orb."),
                        null, AdvancementFrame.TASK, true, false, true
                )
                .criterion("has_dominic_orb", InventoryChangedCriterion.Conditions.items(ModItems.DOMINIC_ORB))
                .build(consumer, Dominium.MOD_ID + ":dominic_orb");

        Advancement dominicContract = Advancement.Builder.create()
                .parent(dominicOrb)
                .display(
                        ModItems.SOULBOUND_CONTRACT,
                        Text.literal("Life Stealer"),
                        Text.literal("Obtain a Dominic Contract."),
                        null, AdvancementFrame.TASK, true, false, true
                )
                .criterion("has_dominic_contract", InventoryChangedCriterion.Conditions.items(ModItems.SOULBOUND_CONTRACT))
                .build(consumer, Dominium.MOD_ID + ":dominic_contract");

        Advancement dominicSignedContract = Advancement.Builder.create()
                .parent(dominicContract)
                .display(
                        ModItems.SOULBOUND_CONTRACT_SIGNED,
                        Text.literal("Don't Give That Away."),
                        Text.literal("Obtain a Signed Dominic Contract. Don't give it away to anyone. Get rid of it while you still can."),
                        null, AdvancementFrame.GOAL, true, false, true
                )
                .criterion("has_signed_dominic_contract",
                        InventoryChangedCriterion.Conditions.items(ModItems.SOULBOUND_CONTRACT_SIGNED))
                .build(consumer, Dominium.MOD_ID + ":signed_dominic_contract");

        Advancement vessel = Advancement.Builder.create()
                .parent(contract)
                .display(
                        ModBlocks.VESSEL,
                        Text.literal("Masonic Voodoo"),
                        Text.literal("Obtain a Vessel. Place a Soul Fire source 2 blocks below it and insert a Contract inside it to apply bad effects to The Signer."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_vessel", InventoryChangedCriterion.Conditions.items(ModBlocks.VESSEL))
                .build(consumer, Dominium.MOD_ID + ":vessel");

        Advancement soulDebt = Advancement.Builder.create()
                .parent(vessel)
                .display(
                        ModBlocks.VESSEL,
                        Text.literal("You Owe More Than You Know"),
                        Text.literal("Have the Soul Debt effect applied to you."),
                        null, AdvancementFrame.GOAL, true, false, false
                )
                .criterion("has_soul_debt", new ImpossibleCriterion.Conditions())
                .build(consumer, Dominium.MOD_ID + ":soul_debt");

        Advancement swapper = Advancement.Builder.create()
                .parent(root)
                .display(
                        ModBlocks.SWAPPER,
                        Text.literal("Switcheroo"),
                        Text.literal("Obtain a Swapper."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_swapper", InventoryChangedCriterion.Conditions.items(ModBlocks.SWAPPER))
                .build(consumer, Dominium.MOD_ID + ":swapper");

        Advancement obelisk = Advancement.Builder.create()
                .parent(soulOrb)
                .display(
                        ModBlocks.OBELISK,
                        Text.literal("Claiming Back What's Yours."),
                        Text.literal("Obtain the Supreme Device of Soul Magic, The Obelisk."),
                        null, AdvancementFrame.CHALLENGE, true, true, false
                )
                .criterion("has_obelisk", InventoryChangedCriterion.Conditions.items(ModBlocks.OBELISK))
                .build(consumer, Dominium.MOD_ID + ":obelisk");

        Advancement signedContract = Advancement.Builder.create()
                .parent(contract)
                .display(
                        ModItems.CONTRACT_SIGNED,
                        Text.literal("Signed in Soul"),
                        Text.literal("Obtain a signed Contract."),
                        null, AdvancementFrame.TASK, true, false, false
                )
                .criterion("has_signed_contract", InventoryChangedCriterion.Conditions.items(ModItems.CONTRACT_SIGNED))
                .build(consumer, Dominium.MOD_ID + ":signed_contract");

        Advancement vassal = Advancement.Builder.create()
                .parent(soulOrb)
                .display(
                        ModItems.VASSAL_ITEM,
                        Text.literal("War Machines"),
                        Text.literal("Obtain a Vassal, a Machine devoted to its Owner."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_vassal", InventoryChangedCriterion.Conditions.items(ModItems.VASSAL_ITEM))
                .build(consumer, Dominium.MOD_ID + ":vassal");

        Advancement slayVassal = Advancement.Builder.create()
                .parent(vassal)
                .display(
                        ModItems.VASSAL_ITEM,
                        Text.literal("An End To Your Forever War"),
                        Text.literal("Slay a Vassal."),
                        null, AdvancementFrame.CHALLENGE, true, true, true
                )
                .criterion("slay_vassal", new ImpossibleCriterion.Conditions())
                .build(consumer, Dominium.MOD_ID + ":slay_vassal");

        Advancement soulCandle = Advancement.Builder.create()
                .parent(soulOrb)
                .display(
                        ModBlocks.SOUL_CANDLE,
                        Text.literal("Building in the Dark"),
                        Text.literal("Obtain a Soul Candle."),
                        null, AdvancementFrame.TASK, true, true, false
                )
                .criterion("has_soul_candle", InventoryChangedCriterion.Conditions.items(ModBlocks.SOUL_CANDLE))
                .build(consumer, Dominium.MOD_ID + ":soul_candle");

        Advancement necklaceUndead = Advancement.Builder.create()
                .parent(netherRoof)
                .display(
                        ModItems.UNDEAD_NECKLACE,
                        Text.literal("Undead"),
                        Text.literal("Obtain a Necklace of the Undead."),
                        null, AdvancementFrame.GOAL, true, true, true
                )
                .criterion("has_necklace_undead", InventoryChangedCriterion.Conditions.items(ModItems.UNDEAD_NECKLACE))
                .build(consumer, Dominium.MOD_ID + ":necklace_undead");

        Advancement necklaceArachnid = Advancement.Builder.create()
                .parent(netherRoof)
                .display(
                        ModItems.SPIDER_NECKLACE,
                        Text.literal("Arachnid"),
                        Text.literal("Obtain a Necklace of the Arachnid."),
                        null, AdvancementFrame.GOAL, true, true, true
                )
                .criterion("has_necklace_arachnid", InventoryChangedCriterion.Conditions.items(ModItems.SPIDER_NECKLACE))
                .build(consumer, Dominium.MOD_ID + ":necklace_arachnid");

        Advancement necklaceEthereal = Advancement.Builder.create()
                .parent(netherRoof)
                .display(
                        ModItems.ETHEREAL_NECKLACE,
                        Text.literal("Ethereal"),
                        Text.literal("Obtain a Necklace of the Ethereal."),
                        null, AdvancementFrame.GOAL, true, true, true
                )
                .criterion("has_necklace_ethereal", InventoryChangedCriterion.Conditions.items(ModItems.ETHEREAL_NECKLACE))
                .build(consumer, Dominium.MOD_ID + ":necklace_ethereal");
    }
}