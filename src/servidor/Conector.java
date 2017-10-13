package servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import paqueteEnvios.PaqueteMensaje;
import paqueteEnvios.PaqueteSala;
import paqueteEnvios.PaqueteUsuario;

public class Conector {

	private String url = "usuariosChat.bd";
	static Connection connect;


	public void connect() {
		try {
			Servidor.getLog().append("Estableciendo conexión con la base de datos..." + System.lineSeparator());
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			Servidor.getLog().append("Conexión con la base de datos establecida con éxito." + System.lineSeparator());
		} catch (SQLException ex) {
			Servidor.getLog().append("Fallo al intentar establecer la conexión con la base de datos. " + ex.getMessage()
			+ System.lineSeparator());
		}
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Servidor.getLog().append("Error al intentar cerrar la conexión con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
		try {
			PreparedStatement st = connect.prepareStatement("INSERT INTO registro (usuario, password) VALUES (?,?)");//, foto) VALUES (?,?,?)");
			st.setString(1, user.getUsername());
			st.setString(2, user.getPassword());
//			st.setString(3, "noItem");
			st.execute();
			Servidor.getLog().append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
			return true;
		} catch (SQLException ex) {
			Servidor.getLog().append("Eror al intentar registrar el usuario " + user.getUsername() + System.lineSeparator());
			return false;
		}
	}


	public boolean loguearUsuario(PaqueteUsuario user) {

		ResultSet result = null;
		try {
			PreparedStatement st = connect
					.prepareStatement("SELECT * FROM registro WHERE usuario = ? AND password = ? ");
			st.setString(1, user.getUsername());
			st.setString(2, user.getPassword());
			result = st.executeQuery();

			if (result.next()) {
				Servidor.getLog().append(
						"El usuario " + user.getUsername() + " ha iniciado sesión." + System.lineSeparator());
				return true;
			}

			Servidor.getLog().append("El usuario " + user.getUsername()
			+ " ha realizado un intento fallido de inicio de sesión." + System.lineSeparator());
			return false;

		} catch (SQLException e) {

			e.printStackTrace();
			return false;
		} 
	}

	public boolean registrarSala(PaqueteSala paqueteSala) {
		try {
			PreparedStatement st = connect.prepareStatement("INSERT INTO Salas (Name, Chat, Owner) VALUES (?,?,?)");
			st.setString(1, paqueteSala.getNombreSala());
			st.setString(2, paqueteSala.getHistorial());
			st.setString(3, paqueteSala.getOwnerSala());
			st.execute();
			Servidor.getLog().append("La sala  " + paqueteSala.getNombreSala() + " se ha registrado." + System.lineSeparator());
			return true;
		} catch (SQLException ex) {
			Servidor.getLog().append("Eror al intentar registrar la sala " + paqueteSala.getNombreSala() + System.lineSeparator());
			return false;
		}
	}

	public boolean eliminarSala(PaqueteSala paqueteSala) {
		try {
			PreparedStatement st = connect.prepareStatement("DELETE FROM Salas WHERE Name = ? ");
			st.setString(1, paqueteSala.getNombreSala());
			st.execute();
			Servidor.getLog().append("La sala  " + paqueteSala.getNombreSala() + " ha sido eliminada." + System.lineSeparator());
			return true;
		} catch (SQLException ex) {
			Servidor.getLog().append("Eror al intentar eliminar la sala " + paqueteSala.getNombreSala() + System.lineSeparator());
			return false;
		}
	}

	public boolean guardarChatSala(PaqueteMensaje msj) {
		try {
			PreparedStatement st = connect.prepareStatement("UPDATE Salas SET Chat = Chat || ? WHERE Name = ?");
			st.setString(1, msj.getUserEmisor() + ": " + msj.getMsjChat() + "\n");
			st.setString(2, msj.getNombreSala());
			st.executeUpdate();
			return true;

		} catch (SQLException e) {
			Servidor.getLog().append("La Sala " + msj.getNombreSala() + " fallo al intentar actualiarze en la BD" + System.lineSeparator());
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
//			String fotoPerfil = result.getString("foto");

			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
//			paqueteUsuario.setFotoPerfil(paqueteUsuario.buscarFotoPerfil(fotoPerfil));

			return paqueteUsuario;
		} catch (SQLException e) {
			Servidor.getLog().append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
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
				Servidor.getSalas().put(result.getString("Name"),new PaqueteSala (result.getString("Name"),result.getString("Chat"),result.getString("Owner")));
				result.next();
			}
			Servidor.getLog().append("Se cargaron las salas existentes en la base de datos con éxito." + System.lineSeparator());
		} catch (SQLException e) {
			Servidor.getLog().append("Error al intentar cargar las salas existentes en la base de datos." + System.lineSeparator());
			Servidor.getLog().append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}

	}


	public boolean cargarPalabrasClaveChatBot() {
		ResultSet result = null;
		PreparedStatement st;
		
		HashMap<String, String> sinonimos = new HashMap<>();
		
		try {
			st = connect.prepareStatement("SELECT COUNT (*) FROM sinonimos");
			result = st.executeQuery();
			int cant = result.getInt(1);
			
			st = connect.prepareStatement("SELECT * FROM sinonimos");
			result = st.executeQuery();
			result.next();
			
			for (int i = 0; i< cant; i++) {
				sinonimos.put(result.getString("palabraA").toLowerCase(), result.getString("palabraB"));
				result.next();
			}
			Servidor.alfred = new ChatBot(sinonimos);
			Servidor.log.append("Se cargo Alfred correctamente." + System.lineSeparator());
			return true;
		} catch (SQLException e) {
			Servidor.log.append("Error al intentar cargar Alfred." + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}

	public boolean yaRegistrado(String user) {
		ResultSet result = null;
		if (!user.toLowerCase().equals("chatbot")) {
			try {
				PreparedStatement st1 = connect.prepareStatement("SELECT * FROM registro WHERE usuario= ? ");
				st1.setString(1, user);
				result = st1.executeQuery();

				if (!result.next()) {
					return false;
				} else {
					Servidor.getLog()
							.append("El usuario " + user + " ya se encuentra en uso." + System.lineSeparator());
					return true;
				}
			} catch (SQLException ex) {
				Servidor.getLog().append("Eror al intentar verificar si el usuario " + user
						+ " ya se encontraba en uso." + System.lineSeparator());
				return false;
			} 
		} else {
			return false;
		}
	}

	public boolean salaYaExistente(String sala) {
		ResultSet result = null;
		try {
			PreparedStatement st1 = connect.prepareStatement("SELECT * FROM Salas WHERE Name = ? ");
			st1.setString(1, sala);
			result = st1.executeQuery();

			if (!result.next()) {
				Servidor.getLog().append("La sala " + sala + " ya se existe." + System.lineSeparator());
				return false;
			} else {
				return true;
			}
		} catch (SQLException ex) {
			Servidor.getLog().append("Eror al intentar verificar si la sala " + sala + " ya existe."  + System.lineSeparator());
			return true;
		}
	}
	
//	public boolean actualizarPerfil(PaqueteUsuario user) {
//		try {
//			PreparedStatement st = connect.prepareStatement("UPDATE registro SET foto = ? WHERE usuario = ?");
//			st.setString(1, user.getNombreFoto());
//			st.setString(2, user.getUsername());
//			st.executeUpdate();
//			return true;
//
//		} catch (SQLException e) {
//			Servidor.getLog().append("La foto " + user.getNombreFoto() + " fallo al intentar actualiarze en la BD" + System.lineSeparator());
//			e.printStackTrace();
//			return false;
//		}
//	}
}