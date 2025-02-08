package net.dragonmounts.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class DMKeyBindings {
    public static final String KEY_CATEGORY = "key.categories.dragon";
    public static KeyBinding KEY_BREATH;
    public static KeyBinding KEY_BOOST;
    public static KeyBinding KEY_DESCENT;
    public static KeyBinding TOGGLE_CAMERA_POS;
    public static KeyBinding TOGGLE_HOVERING;
    public static KeyBinding TOGGLE_YAW_ALIGNMENT;
    public static KeyBinding TOGGLE_PITCH_ALIGNMENT;
    
    public static void init() {
        KEY_BREATH = new KeyBinding("key.dragonmounts.breath", Keyboard.KEY_R, KEY_CATEGORY);
        KEY_BOOST = new KeyBinding("key.dragonmounts.boost", Keyboard.KEY_LCONTROL, KEY_CATEGORY);
        KEY_DESCENT = new KeyBinding("key.dragonmounts.descent", Keyboard.KEY_NONE, KEY_CATEGORY);
        TOGGLE_CAMERA_POS = new KeyBinding("key.dragonmounts.toggleCameraPos", Keyboard.KEY_F7, KEY_CATEGORY);
        TOGGLE_HOVERING = new KeyBinding("key.dragonmounts.toggleHovering", Keyboard.KEY_NONE, KEY_CATEGORY);
        TOGGLE_YAW_ALIGNMENT = new KeyBinding("key.dragonmounts.toggleYawAlignment", Keyboard.KEY_NONE, KEY_CATEGORY);
        TOGGLE_PITCH_ALIGNMENT = new KeyBinding("key.dragonmounts.togglePitchAlignment", Keyboard.KEY_NONE, KEY_CATEGORY);

        ClientRegistry.registerKeyBinding(KEY_BREATH);
        ClientRegistry.registerKeyBinding(TOGGLE_HOVERING);
        ClientRegistry.registerKeyBinding(TOGGLE_YAW_ALIGNMENT);
        ClientRegistry.registerKeyBinding(TOGGLE_PITCH_ALIGNMENT);
        ClientRegistry.registerKeyBinding(TOGGLE_CAMERA_POS);
        ClientRegistry.registerKeyBinding(KEY_BOOST);
        ClientRegistry.registerKeyBinding(KEY_DESCENT);
    }
}
