package com.gerardgv.posclarity.charts;

import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

public class MetaChartBuilder {
    public static void build(PieChart chart, double total, double meta) {

        chart.getData().clear();

        double restante = Math.max(meta - total, 0);

        PieChart.Data avance = new PieChart.Data("Avance", total);
        PieChart.Data faltante = new PieChart.Data("Restante", restante);

        chart.getData().addAll(avance, faltante);

        // 🎨 colores
        avance.getNode().setStyle("-fx-pie-color: #00C853;");
        faltante.getNode().setStyle("-fx-pie-color: #FF5252;");

        double porcentaje = meta == 0 ? 0 : (total / meta) * 100;

        chart.setTitle(String.format("Avance: %.1f%%", porcentaje));
        chart.setLabelsVisible(false);

        for (PieChart.Data data : chart.getData()) {
            Tooltip tooltip = new Tooltip(
                data.getName() + ": $" +
                String.format("%,.2f", data.getPieValue())
            );
            Tooltip.install(data.getNode(), tooltip);
        }
    }
}
