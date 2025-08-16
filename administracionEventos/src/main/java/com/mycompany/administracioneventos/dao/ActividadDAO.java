/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.*;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO 
{
    private boolean existeActividad(String codigoActividad) // Verifica si existe la actividad
    {
        String sql = "SELECT 1 FROM actividad WHERE codigo = ? LIMIT 1";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, codigoActividad);
            try(ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar actividad: " + e.getMessage());
            return false;
        }
    }
    
    private Evento obtenerEvento(String codigoEvento) // obtiene el evento por su codigo
    {
        return new EventoDAO().buscarEvento(codigoEvento);
    }
    
    private Inscripcion obtenerInscripcionEncargado(String correoEncargado, String codigoEvento) // Obtiene la inscripcion del tipo encargado
    {
        return new InscripcionDAO().buscarInscripcion(correoEncargado, codigoEvento);
    }
    
    private boolean validarDatosBasicos(String titulo, LocalTime inicio, LocalTime fin, int cupoMax) // hace las validaciones basicas como que la hora de inicio no sea despues de la hora de fin
    {
        if (titulo == null || titulo.isBlank() || titulo.length() > 200)
        {
            System.err.println("Titulo inválido.");
            return false;
        }
        if (cupoMax <= 0)
        {
            System.err.println("El cupo maximo debe ser mayor a 0.");
            return false;
        }
        if (fin.isBefore(inicio) || fin.equals(inicio))
        {
            System.err.println("La hora de finalizacion siempre debe ser despues de la hora de inicio.");
            return false;
        }
        return true;
    }
    
    private boolean encargadoValidoParaEvento(Inscripcion enc, String codigoEvento) // Verifica que el encargado este inscrito en el evento
    {
        if (enc == null)
        {
            System.err.println("El encargado no esta inscrito en el evento.");
            return false;
        }
        if (!enc.getEvento().getCodigo().equalsIgnoreCase(codigoEvento))
        {
            System.err.println("El encargado no tiene inscripcion para este evento.");
            return false;
        }
        if (enc.getTipo() == TipoInscripcion.ASISTENTE)
        {
            System.err.println("El encargado no puede ser Asistente.");
            return false;
        }
        if (!enc.isValidada())
        {
            System.err.println("El encargado no tiene inscripcion valida en el evento.");
            return false;
        }
        return true;
    }
    
    // CRUD
    
    public boolean agregarActividad (Actividad a) // Añade una actividad con sus respectivas verificicaciones 
    {
        String codigoActividad = a.getCodigoActividad();
        String codigoEvento = a.getEvento().getCodigo();
        String titulo = a.getTitulo();
        int cupo = a.getCupoMaximo();
        LocalTime inicio = a.getHoraInicio();
        LocalTime fin = a.getHoraFin();
        
        if (existeActividad(codigoActividad))
        {
            System.err.println("Ya existe una actividad con este codigo: " + codigoActividad);
            return false;
        }
        Evento eve = obtenerEvento(codigoEvento);
        if (eve == null)
        {
            System.err.println("No existe evento.");
            return false;
        }
        if (!validarDatosBasicos(titulo, inicio, fin, cupo))
        {
            return false;
        }
        Inscripcion enc = a.getEncargado();
        if (enc == null)
        {
            System.err.println("No hay inscripcion de encargado.");
            return false;
        }
        if (!encargadoValidoParaEvento(enc, codigoEvento))
        {
            return false;
        }
        String sql = "INSERT INTO actividad (codigo, evento_codigo, tipo, titulo, encargado_correo, hora_inicio, hora_fin, cupo_maximo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
                ps.setString(1, codigoActividad);
                ps.setString(2, codigoEvento);
                ps.setString(3, a.getTipo().name());
                ps.setString(4, titulo);
                ps.setString(5, enc.getParticipante().getCorreo());
                ps.setTime(6, Time.valueOf(inicio));
                ps.setTime(7, Time.valueOf(fin));
                ps.setInt(8, cupo);
                return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al verificar actividad: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarActividad(Actividad a) // Actualiza las actividades con verificaciones
    {
        String codigoActividad = a.getCodigoActividad();
        String codigoEvento = a.getEvento().getCodigo();
        String titulo = a.getTitulo();
        int cupo = a.getCupoMaximo();
        LocalTime inicio = a.getHoraInicio();
        LocalTime fin = a.getHoraFin();
        
        if (!existeActividad(codigoActividad))
        {
            System.err.println("No existe la actividad: " + codigoActividad);
            return false;
        }
        Evento eve = obtenerEvento(codigoEvento);
        if (eve == null)
        {
            System.err.println("No existe evento.");
            return false;
        }
        Inscripcion enc = a.getEncargado();
        if (!encargadoValidoParaEvento(enc, codigoEvento))
        {
            System.err.println("El encargado no esta validado.");
            return false;
        }
        String sql = "UPDATE actividad SET evento_codigo = ?, tipo = ?, titulo = ?, encargado_correo = ?, hora_inicio = ?, hora_fin = ?, cupo_maximo = ? WHERE codigo = ?"; 
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
                ps.setString(1, codigoEvento);
                ps.setString(2, a.getTipo().name());
                ps.setString(3, titulo);
                ps.setString(4, enc.getParticipante().getCorreo());
                ps.setTime(5, Time.valueOf(inicio));
                ps.setTime(6, Time.valueOf(fin));
                ps.setInt(7, cupo);
                ps.setString(8, codigoActividad);
                return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al actualizar actividad: " + e.getMessage());
            return false;
        }
    }
    
    public Actividad buscarActividad(String codigoActividad) // Busca una actividad
    {
        String sql = "SELECT envento_codigo, tipo, titulo, encargado_correo, hora_inicio, hora_fin, cupo_maximo FROM actividad WHERE codigo = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, codigoActividad);
            try(ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    String codigoEvento = rs.getString("evento_codigo");
                    Evento eve = obtenerEvento(codigoEvento);
                    if (eve == null) return null;
                    String encargadoCorreo = rs.getString("encargado_correo");
                    Inscripcion enc = obtenerInscripcionEncargado(encargadoCorreo, codigoEvento);
                    Actividad actividad = new Actividad(codigoActividad, eve, TipoActividad.valueOf(rs.getString("tipo")), rs.getString("titulo"), enc,
                            rs.getTime("hora_inicio").toLocalTime(), rs.getTime("hora_fin").toLocalTime(), rs.getInt("cupo_maximo"));
                    return actividad;
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al buscar actividad: " + e.getMessage());
        }
        return null;
    }
    
    public List<Actividad>listarActividades() // Enlista todas las actividades
    {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT codigo, evento_codigo, tipo, titulo, encargado_correo, hora_inicio, hora_fin, cupo_maximo FROM actividad";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            EventoDAO eDAO = new EventoDAO();
            InscripcionDAO iDAO = new InscripcionDAO();
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String codigoActividad = rs.getString("codigo");
                    String codigoEvento = rs.getString("evento_codigo");
                    Evento eve = eDAO.buscarEvento(codigoEvento);
                    if (eve == null) continue;
                    String encargadoCorreo = rs.getString("encargado_correo");
                    Inscripcion enc = iDAO.buscarInscripcion(encargadoCorreo, codigoEvento);
                    Actividad actividad = new Actividad(codigoActividad, eve, TipoActividad.valueOf(rs.getString("tipo")), rs.getString("titulo"), enc,
                            rs.getTime("hora_inicio").toLocalTime(), rs.getTime("hora_fin").toLocalTime(), rs.getInt("cupo_maximo"));
                    lista.add(actividad);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al listar las actividades: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean eliminarActividad(String codigoActividad) // Borra la actividad indicada 
    {
        String sql = "DELETE FROM actividad WHERE codigo = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, codigoActividad);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error al eliminar la actividad: " + e.getMessage());
            return false;
        }
    }
}
