package com.gerardgv.posclarity.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    
    //Clientes    
    private static final List<Consumer<Integer>> listeners = new ArrayList<>();
    
    public static void subscribeVenta(Consumer<Integer> listener){
        listeners.add(listener);
    }
    
    public static void publishVenta(int idCliente){
        for(Consumer<Integer> listener:listeners){
            listener.accept(idCliente);
        }
    }
    
    //Productos
    
    private static final List<Consumer<List<Integer>>> stockListeners = new ArrayList<>();
    
    public static void subscribeStock(Consumer<List<Integer>> listener){
        stockListeners.add(listener);
    }
    
    public static void publishStock(List<Integer> productosIds){
        for(Consumer<List<Integer>> listener:stockListeners){
            listener.accept(productosIds);
        }
    }

}
