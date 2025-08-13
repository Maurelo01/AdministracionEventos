/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.modelos.*;

import java.util.List;

public class ValidarInscripcion {

    public boolean validarInscripcion(Inscripcion inscripcion, List<Pago> listaPagos) {
        for (Pago pago : listaPagos) {
            boolean mismoParticipante = pago.getParticipante().getCorreo()
                    .equalsIgnoreCase(inscripcion.getParticipante().getCorreo());

            boolean mismoEvento = pago.getEvento().getCodigo()
                    .equalsIgnoreCase(inscripcion.getEvento().getCodigo());

            if (mismoParticipante && mismoEvento) {
                inscripcion.setValidada(true);
                return true; // Validación exitosa
            }
        }
        return false; // No se encontró pago a validar
    }
}
