package Tests;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.json.JSONObject;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteMensaje;
import paqueteEnvios.PaqueteSala;
import servidor.AtencionConexiones;
import servidor.AtencionConexionesSalas;
import servidor.AtencionNuevasSalas;
import servidor.ChatBot;
import servidor.Conector;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class MockServidor extends Thread {

	private static String city;
	private ChatBot chatbot;
	static Conector conector;
	public static Map<String, PaqueteSala> salitas = new HashMap<>();

	public MockServidor(ChatBot chatBot) {
		this.chatbot = chatBot;
	}

	@Override
	public void run () {

		cargarCiudad();
		PaqueteSala paqueteSala = new PaqueteSala("Sala Test","","User Test");
		salitas.put(paqueteSala.getNombreSala(), paqueteSala);
		chatbot.start();		
	}

	public static Conector getConector() {
		return conector;
	}

	public void simularLlegadaMensaje(PaqueteMensaje paqueteMensaje) {
		if(paqueteMensaje.getUserReceptor().toUpperCase().equals("CHATBOT")){
			synchronized (chatbot) {
				chatbot.setPaqueteMensaje(paqueteMensaje);
				chatbot.notify();
			}
		}
	}


	protected void cargarCiudad() {
		JSONObject j;
		j = getJSONFromURL("http://ip-api.com/json");
		if (j!=null) {
			String r = j.getString("city");
			if (r.equals("N/A"))
				city = null;
			else
				city = r;
		} else {
			city=null;
		}
	}

	private JSONObject getJSONFromURL(String dir) {
		try {
			URL url = null;
			url = new URL(dir);
			Scanner scan = null;
			scan = new Scanner(url.openStream(), "UTF-8");
			String str = new String();
			while (scan.hasNext()) {
				str += scan.nextLine();
			}
			scan.close();
			return new JSONObject(str);
		} catch (IOException e) {
			return null;
		}
	}


	public static String getCity() {
		return city;
	}

	public static Map<String, PaqueteSala> getSalitas(){
		return MockServidor.salitas;
	}

}
