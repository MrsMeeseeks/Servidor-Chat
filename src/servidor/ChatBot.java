package servidor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.Color;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.lang.Math;
import java.util.HashMap;

public class ChatBot {

	private static HashMap<String,String> sinonimos;

	public ChatBot(String mensaje) {
		
		// elimino simbolos de la cadena
		String msj = EliminaCaracteres(mensaje, ",?._|");
		// quito la palabra @chatbot de la misma
		msj = reemplazar(msj, "@chatbot", "");

		String partes[] = BuscarPalabrasClaves(msj);
	}

	public static String reemplazar(String cadena, String busqueda, String reemplazo) {
		return cadena.replaceAll(busqueda, reemplazo);
	}

	public static void main(String[] args) {
		Conector conexionDB = new Conector();
		conexionDB.connect();
		sinonimos = conexionDB.cargarPalabrasClaveChatBot();
		
		ChatBot c = new ChatBot("@chatbot cual es el precio del USD ?");

	}

	public String[] BuscarPalabrasClaves(String cadena) {
		String palabrasClaves[];

		String[] partes = cadena.split(" ");

		for (int i = 0; i < partes.length; i++) {
			if (this.sinonimos.containsKey(partes[i])) {
				System.out.println("la palabra " + partes[i] + " es clave");
			}else{
//				System.out.println(partes[i]);
			}
		}
		return partes;
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
}