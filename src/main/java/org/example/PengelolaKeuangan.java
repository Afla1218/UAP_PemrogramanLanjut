package org.example;

import org.example.data.WordHandler;
import org.example.model.Pemasukan;
import org.example.model.Pengeluaran;
import org.example.ui.HomePanel;
import org.example.ui.PemasukanPanel;
import org.example.ui.PengeluaranPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PengelolaKeuangan extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Data
    private double totalUang;
    private List<Pemasukan> pemasukanList;
    private List<Pengeluaran> pengeluaranList;

    public PengelolaKeuangan() {
        setTitle("Pengelola Keuangan Pribadi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Load Data
        totalUang = WordHandler.loadTotalUang();
        pemasukanList = WordHandler.loadPemasukan();
        pengeluaranList = WordHandler.loadPengeluaran();

        // Initialize CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Home Panel with Listeners
        HomePanel homePanel = new HomePanel(totalUang,
                e -> showPemasukanPanel(),
                e -> showPengeluaranPanel());
        mainPanel.add(homePanel, "Home");

        // Initialize Pemasukan Panel
        PemasukanPanel pemasukanPanel = new PemasukanPanel(totalUang, pemasukanList, homePanel.getTotalUangLabel(),
                e -> cardLayout.show(mainPanel, "Home"));
        mainPanel.add(pemasukanPanel, "Pemasukan");

        // Initialize Pengeluaran Panel
        PengeluaranPanel pengeluaranPanel = new PengeluaranPanel(totalUang, pengeluaranList, homePanel.getTotalUangLabel(),
                e -> cardLayout.show(mainPanel, "Home"));
        mainPanel.add(pengeluaranPanel, "Pengeluaran");

        add(mainPanel);

        // Show Home Panel
        cardLayout.show(mainPanel, "Home");
    }

    private void showPemasukanPanel() {
        pemasukanList = WordHandler.loadPemasukan();
        PemasukanPanel pemasukanPanel = new PemasukanPanel(totalUang, pemasukanList, 
                ((HomePanel) mainPanel.getComponent(0)).getTotalUangLabel(), 
                e -> cardLayout.show(mainPanel, "Home"));
        mainPanel.add(pemasukanPanel, "Pemasukan");
        cardLayout.show(mainPanel, "Pemasukan");
    }

    private void showPengeluaranPanel() {
        pengeluaranList = WordHandler.loadPengeluaran();
        PengeluaranPanel pengeluaranPanel = new PengeluaranPanel(totalUang, pengeluaranList, 
                ((HomePanel) mainPanel.getComponent(0)).getTotalUangLabel(), 
                e -> cardLayout.show(mainPanel, "Home"));
        mainPanel.add(pengeluaranPanel, "Pengeluaran");
        cardLayout.show(mainPanel, "Pengeluaran");
    }

    public static void main(String[] args) {
        // Pastikan Apache POI library ada di classpath
        SwingUtilities.invokeLater(() -> {
            PengelolaKeuangan app = new PengelolaKeuangan();
            app.setVisible(true);
        });
    }
}
