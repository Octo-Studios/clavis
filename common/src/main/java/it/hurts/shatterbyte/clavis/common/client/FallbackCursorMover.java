package it.hurts.shatterbyte.clavis.common.client;

import org.lwjgl.glfw.GLFW;

public class FallbackCursorMover implements NativeCursorMover {
    @Override
    public void moveMouse(long windowHandle, double x, double y) {
        GLFW.glfwSetCursorPos(windowHandle, x, y);
    }
}
