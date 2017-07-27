package comandos;

import java.io.IOException;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteSala;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class EliminarSala extends ComandoServer {

	@Override
	public void ejecutar() {
		PaqueteSala paqueteSala = (PaqueteSala) (gson.fromJson(cadenaLeida, PaqueteSala.class));
		paqueteSala.setComando(Comando.ELIMINARSALA);
		try {
			if(paqueteSala.getCliente().equals(Servidor.getSalas().get(paqueteSala.getNombreSala()).getOwnerSala())){
				if(Servidor.getConector().eliminarSala(paqueteSala)){
					Servidor.getNombresSalasDisponibles().remove(paqueteSala.getNombreSala());
					Servidor.getSalas().remove(paqueteSala.getNombreSala());
					paqueteSala.setComando(Comando.ELIMINARSALA);
					paqueteSala.setMsj(Paquete.msjExito);
					for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
						conectado.getSalida().writeObject(gson.toJson(paqueteSala));
					}
				} else {
					paqueteSala.setMsj(Paquete.msjFracaso);
					escuchaCliente.getSalida().writeObject(gson.toJson(paqueteSala));
				}
			}else{
				paqueteSala.setMsj(Paquete.msjFallo);
				escuchaCliente.getSalida().writeObject(gson.toJson(paqueteSala));
			}
		} catch (IOException e) {
			Servidor.getLog().append("Error al intentar informar al usuario " + escuchaCliente.getPaqueteUsuario().getUsername() + " sobre su intento de eliminar la sala " + paqueteSala.getNombreSala() + System.lineSeparator() );
			e.printStackTrace();
		}		
	}

}
