import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class BasicDrawing {
    static DrawingPanel drawingPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Chess");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        drawingPanel = new DrawingPanel();

        drawingPanel.setPreferredSize(new Dimension(800, 600));
        frame.add(drawingPanel,BorderLayout.CENTER);
        JButton saveButton = new JButton("Save as PNG");
        saveButton.addActionListener(e -> {
            BufferedImage bImg = new BufferedImage(drawingPanel.getWidth(), drawingPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D cg = bImg.createGraphics();
            drawingPanel.paintAll(cg);
            try {
                if (ImageIO.write(bImg, "png", new File("./output_image.png")))
                {
                    System.out.println("-- saved");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        frame.add(saveButton,BorderLayout.NORTH);
        JButton graphButton = new JButton("Show the graph of turns");
        graphButton.addActionListener(e -> {
            JFrame graph = new JFrame();
            graph.setTitle("Graph");
            graph.setSize(500, 500);
            graph.setLocationRelativeTo(null);
            ChartPanel chartpanel = new ChartPanel(createGraph());
            graph.add(chartpanel);
            graph.pack();
            graph.setVisible(true);
        });
        frame.add(graphButton,BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED
                            && e.getKeyChar() == 'r' && (drawingPanel.mate || drawingPanel.pate)) {
                        drawingPanel.reset();
                    }
                    return false;
                });
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED
                            && e.getKeyChar() == 'i') {
                        drawingPanel.isToggledAi = !drawingPanel.isToggledAi;
                    }
                    return false;
                });
    }

    /**
     * Method creates graph
     * @return graph
     */
    public static JFreeChart createGraph() {
        JFreeChart graph = ChartFactory.createBarChart("Time of each move", "Count of moves", "Time in seconds",
                createDatabaseForGraph(), PlotOrientation.VERTICAL, true, true, false);
        NumberAxis rangeAxis = (NumberAxis) graph.getCategoryPlot().getRangeAxis();
        rangeAxis.setTickUnit(new NumberTickUnit(1));
        rangeAxis.setNumberFormatOverride(new DecimalFormat("0 s"));
        return graph;
    }

    /**
     * Database of graph
     * @return returns a database for the graph
     */
    private static CategoryDataset createDatabaseForGraph() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < drawingPanel.getTimeOfWhiteTurns().size(); i++) {
            dataset.addValue(drawingPanel.getTimeOfWhiteTurns().get(i), "White", drawingPanel.getCountOfWhiteTurns().get(i));
        }
        for (int i = 0; i < drawingPanel.getTimeOfBlackTurns().size(); i++) {
            dataset.addValue(drawingPanel.getTimeOfBlackTurns().get(i), "Black", drawingPanel.getCountOfBlackTurns().get(i));
        }
        return dataset;
    }
}