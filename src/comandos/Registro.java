package comandos;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteDeUsuariosYSalas;
import paqueteEnvios.PaqueteUsuario;
import servidor.Servidor;

public class Registro extends ComandoServer {

	@Override
	public void ejecutar() {
		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class));
		try {
			if (!Servidor.getConector().yaRegistrado(paqueteUsuario.getUsername())) {
				if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {

					PaqueteDeUsuariosYSalas pus = new PaqueteDeUsuariosYSalas(Servidor.getUsuariosConectados(),
							Servidor.getNombresSalasDisponibles());
					pus.setComando(Comando.REGISTRO);
					pus.setMsj(Paquete.msjExito);

					Servidor.conectarUsuario(paqueteUsuario.getUsername());
					
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
		} catch (JsonSyntaxException | IOException  e) {
			Servidor.getLog().append("Fallo al intentar informar al usuario "+ paqueteUsuario.getUsername() + " sobre su intento de registro." + System.lineSeparator());
		}	
	}

}
