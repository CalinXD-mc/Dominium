package dev.cxd.dominium.client.screen;

import dev.cxd.dominium.Dominium;
import dev.cxd.dominium.init.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoulboundContractScreen extends Screen {

    private final int itemSlot;

    private static final Identifier BUTTON_TEXTURE =
            new Identifier(Dominium.MOD_ID, "textures/gui/button_normal.png");
    private static final Identifier BUTTON_HOVERED_TEXTURE =
            new Identifier(Dominium.MOD_ID, "textures/gui/button_hovered.png");

    public SoulboundContractScreen(int itemSlot) {
        super(Text.literal("Dominic Contract"));
        this.itemSlot = itemSlot;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 24;
        int startY = centerY - (spacing * 2);

        this.addDrawableChild(new TexturedButtonWidget(
                centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight,
                Text.literal("Deal Damage").styled(style -> style.withColor(0xFF4444)),
                btn -> sendAction(0),
                BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE));

        this.addDrawableChild(new TexturedButtonWidget(
                centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight,
                Text.literal("Teleport to Target").styled(style -> style.withColor(0x9BC0FF)),
                btn -> sendAction(1),
                BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE));

        this.addDrawableChild(new TexturedButtonWidget(
                centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight,
                Text.literal("Set on Fire").styled(style -> style.withColor(0xD68B0C)),
                btn -> sendAction(2),
                BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE));

        this.addDrawableChild(new TexturedButtonWidget(
                centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight,
                Text.literal("Trap in Chains").styled(style -> style.withColor(0xF8D16D)),
                btn -> sendAction(3),
                BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE));

        this.addDrawableChild(new TexturedButtonWidget(
                centerX - buttonWidth / 2, startY + spacing * 4, buttonWidth, buttonHeight,
                Text.literal("Remove Soul Strain").styled(style -> style.withColor(0x44FF88)),
                btn -> sendAction(4),
                BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE));

        this.addDrawableChild(new TexturedButtonWidget(
                centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight,
                Text.literal("Force Sign Contract").styled(style -> style.withColor(0xCC88FF)),
                btn -> sendAction(5),
                BUTTON_TEXTURE, BUTTON_HOVERED_TEXTURE));
    }

    private void sendAction(int action) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(action);
        buf.writeInt(itemSlot);
        ClientPlayNetworking.send(ModPackets.SOULBOUND_ACTION_ID, buf);
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int panelWidth = 220;
        int panelHeight = 204;
        int panelX = this.width / 2 - panelWidth / 2;
        int panelY = this.height / 2 - panelHeight / 2;
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x55000000);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFF886644);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Dominic Contract").styled(style -> style.withColor(0xFFF09A)),
                this.width / 2,
                panelY + 8,
                0xFFFFFF
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}