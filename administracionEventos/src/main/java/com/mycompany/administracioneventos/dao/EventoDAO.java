/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.TipoEvento;
import com.mycompany.administracioneventos.util.DBConnection;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EventoDAO 
{
    public boolean agregarEvento(Evento evento) throws SQLException // Se encarga de crear un nuevo evento
    {
        if (evento.getCupoMaximo() <= 0)
        {
            System.err.println("El cupo máximo debe ser mayor a 0.");
            return false;
        }
        
        if (existeEvento(evento.getCodigo())) 
        {
            System.err.println("Ya existe un evento con el codigo: " + evento.getCodigo());
            return false;
        }
        
        String sql = "INSERT INTO evento (codigo, fecha, tipo, titulo, ubicacion, cupo_maximo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, evento.getCodigo());
            stmt.setDate(2, Date.valueOf(evento.getFecha()));
            stmt.setString(3, evento.getTipo().name());
            stmt.setString(4, evento.getTitulo());
            stmt.setString(5, evento.getUbicacion());
            stmt.setInt(6, evento.getCupoMaximo());
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al agregar evento: " + e.getMessage());
            return false;
        }
    }
    
    public boolean existeEvento(String codigo)
    {
        String sql = "SELECT 1 FROM evento WHERE codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar evento: " + e.getMessage());
            return false;
        }
    }
    public List<Evento> listarEventos() // Se encarga de listar los eventos
    {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM evento";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                 Evento evento = new Evento
                        (
                                rs.getString("codigo"),
                                rs.getDate("fecha").toLocalDate(),
                                TipoEvento.valueOf(rs.getString("tipo")),
                                rs.getString("titulo"),
                                rs.getString("ubicacion"),
                                rs.getInt("cupo_maximo")
                        );
                 lista.add(evento);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar eventos: " + e.getMessage());
        }
        return lista;
    }
    
    public Evento buscarEvento(String codigo) // Se encarga de buscar un evento por su codigoS
    {
        String sql = "SELECT * FROM evento WHERE codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
             stmt.setString(1, codigo);
             ResultSet rs = stmt.executeQuery();
             if (rs.next())
             {
                 return new Evento
                        (
                            rs.getString("codigo"),
                            rs.getDate("fecha").toLocalDate(),
                            TipoEvento.valueOf(rs.getString("tipo")),
                            rs.getString("titulo"),
                            rs.getString("ubicacion"),
                            rs.getInt("cupo_maximo")   
                        );
             }
        }
        catch (SQLException e)
        {
            System.err.println("Error al buscar evento: " + e.getMessage());
        }
        return null;
    }
    
    public boolean actualizarEvento(Evento evento) // Se encarga de actualizar los eventos
    {
        if (evento.getCupoMaximo() <= 0)
        {
            System.err.println("El cupo máximo debe ser mayor a 0.");
            return false;
        }
        String sql = "UPDATE evento SET fecha=?, tipo=?, titulo=?, ubicacion=?, cupo_maximo=? WHERE codigo=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setDate(1, Date.valueOf(evento.getFecha()));
            stmt.setString(2, evento.getTipo().name());
            stmt.setString(3, evento.getTitulo());
            stmt.setString(4, evento.getUbicacion());
            stmt.setInt(5, evento.getCupoMaximo());
            stmt.setString(6, evento.getCodigo());
            
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al actualizar evento: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarEvento(String codigo)
    {
        String sql = "DELETE FROM evento WHERE codigo=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, codigo);
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar evento: " + e.getMessage());
            return false;   
        }
    }
    
    public ResultadoOperacion eliminarEventoSeguro(String codigo)
    {
        String qActividad = "SELECT COUNT(*) FROM actividad WHERE evento_codigo=?";
        String qInscripcion = "SELECT COUNT(*) FROM inscripcion WHERE evento_codigo=?";
        String qPago = "SELECT COUNT(*) FROM pago WHERE evento_codigo=?";
        String qCertificado = "SELECT COUNT(*) FROM certificado WHERE evento_codigo=?";
        try (Connection conn = DBConnection.getConnection())
        {
            int actividad = contar(conn, qActividad, codigo);
            int inscripcion = contar(conn, qInscripcion, codigo);
            int pago = contar(conn, qPago, codigo);
            int certificado = contar(conn, qCertificado, codigo);

            if (actividad + inscripcion + pago + certificado > 0)
            {
                return ResultadoOperacion.fallo(String.format(
                    "No se puede eliminar el evento %s: actividades=%d, inscripciones=%d, pagos=%d, certificados=%d. " +
                    "Elimina primero los registros dependientes.",
                    codigo, actividad, inscripcion, pago, certificado));
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM evento WHERE codigo=?"))
            {
                ps.setString(1, codigo);
                int filas = ps.executeUpdate();
                return filas > 0 ? ResultadoOperacion.ok("Evento eliminado.")
                                 : ResultadoOperacion.fallo("Evento no encontrado.");
            }
        }
        catch (SQLException e)
        {
            return ResultadoOperacion.fallo("Error al eliminar evento: " + e.getMessage());
        }
    }
    
    private int contar(Connection conn,String sql, String parametro) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery())
            {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
