package conexion.Web;

import java.text.Normalizer;

public class MainNoMeGustanLosJUnit {

	public static void main(String[] args) {
		
		System.out.println("-----FINANZAS-----");
		System.out.println("Precio moneda con codigo directo: " + DatosWeb.getPrecio("usd"));
		System.out.println("Precio con nombre moneda: " + DatosWeb.getPrecio("dolar"));
		System.out.println("Precio moneda invalida: " + DatosWeb.getPrecio("zzz", "ars"));
		System.out.println("Convertir 100 usd a pesos: " + DatosWeb.convertir(200, "USD"));
		System.out.println("");
		System.out.println("-----CLIMA-----");
		Clima c = new Clima("Buenos Aires, Argentina");
		System.out.println(c.toString());
		System.out.println("");
		System.out.println("-----FECHA Y HORA-----");
		System.out.println(DatosWeb.getFechaHora("GMT-3").toString());
		System.out.println("");
		System.out.println("-----WOLFRAM-----");
		System.out.println(DatosWeb.consultarWolfram("2+2*5/3"));
		System.out.println(DatosWeb.consultarWolfram("population france"));
		System.out.println(DatosWeb.consultarWolfram("apple iron quantity"));
		System.out.println(DatosWeb.consultarWolfram("solve x=20*y+5 for y") + "   ------ ESTA MIERDA MUESTRA UN \"?\" EN VEZ DE \"=\"");
	}
}


