package net.dragonmounts.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.storage.WorldSavedData;

public class Structures extends WorldSavedData {
    public final Long2ObjectMap<StructureStart> instances = new Long2ObjectOpenHashMap<>();
    private NBTTagCompound data = new NBTTagCompound();

    public Structures(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound root) {
        this.data = root.getCompoundTag("Features");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound root) {
        root.setTag("Features", this.data);
        return root;
    }

    public void persistent(long chunk, NBTTagCompound start) {
        this.data.setTag(Long.toString(chunk), start);
        this.markDirty();
    }

    public void reload(World level) {
        NBTTagCompound data = this.data;
        for (String pos : data.getKeySet()) {
            NBTBase raw = data.getTag(pos);
            if (raw.getId() != 10) continue;
            NBTTagCompound structure = (NBTTagCompound) raw;
            if (structure.hasKey("ChunkX") && structure.hasKey("ChunkZ")) {
                StructureStart start = MapGenStructureIO.getStructureStart(structure, level);
                if (start == null) continue;
                this.instances.put(ChunkPos.asLong(structure.getInteger("ChunkX"), structure.getInteger("ChunkZ")), start);
            }
        }
    }
}
