/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.dao;

import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO 
{
    public static class ParticipanteInscritoDTO 
    {
        public String correo;
        public String nombreCompleto;
        public String tipoParticipante;
        public String institucion;
        public boolean validada;
        public double montoPagado;   
        public String metodoPago;    
    }
    
    public static class ActividadDTO 
    {
        public final String eventoCodigo;
        public final String actividadCodigo;
        public final String titulo;
        public final String tipo;
        public final String encargadoCorreo;
        public final java.time.LocalTime horaInicio;
        public final java.time.LocalTime horaFin;
        public final int cupo;
        public final int inscritos;
        public final int asistencias; 

        public ActividadDTO(String eventoCodigo, String actividadCodigo, String titulo, String tipo, String encargadoCorreo, 
                java.sql.Time horaInicio, java.sql.Time horaFin, int cupo, int inscritos, int asistencias) 
        {
            this.eventoCodigo = eventoCodigo;
            this.actividadCodigo = actividadCodigo;
            this.titulo = titulo;
            this.tipo = tipo;
            this.encargadoCorreo = encargadoCorreo;
            this.horaInicio = (horaInicio != null ? horaInicio.toLocalTime() : null);
            this.horaFin   = (horaFin   != null ? horaFin.toLocalTime()   : null);
            this.cupo = cupo;
            this.inscritos = inscritos;
            this.asistencias = asistencias;
        }
    }
    
    public List<ParticipanteInscritoDTO> participantesPorEvento( String codigoEvento, String tipoParticipanteOpt, String institucionOpt) 
    {
        List<ParticipanteInscritoDTO> lista = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p.correo, p.nombre_completo, p.tipo, p.institucion, i.validada ").append("FROM inscripcion i ")
          .append("JOIN participante p ON p.correo = i.participante_correo ").append("WHERE i.evento_codigo = ? ");
        List<Object> params = new ArrayList<>();
        params.add(codigoEvento);
        if (tipoParticipanteOpt != null && !tipoParticipanteOpt.isBlank()) 
        {
            sb.append("AND p.tipo = ? ");
            params.add(tipoParticipanteOpt);
        }
        if (institucionOpt != null && !institucionOpt.isBlank()) 
        {
            sb.append("AND p.institucion LIKE ? ");
            params.add("%" + institucionOpt + "%");
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sb.toString())) 
        {
            for (int i = 0; i < params.size(); i++)
            {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    ParticipanteInscritoDTO dto = new ParticipanteInscritoDTO();
                    dto.correo = rs.getString("correo");
                    dto.nombreCompleto = rs.getString("nombre_completo");
                    dto.tipoParticipante = rs.getString("tipo");
                    dto.institucion = rs.getString("institucion");
                    dto.validada = rs.getBoolean("validada");

                    // Pagos: suma total y último método
                    dto.montoPagado = montoPagadoParticipante(conn, dto.correo, codigoEvento);
                    dto.metodoPago = ultimoMetodoPago(conn, dto.correo, codigoEvento);
                    lista.add(dto);
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en participantesPorEvento: " + e.getMessage());
        }

        return lista;
    }
    
    private double montoPagadoParticipante(Connection conn, String correo, String evento) throws SQLException 
    {
        String q = "SELECT COALESCE(SUM(monto),0) FROM pago WHERE participante_correo=? AND evento_codigo=?";
        try (PreparedStatement ps = conn.prepareStatement(q)) 
        {
            ps.setString(1, correo);
            ps.setString(2, evento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }
    
    private String ultimoMetodoPago(Connection conn, String correo, String evento) throws SQLException 
    {
        String q = "SELECT metodo FROM pago WHERE participante_correo=? AND evento_codigo=? ORDER BY fecha_pago DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, correo);
            ps.setString(2, evento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) return rs.getString(1);
            }
        }
        return "";
    }
    
    public List<ActividadDTO> actividadesPorEvento(String codigoEvento, String tipoFiltro, String encargadoFiltro) 
    {
        List<ActividadDTO> out = new ArrayList<>();

        String sql =
            "SELECT ac.evento_codigo, ac.codigo, ac.titulo, ac.tipo, ac.encargado_correo, " +
            "       ac.hora_inicio, ac.hora_fin, ac.cupo_maximo, " +
            "       (SELECT COUNT(*) FROM inscripcion i WHERE i.evento_codigo = ac.evento_codigo) AS inscritos, " +
            "       (SELECT COUNT(*) FROM asistencia a WHERE a.actividad_codigo = ac.codigo) AS asistencias " +
            "FROM actividad ac " +
            "WHERE ac.evento_codigo = ? " +
            (tipoFiltro != null && !tipoFiltro.isBlank() ? " AND ac.tipo = ? " : "") +
            (encargadoFiltro != null && !encargadoFiltro.isBlank() ? " AND ac.encargado_correo = ? " : "") +
            "ORDER BY ac.codigo";

        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) 
        {

            int idx = 1;
            ps.setString(idx++, codigoEvento);
            if (tipoFiltro != null && !tipoFiltro.isBlank()) ps.setString(idx++, tipoFiltro);
            if (encargadoFiltro != null && !encargadoFiltro.isBlank()) ps.setString(idx++, encargadoFiltro);

            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    out.add(new ActividadDTO(
                        rs.getString(1),  // evento_codigo
                        rs.getString(2),  // codigo
                        rs.getString(3),  // titulo
                        rs.getString(4),  // tipo
                        rs.getString(5),  // encargado_correo
                        rs.getTime(6),    // hora_inicio
                        rs.getTime(7),    // hora_fin
                        rs.getInt(8),     // cupo_maximo
                        rs.getInt(9),     // inscritos
                        rs.getInt(10)     // asistencias
                    ));
                }
            }
        } 
        catch (SQLException e)
        {
            System.err.println("Error en actividadesPorEvento: " + e.getMessage());
        }
        return out;
    }
    
    public List<Evento> eventosFiltrados( String tipoEventoOpt, LocalDate fechaIniOpt, LocalDate fechaFinOpt, Integer cupoIniOpt, Integer cupoFinOpt) 
    {
        List<Evento> lista = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT codigo, fecha, tipo, titulo, ubicacion, cupo_maximo ")
          .append("FROM evento WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        boolean algunFiltro = false;

        if (tipoEventoOpt != null && !tipoEventoOpt.isBlank()) 
        {
            sb.append("AND tipo = ? ");
            params.add(tipoEventoOpt);
            algunFiltro = true;
        }
        if (fechaIniOpt != null || fechaFinOpt != null) 
        {
            if (fechaIniOpt == null || fechaFinOpt == null) {} 
            else 
            {
                sb.append("AND fecha BETWEEN ? AND ? ");
                params.add(Date.valueOf(fechaIniOpt));
                params.add(Date.valueOf(fechaFinOpt));
                algunFiltro = true;
            }
        }
        if (cupoIniOpt != null || cupoFinOpt != null) 
        {
            if (cupoIniOpt == null || cupoFinOpt == null) {} 
            else 
            {
                sb.append("AND cupo_maximo BETWEEN ? AND ? ");
                params.add(cupoIniOpt);
                params.add(cupoFinOpt);
                algunFiltro = true;
            }
        }

        if (!algunFiltro) 
        {
            return lista;
        }
        sb.append("ORDER BY fecha ASC, codigo ASC");
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sb.toString())) 
        {
            for (int i = 0; i < params.size(); i++) 
            {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    Evento ev = new Evento(
                            rs.getString("codigo"),
                            rs.getDate("fecha").toLocalDate(),
 com.mycompany.administracioneventos.modelos.TipoEvento.valueOf(rs.getString("tipo")),
                            rs.getString("titulo"),
                            rs.getString("ubicacion"),
                            rs.getInt("cupo_maximo")
                    );
                    lista.add(ev);
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en eventosFiltrados: " + e.getMessage());
        }
        return lista;
    }
    
    public double montoTotalEvento(String codigoEvento) 
    {
        String q = "SELECT COALESCE(SUM(monto),0) FROM pago WHERE evento_codigo = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(q)) 
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                rs.next();
                return rs.getDouble(1);
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en montoTotalEvento: " + e.getMessage());
            return 0;
        }
    }
    
    public int contarValidados(String codigoEvento) 
    {
        String q = "SELECT COUNT(*) FROM inscripcion WHERE evento_codigo = ? AND validada = 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(q)) 
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                rs.next();
                return rs.getInt(1);
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en contarValidados: " + e.getMessage());
            return 0;
        }
    }
    
    public int contarNoValidados(String codigoEvento) 
    {
        String q = "SELECT COUNT(*) FROM inscripcion WHERE evento_codigo = ? AND validada = 0";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(q)) 
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                rs.next();
                return rs.getInt(1);
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en contarNoValidados: " + e.getMessage());
            return 0;
        }
    }
    
    public List<ParticipanteInscritoDTO> participantesConPagosPorEvento(String codigoEvento) 
    {
        List<ParticipanteInscritoDTO> out = new ArrayList<>();
        String q = "SELECT p.correo, p.nombre_completo, p.tipo, p.institucion, i.validada " +
                   "FROM inscripcion i JOIN participante p ON p.correo = i.participante_correo " +
                   "WHERE i.evento_codigo = ? ORDER BY p.nombre_completo ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(q)) 
        {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    ParticipanteInscritoDTO dto = new ParticipanteInscritoDTO();
                    dto.correo = rs.getString("correo");
                    dto.nombreCompleto = rs.getString("nombre_completo");
                    dto.tipoParticipante = rs.getString("tipo");
                    dto.institucion = rs.getString("institucion");
                    dto.validada = rs.getBoolean("validada");
                    dto.montoPagado = montoPagadoParticipante(conn, dto.correo, codigoEvento);
                    dto.metodoPago = ultimoMetodoPago(conn, dto.correo, codigoEvento);
                    out.add(dto);
                }
            }
        } 
        catch (SQLException e) 
        {
            System.err.println("Error en participantesConPagosPorEvento: " + e.getMessage());
        }
        return out;
    }
}
