/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.modelos.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CertificadoHTML 
{
    public Certificado generarCertificado(Participante participante, Evento evento,
                                          List<Asistencia> asistencias, String rutaSalida)
    {
        boolean asistio = asistencias.stream().anyMatch(a -> a.getParticipante().getCorreo().equalsIgnoreCase(participante.getCorreo()) &&
                a.getActividad().getEvento().getCodigo().equalsIgnoreCase(evento.getCodigo()));
        if (!asistio) 
        {
             System.out.println("No se puede generar certificado ya que el participante no asistió a ninguna actividad valida.");
             return null;
        }
        String rutaArchivo = rutaSalida + "/" + participante.getNombreCompleto().replace(" ", "_") + "_certificado.html";
        generarHTML(participante, evento, rutaArchivo);
        return new Certificado(participante, evento, rutaArchivo);
    }
    
    private void generarHTML(Participante participante, Evento evento, String rutaArchivo) 
    {
        try (FileWriter writer = new FileWriter(rutaArchivo)) 
        {
            writer.write("<html><head><title>Certificado de Participación</title></head><body>");
            writer.write("<h1 style='text-align:center;'>Certificado de Participación</h1>");
            writer.write("<p style='text-align:center;'>Se certifica que <strong>" + participante.getNombreCompleto() +
                    "</strong> participó en el evento <strong>" + evento.getTitulo() + "</strong> realizado el " +
                    evento.getFecha() + ".</p>");
            writer.write("<p style='text-align:center;'>Emitido el " + java.time.LocalDate.now() + "</p>");
            writer.write("</body></html>");
        }
        catch (IOException e) 
        {
            System.out.println("Error al generar el certificado HTML: " + e.getMessage());
        }
    }
}
