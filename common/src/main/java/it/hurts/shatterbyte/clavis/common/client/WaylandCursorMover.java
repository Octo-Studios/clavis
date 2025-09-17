package it.hurts.shatterbyte.clavis.common.client;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class WaylandCursorMover implements NativeCursorMover {
    @Override
    public void moveMouse(long windowHandle, double x, double y) {
        int prevMode = GLFW.glfwGetInputMode(windowHandle, GLFW.GLFW_CURSOR);
        InputConstants.grabOrReleaseMouse(windowHandle, GLFW.GLFW_CURSOR_DISABLED, x, y);
        InputConstants.grabOrReleaseMouse(windowHandle, prevMode, x, y);
    }
}
