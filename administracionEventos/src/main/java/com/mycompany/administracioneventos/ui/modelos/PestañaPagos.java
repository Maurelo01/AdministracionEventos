/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.administracioneventos.ui.modelos;

import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.modelos.MetodoPago;
import com.mycompany.administracioneventos.modelos.Pago;
import com.mycompany.administracioneventos.modelos.Participante;
import com.mycompany.administracioneventos.servicios.EventoServicio;
import com.mycompany.administracioneventos.servicios.InscripcionServicio;
import com.mycompany.administracioneventos.servicios.PagoServicio;
import com.mycompany.administracioneventos.servicios.ParticipanteServicio;
import com.mycompany.administracioneventos.ui.util.Alertas;
import com.mycompany.administracioneventos.ui.util.ValidacionUtil;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.util.List;
import javax.swing.table.DefaultTableModel;


public class PestañaPagos extends javax.swing.JPanel 
{

    private final PagoServicio servicioPago = new PagoServicio();
    private final ParticipanteServicio servicioParticipante = new ParticipanteServicio();
    private final EventoServicio servicioEvento = new EventoServicio();
    private final Evento evento;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Correo", "Método", "Monto"}, 0) 
    {
        @Override public boolean isCellEditable(int r,int c)
        {
            return false;
        }
    };
    public PestañaPagos(java.awt.Window parent, Evento evento)
    {
        initComponents();
        this.evento = evento;
        tablaPagos.setModel(modelo);
        cboMetodoPago.removeAllItems();
        for (MetodoPago m : MetodoPago.values()) cboMetodoPago.addItem(m.name());
        recargar();
    }
    
    public final void recargar() 
    {
        modelo.setRowCount(0);
        try 
        {
            List<Pago> lista = servicioPago.listarPagoPorEvento(evento.getCodigo());
            for (Pago p : lista) {
                modelo.addRow(new Object[]{
                    p.getParticipante().getCorreo(),
                    p.getMetodo()!=null ? p.getMetodo().name() : "",
                    p.getMonto()
                });
            }
        }
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al cargar pagos: " + ex.getMessage());
        }
    }
    
    public void onRegistrar() {
        String correo = txtCorreoPago.getText().trim();
        String metodoStr = (String) cboMetodoPago.getSelectedItem();
        String montoStr = txtMontoPago.getText().trim();

        if (!ValidacionUtil.esCorreoValido(correo)) 
        { 
            Alertas.error(this, "Correo inválido."); 
            return; 
        }
        if (metodoStr == null || metodoStr.isBlank()) 
        { 
            Alertas.error(this, "Seleccione un método."); 
            return; 
        }
        double monto;
        try 
        { 
            monto = Double.parseDouble(montoStr); 
        } 
        catch (NumberFormatException e) 
        { 
            Alertas.error(this, "Monto inválido."); 
            return; 
        }
        if (monto <= 0) 
        { 
            Alertas.error(this, "El monto debe ser > 0."); 
            return; 
        }
        try 
        {
            Participante participante = servicioParticipante.buscarParticipantePorCorreo(correo);
            if (participante == null) 
            { 
                Alertas.error(this, "Participante no existe."); 
                return; 
            }
            Evento ev = servicioEvento.buscarEventoPorCodigo(evento.getCodigo());
            if (ev == null) 
            { 
                Alertas.error(this, "Evento no encontrado."); 
                return; 
            }
            MetodoPago metodo = MetodoPago.valueOf(metodoStr);
            Pago pago = new Pago(participante, ev, metodo, monto);
            boolean ok = servicioPago.registrarPago(pago);
            if (ok) 
            {
                try 
                { 
                    new InscripcionServicio().validarInscripcion(correo, evento.getCodigo()); 
                } 
                catch (Exception ignore) {}
                Alertas.informacion(this, "Pago registrado.");
                recargar();
            } 
            else 
            {
                Alertas.error(this, "No se pudo registrar el pago.");
            }
        } 
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al registrar pago: " + ex.getMessage());
        }
    }
    
    public void onEliminar()
    {
        int fila = tablaPagos.getSelectedRow(); 
        if (fila < 0)
        {
            Alertas.informacion(this, "Seleccione un pago."); 
            return;
        }
        String correo = (String) modelo.getValueAt(fila, 0);
        if (!Alertas.confirmar(this, "¿Eliminar el pago de " + correo + "?")) return;
        ResultadoOperacion rop = servicioPago.eliminarPagoSeguro(correo, evento.getCodigo());
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtCorreoPago = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cboMetodoPago = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtMontoPago = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaPagos = new javax.swing.JTable();

        jLabel1.setText("Correo:");

        txtCorreoPago.setColumns(28);

        jLabel2.setText("Método:");

        cboMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Monto:");

        txtMontoPago.setColumns(28);
        txtMontoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoPagoActionPerformed(evt);
            }
        });

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnRefrescar.setText("Refrescar");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        tablaPagos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaPagos);

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
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCorreoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtMontoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnRegistrar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefrescar)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCorreoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtMontoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnEliminar)
                    .addComponent(btnRefrescar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtMontoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoPagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMontoPagoActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        onRegistrar();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        onEliminar();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        recargar();
    }//GEN-LAST:event_btnRefrescarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cboMetodoPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaPagos;
    private javax.swing.JTextField txtCorreoPago;
    private javax.swing.JTextField txtMontoPago;
    // End of variables declaration//GEN-END:variables
}
