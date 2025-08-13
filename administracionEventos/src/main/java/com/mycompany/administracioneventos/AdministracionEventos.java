/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.administracioneventos;

import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.TipoEvento;
import java.time.LocalDate;
import java.sql.*;

public class AdministracionEventos {
    // Solo para probar
    public static void main(String[] args) 
    {
        // Evento de prueba
        Evento eventoPrueba = new Evento
        (
                "Evento002",
                LocalDate.of(2025, 8, 16),
                TipoEvento.CHARLA,
                "Una pinche charla",
                "Clase Central",
                50
        );

        // Datos de conexión
        String url = "jdbc:mysql://localhost:3306/administracion_eventos?useSSL=false&serverTimezone=UTC";
        String usuario = "root"; 
        String clave = ""; 

        try (Connection conn = DriverManager.getConnection(url, usuario, clave)) 
        {
            // Inserta el evento
            String insertSQL = "INSERT INTO evento (codigo, fecha, tipo, titulo, ubicacion, cupo_maximo) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                ps.setString(1, eventoPrueba.getCodigo());
                ps.setDate(2, java.sql.Date.valueOf(eventoPrueba.getFecha()));
                ps.setString(3, eventoPrueba.getTipo().name());
                ps.setString(4, eventoPrueba.getTitulo());
                ps.setString(5, eventoPrueba.getUbicacion());
                ps.setInt(6, eventoPrueba.getCupoMaximo());

                int filas = ps.executeUpdate();
                if (filas > 0) 
                {
                    System.out.println("✅ Evento insertado correctamente.");
                }
            } 
            catch (SQLIntegrityConstraintViolationException e) 
            {
                System.out.println("⚠️ Ya existe un evento con el código: " + eventoPrueba.getCodigo());
                mostrarEventoPorCodigo(conn, eventoPrueba.getCodigo());
            }

            // muestra todos los eventos
            mostrarTodosLosEventos(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo para mostrar todos los eventos
    private static void mostrarTodosLosEventos(Connection conn) throws SQLException 
    {
        String selectSQL = "SELECT * FROM evento";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL)) 
        {
            System.out.println("\n=== Lista de todos los eventos ===");
            while (rs.next()) 
            {
                String codigo = rs.getString("codigo");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String tipo = rs.getString("tipo");
                String titulo = rs.getString("titulo");
                String ubicacion = rs.getString("ubicacion");
                int cupo = rs.getInt("cupo_maximo");

                System.out.println(codigo + " | " + fecha + " | " + tipo + " | " + titulo + " | " + ubicacion + " | " + cupo);
            }
        }
    }

    // Método para mostrar un evento específico por su código
    private static void mostrarEventoPorCodigo(Connection conn, String codigoBuscado) throws SQLException {
        String selectSQL = "SELECT * FROM evento WHERE codigo = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
            ps.setString(1, codigoBuscado);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== Evento ya existente con código " + codigoBuscado + " ===");
                if (rs.next()) {
                    String codigo = rs.getString("codigo");
                    LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    String tipo = rs.getString("tipo");
                    String titulo = rs.getString("titulo");
                    String ubicacion = rs.getString("ubicacion");
                    int cupo = rs.getInt("cupo_maximo");

                    System.out.println(codigo + " | " + fecha + " | " + tipo + " | " + titulo + " | " + ubicacion + " | " + cupo);
                } 
                else 
                {
                    System.out.println("No se encontró el evento con ese código.");
                }
            }
        }
    }
}
