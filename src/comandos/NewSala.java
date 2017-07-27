package comandos;

import java.io.IOException;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteSala;
import servidor.Servidor;

public class NewSala extends ComandoServer{

	@Override
	public void ejecutar() {
		PaqueteSala paqueteSala = (PaqueteSala) (gson.fromJson(cadenaLeida, PaqueteSala.class));
		try {
			if(Servidor.getConector().registrarSala(paqueteSala)){
				Servidor.getNombresSalasDisponibles().add(paqueteSala.getNombreSala());
				Servidor.getSalas().put(paqueteSala.getNombreSala(),paqueteSala);
				// COMO SE CREO 1 SALA NUEVA LE DIGO AL SERVER QUE LE MANDE A TODOS LOS QUE SE CONECTAN
				synchronized(Servidor.getAtencionNuevasSalas()){
					Servidor.getAtencionNuevasSalas().notify();
				}
			} else {
				paqueteSala.setComando(Comando.NEWSALA);
				paqueteSala.setMsj(Paquete.msjFracaso);
				escuchaCliente.getSalida().writeObject(gson.toJson(paqueteSala));
			}
		} catch (IOException e) {
			Servidor.getLog().append("Error al intentar informar al usuario " + escuchaCliente.getPaqueteUsuario().getUsername() + " que no se pudo crear la sala " + paqueteSala.getNombreSala() + System.lineSeparator());
			e.printStackTrace();
		}		
	}

}
