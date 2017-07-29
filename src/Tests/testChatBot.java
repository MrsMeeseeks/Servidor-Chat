package Tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cliente.Cliente;
import comandosChatBot.Clima;
import comandosChatBot.ComandoChatBot;
import paqueteEnvios.Comando;
import paqueteEnvios.PaqueteMensaje;
import paqueteEnvios.PaqueteSala;
import servidor.ChatBot;
import servidor.Conector;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class testChatBot {

	@Test
	public void testChatBot() {
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);

		Assert.assertEquals("valorTEST", cb.getSinonimos().get("claveTEST"));

	}

	@Test
	public void testReemplazar() {
		String cad1 = "Esto es un Test";
		String cad2 = " Test";
		String cad3 = "";
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);

		Assert.assertEquals("Esto es un", cb.reemplazar(cad1, cad2, cad3));
	}

	@Test
	public void testBuscarPalabrasClavesNovalido() {

		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);

		String cadena = "claveTEST";
		cb.BuscarPalabrasClaves(cadena);

		Assert.assertEquals("CodigoNoValido", cb.getCodigo());
	}

	@Test
	public void testBuscarPalabrasClavesValido() {

		HashMap<String, String> h = new HashMap<>();
		h.put("codigo1".toLowerCase(), "codigo2");
		ChatBot cb = new ChatBot(h);

		String cadena = "codigoB test codigo1";
		cb.BuscarPalabrasClaves(cadena);

		Assert.assertEquals("codigo2".toLowerCase(), cb.getCodigo());
	}

	@Test
	public void testSetSinonimos() {
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);

		HashMap<String, String> h2 = new HashMap<>();
		h2.put("claveTEST2", "valorTEST2");
		cb.setSinonimos(h2);

		Assert.assertEquals("valorTEST2", cb.getSinonimos().get("claveTEST2"));

	}

	@Test
	public void testNormalizarCadena() {
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);
		Assert.assertEquals("  asdasd  a  d2l3q23", cb.normalizarCadena("+´asdasd+ a}´d2l3q23"));
	}

	@Test
	public void testSetMensajeRecibido() {
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);
		String cad = "testMEnsajeRecibido";
		cb.setMensajeRecibido(cad);
		Assert.assertEquals(cad, cb.getMensajeRecibido());
	}

	@Test
	public void testsetMensajeAEnviar() {
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);
		String cad = "testMEnsajeEnviar";
		cb.setMensajeAEnviar(cad);
		Assert.assertEquals(cad, cb.getMensajeAEnviar());
	}

	@Test
	public void testSetPaqueteMensaje() {
		HashMap<String, String> h = new HashMap<>();
		h.put("claveTEST", "valorTEST");
		ChatBot cb = new ChatBot(h);

		cb.setPaqueteMensaje(new PaqueteMensaje("emisor", "destinatario", "msj", "nombreSala"));

		PaqueteMensaje p = cb.getPaqueteMensaje();
		Assert.assertEquals("nombreSala", p.getNombreSala());
		Assert.assertEquals("msj", p.getMsjChat());
	}


	@Test
	public void testComandoNoValido() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test perro", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("clima".toLowerCase(), "Clima");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);

		synchronized (this) {
			this.wait(3000);
		}

		Assert.assertEquals("Alfred: No se detectó ningun comando valido al que responder.\n", MockServidor.getSalitas().get("Sala Test").getHistorial());
	}

	@Test
	public void testComandoDolar() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test dolar", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("dolar".toLowerCase(), "MockUSD");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);
		String msjEsperadoOK="cadena ejemplo";
		String msjEsperadoError ="cadena ejemplo";
		JSONObject j;
		j = getJSONFromURL("https://query.yahooapis.com/v1/public/yql?q=select%20Name%2C%20Rate%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22" + "USD" + "ARS" + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
		if (j!=null) {
			String r = j.getJSONObject("query").getJSONObject("results").getJSONObject("rate").getString("Rate");
			if (!r.equals("N/A")) {
				msjEsperadoOK = "Alfred: El precio del dolar es de: " + Double.parseDouble(r)+"\n";
			} else {
				msjEsperadoError = "Error al tratar de conseguir el precio del dolar.";
			}
		}
		synchronized (this) {
			this.wait(3000);
		}

		if(msjEsperadoError.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else {
			Assert.assertEquals(msjEsperadoOK, MockServidor.getSalitas().get("Sala Test").getHistorial());

		}
	}
	
	
	@Test
	public void testComandoHora() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test hora", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("hora".toLowerCase(), "MockHora");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);
		
		String husoHorario = getHusoHorario();
		String msjEsperadoOK = "cadena ejemplo";
		String msjEsperadoError1 = "cadena ejemplo";
		String msjEsperadoError2 = "cadena ejemplo";
		if (husoHorario!=null) {
			JSONObject j = getJSONFromURL("http://api.timezonedb.com/v2/get-time-zone?key=J74Q9QN1RN7M&format=json&by=zone&zone=" + husoHorario);
			if (j!=null) {
				String result = j.getString("formatted");
				String[] fechaYhora = result.split(" ", 2);
				String[] hora = fechaYhora[1].split(":",3);
				msjEsperadoOK = "Alfred: La hora es: "+ hora[0]+ ":" + hora[1] +"\n";
			} else {
				msjEsperadoError1 = "Alfred: Error al tratar de conseguir los datos de la hora."+"\n";
			}
		} else {
			msjEsperadoError2 = "Alfred: Error al tratar de conseguir la zona horaria."+"\n";
		}
		synchronized (this) {
			this.wait(3000);
		}

		if(msjEsperadoError1.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError1, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else if(msjEsperadoError2.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError2, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else {
			Assert.assertEquals(msjEsperadoOK, MockServidor.getSalitas().get("Sala Test").getHistorial());
		}
	}
	
	@Test
	public void testComandoFecha() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test fecha", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("fecha".toLowerCase(), "MockFecha");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);
		
		String husoHorario = getHusoHorario();
		String msjEsperadoOK = "cadena ejemplo";
		String msjEsperadoError1 = "cadena ejemplo";
		String msjEsperadoError2 = "cadena ejemplo";
		if (husoHorario!=null) {
			JSONObject j = getJSONFromURL("http://api.timezonedb.com/v2/get-time-zone?key=J74Q9QN1RN7M&format=json&by=zone&zone=" + husoHorario);
			if (j!=null) {
				String result = j.getString("formatted");
				String[] fechaYhora = result.split(" ", 2);
				String[] fecha = fechaYhora[0].split("-", 3);
				msjEsperadoOK = "Alfred: La fecha es: " + fecha[2] +"-"+fecha[1]+"-"+fecha[0]+"\n";
			} else {
				msjEsperadoError1 = "Alfred: Error al tratar de conseguir los datos de la fecha."+"\n";
			}
		} else {
			msjEsperadoError2 = "Alfred: Error al tratar de conseguir la zona horaria."+"\n";
		}
		synchronized (this) {
			this.wait(3000);
		}

		if(msjEsperadoError1.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError1, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else if(msjEsperadoError2.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError2, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else {
			Assert.assertEquals(msjEsperadoOK, MockServidor.getSalitas().get("Sala Test").getHistorial());
		}
	}
	
	@Test
	public void testComandoFechaHora() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test dia", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("dia".toLowerCase(), "MockFechaHora");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);
		
		String husoHorario = getHusoHorario();
		String msjEsperadoOK = "cadena ejemplo";
		String msjEsperadoError1 = "cadena ejemplo";
		String msjEsperadoError2 = "cadena ejemplo";
		if (husoHorario!=null) {
			JSONObject j = getJSONFromURL("http://api.timezonedb.com/v2/get-time-zone?key=J74Q9QN1RN7M&format=json&by=zone&zone=" + husoHorario);
			if (j!=null) {
				String result = j.getString("formatted");
				String[] fechaYhora = result.split(" ", 2);
				String hora[] = fechaYhora[1].split(":",3);
				String[] fecha = fechaYhora[0].split("-", 3);
				msjEsperadoOK = "Alfred: La fecha es: " + fecha[2] +"-"+fecha[1]+"-"+fecha[0]+"\n"+"La hora es: "+hora[0]+ ":" + hora[1]+"\n";
			} else {
				msjEsperadoError1 = "Alfred: Error al tratar de conseguir los datos de la fecha y hora."+"\n";
			}
		} else {
			msjEsperadoError2 = "Alfred: Error al tratar de conseguir la zona horaria."+"\n";
		}
		synchronized (this) {
			this.wait(3000);
		}

		if(msjEsperadoError1.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError1, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else if(msjEsperadoError2.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError2, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else {
			
			Assert.assertEquals(msjEsperadoOK, MockServidor.getSalitas().get("Sala Test").getHistorial());
		}
	}


	@Test
	public void testProcesarMensaje() {

		HashMap<String, String> h = new HashMap<>();
		h.put("codigo1".toLowerCase(), "codigo2");
		ChatBot cb = new ChatBot(h);

		String cadena = "codigoB test codigo1";
		cb.setMensajeRecibido(cadena);

		cb.procesarMensaje();
		Assert.assertEquals("codigo2".toLowerCase(), cb.getCodigo());
	}

	@Test
	public void testClaseComandoNoExistente() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test euro", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("euro".toLowerCase(), "Euro");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);

		synchronized (this) {
			this.wait(3000);
		}
		final ExpectedException exception = ExpectedException.none();
		exception.expect(ClassNotFoundException.class);

	}
	
	@Test
	public void testComandoClima() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test clima", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("clima".toLowerCase(), "MockClima");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);
		
		String city = getCity();
		String msjEsperadoOK = "cadena ejemplo";
		String msjEsperadoError1 = "cadena ejemplo";
		String msjEsperadoError2 = "cadena ejemplo";
		String nombreSala = "Sala Test";
		if (city!=null) {
			try {
				JSONObject obj = getJSONFromURL("http://api.openweathermap.org/data/2.5/weather?q="
						+ URLEncoder.encode(city, "UTF-8")
						+ "&appid=612a51535e726e4c14f5361e57802030&lang=es&units=metric");

				if (obj!=null) {
					msjEsperadoOK = "Alfred: "+ "Pronostico: " + obj.getJSONArray("weather").getJSONObject(0).getString("description")
							+ " " + "Temperatura: " + obj.getJSONObject("main").getDouble("temp") + " �C. Maxima: "
							+ obj.getJSONObject("main").getDouble("temp_min") + " �C. Minima: "
							+ obj.getJSONObject("main").getDouble("temp_max") + " �C. Presion: "
							+ obj.getJSONObject("main").getInt("pressure") + " hPa. Visibilidad: "
							+ obj.getDouble("visibility") / 10000 + " km/h. Velocidad del viento: "
							+ obj.getJSONObject("wind").getDouble("speed") + " km/h." + "\n";
				} else {
					msjEsperadoError1 = "Error al tratar de conseguir los datos del clima de la ciudad.";
				}
			} catch (UnsupportedEncodingException e) {
				msjEsperadoError1 = "Error al tratar de conseguir los datos del clima de la ciudad.";
				e.printStackTrace();
			}
		} else {
			msjEsperadoError2 = "Error al tratar de conseguir la ciudad del servidor.";
		}
		synchronized (this) {
			this.wait(3000);
		}

		if(msjEsperadoError1.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError1, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else if(msjEsperadoError2.equals(MockServidor.getSalitas().get("Sala Test").getHistorial())) {
			Assert.assertEquals(msjEsperadoError2, MockServidor.getSalitas().get("Sala Test").getHistorial());
		} else {
			Assert.assertEquals(msjEsperadoOK, MockServidor.getSalitas().get("Sala Test").getHistorial());
		}
	}
	

	


	@Test
	public void testInterrupcion() throws UnknownHostException, IOException, InterruptedException {

		PaqueteMensaje pm = new PaqueteMensaje("User Test", "chatbot", "test euro", "Sala Test");
		HashMap<String, String> h = new HashMap<>();
		h.put("euro".toLowerCase(), "Euro");
		ChatBot cb = new ChatBot(h,"mockComandosChatBot");

		MockServidor ms = new MockServidor(cb);
		ms.start();

		synchronized (this) {
			this.wait(2000);
		}

		ms.simularLlegadaMensaje(pm);

		cb.stop();
		final ExpectedException exception = ExpectedException.none();
		exception.expect(InterruptedException.class);

	}
	
	private String getHusoHorario() {
		JSONObject j;
		j = getJSONFromURL("http://ip-api.com/json");
		if (j != null) {
			String r = j.getString("timezone");
			if (!r.equals("N/A"))
				return r;
		} 
		return null;
	}
	private String getCity() {
		JSONObject j;
		j = getJSONFromURL("http://ip-api.com/json");
		if (j!=null) {
			String r = j.getString("city");
			if (r.equals("N/A"))
				return null;
			else
				return r;
		} else {
			return null;
		}
	}

	private JSONObject getJSONFromURL(String dir) {
		try {
			URL url = null;
			url = new URL(dir);
			Scanner scan = null;
			scan = new Scanner(url.openStream(), "UTF-8");
			String str = new String();
			while (scan.hasNext()) {
				str += scan.nextLine();
			}
			scan.close();
			return new JSONObject(str);
		} catch (IOException e) {
			return null;
		} 
	}

}
