/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.ui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Alertas 
{
    public static void informacion(Component padre, String mensaje)
    {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void exito(Component padre, String mensaje)
    {
        JOptionPane.showMessageDialog(padre, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void advertencia(Component padre, String mensaje)
    {
        JOptionPane.showMessageDialog(padre, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    public static void error(Component padre, String mensaje)
    {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static boolean confirmar(Component padre, String mensaje)
    {
        int r = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION); 
        return r == JOptionPane.YES_OPTION;
    }
    
    public static String preguntarTexto(Component padre, String titulo, String mensaje, String valorInicial)
    {
        return (String) JOptionPane.showInputDialog(padre, mensaje, titulo, JOptionPane.QUESTION_MESSAGE, null, null, valorInicial);
    }
    
    public static int opciones(Component padre, String titulo, String mensaje, String[] opciones, int indicePorDefecto)
    {
        int r = JOptionPane.showOptionDialog(padre, mensaje, titulo, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, 
                (opciones != null && opciones.length > 0 && indicePorDefecto >= 0 && indicePorDefecto < opciones.length) ? opciones[indicePorDefecto] : null);
        return r;
    }
    
    public static void toast(Component padre, String mensaje, int milisegundos)
    {
        final JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(padre));
        dialogo.setUndecorated(true);
        JLabel lbl = new JLabel(mensaje);
        lbl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0,0,0,120)), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        dialogo.getContentPane().add(lbl);
        dialogo.pack();
        
        Point punto;
        Dimension tamaño;
        if (padre != null)
        {
           Window ventana = SwingUtilities.getWindowAncestor(padre);
           if (ventana != null)
           {
               punto = ventana.getLocationOnScreen();
               tamaño = ventana.getSize();
               dialogo.setLocation(punto.x + tamaño.width - dialogo.getWidth() - 25, punto.y + tamaño.height - dialogo.getHeight()- 25);
           }
           else
           {
               centrarEnPantalla(dialogo);
           }
        }
        else
        {
            centrarEnPantalla(dialogo);
        }
        Timer t = new Timer(milisegundos, e -> dialogo.dispose());
        t.setRepeats(false);
        dialogo.setVisible(true);
        t.start();
    }
    
    private static void centrarEnPantalla(Window ventana)
    {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        ventana.setLocation((pantalla.width - ventana.getWidth())/2 , (pantalla.height - ventana.getHeight())/2);
    }
}
