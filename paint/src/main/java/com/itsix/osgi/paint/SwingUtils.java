package com.itsix.osgi.paint;

import javax.swing.SwingUtilities;

public class SwingUtils {

    public static void invokeAndWait(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void invokeLater(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

}
