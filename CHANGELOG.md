# Changelog

## [2.0.1] - 2024-12-8

### Fix

- Provider of `ARMOR_EFFECT_MANAGER` returns proper value now.

## [2.0.0] - 2024-12-1

### BREAKING CHANGES

- PACKAGE NAME IS SET TO `net.dragonmounts`.
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