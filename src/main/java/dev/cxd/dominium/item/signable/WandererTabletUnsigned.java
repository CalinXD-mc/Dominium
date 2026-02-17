package dev.cxd.dominium.item.signable;

import dev.cxd.dominium.init.ModComponents;
import dev.cxd.dominium.init.ModItems;
import dev.cxd.dominium.item.CustomRarityItem;
import dev.cxd.dominium.utils.ModRarities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WandererTabletUnsigned extends CustomRarityItem {
    public WandererTabletUnsigned(Settings settings, ModRarities rarity) {
        super(settings, rarity);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack unsignedStack = user.getStackInHand(hand);

        if (ModComponents.getWandererTabletSigned(unsignedStack) != 0) {
            return TypedActionResult.fail(unsignedStack);
        }

        ItemStack signedStack = new ItemStack(ModItems.SIGNED_WANDERER_TABLET);

        ModComponents.setVesselUuid(signedStack, user.getUuid().toString());
        ModComponents.setWandererTabletSigned(signedStack, 1);
        ModComponents.setPlayerNameForSoulOwning(signedStack, user.getName().getString());
        user.setStackInHand(hand, signedStack);

        return TypedActionResult.success(signedStack, world.isClient());
    }

}
