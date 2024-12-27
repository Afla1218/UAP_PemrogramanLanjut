package org.example.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomePanel extends JPanel {
    private JLabel lblTotalUang;
    private JButton btnPemasukan;
    private JButton btnPengeluaran;

    public HomePanel(double totalUang, ActionListener homeListener, ActionListener pemasukanListener, ActionListener pengeluaranListener) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 15, 15));
        JLabel lblTitle = new JLabel("Program Pengelola Keuangan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));

        lblTotalUang = new JLabel("Total Uang: Rp " + String.format("%.2f", totalUang), SwingConstants.CENTER);
        lblTotalUang.setFont(new Font("Arial", Font.PLAIN, 20));

        header.add(lblTitle);
        header.add(lblTotalUang);

        add(header, BorderLayout.CENTER);

//        // Navigation Buttons
//        JPanel navPanel = new JPanel(new GridLayout(3, 1, 20, 20));
//        btnPemasukan = new JButton("Pemasukan");
//        btnPengeluaran = new JButton("Pengeluaran");
//
//        btnPemasukan.setFont(new Font("Arial", Font.PLAIN, 18));
//        btnPengeluaran.setFont(new Font("Arial", Font.PLAIN, 18));
//
//        btnPemasukan.addActionListener(pemasukanListener);
//        btnPengeluaran.addActionListener(pengeluaranListener);
//
//        navPanel.add(btnPemasukan);
//        navPanel.add(btnPengeluaran);
//
//        add(navPanel, BorderLayout.SOUTH);
    }

    public void updateTotalUang(double totalUang) {
        lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
    }

    public JLabel getTotalUangLabel() {
        return lblTotalUang; // Mengembalikan referensi ke lblTotalUang
    }
}
