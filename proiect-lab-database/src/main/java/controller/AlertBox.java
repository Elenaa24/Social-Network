package controller;

import javafx.scene.control.Alert;


public class AlertBox {
    static String alerta;
    static String txt;
    public AlertBox(String alerta, String txt) {
        this.alerta = alerta;
        this.txt = txt;
    }

    public  static void display(){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(alerta);
        alert.setContentText(txt);
        alert.showAndWait();
    }
}
