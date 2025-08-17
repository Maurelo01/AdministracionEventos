/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.util;

public class ResultadoOperacion 
{
    private final boolean ok;
    private final String mensaje;
    
    public ResultadoOperacion(boolean ok, String mensaje)
    {
        this.ok = ok;
        this.mensaje = mensaje;
    }
    
    public static ResultadoOperacion ok(String mensaje)
    {
        return new ResultadoOperacion(true, mensaje);
    }
    
    public static ResultadoOperacion fallo(String mensaje)
    {
        return new ResultadoOperacion(false, mensaje);
    }
    
    public boolean isOk() 
    {
        return ok;
    }
    
    public String getMensaje()
    {
        return mensaje;
    }
    
    @Override
    public String toString()
    {
        return (ok ? "OK: " : "ERROR: " ) + mensaje;
    }
}
