package dev.cammiescorner.arcanuscontinuum.common.recipes;

import com.google.common.collect.Lists;
import dev.cammiescorner.arcanuscontinuum.Arcanus;
import dev.cammiescorner.arcanuscontinuum.api.spells.Spell;
import dev.cammiescorner.arcanuscontinuum.common.items.SpellBookItem;
import dev.cammiescorner.arcanuscontinuum.common.items.StaffItem;
import dev.cammiescorner.arcanuscontinuum.common.registry.ArcanusRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class SpellBindingRecipe extends SpecialCraftingRecipe {
	public SpellBindingRecipe(Identifier identifier, CraftingCategory category) {
		super(identifier, category);
	}

	@Override
	public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
		DefaultedList<ItemStack> list = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

		for(int i = 0; i < list.size(); ++i)
			if(inventory.getStack(i).getItem() instanceof SpellBookItem)
				list.set(i, inventory.getStack(i).copy());

		return list;
	}

	@Override
	public boolean matches(RecipeInputInventory inv, World world) {
		List<ItemStack> list = Lists.newArrayList();
		ItemStack result = ItemStack.EMPTY;

		for(int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getStack(i);

			if(!stack.isEmpty()) {
				Item item = stack.getItem();

				if(item instanceof StaffItem) {
					if(!result.isEmpty() || i != 4)
						return false;

					result = stack.copy();
				}
				else {
					if(!(item instanceof SpellBookItem) || i == 4)
						return false;

					list.add(stack);
				}
			}
		}

		return !result.isEmpty() && !list.isEmpty();
	}


	@Override
	public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager manager) {
		DefaultedList<Spell> spells = DefaultedList.ofSize(8, new Spell());
		ItemStack result = ItemStack.EMPTY;

		if(inv.getStack(4).getItem() instanceof StaffItem) {
			if(!result.isEmpty())
				return ItemStack.EMPTY;

			result = inv.getStack(4).copy();
		}

		for(int i = 0; i < inv.size(); i++) {
			if(i == 4)
				continue;

			ItemStack stack = inv.getStack(i);

			if(!stack.isEmpty()) {
				Spell spell = new Spell();
				int[] indices = new int[] { 7, 0, 1, 6, 0, 2, 5, 4, 3 };

				if(stack.getItem() instanceof SpellBookItem)
					spell = SpellBookItem.getSpell(stack);

				spells.set(indices[i], spell);
			}
		}

		return !result.isEmpty() && !spells.isEmpty() ? applySpells(result, spells) : ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height == 9;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ArcanusRecipes.SPELL_BINDING.get();
	}

	private ItemStack applySpells(ItemStack stack, List<Spell> spells) {
		NbtCompound tag = stack.getSubNbt(Arcanus.MOD_ID);

		if(tag == null || tag.isEmpty())
			return ItemStack.EMPTY;

		NbtList list = tag.getList("Spells", NbtElement.COMPOUND_TYPE);

		for(int i = 0; i < spells.size(); i++) {
			Spell spell = spells.get(i);

			if(spell.getComponentGroups().get(0).isEmpty())
				continue;

			list.set(i, spell.toNbt());
		}

		tag.put("Spells", list);

		return stack;
	}
}
