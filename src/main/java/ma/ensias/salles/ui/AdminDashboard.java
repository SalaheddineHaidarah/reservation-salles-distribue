package ma.ensias.salles.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AdminDashboard extends JFrame {
    private JButton listReservationsBtn;
    private JButton addSalleBtn;
    private JButton deleteSalleBtn;
    private JTextField typeField;
    private JTextField capaciteField;
    private JTextField equipementsField;
    private JTextField deleteIdField;
    private JTextArea outputArea;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        typeField = new JTextField();
        capaciteField = new JTextField();
        equipementsField = new JTextField();
        deleteIdField = new JTextField();
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeField);
        formPanel.add(new JLabel("Capacite:"));
        formPanel.add(capaciteField);
        formPanel.add(new JLabel("Equipements:"));
        formPanel.add(equipementsField);
        formPanel.add(new JLabel("Delete salle id:"));
        formPanel.add(deleteIdField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        listReservationsBtn = new JButton("List all reservations");
        addSalleBtn = new JButton("Add salle");
        deleteSalleBtn = new JButton("Delete salle by id");
        buttonPanel.add(listReservationsBtn);
        buttonPanel.add(addSalleBtn);
        buttonPanel.add(deleteSalleBtn);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        listReservationsBtn.addActionListener(this::listReservations);
        addSalleBtn.addActionListener(this::addSalle);
        deleteSalleBtn.addActionListener(this::deleteSalle);
    }

    private void listReservations(ActionEvent e) {
        try {
            String resp = httpGet("http://localhost:8081/api/admin/reservations");
            outputArea.setText(resp);
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void addSalle(ActionEvent e) {
        String type = typeField.getText();
        String capacite = capaciteField.getText();
        String equipements = equipementsField.getText();
        if (type.isEmpty() || capacite.isEmpty()) {
            outputArea.setText("Type and capacite required.");
            return;
        }
        String json = String.format("{\"type\":\"%s\",\"capacite\":%s,\"equipements\":\"%s\"}",
                type, capacite, equipements);
        try {
            String resp = httpPost("http://localhost:8081/api/admin/salles", json);
            outputArea.setText("Add salle response: " + resp);
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void deleteSalle(ActionEvent e) {
        String id = deleteIdField.getText();
        if (id.isEmpty()) {
            outputArea.setText("Enter salle id to delete.");
            return;
        }
        try {
            String resp = httpDelete("http://localhost:8081/api/admin/salles/" + id);
            outputArea.setText("Delete salle response: " + resp);
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
