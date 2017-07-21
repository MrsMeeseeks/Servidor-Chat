
package servidor;

import com.google.gson.Gson;

import paqueteEnvios.Comando;
import paqueteEnvios.Paquete;
import paqueteEnvios.PaqueteDeSalas;

public class AtencionNuevasSalas extends Thread {

	private final Gson gson = new Gson();

	public AtencionNuevasSalas() {
	}

	public void run() {
		synchronized (this) {
			try {
				while (true) {
					// Espero a que se cree una sala nueva
					wait();
					// Le reenvio la nueva sala a todos
					PaqueteDeSalas ps = (PaqueteDeSalas) new PaqueteDeSalas(Servidor.getNombresSalasDisponibles())
							.clone();
					ps.setComando(Comando.NEWSALA);
					ps.setMensaje(Paquete.msjExito);
					String s = gson.toJson(ps);
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