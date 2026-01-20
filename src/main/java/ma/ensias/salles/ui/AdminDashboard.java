package ma.ensias.salles.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel loginPanel, userPanel, adminPanel;
    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JComboBox<String> loginRoleBox;
    private JButton loginBtn;
    private String currentUser = null;
    private String currentRole = null;

    // User tab components
    private JTable userSalleTable, userResTable;
    private JButton userRefreshSallesBtn, userReserveBtn, userCancelBtn, userMyResBtn;
    private JTextField userDateField, userStartField, userEndField;
    private JLabel userWelcomeLabel;

    // Admin tab components
    private JTable adminSalleTable, adminResTable;
    private JButton adminRefreshSallesBtn, adminAddSalleBtn, adminDeleteSalleBtn, adminUpdateSalleBtn, adminRefreshResBtn;
    private JTextField adminTypeField, adminCapaciteField, adminEquipField, adminSalleIdField;
    private JLabel adminWelcomeLabel;
    private JTextArea notificationArea;
    private Thread notificationThread;
    private volatile boolean running = true;

    public AdminDashboard() {
        setTitle("Système de Réservation de Salles - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        buildLoginPanel();
        buildUserPanel();
        buildAdminPanel();

        tabbedPane.addTab("Connexion", loginPanel);
        tabbedPane.addTab("Utilisateur", userPanel);
        tabbedPane.addTab("Admin", adminPanel);

        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);

        add(tabbedPane);
    }

    private void buildLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        loginUserField = new JTextField(15);
        loginPassField = new JPasswordField(15);
        loginRoleBox = new JComboBox<>(new String[]{"user", "admin"});
        loginBtn = new JButton("Se connecter");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; loginPanel.add(new JLabel("Nom d'utilisateur:"), gbc);
        gbc.gridx = 1; loginPanel.add(loginUserField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; loginPanel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1; loginPanel.add(loginPassField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; loginPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1; loginPanel.add(loginRoleBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; loginPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> doLogin());
    }

    private void buildUserPanel() {
        userPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userWelcomeLabel = new JLabel("Bienvenue, utilisateur");
        topPanel.add(userWelcomeLabel);

        userRefreshSallesBtn = new JButton("Rafraîchir Salles");
        userMyResBtn = new JButton("Mes Réservations");
        topPanel.add(userRefreshSallesBtn);
        topPanel.add(userMyResBtn);

        userSalleTable = new JTable(new DefaultTableModel(new Object[]{"ID", "Type", "Capacité", "Équipements"}, 0));
        JScrollPane salleScroll = new JScrollPane(userSalleTable);

        JPanel reservePanel = new JPanel(new FlowLayout());
        userDateField = new JTextField("2026-01-20", 10);
        userStartField = new JTextField("10:00", 5);
        userEndField = new JTextField("11:00", 5);
        userReserveBtn = new JButton("Réserver");
        reservePanel.add(new JLabel("Date:"));
        reservePanel.add(userDateField);
        reservePanel.add(new JLabel("Début:"));
        reservePanel.add(userStartField);
        reservePanel.add(new JLabel("Fin:"));
        reservePanel.add(userEndField);
        reservePanel.add(userReserveBtn);

        userResTable = new JTable(new DefaultTableModel(new Object[]{"ID", "Salle", "Date", "Début", "Fin"}, 0));
        JScrollPane resScroll = new JScrollPane(userResTable);
        userCancelBtn = new JButton("Annuler Réservation");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resScroll, BorderLayout.CENTER);
        bottomPanel.add(userCancelBtn, BorderLayout.SOUTH);

        userPanel.add(topPanel, BorderLayout.NORTH);
        userPanel.add(salleScroll, BorderLayout.CENTER);
        userPanel.add(reservePanel, BorderLayout.SOUTH);
        userPanel.add(bottomPanel, BorderLayout.EAST);

        userRefreshSallesBtn.addActionListener(e -> loadUserSalles());
        userReserveBtn.addActionListener(e -> reserveSalle());
        userMyResBtn.addActionListener(e -> loadUserReservations());
        userCancelBtn.addActionListener(e -> cancelReservation());
    }

    private void buildAdminPanel() {
        adminPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminWelcomeLabel = new JLabel("Bienvenue, admin");
        topPanel.add(adminWelcomeLabel);

        adminRefreshSallesBtn = new JButton("Rafraîchir Salles");
        adminRefreshResBtn = new JButton("Rafraîchir Réservations");
        topPanel.add(adminRefreshSallesBtn);
        topPanel.add(adminRefreshResBtn);

        adminSalleTable = new JTable(new DefaultTableModel(new Object[]{"ID", "Type", "Capacité", "Équipements"}, 0));
        JScrollPane salleScroll = new JScrollPane(adminSalleTable);

        JPanel crudPanel = new JPanel(new FlowLayout());
        adminTypeField = new JTextField(8);
        adminCapaciteField = new JTextField(4);
        adminEquipField = new JTextField(10);
        adminSalleIdField = new JTextField(4);
        adminAddSalleBtn = new JButton("Ajouter Salle");
        adminDeleteSalleBtn = new JButton("Supprimer Salle");
        adminUpdateSalleBtn = new JButton("Modifier Salle");
        crudPanel.add(new JLabel("Type:"));
        crudPanel.add(adminTypeField);
        crudPanel.add(new JLabel("Capacité:"));
        crudPanel.add(adminCapaciteField);
        crudPanel.add(new JLabel("Équipements:"));
        crudPanel.add(adminEquipField);
        crudPanel.add(adminAddSalleBtn);
        crudPanel.add(new JLabel("ID:"));
        crudPanel.add(adminSalleIdField);
        crudPanel.add(adminDeleteSalleBtn);
        crudPanel.add(adminUpdateSalleBtn);

        adminResTable = new JTable(new DefaultTableModel(new Object[]{"ID", "Utilisateur", "Salle", "Date", "Début", "Fin"}, 0));
        JScrollPane resScroll = new JScrollPane(adminResTable);

        adminPanel.add(topPanel, BorderLayout.NORTH);
        adminPanel.add(salleScroll, BorderLayout.CENTER);
        adminPanel.add(crudPanel, BorderLayout.SOUTH);
        adminPanel.add(resScroll, BorderLayout.EAST);

        // Notification area at the bottom
        notificationArea = new JTextArea(4, 50);
        notificationArea.setEditable(false);
        notificationArea.setBorder(BorderFactory.createTitledBorder("Notifications temps réel (TCP)"));
        add(notificationArea, BorderLayout.SOUTH);

        adminRefreshSallesBtn.addActionListener(e -> loadAdminSalles());
        adminAddSalleBtn.addActionListener(e -> addSalle());
        adminDeleteSalleBtn.addActionListener(e -> deleteSalle());
        adminUpdateSalleBtn.addActionListener(e -> updateSalle());
        adminRefreshResBtn.addActionListener(e -> loadAdminReservations());

        // Start notification listener thread
        startNotificationListener();

        // Stop thread on window close
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                running = false;
                if (notificationThread != null) notificationThread.interrupt();
            }
        });
    }

    // --- Login logic ---
    private void doLogin() {
        String username = loginUserField.getText();
        String password = new String(loginPassField.getPassword());
        String role = (String) loginRoleBox.getSelectedItem();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }
        try {
            // REST: POST /api/login (simulate with /api/users/authenticate or similar)
            String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            // For demo, just check if user exists (adapt endpoint as needed)
            String url = "http://localhost:8081/api/users/authenticate";
            String resp = httpPost(url, json);
            if (resp.contains("true")) {
                currentUser = username;
                currentRole = role;
                if ("user".equals(role)) {
                    userWelcomeLabel.setText("Bienvenue, " + username);
                    tabbedPane.setEnabledAt(1, true);
                    tabbedPane.setSelectedIndex(1);
                    loadUserSalles();
                } else {
                    adminWelcomeLabel.setText("Bienvenue, " + username);
                    tabbedPane.setEnabledAt(2, true);
                    tabbedPane.setSelectedIndex(2);
                    loadAdminSalles();
                    loadAdminReservations();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Échec de l'authentification.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion : " + ex.getMessage());
        }
    }

    // --- User tab logic ---
    private void loadUserSalles() {
        DefaultTableModel model = (DefaultTableModel) userSalleTable.getModel();
        model.setRowCount(0);
        try {
            String resp = httpGet("http://localhost:8081/api/admin/salles");
            for (String s : resp.split("\\{")) {
                if (s.contains("id") && s.contains("type")) {
                    int id = parseIntField(s, "id");
                    String type = parseStringField(s, "type");
                    int cap = parseIntField(s, "capacite");
                    String eq = parseStringField(s, "equipements");
                    model.addRow(new Object[]{id, type, cap, eq});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement salles: " + e.getMessage());
        }
    }

    private void reserveSalle() {
        int row = userSalleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une salle.");
            return;
        }
        int salleId = (int) userSalleTable.getValueAt(row, 0);
        String date = userDateField.getText();
        String debut = userStartField.getText();
        String fin = userEndField.getText();
        String json = String.format("{\"utilisateur\":\"%s\",\"salleId\":%d,\"date\":\"%s\",\"heureDebut\":\"%s\",\"heureFin\":\"%s\"}",
                currentUser, salleId, date, debut, fin);
        try {
            String resp = httpPost("http://localhost:8081/api/reservations", json);
            JOptionPane.showMessageDialog(this, "Réponse: " + resp);
            loadUserReservations();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur réservation: " + e.getMessage());
        }
    }

    private void loadUserReservations() {
        DefaultTableModel model = (DefaultTableModel) userResTable.getModel();
        model.setRowCount(0);
        try {
            String resp = httpGet("http://localhost:8081/api/reservations/user/" + currentUser);
            for (String s : resp.split("\\{")) {
                if (s.contains("id") && s.contains("salleId")) {
                    int id = parseIntField(s, "id");
                    int salleId = parseIntField(s, "salleId");
                    String date = parseStringField(s, "date");
                    String debut = parseStringField(s, "heureDebut");
                    String fin = parseStringField(s, "heureFin");
                    model.addRow(new Object[]{id, salleId, date, debut, fin});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement réservations: " + e.getMessage());
        }
    }

    private void cancelReservation() {
        int row = userResTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une réservation à annuler.");
            return;
        }
        int resId = (int) userResTable.getValueAt(row, 0);
        try {
            String resp = httpDelete("http://localhost:8081/api/admin/reservations/" + resId);
            JOptionPane.showMessageDialog(this, "Réponse: " + resp);
            loadUserReservations();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur annulation: " + e.getMessage());
        }
    }

    // --- Admin tab logic ---
    private void loadAdminSalles() {
        DefaultTableModel model = (DefaultTableModel) adminSalleTable.getModel();
        model.setRowCount(0);
        try {
            String resp = httpGet("http://localhost:8081/api/admin/salles");
            for (String s : resp.split("\\{")) {
                if (s.contains("id") && s.contains("type")) {
                    int id = parseIntField(s, "id");
                    String type = parseStringField(s, "type");
                    int cap = parseIntField(s, "capacite");
                    String eq = parseStringField(s, "equipements");
                    model.addRow(new Object[]{id, type, cap, eq});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement salles: " + e.getMessage());
        }
    }

    private void addSalle() {
        String type = adminTypeField.getText();
        String cap = adminCapaciteField.getText();
        String eq = adminEquipField.getText();
        if (type.isEmpty() || cap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type et capacité requis.");
            return;
        }
        String json = String.format("{\"type\":\"%s\",\"capacite\":%s,\"equipements\":\"%s\"}", type, cap, eq);
        try {
            String resp = httpPost("http://localhost:8081/api/admin/salles", json);
            JOptionPane.showMessageDialog(this, "Réponse: " + resp);
            loadAdminSalles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur ajout salle: " + e.getMessage());
        }
    }

    private void deleteSalle() {
        String id = adminSalleIdField.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Entrez l'ID de la salle à supprimer.");
            return;
        }
        try {
            String resp = httpDelete("http://localhost:8081/api/admin/salles/" + id);
            JOptionPane.showMessageDialog(this, "Réponse: " + resp);
            loadAdminSalles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur suppression salle: " + e.getMessage());
        }
    }

    private void updateSalle() {
        String id = adminSalleIdField.getText();
        String type = adminTypeField.getText();
        String cap = adminCapaciteField.getText();
        String eq = adminEquipField.getText();
        if (id.isEmpty() || type.isEmpty() || cap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, type et capacité requis.");
            return;
        }
        String json = String.format("{\"type\":\"%s\",\"capacite\":%s,\"equipements\":\"%s\"}", type, cap, eq);
        try {
            String resp = httpPut("http://localhost:8081/api/admin/salles/" + id, json);
            JOptionPane.showMessageDialog(this, "Réponse: " + resp);
            loadAdminSalles();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur modification salle: " + e.getMessage());
        }
    }

    private void loadAdminReservations() {
        DefaultTableModel model = (DefaultTableModel) adminResTable.getModel();
        model.setRowCount(0);
        try {
            String resp = httpGet("http://localhost:8081/api/admin/reservations");
            for (String s : resp.split("\\{")) {
                if (s.contains("id") && s.contains("salleId")) {
                    int id = parseIntField(s, "id");
                    String user = parseStringField(s, "utilisateur");
                    int salleId = parseIntField(s, "salleId");
                    String date = parseStringField(s, "date");
                    String debut = parseStringField(s, "heureDebut");
                    String fin = parseStringField(s, "heureFin");
                    model.addRow(new Object[]{id, user, salleId, date, debut, fin});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement réservations: " + e.getMessage());
        }
    }

    // --- HTTP helpers ---
    private String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        int code = con.getResponseCode();
        InputStream in = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    private String httpPost(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = con.getResponseCode();
        InputStream in = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    private String httpDelete(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        int code = con.getResponseCode();
        InputStream in = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    private String httpPut(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = con.getResponseCode();
        InputStream in = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    // --- Simple JSON parsing helpers ---
    private int parseIntField(String json, String field) {
        String pat = '"' + field + '"' + ":";
        int idx = json.indexOf(pat);
        if (idx == -1) return -1;
        int start = idx + pat.length();
        int end = json.indexOf(',', start);
        if (end == -1) end = json.indexOf('}', start);
        return Integer.parseInt(json.substring(start, end).replaceAll("[^0-9]", ""));
    }
    private String parseStringField(String json, String field) {
        String pat = '"' + field + '"' + ":";
        int idx = json.indexOf(pat);
        if (idx == -1) return "";
        int start = json.indexOf('"', idx + pat.length()) + 1;
        int end = json.indexOf('"', start);
        return json.substring(start, end);
    }

    private void startNotificationListener() {
        notificationThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while (running && (line = in.readLine()) != null) {
                    final String msg = line;
                    SwingUtilities.invokeLater(() -> {
                        notificationArea.append(msg + "\n");
                        notificationArea.setCaretPosition(notificationArea.getDocument().getLength());
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> notificationArea.append("Connexion au serveur de notifications perdue.\n"));
            }
        });
        notificationThread.setDaemon(true);
        notificationThread.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
