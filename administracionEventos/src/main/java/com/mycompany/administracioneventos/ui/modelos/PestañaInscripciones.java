/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.administracioneventos.ui.modelos;

import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.Inscripcion;
import com.mycompany.administracioneventos.modelos.TipoInscripcion;
import com.mycompany.administracioneventos.servicios.InscripcionServicio;
import com.mycompany.administracioneventos.servicios.ParticipanteServicio;
import com.mycompany.administracioneventos.servicios.ReporteServicio;
import com.mycompany.administracioneventos.ui.util.Alertas;
import com.mycompany.administracioneventos.ui.util.ValidacionUtil;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.awt.Desktop;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PestañaInscripciones extends javax.swing.JPanel 
{
    private final InscripcionServicio servicioInscripcion = new InscripcionServicio();
    private final ParticipanteServicio servicioParticipante = new ParticipanteServicio();
    private final Evento evento;
    private final ReporteServicio servicioReporte = new ReporteServicio();

    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Correo", "Tipo", "Validada"}, 0)
    {
        @Override
        public boolean isCellEditable(int r,int c)
        {
            return false;
        }
    };
    
    public PestañaInscripciones(java.awt.Window parent, Evento evento) 
    {
        initComponents();
        this.evento = evento;
        tablaInscripciones.setModel(modelo);
        cboTipoInscripcion.removeAllItems();
        for (TipoInscripcion tipo : TipoInscripcion.values()) 
        {
            cboTipoInscripcion.addItem(tipo.name());
        }
        recargar();
    }
    
    public final void recargar() 
    {
        modelo.setRowCount(0);
        try
        {
            List<Inscripcion> lista = servicioInscripcion.obtenerInscripciones();
            for (Inscripcion i : lista)
            {
                modelo.addRow(new Object[]{i.getParticipante().getCorreo(), i.getTipo()!=null? i.getTipo().name() : "", i.isValidada()? "Sí":"No"});
            }
        }
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al cargar inscripciones: " + ex.getMessage());
        }
    }
    
    public void onAgregar()
    {
        String correo = txtCorreoInscripcion.getText().trim();
        String tipoStr = (String) cboTipoInscripcion.getSelectedItem();
        if (!ValidacionUtil.esCorreoValido(correo))
        {
            Alertas.error(this, "Correo inválido.");
            return;
        }
        if (tipoStr == null || tipoStr.isBlank())
        {
            Alertas.error(this, "Seleccione un tipo.");
            return;
        }
        TipoInscripcion tipo = TipoInscripcion.valueOf(tipoStr);
        try 
        {
            if (servicioParticipante.buscarParticipantePorCorreo(correo) == null) 
            {
                Alertas.error(this, "El participante no existe."); return;
            }
            boolean ok = servicioInscripcion.registrarInscripcion(correo, evento.getCodigo(), tipo);
            if (ok) 
            {
                Alertas.informacion(this, "Inscripción creada.");
                recargar();
            } 
            else 
            {
                Alertas.error(this, "No se pudo crear");
            }
        } 
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al agregar: " + ex.getMessage());
        }
    }
    
    public void onValidar()
    {
        int fila = tablaInscripciones.getSelectedRow();
        if (fila < 0)
        {
            Alertas.informacion(this, "Seleccione una inscripción.");
            return;
        }
        String correo = (String) modelo.getValueAt(fila, 0);
        try
        {
            boolean ok = servicioInscripcion.validarInscripcion(correo, evento.getCodigo());
            if (ok)
            {
                Alertas.informacion(this, "Inscripción validada."); 
                recargar();
            }
            else    
            { 
                Alertas.error(this, "No se pudo validar (pago/cupo)."); 
            }
        }
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al validar: " + ex.getMessage());
        }
    }
    
    public void onEliminar()
    {
        int fila = tablaInscripciones.getSelectedRow();
        if (fila < 0)
        {
            Alertas.informacion(this, "Seleccione una inscripción."); 
            return;
        }
        String correo = (String) modelo.getValueAt(fila, 0);
        if (!Alertas.confirmar(this, "¿Eliminar la inscripción de " + correo + "?")) return;
        try
        {
            ResultadoOperacion rop = servicioInscripcion.eliminarInscripcionSeguro(correo, evento.getCodigo());
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
            Alertas.error(this, "Error al eliminar: " + ex.getMessage());
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

        jLabel1 = new javax.swing.JLabel();
        txtCorreoInscripcion = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboTipoInscripcion = new javax.swing.JComboBox<>();
        btnAgregarInscripcion = new javax.swing.JButton();
        btnValidarInscripcion = new javax.swing.JButton();
        btnEliminarInscripcion = new javax.swing.JButton();
        btnRefrescarInscripciones = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaInscripciones = new javax.swing.JTable();
        btnReporteParticipante = new javax.swing.JButton();

        jLabel1.setText("Correo:");

        txtCorreoInscripcion.setColumns(28);
        txtCorreoInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorreoInscripcionActionPerformed(evt);
            }
        });

        jLabel2.setText("Tipo:");

        cboTipoInscripcion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAgregarInscripcion.setText("Agregar");
        btnAgregarInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarInscripcionActionPerformed(evt);
            }
        });

        btnValidarInscripcion.setText("Validar");
        btnValidarInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidarInscripcionActionPerformed(evt);
            }
        });

        btnEliminarInscripcion.setText("Eliminar");
        btnEliminarInscripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarInscripcionActionPerformed(evt);
            }
        });

        btnRefrescarInscripciones.setText("Refrescar");
        btnRefrescarInscripciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarInscripcionesActionPerformed(evt);
            }
        });

        tablaInscripciones.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaInscripciones);

        btnReporteParticipante.setText("Generar Reporte");
        btnReporteParticipante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteParticipanteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCorreoInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboTipoInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAgregarInscripcion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnValidarInscripcion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminarInscripcion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefrescarInscripciones)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReporteParticipante)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCorreoInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboTipoInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarInscripcion)
                    .addComponent(btnValidarInscripcion)
                    .addComponent(btnEliminarInscripcion)
                    .addComponent(btnRefrescarInscripciones)
                    .addComponent(btnReporteParticipante))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCorreoInscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoInscripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoInscripcionActionPerformed

    private void btnAgregarInscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarInscripcionActionPerformed
        onAgregar();
    }//GEN-LAST:event_btnAgregarInscripcionActionPerformed

    private void btnValidarInscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidarInscripcionActionPerformed
        onValidar();
    }//GEN-LAST:event_btnValidarInscripcionActionPerformed

    private void btnEliminarInscripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarInscripcionActionPerformed
        onEliminar();
    }//GEN-LAST:event_btnEliminarInscripcionActionPerformed

    private void btnRefrescarInscripcionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarInscripcionesActionPerformed
        recargar();
    }//GEN-LAST:event_btnRefrescarInscripcionesActionPerformed

    private void btnReporteParticipanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteParticipanteActionPerformed
        if (evento == null || evento.getCodigo() == null)
        {
            Alertas.error(this, "No hay evento seleccionado.");
            return;
        }
        // Carpeta de salida
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Elegir carpeta de salida");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;
        String dir = fc.getSelectedFile().getAbsolutePath();
        ResultadoOperacion res = servicioReporte.generarReporteParticipantes(evento.getCodigo(), "", "", dir);
        if (res.isOk())
        {
            Alertas.informacion(this, res.getMensaje());
            try 
            { 
                Desktop.getDesktop().open(new File(dir)); 
            }
            catch (Exception ignore) {}
        }
        else 
        {
            Alertas.error(this, res.getMensaje());
        }
    }//GEN-LAST:event_btnReporteParticipanteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarInscripcion;
    private javax.swing.JButton btnEliminarInscripcion;
    private javax.swing.JButton btnRefrescarInscripciones;
    private javax.swing.JButton btnReporteParticipante;
    private javax.swing.JButton btnValidarInscripcion;
    private javax.swing.JComboBox<String> cboTipoInscripcion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaInscripciones;
    private javax.swing.JTextField txtCorreoInscripcion;
    // End of variables declaration//GEN-END:variables
}
