package com.lastabyss.lithium.util;

import org.bukkit.Location;

public class NotCorrectTileEntityException extends Exception {
    private static final long serialVersionUID = 3855957883078890902L;

    String message = "";

    public NotCorrectTileEntityException(String tileEntityName, Location loc) {
        message = "Block at X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + " Must be a " +
                tileEntityName;
    }

    @Override
    public void printStackTrace() {
        System.err.println(message);
        for (StackTraceElement st : this.getStackTrace()) {
            System.err.println("    Caused by " + st.getClassName() + " in method " + st.getMethodName() +
                    " at Line " + st.getLineNumber());
        }
    }
}