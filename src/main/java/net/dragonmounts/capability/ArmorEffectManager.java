package net.dragonmounts.capability;

import net.dragonmounts.api.IArmorEffect;
import net.dragonmounts.api.IArmorEffectSource;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.network.SInitCooldownPacket;
import net.dragonmounts.network.SSyncCooldownPacket;
import net.dragonmounts.registry.CooldownCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;
import static net.dragonmounts.DragonMounts.NETWORK_WRAPPER;
import static net.dragonmounts.capability.DMCapabilities.ARMOR_EFFECT_MANAGER;

public final class ArmorEffectManager implements IArmorEffectManager {
    private static ArmorEffectManager LOCAL_MANAGER = null;
    private static SInitCooldownPacket LOCAL_CACHE = null;
    public static final int INITIAL_COOLDOWN_SIZE = 8;
    public static final int INITIAL_LEVEL_SIZE = 5;

    public static void onPlayerClone(EntityPlayer neoPlayer, EntityPlayer oldPlayer) {
        IArmorEffectManager neo = neoPlayer.getCapability(ARMOR_EFFECT_MANAGER, null);
        if (!(neo instanceof ArmorEffectManager)) return;
        IArmorEffectManager old = oldPlayer.getCapability(ARMOR_EFFECT_MANAGER, null);
        if (!(old instanceof ArmorEffectManager)) return;
        ArmorEffectManager $neo = (ArmorEffectManager) neo;
        ArmorEffectManager $old = (ArmorEffectManager) old;
        $neo.cdRef = $old.cdRef;
        $neo.cdKey = $old.cdKey;
        $neo.cdDat = $old.cdDat;
        $neo.cdMask = $old.cdMask;
        $neo.cdN = $old.cdN;
    }

    public final EntityPlayer player;
    private int[] cdRef;
    private int[] cdKey;
    private int[] cdDat;
    private int cdMask;
    private int cdN;
    private int[] lvlRef;//active effects
    private IArmorEffect[] lvlKey;//all effects
    private int[] lvlDat;
    private int lvlSize;
    private int lvlN;
    private int activeN;

    public ArmorEffectManager(EntityPlayer player) {
        this.player = player;
        this.lvlSize = INITIAL_LEVEL_SIZE;
        this.lvlRef = new int[INITIAL_LEVEL_SIZE];
        this.lvlKey = new IArmorEffect[INITIAL_LEVEL_SIZE];
        this.lvlDat = new int[INITIAL_LEVEL_SIZE];
        if (player.isUser()) {
            LOCAL_MANAGER = this;
            if (LOCAL_CACHE != null) {
                this.cdMask = INITIAL_COOLDOWN_SIZE - 1;
                this.cdRef = new int[INITIAL_COOLDOWN_SIZE];
                this.cdKey = new int[INITIAL_COOLDOWN_SIZE];
                this.cdDat = new int[INITIAL_COOLDOWN_SIZE];
                init(LOCAL_CACHE);
                LOCAL_CACHE = null;
                return;
            }
            // capabilities are attached to entities in <init>,
            // while assignments occur after <init>.
            // if we try to read a field when running <init>,
            // we will get the same value as before <init>.
            EntityPlayer prior = ClientUtil.getLocalPlayer();
            if (prior != null) {
                IArmorEffectManager mgr = prior.getCapability(ARMOR_EFFECT_MANAGER, null);
                if (mgr instanceof ArmorEffectManager) {
                    ArmorEffectManager manager = (ArmorEffectManager) mgr;
                    this.cdRef = manager.cdRef;
                    this.cdKey = manager.cdKey;
                    this.cdDat = manager.cdDat;
                    this.cdMask = manager.cdMask;
                    this.cdN = manager.cdN;
                    return;
                }
            }
        }
        this.cdMask = INITIAL_COOLDOWN_SIZE - 1;
        this.cdRef = new int[INITIAL_COOLDOWN_SIZE];
        fill(this.cdKey = new int[INITIAL_COOLDOWN_SIZE], -1);
        this.cdDat = new int[INITIAL_COOLDOWN_SIZE];
    }

    public static ArmorEffectManager getLocal() {
        return LOCAL_MANAGER;
    }

    public static int getLocalCooldown(CooldownCategory category) {
        return LOCAL_MANAGER == null ? 0 : LOCAL_MANAGER.getCooldown(category);
    }

    public static void init(SInitCooldownPacket packet) {
        ArmorEffectManager manager = LOCAL_MANAGER;
        if (manager == null) {
            LOCAL_CACHE = packet;
            return;
        }
        manager.cdN = 0;
        final int length = packet.size;
        final int[] data = packet.data;
        if (length > manager.cdMask) {
            int size = manager.cdRef.length << 1;
            while (length >= size) size <<= 1;
            manager.cdMask = size - 1;
            manager.cdRef = new int[size];
            fill(manager.cdKey = new int[size], -1);
            manager.cdDat = new int[size];
        } else {
            fill(manager.cdKey, -1);
        }
        CooldownTracker vanilla = manager.player.getCooldownTracker();
        for (int i = 0, j = 0, k, n; i < length; ++i) {
            if ((k = data[i++]) >= 0) {
                CooldownCategory category = CooldownCategory.REGISTRY.getValue(k);
                if (category == null) continue;
                j = manager.setCdImpl(k, n = data[i], j);
                for (Item item : category.getItems()) {
                    vanilla.setCooldown(item, n);
                }
            }
        }
    }

    private void reassign(final int pos, final int arg) {
        final int[] cdRef = this.cdRef, cdKey = this.cdKey, cdDat = this.cdDat;
        for (int i = this.cdN - 1, j, k, mask = this.cdMask; i > arg; --i) {
            if (((k = cdKey[j = cdRef[i]]) & mask) == pos) {
                cdRef[i] = pos;
                cdKey[pos] = k;
                cdDat[pos] = cdDat[j];
                this.reassign(j, i);
                return;
            }
        }
        cdKey[pos] = -1;//it is unnecessary to reset `cdDat[pos]`
    }

    private int setCdImpl(final int category, final int cooldown, int cursor) {
        final int[] cdRef = this.cdRef, cdKey = this.cdKey, cdDat = this.cdDat;
        int mask = this.cdMask, pos = category & mask;
        do {
            int key = cdKey[pos];
            if (key == -1) {
                if (cooldown > 0) {
                    cdRef[this.cdN++] = pos;
                    cdKey[pos] = category;
                    cdDat[pos] = cooldown;
                }
                return cursor == pos ? pos + 1 : cursor;
            } else if (key == category) {
                if (cooldown > 0) {
                    cdDat[pos] = cooldown;
                } else for (int i = 0; i < this.cdN; ++i) {
                    if (cdRef[i] == pos) {
                        arraycopy(cdRef, i + 1, cdRef, i, --this.cdN - i);
                        this.reassign(pos, i - 1);
                        return cursor == pos ? pos + 1 : cursor;
                    }
                }
                return cursor == pos ? pos + 1 : cursor;
            }
        } while ((pos = cursor++) <= mask);
        throw new IndexOutOfBoundsException();
    }

    private void setCooldown(final int category, final int cooldown) {
        if (this.cdN == this.cdRef.length) {
            final int[] ref = this.cdRef, key = this.cdKey, dat = this.cdDat;
            final int n = this.cdN;
            int temp = n << 1;//temp: new array size
            this.cdMask = temp - 1;
            this.cdN = 0;
            this.cdRef = new int[temp];
            fill(this.cdKey = new int[temp], -1);
            this.cdDat = new int[temp];
            for (int i = temp = 0, j; i < n; ++i) {//temp: cursor
                temp = this.setCdImpl(key[j = ref[i]], dat[j], temp);
            }
            this.setCdImpl(category, cooldown, temp);
        } else {
            this.setCdImpl(category, cooldown, 0);
        }
    }

    @Override
    public void setCooldown(final CooldownCategory category, final int cooldown) {
        final int id = category.getId();
        if (id < 0) return;
        this.setCooldown(id, cooldown);
        if (!this.player.world.isRemote) {
            NETWORK_WRAPPER.sendTo(new SSyncCooldownPacket(id, cooldown), (EntityPlayerMP) this.player);
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final int[] cdRef = this.cdRef, cdKey = this.cdKey, cdDat = this.cdDat;
        NBTTagCompound tag = new NBTTagCompound();
        for (int i = 0, j, v, n = this.cdN; i < n; ++i) {
            if ((v = cdDat[j = cdRef[i]]) > 0) {
                CooldownCategory category = CooldownCategory.REGISTRY.getValue(cdKey[j]);
                if (category == null) continue;
                ResourceLocation identifier = category.getRegistryName();
                if (identifier == null) continue;
                tag.setInteger(identifier.toString(), v);
            }
        }
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        for (CooldownCategory category : CooldownCategory.REGISTRY) {
            if (category == null) continue;
            ResourceLocation identifier = category.getRegistryName();
            if (identifier == null) continue;
            this.setCooldown(category.getId(), tag.getInteger(identifier.toString()));
        }
    }

    @Override
    public void deserializeNothing() {
        this.cdN = 0;
        fill(this.cdKey, -1);
    }

    @Override
    public @Nullable NBTTagCompound validateTag(@Nullable NBTBase tag) {
        return tag instanceof NBTTagCompound ? ((NBTTagCompound) tag) : null;
    }

    @Override
    public void sendInitPacket() {
        final int n = this.cdN;
        if (n == 0) return;
        final int[] cdRef = this.cdRef, cdKey = this.cdKey, cdDat = this.cdDat, data = new int[n << 1];
        int index = 0;
        for (int i = 0, j, k; i < n; ++i) {
            if ((k = cdKey[j = cdRef[i]]) != -1) {
                data[index++] = k;
                data[index++] = cdDat[j];
            }
        }
        NETWORK_WRAPPER.sendTo(new SInitCooldownPacket(index, data), (EntityPlayerMP) this.player);
    }

    @Override
    public int getCooldown(final CooldownCategory category) {
        final int id = category.getId();
        if (id < 0) return 0;
        int pos = id & this.cdMask, key = this.cdKey[pos];
        if (key == -1) return 0;
        if (key == id) return this.cdDat[pos];
        final int[] cdRef = this.cdRef, cdKey = this.cdKey, cdDat = this.cdDat;
        for (int i = 0, n = this.cdN; i < n; ++i) {
            if (cdRef[i] == pos) {
                while (++i < n) {
                    if (cdKey[pos = cdRef[i]] == id) {
                        return cdDat[pos];
                    }
                }
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean isAvailable(final CooldownCategory category) {
        final int id = category.getId();
        if (id < 0) return true;
        int pos = id & this.cdMask, key = this.cdKey[pos];
        if (key == -1) return true;
        if (key == id) return this.cdDat[pos] <= 0;
        final int[] cdRef = this.cdRef, cdKey = this.cdKey, cdDat = this.cdDat;
        for (int i = 0, n = this.cdN; i < n; ++i) {
            if (cdRef[i] == pos) {
                while (++i < n) {
                    if (cdKey[pos = cdRef[i]] == id) {
                        return cdDat[pos] <= 0;
                    }
                }
                return true;
            }
        }
        return true;
    }

    private void validateLvlSize() {
        if (this.lvlN == this.lvlSize) {
            this.lvlSize += 4;
            final IArmorEffect[] key = new IArmorEffect[this.lvlSize];
            final int[] dat = new int[this.lvlSize];
            arraycopy(this.lvlKey, 0, key, 0, this.lvlN);
            arraycopy(this.lvlDat, 0, dat, 0, this.lvlN);
            this.lvlKey = key;
            this.lvlDat = dat;
        }
    }

    @Override
    public int setLevel(final IArmorEffect effect, final int level) {
        final IArmorEffect[] lvlKey = this.lvlKey;
        final int n = this.lvlN;
        for (int i = 0; i < n; ++i) {
            if (lvlKey[i] == effect) {
                return this.lvlDat[i] = level;
            }
        }
        this.validateLvlSize();//may assign new array to `this.lvlKey`
        this.lvlKey[n] = effect;
        return this.lvlDat[this.lvlN++] = level;
    }

    @Override
    public int stackLevel(final IArmorEffect effect) {
        final IArmorEffect[] lvlKey = this.lvlKey;
        final int n = this.lvlN;
        for (int i = 0; i < n; ++i) {
            if (lvlKey[i] == effect) {
                return ++this.lvlDat[i];
            }
        }
        this.validateLvlSize();//may assign new array to `this.lvlKey`
        this.lvlKey[n] = effect;
        return this.lvlDat[this.lvlN++] = 1;
    }

    @Override
    public boolean isActive(final IArmorEffect effect) {
        final IArmorEffect[] lvlKey = this.lvlKey;
        final int[] lvlRef = this.lvlRef;
        for (int i = 0, n = this.activeN; i < n; ++i) {
            if (lvlKey[lvlRef[i]] == effect) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int getLevel(final IArmorEffect effect, final boolean filtered) {
        final IArmorEffect[] lvlKey = this.lvlKey;
        if (filtered) {
            final int[] lvlRef = this.lvlRef;
            for (int i = 0, j, n = this.activeN; i < n; ++i) {
                if (lvlKey[j = lvlRef[i]] == effect) {
                    return this.lvlDat[j];
                }
            }
        } else for (int i = 0, n = this.lvlN; i < n; ++i) {
            if (lvlKey[i] == effect) {
                return this.lvlDat[i];
            }
        }
        return 0;
    }

    @Override
    public void tick() {
        final int[] cdRef = this.cdRef, cdDat = this.cdDat, lvlDat = this.lvlDat;
        final EntityPlayer player = this.player;
        for (int i = 0, j; i < this.cdN; ++i) {
            if (--cdDat[j = cdRef[i]] < 1) {
                arraycopy(cdRef, i + 1, cdRef, i, --this.cdN - i);
                this.reassign(j, --i);
            }
        }
        int sum = this.activeN = this.lvlN = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            Item item = stack.getItem();
            if (item instanceof IArmorEffectSource) {
                ((IArmorEffectSource) item).affect(this, player, stack);
            }
        }
        final IArmorEffect[] lvlKey = this.lvlKey;
        int[] lvlRef = this.lvlRef;
        for (int i = 0, end = this.lvlN; i < end; ++i) {
            final IArmorEffect effect = lvlKey[i];
            if (effect.activate(this, player, lvlDat[i])) {
                if (sum == lvlRef.length) {
                    arraycopy(this.lvlRef, 0, lvlRef = new int[sum + 4], 0, sum);
                    this.lvlRef = lvlRef;
                }
                lvlRef[sum++] = i;
            }
        }
        this.activeN = sum;
    }
}
