/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.InscripcionDAO;
import com.mycompany.administracioneventos.modelos.Inscripcion;
import java.util.List;

public class InscripcionServicio 
{
    private final InscripcionDAO dao;
    
    public InscripcionServicio()
    {
        this.dao = new InscripcionDAO();
    }
    
    public boolean registrarInscripcion(Inscripcion inscripcion) 
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
}
