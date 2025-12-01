package dev.cxd.dominium.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Random;

public class HopelessEffect extends StatusEffect {

    private static final List<String> VARIANTS = List.of(
            "...",
            "You wish you could go back...",
            "You feel sick...",
            "What have I done...? ",
            "Regret...",
            "Emotionless...",
            "Empty...",
            "There is nothing left...",
            "<CONTENT ERASED> is truly gone...",
            "Even this was repurposed...",
            "They’ve all moved on without <CONTENT ERASED>...",
            "They’ve all forgotten <CONTENT ERASED>...",
            "It’s your fault...",
            "They will all find out...",
            "You will be judged...",
            "They will never like you again...",
            "You repurposed <CONTENT ERASED> into nothingness...",
            "Hopelessness..."
    );

    private String cachedName = VARIANTS.get(0);
    private int tickCounter = 0;


    public HopelessEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x000000);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        tickCounter++;
        if (tickCounter >= 20) {
            cachedName = VARIANTS.get(new Random().nextInt(VARIANTS.size()));
            tickCounter = 0;
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public Text getName() {
        return Text.literal(cachedName);
    }

}
