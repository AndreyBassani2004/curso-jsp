package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@WebFilter(urlPatterns = {"/principal/*"})//Interceptas todas as requisicoes que vierem do projeto ou mapeamento 
public class FilterAutenticacao implements Filter {

   
    public FilterAutenticacao() {
       
    }

	//Encerra os processos quando o servidor é parado
    //Mataria conexao do banco
	public void destroy() {
		
	}

	//intercepta todas as requisicoes no sistema
	//Tudo que o sistema vai fazer passa por ele
	//Validar autenticacao
	//Dar commit e roolback de transacoes do banco
	//Validar e fazer redirecionamento de pagina
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		
		String usuarioLogado = (String) session.getAttribute("usuario");
		
		String urlParaAutenticar = req.getServletPath();//url sendo acessada
		
		//Validar se estar logado, se nao redirecionar para tela de login
		if(usuarioLogado == null  && !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin") ) {//Nao Logado
			
			RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);
			request.setAttribute("msg", "Por favor realize o login");
			redireciona.forward(request, response);
			return;//Para a execucao e direciona para o login 
		}else {
		
		chain.doFilter(request, response);
		}
	}

	//Inicia os processos e configuracao do servidor sobre o projeto
	//Iniciar conexao com o banco
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
