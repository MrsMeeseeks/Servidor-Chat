package servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import intefaces.Sala;
import paqueteEnvios.PaqueteSala;
import paqueteEnvios.PaqueteUsuario;

public class Conector {

	private String url = "usuariosChat.bd";
	static Connection connect;


	public void connect() {
		try {
			Servidor.log.append("Estableciendo conexi�n con la base de datos..." + System.lineSeparator());
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			Servidor.log.append("Conexión con la base de datos establecida con éxito." + System.lineSeparator());
		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar establecer la conexión con la base de datos. " + ex.getMessage()
					+ System.lineSeparator());
		}
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Servidor.log.append("Error al intentar cerrar la conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			PreparedStatement st1 = connect.prepareStatement("SELECT * FROM registro WHERE usuario= ? ");
			st1.setString(1, user.getUsername());
			result = st1.executeQuery();

			if (!result.next()) {

				PreparedStatement st = connect.prepareStatement("INSERT INTO registro (usuario, password) VALUES (?,?)");
				st.setString(1, user.getUsername());
				st.setString(2, user.getPassword());
				st.execute();
				Servidor.log.append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
				return true;
			} else {
				Servidor.log.append("El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
				return false;
			}
		} catch (SQLException ex) {
			Servidor.log.append("Eror al intentar registrar el usuario " + user.getUsername() + System.lineSeparator());
			System.err.println(ex.getMessage());
			return false;
		}

	}

	
	public boolean loguearUsuario(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ? AND password = ? ");
			st.setString(1, user.getUsername());
			st.setString(2, user.getPassword());
			result = st.executeQuery();

			if (result.next()) {
				Servidor.log.append("El usuario " + user.getUsername() + " ha iniciado sesi�n." + System.lineSeparator());
				return true;
			}
			
			Servidor.log.append("El usuario " + user.getUsername() + " ha realizado un intento fallido de inicio de sesi�n." + System.lineSeparator());
			return false;

		} catch (SQLException e) {
			Servidor.log.append("El usuario " + user.getUsername() + " fallo al iniciar sesi�n." + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean cargarChatSalas(PaqueteSala salas) {
		ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("SELECT * FROM Salas WHERE Name = ?");
			st.setString(1, salas.getNombreSala());
			result = st.executeQuery();
			
			salas.setTexto(result.getString("Chat"));

			if (result.next()) {
				Servidor.log.append("La Sala " + salas.getNombreSala() + " ha cargado el historial de chat correctamente" + System.lineSeparator());
				return true;
			}
			
			Servidor.log.append("La Sala " + salas.getNombreSala() + " ha realizado un intento fallido de carga del historial de chat" + System.lineSeparator());
			return false;

		} catch (SQLException e) {
			Servidor.log.append("La Sala " + salas.getNombreSala() + " fallo al cargar el historial de chat." + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean registrarSala(PaqueteSala paqueteSala) {
		ResultSet result = null;
		try {
			PreparedStatement st1 = connect.prepareStatement("SELECT * FROM Salas WHERE Name = ? ");
			st1.setString(1, paqueteSala.getNombreSala());
			result = st1.executeQuery();

			if (!result.next()) {

				PreparedStatement st = connect.prepareStatement("INSERT INTO Salas (Name, Chat) VALUES (?,?)");
				st.setString(1, paqueteSala.getNombreSala());
				st.setString(2, paqueteSala.getHistorial());
				st.execute();
				Servidor.log.append("La sala  " + paqueteSala.getNombreSala() + " se ha registrado." + System.lineSeparator());
				return true;
			} else {
				Servidor.log.append("La sala " + paqueteSala.getNombreSala() + " ya se existe." + System.lineSeparator());
				return false;
			}
		} catch (SQLException ex) {
			Servidor.log.append("Eror al intentar registrar la sala " + paqueteSala.getNombreSala() + System.lineSeparator());
			System.err.println(ex.getMessage());
			return false;
		}
	}
	
	public boolean eliminarSala(PaqueteSala paqueteSala) {
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM Salas WHERE Name = ? ");
			st.setString(1, paqueteSala.getNombreSala());
			st.execute();
			Servidor.log.append("La sala  " + paqueteSala.getNombreSala() + " ha sido eliminada." + System.lineSeparator());
			return true;
		} catch (SQLException ex) {
			Servidor.log.append("Eror al intentar Eliminar la sala " + paqueteSala.getNombreSala() + System.lineSeparator());
			System.err.println(ex.getMessage());
			return false;
		}
	}
	
	public boolean guardarChatSala(PaqueteSala salas) {
		//ResultSet result = null;
		try {
			PreparedStatement st = connect.prepareStatement("UPDATE Salas SET Chat = Chat || ? WHERE Name = ?");
			st.setString(1, salas.getTexto());
			st.setString(2, salas.getNombreSala());
			st.executeUpdate();
			return true;

		} catch (SQLException e) {
			Servidor.log.append("La Sala " + salas.getNombreSala() + " fallo al intentar actualiarze en la BD" + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}

	public PaqueteUsuario getUsuario(String usuario) {
		ResultSet result = null;
		PreparedStatement st;
		
		try {
			st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, usuario);
			result = st.executeQuery();

			String password = result.getString("password");
			
			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
			
			return paqueteUsuario;
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}
		
		return new PaqueteUsuario();
	}

	public void cargarSalasExistentes() {
		ResultSet result = null;
		PreparedStatement st;
		
		try {
			st = connect.prepareStatement("SELECT COUNT (*) FROM Salas");
			result = st.executeQuery();
			int cant = result.getInt(1);
			st = connect.prepareStatement("SELECT * FROM Salas");
			result = st.executeQuery();
			result.next();
			for (int i = 0; i< cant; i++) {
				Servidor.getNombresSalasDisponibles().add(result.getString("Name"));
				Servidor.getSalas().put(result.getString("Name"),new PaqueteSala (result.getString("Name"),result.getString("Chat")));
				result.next();
			}
			Servidor.log.append("Se cargaron las salas existentes en la base de datos con éxito." + System.lineSeparator());
		} catch (SQLException e) {
			Servidor.log.append("Error al intentar cargar las salas existentes en la base de datos." + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}
		
	}

	
}