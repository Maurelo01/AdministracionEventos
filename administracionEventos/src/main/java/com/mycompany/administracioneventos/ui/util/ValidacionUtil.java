/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.ui.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class ValidacionUtil 
{
    private ValidacionUtil() {}
    
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static LocalDate parsearFecha(String texto)
    {
        if (texto == null) return null;
        try
        {
            return LocalDate.parse(texto.trim(), FECHA);
        }
        catch (DateTimeParseException e) 
        {
            return null;
        }
    }
    
    public static LocalTime parsearHora(String texto) 
    {
        if (texto == null) return null;
        try
        {
            return LocalTime.parse(texto.trim(), HORA);
        }
        catch (DateTimeParseException e) 
        {
            return null;
        }
    }
    
    public static boolean esFuturoOHoy(LocalDate fecha)
    {
        return fecha != null && !fecha.isBefore(LocalDate.now());
    }
    
    public static boolean noVacio(String texto)
    {
        return texto != null && !texto.trim().isEmpty();
    }
    
    public static String formatear(LocalDate fecha)
    {
         return fecha == null ? "" : fecha.format(FECHA);
    }
    
    public static boolean esCorreoValido(String correo)
    {
        return correo != null && EMAIL.matcher(correo.trim()).matches();
    }   
    
    public static boolean longitudMaxima(String texto, int max)
    {
        return texto == null || texto.length() <= max;
    }

    public static boolean entreLongitudes(String texto, int min, int max)
    {
        if (texto == null) return min == 0;
        int longitud = texto.length();
        return longitud >= min && longitud <= max;
    }
}
