package mockComandosChatBot;



import Tests.MockServidor;
import comandosChatBot.ComandoChatBot;

import paqueteEnvios.PaqueteMensaje;


public class CodigoNoValido extends ComandoChatBot {
	private String msjFinal;
	@Override
	public void ejecutar() {
		msjFinal = "No se detectó ningun comando valido al que responder.";
		PaqueteMensaje paqueteMensaje = new PaqueteMensaje("Alfred", null, msjFinal, nombreSala);
			String msjAgregar = paqueteMensaje.getUserEmisor() + ": " + paqueteMensaje.getMsjChat() + "\n";
			MockServidor.getSalitas().get(paqueteMensaje.getNombreSala()).agregarMsj(msjAgregar);		
	}

}
