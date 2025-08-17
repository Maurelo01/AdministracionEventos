/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.Certificado;
import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.Participante;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificadoDAO 
{
    public boolean existeCertificado(String correoParticipante, String codigoEvento) // Validacion de existencia de certificado
    {
        String sql = "SELECT 1 FROM certificado WHERE participante_correo = ? AND evento_codigo = ? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoEvento);
            try(ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar certificado: " + e.getMessage());
            return false;
        }
    }
    
    public boolean asistioAlEvento(String correoParticipante, String codigoEvento) // Comprueba si el participante asistió 
    {
        String sql = "SELECT 1 FROM asistencia a JOIN actividad ac ON ac.codigo = a.actividad_codigo WHERE a.participante_correo = ? AND ac.evento_codigo = ? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoEvento);
            try(ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar asistencia para el certificado: " + e.getMessage());
            return false;
        }
    }
    
    public boolean inscripcionValidada(String correoParticipante, String codigoEvento) // Verifica si la inscripcion esta validada
    {
       String sql = "SELECT validada FROM inscripcion WHERE participante_correo = ? AND evento_codigo = ?";
       try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
       {
           ps.setString(1, correoParticipante);
           ps.setString(2, codigoEvento);
           try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) return rs.getBoolean("validada");
            }
       }
       catch (SQLException e)
        {
            System.err.println("Error al verificar inscripción validada: " + e.getMessage());
        }
        return false;
    }
    
    public boolean insertarCertificado(Participante participante, Evento evento, String rutaArchivo) // Inserta un certificado
    {
        String sql = "INSERT INTO certificado (participante_correo, evento_codigo, fecha_emision, ruta_archivo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
       {
           ps.setString(1, participante.getCorreo());
           ps.setString(2, evento.getCodigo());
           ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
           ps.setString(4, rutaArchivo);
           return ps.executeUpdate()> 0;
       }
        catch (SQLException ex)
        {
            System.err.println("[CertificadoDAO] SQLState=" + ex.getSQLState() + " Code=" + ex.getErrorCode());
            System.err.println("Error al insertar certificado: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    public Certificado buscarCertificado(String correoParticipante, String codigoEvento) // Realiza una busqueda del certificado
    {
        String sql = "SELECT fecha_emision, ruta_archivo FROM certificado WHERE participante_correo = ? AND evento_codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoEvento);
            try (ResultSet rs = ps.executeQuery())
             {
                 if (rs.next())
                 {
                     Participante participante = new ParticipanteDAO().buscarParticipante(correoParticipante);
                     Evento evento = new EventoDAO().buscarEvento(codigoEvento);
                     if(participante == null || evento == null) return null;
                     Certificado certificado = new Certificado(participante, evento, rs.getString("ruta_archivo"));
                     certificado.setFechaEmision(rs.getTimestamp("fecha_emision").toLocalDateTime());
                     return certificado;
                 }
             }
        }
        catch (SQLException e)
        {
            System.err.println("Error al buscar certificado: " + e.getMessage());
        }
        return null;
    }
    
    public List<Certificado> listarCertificados() // Lista los certificados
    {
        List<Certificado> lista = new ArrayList<>();
        String sql = "SELECT participante_correo, evento_codigo, fecha_emision, ruta_archivo FROM certificado";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            ParticipanteDAO participanteDAO = new ParticipanteDAO();
            EventoDAO eventoDAO = new EventoDAO();
            while (rs.next())
            {
                String correoParticipante = rs.getString("participante_correo");
                String codigoEvento = rs.getString("evento_codigo");
                Participante participante = participanteDAO.buscarParticipante(correoParticipante);
                Evento evento = eventoDAO.buscarEvento(codigoEvento);
                if (participante == null || evento == null) continue;
                Certificado certificado = new Certificado(participante, evento, rs.getString("ruta_archivo"));
                certificado.setFechaEmision(rs.getTimestamp("fecha_emision").toLocalDateTime());
                lista.add(certificado);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar certificados: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean eliminarCertificado(String correoParticipante, String codigoEvento) // Elimina un certificado
    {
        String sql = "DELETE FROM certificado WHERE participante_correo = ? AND evento_codigo = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoEvento);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar certificado: " + e.getMessage());
            return false;
        }
    }
}
