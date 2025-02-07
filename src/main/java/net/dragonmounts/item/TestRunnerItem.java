package net.dragonmounts.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dragonmounts.init.DMItemGroups;
import net.dragonmounts.util.ITestCase;
import net.dragonmounts.util.LogUtil;
import net.dragonmounts.util.debugging.TestRunner;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * User: The Grey Ghost
 * Date: 04/01/2016
 * <p>
 * ItemTestRunner is used to trigger a test case
 */
public class TestRunnerItem extends Item implements ITestCase {
    private final Int2ObjectOpenHashMap<ITestCase> client = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<ITestCase> server = new Int2ObjectOpenHashMap<>();

    public TestRunnerItem() {
        this.setCreativeTab(DMItemGroups.MAIN);
        TestRunner.register(this);
    }

    public void register(Side side, int index, ITestCase test) {
        if (this == test) return;
        (side.isClient() ? this.client : this.server).put(index, test);
        if (index > this.maxStackSize) {
            this.setMaxStackSize(index);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World level, List<String> tooltips, ITooltipFlag flag) {
        tooltips.add("Right click: conduct test");
        tooltips.add("Stacksize: change test #");
    }

    // what animation to use when the player holds the "use" button
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    // how long the player needs to hold down the right button before the test runs again
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World level, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public static void execute(ITestCase test, World level, EntityPlayer player, ItemStack stack, int index) {
        try {
            if (test.run(level, player, stack)) {
                LogUtil.LOGGER.info("[Success] Test(#{}) called on {}", index, level.isRemote ? "client side" : "server side");
            } else {
                LogUtil.LOGGER.info("[Failure] Test(#{}) called on {}", index, level.isRemote ? "client side" : "server side");
            }
        } catch (Exception e) {
            LogUtil.LOGGER.warn("[Error] Test(#{}) called on {}", index, level.isRemote ? "client side" : "server side", e);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World level, EntityLivingBase user) {
        if (user instanceof EntityPlayer) {
            int index = stack.getCount();
            execute((level.isRemote ? this.client : this.server).getOrDefault(index, this), level, (EntityPlayer) user, stack, index);
        }
        return stack;
    }

    @Override
    public boolean run(World level, EntityPlayer player, ItemStack stack) {
        for (Int2ObjectMap.Entry<ITestCase> entry : (level.isRemote ? this.client : this.server).int2ObjectEntrySet()) {
            execute(entry.getValue(), level, player, stack, entry.getIntKey());
        }
        return true;
    }
}
