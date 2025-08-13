/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

import java.time.LocalDate;

public class Evento {
    private String codigo;          // PK
    private LocalDate fecha;
    private TipoEvento tipo;        // enums
    private String titulo;
    private String ubicacion;
    private int cupoMaximo;

    public Evento(String codigo, LocalDate fecha, TipoEvento tipo, String titulo, String ubicacion, int cupoMaximo) 
    {
        this.codigo = codigo;
        this.fecha = fecha;
        this.tipo = tipo;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.cupoMaximo = cupoMaximo;
    }

    // Getters y setters
    public String getCodigo() 
    { 
        return codigo; 
    }
    public void setCodigo(String codigo) 
    { 
        this.codigo = codigo; 
    }

    public LocalDate getFecha() 
    { 
        return fecha; 
    }
    public void setFecha(LocalDate fecha) 
    { 
        this.fecha = fecha; 
    }

    public TipoEvento getTipo() 
    { 
        return tipo; 
    }
    public void setTipo(TipoEvento tipo) 
    { 
        this.tipo = tipo; 
    }

    public String getTitulo() 
    { 
        return titulo; 
    }
    public void setTitulo(String titulo) 
    { 
        this.titulo = titulo; 
    }

    public String getUbicacion() 
    { 
        return ubicacion; 
    }
    public void setUbicacion(String ubicacion) 
    { 
        this.ubicacion = ubicacion; 
    }

    public int getCupoMaximo() 
    { 
        return cupoMaximo; 
    }
    public void setCupoMaximo(int cupoMaximo) 
    { 
        this.cupoMaximo = cupoMaximo; 
    }

    @Override
    public String toString() 
    {
        return "Evento{" +
                "codigo='" + codigo + '\'' +
                ", fecha=" + fecha +
                ", tipo=" + tipo +
                ", titulo='" + titulo + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", cupoMaximo=" + cupoMaximo +
                '}';
    }
}
