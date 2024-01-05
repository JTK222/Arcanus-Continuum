package dev.cammiescorner.arcanuscontinuum.common.spell_components.effects.utility;

import dev.cammiescorner.arcanuscontinuum.api.spells.SpellEffect;
import dev.cammiescorner.arcanuscontinuum.api.spells.SpellType;
import dev.cammiescorner.arcanuscontinuum.api.spells.Weight;
import dev.cammiescorner.arcanuscontinuum.common.registry.ArcanusSpellComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GrowthSpellEffect extends SpellEffect {
	public GrowthSpellEffect(boolean isEnabled, SpellType type, Weight weight, double manaCost, int coolDown, int minLevel) {
		super(isEnabled, type, weight, manaCost, coolDown, minLevel);
	}

	@Override
	public void effect(@Nullable LivingEntity caster, @Nullable Entity sourceEntity, World world, HitResult target, List<SpellEffect> effects, ItemStack stack, double potency) {
		int growthCount = (int) (effects.stream().filter(ArcanusSpellComponents.GROWTH::is).count() * potency);

		if(target.getType() == HitResult.Type.ENTITY) {
			EntityHitResult entityHit = (EntityHitResult) target;

			if(entityHit.getEntity() instanceof AnimalEntity animal && animal.isBaby())
				animal.growUp(PassiveEntity.getGrowthSeconds(-animal.getBreedingAge()) * growthCount, true);
		}
		else if(target.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) target;
			BlockPos pos = blockHit.getBlockPos().offset(blockHit.getSide());

			for(int i = 0; i < growthCount; i++) {
				BoneMealItem.useOnFertilizable(ItemStack.EMPTY, world, pos);
				BoneMealItem.useOnGround(ItemStack.EMPTY, world, pos, blockHit.getSide());
			}
		}
	}
}
