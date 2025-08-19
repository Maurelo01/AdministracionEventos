/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.administracioneventos.ui.modelos;

import com.mycompany.administracioneventos.modelos.Actividad;
import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.Inscripcion;
import com.mycompany.administracioneventos.modelos.TipoActividad;
import com.mycompany.administracioneventos.modelos.TipoInscripcion;
import com.mycompany.administracioneventos.modelos.TipoParticipante;
import com.mycompany.administracioneventos.servicios.ActividadServicio;
import com.mycompany.administracioneventos.servicios.InscripcionServicio;
import com.mycompany.administracioneventos.ui.util.Alertas;
import com.mycompany.administracioneventos.ui.util.ValidacionUtil;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.time.LocalTime;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mauricio
 */
public class PestañaActividades extends javax.swing.JPanel {

    private final ActividadServicio servicioActividad = new ActividadServicio();
    private final InscripcionServicio servicioInscripcion = new InscripcionServicio();
    private final Evento evento;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Evento", "Código", "Tipo", "Título", "Encargado", "Inicio", "Fin", "Cupo"}, 0) 
    {
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    
    public PestañaActividades(java.awt.Window parent, Evento evento) 
    {
        initComponents();
        this.evento = evento;
        tablaActividades.setModel(modelo);
        tablaActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActividades.setAutoCreateRowSorter(true); 
        tablaActividades.setRowHeight(22);
        cboTipo.removeAllItems();
        for (TipoActividad tipo : TipoActividad.values()) cboTipo.addItem(tipo.name());
    }
    
    public final void recargar() 
    {
        modelo.setRowCount(0);
        try 
        {
            List<Actividad> lista = servicioActividad.listarActividades();
            for (Actividad actividades : lista) 
            {
                modelo.addRow(new Object[]
                {
                    (actividades.getEvento()!=null ? actividades.getEvento().getCodigo() : ""),                  // Evento
                    actividades.getCodigoActividad(),                                                  // Código
                    (actividades.getTipo()!=null ? actividades.getTipo().name() : ""),                           // Tipo
                    actividades.getTitulo(),                                                           // Título
                    (actividades.getEncargado()!=null && actividades.getEncargado().getParticipante()!=null
                        ? actividades.getEncargado().getParticipante().getCorreo() : ""),             // Encargado
                    (actividades.getHoraInicio()!=null ? actividades.getHoraInicio().toString() : ""),           // Inicio
                    (actividades.getHoraFin()!=null ? actividades.getHoraFin().toString() : ""),                 // Fin
                    actividades.getCupoMaximo()                                                        // Cupo
                });
            }
        } 
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al cargar actividades: " + ex.getMessage());
        }
    }
    
    public void onAgregar() 
    {
        String codigo = txtCodigo.getText().trim();
        String titulo = txtTitulo.getText().trim();
        String tipoStr = (String) cboTipo.getSelectedItem();
        String correoEnc = txtEncargadoCorreo.getText().trim();
        LocalTime inicio = ValidacionUtil.parsearHora(txtHoraInicio.getText());
        LocalTime fin = ValidacionUtil.parsearHora(txtHoraFin.getText());
        int cupo = (Integer) spnCupo.getValue();

        if (codigo.isEmpty()) 
        { 
            Alertas.error(this, "El código es obligatorio."); 
            return; 
        }
        if (titulo.isEmpty()) 
        { 
            Alertas.error(this, "El título es obligatorio.");
            return; 
        }
        if (tipoStr == null || tipoStr.isBlank()) 
        { 
            Alertas.error(this, "Seleccione un tipo de actividad."); 
            return; 
        }
        if (!ValidacionUtil.esCorreoValido(correoEnc)) 
        { 
            Alertas.error(this, "Correo de encargado inválido."); 
            return; 
        }
        if (inicio == null || fin == null) 
        { 
            Alertas.error(this, "Formato de hora inválido (use HH:mm)."); 
            return; 
        }
        if (!fin.isAfter(inicio)) 
        { 
            Alertas.error(this, "La hora fin debe ser posterior a la hora inicio."); 
            return; 
        }
        if (cupo <= 0) 
        { 
            Alertas.error(this, "El cupo debe ser > 0."); 
            return; 
        }

        try 
        {
            Inscripcion enc = servicioInscripcion.buscarInscripcion(correoEnc, evento.getCodigo());
            if (enc == null) 
            { 
                Alertas.error(this, "El encargado no está inscrito en el evento."); 
                return; 
            }
            if (enc.getTipo() == TipoInscripcion.ASISTENTE) 
            { 
                Alertas.error(this, "El encargado no puede ser ASISTENTE.");
                return; 
            }
            if (!enc.isValidada()) 
            { 
                Alertas.error(this, "La inscripción del encargado debe estar validada."); 
                return; 
            }

            TipoActividad tipo = TipoActividad.valueOf(tipoStr);
            Actividad act = new Actividad(codigo, evento, tipo, titulo, enc, inicio, fin, cupo);

            boolean ok = servicioActividad.registrarActividad(codigo, evento.getCodigo(), tipo, titulo, enc.getParticipante().getCorreo(), inicio, fin, cupo);
            if (ok) 
            {
                Alertas.informacion(this, "Actividad creada.");
                recargar();
                txtCodigo.setText("");
                txtTitulo.setText("");
                txtEncargadoCorreo.setText("");
                txtHoraInicio.setText("");
                txtHoraFin.setText("");
                spnCupo.setValue(1);
            } 
            else 
            {
                Alertas.error(this, "No se pudo crear la actividad (¿código duplicado?).");
            }
        } 
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al crear actividad: " + ex.getMessage());
        }
    }
    
    public void onEliminar() 
    {
        int fila = tablaActividades.getSelectedRow();
        if (fila < 0) 
        { 
            Alertas.informacion(this, "Seleccione una actividad en la tabla."); 
            return; 
        }
        String codigo = (String) modelo.getValueAt(fila, 0);
        if (!Alertas.confirmar(this, "¿Eliminar la actividad " + codigo + "?")) return;

        try 
        {
            ResultadoOperacion rop = servicioActividad.eliminarActividadSeguro(codigo);
            if (rop.isOk()) 
            { 
                Alertas.informacion(this, rop.getMensaje());
                recargar();
            }
            else 
            { 
                Alertas.error(this, rop.getMensaje()); 
            }
        }
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al eliminar actividad: " + ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtEncargadoCorreo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtHoraInicio = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtHoraFin = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        spnCupo = new javax.swing.JSpinner();
        btnAgregar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        aaaa = new javax.swing.JScrollPane();
        tablaActividades = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();

        jLabel1.setText("Código:");

        txtCodigo.setColumns(28);

        jLabel2.setText("Titulo:");

        txtTitulo.setColumns(28);
        txtTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTituloActionPerformed(evt);
            }
        });

        jLabel3.setText("Correo Encargado:");

        txtEncargadoCorreo.setColumns(28);

        jLabel4.setText("Hora inicio:");

        txtHoraInicio.setColumns(28);

        jLabel5.setText("Hora fin:");

        txtHoraFin.setColumns(28);

        jLabel6.setText("Cupo Máximo:");

        spnCupo.setModel(new javax.swing.SpinnerNumberModel(1, 1, 1000, 1));

        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnRefrescar.setText("Refrescar");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        tablaActividades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        aaaa.setViewportView(tablaActividades);

        jLabel7.setText("Tipo:");

        cboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTitulo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(aaaa)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(110, 110, 110)
                                .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAgregar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefrescar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEncargadoCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spnCupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtEncargadoCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(spnCupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregar)
                    .addComponent(btnRefrescar)
                    .addComponent(btnEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(aaaa, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTituloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTituloActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        onAgregar();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        recargar();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        onEliminar();
    }//GEN-LAST:event_btnEliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane aaaa;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spnCupo;
    private javax.swing.JTable tablaActividades;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtEncargadoCorreo;
    private javax.swing.JTextField txtHoraFin;
    private javax.swing.JTextField txtHoraInicio;
    private javax.swing.JTextField txtTitulo;
    // End of variables declaration//GEN-END:variables
}
