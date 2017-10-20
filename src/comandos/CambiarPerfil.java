package comandos;


import java.io.IOException;

import paqueteEnvios.PaqueteUsuario;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class CambiarPerfil extends ComandoServer{
	@Override
	public void ejecutar() {
		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) gson.fromJson(cadenaLeida, PaqueteUsuario.class);
		try {
			Servidor.getConector().actualizarPerfil(paqueteUsuario);
			//guarda la foto en la carpeta de perfiles
			String archivoDestino = "perfiles/" + paqueteUsuario.getUsername() + ".png";
		    PaqueteUsuario.deBytesAFile(paqueteUsuario.getFotoPerfil(), archivoDestino);
		} catch (IOException e1) {
			System.out.println("Error en comandos al cambiar perfil.");
		}
		Servidor.getUsuariosConectados().remove(paqueteUsuario.getUsername());
//		Servidor.getFotosConectados().remove(paqueteUsuario.getFotoPerfil());
		
		Servidor.getUsuariosConectados().add(paqueteUsuario.getUsername());
//		Servidor.getFotosConectados().add(paqueteUsuario.getFotoPerfil());
		
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(gson.toJson(paqueteUsuario));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
