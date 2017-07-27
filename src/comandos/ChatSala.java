package comandos;

import java.io.IOException;
import java.net.Socket;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensajeSala;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ChatSala extends ComandoServer{

	@Override
	public void ejecutar() {
		PaqueteMensajeSala paqueteMensajeSala = (PaqueteMensajeSala) (gson.fromJson(cadenaLeida, PaqueteMensajeSala.class));
		try {
			paqueteMensajeSala.setComando(Comando.CHATSALA);
			Socket s2 = Servidor.getMapConectados().get(paqueteMensajeSala.getUserEmisor());
			for(EscuchaCliente conectado : Servidor.getClientesConectados()){
				if(Servidor.getSalas().get(paqueteMensajeSala.getNombreSala()).getUsuariosConectados().contains(conectado.getPaqueteUsuario().getUsername()) 
						&& conectado.getSocket() != s2){
					conectado.getSalida().writeObject(gson.toJson(paqueteMensajeSala));
				}
			}
			String msjAgregar = paqueteMensajeSala.getUserEmisor() + ": " + paqueteMensajeSala.getMsj() + "\n";
			String chatAnterior = Servidor.getSalas().get(paqueteMensajeSala.getNombreSala()).getHistorial();
			
			Servidor.getSalas().get(paqueteMensajeSala.getNombreSala()).setHistorial(chatAnterior + msjAgregar);

			Servidor.getConector().guardarChatSala(paqueteMensajeSala);
		} catch (IOException e) {
			Servidor.getLog().append("Error al enviar el mensaje de " + paqueteMensajeSala.getUserEmisor() + " para la sala "+ paqueteMensajeSala.getNombreSala() + System.lineSeparator());
			e.printStackTrace();
		}
		
	}

}
