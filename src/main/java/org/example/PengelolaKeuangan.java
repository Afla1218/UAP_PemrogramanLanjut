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

/**
 * PengelolaKeuangan
 * Aplikasi pengelolaan keuangan pribadi berbasis GUI yang menggunakan Java Swing.
 * Aplikasi ini memungkinkan pengguna untuk mengelola pemasukan, pengeluaran,
 * dan melihat total uang yang dimiliki. Data disimpan dan dimuat menggunakan
 * kelas WordHandler untuk memastikan persistensi.
 *
 * @version 1.0
 * @since 2024-12-22
 */
public class PengelolaKeuangan extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Data
    private double totalUang;
    private List<Pemasukan> pemasukanList;
    private List<Pengeluaran> pengeluaranList;

    // Panels
    private HomePanel homePanel;
    private PemasukanPanel pemasukanPanel;
    private PengeluaranPanel pengeluaranPanel;

    public PengelolaKeuangan() {
        setTitle("Pengelola Keuangan Pribadi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Load Data
        totalUang = WordHandler.loadTotalUang();
        pemasukanList = WordHandler.loadPemasukan();
        pengeluaranList = WordHandler.loadPengeluaran();

        // Initialize Panels
        initializePanels();

        // Initialize Split Pane
        JSplitPane splitPane = createSplitPane();

        add(splitPane);
    }

    private void initializePanels() {
        // Initialize Home Panel with Listeners
        homePanel = new HomePanel(totalUang,
                e -> showHomePanel(),
                e -> showPemasukanPanel(),
                e -> showPengeluaranPanel());

        // Initialize Pemasukan Panel
        pemasukanPanel = new PemasukanPanel(totalUang, pemasukanList, homePanel.getTotalUangLabel(),
                e -> showHomePanel());

        // Initialize Pengeluaran Panel
        pengeluaranPanel = new PengeluaranPanel(totalUang, pengeluaranList, homePanel.getTotalUangLabel(),
                e -> showHomePanel());
    }

    private JSplitPane createSplitPane() {
        // Panel Kiri (Navigasi)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(52, 152, 219)); // Warna biru
        leftPanel.setLayout(new GridBagLayout());

        // Tambahkan tombol navigasi
        JPanel navButtonsPanel = new JPanel();
        navButtonsPanel.setLayout(new GridLayout(3, 1, 10, 10));
        navButtonsPanel.setOpaque(false); // Transparan agar warna latar kiri terlihat

        JButton btnHome = new JButton("Home");
        JButton btnPemasukan = new JButton("Pemasukan");
        JButton btnPengeluaran = new JButton("Pengeluaran");

        // Atur font dan warna tombol
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        JButton[] buttons = {btnHome, btnPemasukan, btnPengeluaran};
        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(41, 128, 185));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 50));
        }

        // Tambahkan ActionListener pada tombol
        btnHome.addActionListener(e -> showHomePanel());
        btnPemasukan.addActionListener(e -> showPemasukanPanel());
        btnPengeluaran.addActionListener(e -> showPengeluaranPanel());

        navButtonsPanel.add(btnHome);
        navButtonsPanel.add(btnPemasukan);
        navButtonsPanel.add(btnPengeluaran);

        leftPanel.add(navButtonsPanel);

        // Panel Kanan (Konten Utama)
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add(homePanel, "Home");
        mainPanel.add(pemasukanPanel, "Pemasukan");
        mainPanel.add(pengeluaranPanel, "Pengeluaran");

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, mainPanel);
        splitPane.setDividerSize(2); // Ukuran garis pembatas
        splitPane.setDividerLocation(200); // Lebar panel kiri
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        return splitPane;
    }

    private void showHomePanel() {
        homePanel.updateTotalUang(totalUang);
        cardLayout.show(mainPanel, "Home");
    }

    private void showPemasukanPanel() {
        pemasukanList = WordHandler.loadPemasukan();
        pemasukanPanel.updateData(totalUang, pemasukanList);
        cardLayout.show(mainPanel, "Pemasukan");
    }

    private void showPengeluaranPanel() {
        pengeluaranList = WordHandler.loadPengeluaran();
        pengeluaranPanel.updateData(totalUang, pengeluaranList);
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
