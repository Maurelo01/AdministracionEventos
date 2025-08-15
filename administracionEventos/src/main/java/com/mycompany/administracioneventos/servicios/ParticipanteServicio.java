/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.ParticipanteDAO;
import com.mycompany.administracioneventos.modelos.Participante;
import java.util.List;

public class ParticipanteServicio 
{
    private ParticipanteDAO participanteDAO;
    public ParticipanteServicio()
    {
        this.participanteDAO = new ParticipanteDAO();
    }
    public boolean registrarParticipante(Participante participante)
    {
        return participanteDAO.agregarParticipante(participante);
    }
    public List<Participante> obtenerParticipantes() 
    {
        return participanteDAO.listarParticipantes();
    }
    public Participante buscarParticipantePorCorreo(String correo)
    {
        return participanteDAO.buscarParticipante(correo);
    }
    public boolean modificarParticipante(Participante participante)
    {
        return participanteDAO.actualizarParticipante(participante);
    }
    public boolean eliminarParticipante(String correo)
    {
        return participanteDAO.eliminarParticipante(correo);
    }
}
