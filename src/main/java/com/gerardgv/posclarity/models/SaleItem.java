package com.gerardgv.posclarity.models;

public class SaleItem {
    
    private Product product;
    private String discountName;
    private int quantity;
    private double discount;
    private String description;
    private double price;
    private int detailId;

    public SaleItem() {
    }

    public SaleItem(Product product, String discountName, double discount) {
        this.product = product;
        this.discountName = discountName;
        this.quantity = 1;
        this.discount = discount;
    }

     public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getSubtotal() {
        double total = getPrice() * quantity;
        return total - (discount * quantity);
    }

    public String getDescription() {
        return description != null ? description : product.getModel();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price != 0 ? price : product.getPrice();
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }
    
    // =====================================================
// COMPATIBILIDAD TEMPORAL
// =====================================================

@Deprecated
public Product getProducto() {
    return getProduct();
}

@Deprecated
public void setProducto(Product producto) {
    setProduct(producto);
}

@Deprecated
public int getCantidad() {
    return getQuantity();
}

@Deprecated
public void setCantidad(int cantidad) {
    setQuantity(cantidad);
}

@Deprecated
public double getDescuento() {
    return getDiscount();
}

@Deprecated
public void setDescuento(double descuento) {
    setDiscount(descuento);
}

@Deprecated
public String getNombreDescuento() {
    return getDiscountName();
}

@Deprecated
public void setNombreDescuento(String nombreDescuento) {
    setDiscountName(nombreDescuento);
}

@Deprecated
public int getIdDetalle() {
    return getDetailId();
}

@Deprecated
public void setIdDetalle(int idDetalle) {
    setDetailId(idDetalle);
}

@Deprecated
public String getDescripcion() {
    return getDescription();
}

@Deprecated
public void setDescripcion(String descripcion) {
    setDescription(descripcion);
}

@Deprecated
public double getPrecio() {
    return getPrice();
}

@Deprecated
public void setPrecio(double precio) {
    setPrice(precio);
}
    
}
