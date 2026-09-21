package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.sale.*;
import com.gerardgv.posclarity.models.*;
import com.gerardgv.posclarity.utils.Session;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class SaleApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/sales";
    
    /*
    Consultar Venta por ID.
    */    
    public Sale getById(int id) throws IOException, InterruptedException {
        
        SaleResponse response = get(BASE_URL + "/" +id,SaleResponse.class);
        
        return convertirSale(response);
        
    }
    
    /*
    Conultar Venta Por Folio.
    */
    public Sale getByFolio(String folio)
            throws IOException, InterruptedException{
        
        SaleResponse response = get(
            BASE_URL + "/folio/"
                    + java.net.URLEncoder.encode(
                            folio,
                            java.nio.charset.StandardCharsets.UTF_8
                    ),
            SaleResponse.class
        );

    return convertirSale(response);
        
    }
    
    /*
    Consultar Ventas Actuales de Sucursal.
    */
    public List<Sale> getAll() throws IOException, InterruptedException {
        
        Integer branchId = Session.getSucursal().getId();

        List<SaleResponse> responses =
                getList(
                        BASE_URL + "?branchId=" + branchId,
                        SaleResponse.class
                );

        List<Sale> sales = new ArrayList<>();

        for (SaleResponse response : responses) {
            sales.add(convertirSale(response));
        }

        return sales;        
    }
    
    /*
    Crear Venta.
    */
    public Sale create(List<SaleItem> items,
            Payment payment,
            Integer clientId,
            Integer sellerId,
            String odEsf,
            String odCil,
            String odEje,
            String oiEsf,
            String oiCil,
            String oiEje,
            String addLens,
            String promotionName,
            String couponCode) throws IOException,InterruptedException{
        
        SaleCreateRequest request = new SaleCreateRequest();
        
        request.setBranchId(Session.getSucursal().getId());
        request.setClientId(clientId);
        request.setSellerId(sellerId);

        /*
        Folio
        */
        // Por ahora lo dejamos pendiente.
        // El API deberá encargarse posteriormente del folio.
    
        /*
        Graduación
        */
        request.setOdEsf(odEsf);
        request.setOdCil(odCil);
        request.setOdEje(odEje);
        request.setOiEsf(oiEsf);
        request.setOiCil(oiCil);
        request.setOiEje(oiEje);
        request.setAddLens(addLens);

        /*
        Promoción
        */
        request.setPromotionName(promotionName);
        request.setCouponCode(couponCode);


        /*
        Pago inicial
        */
        if (payment != null) {

            request.setPaymentMethod(
                payment.getPaymentMethod()
            );

            request.setPaymentAmount(
                BigDecimal.valueOf(payment.getAmount())
                        .setScale(2, RoundingMode.HALF_UP)
            );

            request.setPaymentReference(
                payment.getReference()
            );
        }

        /*
        Detalles
        */
        List<SaleDetailRequest> details = new ArrayList<>();

        for (SaleItem item : items) {

            SaleDetailRequest detail = new SaleDetailRequest();

            detail.setProductId(item.getProduct().getId());

            detail.setQuantity(item.getQuantity());

            detail.setDiscountName(item.getDiscountName());

            detail.setUnitPrice(
                BigDecimal.valueOf(item.getPrice())
                        .setScale(2, RoundingMode.HALF_UP)
            );

            /*
            * El descuento de SaleItem es por unidad.
            * El API espera el descuento aplicado
            * al detalle completo.
            */
            BigDecimal appliedDiscount =
                BigDecimal.valueOf(item.getDiscount())
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
                        .setScale(2, RoundingMode.HALF_UP);

            detail.setAppliedDiscount(appliedDiscount);

            details.add(detail);
        }

        request.setDetails(details);

        /*
        Enviar al API
        */
        SaleResponse response =
            post(
                    BASE_URL,
                    request,
                    SaleResponse.class
            );

        return convertirSale(response);
    }

    private Sale convertirSale(SaleResponse response) {
        
        
    Sale sale = new Sale();

    /*
    ID
    */
    sale.setId(
            response.getId() != null
                    ? response.getId()
                    : 0
    );

    /*
    Folio
    */
    sale.setFolio(response.getFolio());

    /*
    Sucursal
    */
    if (response.getBranchId() != null) {

        Branch branch = new Branch();

        branch.setId(response.getBranchId());
        branch.setName(response.getBranchName());

        Branch sucursalActual = Session.getSucursal();
        
        if(sucursalActual != null && 
                sucursalActual.getId() == response.getBranchId()){
            
            branch.setPhone(sucursalActual.getPhone());
            branch.setAddress(sucursalActual.getAddress());
            branch.setCode(sucursalActual.getCode());
            branch.setActive(sucursalActual.isActive());            
        }
        sale.setBranch(branch);
    }

    /*
    Cliente
    */
        if (response.getClientId() != null) {

            Clients client = new Clients();
            client.setId(response.getClientId());
            client.setName(response.getClientName());

            sale.setClient(client);
        }

    /*
    Vendedor
    */
    if (response.getSellerId() != null) {

        Seller seller = new Seller();

        seller.setId(response.getSellerId());
        seller.setName(response.getSellerName());

        sale.setSeller(seller);
    }

    /*
    Fecha de venta
    */
    sale.setSaleDate(response.getSaleDate());

    /*
    Totales
    */
    sale.setGrossTotal(
            convertirDouble(response.getGrossTotal())
    );

    sale.setTotalDiscount(
            convertirDouble(response.getTotalDiscount())
    );

    sale.setFinalTotal(
            convertirDouble(response.getFinalTotal())
    );

    /*
    Pagos
    */
    sale.setPaid(
            convertirDouble(response.getPaid())
    );

    sale.setRemaining(
            convertirDouble(response.getRemaining())
    );
    
    if (response.getPayments() != null) {

        List<Payment> payments = new ArrayList<>();

        for (PaymentResponse paymentResponse : response.getPayments()) {

            Payment payment = new Payment();

            payment.setId(
                paymentResponse.getId() != null
                        ? paymentResponse.getId()
                        : 0
            );

            payment.setSaleId(
                paymentResponse.getSaleId() != null
                        ? paymentResponse.getSaleId()
                        : 0
            );

            payment.setPaymentMethod(
                paymentResponse.getPaymentMethod()
            );

            payment.setAmount(
                convertirDouble(paymentResponse.getAmount())
            );

            payment.setReference(
                paymentResponse.getReference()
            );

            payment.setPaymentType(
                paymentResponse.getPaymentType()
            );

            payment.setDate(paymentResponse.getDate());

            payments.add(payment);
        }

        sale.setPayments(payments);

    } else {

        sale.setPayments(new ArrayList<>());
    }

    /*
    Graduación
    */
    sale.setOdEsf(response.getOdEsf());
    sale.setOdCil(response.getOdCil());
    sale.setOdEje(response.getOdEje());

    sale.setOiEsf(response.getOiEsf());
    sale.setOiCil(response.getOiCil());
    sale.setOiEje(response.getOiEje());

    sale.setAddLens(response.getAddLens());

    /*
    Estados
    */
    sale.setPaymentStatus(response.getPaymentStatus());
    sale.setWorkStatus(response.getWorkStatus());

    /*
    Detalles de venta
    */
    if (response.getDetails() != null) {

        List<SaleItem> items = new ArrayList<>();

        for (SaleDetailResponse detailResponse : response.getDetails()) {

            SaleItem item = convertirSaleItem(detailResponse);

            items.add(item);
        }

        sale.setItems(items);

    } else {

        sale.setItems(new ArrayList<>());
    }

    return sale;
    }
    
    /*
    Conversióm a BigDecimal.
    */
    private double convertirDouble(BigDecimal value) {

        return value != null
                ? value.doubleValue()
                : 0.0;
    }
    
    private SaleItem convertirSaleItem(SaleDetailResponse response){
        
       SaleItem item = new SaleItem();

        /*
        ID del detalle
        */
        item.setDetailId(
            response.getId() != null
                    ? response.getId()
                    : 0
        );

        /*
        Producto
        */
        Product product = new Product();

        product.setId(
            response.getProductId() != null
                    ? response.getProductId()
                    : 0
        );

        product.setModel(response.getProductName());
        product.setPrice(convertirDouble(response.getUnitPrice()));

        item.setProduct(product);

        /*
        Cantidad
        */
        item.setQuantity(
            response.getQuantity() != null
                    ? response.getQuantity()
                    : 0
        );

        /*
        Descuento
        */
        double descuentoTotal = convertirDouble(response.getAppliedDiscount());
        
        int cantidad = response.getQuantity() != null ? response.getQuantity() : 0;
        
        double descuentoUnitario = cantidad > 0 ? descuentoTotal / cantidad : 0;
        
        item.setDiscount(descuentoUnitario);

        /*
        Nombre del descuento
        */
        item.setDiscountName(
            response.getDiscountName()
        );

        /*
        Precio
        */
        item.setPrice(
            convertirDouble(response.getUnitPrice())
        );

        /*
        Descripción
        */
        item.setDescription(
            response.getProductName()
        );

        return item; 
    }
    
    public ClientStats getClientStats(int clientId)
                throws IOException, InterruptedException {
        
        ClientStatsResponse response = get(
            BASE_URL + "/client/" + clientId + "/stats",
            ClientStatsResponse.class
    );

    ClientStats stats = new ClientStats();

    stats.setTotalPurchases(response.getTotalPurchases());
    stats.setTotalSpent(
            response.getTotalSpent() != null
                    ? response.getTotalSpent().doubleValue()
                    : 0.0
    );
    stats.setLastVisit(response.getLastVisit());
    stats.setClientSince(response.getClientSince());

    return stats;
    }
    
    public boolean cancelSale(int saleId)
        throws IOException, InterruptedException {

        patch(
            BASE_URL + "/" + saleId + "/cancel",
            null,
            Void.class
        );

    return true;
    }
    
    /*
    Consultar trabajos por realizar.
    */
    public List<Sale> getSalesToDo()
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        List<SaleResponse> responses =
            getList(
                    BASE_URL + "/pending/todo?branchId=" + branchId,
                    SaleResponse.class
            );

        List<Sale> sales = new ArrayList<>();

        for (SaleResponse response : responses) {
            sales.add(convertirSale(response));
        }
        return sales;
    }

    /*
    Consultar trabajos por entregar.
    */
    public List<Sale> getSalesToDeliver()
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        List<SaleResponse> responses =
            getList(
                    BASE_URL + "/pending/deliver?branchId=" + branchId,
                    SaleResponse.class
            );
        List<Sale> sales = new ArrayList<>();

        for (SaleResponse response : responses) {
            sales.add(convertirSale(response));
        }
        return sales;
    }

    /*
    Buscar ventas pendientes.
    */
    public List<Sale> searchPending(String filter)
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        String encodedFilter =
            java.net.URLEncoder.encode(
                    filter != null ? filter : "",
                    java.nio.charset.StandardCharsets.UTF_8
            );

        List<SaleResponse> responses =
            getList(
                    BASE_URL
                            + "/pending/search?branchId="
                            + branchId
                            + "&filter="
                            + encodedFilter,
                    SaleResponse.class
            );

        List<Sale> sales = new ArrayList<>();

        for (SaleResponse response : responses) {
            sales.add(convertirSale(response));
        }
        return sales;
    }
    
    /*
    Recepcionar venta.
    */
    public boolean receiveSale(int saleId)
        throws IOException, InterruptedException {

        patch(
            BASE_URL + "/" + saleId + "/receive",
            null,
            Void.class
        );
        return true;
    }

    /*
    Entregar venta.
    */
    public boolean deliverSale(int saleId)
        throws IOException, InterruptedException {

        patch(
            BASE_URL + "/" + saleId + "/deliver",
            null,
            Void.class
        );
        return true;
    }
    
    /*
    Consultar resumen de ventas pendientes.
    */
    public PendingReport getPendingSummary()
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        PendingSummaryResponse response =
            get(
                    BASE_URL
                            + "/pending/summary?branchId="
                            + branchId,
                    PendingSummaryResponse.class
            );

        PendingReport report  = new PendingReport();

        report.setSalesToDo(
            response.getSalesToDo()
        );

        report.setSalesToDeliver(
            response.getSalesToDeliver()
        );

        report.setBalanceToDo(
            response.getBalanceToDo()!= null
                    ? response.getBalanceToDo().doubleValue()
                    : 0.0
        );

        report.setBalanceToDeliver(
            response.getBalanceToDeliver()!= null
                    ? response.getBalanceToDeliver().doubleValue()
                    : 0.0
        );

        return report;
    }
}
