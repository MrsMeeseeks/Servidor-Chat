package servidor;

import comandosChatBot.ComandoChatBot;

import paqueteEnvios.PaqueteMensaje;


import java.text.Normalizer;
import java.util.HashMap;

public class ChatBot extends Thread {

	private HashMap<String,String> sinonimos;
	private String codigo=null;

	private PaqueteMensaje paqueteMensaje;
	private String mensajeAEnviar;
	private String mensajeRecibido;


	public ChatBot(HashMap<String,String> map) {
		this.sinonimos = map;
	}


	@Override
	public void run() {
		synchronized (this) {
			try {
				ComandoChatBot comando; 

				while (true) {
					wait();

					if (paqueteMensaje!=null) {
						mensajeRecibido = paqueteMensaje.getMsjChat();
						procesarMensaje();
						comando = (ComandoChatBot) Class.forName("comandosChatBot" + "." + codigo).newInstance();
						comando.setNombreSala(paqueteMensaje.getNombreSala());
						comando.ejecutar();
						codigo=null;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void procesarMensaje(){
		String  msj = normalizarCadena(mensajeRecibido);
		BuscarPalabrasClaves(msj);
	}

	public static String reemplazar(String cadena, String busqueda, String reemplazo) {
		return cadena.replaceAll(busqueda, reemplazo);
	}

	public void BuscarPalabrasClaves(String cadena) {

		String[] partes = cadena.split(" ");
	
		for (int i = 0; i < partes.length; i++) {
			if (sinonimos.containsKey(partes[i].toLowerCase())) {
				setCodigo(sinonimos.get(partes[i]));
			}
		} 
		if(codigo==null){
			setCodigo("CodigoNoValido");
		}
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