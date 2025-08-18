/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AplicacionSwing 
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ignore) {}
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        }
        );
    }
}
