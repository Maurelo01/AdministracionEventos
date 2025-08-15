/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.EventoDAO;
import com.mycompany.administracioneventos.modelos.Evento;
import java.sql.SQLException;
import java.util.List;

public class EventoServicio 
{
    private EventoDAO eventoDAO;
    
    public EventoServicio()
    {
        this.eventoDAO = new EventoDAO();
    }
    
    public boolean registrarEvento(Evento evento)
    {
        try
        {
            return eventoDAO.agregarEvento(evento);
        }
        catch (SQLException e)
        {
            System.err.println("Error al registrar evento: " + e.getMessage());
            return false;
        }
    }
    
    public List<Evento> obtenerEventos() 
    {
        return eventoDAO.listarEventos();
    }
    
    public Evento buscarEventoPorCodigo(String codigo) 
    {
        return eventoDAO.buscarEvento(codigo);
    }
    
    public boolean modificarEvento(Evento evento) 
    {
        return eventoDAO.actualizarEvento(evento);
    }
    
    public boolean eliminarEvento(String codigo)
    {
        return eventoDAO.eliminarEvento(codigo);
    }
}
