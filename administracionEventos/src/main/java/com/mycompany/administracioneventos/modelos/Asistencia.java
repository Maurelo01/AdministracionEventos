/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

import java.time.LocalDateTime;

public class Asistencia 
{
    private Participante participante;   // Relacion con Participante
    private Actividad actividad;         // Relacion con Actividad
    private LocalDateTime fechaRegistro; // Fecha y hora en que se registro la asistencia
    
    public Asistencia(Participante participante, Actividad actividad) 
    {
        this.participante = participante;
        this.actividad = actividad;
        this.fechaRegistro = LocalDateTime.now(); 
    }
    
    // Getters y Setters
    public Participante getParticipante() 
    {
        return participante;
    }
    
    public void setParticipante(Participante participante) 
    {
        this.participante = participante;
    }
    
    public Actividad getActividad() 
    {
        return actividad;
    }
    
    public void setActividad(Actividad actividad)
    {
        this.actividad = actividad;
    }
    
    public LocalDateTime getFechaRegistro()
    {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro)
    {
        this.fechaRegistro = fechaRegistro;
    }
    
    public String toString() 
    {
        return "Asistencia{" +
                "participante=" + participante.getCorreo() +
                ", actividad=" + actividad.getCodigoActividad() +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
