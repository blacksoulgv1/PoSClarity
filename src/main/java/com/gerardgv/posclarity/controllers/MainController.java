package com.gerardgv.posclarity.controllers;

//Librerias Java
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import com.gerardgv.posclarity.utils.ViewInf;

//Librerias Java Fx
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {
    
    //guardar vistas
    private Map<String,ViewInf> Showviews= new HashMap<>();

    @FXML
    private StackPane stackContent;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         // Metodo para iniciar la ventana de ventas al abrir
        viewVista("Saleview");
    }    

     // Metodo para el llamado de ventanas.
    private void viewVista(String vista) {
        
        try{
            
            //Si la Vista ya Fue Cargada se Reutiliza.
            if (Showviews.containsKey(vista)){
                stackContent.getChildren().setAll(
                        Showviews.get(vista).getView()
                );
                return;
            }
            
            //La Vista Se Crea Por Primera Vez.
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gerardgv/posclarity/views/" + vista + ".fxml"));
            
            Parent root = loader.load();
            Object controller = loader.getController();
            
            //Limpiar Estilos
            stackContent.getStylesheets().clear();
            
            // se Agrega el Css Correctamente
            root.getStylesheets().add(getClass().getResource("/com/gerardgv/posclarity/css/main-layout.css").toExternalForm());
            
            //Se Guarda Vista & Controlador.
            Showviews.put(vista, new ViewInf(root, controller));
            mostrarVista(root);
        }catch(IOException e){
            System.out.println("Error al Cargar la Vista:" + vista);
            e.printStackTrace();
        }       
    }
    
    private void mostrarVista(Parent root) {
        
        StackPane.setAlignment(root, javafx.geometry.Pos.TOP_LEFT);
        stackContent.getChildren().setAll(root);
        System.out.println("Stack width: " + stackContent.getWidth());
System.out.println("Stack height: " + stackContent.getHeight());
System.out.println("View width: " + root.getBoundsInParent().getWidth());
System.out.println("View height: " + root.getBoundsInParent().getHeight());
    }
    // Funcionamiento de botonos para ventanas
    
    @FXML
    private void abrirNuevaVenta(){
        viewVista("Saleview");
    }
    
     @FXML
    private void AbrirGarantias(){
        viewVista("Garantias");
    }
    
    @FXML
    private void abrirPendintes(){
        viewVista("Ventas_pendientes");
    }
    
    @FXML
    private void abrirEntregados(){
        viewVista("ventas_entregadas");
    }
    
    @FXML
    private void abrirProductos(){
        
        /*Optional<Empleados> autorizado =
        AuthorizationDialog.solicitarGerente();

        if (autorizado.isEmpty()) {
            return;
        }*/
        
        viewVista("Productsview");
    }
    
    @FXML
    private void abrirClientes(){
        viewVista("Clientsview");
    }
    
    @FXML
    private void abrirSucursales(){
        
        /*Optional<Empleados> autorizado =
        AuthorizationDialog.solicitarGerente();

        if (autorizado.isEmpty()) {
            return;
        }*/
        
        viewVista("Branch");
    }
    @FXML
    private void abrirTraspaso(){
        viewVista("Transfer");
    }
    
    @FXML
    private void abrirDescuento(){
        /*Optional<Empleados> autorizado =
        AuthorizationDialog.solicitarGerente();

        if (autorizado.isEmpty()) {
            return;
        }*/
        
        viewVista("Discount");
    }
    @FXML
    private void abrirAbono(){
        viewVista("Pending");
    }
    
    @FXML
    private void abrirEmpleados(){
        viewVista("Sellers");
    }
    @FXML
    private void abrirReportes(){
        viewVista("Reports");
    }

}
