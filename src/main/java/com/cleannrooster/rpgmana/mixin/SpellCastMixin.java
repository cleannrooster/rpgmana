package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.api.SpellcostMixinInterface;
import com.cleannrooster.rpgmana.item.ItemInit;
import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.config.ServerConfig;
import net.spell_engine.internals.SpellCastSyncHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.internals.WorldScheduler;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_power.api.SpellPower;
import net.spell_power.api.enchantment.SpellPowerEnchanting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.*;

@Mixin(SpellHelper.class)
public class SpellCastMixin {

    private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress){

        float channelMultiplier = 1.0F;
        switch (action) {
            case CHANNEL:
                channelMultiplier = channelValueMultiplier(spell);
                break;
            case RELEASE:
                if (isChanneled(spell)) {
                    channelMultiplier = 1.0F;
                } else {
                    channelMultiplier = progress >= 1.0F ? 1.0F : 0.0F;
                }

        }
        ImpactContext context = new ImpactContext(channelMultiplier, 1.0F, (Vec3d)null, SpellPower.getSpellPower(spell.school, player), impactTargetingMode(spell));
        float coeff = 0;
        int proj = 1;
        if(spell.impact != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost() ==true) {

            for (Spell.Impact impact : spell.impact) {
                if (impact.action != null && impact.action.damage != null) {
                    coeff += impact.action.damage.spell_power_coefficient;
                }
            }
            if (spell.impact != null && spell.impact.length > 0) {
                coeff /= spell.impact.length;
            }
            if (spell.release != null && spell.release.target != null && spell.release.target.projectile != null) {
                proj += spell.release.target.projectile.launch_properties.extra_launch_count;
            }
        }
        float mult = (float) ((Rpgmana.config.inspiration*0.01 * SpellPowerEnchanting.getEnchantmentLevel(Rpgmana.ARCHMAGE, player,player.getMainHandStack()) - Rpgmana.config.manastabilized*0.01* SpellPowerEnchanting.getEnchantmentLevel(Rpgmana.MANASTABILIZED, player,player.getMainHandStack())) + player.getAttributeValue(Rpgmana.MANACOST) * 0.01F);

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

    private static void ammoForSpellMana(PlayerEntity player, Spell spell, ItemStack itemStack, CallbackInfoReturnable<AmmoResult> returnable) {
        boolean satisfied = true;
        ItemStack ammo = null;
        Identifier identifier = null;
        if(player.hasStatusEffect(Rpgmana.BORROWEDPOWER) || (player instanceof ManaInterface manaInterface && manaInterface.getMana() > 0)) {
            for(Map.Entry<Identifier, SpellRegistry.SpellEntry> spell1 : SpellRegistry.all().entrySet()){
                if(SpellRegistry.getSpell(spell1.getKey()).equals(spell)){
                    identifier = spell1.getKey();
                }
            }
            if(identifier != null && !matches(identifier.toString(),Rpgmana.config.blacklist_spell_casting_regex)) {
                ammo = ItemInit.MANA.getDefaultStack();
                returnable.setReturnValue(new AmmoResult(satisfied, ammo));
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)
    private static void performSpell(World world, PlayerEntity player, Identifier spellId, List<Entity> targets, SpellCast.Action action, float progress, CallbackInfo info) {
        Spell spell = SpellRegistry.getSpell(spellId);
        if(!matches(spellId.toString(),Rpgmana.config.blacklist_spell_casting_regex) && player instanceof ManaInterface manaInterface && spell.release != null && spell.cost != null && spell.cost.item_id != null && !spell.cost.item_id.contains("arrow") ){
            SpellCast.Attempt attempt = attemptCasting(player, player.getMainHandStack(), spellId);
            if(attempt.isSuccess()) {
                progress = Math.max(Math.min(progress, 1.0F), 0.0F);
                float channelMultiplier = 1.0F;
                boolean shouldPerformImpact = true;
                switch (action) {
                    case CHANNEL:
                        channelMultiplier = channelValueMultiplier(spell);
                        break;
                    case RELEASE:
                        if (isChanneled(spell)) {
                            channelMultiplier = 1.0F;
                        } else {
                            channelMultiplier = progress >= 1.0F ? 1.0F : 0.0F;
                        }

                        SpellCastSyncHelper.clearCasting(player);
                }
                float finalProgress = progress;
                if( ammoForSpell(player, spell, player.getMainHandStack()).ammo() != null
                        && ammoForSpell(player, spell, player.getMainHandStack()).ammo().getItem() == ItemInit.MANA && player instanceof SpellCasterEntity casterEntity) {

                    ((WorldScheduler) world).schedule(1, () -> {
                        if(!casterEntity.getCooldownManager().isCoolingDown(spellId)&& action== SpellCast.Action.RELEASE) {
                            casterEntity.getCooldownManager().set(spellId, 2);
                        }

                        manaInterface.spendMana(-getManaCost(player, spell, action, finalProgress));
                    });
                }

            }
            else {
                Identifier id ;
                boolean needsArrow = false;
                if(spell.cost != null) {
                    id = new Identifier(spell.cost.item_id);
                    if(spell.cost.item_id != null)
                    needsArrow = id.getPath().contains("arrow");
                }

                if (SpellHelper.ammoForSpell(player, spell, player.getMainHandStack()).satisfied() || needsArrow) {
                    return;
                }
                Supplier<Collection<ServerPlayerEntity>> trackingPlayers = Suppliers.memoize(() -> {
                    return PlayerLookup.tracking(player);
                });
                ItemStack itemStack = player.getMainHandStack();


                ParticleHelper.sendBatches(player, spell.release.particles);
                SoundHelper.playSound(world, player, spell.release.sound);
                float castingSpeed = ((SpellCasterEntity) player).getCurrentCastingSpeed();

                AnimationHelper.sendAnimation(player, (Collection) trackingPlayers.get(), SpellCast.Animation.RELEASE, spell.release.animation, castingSpeed);
                imposeCooldown(player, spellId, spell, 1.0F);
                player.addExhaustion(spell.cost.exhaust * SpellEngineMod.config.spell_cost_exhaust_multiplier);
                if (SpellEngineMod.config.spell_cost_durability_allowed && spell.cost.durability > 0) {
                    itemStack.damage(spell.cost.durability, player, (playerObj) -> {
                        playerObj.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
                        playerObj.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND);
                    });
                }


                if (spell.cost.effect_id != null) {
                    StatusEffect effect = (StatusEffect) Registries.STATUS_EFFECT.get(new Identifier(spell.cost.effect_id));
                    player.removeStatusEffect(effect);
                }
                SpellCastSyncHelper.clearCasting(player);

                info.cancel();
            }

        }
    }
}
