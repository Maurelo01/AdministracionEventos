/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.*;
import com.mycompany.administracioneventos.modelos.*;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.util.List;
public class PagoServicio 
{
    private final PagoDAO pagoDAO;
    private final ParticipanteDAO pDAO;
    private final EventoDAO eDAO;
    
    public PagoServicio()
    {
        this.pagoDAO = new PagoDAO();
        this.pDAO = new ParticipanteDAO();
        this.eDAO = new EventoDAO();
    }
    
    public boolean registrarPago(String correoParticipante, String codigoEvento, MetodoPago metodo, double monto) // se registra el pago por medio de la Id
    {
        Participante p = pDAO.buscarParticipante(correoParticipante);
        Evento e = eDAO.buscarEvento(codigoEvento);
        if (p == null || e == null)
        {
            System.err.println("Participante o evento no existe.");
            return false;
        }
        return pagoDAO.registrarPago(new Pago(p, e, metodo, monto));
    }
    
    public boolean registrarPago(Pago pago)
    {
        return pagoDAO.registrarPago(pago);
    }
    
    public boolean existePago(String correoParticipante, String codigoEvento)
    {
        return pagoDAO.existePago(correoParticipante, codigoEvento);
    }
    
    public List<Pago> listarPago()
    {
        return pagoDAO.listarPagos();
    }
    
    public List<Pago> listarPagoPorEvento(String codigoEvento)
    {
        return pagoDAO.listarPagosPorEventos(codigoEvento);
    }
    
    public List<Pago> listarPagoPorParticipante(String correoParticipante)
    {
        return pagoDAO.listarPagosPorParticipante(correoParticipante);
    }
    
    public ResultadoOperacion eliminarPago(String correoParticipante, String codigoEvento, MetodoPago metodo, double monto)
    {
        return pagoDAO.eliminarPago(correoParticipante, codigoEvento, metodo, monto);
    }
    
    public ResultadoOperacion eliminarPagoSeguro(String correoParticipante, String codigoEvento)
    {
        return pagoDAO.eliminarPagoSeguro(correoParticipante, codigoEvento);
    }
}
