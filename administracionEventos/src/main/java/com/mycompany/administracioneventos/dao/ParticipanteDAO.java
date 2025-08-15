/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.Participante;
import com.mycompany.administracioneventos.modelos.TipoParticipante;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteDAO 
{
    public boolean existeCorreo(String correo) // Verificar si existe un correo
    {
        String sql = "SELECT 1 FROM participante WHERE correo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar participante: " + e.getMessage());
            return false;
        }
    }
    
    private boolean validarDatos(Participante participante) // Validar datos antes de insertar o actualizar
    {
        if (participante.getCorreo() == null || participante.getCorreo().isEmpty())
        {
            System.err.println("El correo no puede estar vacío.");
            return false;
        }
        if (!participante.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
        {
            System.err.println("Formato de correo inválido.");
            return false;
        }
        if (participante.getNombreCompleto() == null || participante.getNombreCompleto().length() > 45) 
        {
            System.err.println("El nombre completo no puede superar los 45 caracteres.");
            return false;
        }
        if (participante.getInstitucion() != null && participante.getInstitucion().length() > 150)
        {
            System.err.println("La institución no puede superar los 150 caracteres.");
            return false;
        }
        return true;
    }
    
    public boolean agregarParticipante(Participante participante) // Crear un nuevo participante 
    {
        if (!validarDatos(participante))
        {
            return false;
        }
        if (existeCorreo(participante.getCorreo()))
        {
            System.err.println("Ya existe un participante con el correo: " + participante.getCorreo());
            return false;
        }
        String sql = "INSERT INTO participante (correo, nombre_completo, tipo, institucion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, participante.getCorreo());
            stmt.setString(2, participante.getNombreCompleto());
            stmt.setString(3, participante.getTipo().name());
            stmt.setString(4, participante.getInstitucion());
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al agregar participante: " + e.getMessage());
            return false;
        }
    }
    
    public List<Participante> listarParticipantes() // Listar todos los participantes
    {
        List<Participante> lista = new ArrayList<>();
        String sql = "SELECT * FROM participante";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Participante participante = new Participante
                        (
                                rs.getString("correo"),
                                rs.getString("nombre_completo"),
                                TipoParticipante.valueOf(rs.getString("tipo")),
                                rs.getString("institucion")
                        );
                lista.add(participante);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar participantes: " + e.getMessage());
        }
        return lista;
    }
    
    public Participante buscarParticipante(String correo) // BUsca al participante por su correo
    {
        String sql = "SELECT * FROM participante WHERE correo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                return new Participante
                        (
                                rs.getString("correo"),
                                rs.getString("nombre_completo"),
                                TipoParticipante.valueOf(rs.getString("tipo")),
                                rs.getString("institucion")
                        );
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al buscar participante: " + e.getMessage());
        }
        return null;
    }
    
    public boolean actualizarParticipante(Participante participante) // Actualiza el participante
    {
        if (!validarDatos(participante))
        {
            return false;
        }
        String sql = "UPDATE participante SET nombre_completo=?, tipo=?, institucion=? WHERE correo=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, participante.getNombreCompleto());  
            stmt.setString(2, participante.getTipo().name());
            stmt.setString(3, participante.getInstitucion());
            stmt.setString(4, participante.getCorreo());
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al actualizar participante: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarParticipante(String correo) // Eliminar participante
    {
        String sql = "DELETE FROM participante WHERE correo=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, correo);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar participante: " + e.getMessage());
            return false;
        }
    }
}
