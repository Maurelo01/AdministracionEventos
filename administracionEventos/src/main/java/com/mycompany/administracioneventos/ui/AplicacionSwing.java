/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AplicacionSwing 
{
    public static void iniciarAplicacion()
    {
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception e) 
        {
            System.err.println("Error al establecer el look and feel: " + e.getMessage());
        }
        
        VentanaPrincipal ventana = new VentanaPrincipal();
        ventana.setVisible(true);
    }
    public static void main(String[] args) 
    {
        iniciarAplicacion();
    }
}
