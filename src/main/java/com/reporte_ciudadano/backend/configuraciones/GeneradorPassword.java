package com.reporte_ciudadano.backend.configuraciones;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;

public class GeneradorPassword {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/App_reportes";
        String user = "postgres";
        String password = "postgres123";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, contrasena FROM usuario_institucional");

            while (resultSet.next()) {
                long usuarioId = resultSet.getLong("id");
                String contrasenaActual = resultSet.getString("contrasena");

                // Verifica si la contraseña ya está encriptada (por si se ejecuta más de una
                // vez)
                if (contrasenaActual != null && !contrasenaActual.startsWith("$2a$")) {
                    String contrasenaCodificada = encoder.encode(contrasenaActual);

                    String updateQuery = "UPDATE usuario_institucional SET contrasena = ? WHERE id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setString(1, contrasenaCodificada);
                        preparedStatement.setLong(2, usuarioId);
                        preparedStatement.executeUpdate();
                        System.out.println("Contraseña encriptada para usuario ID " + usuarioId);
                    }
                } else {
                    System.out.println("Usuario ID " + usuarioId + ": contraseña ya encriptada o vacía.");
                }
            }

            System.out.println("✅ Contraseñas actualizadas correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
