/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

public class Inscripcion 
{
    private Participante participante;  // Relación con Participante
    private Evento evento;              // Relación con Evento
    private TipoInscripcion tipo;       // Enum
    private boolean validada;           // Si la inscripción ya fue confirmada

    public Inscripcion(Participante participante, Evento evento, TipoInscripcion tipo) 
    {
        this.participante = participante;
        this.evento = evento;
        this.tipo = tipo;
        this.validada = false; // Por defecto no está validada
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

    public Evento getEvento() 
    {
        return evento;
    }

    public void setEvento(Evento evento) 
    {
        this.evento = evento;
    }

    public TipoInscripcion getTipo() 
    {
        return tipo;
    }

    public void setTipo(TipoInscripcion tipo) 
    {
        this.tipo = tipo;
    }

    public boolean isValidada() 
    {
        return validada;
    }

    public void setValidada(boolean validada) 
    {
        this.validada = validada;
    }

    @Override
    public String toString() 
    {
        return "Inscripcion{" +
                "participante=" + participante.getCorreo() +
                ", evento=" + evento.getCodigo() +
                ", tipo=" + tipo +
                ", validada=" + validada +
                '}';
    }
}