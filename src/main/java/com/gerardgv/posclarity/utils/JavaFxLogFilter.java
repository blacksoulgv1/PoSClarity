package com.gerardgv.posclarity.utils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class JavaFxLogFilter implements Filter{
    
    @Override
    public boolean isLoggable(LogRecord record) {

        String loggerName = record.getLoggerName();
        String message = record.getMessage();

        // Ocultar únicamente los warnings generados por JavaFX CSS
        if ("javafx.scene.CssStyleHelper".equals(loggerName)) {
            return false;
        }

        // Ocultar el warning específico de Unsafe usado por JavaFX/Marlin
        if (message != null
                && message.contains("sun.misc.Unsafe")
                && message.contains("OffHeapArray")) {
            return false;
        }

        return true;
    }
    
}
