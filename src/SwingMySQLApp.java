import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Random;

public class SwingMySQLApp {
    private static final String url = "jdbc:mysql://localhost:3306/bolt_db";
    private static final String user = "root";
    private static final String password = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("Sikeres kapcsolódás az adatbázishoz!");

                // Fő ablak létrehozása
                JFrame frame = new JFrame("Bolt Termékkezelő");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);

                // Táblázat létrehozása
                String[] oszlopok = {"ID", "Név", "Darabszám", "Lejárati Dátum"};
                DefaultTableModel tableModel = new DefaultTableModel(oszlopok, 0);
                JTable table = new JTable(tableModel);

                // Egyedi cellamegjelenítő beállítása a lejárati dátum oszlophoz
                table.setDefaultRenderer(Object.class, new ExpiryDateCellRenderer());

                JScrollPane scrollPane = new JScrollPane(table);

                // Gombok létrehozása
                JButton addButton = new JButton("Termék hozzáadása");
                JButton deleteButton = new JButton("Törlés");
                JButton killAllButton = new JButton("Minden törlése");
                JButton addRandomButton = new JButton("Random termékek hozzáadása");
                JButton exportButton = new JButton("Exportálás Excelbe");
                listProducts(conn, tableModel);

                // Gombok eseménykezelői
                exportButton.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Válassz helyet az Excel fájl mentéséhez");
                    int userSelection = fileChooser.showSaveDialog(frame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        String filePath = fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx";
                        try {
                            ExcelExporter.exportToExcel(conn, filePath);
                            JOptionPane.showMessageDialog(frame, "Az adatok sikeresen exportálva lettek: " + filePath);
                        } catch (SQLException | IOException ex) {
                            JOptionPane.showMessageDialog(frame, "Hiba történt az exportálás során: " + ex.getMessage());
                        }
                    }
                });
                addButton.addActionListener(e -> {
                    try {
                        insertProduct(conn, tableModel);
                        listProducts(conn, tableModel);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Hiba történt a termék hozzáadása során: " + ex.getMessage());
                    }
                });

                deleteButton.addActionListener(e -> {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int id = (int) tableModel.getValueAt(selectedRow, 0);
                        try {
                            delProductById(conn, id);
                            tableModel.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(frame, "A kiválasztott elem sikeresen törölve.");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(frame, "Hiba történt a törlés során: " + ex.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Kérlek válassz ki egy sort a törléshez!");
                    }
                });

                killAllButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(frame,
                            "Biztosan törölni szeretnéd az összes elemet?",
                            "Megerősítés",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            killAll(conn);
                            tableModel.setRowCount(0); // Táblázat ürítése
                            JOptionPane.showMessageDialog(frame, "Minden elem sikeresen törölve.");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(frame, "Hiba történt az adatok törlése során: " + ex.getMessage());
                        }
                    }
                });

                addRandomButton.addActionListener(e -> {
                    try {
                        insertRandomProducts(conn, 10); // 10 random termék hozzáadása
                        JOptionPane.showMessageDialog(frame, "10 random termék sikeresen hozzáadva.");
                        listProducts(conn, tableModel); // Táblázat frissítése
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Hiba történt a random termékek hozzáadása során: " + ex.getMessage());
                    }
                });

                // Felület elrendezése
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(addButton);
                buttonPanel.add(deleteButton);
                buttonPanel.add(killAllButton);
                buttonPanel.add(addRandomButton);
                buttonPanel.add(exportButton);


                // Szövegek létrehozása és beállítása
                JLabel greenLabel = new JLabel("○ A Zöld elemek lejárt termékek");
                JLabel redLabel = new JLabel("○ A Piros elemek 30 napon belül fognak lejárni");
                JLabel blueLabel = new JLabel("○ A Kék elemek ma járnak le");

                greenLabel.setHorizontalAlignment(SwingConstants.CENTER); // Balra igazítás
                redLabel.setHorizontalAlignment(SwingConstants.CENTER);
                blueLabel.setHorizontalAlignment(SwingConstants.CENTER);

                greenLabel.setForeground(Color.GREEN); // Szöveg zöldre állítása
                redLabel.setForeground(Color.RED);     // Szöveg pirosra állítása
                blueLabel.setForeground(Color.BLUE);   // Szöveg kékre állítása

                // Új panel létrehozása a szövegekhez
                JPanel labelPanel = new JPanel(new GridLayout(3, 1)); // 3 sor, 1 oszlop
                labelPanel.add(greenLabel);
                labelPanel.add(redLabel);
                labelPanel.add(blueLabel);

                // Alsó panel létrehozása (gombok és szövegek)
                JPanel southPanel = new JPanel(new BorderLayout());
                southPanel.add(buttonPanel, BorderLayout.CENTER); // Gombok középen
                southPanel.add(labelPanel, BorderLayout.SOUTH);   // Szövegek alul

                // Fő elrendezés beállítása
                frame.setLayout(new BorderLayout());
                frame.add(scrollPane, BorderLayout.CENTER);       // Táblázat középen
                frame.add(southPanel, BorderLayout.SOUTH);        // Gombok és szövegek alul

                // Ablak megjelenítése
                frame.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static void insertProduct(Connection conn, DefaultTableModel tableModel) throws SQLException {
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField expiryDateField = new JTextField();

        Object[] message = {
                "Termék neve:", nameField,
                "Darabszám:", quantityField,
                "Lejárati dátum (YYYY-MM-DD):", expiryDateField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Új termék hozzáadása", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            int quantity;
            LocalDate expiryDate;

            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Érvénytelen darabszám! Kérlek adj meg egy egész számot.");
                return;
            }

            try {
                expiryDate = LocalDate.parse(expiryDateField.getText().trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Érvénytelen dátum! Kérlek add meg a dátumot YYYY-MM-DD formátumban.");
                return;
            }

            String insertSQL = "INSERT INTO termekek (nev, darabszam, lejarati_datum) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, quantity);
                pstmt.setDate(3, Date.valueOf(expiryDate));

                pstmt.executeUpdate();
                listProducts(conn, tableModel); // Táblázat frissítése

                JOptionPane.showMessageDialog(null, "A termék sikeresen hozzáadva!");
            }
        }
    }

    private static void insertRandomProducts(Connection conn, int count) throws SQLException {
        String[] productNames = {
                "Alma", "Banán", "Narancs", "Tej", "Kenyér", "Sajt", "Csokoládé", "Kávé", "Tea", "Vaj",
                "Paradicsom", "Uborka", "Paprika", "Hagyma", "Fokhagyma", "Répa", "Burgonya", "Brokkoli",
                "Karfiol", "Cukkini", "Padlizsán", "Saláta", "Gomba", "Körte", "Szőlő", "Eper",
                "Málna", "Áfonya", "Citrom", "Lime", "Ananász", "Mangó", "Avokádó",
                "Csirkemell filé", "Sertéskaraj", "Marhahús szelet", "Halfilé",
                "Sonka szeletek", "Kolbász", "Szalámi",
                "Joghurt natúr", "Joghurt gyümölcsös",
                "Tejszín főző", "Tejszín habtejszín",
                "Túró Rudi csomagolt",
                "Pizza (fagyasztott)",
                "Lazac steak",
                "Rántott hús (fagyasztott)",
                "Tészta spagetti",
                "Tészta penne",
                "Rizs basmati",
                "Rizs jázmin",
                // Lidl Deluxe termékek
                "Deluxe Belgiumi Csokoládéválogatás",
                "Deluxe Pisztáciás Panettone",
                "Deluxe Marcipános Stollen Rumos ízesítéssel",
                // Ünnepi termékek
                "Karácsonyi Mézeskalács",
                "Karácsonyi Gyümölcskenyér",
                // Egyéb
                "Olívaolaj extra szűz",
                "Napraforgóolaj",
                // Snackek és édességek
                "Chips sós ízesítésű",
                "Chips paprikás ízesítésű",
                // Italok
                "Narancslé 100%",
                // Gyerektermékek
                // Egyéb szezonális termékek
        };
        Random rand = new Random();

        String insertSQL = "INSERT INTO termekek (nev, darabszam, lejarati_datum) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            for (int i = 0; i < count; i++) {
                String name =
                        productNames[rand.nextInt(productNames.length)];
                int quantity =
                        rand.nextInt(50) + 1;

                LocalDate expiryDate =
                        LocalDate.now().plusDays(rand.nextInt(70));

                pstmt.setString(1,
                        name);
                pstmt.setInt(2,
                        quantity);
                pstmt.setDate(3,
                        Date.valueOf(expiryDate));
                pstmt.executeUpdate();
            }
        }
    }

    private static void listProducts(Connection conn,
                                     DefaultTableModel tableModel)
            throws SQLException {

        String querySQL =
                "SELECT * FROM termekek";

        try (
                Statement stmt =
                        conn.createStatement();

                ResultSet rs =
                        stmt.executeQuery(querySQL)) {

            tableModel.setRowCount(0);

            while (
                    rs.next()) {

                int id =
                        rs.getInt("id");

                String nev =
                        rs.getString("nev");

                int darabszam =
                        rs.getInt("darabszam");

                Date lejaratiDatum =
                        rs.getDate(
                                "lejarati_datum");

                tableModel.addRow(
                        new Object[]{id,
                                nev,
                                darabszam,
                                lejaratiDatum});
            }
        }
    }

    private static void delProductById(Connection conn,
                                       int id)
            throws SQLException {

        String deleteQuery =
                "DELETE FROM termekek WHERE id=?";

        try (
                PreparedStatement pstmt=
                        conn.prepareStatement(deleteQuery)){
            pstmt.setInt(1,id);pstmt.executeUpdate();
        }
    }
    private static void killAll(Connection conn)throws SQLException{
        String deleteQuery="DELETE FROM termekek";
        try(PreparedStatement pstmt=conn.prepareStatement(deleteQuery)){
            pstmt.executeUpdate();
        }
    }
    private static class ExpiryDateCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            // Alapértelmezett megjelenítés
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Ellenőrizzük, hogy a lejárati dátum oszlopról van-e szó
            if (column == 3 && value != null) { // Lejárati dátum oszlop
                try {
                    LocalDate expiryDate = LocalDate.parse(value.toString()); // Dátum konvertálása
                    LocalDate today = LocalDate.now();

                    // Ha 30 napon belül lejár, piros és félkövér szöveg
                    if (expiryDate.isEqual(today)){
                        cellComponent.setForeground(Color.BLUE);
                        cellComponent.setFont(cellComponent.getFont().deriveFont(Font.BOLD));
                    }
                    else if (expiryDate.isBefore(today)) {
                        cellComponent.setForeground(Color.GREEN);
                        cellComponent.setFont(cellComponent.getFont().deriveFont(Font.BOLD));
                    }
                    else if (!expiryDate.isAfter(today.plusDays(30))) {
                        cellComponent.setForeground(Color.RED);
                        cellComponent.setFont(cellComponent.getFont().deriveFont(Font.BOLD));
                    }  else { // Egyébként normál megjelenítés
                        cellComponent.setForeground(Color.BLACK);
                        cellComponent.setFont(cellComponent.getFont().deriveFont(Font.PLAIN));
                    }
                } catch (Exception e) {
                    // Hibás dátumformátum esetén nem csinál semmit
                }
            } else { // Nem lejárati dátum oszlop: normál megjelenítés
                cellComponent.setForeground(Color.BLACK);
                cellComponent.setFont(cellComponent.getFont().deriveFont(Font.PLAIN));
            }
            return cellComponent;
        }
    }
}
