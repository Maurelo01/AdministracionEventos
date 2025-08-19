/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.administracioneventos.servicios;

import com.mycompany.administracioneventos.dao.ReporteDAO;
import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteServicio 
{
    private final ReporteDAO dao = new ReporteDAO();

    private void asegurarDirectorio(String dir) 
    {
        File carpeta = new File(dir);
        if (!carpeta.exists()) carpeta.mkdirs();
    }
    
    private String timestamp() 
    {
        return java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
   
    public ResultadoOperacion generarReporteParticipantes(String codigoEvento, String tipoParticipanteOpt, String institucionOpt, String directorioBase)
    {
        String dir = (directorioBase == null || directorioBase.isBlank())  ? "reportes/participantes" : directorioBase;  asegurarDirectorio(dir);
        var filas = dao.participantesPorEvento(codigoEvento, tipoParticipanteOpt, institucionOpt);
        String archivo = dir + File.separator + "participantes_" + codigoEvento + "_" + timestamp() + ".html";
        try (FileWriter w = new FileWriter(archivo))
        {
            w.write("<html><head><meta charset='UTF-8'><title>Reporte Participantes " + codigoEvento + "</title>");
            w.write("<style>table{border-collapse:collapse;width:100%;}th,td{border:1px solid #aaa;padding:6px;}th{background:#eee;}</style>");
            w.write("</head><body>");
            w.write("<h2>Participantes del evento " + codigoEvento + "</h2>");

            if (tipoParticipanteOpt != null && !tipoParticipanteOpt.isBlank()) {
                w.write("<p><strong>Filtro tipo:</strong> " + tipoParticipanteOpt + "</p>");
            }
            if (institucionOpt != null && !institucionOpt.isBlank()) {
                w.write("<p><strong>Filtro institución:</strong> " + institucionOpt + "</p>");
            }

            w.write("<table>");
            w.write("<tr>");
            w.write("<th>CORREO ELECTRÓNICO</th>");
            w.write("<th>TIPO</th>");
            w.write("<th>NOMBRE COMPLETO</th>");
            w.write("<th>INSTITUCIÓN DE PROCEDENCIA</th>");
            w.write("<th>FUE VALIDADO</th>");
            w.write("<th>MÉTODO DE PAGO</th>");
            w.write("<th>MONTO PAGADO</th>");
            w.write("</tr>");

            for (var r : filas) {
                w.write("<tr>");
                w.write("<td>" + r.correo + "</td>");
                w.write("<td>" + r.tipoParticipante + "</td>");
                w.write("<td>" + r.nombreCompleto + "</td>");
                w.write("<td>" + (r.institucion == null ? "" : r.institucion) + "</td>");
                w.write("<td>" + (r.validada ? "Sí" : "No") + "</td>");
                w.write("<td>" + (r.metodoPago == null || r.metodoPago.isBlank() ? "—" : r.metodoPago) + "</td>");
                w.write(String.format("<td>Q.%.2f</td>", r.montoPagado));
                w.write("</tr>");
            }

            w.write("</table>");
            w.write("</body></html>");
        }
        catch (IOException e) 
        {
            return ResultadoOperacion.fallo("Error al escribir reporte de actividades: " + e.getMessage());
        }
        return ResultadoOperacion.ok("Reporte generado: " + archivo);
    }
    
    public ResultadoOperacion generarReporteEventos(String tipoEventoOpt, LocalDate fechaIniOpt, LocalDate fechaFinOpt, Integer cupoIniOpt, Integer cupoFinOpt, String directorioBase)
    {
        boolean algunFiltro = (tipoEventoOpt != null && !tipoEventoOpt.isBlank()) // Validaciones de al menos un filtro y rangos completos
                || (fechaIniOpt != null && fechaFinOpt != null)
                || (cupoIniOpt != null && cupoFinOpt != null);
        if (!algunFiltro)
        {
            return ResultadoOperacion.fallo("Debe especificar al menos un filtro (tipo de evento, rango de fechas o rango de cupo).");
        }
        if ((fechaIniOpt != null && fechaFinOpt == null) || (fechaFinOpt != null && fechaIniOpt == null)) 
        {
            return ResultadoOperacion.fallo("Si filtras por fechas, debes indicar fecha inicial y final.");
        }
        String dir = (directorioBase == null || directorioBase.isBlank())
                ? "reportes/eventos"
                : directorioBase;
        asegurarDirectorio(dir);
        List<Evento> eventos = dao.eventosFiltrados(tipoEventoOpt, fechaIniOpt, fechaFinOpt, cupoIniOpt, cupoFinOpt);
        String archivo = dir + File.separator + "eventos_" + timestamp() + ".html";
        try (FileWriter w = new FileWriter(archivo)) 
        {
            w.write("<html><head><meta charset='UTF-8'><title>Reporte de Eventos</title>");
            w.write("<style>table{border-collapse:collapse;width:100%;}th,td{border:1px solid #aaa;padding:6px;}th{background:#eee;}"
                    + "h3{margin-top:24px;border-bottom:2px solid #333;padding-bottom:4px;}"
                    + ".totales{margin:10px 0; padding:8px; background:#f6f6f6; border:1px solid #ddd;}"
                    + "</style>");
            w.write("</head><body>");
            w.write("<h2>Reporte de eventos</h2>");

            // Muestra filtros aplicados
            if (tipoEventoOpt != null && !tipoEventoOpt.isBlank()) w.write("<p><strong>Tipo:</strong> " + tipoEventoOpt + "</p>");
            if (fechaIniOpt != null && fechaFinOpt != null) 
            {
                w.write("<p><strong>Rango de fechas:</strong> " + fechaIniOpt + " a " + fechaFinOpt + "</p>");
            }
            if (cupoIniOpt != null && cupoFinOpt != null) 
            {
                w.write("<p><strong>Rango de cupo:</strong> " + cupoIniOpt + " a " + cupoFinOpt + "</p>");
            }

            if (eventos.isEmpty()) 
            {
                w.write("<p><em>No hay eventos que cumplan los filtros.</em></p>");
                w.write("</body></html>");
                return ResultadoOperacion.ok("Reporte generado (sin resultados): " + archivo);
            }

            // Por cada evento la tabla de participantes + totales
            for (Evento ev : eventos) 
            {
                w.write("<h3>" + ev.getCodigo() + " — " + ev.getTitulo() + "</h3>");
                w.write("<p><strong>Fecha:</strong> " + ev.getFecha() +
                        " &nbsp; <strong>Tipo:</strong> " + (ev.getTipo() != null ? ev.getTipo().name() : "") +
                        " &nbsp; <strong>Ubicación:</strong> " + ev.getUbicacion() +
                        " &nbsp; <strong>Cupo Máximo:</strong> " + ev.getCupoMaximo() + "</p>");

                var filas = dao.participantesConPagosPorEvento(ev.getCodigo());

                w.write("<table>");
                w.write("<tr>");
                w.write("<th>CÓDIGO DE EVENTO</th>");
                w.write("<th>FECHA DE EVENTO</th>");
                w.write("<th>TÍTULO DEL EVENTO</th>");
                w.write("<th>TIPO DE EVENTO</th>");
                w.write("<th>UBICACIÓN</th>");
                w.write("<th>CUPO MÁXIMO</th>");
                w.write("<th>CORREO DEL PARTICIPANTE</th>");
                w.write("<th>NOMBRE DEL PARTICIPANTE</th>");
                w.write("<th>TIPO DE PARTICIPANTE</th>");
                w.write("<th>MÉTODO DE PAGO</th>");
                w.write("<th>MONTO PAGADO</th>");
                w.write("</tr>");

                for (var p : filas) 
                {
                    w.write("<tr>");
                    w.write("<td>" + ev.getCodigo() + "</td>");
                    w.write("<td>" + ev.getFecha() + "</td>");
                    w.write("<td>" + ev.getTitulo() + "</td>");
                    w.write("<td>" + (ev.getTipo() != null ? ev.getTipo().name() : "") + "</td>");
                    w.write("<td>" + ev.getUbicacion() + "</td>");
                    w.write("<td>" + ev.getCupoMaximo() + "</td>");
                    w.write("<td>" + p.correo + "</td>");
                    w.write("<td>" + p.nombreCompleto + "</td>");
                    w.write("<td>" + p.tipoParticipante + "</td>");
                    w.write("<td>" + (p.metodoPago == null || p.metodoPago.isBlank() ? "—" : p.metodoPago) + "</td>");
                    w.write(String.format("<td>Q.%.2f</td>", p.montoPagado));
                    w.write("</tr>");
                }
                w.write("</table>");

                double total = dao.montoTotalEvento(ev.getCodigo());
                int val = dao.contarValidados(ev.getCodigo());
                int noval = dao.contarNoValidados(ev.getCodigo());

                w.write("<div class='totales'>");
                w.write(String.format("<p><strong>MONTO TOTAL:</strong> Q.%.2f</p>", total));
                w.write("<p><strong>PARTICIPANTES VALIDADOS:</strong> " + val + "</p>");
                w.write("<p><strong>PARTICIPANTES NO VALIDADOS:</strong> " + noval + "</p>");
                w.write("</div>");
            }

            w.write("</body></html>");
        } 
        catch (IOException e) 
        {
            return ResultadoOperacion.fallo("Error al escribir reporte de eventos: " + e.getMessage());
        }
        return ResultadoOperacion.ok("Reporte generado: " + archivo);
    }
    
    public ResultadoOperacion generarReporteActividades(String codigoEvento, String tipoActividadOpt, String correoEncargadoOpt, String directorioBase) 
    {
        String dir = (directorioBase == null || directorioBase.isBlank()) ? "reportes/actividades" : directorioBase;
        asegurarDirectorio(dir);

        var filas = dao.actividadesPorEvento(codigoEvento, tipoActividadOpt, correoEncargadoOpt);

        String archivo = dir + File.separator + "actividades_" + codigoEvento + "_" + timestamp() + ".html";
        try (FileWriter w = new FileWriter(archivo)) 
        {
            w.write("<html><head><meta charset='UTF-8'><title>Reporte Actividades " + codigoEvento + "</title>");
            w.write("<style>table{border-collapse:collapse;width:100%;}th,td{border:1px solid #aaa;padding:6px;}th{background:#eee;}</style>");
            w.write("</head><body>");
            w.write("<h2>Actividades del evento " + codigoEvento + "</h2>");

            if (tipoActividadOpt != null && !tipoActividadOpt.isBlank()) 
            {
                w.write("<p><strong>Filtro tipo actividad:</strong> " + tipoActividadOpt + "</p>");
            }
            if (correoEncargadoOpt != null && !correoEncargadoOpt.isBlank()) 
            {
                w.write("<p><strong>Filtro encargado:</strong> " + correoEncargadoOpt + "</p>");
            }

            w.write("<table>");
            w.write("<tr>");
            w.write("<th>CÓDIGO DE LA ACTIVIDAD</th>");
            w.write("<th>CÓDIGO DEL EVENTO</th>");
            w.write("<th>TÍTULO DE LA ACTIVIDAD</th>");
            w.write("<th>CORREO DEL ENCARGADO</th>");
            w.write("<th>HORA DE INICIO</th>");
            w.write("<th>HORA DE FIN</th>");
            w.write("<th>CUPO MÁXIMO</th>");
            w.write("<th>CANTIDAD DE PARTICIPANTES</th>");
            w.write("</tr>");

            for (var a : filas) 
            {
                w.write("<tr>");
                w.write("<td>" + (a.actividadCodigo == null ? "" : a.actividadCodigo) + "</td>");
                w.write("<td>" + (a.eventoCodigo    == null ? "" : a.eventoCodigo)    + "</td>");
                w.write("<td>" + (a.titulo          == null ? "" : a.titulo)          + "</td>");
                w.write("<td>" + (a.encargadoCorreo == null ? "" : a.encargadoCorreo) + "</td>");
                w.write("<td>" + (a.horaInicio      == null ? "" : a.horaInicio)      + "</td>");
                w.write("<td>" + (a.horaFin         == null ? "" : a.horaFin)         + "</td>");
                w.write("<td>" + a.cupo + "</td>");
                w.write("<td>" + a.asistencias + "</td>");
                w.write("</tr>");
            }

            w.write("</table>");
            w.write("</body></html>");
        } 
        catch (IOException e) 
        {
            return ResultadoOperacion.fallo("Error al escribir reporte de actividades: " + e.getMessage());
        }

        return ResultadoOperacion.ok("Reporte generado: " + archivo);
    }
}
