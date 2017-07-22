package servidor;

import com.google.gson.Gson;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteDeSalas;
import paqueteEnvios.PaqueteSala;

public class AtencionConexionesSalas extends Thread {

	private final Gson gson = new Gson();
	private String nombreSala;


	public AtencionConexionesSalas() {
	}

	public void run() {
		synchronized (this) {
			try {
				while (true) {
					// Espero a que se conecte alguien
					wait();
					// Le reenvio la conexion a todos
					if (nombreSala != null) {

						PaqueteSala ps = Servidor.getSalas().get(nombreSala);						
						ps.setComando(Comando.CONEXIONSALA);
						String s = gson.toJson(ps);

						for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
							if (ps.getUsuariosConectados()
									.contains(conectado.getPaqueteUsuario().getUsername())) {
								if (conectado.getPaqueteUsuario().getEstado()) {
									conectado.getSalida().writeObject(s);
								}	
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getNombreSala() {
		return nombreSala;
	}

	public void setNombreSala(String nombreSala) {
		this.nombreSala = nombreSala;
	}
}