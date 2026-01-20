package ma.ensias.salles.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class UserDashboard extends JFrame {
    private JTextField usernameField;
    private JComboBox<SalleItem> salleCombo;
    private JTextField dateField;
    private JTextField startField;
    private JTextField endField;
    private JButton checkButton;
    private JButton reserveButton;
    private JButton myResButton;
    private JTextArea resultArea;

    public UserDashboard() {
        setTitle("Room Reservation Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        usernameField = new JTextField();
        salleCombo = new JComboBox<>();
        dateField = new JTextField("2026-01-20");
        startField = new JTextField("10:00");
        endField = new JTextField("11:00");
        checkButton = new JButton("Check availability");
        reserveButton = new JButton("Reserve");
        myResButton = new JButton("My reservations");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(resultArea);

        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Salle:"));
        form.add(salleCombo);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Start time (HH:mm):"));
        form.add(startField);
        form.add(new JLabel("End time (HH:mm):"));
        form.add(endField);
        form.add(checkButton);
        form.add(reserveButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(myResButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Populate salles
        fetchSalles();

        checkButton.addActionListener(e -> checkAvailability());
        reserveButton.addActionListener(e -> reserveSalle());
        myResButton.addActionListener(e -> fetchMyReservations());
    }

    private void fetchSalles() {
        salleCombo.removeAllItems();
        try {
            String resp = httpGet("http://localhost:8081/api/admin/salles");
            // Expecting JSON array: [{"id":1,"type":"moyenne",...}, ...]
            // We'll parse manually for id and type
            for (String s : resp.split("\\{")) {
                if (s.contains("id") && s.contains("type")) {
                    int id = parseIntField(s, "id");
                    String type = parseStringField(s, "type");
                    salleCombo.addItem(new SalleItem(id, type));
                }
            }
        } catch (Exception e) {
            resultArea.setText("Failed to fetch salles: " + e.getMessage());
        }
    }

    private void checkAvailability() {
        SalleItem salle = (SalleItem) salleCombo.getSelectedItem();
        if (salle == null) {
            resultArea.setText("Select a salle.");
            return;
        }
        String url = String.format("http://localhost:8081/api/salles/disponible?id=%d&date=%s&debut=%s&fin=%s",
                salle.id, dateField.getText(), startField.getText(), endField.getText());
        try {
            String resp = httpGet(url);
            resultArea.setText("Available: " + resp.trim());
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private void reserveSalle() {
        SalleItem salle = (SalleItem) salleCombo.getSelectedItem();
        if (salle == null) {
            resultArea.setText("Select a salle.");
            return;
        }
        String json = String.format("{\"utilisateur\":\"%s\",\"salleId\":%d,\"date\":\"%s\",\"heureDebut\":\"%s\",\"heureFin\":\"%s\"}",
                usernameField.getText(), salle.id, dateField.getText(), startField.getText(), endField.getText());
        try {
            String resp = httpPost("http://localhost:8081/api/reservations", json);
            resultArea.setText("Reservation response: " + resp);
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private void fetchMyReservations() {
        String user = usernameField.getText();
        if (user.isEmpty()) {
            resultArea.setText("Enter username.");
            return;
        }
        String url = "http://localhost:8081/api/reservations/user/" + user;
        try {
            String resp = httpGet(url);
            resultArea.setText("My reservations:\n" + resp);
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

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

    // Simple JSON parsing helpers (not robust, but enough for demo)
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

    // Salle item for combo box
    private static class SalleItem {
        int id;
        String type;
        SalleItem(int id, String type) { this.id = id; this.type = type; }
        public String toString() { return id + " - " + type; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard().setVisible(true));
    }
}
