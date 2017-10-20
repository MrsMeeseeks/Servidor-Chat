package Tests;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import paqueteEnvios.PaqueteMensaje;
import paqueteEnvios.PaqueteSala;
import paqueteEnvios.PaqueteUsuario;
import servidor.Conector;
import servidor.Servidor;

public class ConectorTest {

	@Test
	public void testConexionConLaDB() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();
		
		Assert.assertEquals(1, 1);
		
		conector.close();
		
	}
	
	@Test
	public void testRegistrarUsuarioFallido() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();

		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setUsername("UserTest");
		pu.setPassword("test");

		try {
			conector.registrarUsuario(pu);
		} catch (IOException e) {
			e.printStackTrace();
		}

		pu = conector.getUsuario("UserTest");
		conector.close();

		Assert.assertEquals("UserTest", pu.getUsername());
	}
	
	@Test
	public void testLoginUsuarioFallido() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();

		PaqueteUsuario pu = new PaqueteUsuario();
		pu.setUsername("userInventado");
		pu.setPassword("test");

		boolean resultadoLogin = conector.loguearUsuario(pu);
		
		conector.close();
		
		Assert.assertEquals(false, resultadoLogin);
	}
	
	@Test
	public void testRegistrarSala() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();

		PaqueteSala ps = new PaqueteSala();
		ps.setNombreSala("sala test");
		ps.setOwnerSala("testOWNER");

		boolean resultadoCrearSala = conector.registrarSala(ps);
		conector.close();
		Assert.assertEquals(true, resultadoCrearSala);
	}
	
	@Test
	public void testGuardarChatSala() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();

		PaqueteMensaje pm = new PaqueteMensaje();
		pm.setNombreSala("sala test");
		pm.setMsj("mensaje test");
		pm.setUserEmisor("test1");
		pm.setUserReceptor("test2");

		boolean resultadoGuardarChatSala = conector.guardarChatSala(pm);
		
		conector.close();
		Assert.assertEquals(true, resultadoGuardarChatSala);
	}
	
	@Test
	public void testRegistrarSalaFallido() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();

		PaqueteSala ps = new PaqueteSala();
		ps.setNombreSala("sala test");
		ps.setOwnerSala("testOWNER");

		boolean resultadoCrearSala = conector.registrarSala(ps);
		conector.close();
		Assert.assertEquals(false, resultadoCrearSala);
	}
		
	@Test
	public void testEliminarSala() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();

		PaqueteSala ps = new PaqueteSala();
		ps.setNombreSala("sala test");
		ps.setOwnerSala("testOWNER");

		boolean resultadoEliminarSala = conector.eliminarSala(ps);
		conector.close();
		Assert.assertEquals(true, resultadoEliminarSala);
	}
	
	
//	@Test
//	public void testAgregarPalabrasClaveChatBot() {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//		
//		String palabraA = "pa";
//		String palabraB = "pb";
//		
//		boolean resultadoGuardarChatSala = conector.AgregarPalabrasClaveChatBot(palabraA, palabraB);
//		
//		conector.close();
//		
//		Assert.assertEquals(true, resultadoGuardarChatSala);		
//	}
	
	@Test
	public void testCargarPalabrasClaveChatBot() {
		new Servidor();
		Servidor.main(null);

		Conector conector = new Conector();
		conector.connect();
		
		String palabraA = "usd";
		String palabraB = "Dolar";
		
		boolean resultadoCargarPalabrasClave = conector.cargarPalabrasClaveChatBot();
		
		Assert.assertEquals(true, resultadoCargarPalabrasClave);
		
		HashMap<String,String> sinonimosTest = Servidor.alfred.getSinonimos();
		
		conector.close();
		
		Assert.assertEquals(true, sinonimosTest.containsKey(palabraA));
		
		Assert.assertEquals(palabraB, sinonimosTest.get(palabraA));
	}
	
//	@Test
//	public void testEliminarSinonimo() {
//		new Servidor();
//		Servidor.main(null);
//
//		Conector conector = new Conector();
//		conector.connect();
//
//		String palabraA = "pa";
//		
//		boolean resultadoEliminarSala = conector.EliminarSinonimo(palabraA);
//				
//		conector.close();
//		Assert.assertEquals(true, resultadoEliminarSala);
//	}
}
