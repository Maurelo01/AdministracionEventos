/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.*;
import com.mycompany.administracioneventos.modelos.*;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.time.LocalTime;
import java.util.List;

public class ActividadServicio 
{
    private final ActividadDAO actividadDAO;
    private final EventoDAO eventoDAO;
    private final InscripcionDAO inscripcionDAO;
    
    public ActividadServicio()
    {
        this.actividadDAO = new ActividadDAO();
        this.eventoDAO = new EventoDAO();
        this.inscripcionDAO = new InscripcionDAO();
    }
    
    public boolean registrarActividad(String codigoActividad, String codigoEvento, TipoActividad tipo, String titulo,
                     String correoEncargado, LocalTime horaInicio, LocalTime horaFin, int cupoMaximo)
    {
        Inscripcion encargado = inscripcionDAO.buscarInscripcion(correoEncargado, codigoEvento);
        Evento evento = eventoDAO.buscarEvento(codigoEvento);
        if (evento == null)
        {
            System.err.println("Evento no encontrado: " + codigoEvento);
            return false;
        }
        if (encargado == null)
        {
            System.err.println("El encargado no esta inscrito en el evento.");
            return false;
        }
        try
        {
            Actividad actividad = new Actividad(codigoActividad, evento, tipo, titulo, encargado, horaInicio, horaFin, cupoMaximo);
            return actividadDAO.agregarActividad(actividad);
        }
        catch (IllegalArgumentException ex)
        {
            System.err.println(ex.getMessage());
            return false;
        }
    }
    
    public boolean actualizarActividad(Actividad actividad)
    {
        return actividadDAO.actualizarActividad(actividad);
    }
    
    public Actividad buscarActividad(String codigoActividad)
    {
        return actividadDAO.buscarActividad(codigoActividad);
    }
    
    public List<Actividad> listarActividades()
    {
        return actividadDAO.listarActividades();
    }
    
    public boolean eliminarActividad(String codigoActividad)
    {
        return actividadDAO.eliminarActividad(codigoActividad);
    }
    
    public ResultadoOperacion eliminarActividadSeguro(String codigoActividad)
    {
        return actividadDAO.eliminarActividadSeguro(codigoActividad);
    }
}
