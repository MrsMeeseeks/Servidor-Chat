package comandos;

import java.io.IOException;
import java.net.Socket;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMencion;
import paqueteEnvios.PaqueteMensajeSala;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class MencionSala extends ComandoServer {

	@Override
	public void ejecutar() {
		PaqueteMencion paqueteMencion = (PaqueteMencion) (gson.fromJson(cadenaLeida, PaqueteMencion.class));
		try {
			if (Servidor.mencionUsuario(paqueteMencion)) {
				paqueteMencion.setComando(Comando.MENCIONSALA);

				Socket s2 = Servidor.getMapConectados().get(paqueteMencion.getUserEmisor());
				for(EscuchaCliente conectado : Servidor.getClientesConectados()){
					if(Servidor.getSalas().get(paqueteMencion.getNombreSala()).getUsuariosConectados().contains(conectado.getPaqueteUsuario().getUsername()) 
							&& conectado.getSocket() != s2){
						conectado.getSalida().writeObject(gson.toJson(paqueteMencion));
					}
				}
				String msjAgregar = paqueteMencion.getUserEmisor() + ": " + paqueteMencion.getMsj() + "\n";
				String chatAnterior = Servidor.getSalas().get(paqueteMencion.getNombreSala()).getHistorial();
				
				Servidor.getSalas().get(paqueteMencion.getNombreSala()).setHistorial(chatAnterior + msjAgregar);

				PaqueteMensajeSala pack = new PaqueteMensajeSala();
				pack.setMsj(paqueteMencion.getMsj());
				pack.setNombreSala(paqueteMencion.getNombreSala());
				pack.setUserEmisor(paqueteMencion.getUserEmisor());
				Servidor.getConector().guardarChatSala(pack);
			}
		} catch (IOException e) {
			Servidor.getLog().append("Error al enviar el mensaje de " + paqueteMencion.getUserEmisor() + " para la sala "+ paqueteMencion.getNombreSala() + System.lineSeparator());
			e.printStackTrace();
		}
		
	}

}
