package dev.cxd.dominium.init;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.block.WardingSoulCandleBlock;
import dev.cxd.dominium.block.entity.IdolBlockEntity;
import dev.cxd.dominium.block.entity.WardingSoulCandleBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<IdolBlockEntity> IDOL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Dominium.MOD_ID, "idol_be"),
                    FabricBlockEntityTypeBuilder.create(IdolBlockEntity::new,
                            ModBlocks.IDOL).build());

    public static final BlockEntityType<WardingSoulCandleBlockEntity> SOUL_CANDLE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Dominium.MOD_ID, "soul_candle_be"),
                    FabricBlockEntityTypeBuilder.create(WardingSoulCandleBlockEntity::new,
                            ModBlocks.SOUL_CANDLE).build());

    public static void initialize() {
        Dominium.LOGGER.info("initializing Block Entities for " + Dominium.MOD_ID);
    }
}
