package servidor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.google.gson.Gson;

import comandosChatBot.ComandoChatBot;
import paqueteEnvios.PaqueteMensaje;

import javax.swing.JScrollPane;

import java.awt.Color;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.lang.Math;
import java.text.Normalizer;
import java.util.HashMap;

public class ChatBot extends Thread {

	private HashMap<String,String> sinonimos;
	private String codigo;

	private final Gson gson = new Gson();
	private PaqueteMensaje paqueteMensaje;
	private String mensajeAEnviar;
	private String mensajeRecibido;


	public ChatBot(HashMap<String,String> map) {
		this.sinonimos = map;
	}

	public void procesarMensaje(){
		
		String  msj = normalizarCadena(mensajeRecibido);

		BuscarPalabrasClaves(msj);


	}

	public static String reemplazar(String cadena, String busqueda, String reemplazo) {
		return cadena.replaceAll(busqueda, reemplazo);
	}

	public void analizarMensaje(){
		switch(this.codigo){
		case "USD":

			break;

		case "CLIMA":

			break;

		case "HORA":

			break;

		case "FECHA":

			break;

		case "FECHAHORA":

			break;
		};
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				ComandoChatBot comando; 
				
				while (true) {
					wait();

					mensajeRecibido = paqueteMensaje.getMsj();
					procesarMensaje();
					
					
					
					
					
					
					PaqueteMensaje pm = new PaqueteMensaje("Alfred", null, mensajeAEnviar, paqueteMensaje.getNombreSala());
					String s = gson.toJson(pm);
					for (EscuchaCliente conectado : Servidor.getClientesConectados())
						conectado.getSalida().writeObject(s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void BuscarPalabrasClaves(String cadena) {

		String[] partes = cadena.split(" ");

		for (int i = 0; i < partes.length; i++) {
			if (this.sinonimos.containsKey(partes[i].toLowerCase())) {
				this.setCodigo(this.sinonimos.get(partes[i]).toUpperCase());
			}
		} 
	}

	public String EliminaCaracteres(String s_cadena, String s_caracteres) {
		String nueva_cadena = "";
		Character caracter = null;
		boolean valido = true;

		/*
		 * Va recorriendo la cadena s_cadena y copia a la cadena que va a
		 * regresar, sólo los caracteres que no estén en la cadena s_caracteres
		 */
		for (int i = 0; i < s_cadena.length(); i++) {
			valido = true;
			for (int j = 0; j < s_caracteres.length(); j++) {
				caracter = s_caracteres.charAt(j);

				if (s_cadena.charAt(i) == caracter) {
					valido = false;
					break;
				}
			}
			if (valido)
				nueva_cadena += s_cadena.charAt(i);
		}

		return nueva_cadena;
	}

	public HashMap<String, String> getSinonimos() {
		return sinonimos;
	}

	public void setSinonimos(HashMap<String, String> sinonimos) {
		this.sinonimos = sinonimos;
	}


	public PaqueteMensaje getPaqueteMensaje() {
		return paqueteMensaje;
	}

	public void setPaqueteMensaje(PaqueteMensaje paqueteMensaje) {
		this.paqueteMensaje = paqueteMensaje;
	}


	public String getMensajeAEnviar() {
		return mensajeAEnviar;
	}


	public void setMensajeAEnviar(String mensajeAEnviar) {
		this.mensajeAEnviar = mensajeAEnviar;
	}

	public String getMensajeRecibido() {
		return mensajeRecibido;
	}

	public void setMensajeRecibido(String mensajeRecibido) {
		this.mensajeRecibido = mensajeRecibido;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public static String normalizarCadena(String texto) {
	    texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
	    texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    texto = texto.replaceAll("[^\\w\\s]"," ");
	    return texto;
	}

}