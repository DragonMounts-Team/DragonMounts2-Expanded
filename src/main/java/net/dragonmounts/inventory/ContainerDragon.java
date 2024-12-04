package net.dragonmounts.inventory;

import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerDragon extends Container {
	public final DragonInventory inventory;
	private final EntityTameableDragon dragon;
	public static final int chestStartIndex = 3;

	public ContainerDragon(final EntityTameableDragon dragon, EntityPlayer player) {
		DragonInventory inventory = this.inventory = dragon.inventory;
		this.dragon = dragon;
		final int inventoryColumn = 9;
		inventory.openInventory(player);

		// location of the slot for the saddle in the dragon inventory
		this.addSlotToContainer(new Slot(inventory, 33, 8, 18) {
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.SADDLE && !this.getHasStack();
			}

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return dragon.isOldEnoughToBreathe();
			}

		});

		// location of the slot for chest in the dragon inventory
		this.addSlotToContainer(new Slot(inventory, 31, 8, 36) {
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !this.getHasStack();
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
		this.addSlotToContainer(new Slot(inventory, 32, 8, 54) {

			public boolean isItemValid(ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() instanceof DragonArmorItem && dragon.isOldEnoughToBreathe();
			}

		});

		// Build Banner Slots
		for (int b = 0; b < 4; ++b) {
			this.addSlotToContainer(new Slot(inventory, 27 + b, b == 1 || b == 2 ? 134 : 152, b < 2 ? 36 : 18) {
				public boolean isItemValid(ItemStack stack) {
					return stack.getItem() == Items.BANNER && !this.getHasStack();
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
				this.addSlotToContainer(new Slot(inventory, l + k * inventoryColumn, 8 + l * 18, 75 + k * 18) {

					@SideOnly(Side.CLIENT)
					public boolean isEnabled() {
						return ContainerDragon.this.dragon.isChested();
					}

				});
			}
		}
		
		/*
		 * Player Inventory Slots within Dragon GUI
		 */
		// Offhand
		this.addSlotToContainer(new Slot(player.inventory, 40, -14, 190));
		// Build Inventory Slots
		for (int j = 0; j < 3; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 8 + k * 18, 150 + j * 18 - 18));
			}
		}
		// Build hotbar slots
		for (int j = 0; j < 9; ++j) {
			this.addSlotToContainer(new Slot(player.inventory, j, 8 + j * 18, 190));
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.inventory.isUsableByPlayer(playerIn) && this.dragon.isEntityAlive() && this.dragon.getDistance(playerIn) < 16.0F;
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();
			Slot target;
			int size = this.inventory.getSizeInventory();
			if (index < size) {
				if (!this.mergeItemStack(stack, size, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(2)).isItemValid(stack) && !target.getHasStack()) {
				if (!this.mergeItemStack(stack, 2, 3, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(1)).isItemValid(stack) && !target.getHasStack()) {
				if (!this.mergeItemStack(stack, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if ((target = this.getSlot(0)).isItemValid(stack) && !target.getHasStack()) {
				if (!this.mergeItemStack(stack, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(stack, 0, size, false)) {
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

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.inventory.closeInventory(playerIn);
	}
}