
package servidor;

import com.google.gson.Gson;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteDeUsuariosYSalas;

public class AtencionConexiones extends Thread {

	private final Gson gson = new Gson();

	public AtencionConexiones() {
	}

	public void run() {
		synchronized (this) {
			try {
				while (true) {
					// Espero a que se conecte alguien
					wait();
					// Le reenvio la conexion a todos
					PaqueteDeUsuariosYSalas pdu = (PaqueteDeUsuariosYSalas) new PaqueteDeUsuariosYSalas(Servidor.getUsuariosConectados(), Servidor.getNombresSalasDisponibles())
							.clone();
					pdu.setComando(Comando.CONEXION);
					String s = gson.toJson(pdu);
					for (EscuchaCliente conectado : Servidor.getClientesConectados())
						if (conectado.getPaqueteUsuario().getEstado())
							conectado.getSalida().writeObject(s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}