/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

import java.time.LocalDateTime;

public class Pago 
{
    private Participante participante;  // Relacion con Participante
    private Evento evento;              // Relacion con Evento
    private MetodoPago metodo;          // Enum
    private double monto;
    private LocalDateTime fechaPago;    // Fecha y hora en que se registró el pago

    public Pago(Participante participante, Evento evento, MetodoPago metodo, double monto) 
    {
        this.participante = participante;
        this.evento = evento;
        this.metodo = metodo;
        this.monto = monto;
        this.fechaPago = LocalDateTime.now(); // La fecha actual
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

    public MetodoPago getMetodo() 
    {
        return metodo;
    }

    public void setMetodo(MetodoPago metodo) 
    {
        this.metodo = metodo;
    }

    public double getMonto() 
    {
        return monto;
    }

    public void setMonto(double monto) 
    {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() 
    {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) 
    {
        this.fechaPago = fechaPago;
    }

    @Override
    public String toString() 
    {
        return "Pago{" +
                "participante=" + participante.getCorreo() +
                ", evento=" + evento.getCodigo() +
                ", metodo=" + metodo +
                ", monto=" + monto +
                ", fechaPago=" + fechaPago +
                '}';
    }
}
