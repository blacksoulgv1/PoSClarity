package com.gerardgv.posclarity.service;

import com.gerardgv.posclarity.database.DescuentoDAO;
import com.gerardgv.posclarity.models.Descuento;


public class DescuentoService {
    
    private final DescuentoDAO dao;
    
    public DescuentoService(){
        this.dao = new DescuentoDAO();
    }
    
    public Descuento obtenerDescuentoAplicable(String categoriaProducto, String codigoCupon){
        
        if(codigoCupon != null && !codigoCupon.trim().isEmpty()){
            
            Descuento cupon = dao.obtenerCuponValido(codigoCupon.trim());
            
            if(cupon != null){
                return cupon;
            }
        }
        
        if(categoriaProducto != null){
            
            Descuento categoria = dao.obtenerPorCategoria(categoriaProducto);
            
            if(categoria != null){
                return categoria;
            }
        }
        
        return dao.obtenerGeneralActivo();
    }
    
    public double aplicarDescuento(double total, Descuento descuento){
        
        if(descuento == null){
            return total;
        }
        
        double descuentoCalculado = calcularMontoDescuento(total,descuento);
        
        if(descuentoCalculado > total){
            descuentoCalculado = total;
        }
        return total - descuentoCalculado;
    }
    
    public double calcularMontoDescuento(double total, Descuento descuento){
        
        if(descuento == null){
            return 0;
        }
        
        if("PORCENTAJE".equalsIgnoreCase(descuento.getTipoValor())){
            return total * (descuento.getValor() / 100.0);
        } else {
            return descuento.getValor();
        }
    }
}
