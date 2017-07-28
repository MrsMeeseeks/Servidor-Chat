package comandosChatBot;

import java.io.*;
import org.json.*;

import servidor.Servidor;

public class GestorMoneda {
	private static GestorMoneda instance = null;
	JSONObject obj;
	String dir;

	/**
	 * Constructor.OBTIENE LOS CODIGOS DE MONEDAS DEL ARCHIVO (ES PRIVATE PORQUE
	 * ES UN SINGLETON).
	 * 
	 * @param dir
	 *            dir URL del codigo de moneda.
	 * @return devuelve un objeto Json.
	 */
	private GestorMoneda(final String dir) {
		this.dir = dir;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(this.dir)));
			String l, j = "";
			while ((l = br.readLine()) != null) {
				j += l;
			}
			this.obj = new JSONObject(j);
		} catch (Exception e) {
			Servidor.getLog().append("Error al obtener el JSON GestorMoneda POLIMORFISMO");
		} finally {
			try {
				if (null != br) {
					br.close();
				}
			} catch (Exception e) {
				Servidor.getLog().append("Error al cerrar JSON GestorMoneda POLIMORFISMO");
			}
		}
	}

	/**
	 * RETORNA CODIGO DE MONEDA O NULL SI NO EXISTE
	 * @param moneda
	 *            codigo de moneda.
	 * @return devuelve codigo de moneda.
	 */
	public String getCodigo(final String moneda) {
		try {
			return this.obj.getString(moneda);
		} catch (JSONException e) {
			if (moneda.length() == 3) {
				return moneda.toUpperCase();
			} else {
				return null;
			}
		}
	}

	/**
	 * Añade un codigo de moneda en ejecucion.
	 * @param moneda
	 *            codigo de moneda.
	 * @param cod
	 *            codigo de moneda.
	 */
	public void addMoneda(final String moneda, final String cod) {
		this.obj.putOpt(moneda, cod);
	}

	/**
	 * Lista los codigos de moneda.
	 * @return retorna la lista de codigos de moneda.
	 */
	public String toString() {
		return this.obj.toString();
	}

	/**
	 * Sirve para almacenar el archivo leido
	 * si se agregaron codigos de moneda.
	 */
	public void guardarArchivo() {
			FileWriter fichero = null;
			PrintWriter pw = null;
			try {
				fichero = new FileWriter(this.dir);
				pw = new PrintWriter(fichero);
				pw.println(this.obj.toString());

			} catch (Exception e) {
				Servidor.getLog().append("No se puede obtener archivo JSON GestorMoneda POLIMORFISMO");
			} finally {
				try {
					if (null != fichero)
						fichero.close();
				} catch (Exception e) {
					Servidor.getLog().append("Error cerrar archivo JSON GestorMoneda POLIMORFISMO");
				}
			}
	}

	/**
	 * Obtiene la instancia de GestorMoneda.
	 * @param cdir codigo de instancia.
	 */
	public static GestorMoneda getInstance(final String cdir) {
		if (instance == null) {
			instance = new GestorMoneda(cdir);
		}
		return instance;
	}

}
