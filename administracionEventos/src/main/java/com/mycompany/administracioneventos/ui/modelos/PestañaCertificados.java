/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.administracioneventos.ui.modelos;

import com.mycompany.administracioneventos.modelos.Certificado;
import com.mycompany.administracioneventos.modelos.Evento;
import com.mycompany.administracioneventos.servicios.CertificadoServicio;
import com.mycompany.administracioneventos.ui.util.Alertas;
import com.mycompany.administracioneventos.ui.util.ValidacionUtil;
import com.mycompany.administracioneventos.util.ResultadoOperacion;
import java.awt.Desktop;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mauricio
 */
public class PestañaCertificados extends javax.swing.JPanel 
{
    private final CertificadoServicio servicioCertificado = new CertificadoServicio();
    private final Evento evento;
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"Evento", "Correo", "Fecha emisión", "Ruta archivo"}, 0)
    {
        @Override public boolean isCellEditable(int r, int c)
        {
            return false;
        }
    };
    
    public PestañaCertificados(java.awt.Window parent, Evento evento) 
    {
        initComponents();
        this.evento = evento;
        tablaCertificados.setModel(modelo);
        tablaCertificados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCertificados.setAutoCreateRowSorter(true);
        tablaCertificados.setRowHeight(22);
        if (txtDirectorioSalida.getText() == null || txtDirectorioSalida.getText().isBlank())
        {
            txtDirectorioSalida.setText("reportes/certificados");
        }
        chkSoloEvento.setSelected(true);
        recargar();
    }
    
    public final void recargar() 
    {
        modelo.setRowCount(0);
        try
        {
            if (chkSoloEvento.isSelected())
            {
                List<Certificado> lista = servicioCertificado.listarPorEvento(evento.getCodigo());
                for (Certificado c : lista)
                {
                    modelo.addRow(new Object[]{(c.getEvento() != null ? c.getEvento().getCodigo() : evento.getCodigo()), 
                        (c.getParticipante() != null ? c.getParticipante().getCorreo() : ""), (c.getFechaEmision() != null ? c.getFechaEmision().toString() : ""), 
                        c.getRutaArchivo()});
                }
            }
            else
            {
                List<Certificado> lista = servicioCertificado.listarTodos();
                for (Certificado c : lista)
                {
                    modelo.addRow(new Object[]{(c.getEvento() != null ? c.getEvento().getCodigo() : evento.getCodigo()), 
                        (c.getParticipante() != null ? c.getParticipante().getCorreo() : ""), (c.getFechaEmision() != null ? c.getFechaEmision().toString() : ""), 
                        c.getRutaArchivo()});
                }
            }
        }
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al cargar certificados: " + ex.getMessage());
        }
    }
    
    public void onEmitir() 
    {
        String correo = txtCorreo.getText().trim();
        String dir = txtDirectorioSalida.getText().trim();
        if (!ValidacionUtil.esCorreoValido(correo))
        {
            Alertas.error(this, "Correo inválido.");
            return;
        }
        if (dir.isBlank())
        {
            Alertas.error(this, "Indique directorio de salida.");
            return;
        }
        try
        {
            var cert = servicioCertificado.generarYRegistrarCertificado(correo, evento.getCodigo(), dir, true);
            if (cert != null)
            {
                Alertas.informacion(this, "Certificado emitido: " + cert.getRutaArchivo());
                recargar();
            }
            else
            {
                Alertas.error(this, "No se pudo emitir.");
            }
        }
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al emitir certificado: " + ex.getMessage());
        }
    }
    
    public void onAbrir() 
    {
        int fila = tablaCertificados.getSelectedRow();
        if (fila < 0) 
        { 
            Alertas.informacion(this, "Seleccione un certificado."); 
            return; 
        }
        int row = tablaCertificados.convertRowIndexToModel(fila);
        String ruta = (String) modelo.getValueAt(row, 3);
        try 
        {
            File f = new File(ruta);
            if (!f.exists()) 
            {
                Alertas.error(this, "Archivo no encontrado: " + ruta); 
                return; 
            }
            Desktop.getDesktop().open(f);
        } 
        catch (Exception ex) 
        {
            Alertas.error(this, "No se pudo abrir: " + ex.getMessage());
        }
    }
    
    public void onEliminar() 
    {
        int fila = tablaCertificados.getSelectedRow();
        if (fila < 0) 
        { 
            Alertas.informacion(this, "Seleccione un certificado."); 
            return; 
        }
        int row = tablaCertificados.convertRowIndexToModel(fila);

        String evCodigo = (String) modelo.getValueAt(row, 0); // Evento
        String correo = (String) modelo.getValueAt(row, 1);   // Correo
        if (!Alertas.confirmar(this, "¿Eliminar certificado de " + correo + " en " + evCodigo + "?")) return;
        try 
        {
            ResultadoOperacion ok = servicioCertificado.eliminarCertificadoSeguro(correo, evCodigo);
            if (ok.isOk()) 
            { 
                Alertas.informacion(this, "Certificado eliminado."); 
                recargar(); 
            }
            else
            { 
                Alertas.error(this, "No se pudo eliminar el certificado."); 
            }
        } 
        catch (Exception ex) 
        {
            Alertas.error(this, "Error al eliminar: " + ex.getMessage());
        }
    }
    
    public void onExaminarRuta() 
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            File dir = fc.getSelectedFile();
            txtDirectorioSalida.setText(dir.getAbsolutePath());
        }
    }
    
    public void onToggleSoloEvento() 
    {
        recargar();
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
        txtCorreo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDirectorioSalida = new javax.swing.JTextField();
        btnExaminarRuta = new javax.swing.JButton();
        chkSoloEvento = new javax.swing.JCheckBox();
        btnEmitir = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCertificados = new javax.swing.JTable();

        jLabel1.setText("Correo:");

        txtCorreo.setColumns(28);

        jLabel2.setText("Directorio de Salida:");

        txtDirectorioSalida.setColumns(28);

        btnExaminarRuta.setText("Examinar");
        btnExaminarRuta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExaminarRutaActionPerformed(evt);
            }
        });

        chkSoloEvento.setSelected(true);
        chkSoloEvento.setText("Solo este evento");

        btnEmitir.setText("Emitir");
        btnEmitir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmitirActionPerformed(evt);
            }
        });

        btnAbrir.setText("Abrir");
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
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

        tablaCertificados.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaCertificados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExaminarRuta))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtDirectorioSalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkSoloEvento)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEmitir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAbrir)
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
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDirectorioSalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExaminarRuta)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSoloEvento)
                    .addComponent(btnEmitir)
                    .addComponent(btnAbrir)
                    .addComponent(btnEliminar)
                    .addComponent(btnRefrescar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnExaminarRutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExaminarRutaActionPerformed
        onExaminarRuta();
    }//GEN-LAST:event_btnExaminarRutaActionPerformed

    private void btnEmitirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmitirActionPerformed
        onEmitir();
    }//GEN-LAST:event_btnEmitirActionPerformed

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        onAbrir();
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        onEliminar();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        recargar();
    }//GEN-LAST:event_btnRefrescarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEmitir;
    private javax.swing.JButton btnExaminarRuta;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JCheckBox chkSoloEvento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaCertificados;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDirectorioSalida;
    // End of variables declaration//GEN-END:variables
}
