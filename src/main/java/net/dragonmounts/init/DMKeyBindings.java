package net.dragonmounts.init;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class DMKeyBindings {
    public static final String KEY_CATEGORY = "key.categories.dragonmounts";
    public static final KeyBinding KEY_BREATH = new KeyBinding("key.dragonmounts.breath", Keyboard.KEY_R, KEY_CATEGORY);
    public static final KeyBinding KEY_DESCENT = new KeyBinding("key.dragonmounts.descent", Keyboard.KEY_NONE, KEY_CATEGORY);
    public static final KeyBinding TOGGLE_CAMERA_POS = new KeyBinding("key.dragonmounts.toggleCameraPos", Keyboard.KEY_F7, KEY_CATEGORY);
    public static final KeyBinding TOGGLE_HOVERING = new KeyBinding("key.dragonmounts.toggleHovering", Keyboard.KEY_NONE, KEY_CATEGORY);
    public static final KeyBinding TOGGLE_YAW_ALIGNMENT = new KeyBinding("key.dragonmounts.toggleYawAlignment", Keyboard.KEY_NONE, KEY_CATEGORY);
    public static final KeyBinding TOGGLE_PITCH_ALIGNMENT = new KeyBinding("key.dragonmounts.togglePitchAlignment", Keyboard.KEY_NONE, KEY_CATEGORY);

    public static void register() {
        ClientRegistry.registerKeyBinding(KEY_BREATH);
        ClientRegistry.registerKeyBinding(TOGGLE_HOVERING);
        ClientRegistry.registerKeyBinding(TOGGLE_YAW_ALIGNMENT);
        ClientRegistry.registerKeyBinding(TOGGLE_PITCH_ALIGNMENT);
        ClientRegistry.registerKeyBinding(TOGGLE_CAMERA_POS);
        ClientRegistry.registerKeyBinding(KEY_DESCENT);
    }
}
