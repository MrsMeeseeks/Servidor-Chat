package comandos;

import java.io.IOException;
import java.net.Socket;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteMensaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class MencionSala extends ComandoServer {

	private String msjAgregar;
	@Override
	public void ejecutar() {
		PaqueteMensaje paqueteMensaje = (gson.fromJson(cadenaLeida, PaqueteMensaje.class));
		Socket s2 = Servidor.getMapConectados().get(paqueteMensaje.getUserEmisor());
		paqueteMensaje.setComando(Comando.MENCIONSALA);

		if (!paqueteMensaje.getUserReceptor().toUpperCase().equals("CHATBOT")) {
			try {
				paqueteMensaje.setMsj(Paquete.msjExito);
				msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
				for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
					if (Servidor.getSalas().get(paqueteMensaje.getNombreSala()).getUsuariosConectados()
							.contains(conectado.getPaqueteUsuario().getUsername()) && conectado.getSocket() != s2) {
						conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
					}
				}
				Servidor.getSalas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);
				Servidor.getConector().guardarChatSala(paqueteMensaje);
				if (!Servidor.mencionUsuario(paqueteMensaje)) {
					paqueteMensaje.setMsj(Paquete.msjFracaso);
					msjAgregar = "El usuario " + paqueteMensaje.getUserReceptor() + " " + " no existe o se encuentra desconectado.";
					paqueteMensaje.setMsjChat(msjAgregar);
					s2=null;
					Servidor.getSalas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);
					Servidor.getConector().guardarChatSala(paqueteMensaje);
					
					for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
						if (Servidor.getSalas().get(paqueteMensaje.getNombreSala()).getUsuariosConectados()
								.contains(conectado.getPaqueteUsuario().getUsername()) && conectado.getSocket() != s2) {
							conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
						}
					}  
				}	
			} catch (IOException e) {
				Servidor.getLog().append("Error al intentar enviar el mensaje de " + paqueteMensaje.getUserEmisor() + " para la sala "+ paqueteMensaje.getNombreSala() + "." + System.lineSeparator());
				e.printStackTrace();
			}


		} else {
			synchronized (Servidor.getAlfred()) {
				Servidor.getAlfred().setPaqueteMensaje(paqueteMensaje);
				Servidor.getAlfred().notify();
			}
		}


	}

}
