package mockComandosChatBot;


import org.json.JSONObject;

import Tests.MockServidor;
import comandosChatBot.ComandoChatBot;
import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensaje;

public class MockUSD extends ComandoChatBot {
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

			PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);
			paqueteMensaje.setComando(Comando.CHATSALA);

			String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
			MockServidor.getSalitas().get(nombreSala).agregarMsj(msjAgregar);

		}

	}
}
