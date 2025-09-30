# Changelog

## [2.1.2] - 2025-09-30

### Fixes

- Owner name of whistle won't get converted into uuid if uuid already exists.
- Riding can properly consume interaction to prevent inventory opening.
- Riding now syncs on client side.
- Dragon can be saved after dismounting from a disconnected local player.

## [2.1.1] - 2025-08-09

### Changes

- Axes can mine Nest Block faster.
- Dragon Heads can be dispensed.
- Make dragons being tempted by food more frequently.

### Fixes

- Navigation will stop if close enough to adult.
- Dragon variant will be loaded earlier.

## [2.1.0] - 2025-07-20

### Additions

#### Blocks

- Add Blocks of Dragon Scale.
  - Regarded as the base of Beacon.
- Add Dragon Head blocks.

#### Items

- Add Raw Dragon Meat and Cooked Dragon Meat.
  - ~~Dragons won't eat them~~

#### Gameplay

- Add dark dragon type.
  - It has 2 dragon variants.
- Add Dark Dragon's Grace status effect.
  - It is obtained by equipping Dark Dragon Scale Armor set.
- Dragon Breath can be collected when interacting with tamed dragon using Glass Bottle.

#### Configuration

- Add following configuration options:
  - `baseArmorToughness`
  - `baseBodySize`
  - `baseFlyingSpeed`
  - `baseFollowRange`
  - `baseKnockbackResistance`
  - `baseMovementSpeed`
  - `baseSwimmingSpeed`
  - `pauseOnWhistle`
  - `redirectInventory`
  - `reproductionLimit`

#### Technical

- Add Data Fix for Dragon Mounts 2.
- Add compatibility to [Patchouli](https://github.com/VazkiiMods/Patchouli).
- Add command `/dragonmounts book`.
  - Only available when Patchouli is installed.
- Add `/dragonmounts size` command.
- `AmuletItem`, `DragonEssenceItem` and `DragonSpawnEggItem` now implement `IEntityContianer` interface.
- Add following capabilities:
  - `HARD_SHEARS`
  - `DRAGON_FOOD`
  - `WHISTLE_HOLDER`
- Add following registries:
  - `CarriageType`
  - `DragonType`
  - `DragonVariant`

### Changes

#### Blocks & Items

- Dragon Armors
  - They have tooltips now.
  - They provide defense for dragons, instead of blocking damage.
- Dragon Whistle
  - It can be used to order dragon to or not to follow owner.
  - Trusted players can use it to order the dragon.
  - Attacks towards dragon with it won't be blocked and won't make it bound.
  - It can be bound to dragon via the dragon's inventory.
  - Its name won't get automatically changed when binding.
  - It can be renamed freely via dragon's inventory.
  - It will try to teleport dragon to the top of the block at which player looking.
- Dragon Scale Bows
  - Their durabilities is related to their type.
  - One with enchanted dragon type have higher enchantability now.
- Dragon Scale Shields
  - Their durabilities is related to their type.
  - One with enchanted dragon type can be enchanted via Enchanting Table.
- Dragon Essence will release dragon when destoryed.
- Nether Dragon Egg block is slightly brighter.
- Nest Block will be regarded as habitat.
- Vanilla Dragon Egg will be placed instead of modded one when a Dragon Egg from vanilla Dragon Egg stops hatching.

#### Dragon

- Behaior
  - They are able to be steered now.
  - Reduced the heigth of skeleton dragon habitat. (25% -> 20%)
  - They won't attack Dragon Egg anymore.
  - Tamed dragon won't attack tamed horse anymore.
  - They have different step height depending on its size.
  - The Water Breathing effect for water dragon rider who is under water will be reset earlier.
  - Breeding dragons will spawn experience orbs now.
  - They will begin to hunt before they are fully grown.
  - They can be fed with item stack that has `DRAGON_FOOD` capability.
  - Feeding can heal/hurt dragons and affect their growth time.
  - Dragon food can be used to tempt baby dragons.
  - Tamed dragon will drop dragon scales when killed as well.
  - Adjust the bonus to reduce the speed when boosted flying.
  - Moonlight Dragons will get converted to Dark Dragons when struck by lightning.
  - Storm Dragons will gain a stronger Strength effect when struck by lightning. (Strength II, 01:00)
  - Replace the sound that played when equipping a dragon with chest.
  - Dragon will start to sit once it tamed.
  - Players require a carriage or a saddle to ride a dragon now.
  - Players can interact with a dragon to board carriages on it.
  - More specific reasons will be shown when interaction with dragon denied.
- Breath
  - Zombie Dragon Breath can apply poison effect when it hits entity.
  - Wither Dragon Breath can apply wither effect when it hits entity.
  - Dark Dragon Breath can spawn blindness effect cloud when it hits block.
  - Fire-like dragon breath can apply extra damage to those wet.
  - Spray-like and blizzard-like dragon breath can apply extra damage to those on fire.
  - Spray-like dragon breath can hydrate Farmland.
- Inventory
  - The chest slot of inventory accpets item stack included in `chestWood` ore dictionary.
  - Update its layout.
  - Add tooltip for buttons in inventory.
  - Limits to enabling its slots are relaxed.

#### CargoBob

- CargoBobs are now equipped when they really collides with dragons.
- Adjust the size of CargoBobs' boundary box so that they can collide with dragons more easily.
  - Width:  0.8 -> 1.0
  - Height: 0.8 -> 0.5

#### World Generation

- Aether Dragon Nest will try to spawn in biomes that is `BiomeDictionary.Type.VOID` and `BiomeDictionary.Type.MAGICAL`.
- Gold Nuggets in the loot boxes within water dragon nest are now randomly placed.
- Diamonds, Gold Ingots and and obsidians in the loot boxes within zombie and skeleton nest are now randomly placed.

#### Gameplay

- Replace boost key binding with vanilla sprint key binding.
- Adjust category and order of items in creative mode tab.
- Adjust recipe for saddles.
  - Saddles can now be crafted with 3 Leather and 1 Iron Ingot.
  - Recipe unlocks when a player picks up their first Leather.
- Most recipes accept ingredients form ore dictionary.

#### Localization

- Remove outdated translations in Russian.
- Update translations in supported languagues.

#### Visual

- Update model for Dragon Scale Shields.
- Update icons of lock button in Dragon Inventory.
- Update textures for Dragon Core and Dragon Nest.
- Update textures for dragons.
- Update texture for the saddle on dragon.
- Update texture for dragon armors on dragon.
- Update textures for following items:
  - Carriages
  - Diamond Shears
  - Dragon Armors
  - Dragon Orb (WIP)
  - Dragon Scales
  - Dragon Scale Armors
  - Dragon Scale Shields
  - Orb of Variation

#### Technical

- Flatten identifiers of items, blocks, and block entities.
- Control packets only be sent on key change when ridden.
- Dragon layers will be rendered partially if possible.
- Increase the width of dragons' render bounding box.
- Reproduction count won't be synchronized to client.
- Removed some unused codes and resources.

### Fixes

#### Blocks & Items

- Diamond Shears now need diamond to repair.
- Dragon Whistle will only open GUI on logical client.
- Dragon Whistle functions correctly now.
- Dragon Egg block won't disappear after interaction if no variant available.
- Empty Amulet will expire as common item.
- Dragon Essence will update stage before spawning.
- NBT entry named `Potion Core - Health Fix` in custom entity data will be ignored when saved to Dragon Essence.

#### Entities

- Dragon Amulet item entity has hover animation now.
- Carriage entity can be rendered again.
- Carriage entity has correct pick result now.
- Carriage won't keep invulnerable after dismount.
- Passengers have correct attachment position now.
- Dragons won't enter blocks as they grow.
- Dragons won't push carriages twice a tick.
- Item interactions with dragons will be handled first.
- Item cannot be transferred to disabled inventory slots.
- Taming dragons can achieve Best Friends Forever advancement now.
- Dragon will no longer attempt to interact with off hand after successfully interacting with main hand.
- The age of a dragon can be performed correctly now.
- Dragon head can be located correctly.
- Dragon entity has correct pick result now.
- Dragons will try follow owner when leashed.
- Skeleton dragon egg can judge brightness correctly now.
- Crack particles of dragon egg blocks can be played now.
- The max stack size of saddle slot in dragon inventory is `1`.
- Saddle can only be taken when the dragon is not being ridden.
- Item stack won't get transfered into hidden slots in dragon inventory without equipping chest.
- `DragonInventory::decrStackSize` can update equipment state correctly.
- The age of the dragon can be properly synchronized before the first tick.
- Dragons in client side has correct fire immunity.
- Dragon's animations will be calculated only once before each rendering.
- Dragon won't try to find attack target when ridden.
- Crack sound of dragon egg can be played now.
- Dragon eggs won't try to find End Crystal.
- Max health modifiers provided by dragon type will be applied before loading health.
- Layers won't sync animation properties when rendered.
- Dragon won't get hungrier when loaded.
- Dragon won't lose health unintentionally when spawned.
- Spray-like dragon breath will replace following lava with Cobblestone instead of Obsidian.
- Looping breath sound won't get skipped when previous one is too short.
- Dragon will clear attack target when start to sit.
- Particles for ice dragons can be spawned correctly.
- Particles for nether dragons can be spawned correctly.
- Particles for wither dragons can be spawned correctly.

#### Others

- `/dragonmounts stage` command sends correct feedback now.
- `/dragonmounts type` command can throw exception if the type is invalid.
- `/give` command won't additionally toss specific items.
- Players won't receive command feedback if game rule `sendCommandFeedback` is set to `false`.
- Server will check if the sender has access after receiving network packets about dragon inventory or dragon whistle.
- Configuration `disable block override` has no effect on `HatchableDragonEggBlock` any more.
- The configuration to disable generation in specific dimensions is now valid.
- Third person view as seen from the front of the player on dragon has correct offset.
- Outline in slots will be rendered only when the slot is empty.
- `ForgeRegistry$Snapshot` can be saved correctly.
- Delay the handle of some network packets to avoid multi-threading race.

## [2.0.1] - 2024-12-8

### Fix

- Provider of `ARMOR_EFFECT_MANAGER` returns proper value now.

## [2.0.0] - 2024-12-1

### BREAKING CHANGES

- PACKAGE NAME IS RENAMED TO `net.dragonmounts`.
- REMOVE GENDER SYSTEM.
  - Data Fix is scheduled later. **Backup your level**!

### Additions

- Add compatibility to [baubles](https://github.com/Azanor/Baubles).
- Add French localization.
- Add Japanese localization.
- Add `ARMOR_EFFECT_MANAGER` capability.
- Add `Forced Rename` config, which allows anvil to change dragon's name by renaming amulet.

### Changes

- Dragon whistle now uses UUID to identify owner.
- Dragons now get slow down when in fluid.
- Dragons will get saddled when interacting with saddle.
- Dragon egg entity has the same bounding box with dragon egg block.
- Dragons have dedicated inventory now.
- Moonlight dragons share the same breath with aether dragons.
- Some dragon breath can smelt block now.
- Commands have feedback now.
- Commands that interact with dragon now accept an entity selector or a UUID to specify the target.
- Change the content of title of dragon inventory screen to vanilla style.
- Add descriptions of armor effect to tooltips.
- Update textures for capes.
- Update textures for dragon core and its GUI.
- Update textures for dragon armors.
- Update textures for dragon breath.
- Update textures for ice and moonlight dragon.
- Update textures for dragon amulets.
- Update texture for dragon wand.
- Update texture for dragon inventory GUI.
- Update model for dragon eggs.
- Dragon breath will be rendered ignoring light.
- Dragon spawn eggs now use tinted vanilla textures.

### Fixes

- Obfuscated Minecraft won't crash when loading with `debug` config on.
- Config `can eggs change breeds` has effects now.
- Recipes can be unlocked in certain condition.
- Dragon scale shield has using animation now.
- Cooldown of armor effect can be saved properly now.
- Enchant dragon scale armor effect now work properly.
- Dragon amulets can prevent interaction properly.
- Dragon amulets will place dragons at the center of block now.
- Dragon amulets will not release dragon when collected by hopper anymore.
- Dragon essences can spawn dragon now.
- Dragon core can be rendered properly now.
- Dragon core has breaking particles now.
- Dragon egg block has correct break particles now.
- Dragon egg entity cannot execute normal AI now.
- Items in offhand can interact with dragons now.
- Dragons have correct initial breed points now.
- Dragon will stop breathing after dismount.
- Dragons cannot mate when sitting anymore.
- Dragons can save custom name correctly now.
- Dragon won't reset health when loaded.
- Hatchling dragons cannot breathe fire anymore.
- Breath from infant dragons can set entities on fire now.
- Ice breath won't place snow layer in air anymore.
- Sitting button in dragon inventory screen could be translated now.
- Crystal beams won't be rendered in dragon inventory screen anymore.