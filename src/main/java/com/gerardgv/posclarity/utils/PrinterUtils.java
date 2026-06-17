package com.gerardgv.posclarity.utils;

import java.util.List;
import java.util.Optional;
import javafx.print.Printer;
import javafx.scene.control.ChoiceDialog;

public class PrinterUtils {
    
    public static Printer obtenerImpresoraGuardada(){
        
        String nombreImpresora = Configuracion.obtenerImpresora();
        
        if(nombreImpresora == null){
            return null;
        }
        
        for(Printer printer : Printer.getAllPrinters()){
            if(printer.getName().equals(nombreImpresora)){
                return printer;
            }
        }
        return null;
    }
    
    public static Printer seleccionarImpresora(){
        
        List<String> nombres = Printer.getAllPrinters().stream().map(Printer::getName).toList();
        
        if(nombres.isEmpty()){
            return null;
        }
        
         ChoiceDialog<String> dialog =
            new ChoiceDialog<>(nombres.get(0), nombres);

        dialog.setTitle("Seleccionar impresora");
        dialog.setHeaderText("Seleccione una impresora");
        Optional<String> resultado = dialog.showAndWait();
        
        if(resultado.isEmpty()){
            return null;
        }
        
        String nombreSeleccionado = resultado.get();
        Configuracion.guardarImpresora(nombreSeleccionado);

        return Printer.getAllPrinters()
            .stream()
            .filter(p -> p.getName().equals(nombreSeleccionado))
            .findFirst()
            .orElse(null);
        
    }
    
}
