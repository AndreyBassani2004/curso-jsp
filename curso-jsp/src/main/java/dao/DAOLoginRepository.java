package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.SingleConnectionBanco;
import model.ModelLogin;

public class DAOLoginRepository {

	private Connection connection;

	// Conectar BD
	public DAOLoginRepository() {
		connection = SingleConnectionBanco.getConnection();

	}

	public boolean validarAutenticacao(ModelLogin modelLogin) throws Exception {

		// Comando sql para validacao
		String sql = "select * from model_login where upper(login) = upper(?) and upper(senha) = upper(?) ";

		// Conectar BD
		PreparedStatement statement = connection.prepareStatement(sql);

		// Atribultos para o login
		statement.setString(1, modelLogin.getLogin());
		statement.setString(2, modelLogin.getSenha());

		ResultSet resultSet = statement.executeQuery();

		if (resultSet.next()) {
			return true; // Autenticado
		}

		return false; // Nao Autenticado

	}

}
