/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.modelos;

public class Participante 
{
    private String correo;               // PK
    private String nombreCompleto;       // Máx. 45 caracteres
    private TipoParticipante tipo;       // Enum: ESTUDIANTE, PROFESIONAL, INVITADO
    private String institucion;          // Máx. 150 caracteres

    public Participante(String correo, String nombreCompleto, TipoParticipante tipo, String institucion) 
    {
        this.correo = correo;
        this.nombreCompleto = nombreCompleto;
        this.tipo = tipo;
        this.institucion = institucion;
    }

    // Getters y Setters
    public String getCorreo() 
    {
        return correo;
    }

    public void setCorreo(String correo) 
    {
        this.correo = correo;
    }

    public String getNombreCompleto() 
    {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) 
    {
        this.nombreCompleto = nombreCompleto;
    }

    public TipoParticipante getTipo() 
    {
        return tipo;
    }

    public void setTipo(TipoParticipante tipo) 
    {
        this.tipo = tipo;
    }

    public String getInstitucion() 
    {
        return institucion;
    }

    public void setInstitucion(String institucion) 
    {
        this.institucion = institucion;
    }

    @Override
    public String toString() 
    {
        return "Participante{" +
                "correo='" + correo + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", tipo=" + tipo +
                ", institucion='" + institucion + '\'' +
                '}';
    }
}
