/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AlgorithmsResults;

import ProblemRepresentation.Solution;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

/**
 *
 * @author renansantos
 */
public class ResultsGraphicsForConvergence {

    private List<Double> hypervolumeConvergence;
    private JFreeChart convergence;
    private String folder;
    private String convergenceFileName;

    public ResultsGraphicsForConvergence(List<Double> hypervolumeConverence, JFreeChart convergence, 
            String folder, String convergenceFileName) {
        this.hypervolumeConvergence = hypervolumeConverence;
        this.convergence = convergence;
        this.folder = folder;
        this.convergenceFileName = convergenceFileName;
        boolean successForCreateFolder = (new File(folder)).mkdirs();
    }
    
    public void buildConvergenceGraphic() throws FileNotFoundException, IOException {
        this.convergence = ChartFactory.createLineChart("Combined Pareto Set", "Objective Function 1",
                "Objective Function 2", createDataset(), PlotOrientation.VERTICAL, true, true, false);
        Shape serieShape = ShapeUtilities.createDiagonalCross(3, 1);

        XYPlot xyPlot = (XYPlot) this.convergence.getPlot();
        
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        xyPlot.setRangeGridlinesVisible(true);
        xyPlot.setRangeGridlinePaint(Color.gray);
        xyPlot.setDomainGridlinesVisible(true);
        xyPlot.setDomainGridlinePaint(Color.gray);
        xyPlot.setBackgroundPaint(Color.white);
        xyPlot.setDomainCrosshairLockedOnData(true);
        
        xyPlot.addChangeListener(new PlotChangeListener() {
            @Override
            public void plotChanged(PlotChangeEvent pce) {
                System.out.println(pce.getType());
            }
        });
        
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesShape(0, serieShape);
        renderer.setSeriesPaint(0, Color.red);

        this.saveGraphicInFile(new FileOutputStream(folder + "/" + convergenceFileName));
    }

    private XYDataset createDataset() {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("NSGA-II");
        for (int i = 0; i < this.hypervolumeConvergence.size(); i++) {
            double x = this.hypervolumeConvergence.get(i);
           // series.add(x);
        }
        result.addSeries(series);
        return result;
    }

    private void saveGraphicInFile(OutputStream out) throws IOException {
        ChartUtilities.writeChartAsPNG(out, this.convergence, 1024, 600);
    }

    private void showGraphic() throws FileNotFoundException, IOException {
        JFrame frame = new JFrame("Combined Pareto Set");
        JPanel graphic = this.getPanel();
        graphic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //JOptionPane.showMessageDialog(null, "Test - Clicked Point = " + e.getPoint());
            }
        });

        frame.add(graphic);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public JPanel getPanel() {
        return new ChartPanel(convergence);
    }
    
    
}
