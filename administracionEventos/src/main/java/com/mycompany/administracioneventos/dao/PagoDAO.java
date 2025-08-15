/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.Pago;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagoDAO 
{
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
    
    public boolean registrarPago(Pago pago)
    {
        String sql = "INSERT INTO pago (participante_correo, evento_codigo, metodo, monto) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, pago.getParticipante().getCorreo());
            ps.setString(2, pago.getEvento().getCodigo());
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
}