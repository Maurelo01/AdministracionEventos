/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.AsistenciaDAO;
import com.mycompany.administracioneventos.dao.CertificadoDAO;
import com.mycompany.administracioneventos.dao.EventoDAO;
import com.mycompany.administracioneventos.dao.ParticipanteDAO;
import com.mycompany.administracioneventos.modelos.Asistencia;
import com.mycompany.administracioneventos.modelos.Certificado;
import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.Participante;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class CertificadoServicio 
{
    private final CertificadoDAO certificadoDAO;
    private final ParticipanteDAO participanteDAO;
    private final EventoDAO eventoDAO;
    private final AsistenciaDAO asistenciaDAO;
    private final CertificadoHTML certificadoHTML;
    
    public CertificadoServicio()
    {
        this.certificadoDAO = new CertificadoDAO();
        this.participanteDAO = new ParticipanteDAO();
        this.eventoDAO = new EventoDAO();
        this.asistenciaDAO = new AsistenciaDAO();
        this.certificadoHTML = new CertificadoHTML();
    }
    
    public Certificado generarYRegistrarCertificado(String correoParticipante, String codigoEvento, String directorioSalida, boolean exigirInscripcionValidada)
    {
        Participante participante = participanteDAO.buscarParticipante(correoParticipante);
        Evento evento = eventoDAO.buscarEvento(codigoEvento);
        if (participante == null || evento == null)
        {
            System.err.println("No existe participante o evento.");
            return null;
        }
        if (certificadoDAO.existeCertificado(correoParticipante, codigoEvento))
        {
            System.err.println("Ya se genero el certificado para el participante y evento.");
            return certificadoDAO.buscarCertificado(correoParticipante, codigoEvento);
        }
        if (!certificadoDAO.asistioAlEvento(correoParticipante, codigoEvento))
        {
            System.err.println("No se puede general el certificado ya que el participante no asistio al evento.");
            return null;
        }
        if (exigirInscripcionValidada && !certificadoDAO.inscripcionValidada(correoParticipante, codigoEvento))
        {
            System.err.println("No tiene una inscripcion validada por lo tanto no se puede generar certificado.");
            return null;
        }
        List<Asistencia> asistenciaDelEvento = obtenerAsistenciasDe(correoParticipante, codigoEvento);
        asegurarDirectorio(directorioSalida);
        Certificado certificado = certificadoHTML.generarCertificado(participante, evento, asistenciaDelEvento, directorioSalida);
        if (certificado == null)
        {
            return null;
        }
        boolean ok = certificadoDAO.insertarCertificado(participante, evento, certificado.getRutaArchivo());
        if (!ok)
        {
            System.err.println("No se pudo registrar el certificado en la BD.");
            return null;
        }
        return certificado;
    }
    
    private List<Asistencia> obtenerAsistenciasDe(String correoParticipante, String codigoEvento)
    {
        return asistenciaDAO.listarAsistencias().stream().filter(a -> a.getParticipante() != null && a.getParticipante().getCorreo().equalsIgnoreCase(correoParticipante) &&
                a.getActividad() != null && a.getActividad().getEvento() != null && 
                a.getActividad().getEvento().getCodigo().equalsIgnoreCase(codigoEvento)).collect(Collectors.toList());
    }
    
    private void asegurarDirectorio(String dir)
    {
        File carpeta = new File(dir);
        if (!carpeta.exists())
        {
            carpeta.mkdirs();
        }
    }
}
