/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

import java.time.LocalDateTime;

public class Certificado 
{
    private Participante participante;  // Relacion con Participante
    private Evento evento;              // Relacion con Evento
    private LocalDateTime fechaEmision; // Fecha y hora en que se genero
    private String rutaArchivo;         // Ruta del archivo HTML generado
    
    public Certificado(Participante participante, Evento evento, String rutaArchivo) 
    {
        this.participante = participante;
        this.evento = evento;
        this.fechaEmision = LocalDateTime.now();
        this.rutaArchivo = rutaArchivo;
    }
    
    // Getters y setters
    public Participante getParticipante() 
    {
        return participante;
    }
    
    public void setParticipante(Participante participante) 
    {
        this.participante = participante;
    }
    
    public Evento getEvento() 
    {
        return evento;
    }
    
    public void setEvento(Evento evento) 
    {
        this.evento = evento;
    }
    
    public LocalDateTime getFechaEmision() 
    {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDateTime fechaEmision) 
    {
        this.fechaEmision = fechaEmision;
    }
    
    public String getRutaArchivo() 
    {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) 
    {
        this.rutaArchivo = rutaArchivo;
    }
    
    @Override
    public String toString() 
    {
        return "Certificado{" +
                "participante=" + participante.getCorreo() +
                ", evento=" + evento.getCodigo() +
                ", fechaEmision=" + fechaEmision +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                '}';
    }


}
