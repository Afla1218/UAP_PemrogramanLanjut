import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class KeuanganApp extends JFrame {
    // Komponen UI
    private JComboBox<String> jenisComboBox;
    private JTextField jumlahField;
    private JTextField deskripsiField;
    private JButton tambahButton;
    private JTable transaksiTable;
    private DefaultTableModel tableModel;
    private JLabel saldoLabel;

    // Variabel untuk menghitung saldo
    private double saldo = 0.0;

    public KeuanganApp() {
        setTitle("Aplikasi Pengelolaan Keuangan");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel input
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Tambah Transaksi"));

        inputPanel.add(new JLabel("Jenis:"));
        jenisComboBox = new JComboBox<>(new String[]{"Pemasukan", "Pengeluaran"});
        inputPanel.add(jenisComboBox);

        inputPanel.add(new JLabel("Jumlah:"));
        jumlahField = new JTextField();
        inputPanel.add(jumlahField);

        inputPanel.add(new JLabel("Deskripsi:"));
        deskripsiField = new JTextField();
        inputPanel.add(deskripsiField);

        tambahButton = new JButton("Tambah");
        inputPanel.add(tambahButton);

        // Panel tabel
        tableModel = new DefaultTableModel(new String[]{"Jenis", "Jumlah", "Deskripsi"}, 0);
        transaksiTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(transaksiTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Transaksi"));

        // Panel saldo
        JPanel saldoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saldoLabel = new JLabel("Saldo: Rp 0,00");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        saldoPanel.add(saldoLabel);

        // Tambahkan komponen ke frame
        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(saldoPanel, BorderLayout.SOUTH);

        // Action listener untuk tombol tambah
        tambahButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahTransaksi();
            }
        });
    }

    private void tambahTransaksi() {
        String jenis = (String) jenisComboBox.getSelectedItem();
        String jumlahText = jumlahField.getText().trim();
        String deskripsi = deskripsiField.getText().trim();

        if (jumlahText.isEmpty() || deskripsi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi semua field.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double jumlah;
        try {
            jumlah = Double.parseDouble(jumlahText);
            if (jumlah < 0) {
                throw new NumberFormatException("Jumlah tidak boleh negatif.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka positif.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tambahkan ke tabel
        tableModel.addRow(new Object[]{jenis, String.format("Rp %.2f", jumlah), deskripsi});

        // Update saldo
        if (jenis.equals("Pemasukan")) {
            saldo += jumlah;
        } else {
            saldo -= jumlah;
        }
        saldoLabel.setText(String.format("Saldo: Rp %.2f", saldo));

        // Bersihkan field input
        jumlahField.setText("");
        deskripsiField.setText("");
    }

    public static void main(String[] args) {
        // Jalankan aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KeuanganApp().setVisible(true);
            }
        });
    }
}
