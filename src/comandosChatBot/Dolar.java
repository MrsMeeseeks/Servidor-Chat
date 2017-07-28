package comandosChatBot;

import java.io.IOException;

import org.json.JSONObject;

import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class Dolar extends ComandoChatBot{
	
	private String msjFinal;
	@Override
	public void ejecutar() {
		JSONObject j;
		try {
			j = getJSONFromURL("https://query.yahooapis.com/v1/public/yql?q=select%20Name%2C%20Rate%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + GestorMoneda.getInstance("src/data/codmoneda.json").getCodigo("usd")+GestorMoneda.getInstance("src/data/codmoneda.json").getCodigo("ars") + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
			String r = j.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
		    if (!r.equals("N/A")) {
		    	msjFinal = "El precio del dolar es de: " + Double.parseDouble(r);
		    } else {
		    	msjFinal = "Error al tratar de conseguir el precio del dolar.";
		    }
		    
		    try {
				PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);
				paqueteMensaje.setComando(Comando.CHATSALA);
				for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
					if (Servidor.getSalas().get(paqueteMensaje.getNombreSala()).getUsuariosConectados()
							.contains(conectado.getPaqueteUsuario().getUsername())) {
						conectado.getSalida().writeObject(gson.toJson(paqueteMensaje));
					}
				}
				String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsj() + "\n";
				Servidor.getSalas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);
				Servidor.getConector().guardarChatSala(paqueteMensaje);
			} catch (IOException e) {
				Servidor.getLog().append("Error al tratar de responder la solicitud de fecha y hora." + System.lineSeparator());
				e.printStackTrace();
			}
		} catch (IOException e) {
			Servidor.getLog().append("No se pudo encontrar los datos del precio del dolar." + System.lineSeparator());
		}		
	}

}
