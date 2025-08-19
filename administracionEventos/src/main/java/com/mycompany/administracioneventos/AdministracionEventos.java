/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.administracioneventos;

import com.mycompany.administracioneventos.ui.AplicacionSwing;
import javax.swing.SwingUtilities;

public class AdministracionEventos 
{
    public static void main(String[] args) 
    {
        System.out.println("Aplicación de Administración de Eventos iniciada.");
        SwingUtilities.invokeLater(() -> {
            AplicacionSwing.iniciarAplicacion();
        });
    }
}