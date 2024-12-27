package org.example.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class HomePanel extends JPanel {
    private JLabel lblProgramName;
    private JLabel lblUserName;
    private JLabel lblTotalUang;
    private JButton btnPemasukan;
    private JButton btnPengeluaran;

    public HomePanel(double totalUang, ActionListener pemasukanListener, ActionListener pengeluaranListener) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new GridLayout(3, 1, 15, 15));
        lblProgramName = new JLabel("Program Pengelola Keuangan Pribadi", SwingConstants.CENTER);
        lblProgramName.setFont(new Font("Arial", Font.BOLD, 28));

        lblUserName = new JLabel("Nama: Tabuarasa", SwingConstants.CENTER);
        lblUserName.setFont(new Font("Arial", Font.PLAIN, 20));

        lblTotalUang = new JLabel("Total Uang: Rp " + String.format("%.2f", totalUang), SwingConstants.CENTER);
        lblTotalUang.setFont(new Font("Arial", Font.PLAIN, 20));

        header.add(lblProgramName);
        header.add(lblUserName);
        header.add(lblTotalUang);

        add(header, BorderLayout.CENTER);

        // Navigation Buttons
        JPanel navPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnPemasukan = new JButton("Pemasukan");
        btnPemasukan.setFont(new Font("Arial", Font.PLAIN, 18));
        btnPengeluaran = new JButton("Pengeluaran");
        btnPengeluaran.setFont(new Font("Arial", Font.PLAIN, 18));

        btnPemasukan.addActionListener(pemasukanListener);
        btnPengeluaran.addActionListener(pengeluaranListener);

        navPanel.add(btnPemasukan);
        navPanel.add(btnPengeluaran);

        add(navPanel, BorderLayout.SOUTH);
    }

    public void setTotalUang(double totalUang) {
        lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
    }

    public JLabel getTotalUangLabel() {
        return lblTotalUang;
    }
}
