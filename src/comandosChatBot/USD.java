package comandosChatBot;

import java.io.IOException;

import org.json.JSONObject;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class USD extends ComandoChatBot{

	private String msjFinal;
	@Override
	public void ejecutar() {
		JSONObject j;
		j = getJSONFromURL("https://query.yahooapis.com/v1/public/yql?q=select%20Name%2C%20Rate%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + "USD" + "ARS" + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
		if (j!=null) {
			String r = j.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
			if (!r.equals("N/A")) {
				msjFinal = "El precio del dolar es de: " + Double.parseDouble(r);
			} else {
				msjFinal = "Error al tratar de conseguir el precio del dolar.";
			}
			try {
				PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);
				paqueteMensaje.setComando(Comando.CHATSALA);
				if (!nombreSala.equals("Ventana Principal")) {
					for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
						if (Servidor.getSalas().get(nombreSala).getUsuariosConectados()
								.contains(conectado.getPaqueteUsuario().getUsername())) {
							conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
						}
					}
					String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
					Servidor.getSalas().get(nombreSala).agregarMsj(msjAgregar);
					Servidor.getConector().guardarChatSala(paqueteMensaje);
				} else {
					paqueteMensaje.setComando(Comando.CHATALL);
					for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
						conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
					}
				}
			} catch (IOException e) {
				Servidor.getLog().append(
						"Error al tratar de responder la solicitud del valor del dolar." + System.lineSeparator());
				e.printStackTrace();
			} 
		} else {
			Servidor.getLog().append("No se pudo encontrar los datos del precio del dolar." + System.lineSeparator());
		}
	}

}
