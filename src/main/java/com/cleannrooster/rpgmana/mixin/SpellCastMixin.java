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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.config.ServerConfig;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.internals.Ammo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCastSyncHelper;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.internals.container.SpellContainerSource;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.AnimationHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.WorldScheduler;
import net.spell_power.api.SpellPower;
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

import static net.spell_engine.internals.Ammo.ammoForSpell;
import static net.spell_engine.internals.SpellHelper.*;

@Mixin(SpellHelper.class)
public class SpellCastMixin {


     @Inject(at = @At("HEAD"), method = "attemptCasting", cancellable = true)

    private static void attemptCastingRPGMana(PlayerEntity player, ItemStack itemStack, Identifier spellId, CallbackInfoReturnable<SpellCast.Attempt> attemptCallbackInfoReturnable) {

    }
    @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)
    private static void performSpellRPGMana(World world, PlayerEntity player, RegistryEntry<Spell> spellEntry, SpellTarget.SearchResult targetResult, SpellCast.Action action, float progress, CallbackInfo ci) {

    }

 /*   @Inject(at = @At("HEAD"), method = "ammoForSpell", cancellable = true)

    private static void ammoForSpellMana(PlayerEntity player, Spell spell, ItemStack itemStack, CallbackInfoReturnable<Ammo.Result> returnable) {
        boolean satisfied = true;
        Ammo.Result ammoResult = ammoForSpell(player, spell, itemStack);
        Identifier identifier = null;
        if( (player instanceof ManaInterface manaInterface && manaInterface.getMana() > 0)) {



            if(  !matches(SpellRegistry.from(player.getWorld()).getId(spell).toString(),Rpgmana.config.blacklist_spell_casting_regex)) {
                     ammoResult = ammoForSpell(player, spell, ItemInit.MANA.getDefaultStack();
                List<Ammo.Source> sources = List.of();
                Ammo.Searched ammo = Ammo.Searched.from(Identifier.of(Rpgmana.MOD_ID,"mana").toString());

                returnable.setReturnValue(new Ammo.Result(satisfied, ammo,0,sources));
            }
        }
    }*/
  /*  @Inject(at = @At("HEAD"), method = "performSpell", cancellable = true)
    public static void performSpellRPGMana(World world, PlayerEntity player, RegistryEntry<Spell> spellEntry, SpellTarget.SearchResult targetResult, SpellCast.Action action, float progress, CallbackInfo info) {
        Spell spell = spellEntry.value();
        Identifier spellId = SpellRegistry.from(player.getWorld()).getId(spellEntry.value());
        if(!matches(SpellRegistry.from(player.getWorld()).getId(spellEntry.value()).toString(),Rpgmana.config.blacklist_spell_casting_regex) &&
                player instanceof ManaInterface manaInterface &&
                spell.release != null &&
                spell.cost != null && spell.cost.item.id != null && !spell.cost.item.id.contains("arrow") &&
                !(targetResult.entities().isEmpty() && spell.target.type.equals(Spell.Target.Type.AIM)) ){
            SpellCast.Attempt attempt = attemptCasting(player, player.getMainHandStack(), SpellRegistry.from(player.getWorld()).getId(spellEntry.value()));
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

*//*
                        SpellCastSyncHelper.clearCasting(player);
*//*
                }

                float finalProgress = progress;
                if( ammoForSpell(player, spell, player.getMainHandStack()).item() != null
                        && ammoForSpell(player, spell, player.getMainHandStack()).item().item() == ItemInit.MANA && player instanceof SpellCasterEntity casterEntity) {

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
                    id = Identifier.of(spell.cost.item.id);
                    if(spell.cost.item.id != null)
                    needsArrow = id.getPath().contains("arrow");
                }

                if (Ammo.ammoForSpell(player, spell, player.getMainHandStack()).satisfied() || needsArrow) {
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
                if ( world instanceof ServerWorld && SpellEngineMod.config.spell_cost_durability_allowed && spell.cost.durability > 0) {
                    itemStack.damage(spell.cost.durability, (ServerWorld) player.getWorld(),(ServerPlayerEntity)player, (playerObj) -> {

                    });
                }


                if (spell.cost.effect_id != null) {
                    StatusEffect effect = (StatusEffect) Registries.STATUS_EFFECT.get(Identifier.of(spell.cost.effect_id));
                    player.removeStatusEffect(RegistryEntry.of(effect));
                }

                info.cancel();
            }

        }
    }*/
}
