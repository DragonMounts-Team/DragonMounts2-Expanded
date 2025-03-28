package net.dragonmounts.inventory;

import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IWhistleHolder;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.util.LogUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DragonContainer<T extends TameableDragonEntity> extends Container {
	public final DragonInventory inventory;
	public final WhistleSlot whistle;
	public final T dragon;
	public final EntityPlayer player;

	public DragonContainer(final T dragon, EntityPlayer player) {
		DragonInventory inventory = this.inventory = dragon.inventory;
		this.dragon = dragon;
		inventory.openInventory(this.player = player);
		IWhistleHolder whistle = player.getCapability(DMCapabilities.WHISTLE_HOLDER, null);
		if (whistle == null) {
			LogUtil.LOGGER.error("Player {} does NOT have a whistle holder", player);
		} else {
			whistle.openInventory(player);
		}
		this.addSlotToContainer(this.whistle = new WhistleSlot(whistle, this, 8, 8));
		// location of the slot for the saddle in the dragon inventory
		this.addSlotToContainer(new Slot(inventory, 33, 156, 18) {
			public boolean isItemValid(ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() == Items.SADDLE && !this.getHasStack();
			}

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return dragon.isOldEnoughToBreathe();
			}

			@Override
			public boolean canTakeStack(EntityPlayer player) {
				return dragon.getPassengers().isEmpty();
			}

			@Override
			public int getSlotStackLimit() {
				return 1;
			}
		});

		// location of the slot for chest in the dragon inventory
		this.addSlotToContainer(new Slot(inventory, 31, 156, 36) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return DragonInventory.isValidChest(stack) && !this.getHasStack();
			}

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return dragon.isOldEnoughToBreathe();
			}

			@Override
			public int getSlotStackLimit() {
				return 1;
			}
		});

		// location of the slot for armor in the dragon inventory
		this.addSlotToContainer(new Slot(inventory, 32, 156, 54) {
			public boolean isItemValid(ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() instanceof DragonArmorItem && dragon.isOldEnoughToBreathe();
			}
		});

		// Build Banner Slots
		for (int b = 0; b < 4; ++b) {
			this.addSlotToContainer(new Slot(inventory, 27 + b, b == 1 || b == 2 ? 282 : 300, b < 2 ? 36 : 18) {
				public boolean isItemValid(ItemStack stack) {
					return !stack.isEmpty() && stack.getItem() == Items.BANNER && !this.getHasStack();
				}

				@SideOnly(Side.CLIENT)
				public boolean isEnabled() {
					return dragon.isOldEnoughToBreathe();
				}
			});
		}

		// Build Chest Inventory Slots
		for (int k = 0; k < 3; ++k) {
			for (int l = 0; l < 9; ++l) {
				this.addSlotToContainer(new Slot(inventory, l + k * 9, 156 + l * 18, 75 + k * 18) {

					@SideOnly(Side.CLIENT)
					public boolean isEnabled() {
						return DragonContainer.this.dragon.isChested();
					}

				});
			}
		}
		
		/*
		 * Player Inventory Slots within Dragon GUI
		 */
		// Build Inventory Slots
		for (int j = 0; j < 3; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 156 + k * 18, 150 + j * 18 - 18));
			}
		}
		// Build hotbar slots
		for (int j = 0; j < 9; ++j) {
			this.addSlotToContainer(new Slot(player.inventory, j, 156 + j * 18, 190));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.inventory.isUsableByPlayer(player) && this.dragon.isEntityAlive() && this.dragon.getDistanceSq(player) < 256.0F;
	}

	/// @return the remaining stack in the slot
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();
			Slot target;
			int size = this.inventory.getSizeInventory();
			if (index < size) {
				// move item form dragon's inventory to player's
				if (!this.mergeItemStack(stack, size, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(3)).isItemValid(stack) && !target.getHasStack()) {
				// move item to armor
				if (!this.mergeItemStack(stack, 3, 4, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(2)).isItemValid(stack) && !target.getHasStack()) {
				// move item as chest
				if (!this.mergeItemStack(stack, 2, 3, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(1)).isItemValid(stack) && !target.getHasStack()) {
				// move item as saddle
				if (!this.mergeItemStack(stack, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(0)).isItemValid(stack) && !target.getHasStack()) {
				// move item as whistle
				if (!this.mergeItemStack(stack, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.dragon.isChested() ||
					// move item to slots in chest
					!this.mergeItemStack(stack, 3, size, false)
			) {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return result;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		this.inventory.closeInventory(player);
		IWhistleHolder holder = this.whistle.holder;
		if (holder != null) {
			holder.closeInventory(player);
		}
	}
}