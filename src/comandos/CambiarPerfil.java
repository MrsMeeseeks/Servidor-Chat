package comandos;


import java.io.IOException;

import paqueteEnvios.PaqueteUsuario;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class CambiarPerfil extends ComandoServer{
	@Override
	public void ejecutar() {
		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) gson.fromJson(cadenaLeida, PaqueteUsuario.class);
//		Servidor.getConector().actualizarPerfil(paqueteUsuario);
		Servidor.getUsuariosConectados().remove(paqueteUsuario.getUsername());
		Servidor.getUsuariosConectados().add(paqueteUsuario.getUsername());
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(gson.toJson(paqueteUsuario));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
