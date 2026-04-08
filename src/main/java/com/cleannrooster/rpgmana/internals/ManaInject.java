package com.cleannrooster.rpgmana.internals;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.api.SpellcostMixinInterface;
import com.cleannrooster.rpgmana.item.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellEvents;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.internals.Ammo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCastSyncHelper;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.internals.container.SpellContainerSource;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_engine.utils.WorldScheduler;
import net.spell_power.api.SpellPower;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.*;

public class ManaInject {
    private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress){
        if(spell.cost == null){
            return 0;
        }
        if(spell.cost.item == null){
            return 0;
        }
        if(spell.cost == null || (spell.cost.item.id == null || !spell.cost.item.id.contains("rune") ||  spell.cost.item.id.contains("arrow") || spell.cost.item.id.contains("bullet") || spell.cost.item.id.contains("jacket") || spell.cost.item.id.contains("round") || spell.cost.item.id.contains("cartrdge"))){
            return 0;
        }
        float channelMultiplier = 1.0F;

        ImpactContext context = new ImpactContext(channelMultiplier, 1.0F, (Vec3d)null, SpellPower.getSpellPower(spell.school, player), SpellTarget.FocusMode.DIRECT,0);
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
        float mult = (float) (( player.getAttributeValue(Rpgmana.MANA_COST_ATTRIBUTE) * 0.01F));

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
    public static void init(){
        SpellEvents.CASTING_ATTEMPT.PRE.register(args -> {
            var player = args.caster();
            var spellEntry = args.spell();
            var spellId = spellEntry.getIdAsString();
            var itemStack = player.getStackInHand(Hand.MAIN_HAND);
            if(  !matches(spellId,Rpgmana.config.blacklist_spell_casting_regex)) {

                if( (getManaCost(player,SpellRegistry.from(player.getWorld()).get(Identifier.tryParse(spellId)), SpellCast.Action.RELEASE,1.0F)>  0  || getManaCost(player,SpellRegistry.from(player.getWorld()).get(Identifier.tryParse(spellId)), SpellCast.Action.CHANNEL,1.0F) >  0)){
                   if(((ManaInterface)player).getMana() > 0) {
                       return (attemptCasting(player, itemStack, Identifier.tryParse(spellId), false));
                   }
                   else{
                       return  SpellCast.Attempt.failMissingItem(new SpellCast.Attempt.MissingItemInfo(Ammo.Searched.from("rpgmana:mana")));
                   }

                }
            }
            return attemptCasting(player, itemStack, Identifier.tryParse(spellId), true);
        });

        SpellEvents.COST_CONSUME.register(args -> {
            var player = args.caster();
            var spellEntry = args.spell();
            var action = SpellCast.Action.RELEASE;
            var progress =  1.0F;
            var world = player.getWorld();
            var  ammoResult = Ammo.ammoForSpell(player, spellEntry.value(), args.itemStack());

            if(ammoResult.item().item() != null && ammoResult.item().item().equals(ItemInit.MANA) &&  getManaCost(player,spellEntry.value(), action,progress) > 0 &&   !matches(SpellRegistry.from(player.getWorld()).getId(spellEntry.value()).toString(), Rpgmana.config.blacklist_spell_casting_regex)) {
                if( (getManaCost(player,spellEntry.value(), SpellCast.Action.RELEASE,1.0F) > 0 )) {
                    if (player instanceof SpellCasterEntity casterEntity && player instanceof ManaInterface manaInterface) {
                        manaInterface.spendMana(-getManaCost(player, spellEntry.value(), action, progress));
                    }
                }
            }
        });
    }
}
