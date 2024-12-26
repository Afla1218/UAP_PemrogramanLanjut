import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author Ahmad Nizar
 * @author Aflla Abdi
 */

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
    // MODEL APLIKASI (SIMPLE STATE MANAGEMENT)
    // -----------------------------------------------------
    static class AppModel {
        private double totalPendapatan = 0;
        private double totalPengeluaran = 0;
        private double targetTabungan = 0;
        private double totalTabungan = 0; // totalPendapatan - totalPengeluaran - investasi (jika ada)

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
        //private LaporanPanel laporanPanel;
        private TabunganPanel tabunganPanel;
        //private TransaksiBerulangPanel transaksiBerulangPanel;
        //private KalkulatorPanel kalkulatorPanel;
        private PengaturanPanel pengaturanPanel;

        public MainFrame(AppModel model) {
            this.model = model;
            setTitle("TabuaRasa - Pengelola Keuangan Pribadi");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 700);
            setLocationRelativeTo(null);

            initMenuBar();
            initComponents();
        }

        private void initMenuBar() {
            JMenuBar menuBar = new JMenuBar();
            JMenu menuFile = new JMenu("File");
            JMenuItem miExit = new JMenuItem("Exit");
            miExit.addActionListener(e -> System.exit(0));
            menuFile.add(miExit);

            JMenu menuHelp = new JMenu("Help");
            JMenuItem miAbout = new JMenuItem("About");
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
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel panelInfo = new JPanel(new GridLayout(2,2,10,10));
            lblSaldo = new JLabel("Saldo saat ini: Rp 0");
            lblPendapatanBulanIni = new JLabel("Pendapatan Bulan Ini: Rp 0");
            lblPengeluaranBulanIni = new JLabel("Pengeluaran Bulan Ini: Rp 0");
            lblProgressTabungan = new JLabel("Progress Tabungan: 0%");

            panelInfo.add(lblSaldo);
            panelInfo.add(lblPendapatanBulanIni);
            panelInfo.add(lblPengeluaranBulanIni);
            panelInfo.add(lblProgressTabungan);

            JLabel lblTitle = new JLabel("Dashboard Keuangan");
            lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

            add(lblTitle, BorderLayout.NORTH);
            add(panelInfo, BorderLayout.CENTER);
            updateDashboardData();
        }

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

            lblSaldo.setText("Saldo saat ini: Rp " + saldo);
            lblPendapatanBulanIni.setText("Pendapatan: Rp " + pendapatan);
            lblPengeluaranBulanIni.setText("Pengeluaran: Rp " + pengeluaran);
            lblProgressTabungan.setText("Progress Tabungan: " + (int)(progress*100) + "%");
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
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            inputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Tambah Pendapatan"));

            txtJumlah = new JTextField(10);
            txtJumlah.setToolTipText("Masukkan jumlah pendapatan");
            cmbKategori = new JComboBox<>(new String[]{"Pekerjaan", "Investasi", "Lainnya"});
            cmbKategori.setToolTipText("Pilih kategori pendapatan");
            btnTambah = new JButton("Tambah");
            btnTambah.setToolTipText("Klik untuk menambahkan pendapatan");

            inputPanel.add(new JLabel("Jumlah:"));
            inputPanel.add(txtJumlah);
            inputPanel.add(new JLabel("Kategori:"));
            inputPanel.add(cmbKategori);
            inputPanel.add(btnTambah);

            tblModel = new DefaultTableModel(new Object[]{"Tanggal","Jumlah","Kategori"},0);
            tblRiwayat = new JTable(tblModel);
            JScrollPane scroll = new JScrollPane(tblRiwayat);
            scroll.setBorder(new TitledBorder(new EtchedBorder(), "Riwayat Pendapatan"));

            add(inputPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);

            btnTambah.addActionListener(e -> {
                try {
                    double jumlah = Double.parseDouble(txtJumlah.getText());
                    if (jumlah <= 0) throw new NumberFormatException();
                    String kategori = (String) cmbKategori.getSelectedItem();
                    Date tanggal = new Date();
                    tblModel.addRow(new Object[]{tanggal, jumlah, kategori});
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
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            inputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Tambah Pengeluaran"));

            txtJumlah = new JTextField(10);
            txtJumlah.setToolTipText("Masukkan jumlah pengeluaran");
            cmbKategori = new JComboBox<>(new String[]{"Makanan", "Transportasi", "Hiburan", "Lainnya"});
            cmbKategori.setToolTipText("Pilih kategori pengeluaran");
            btnTambah = new JButton("Tambah");
            btnTambah.setToolTipText("Klik untuk menambahkan pengeluaran");

            inputPanel.add(new JLabel("Jumlah:"));
            inputPanel.add(txtJumlah);
            inputPanel.add(new JLabel("Kategori:"));
            inputPanel.add(cmbKategori);
            inputPanel.add(btnTambah);

            tblModel = new DefaultTableModel(new Object[]{"Tanggal","Jumlah","Kategori"},0);
            tblRiwayat = new JTable(tblModel);
            JScrollPane scroll = new JScrollPane(tblRiwayat);
            scroll.setBorder(new TitledBorder(new EtchedBorder(), "Riwayat Pengeluaran"));

            add(inputPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);

            btnTambah.addActionListener(e -> {
                try {
                    double jumlah = Double.parseDouble(txtJumlah.getText());
                    if (jumlah <= 0) throw new NumberFormatException();
                    String kategori = (String) cmbKategori.getSelectedItem();
                    Date tanggal = new Date();
                    tblModel.addRow(new Object[]{tanggal, jumlah, kategori});
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
            setBorder(new EmptyBorder(20,20,20,20));

            lblInfo = new JLabel("Grafik Pendapatan vs Pengeluaran akan ditampilkan di sini (Belum diimplementasi)");
            lblInfo.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel lblTitle = new JLabel("Laporan Keuangan");
            lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

            add(lblTitle, BorderLayout.NORTH);
            add(lblInfo, BorderLayout.CENTER);
        }

        public void updateLaporan(/* data */) {
            // TODO: update chart dan laporan
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

        public TabunganPanel(AppModel model, DashboardPanel dashboard) {
            this.model = model;
            this.dashboard = dashboard;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            inputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Target Tabungan"));

            txtNamaTarget = new JTextField(10);
            txtNamaTarget.setToolTipText("Masukkan nama target tabungan (misal: Rumah, Liburan)");
            txtTarget = new JTextField(10);
            txtTarget.setToolTipText("Masukkan jumlah target tabungan");
            btnSetTarget = new JButton("Set Target Tabungan");
            btnSetTarget.setToolTipText("Klik untuk menetapkan target tabungan");

            inputPanel.add(new JLabel("Nama Target:"));
            inputPanel.add(txtNamaTarget);
            inputPanel.add(new JLabel("Jumlah Target:"));
            inputPanel.add(txtTarget);
            inputPanel.add(btnSetTarget);

            progressBar = new JProgressBar(0,100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setBorder(new TitledBorder(new EtchedBorder(), "Progress Tabungan"));

            add(inputPanel, BorderLayout.NORTH);
            add(progressBar, BorderLayout.CENTER);

            btnSetTarget.addActionListener(e -> {
                try {
                    double target = Double.parseDouble(txtTarget.getText());
                    if (target <= 0) throw new NumberFormatException();
                    model.setTargetTabungan(target);
                    updateProgress();
                    dashboard.updateDashboardData();
                    JOptionPane.showMessageDialog(TabunganPanel.this,
                            "Target tabungan telah ditetapkan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex1) {
                    JOptionPane.showMessageDialog(TabunganPanel.this,
                            "Masukkan jumlah target yang valid (>0)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        public void updateProgress() {
            double current = model.getTotalTabungan();
            double target = model.getTargetTabungan();
            int value = 0;
            if(target > 0) {
                value = (int)((current/target)*100);
                if (value > 100) value = 100;
            }
            progressBar.setValue(value);
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
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            inputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Transaksi Berulang"));

            txtJumlah = new JTextField(10);
            txtJumlah.setToolTipText("Masukkan jumlah transaksi berulang");
            cmbKategori = new JComboBox<>(new String[]{"Pendapatan", "Pengeluaran"});
            cmbKategori.setToolTipText("Pilih tipe transaksi");
            cmbFrekuensi = new JComboBox<>(new String[]{"Harian", "Mingguan", "Bulanan"});
            cmbFrekuensi.setToolTipText("Pilih frekuensi transaksi");
            btnTambah = new JButton("Tambah Transaksi");
            btnTambah.setToolTipText("Klik untuk menambahkan transaksi berulang");

            inputPanel.add(new JLabel("Jumlah:"));
            inputPanel.add(txtJumlah);
            inputPanel.add(new JLabel("Tipe:"));
            inputPanel.add(cmbKategori);
            inputPanel.add(new JLabel("Frekuensi:"));
            inputPanel.add(cmbFrekuensi);
            inputPanel.add(btnTambah);

            model = new DefaultTableModel(new Object[]{"Jumlah","Tipe","Frekuensi"},0);
            tblTransaksiBerulang = new JTable(model);

            JScrollPane scroll = new JScrollPane(tblTransaksiBerulang);
            scroll.setBorder(new TitledBorder(new EtchedBorder(), "Daftar Transaksi Berulang"));

            add(inputPanel, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);

            btnTambah.addActionListener(e -> {
                try {
                    double jumlah = Double.parseDouble(txtJumlah.getText());
                    if (jumlah <= 0) throw new NumberFormatException();
                    String tipe = (String) cmbKategori.getSelectedItem();
                    String freq = (String) cmbFrekuensi.getSelectedItem();
                    model.addRow(new Object[]{jumlah, tipe, freq});
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
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel inputPanel = new JPanel(new GridLayout(4,2,10,10));
            inputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Input Data"));

            txtPendapatan = new JTextField();
            txtPendapatan.setToolTipText("Masukkan total pendapatan");
            txtPengeluaran = new JTextField();
            txtPengeluaran.setToolTipText("Masukkan total pengeluaran");
            txtInvestasi = new JTextField();
            txtInvestasi.setToolTipText("Masukkan total investasi");
            btnHitung = new JButton("Hitung");

            inputPanel.add(new JLabel("Total Pendapatan:"));
            inputPanel.add(txtPendapatan);
            inputPanel.add(new JLabel("Total Pengeluaran:"));
            inputPanel.add(txtPengeluaran);
            inputPanel.add(new JLabel("Total Investasi:"));
            inputPanel.add(txtInvestasi);
            inputPanel.add(new JLabel(""));
            inputPanel.add(btnHitung);

            JPanel outputPanel = new JPanel(new GridLayout(2,2,10,10));
            outputPanel.setBorder(new TitledBorder(new EtchedBorder(), "Hasil"));
            lblHasilRasio = new JLabel("Rasio: ");
            lblHasilBunga = new JLabel("Hasil Bunga: ");
            outputPanel.add(new JLabel("Rasio Tabungan/Pengeluaran/Investasi:"));
            outputPanel.add(lblHasilRasio);
            outputPanel.add(new JLabel("Perhitungan Bunga:"));
            outputPanel.add(lblHasilBunga);

            add(inputPanel, BorderLayout.NORTH);
            add(outputPanel, BorderLayout.CENTER);

            btnHitung.addActionListener(e -> {
                try {
                    double pendapatan = Double.parseDouble(txtPendapatan.getText());
                    double pengeluaran = Double.parseDouble(txtPengeluaran.getText());
                    double investasi = Double.parseDouble(txtInvestasi.getText());

                    double tabungan = pendapatan - pengeluaran - investasi;
                    lblHasilRasio.setText("Tabungan: " + tabungan + ", Investasi: " + investasi);

                    // Contoh kalkulasi bunga sederhana
                    double bunga = tabungan * 0.05; // misal bunga 5%
                    lblHasilBunga.setText("Bunga (5%): " + bunga);
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
            setBorder(new EmptyBorder(20,20,20,20));

            JPanel settingPanel = new JPanel(new GridBagLayout());
            settingPanel.setBorder(new TitledBorder(new EtchedBorder(), "Pengaturan"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5,5,5,5);

            cmbTema = new JComboBox<>(new String[]{"Terang", "Gelap"});
            btnBackupData = new JButton("Backup Data");
            btnBackupData.setToolTipText("Klik untuk memulai proses backup");
            lblReminder = new JLabel("Reminder Tagihan: Belum diimplementasi");
            backupProgressBar = new JProgressBar();
            backupProgressBar.setStringPainted(true);
            backupProgressBar.setVisible(false);

            gbc.gridx = 0; gbc.gridy = 0;
            settingPanel.add(new JLabel("Tema:"), gbc);
            gbc.gridx = 1;
            settingPanel.add(cmbTema, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            settingPanel.add(new JLabel("Pengingat Tagihan:"), gbc);
            gbc.gridx = 1;
            settingPanel.add(lblReminder, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            settingPanel.add(btnBackupData, gbc);

            gbc.gridx = 1; gbc.gridy = 2;
            settingPanel.add(backupProgressBar, gbc);

            add(settingPanel, BorderLayout.NORTH);

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
                parent.setSize(1200,700);
            });

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
    }

    // -----------------------------------------------------
    // UTILITAS
    // -----------------------------------------------------
    private static void setLookAndFeel(String laf) {
        try {
            UIManager.setLookAndFeel(laf);
        } catch(Exception e) {
            // ignore, use default
        }
    }
}
