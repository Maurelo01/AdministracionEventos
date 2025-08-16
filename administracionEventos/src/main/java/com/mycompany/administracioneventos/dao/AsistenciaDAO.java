/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.Actividad;
import com.mycompany.administracioneventos.modelos.Asistencia;
import com.mycompany.administracioneventos.modelos.Inscripcion;
import com.mycompany.administracioneventos.modelos.Participante;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO 
{
    private int contarAsistencias(String codigoActividad) // Cuenta todos los asistentes a una actividad
    {
        String sql = "SELECT COUNT(*) AS total FROM asistencia WHERE actividad_codigo = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, codigoActividad);
            try(ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("total");
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al contar asistencias: " + e.getMessage());
        }
        return Integer.MAX_VALUE;
    }
    
    private boolean existeAsistencia(String correoParticipante, String codigoActividad) // Verifica si la asistencia no esta duplicada
    {
        String sql = "SELECT 1 FROM asistencia WHERE participante_correo = ? AND actividad_codigo = ? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoActividad);
            try(ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar si la asistencia ya existe: " + e.getMessage());
            return false;
        }
    }
    
    // CRUD
    
    public boolean registrarAsistencia(String correoParticipante, String actividadCodigo, boolean exigirInscripcionValidada) // Registra la asistencia con las validaciones basicas
    {
        ActividadDAO actividadDAO = new ActividadDAO();
        Actividad actividad = new ActividadDAO().buscarActividad(actividadCodigo);
        if(actividad == null)
        {
            System.err.println("No existe la actividad: " + actividadCodigo);
            return false;
        }
        String codigoEvento = actividad.getEvento().getCodigo();
        int cupoMaximo = actividad.getCupoMaximo();
        InscripcionDAO inscripcionDAO = new InscripcionDAO();
        Inscripcion inscripcion = inscripcionDAO.buscarInscripcion(correoParticipante, codigoEvento);
        if (inscripcion == null)
        {
            System.err.println("El participante no esta inscrito en el evento: " + codigoEvento);
            return false;
        }
        if (exigirInscripcionValidada && !inscripcion.isValidada())
        {
            System.err.println("El participante no tiene una inscripcion validada.");
            return false;
        }
        if(existeAsistencia(correoParticipante, actividadCodigo))
        {
            System.err.println("Asistencia duplicada para: " + correoParticipante + " en la actividad: " + actividadCodigo);
            return false;
        }
        
        int asistenciasActuales = contarAsistencias(actividadCodigo);
        if(asistenciasActuales >= cupoMaximo)
        {
            System.err.println("El cupo esta lleno en la actividad: " + actividadCodigo + " El cupo maximo es: " + cupoMaximo);
            return false;
        }
        
        String sql = "INSERT INTO asistencia (participante_correo, actividad_codigo) VALUE(?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, actividadCodigo);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registrarAsistencia(Asistencia asistencia, boolean exigirInscripcionValidada) // Version para registrar asistencia desde la clase Asistencia
    {
        String correoParticipante = asistencia.getParticipante().getCorreo();
        String codigoActividad = asistencia.getActividad().getCodigoActividad();
        return registrarAsistencia(correoParticipante, codigoActividad, exigirInscripcionValidada); 
    }
    
    public boolean eliminarAsistencia(String correoParticipante, String codigoActividad) // Elimina una asistencia
    {
        String sql = "DELETE FROM asistencia WHERE participante_correo = ? AND actividad_codigo = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoActividad);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar asistencia: " + e.getMessage());
            return false;
        }
    }
    
    public Asistencia buscarAsistencia(String correoParticipante, String codigoActividad) // se encarga de buscar la asistencia
    {
        String sql = "SELECT fecha_registro FROM asistencia WHERE participante_correo =? AND actividad_codigo = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, correoParticipante);
            ps.setString(2, codigoActividad);
            try(ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    Participante participante = new ParticipanteDAO().buscarParticipante(correoParticipante);
                    Actividad actividad = new ActividadDAO().buscarActividad(codigoActividad);
                    if (participante == null || actividad == null) return null;
                    return new Asistencia(participante, actividad);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar asistencia: " + e.getMessage());
        }
        return null;
    }
    
    public List<Asistencia> listarAsistencias() // Lista las asistencias
    {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT participante_correo, actividad_codigo FROM asistencia";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery())
        {
            ParticipanteDAO participanteDAO = new ParticipanteDAO();
            ActividadDAO actividadDAO = new ActividadDAO();
            while (rs.next())
            {
                String correoParticipante = rs.getString("participante_correo");
                String codigoActividad = rs.getString("actividad_codigo");
                Participante participante = participanteDAO.buscarParticipante(correoParticipante);
                Actividad actividad = actividadDAO.buscarActividad(codigoActividad);
                if (participante == null || actividad == null) continue;
                lista.add(new Asistencia(participante, actividad));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar asistencias: " + e.getMessage());
        }
        return lista;
    }
}
