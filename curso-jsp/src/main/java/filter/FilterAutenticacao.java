package filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import connection.SingleConnectionBanco;
import dao.DaoVersionadorBanco;

@WebFilter(urlPatterns = { "/principal/*" }) // Interceptas todas as requisicoes que vierem do projeto ou mapeamento
public class FilterAutenticacao implements Filter {

	private static Connection connection;

	public FilterAutenticacao() {

	}

	// Encerra os processos quando o servidor é parado
	// Mataria conexao do banco
	public void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// intercepta todas as requisicoes no sistema
	// Tudo que o sistema vai fazer passa por ele
	// Validar autenticacao
	// Dar commit e roolback de transacoes do banco
	// Validar e fazer redirecionamento de pagina
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();

			String usuarioLogado = (String) session.getAttribute("usuario");

			String urlParaAutenticar = req.getServletPath();// url sendo acessada

			// Validar se estar logado, se nao redirecionar para tela de login
			if (usuarioLogado == null && !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) {// Nao Logado

				RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);
				request.setAttribute("msg", "Por favor realize o login");
				redireciona.forward(request, response);
				return;// Para a execucao e direciona para o login
			} else {

				chain.doFilter(request, response);
			}
			connection.commit();//deu certo, commit no banco de dados
		} catch (Exception e) {
			e.printStackTrace();
			
			RequestDispatcher redirecionar = request.getRequestDispatcher("erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redirecionar.forward(request, response);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	// Inicia os processos e configuracao do servidor sobre o projeto
	// Iniciar conexao com o banco
	public void init(FilterConfig fConfig) throws ServletException {
		connection = SingleConnectionBanco.getConnection();

		DaoVersionadorBanco daoVersionadorBanco = new DaoVersionadorBanco();
		
		String caminhoPastaSQL = fConfig.getServletContext().getRealPath("versionamentodobancosql") + File.separator;
		
		File[] filesSql = new File(caminhoPastaSQL).listFiles();
		
		try {
		for (File file : filesSql) {
			
			boolean arquivoJaRodado = daoVersionadorBanco.arquivoSQLRodado(file.getName());
			
			if(!arquivoJaRodado) {
				
				FileInputStream entradaArquivo = new FileInputStream(file);
				
				Scanner lerArquivo = new Scanner(entradaArquivo, "UTF-8");
				
				StringBuilder sql = new StringBuilder();
				while (lerArquivo.hasNext()) {
					
					sql.append(lerArquivo.nextLine());
					sql.append("\n");
				}
				
				connection.prepareStatement(sql.toString()).execute();
				daoVersionadorBanco.gravaArquivoSqlRodado(file.getName());
				connection.commit();
				lerArquivo.close();
			}
			
		}
		}catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

}
