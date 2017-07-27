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
			if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {

				PaqueteDeUsuariosYSalas pus = new PaqueteDeUsuariosYSalas(Servidor.getUsuariosConectados(), Servidor.getNombresSalasDisponibles());
				pus.setComando(Comando.REGISTRO);
				pus.setMsj(Paquete.msjExito);

				Servidor.getUsuariosConectados().add(paqueteUsuario.getUsername());

				// Consigo el socket, y entonces ahora pongo el username y el socket en el map
				int index = Servidor.getUsuariosConectados().indexOf(paqueteUsuario.getUsername());
				Servidor.getMapConectados().put(paqueteUsuario.getUsername(), Servidor.getSocketsConectados().get(index));

				escuchaCliente.getSalida().writeObject(gson.toJson(pus));

				// COMO SE CONECTO 1 LE DIGO AL SERVER QUE LE MANDE A TODOS LOS QUE SE CONECTAN
				synchronized(Servidor.getAtencionConexiones()){
					Servidor.getAtencionConexiones().notify();
				}
				// Si el usuario no se pudo registrar le envio un msj de fracaso
			} else {
				paqueteUsuario.setMsj(Paquete.msjFracaso);
				escuchaCliente.getSalida().writeObject(gson.toJson(paqueteUsuario));
			}
		} catch (JsonSyntaxException | IOException  e) {
			Servidor.getLog().append("Fallo al intentar informar al usuario "+ paqueteUsuario.getUsername() + " sobre su intento de registro." + System.lineSeparator());
			e.printStackTrace();
		}	
	}

}
