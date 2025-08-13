/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

import java.time.LocalTime;


public class Actividad 
{
    private String codigoActividad;
    private Evento evento;      // Relacion con Evento
    private TipoActividad tipo; // Enum
    private String titulo;     // Max 200 caracteres
    private Inscripcion encargado;        // No puede ser tipo ASISTENTE
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int cupoMaximo;
    
    public Actividad(String codigoActividad, Evento evento, TipoActividad tipo, String titulo,
                     Inscripcion encargado, LocalTime horaInicio, LocalTime horaFin, int cupoMaximo)
    {
        this.codigoActividad = codigoActividad;
        this.evento = evento;
        this.tipo = tipo;
        this.titulo = titulo;
        this.encargado = encargado;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cupoMaximo = cupoMaximo;
        
        // Validacion de caracteres del titulo
        if (titulo.length() > 200) 
        {
            throw new IllegalArgumentException("El título no puede superar los 200 caracteres");
        }
        // Validacion de encargado
        if (encargado.getTipo() == TipoInscripcion.ASISTENTE) 
        {
            throw new IllegalArgumentException("El encargado no puede ser del tipo ASISTENTE");
        }
        // Validacion de horas
        if (horaFin.isBefore(horaInicio)) 
        {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la hora de inicio");
        }
    }
    
    // Getters y Setters
    public String getCodigoActividad() 
    {
        return codigoActividad;
    }
    
    public void setCodigoActividad(String codigoActividad) 
    {
        this.codigoActividad = codigoActividad;
    }

    public Evento getEvento() 
    {
        return evento;
    }
    
    public void setEvento(Evento evento) 
    {
        this.evento = evento;
    }
    
    public TipoActividad getTipo() 
    {
        return tipo;
    }
    
    public void setTipo(TipoActividad tipo) 
    {
        this.tipo = tipo;
    }
    
    public String getTitulo() 
    {
        return titulo;
    }
    
    public void setTitulo(String titulo) 
    {
        if (titulo.length() > 200) 
        {
            throw new IllegalArgumentException("El título no puede superar los 200 caracteres");
        }
        this.titulo = titulo;
    }
    
    public Inscripcion getEncargado() 
    {
        return encargado;
    }
    
    public void setEncargado(Inscripcion encargado) 
    {
        if (encargado.getTipo() == TipoInscripcion.ASISTENTE) 
        {
            throw new IllegalArgumentException("El encargado no puede tener tipo ASISTENTE");
        }
        this.encargado = encargado;
    }
    
    public LocalTime getHoraInicio() 
    {
        return horaInicio;
    }
    
    public void setHoraInicio(LocalTime horaInicio) 
    {
        this.horaInicio = horaInicio;
    }
    
    public LocalTime getHoraFin() 
    {
        return horaFin;
    }
    
    public void setHoraFin(LocalTime horaFin) 
    {
        if (horaFin.isBefore(this.horaInicio)) 
        {
            throw new IllegalArgumentException("La hora de fin no puede ser antes que la hora de inicio");
        }
        this.horaFin = horaFin;
    }
    
    public int getCupoMaximo() 
    {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) 
    {
        this.cupoMaximo = cupoMaximo;
    }
    
    public String toString() {
        return "Actividad{" +
                "codigoActividad='" + codigoActividad + '\'' +
                ", evento=" + evento.getCodigo() +
                ", tipo=" + tipo +
                ", titulo='" + titulo + '\'' +
                ", encargado=" + encargado.getParticipante().getCorreo()+
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", cupoMaximo=" + cupoMaximo +
                '}';
    }

}
