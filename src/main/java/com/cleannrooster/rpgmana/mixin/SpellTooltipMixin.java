package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.SpellcostMixinInterface;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.fabric.mixin.item.client.ItemStackMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.ImpactContext;

@Mixin(SpellTooltip.class)
public class SpellTooltipMixin {
    private static float getManaCost(PlayerEntity player,Spell spell, ImpactContext context, float coeff, int proj){
        float mult = (float) ((player.getAttributeValue(Rpgmana.MANA_COST_ATTRIBUTE) * 0.01F));

        return mult* context.total()*(((SpellcostMixinInterface)spell.cost).calculateManaCost() ? (float) Math.max( 20 , 40  * coeff * proj ) :
        ((SpellcostMixinInterface)spell.cost).getManaCost());
    }

    private static boolean matches(String subject, String nullableRegex) {
        if (subject == null) {
            return false;
        }
        if (nullableRegex == null || nullableRegex.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(nullableRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }

    @Inject(at = @At("RETURN"), method = "addSpellDetails")
    private static void addSpellDetailsRPGMana(RegistryEntry<Spell> spellEntry, PlayerEntity player, ItemStack itemStack, int indentLevel, ArrayList<Text> lines, CallbackInfo callbackInfo) {

        try {
            Spell spell = spellEntry.value();
            float channelMultiplier = 1.0F;
            boolean needsArrow = false;
            if (spell.cost != null &&  spell.cost.item != null && spell.cost.item.id != null) {
                needsArrow = spell.cost.item.id.contains("arrow");
            }

            if (!matches(SpellRegistry.from(player.getWorld()).getId(spell).toString(),Rpgmana.config.blacklist_spell_casting_regex) &&!(needsArrow) && spell.release != null && spell.cost != null && spell.cost.item != null &&  spell.cost.item.id != null) {
                ImpactContext context = new ImpactContext(channelMultiplier, 1.0F, (Vec3d) null, SpellPower.getSpellPower(spell.school, player), SpellTarget.FocusMode.DIRECT,0);

                float coeff = 0;
                int proj = 1;
                if(spell.impacts != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost()) {

                    for (Spell.Impact impact : spell.impacts) {
                        if (impact.action != null && impact.action.damage != null) {
                            coeff += impact.action.damage.spell_power_coefficient;
                        }
                    }
                    if (spell.impacts != null && spell.impacts.size() > 0) {
                        coeff /= spell.impacts.size();
                    }
                    if (spell.deliver != null && spell.deliver.projectile != null) {
                        proj += spell.deliver.projectile.launch_properties.extra_launch_count;
                    }
                }
                MutableText text = Text.literal(" ").append(Text.translatable("rpgmana.manacost").append(Text.literal(": ").append(String.valueOf((int) Math.round(getManaCost(player, spell, context, coeff, proj))))));
                if (spell.active.cast.channel_ticks > 0) {
                    text.append(Text.translatable("rpgmana.persecond"));
                }
                lines.add(text);
            }
        } catch (Exception ignore) {
        }
    }
}
