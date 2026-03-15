package dev.cxd.dominium.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TexturedButtonWidget extends ButtonWidget {

    private final Identifier texture;
    private final Identifier hoveredTexture;

    public TexturedButtonWidget(int x, int y, int width, int height,
                                Text text, PressAction onPress,
                                Identifier texture, Identifier hoveredTexture) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier tex = this.isHovered() ? hoveredTexture : texture;

        context.drawTexture(tex, this.getX(), this.getY(), 0, 0,
                this.width, this.height, this.width, this.height);

        int textColor = this.active ? 0xFFFFFF : 0xAAAAAA;
        context.drawCenteredTextWithShadow(
                net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                textColor
        );
    }
}