import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class TabuaRasa {
    public static void main(String[] args) {
        // Mengatur LookAndFeel default
        setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(() -> {
            AppModel model = new AppModel();
            MainFrame frame = new MainFrame(model);
            frame.setVisible(true);
        });
    }

    // -----------------------------------------------------
    // MODEL APLIKASI (MANAGEMENT STATE SEDERHANA)
    // -----------------------------------------------------
    static class AppModel {
        private double totalPendapatan = 0;
        private double totalPengeluaran = 0;
        private double targetTabungan = 0;

        public double getTotalPendapatan() {
            return totalPendapatan;
        }

        public double getTotalPengeluaran() {
            return totalPengeluaran;
        }

        public double getTargetTabungan() {
            return targetTabungan;
        }

        public double getTotalTabungan() {
            return totalPendapatan - totalPengeluaran;
        }

        public void addPendapatan(double amount) {
            totalPendapatan += amount;
        }

        public void addPengeluaran(double amount) {
            totalPengeluaran += amount;
        }

        public void setTargetTabungan(double target) {
            this.targetTabungan = target;
        }
    }

    // -----------------------------------------------------
    // FRAME UTAMA
    // -----------------------------------------------------
    static class MainFrame extends JFrame {
        private JTabbedPane tabbedPane;
        private AppModel model;

        private DashboardPanel dashboardPanel;
        private PendapatanPanel pendapatanPanel;
        private PengeluaranPanel pengeluaranPanel;
        private LaporanPanel laporanPanel;
        private TabunganPanel tabunganPanel;
        private TransaksiBerulangPanel transaksiBerulangPanel;
        private KalkulatorPanel kalkulatorPanel;
        private PengaturanPanel pengaturanPanel;

        public MainFrame(AppModel model) {
            this.model = model;
            setTitle("TabuaRasa - Pengelola Keuangan Pribadi");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1300, 800);
            setLocationRelativeTo(null);
            setMinimumSize(new Dimension(1200, 700));

            initMenuBar();
            initComponents();
        }

        // Inisialisasi Menu Bar tanpa ikon
        private void initMenuBar() {
            JMenuBar menuBar = new JMenuBar();
            menuBar.setBackground(new Color(60, 63, 65));

            // Menu File
            JMenu menuFile = new JMenu("File");
            menuFile.setForeground(Color.WHITE);
            JMenuItem miExit = new JMenuItem("Exit");
            // miExit.setIcon(new ImageIcon(getClass().getResource("/icons/exit.png"))); // Di-comment
            miExit.addActionListener(e -> System.exit(0));
            menuFile.add(miExit);

            // Menu Help
            JMenu menuHelp = new JMenu("Help");
            menuHelp.setForeground(Color.WHITE);
            JMenuItem miAbout = new JMenuItem("About");
            // miAbout.setIcon(new ImageIcon(getClass().getResource("/icons/about.png"))); // Di-comment
            miAbout.addActionListener(e -> {
                JOptionPane.showMessageDialog(this,
                        "TabuaRasa - Pengelola Keuangan Pribadi\nVersi 1.0\nLebih Interaktif dan Responsif",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            menuHelp.add(miAbout);

            menuBar.add(menuFile);
            menuBar.add(menuHelp);

            setJMenuBar(menuBar);
        }

        // Inisialisasi Semua Komponen Panel tanpa ikon pada tab
        private void initComponents() {
            dashboardPanel = new DashboardPanel(model);
            pendapatanPanel = new PendapatanPanel(model, dashboardPanel);
            pengeluaranPanel = new PengeluaranPanel(model, dashboardPanel);
            laporanPanel = new LaporanPanel();
            tabunganPanel = new TabunganPanel(model, dashboardPanel);
            transaksiBerulangPanel = new TransaksiBerulangPanel();
            kalkulatorPanel = new KalkulatorPanel();
            pengaturanPanel = new PengaturanPanel(this);

            tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            // Menambahkan tab tanpa ikon
            tabbedPane.addTab("Dashboard", dashboardPanel);
            tabbedPane.addTab("Pendapatan", pendapatanPanel);
            tabbedPane.addTab("Pengeluaran", pengeluaranPanel);
            tabbedPane.addTab("Laporan", laporanPanel);
            tabbedPane.addTab("Tabungan", tabunganPanel);
            tabbedPane.addTab("Transaksi Berulang", transaksiBerulangPanel);
            tabbedPane.addTab("Kalkulator", kalkulatorPanel);
            tabbedPane.addTab("Pengaturan", pengaturanPanel);

            add(tabbedPane, BorderLayout.CENTER);
        }
    }

    // -----------------------------------------------------
    // PANEL DASHBOARD
    // -----------------------------------------------------
    static class DashboardPanel extends JPanel {
        private AppModel model;
        private JLabel lblSaldo;
        private JLabel lblPendapatanBulanIni;
        private JLabel lblPengeluaranBulanIni;
        private JLabel lblProgressTabungan;

        public DashboardPanel(AppModel model) {
            this.model = model;
            setLayout(new BorderLayout());
            setBackground(new Color(230, 230, 250));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Judul
            JLabel lblTitle = new JLabel("Dashboard Keuangan");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitle.setForeground(new Color(25, 25, 112));

            // Panel Informasi
            JPanel panelInfo = new JPanel(new GridBagLayout());
            panelInfo.setOpaque(false);
            panelInfo.setBorder(new TitledBorder(new LineBorder(new Color(25, 25, 112), 2), "Informasi Keuangan", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(25, 25, 112)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(20, 20, 20, 20);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;

            lblSaldo = new JLabel("Saldo saat ini: Rp 0");
            lblSaldo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            panelInfo.add(lblSaldo, gbc);

            gbc.gridy++;
            lblPendapatanBulanIni = new JLabel("Pendapatan Bulan Ini: Rp 0");
            lblPendapatanBulanIni.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            panelInfo.add(lblPendapatanBulanIni, gbc);

            gbc.gridy++;
            lblPengeluaranBulanIni = new JLabel("Pengeluaran Bulan Ini: Rp 0");
            lblPengeluaranBulanIni.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            panelInfo.add(lblPengeluaranBulanIni, gbc);

            gbc.gridy++;
            lblProgressTabungan = new JLabel("Progress Tabungan: 0%");
            lblProgressTabungan.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            panelInfo.add(lblProgressTabungan, gbc);

            add(lblTitle, BorderLayout.NORTH);
            add(panelInfo, BorderLayout.CENTER);
            updateDashboardData();
        }

        // Update Data pada Dashboard
        public void updateDashboardData() {
            double saldo = model.getTotalTabungan();
            double pendapatan = model.getTotalPendapatan();
            double pengeluaran = model.getTotalPengeluaran();
            double target = model.getTargetTabungan();
            double progress = 0;
            if (target > 0) {
                progress = saldo / target;
                if (progress > 1) progress = 1;
            }

            lblSaldo.setText(String.format("Saldo saat ini: Rp %.2f", saldo));
            lblPendapatanBulanIni.setText(String.format("Pendapatan Bulan Ini: Rp %.2f", pendapatan));
            lblPengeluaranBulanIni.setText(String.format("Pengeluaran Bulan Ini: Rp %.2f", pengeluaran));
            lblProgressTabungan.setText(String.format("Progress Tabungan: %d%%", (int)(progress * 100)));
        }
    }

    // -----------------------------------------------------
    // PANEL PENDAPATAN
    // -----------------------------------------------------
    static class PendapatanPanel extends JPanel {
        private AppModel model;
        private DashboardPanel dashboard;
        private JTextField txtJumlah;
        private JComboBox<String> cmbKategori;
        private JButton btnTambah;
        private JTable tblRiwayat;
        private DefaultTableModel tblModel;

        public PendapatanPanel(AppModel model, DashboardPanel dashboard) {
            this.model = model;
            this.dashboard = dashboard;
            setLayout(new BorderLayout());
            setBackground(new Color(245, 245, 220));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Panel Input Pendapatan
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setOpaque(false);
            inputPanel.setBorder(new TitledBorder(new LineBorder(new Color(34, 139, 34), 2), "Tambah Pendapatan", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(34, 139, 34)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            inputPanel.add(new JLabel("Jumlah: "), gbc);
            gbc.gridx++;
            txtJumlah = new JTextField(15);
            txtJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtJumlah, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Kategori: "), gbc);
            gbc.gridx++;
            cmbKategori = new JComboBox<>(new String[]{"Pekerjaan", "Investasi", "Lainnya"});
            cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(cmbKategori, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            btnTambah = new JButton("Tambah");
            btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnTambah.setBackground(new Color(34, 139, 34));
            btnTambah.setForeground(Color.WHITE);
            btnTambah.setFocusPainted(false);
            btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
            inputPanel.add(btnTambah, gbc);

            // Tambahkan Efek Hover pada Tombol
            btnTambah.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnTambah.setBackground(new Color(50, 205, 50));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnTambah.setBackground(new Color(34, 139, 34));
                }
            });

            // Panel Riwayat Pendapatan
            tblModel = new DefaultTableModel(new Object[]{"Tanggal", "Jumlah", "Kategori"}, 0);
            tblRiwayat = new JTable(tblModel);
            tblRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tblRiwayat.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            JScrollPane scroll = new JScrollPane(tblRiwayat);
            scroll.setBorder(new TitledBorder(new LineBorder(new Color(34, 139, 34), 2), "Riwayat Pendapatan", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(34, 139, 34)));

            add(inputPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);

            // Action Listener untuk Tombol Tambah
            btnTambah.addActionListener(e -> {
                try {
                    double jumlah = Double.parseDouble(txtJumlah.getText());
                    if (jumlah <= 0) throw new NumberFormatException();
                    String kategori = (String) cmbKategori.getSelectedItem();
                    Date tanggal = new Date();
                    tblModel.addRow(new Object[]{tanggal, String.format("Rp %.2f", jumlah), kategori});
                    model.addPendapatan(jumlah);
                    dashboard.updateDashboardData();
                    txtJumlah.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(PendapatanPanel.this,
                            "Masukkan jumlah yang valid (>0)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    // -----------------------------------------------------
    // PANEL PENGELUARAN
    // -----------------------------------------------------
    static class PengeluaranPanel extends JPanel {
        private AppModel model;
        private DashboardPanel dashboard;
        private JTextField txtJumlah;
        private JComboBox<String> cmbKategori;
        private JButton btnTambah;
        private JTable tblRiwayat;
        private DefaultTableModel tblModel;

        public PengeluaranPanel(AppModel model, DashboardPanel dashboard) {
            this.model = model;
            this.dashboard = dashboard;
            setLayout(new BorderLayout());
            setBackground(new Color(255, 228, 225));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Panel Input Pengeluaran
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setOpaque(false);
            inputPanel.setBorder(new TitledBorder(new LineBorder(new Color(178, 34, 34), 2), "Tambah Pengeluaran", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(178, 34, 34)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            inputPanel.add(new JLabel("Jumlah: "), gbc);
            gbc.gridx++;
            txtJumlah = new JTextField(15);
            txtJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtJumlah, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Kategori: "), gbc);
            gbc.gridx++;
            cmbKategori = new JComboBox<>(new String[]{"Makanan", "Transportasi", "Hiburan", "Lainnya"});
            cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(cmbKategori, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            btnTambah = new JButton("Tambah");
            btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnTambah.setBackground(new Color(178, 34, 34));
            btnTambah.setForeground(Color.WHITE);
            btnTambah.setFocusPainted(false);
            btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
            inputPanel.add(btnTambah, gbc);

            // Tambahkan Efek Hover pada Tombol
            btnTambah.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnTambah.setBackground(new Color(220, 20, 60));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnTambah.setBackground(new Color(178, 34, 34));
                }
            });

            // Panel Riwayat Pengeluaran
            tblModel = new DefaultTableModel(new Object[]{"Tanggal", "Jumlah", "Kategori"}, 0);
            tblRiwayat = new JTable(tblModel);
            tblRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tblRiwayat.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            JScrollPane scroll = new JScrollPane(tblRiwayat);
            scroll.setBorder(new TitledBorder(new LineBorder(new Color(178, 34, 34), 2), "Riwayat Pengeluaran", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(178, 34, 34)));

            add(inputPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);

            // Action Listener untuk Tombol Tambah
            btnTambah.addActionListener(e -> {
                try {
                    double jumlah = Double.parseDouble(txtJumlah.getText());
                    if (jumlah <= 0) throw new NumberFormatException();
                    String kategori = (String) cmbKategori.getSelectedItem();
                    Date tanggal = new Date();
                    tblModel.addRow(new Object[]{tanggal, String.format("Rp %.2f", jumlah), kategori});
                    model.addPengeluaran(jumlah);
                    dashboard.updateDashboardData();
                    txtJumlah.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(PengeluaranPanel.this,
                            "Masukkan jumlah yang valid (>0)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    // -----------------------------------------------------
    // PANEL LAPORAN
    // -----------------------------------------------------
    static class LaporanPanel extends JPanel {
        private JLabel lblInfo;

        public LaporanPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(224, 255, 255));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Judul
            JLabel lblTitle = new JLabel("Laporan Keuangan");
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblTitle.setForeground(new Color(0, 139, 139));

            // Informasi Placeholder
            lblInfo = new JLabel("Grafik Pendapatan vs Pengeluaran akan ditampilkan di sini (Belum diimplementasi)");
            lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
            lblInfo.setForeground(new Color(0, 139, 139));

            add(lblTitle, BorderLayout.NORTH);
            add(lblInfo, BorderLayout.CENTER);
        }

        public void updateLaporan(/* data */) {
            // TODO: Implementasi Grafik dan Laporan
        }
    }

    // -----------------------------------------------------
    // PANEL TABUNGAN
    // -----------------------------------------------------
    static class TabunganPanel extends JPanel {
        private AppModel model;
        private DashboardPanel dashboard;
        private JTextField txtTarget;
        private JTextField txtNamaTarget;
        private JButton btnSetTarget;
        private JProgressBar progressBar;
        private JLabel lblNamaTarget;

        public TabunganPanel(AppModel model, DashboardPanel dashboard) {
            this.model = model;
            this.dashboard = dashboard;
            setLayout(new BorderLayout());
            setBackground(new Color(240, 255, 240));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Panel Input Target Tabungan
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setOpaque(false);
            inputPanel.setBorder(new TitledBorder(new LineBorder(new Color(34, 139, 34), 2), "Target Tabungan", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(34, 139, 34)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            inputPanel.add(new JLabel("Nama Target: "), gbc);
            gbc.gridx++;
            txtNamaTarget = new JTextField(15);
            txtNamaTarget.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtNamaTarget, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Jumlah Target: "), gbc);
            gbc.gridx++;
            txtTarget = new JTextField(15);
            txtTarget.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtTarget, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            btnSetTarget = new JButton("Set Target Tabungan");
            btnSetTarget.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnSetTarget.setBackground(new Color(34, 139, 34));
            btnSetTarget.setForeground(Color.WHITE);
            btnSetTarget.setFocusPainted(false);
            btnSetTarget.setCursor(new Cursor(Cursor.HAND_CURSOR));
            inputPanel.add(btnSetTarget, gbc);

            // Tambahkan Efek Hover pada Tombol
            btnSetTarget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnSetTarget.setBackground(new Color(50, 205, 50));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnSetTarget.setBackground(new Color(34, 139, 34));
                }
            });

            // Panel Progress Tabungan
            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setOpaque(false);
            progressPanel.setBorder(new TitledBorder(new LineBorder(new Color(34, 139, 34), 2), "Progress Tabungan", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(34, 139, 34)));

            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setForeground(new Color(34, 139, 34));

            lblNamaTarget = new JLabel("Nama Target: -");
            lblNamaTarget.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblNamaTarget.setHorizontalAlignment(SwingConstants.CENTER);

            progressPanel.add(lblNamaTarget, BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);

            add(inputPanel, BorderLayout.NORTH);
            add(progressPanel, BorderLayout.CENTER);

            // Action Listener untuk Tombol Set Target
            btnSetTarget.addActionListener(e -> {
                try {
                    double target = Double.parseDouble(txtTarget.getText());
                    if (target <= 0) throw new NumberFormatException();
                    String namaTarget = txtNamaTarget.getText().trim();
                    if (namaTarget.isEmpty()) {
                        JOptionPane.showMessageDialog(TabunganPanel.this,
                                "Masukkan nama target tabungan", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    model.setTargetTabungan(target);
                    lblNamaTarget.setText("Nama Target: " + namaTarget);
                    updateProgress();
                    dashboard.updateDashboardData();
                    JOptionPane.showMessageDialog(TabunganPanel.this,
                            "Target tabungan telah ditetapkan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    txtTarget.setText("");
                    txtNamaTarget.setText("");
                } catch (NumberFormatException ex1) {
                    JOptionPane.showMessageDialog(TabunganPanel.this,
                            "Masukkan jumlah target yang valid (>0)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        // Update Progress Bar
        public void updateProgress() {
            double current = model.getTotalTabungan();
            double target = model.getTargetTabungan();
            int value = 0;
            if (target > 0) {
                value = (int)((current / target) * 100);
                if (value > 100) value = 100;
            }
            progressBar.setValue(value);
            progressBar.setString(value + "%");
        }
    }

    // -----------------------------------------------------
    // PANEL TRANSAKSI BERULANG
    // -----------------------------------------------------
    static class TransaksiBerulangPanel extends JPanel {
        private JTextField txtJumlah;
        private JComboBox<String> cmbKategori;
        private JComboBox<String> cmbFrekuensi;
        private JButton btnTambah;
        private JTable tblTransaksiBerulang;
        private DefaultTableModel model;

        public TransaksiBerulangPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(255, 250, 205));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Panel Input Transaksi Berulang
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setOpaque(false);
            inputPanel.setBorder(new TitledBorder(new LineBorder(new Color(218, 165, 32), 2), "Transaksi Berulang", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(218, 165, 32)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            inputPanel.add(new JLabel("Jumlah: "), gbc);
            gbc.gridx++;
            txtJumlah = new JTextField(15);
            txtJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtJumlah, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Tipe: "), gbc);
            gbc.gridx++;
            cmbKategori = new JComboBox<>(new String[]{"Pendapatan", "Pengeluaran"});
            cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(cmbKategori, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Frekuensi: "), gbc);
            gbc.gridx++;
            cmbFrekuensi = new JComboBox<>(new String[]{"Harian", "Mingguan", "Bulanan"});
            cmbFrekuensi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(cmbFrekuensi, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            btnTambah = new JButton("Tambah Transaksi");
            btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnTambah.setBackground(new Color(218, 165, 32));
            btnTambah.setForeground(Color.WHITE);
            btnTambah.setFocusPainted(false);
            btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
            inputPanel.add(btnTambah, gbc);

            // Tambahkan Efek Hover pada Tombol
            btnTambah.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnTambah.setBackground(new Color(238, 232, 170));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnTambah.setBackground(new Color(218, 165, 32));
                }
            });

            // Panel Daftar Transaksi Berulang
            model = new DefaultTableModel(new Object[]{"Jumlah", "Tipe", "Frekuensi"}, 0);
            tblTransaksiBerulang = new JTable(model);
            tblTransaksiBerulang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tblTransaksiBerulang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            JScrollPane scroll = new JScrollPane(tblTransaksiBerulang);
            scroll.setBorder(new TitledBorder(new LineBorder(new Color(218, 165, 32), 2), "Daftar Transaksi Berulang", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(218, 165, 32)));

            add(inputPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);

            // Action Listener untuk Tombol Tambah
            btnTambah.addActionListener(e -> {
                try {
                    double jumlah = Double.parseDouble(txtJumlah.getText());
                    if (jumlah <= 0) throw new NumberFormatException();
                    String tipe = (String) cmbKategori.getSelectedItem();
                    String freq = (String) cmbFrekuensi.getSelectedItem();
                    model.addRow(new Object[]{String.format("Rp %.2f", jumlah), tipe, freq});
                    txtJumlah.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TransaksiBerulangPanel.this,
                            "Masukkan jumlah yang valid (>0)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    // -----------------------------------------------------
    // PANEL KALKULATOR
    // -----------------------------------------------------
    static class KalkulatorPanel extends JPanel {
        private JTextField txtPendapatan;
        private JTextField txtPengeluaran;
        private JTextField txtInvestasi;
        private JButton btnHitung;
        private JLabel lblHasilRasio;
        private JLabel lblHasilBunga;

        public KalkulatorPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(255, 228, 196));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Panel Input Data
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setOpaque(false);
            inputPanel.setBorder(new TitledBorder(new LineBorder(new Color(255, 140, 0), 2), "Input Data", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(255, 140, 0)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            inputPanel.add(new JLabel("Total Pendapatan: "), gbc);
            gbc.gridx++;
            txtPendapatan = new JTextField(15);
            txtPendapatan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtPendapatan, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Total Pengeluaran: "), gbc);
            gbc.gridx++;
            txtPengeluaran = new JTextField(15);
            txtPengeluaran.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtPengeluaran, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            inputPanel.add(new JLabel("Total Investasi: "), gbc);
            gbc.gridx++;
            txtInvestasi = new JTextField(15);
            txtInvestasi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            inputPanel.add(txtInvestasi, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            btnHitung = new JButton("Hitung");
            btnHitung.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnHitung.setBackground(new Color(255, 140, 0));
            btnHitung.setForeground(Color.WHITE);
            btnHitung.setFocusPainted(false);
            btnHitung.setCursor(new Cursor(Cursor.HAND_CURSOR));
            inputPanel.add(btnHitung, gbc);

            // Tambahkan Efek Hover pada Tombol
            btnHitung.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnHitung.setBackground(new Color(255, 165, 0));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnHitung.setBackground(new Color(255, 140, 0));
                }
            });

            // Panel Output Hasil
            JPanel outputPanel = new JPanel(new GridBagLayout());
            outputPanel.setOpaque(false);
            outputPanel.setBorder(new TitledBorder(new LineBorder(new Color(255, 140, 0), 2), "Hasil", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(255, 140, 0)));

            gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;

            outputPanel.add(new JLabel("Rasio Tabungan/Pengeluaran/Investasi: "), gbc);
            gbc.gridx++;
            lblHasilRasio = new JLabel("Tabungan: Rp 0, Investasi: Rp 0");
            lblHasilRasio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            outputPanel.add(lblHasilRasio, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            outputPanel.add(new JLabel("Perhitungan Bunga: "), gbc);
            gbc.gridx++;
            lblHasilBunga = new JLabel("Bunga (5%): Rp 0");
            lblHasilBunga.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            outputPanel.add(lblHasilBunga, gbc);

            add(inputPanel, BorderLayout.NORTH);
            add(outputPanel, BorderLayout.CENTER);

            // Action Listener untuk Tombol Hitung
            btnHitung.addActionListener(e -> {
                try {
                    double pendapatan = Double.parseDouble(txtPendapatan.getText());
                    double pengeluaran = Double.parseDouble(txtPengeluaran.getText());
                    double investasi = Double.parseDouble(txtInvestasi.getText());

                    double tabungan = pendapatan - pengeluaran - investasi;
                    lblHasilRasio.setText(String.format("Tabungan: Rp %.2f, Investasi: Rp %.2f", tabungan, investasi));

                    // Contoh kalkulasi bunga sederhana
                    double bunga = tabungan * 0.05; // misal bunga 5%
                    lblHasilBunga.setText(String.format("Bunga (5%%): Rp %.2f", bunga));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(KalkulatorPanel.this,
                            "Masukkan angka yang valid", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    // -----------------------------------------------------
    // PANEL PENGATURAN
    // -----------------------------------------------------
    static class PengaturanPanel extends JPanel {
        private JComboBox<String> cmbTema;
        private JButton btnBackupData;
        private JLabel lblReminder;
        private JFrame parent;
        private JProgressBar backupProgressBar;

        public PengaturanPanel(JFrame parent) {
            this.parent = parent;
            setLayout(new BorderLayout());
            setBackground(new Color(240, 255, 255));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Panel Pengaturan
            JPanel settingPanel = new JPanel(new GridBagLayout());
            settingPanel.setOpaque(false);
            settingPanel.setBorder(new TitledBorder(new LineBorder(new Color(70, 130, 180), 2), "Pengaturan", TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(70, 130, 180)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(15, 15, 15, 15);
            gbc.gridx = 0;
            gbc.gridy = 0;

            // Tema
            settingPanel.add(new JLabel("Tema:"), gbc);
            gbc.gridx++;
            cmbTema = new JComboBox<>(new String[]{"Terang", "Gelap"});
            cmbTema.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            settingPanel.add(cmbTema, gbc);

            // Pengingat Tagihan (Placeholder)
            gbc.gridx = 0;
            gbc.gridy++;
            settingPanel.add(new JLabel("Pengingat Tagihan:"), gbc);
            gbc.gridx++;
            lblReminder = new JLabel("Belum diimplementasi");
            lblReminder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblReminder.setForeground(new Color(220, 20, 60));
            settingPanel.add(lblReminder, gbc);

            // Backup Data
            gbc.gridx = 0;
            gbc.gridy++;
            settingPanel.add(new JLabel("Backup Data:"), gbc);
            gbc.gridx++;
            btnBackupData = new JButton("Backup Data");
            btnBackupData.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btnBackupData.setBackground(new Color(70, 130, 180));
            btnBackupData.setForeground(Color.WHITE);
            btnBackupData.setFocusPainted(false);
            btnBackupData.setCursor(new Cursor(Cursor.HAND_CURSOR));
            settingPanel.add(btnBackupData, gbc);

            // Tambahkan Efek Hover pada Tombol Backup
            btnBackupData.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnBackupData.setBackground(new Color(100, 149, 237));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btnBackupData.setBackground(new Color(70, 130, 180));
                }
            });

            // Progress Bar Backup Data
            backupProgressBar = new JProgressBar();
            backupProgressBar.setStringPainted(true);
            backupProgressBar.setVisible(false);
            backupProgressBar.setForeground(new Color(70, 130, 180));

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            settingPanel.add(backupProgressBar, gbc);

            add(settingPanel, BorderLayout.CENTER);

            // Action Listener untuk Pilihan Tema
            cmbTema.addActionListener(e -> {
                String tema = (String) cmbTema.getSelectedItem();
                if (tema.equals("Gelap")) {
                    setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    UIManager.put("control", Color.DARK_GRAY);
                    UIManager.put("info", Color.GRAY);
                    UIManager.put("nimbusBase", Color.DARK_GRAY);
                    UIManager.put("nimbusAlertYellow", Color.YELLOW);
                    UIManager.put("nimbusDisabledText", Color.LIGHT_GRAY);
                    UIManager.put("nimbusFocus", Color.BLUE);
                    UIManager.put("nimbusGreen", Color.GREEN);
                    UIManager.put("nimbusInfoBlue", Color.BLUE);
                    UIManager.put("nimbusLightBackground", Color.GRAY);
                    UIManager.put("nimbusOrange", Color.ORANGE);
                    UIManager.put("nimbusRed", Color.RED);
                    UIManager.put("nimbusSelectedText", Color.WHITE);
                    UIManager.put("nimbusSelectionBackground", Color.BLACK);
                } else {
                    setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                SwingUtilities.updateComponentTreeUI(parent);
                parent.pack();
                parent.setSize(1300, 800);
            });

            // Action Listener untuk Backup Data
            btnBackupData.addActionListener(e -> {
                backupProgressBar.setVisible(true);
                backupProgressBar.setIndeterminate(true);
                btnBackupData.setEnabled(false);

                // Simulasi proses backup data dengan SwingWorker
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Simulasi waktu backup
                        Thread.sleep(2000);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(parent, "Data berhasil dibackup!", "Backup", JOptionPane.INFORMATION_MESSAGE);
                        } catch (InterruptedException | ExecutionException ex) {
                            JOptionPane.showMessageDialog(parent, "Gagal backup data!", "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            backupProgressBar.setIndeterminate(false);
                            backupProgressBar.setVisible(false);
                            btnBackupData.setEnabled(true);
                        }
                    }
                };
                worker.execute();
            });
        }

        // Utilitas untuk Mengatur LookAndFeel
        private static void setLookAndFeel(String laf) {
            try {
                UIManager.setLookAndFeel(laf);
            } catch(Exception e) {
                // ignore, gunakan default
            }
        }
    }

    // -----------------------------------------------------
    // UTILITAS
    // -----------------------------------------------------
    private static void setLookAndFeel(String laf) {
        try {
            UIManager.setLookAndFeel(laf);
        } catch(Exception e) {
            // ignore, gunakan default
        }
    }
}