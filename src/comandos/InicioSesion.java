package comandos;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteDeUsuariosYSalas;
import paqueteEnvios.PaqueteUsuario;
import servidor.Servidor;

public class InicioSesion extends ComandoServer {

	@Override
	public void ejecutar() {

		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class));

		try {

			if(!Servidor.getUsuariosConectados().contains(paqueteUsuario.getUsername())) {
				if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {
					escuchaCliente.setPaqueteUsuario(paqueteUsuario);
					PaqueteDeUsuariosYSalas pus = new PaqueteDeUsuariosYSalas(Servidor.getUsuariosConectados(),
							Servidor.getNombresSalasDisponibles());
					pus.setComando(Comando.INICIOSESION);
					pus.setMsj(Paquete.msjExito);

					Servidor.getUsuariosConectados().add(paqueteUsuario.getUsername());
					// Consigo el socket, y entonces ahora pongo el username y el socket en el map
					int index = Servidor.getUsuariosConectados().indexOf(paqueteUsuario.getUsername());
					Servidor.getMapConectados().put(paqueteUsuario.getUsername(),
							Servidor.getSocketsConectados().get(index));

					escuchaCliente.getSalida().writeObject(gson.toJson(pus));

					synchronized (Servidor.getAtencionConexiones()) {
						Servidor.getAtencionConexiones().notify();
					}

				} else {
					paqueteUsuario.setMsj(Paquete.msjFracaso);
					escuchaCliente.getSalida().writeObject(gson.toJson(paqueteUsuario));
				} 
			} else {
				paqueteUsuario.setMsj(Paquete.msjFallo);
				escuchaCliente.getSalida().writeObject(gson.toJson(paqueteUsuario));
			}
		} catch (JsonSyntaxException | IOException e) {
			Servidor.getLog().append("Fallo al intentar informar al usuario "+ paqueteUsuario.getUsername() + " sobre su intento de inicio de sesión." + System.lineSeparator());
			e.printStackTrace();
		} 
	}
}
