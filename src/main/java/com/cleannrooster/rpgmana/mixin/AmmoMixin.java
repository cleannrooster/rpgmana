package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.api.SpellcostMixinInterface;
import com.cleannrooster.rpgmana.item.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.internals.Ammo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.channelValueMultiplier;
import static net.spell_engine.internals.SpellHelper.isChanneled;

@Mixin(Ammo.class)
public class AmmoMixin {

    private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress){
        if(spell.cost == null){
            return 0;
        }
        if(spell.cost.item == null){
            return 0;
        }
        if(  ( !spell.cost.item.id.contains("rune") ||  spell.cost.item.id.contains("arrow") || spell.cost.item.id.contains("bullet") || spell.cost.item.id.contains("jacket") || spell.cost.item.id.contains("round") || spell.cost.item.id.contains("cartrdge"))){
            return 0;
        }
        float channelMultiplier = 1.0F;

        SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(channelMultiplier, 1.0F, (Vec3d)null, SpellPower.getSpellPower(spell.school, player), SpellTarget.FocusMode.DIRECT,0);
        float coeff = 0;
        int proj = 1;
        if(spell.impacts != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost() ==true) {

            for (Spell.Impact impact : spell.impacts) {
                if (impact.action != null && impact.action.damage != null) {
                    coeff += impact.action.damage.spell_power_coefficient;
                }
            }
            if (spell.impacts != null && spell.impacts.size() > 0) {
                coeff /= spell.impacts.size();
            }
            if (spell.release != null && spell.target != null && spell.deliver != null && spell.deliver.projectile != null) {
                proj += spell.deliver.projectile.launch_properties.extra_launch_count;
            }
        }
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

    @Inject(at = @At("HEAD"), method = "ammoForSpell", cancellable = true)
    private static void ammoForSpellRPG(PlayerEntity player, Spell spell, ItemStack casterStack, CallbackInfoReturnable<Ammo.Result> cir) {
        if(((ManaInterface)player).getMana() > 0 &&  !matches(SpellRegistry.from(player.getWorld()).getId(spell).toString(), Rpgmana.config.blacklist_spell_casting_regex)) {
            if(spell.cost != null && spell.cost.item != null && ((ManaInterface)player).getMana() > 0 && (getManaCost(player,spell, SpellCast.Action.RELEASE,1.0F) > 0 || getManaCost(player,spell, SpellCast.Action.CHANNEL,1.0F) > 0)) {
                cir.setReturnValue(new Ammo.Result(true, Ammo.Searched.from("rpgmana:mana"), 0, List.of()));
            }
        }
}}
