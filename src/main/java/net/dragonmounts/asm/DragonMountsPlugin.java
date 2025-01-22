package net.dragonmounts.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

import static net.dragonmounts.DragonMountsTags.MOD_ID;

public class DragonMountsPlugin implements IFMLLoadingPlugin {
    static final Logger PLUGIN_LOGGER = LogManager.getLogger(MOD_ID);
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                "net.dragonmounts.asm.DMClassTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
