/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.AsistenciaDAO;
import com.mycompany.administracioneventos.modelos.Actividad;
import com.mycompany.administracioneventos.modelos.Asistencia;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.util.List;

public class AsistenciaServicio 
{
    private final AsistenciaDAO asistenciaDAO;
    
    public AsistenciaServicio()
    {
        this.asistenciaDAO = new AsistenciaDAO();
    }
    
    public boolean registrarAsistencia(String correoParticipante, String codigoActividad) // Registra una asistencia, la asistencia es validada 
    {
        return asistenciaDAO.registrarAsistencia(correoParticipante, codigoActividad, true);
    }
    
    public boolean eliminarAsistencias(String correoParticipante, String codigoActividad)
    {
        return asistenciaDAO.eliminarAsistencia(correoParticipante, codigoActividad);
    }
    
    public Asistencia buscarAsistencia(String correoParticipante, String codigoActividad)
    {
        return asistenciaDAO.buscarAsistencia(correoParticipante, codigoActividad);
    }
    
    public List<Asistencia> listarAsistencias()
    {
        return asistenciaDAO.listarAsistencias();
    }
    
    public ResultadoOperacion eliminarAsistenciaSeguro(String correoParticipante, String codigoActividad)
    {
        return asistenciaDAO.eliminarAsistenciaSeguro(correoParticipante, codigoActividad);
    }
}
