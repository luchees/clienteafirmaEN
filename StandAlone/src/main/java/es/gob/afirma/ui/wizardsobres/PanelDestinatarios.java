/*
 * Este fichero forma parte del Cliente @firma.
 * El Cliente @firma es un applet de libre distribuci�n cuyo c�digo fuente puede ser consultado
 * y descargado desde www.ctt.map.es.
 * Copyright 2009,2010 Ministerio de la Presidencia, Gobierno de Espa�a (opcional: correo de contacto)
 * Este fichero se distribuye bajo las licencias EUPL versi�n 1.1  y GPL versi�n 3  seg�n las
 * condiciones que figuran en el fichero 'licence' que se acompa�a.  Si se   distribuyera este
 * fichero individualmente, deben incluirse aqu� las condiciones expresadas all�.
 */
package es.gob.afirma.ui.wizardsobres;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.security.auth.callback.PasswordCallback;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import es.gob.afirma.callbacks.NullPasswordCallback;
import es.gob.afirma.callbacks.UIPasswordCallback;
import es.gob.afirma.exceptions.AOCancelledOperationException;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.KeyStoreConfiguration;
import es.gob.afirma.misc.AOConstants;
import es.gob.afirma.misc.AOConstants.AOKeyStore;
import es.gob.afirma.ui.utils.HelpUtils;
import es.gob.afirma.ui.utils.KeyStoreLoader;
import es.gob.afirma.ui.utils.Messages;
import es.gob.afirma.ui.wizardUtils.BotoneraInferior;
import es.gob.afirma.ui.wizardUtils.CabeceraAsistente;
import es.gob.afirma.ui.wizardUtils.CertificateDestiny;
import es.gob.afirma.ui.wizardUtils.JDialogWizard;
import es.gob.afirma.ui.wizardUtils.PanelesTexto;

/**
 *
 * Clase que contiene los elementos necesarios para crear un grupo de destinatarios
 * a partir de una seleccion de certificados de destinatarios.
 */
public class PanelDestinatarios extends JDialogWizard {

	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(PanelDestinatarios.class.getName());
	
	private List<CertificateDestiny> listaCertificados = new ArrayList<CertificateDestiny>();
	
	/**
	 * Guarda todas las ventanas del asistente para poder controlar la botonera
	 * @param ventanas	Listado con todas las paginas del asistente
	 */
	public void setVentanas(List<JDialogWizard> ventanas) {
		Botonera botonera = new Botonera(ventanas, 1);
		getContentPane().add(botonera, BorderLayout.PAGE_END);
	}
	
	public PanelDestinatarios() {
		initComponents();
	}
	
	// Lista con los destinatarios
	private JList listaDestinatarios = new JList();
	
	/**
	 * Inicializacion de componentes
	 */
	private void initComponents() {
		// Titulo de la ventana
    	setTitulo(Messages.getString("Wizard.sobres.titulo"));
		
		// Panel con la cabecera
        CabeceraAsistente panelSuperior = new CabeceraAsistente("Wizard.sobres.pagina1.titulo", "Wizard.sobres.pagina1.titulo.explicacion", null, true);
        getContentPane().add(panelSuperior, BorderLayout.NORTH);
		
        // Panel central
    	JPanel panelCentral = new JPanel();
    	panelCentral.setMinimumSize(new Dimension(603, 289));
    	panelCentral.setLayout(new GridBagLayout());
        
    	// Configuramos el layout
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 20, 0, 20);
        c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridx = 0;
    	
    	// Panel con el texto "Puede anadir uno o..."
		JTextPane panelTexto = PanelesTexto.generarPanelTexto("Wizard.sobres.pagina1.contenido.explicacion1", false);
		panelCentral.add(panelTexto, c);
		
		c.gridy = 1;
		c.gridwidth = 1;
		
        // Etiqueta con el texto "Anadir un destinatario..."
        JLabel etiquetaAnadir = new JLabel();
        etiquetaAnadir.setText(Messages.getString("wizard.sobres.aniadir.destinatario"));
		panelCentral.add(etiquetaAnadir, c);
		
		c.insets = new Insets(0, 20, 0, 0);
		c.gridwidth = 1;
		c.gridy = 2;
		c.weightx = 1.0;
		
		// Combo con las listas de destinatarios
		final JComboBox comboDestinatarios = new JComboBox();
		comboDestinatarios.setToolTipText(Messages.getString("Wizard.sobres.pagina1.comboDestinatarios.description")); 
		cargarCombo(comboDestinatarios);
		panelCentral.add(comboDestinatarios, c);
		
		c.insets = new Insets(0, 10, 0, 20);
		c.gridx = 1;
		c.weightx = 0.0;
		
		// Boton anadir destinatarios
		JButton anadir = new JButton();
		final JButton eliminar = new JButton();
		anadir.setToolTipText(Messages.getString("wizard.aniadir.description"));
		anadir.setText(Messages.getString("wizard.aniadir"));
		anadir.setAutoscrolls(true);
		anadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				anadirActionPerformed(comboDestinatarios, (DefaultListModel) listaDestinatarios.getModel(), 
						eliminar);
			}
		});
		panelCentral.add(anadir, c);
		
		c.insets = new Insets(10, 20, 0, 20);
		c.ipady = 80;
		c.gridwidth = 2;
		c.gridy = 3;
		c.gridx = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		// Panel que contiene a la lista de destintatarios
		JScrollPane panelLista = new JScrollPane();
		panelCentral.add(panelLista, c);
		
		// Lista con los destinatarios
		listaDestinatarios.setToolTipText(Messages.getString("wizard.listaDestinatarios.description"));
		listaDestinatarios.setModel(new DefaultListModel());
		panelLista.setViewportView(listaDestinatarios);

		c.ipady = 0;
		c.gridwidth = 1;
		c.gridy = 4;
		c.weightx = 1.0;
		
		// Espacio izdo del boton
		Panel panelVacio = new Panel();
		panelCentral.add(panelVacio, c);
		
		c.insets = new Insets(10, 0, 20, 20);
		c.weightx = 0.0;
		c.gridx = 1;
		
		// Boton eliminar destinatarios
		eliminar.setToolTipText(Messages.getString("wizard.eliminar.description"));
		eliminar.setEnabled(false);
		eliminar.setText(Messages.getString("wizard.sobres.eliminar.destinatario"));
		eliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				eliminarActionPerformed((DefaultListModel) listaDestinatarios.getModel(), eliminar);
			}
		});
		panelCentral.add(eliminar, c);
		
		getContentPane().add(panelCentral, BorderLayout.CENTER);

		// Accesos rapidos al menu de ayuda
		HelpUtils.enableHelpKey(comboDestinatarios,"ensobrado.wizard.repositorio.destinatario");
		HelpUtils.enableHelpKey(panelTexto,"ensobrado.wizard.destinatarios");
	}

	/**
	 * Carga el combo con las diferentes opciones de destinatarios
	 * @param comboDestinatarios Combo a cargar con las diferentes opciones de destinatarios
	 */
	private void cargarCombo(JComboBox comboDestinatarios) {
		comboDestinatarios.setModel(new DefaultComboBoxModel(KeyStoreLoader.getKeyStoresToWrap()));
	}

	/**
	 * Anade un destinatario del origen seleccionado en el combo
	 * @param listaModel Modelo de la lista de destinatarios
	 */
	private void anadirActionPerformed(JComboBox comboDestinatarios, DefaultListModel listaModel, JButton eliminar) {
		AOKeyStoreManager keyStoreManager = null;
		KeyStoreConfiguration kc = (KeyStoreConfiguration) comboDestinatarios.getSelectedItem();
		try {
			AOKeyStore ao= kc.getType();
			keyStoreManager = AOKeyStoreManagerFactory.getAOKeyStoreManager(ao, null, null, getPreferredPCB(ao), this);
		} catch (AOCancelledOperationException e) {
			logger.severe("Operacion cancelada por el usuario");
			return;
		} catch (Exception e) {
		    //e.printStackTrace();
			logger.severe("No se ha podido abrir el almacen de certificados: "+e);
			JOptionPane.showMessageDialog(this, Messages.getString("Wizard.sobres.error.abrir.almacen"), 
					Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		CertificateDestiny certDest = new CertificateDestiny(keyStoreManager, this);
		
		// Comprobamos que el certificado es correcto
		if (certDest.getAlias() != null && !certDest.equals("")) {
			boolean copiar = true;
			for (int i = 0; i < listaModel.getSize(); i++){
				CertificateDestiny c = (CertificateDestiny) listaModel.getElementAt(i);
				if (certDest.getAlias().equals(c.getAlias())){
					copiar = false;
				}
			}
			if (copiar) {
				listaModel.addElement(certDest.getAlias());
				listaCertificados.add(certDest);
				eliminar.setEnabled(true);
			} else 
				JOptionPane.showMessageDialog(this, Messages.getString("Wizard.sobres.error.usuario.existe"), 
						Messages.getString("error"), JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Elimina un destintatario de la lista
	 * @param listaModel	Modelo de la lista de destinatarios
	 * @param eliminar 		Boton eliminar
	 */
	private void eliminarActionPerformed(DefaultListModel listaModel, JButton eliminar) {
		for (int i=0; i<listaCertificados.size(); i++)
			if (listaCertificados.get(i).getAlias().equals(listaDestinatarios.getSelectedValue())) {
				listaCertificados.remove(listaCertificados.get(i));
				listaModel.remove(listaDestinatarios.getSelectedIndex());
				break;
			}

		if (listaModel.isEmpty()) {
			eliminar.setEnabled(false);
		}
	}

	/**
	 * Recupera el PasswordCallback que t&iacute;picamente se requiere para el acceso a un
	 * almac&eacute;n de claves.
	 * @param kStore Almac&eacuten de claves
	 */
	private PasswordCallback getPreferredPCB(AOConstants.AOKeyStore kStore) {
		if (kStore == null)
			throw new NullPointerException("No se ha indicado el KeyStore del que desea " +
				"obtener el PasswordCallBack");

		PasswordCallback pssCallback;
		if (kStore == AOConstants.AOKeyStore.WINDOWS || kStore == AOConstants.AOKeyStore.WINROOT
				|| kStore == AOConstants.AOKeyStore.PKCS11 || kStore == AOConstants.AOKeyStore.SINGLE)
			pssCallback = new NullPasswordCallback();
		else {
			pssCallback = new UIPasswordCallback(Messages.getString("Wizard.sobres.almacen.pass")+" "+kStore.getDescription(), this);
		}
		
		return pssCallback;
	}
	
	/**
	 * Botonera con funciones para la pagina panel de multifirma - cofirma
	 */
	private class Botonera extends BotoneraInferior {

		private static final long serialVersionUID = 1L;

		public Botonera(List<JDialogWizard> ventanas, Integer posicion) {
			super(ventanas, posicion);
		}

		@Override
		protected void siguienteActionPerformed(JButton anterior,
				JButton siguiente, JButton finalizar) {
			
			Boolean continuar = true;
			continuar = verificarCertificados();
			
			// Cargamos el listado de certificados
			((PanelRemitentes) getVentanas().get(2)).setListaCertificados(listaCertificados);
			
			if (continuar == true) {
				super.siguienteActionPerformed(anterior, siguiente, finalizar);
			}
		}
	}

	/**
	 * Comprueba que se ha seleccionado algun certificado
	 * @return	True o false segun la verificacion
	 */
	public Boolean verificarCertificados() {
		DefaultListModel listModel = (DefaultListModel) listaDestinatarios.getModel();
		if (listModel.isEmpty()){
            JOptionPane.showMessageDialog(this, Messages.getString("WizardCifrado.error.destinatario"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
		
		return true;
	}
}
