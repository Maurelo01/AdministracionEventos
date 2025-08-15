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

public class InscripcionDAO 
{
    private boolean existeParticipante(String correo) // Verifica si existe participante
    {
        // Validaciones en la base de datos DB
        String sql = "SELECT 1 FROM participante WHERE correo = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correo);
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
    
    private boolean existeEvento(String codigo) // Verifica si ya existe el evento
    {
        String sql = "SELECT 1 FROM evento WHERE codigo = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))        
        {
            ps.setString(1, codigo);
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
    
    private boolean existeInscripcion(String correo, String codigoEvento) // Verifica si ya esta inscrito
    {
        String sql = "SELECT 1 FROM inscripcion WHERE participante_correo = ? AND evento_codigo = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correo);
            ps.setString(2, codigoEvento);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar inscripcion: " + e.getMessage());
            return false;
        }
    }
    
    private int obtenerCupoMaximo(String codigoEvento) // Obtiene el cupo maximo del evento
    {
        String sql = "SELECT cupo_maximo FROM evento WHERE codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) return rs.getInt("cupo_maximo");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al obtener el cupo maximo: " + e.getMessage());
        }
        return 0;
    }
    
    private int contarInscritosEvento(String codigoEvento) // Cuenta a los inscritos en el evento
    {
        String sql = "SELECT COUNT(*) AS total FROM inscripcion WHERE evento_codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) return rs.getInt("total");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al contar inscritos: " + e.getMessage());
        }
        return Integer.MAX_VALUE;
    }
    
    private boolean yaValidada(String correo, String evento) // Verifica si la inscripcion es valida
    {
       String sql = "SELECT validada FROM inscripcion WHERE participante_correo=? AND evento_codigo=?";
       try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
       {
           ps.setString(1, correo);
           ps.setString(2, evento);
           try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) return rs.getBoolean("validada");
            }
       }
       catch (SQLException e)
        {
            System.err.println("Error al consultar estado de validacion: " + e.getMessage());
        }
        return false;
    }
    
    // CRUD
    public boolean agregarInscripcion(Inscripcion insc) // Agrega la inscripcion y hace las verificaciones
    {
        String correo = insc.getParticipante().getCorreo();
        String codEvento = insc.getEvento().getCodigo();
        if (!existeParticipante(correo))
        {
            System.err.println("No existe el participante: " + correo);
            return false;
        }
        if (!existeEvento(codEvento))
        {
            System.err.println("No existe el evento: " + codEvento);
            return false;
        }
        if (existeInscripcion(correo, codEvento))
        {
            System.err.println("Inscripción duplicada para " + correo + " en evento " + codEvento);
            return false;
        }
        
        int cupo = obtenerCupoMaximo(codEvento);
        int inscritos = contarInscritosEvento(codEvento);
        if (inscritos >= cupo) 
        {
            System.err.println("Cupo lleno para el evento " + codEvento + " (cupo=" + cupo + ")");
            return false;
        }
        String sql = "INSERT INTO inscripcion (participante_correo, evento_codigo, tipo, validada) VALUES (?,?,?,false)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
           ps.setString(1, correo);
           ps.setString(2, codEvento);
           ps.setString(3, insc.getTipo().name());
           return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al agregar inscripcion: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validarInscripcion(String correo, String codEvento) // Valida la inscripcion y hace las verificaciones
    {
        if (!existeInscripcion(correo, codEvento))
        {
            System.err.println("No existe la inscripcion para validarla");
            return false;
        }
        if (yaValidada(correo, codEvento))
        {
            System.err.println("La inscripción ya estaba validada.");
            return false;
        }
        PagoDAO pagoDAO = new PagoDAO();
        if (!pagoDAO.existePago(correo, codEvento))
        {
             System.err.println("No se puede validar: no hay pago registrado.");
             return false;
        }
        String sql = "UPDATE inscripcion SET validada = true WHERE participante_correo = ? AND evento_codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correo);
            ps.setString(2, codEvento);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al validar inscripción: " + e.getMessage());
            return false;
        }
    }
    
    public List<Inscripcion> listarInscripciones()
    {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT participante_correo, evento_codigo, tipo, validada FROM inscripcion";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery())
        {
            ParticipanteDAO pDAO = new ParticipanteDAO();
            EventoDAO eDAO = new EventoDAO();
            while (rs.next())
            {
                String correo = rs.getString("participante_correo");
                String codEvento = rs.getString("evento_codigo");
                TipoInscripcion tipo = TipoInscripcion.valueOf(rs.getString("tipo"));
                boolean validada = rs.getBoolean("validada");
                Participante p = pDAO.buscarParticipante(correo);
                Evento e = eDAO.buscarEvento(codEvento);
                Inscripcion insc = new Inscripcion(p, e, tipo);
                insc.setValidada(validada);
                lista.add(insc);
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar las inscripciones: " + e.getMessage());
        }
        return lista;
    }
    
    public Inscripcion buscarInscripcion(String correo, String codEvento) // Se encarga de buscar las inscripciones
    {
        String sql = "SELECT tipo, validada FROM inscripcion WHERE participante_correo=? AND evento_codigo=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correo);
            ps.setString(2, codEvento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next())
                {
                    Participante p = new ParticipanteDAO().buscarParticipante(correo);
                    Evento e = new EventoDAO().buscarEvento(codEvento);
                    Inscripcion insc = new Inscripcion(p, e, TipoInscripcion.valueOf(rs.getString("tipo")));
                    insc.setValidada(rs.getBoolean("validada"));
                    return insc;
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al buscar inscripción: " + e.getMessage());
        }
        return null;
    }
    
    public boolean eliminarInscripcion(String correo, String codEvento)
    {
        String sql = "DELETE FROM inscripcion WHERE participante_correo=? AND evento_codigo=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, correo);
            ps.setString(2, codEvento);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar inscripción: " + e.getMessage());
            return false;
        }
    }
}
