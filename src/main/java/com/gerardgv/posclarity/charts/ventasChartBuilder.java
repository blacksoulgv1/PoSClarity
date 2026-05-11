
package com.gerardgv.posclarity.charts;

import java.time.LocalDate;
import java.util.Map;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

public class ventasChartBuilder {
       public static void build(LineChart<String, Number> chart,
                             Map<Integer, Double> ventasPorDia,
                             LocalDate fecha) {

        chart.getData().clear();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();

        int diasMes = fecha.lengthOfMonth();

        for (int dia = 1; dia <= diasMes; dia++) {
            double total = ventasPorDia.getOrDefault(dia, 0.0);

            XYChart.Data<String, Number> data =
                    new XYChart.Data<>(String.valueOf(dia), total);

            serie.getData().add(data);

            data.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) {

                    if (total == 0) {
                        node.setStyle("-fx-background-color: transparent, transparent;");
                    } else {
                        node.setStyle("-fx-background-color: #00C853, white;");
                    }

                    Tooltip tooltip = new Tooltip(
                            "Día " + data.getXValue() +
                            "\n$" + String.format("%,.2f", total)
                    );

                    Tooltip.install(node, tooltip);
                }
            });
        }

        chart.getData().add(serie);

        // 🎨 Estilo PRO
        chart.lookup(".chart-series-line")
             .setStyle("-fx-stroke: #00C853; -fx-stroke-width: 2px;");

        chart.setAnimated(true);
        chart.setLegendVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setCreateSymbols(true);

        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelRotation(45);
    }
}
