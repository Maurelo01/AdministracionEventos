/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.*;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO 
{
    private boolean existeParticipante(String correoParticipante) // Verifica si existe participante
    {
        String sql = "SELECT 1 FROM participante WHERE correo = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar participante: " + e.getMessage());
            return false;
        }
    }
    
    private boolean existeEvento(String codigoEvento) // Verifica si existe el evento
    {
        String sql = "SELECT 1 FROM evento WHERE codigo = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar evento: " + e.getMessage());
            return false;
        }
    }
    
    public boolean existePago(String correoParticipante, String codigoEvento) // Revisa si existe el pago
    {
        String sql = "SELECT 1 FROM pago WHERE participante_correo = ? AND evento_codigo = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoEvento);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar pago: " + e.getMessage());
            return false;
        }
    }
    
    public boolean existePagoId(int id) // Revisa si existe el pago mediante el id
    {
        String sql = "DELETE FROM pago WHERE id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar pago por id: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registrarPago(Pago pago) // Registra un pago despues de hacer las nuevas verificaciones
    {
        if (pago.getMonto() <= 0)
        {
            System.err.println("El monto del pago debe ser mayor a 0.");
            return false;
        }
        String correoParticipante = pago.getParticipante().getCorreo();
        String codigoEvento = pago.getEvento().getCodigo();
        if (!existeEvento(codigoEvento))
        {
            System.err.println("No existe el evento: " + codigoEvento);
            return false;
        }
        if (!existeParticipante(correoParticipante))
        {
            System.err.println("No existe el participante: " + correoParticipante);
            return false;
        }
        String sql = "INSERT INTO pago (participante_correo, evento_codigo, metodo, monto) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoEvento);
            ps.setString(3, pago.getMetodo().name());
            ps.setDouble(4, pago.getMonto());
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al registrar pago: " + e.getMessage());
            return false;
        }
    }
    
    public List<Pago> listarPagos() // Lista los pagos de forma general
    {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT id, participante_correo, evento_codigo, metodo, monto, fecha_pago FROM pago";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            ParticipanteDAO pDAO = new ParticipanteDAO();
            EventoDAO eDAO = new EventoDAO();
            while (rs.next())
            {
                String correoParticipante = rs.getString("participante_correo");
                String codigoEvento = rs.getString("evento_codigo");
                Participante p = pDAO.buscarParticipante(correoParticipante);
                Evento e = eDAO.buscarEvento(codigoEvento);
                Pago pago = new Pago(p, e, MetodoPago.valueOf(rs.getString("metodo")), rs.getDouble("monto"));
                lista.add(pago);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar pago: " + e.getMessage());
        }
        return lista;
    }
    
    public List<Pago> listarPagosPorEventos(String codigoEvento)  // Lista los pagos dependiendo de los eventos
    {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT participante_correo, metodo, monto, fecha_pago FROM pago WHERE evento_codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            ps.setString(1, codigoEvento);
            ParticipanteDAO pDAO = new ParticipanteDAO();
            Evento e = new EventoDAO().buscarEvento(codigoEvento);
            while (rs.next())
            {
                String correoParticipante = rs.getString("participante_correo");
                Participante p = pDAO.buscarParticipante(correoParticipante);
                Pago pago = new Pago(p, e, MetodoPago.valueOf(rs.getString("metodo")), rs.getDouble("monto"));
                lista.add(pago);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar pago por evento: " + e.getMessage());
        }
        return lista;
    }
    
    public List<Pago> listarPagosPorParticipante(String correoParticipante) // Lista los pagos dependiendo de los participantes
    {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT evento_codigo, metodo, monto, fecha_pago FROM pago WHERE participante_correo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            ps.setString(1, correoParticipante);
            Participante p = new ParticipanteDAO().buscarParticipante(correoParticipante);
            EventoDAO eDAO = new EventoDAO();
            while (rs.next())
            {
                String codigoEvento = rs.getString("evento_codigo");
                Evento e = eDAO.buscarEvento(codigoEvento);
                Pago pago = new Pago(p, e, MetodoPago.valueOf(rs.getString("metodo")), rs.getDouble("monto"));
                lista.add(pago);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar pago por participantes: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean eliminarPagoPorId(int id) // Elimina pagos por medio de id
    {
        String sql = "DELETE FROM pago WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar pago: " + e.getMessage());
            return false;
        }
    }
    
    
}