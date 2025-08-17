/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.EventoDAO;
import com.mycompany.administracioneventos.dao.InscripcionDAO;
import com.mycompany.administracioneventos.dao.ParticipanteDAO;
import com.mycompany.administracioneventos.modelos.*;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.util.List;

public class InscripcionServicio 
{
    private final InscripcionDAO dao;
    private final ParticipanteDAO participanteDAO;
    private final EventoDAO eventoDAO;
    
    public InscripcionServicio()
    {
        this.dao = new InscripcionDAO();
        this.participanteDAO = new ParticipanteDAO();
        this.eventoDAO = new EventoDAO();
    }
    
    public boolean registrarInscripcion(String correoParticipante, String codigoEvento, TipoInscripcion tipo) // Construye y registra la inscripcion  
    {
        Participante p = participanteDAO.buscarParticipante(correoParticipante);
        Evento e = eventoDAO.buscarEvento(codigoEvento);
        if (p == null || e == null)
        {
            System.err.println("Participante o evento no existen.");
            return false;
        }
        return dao.agregarInscripcion(new Inscripcion(p, e, tipo));
    }
    
    public boolean registrarInscripcion(Inscripcion inscripcion) // Registra la inscripcion ya contruida 
    {
        return dao.agregarInscripcion(inscripcion);
    }
    
    public boolean validarInscripcion(String correoParticipante, String codigoEvento)
    {
        return dao.validarInscripcion(correoParticipante, codigoEvento);
    }
    
    public List<Inscripcion> obtenerInscripciones()
    {
        return dao.listarInscripciones();
    }
    
    public Inscripcion buscarInscripcion(String correoParticipante, String codigoEvento)
    {
        return dao.buscarInscripcion(correoParticipante, codigoEvento);
    }
    
    public boolean eliminarInscripcion(String correoParticipante, String codigoEvento)
    {
        return dao.eliminarInscripcion(correoParticipante, codigoEvento);
    }
    
    public ResultadoOperacion eliminarInscripcionSeguro(String correoParticipante, String codigoEvento)
    {
        return dao.eliminarInscripcionSeguro(correoParticipante, codigoEvento);
    }
}
