package dev.cammiescorner.arcanuscontinuum.common.spell_components.shapes;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.cammiescorner.arcanuscontinuum.api.spells.SpellEffect;
import dev.cammiescorner.arcanuscontinuum.api.spells.SpellGroup;
import dev.cammiescorner.arcanuscontinuum.api.spells.SpellShape;
import dev.cammiescorner.arcanuscontinuum.api.spells.Weight;
import dev.cammiescorner.arcanuscontinuum.common.util.ArcanusHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class TouchSpellShape extends SpellShape {
	public TouchSpellShape(boolean isEnabled, Weight weight, double manaCost, double manaMultiplier, int coolDown, int minLevel, double potencyModifier) {
		super(isEnabled, weight, manaCost, manaMultiplier, coolDown, minLevel, potencyModifier);
	}

	@Override
	public void cast(@Nullable LivingEntity caster, Vec3d castFrom, @Nullable Entity castSource, ServerWorld world, ItemStack stack, List<SpellEffect> effects, List<SpellGroup> spellGroups, int groupIndex, double potency) {
		potency += getPotencyModifier();
		double range = caster != null ? ReachEntityAttributes.getAttackRange(caster, caster instanceof PlayerEntity player && player.isCreative() ? 5 : 4.5) : 4.5;
		Entity sourceEntity = castSource != null ? castSource : caster;
		HitResult target = ArcanusHelper.raycast(sourceEntity, range, true, true);

		if(target.getType() != HitResult.Type.MISS)
			for(SpellEffect effect : new HashSet<>(effects))
				effect.effect(caster, sourceEntity, world, target, effects, stack, potency);

		Entity targetEntity = target.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) target).getEntity() : castSource;
		castNext(caster, target.getPos(), targetEntity, world, stack, spellGroups, groupIndex, potency);
	}
}
