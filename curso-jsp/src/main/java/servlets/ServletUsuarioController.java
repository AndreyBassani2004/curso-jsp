package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.codec.binary.Base64;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.DAOUsuarioRepository;
import model.ModelLogin;

@MultipartConfig
@WebServlet(urlPatterns = {"/ServletUsuarioController"})
public class ServletUsuarioController extends ServletGenericUtil {
	private static final long serialVersionUID = 1L;

	private DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();

	public ServletUsuarioController() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
		
		String acao = request.getParameter("acao");
		
		if(acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("deletar")) {
			String idUser = request.getParameter("id");
			
			daoUsuarioRepository.deletarUser(idUser);
			
			List<ModelLogin> modelLogins = daoUsuarioRepository.consultaUsuarioList(super.getUserLogado(request));
			request.setAttribute("modelLogins", modelLogins);
			
			request.setAttribute("msg", "Excluido com sucesso!");
		
			request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);
			
			
		}else if(acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("deletarajax")) {
			 String idUser = request.getParameter("id");
			 
			 
			 
			 daoUsuarioRepository.deletarUser(idUser);
			 
			 response.getWriter().write("Excluido com sucesso!");
			
		} else if(acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("buscarUserAjax")) {
			 String nomeBusca = request.getParameter("nomeBusca");
			 
			 List<ModelLogin> dadosJsonUser = daoUsuarioRepository.consultaUsuarioList(super.getUserLogado(request));
			 
			 ObjectMapper mapper = new ObjectMapper();
			 String json = mapper.writeValueAsString(dadosJsonUser);
			 
			 response.getWriter().write(json);
			
		}else if(acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("buscarEditar")) {
			String id = request.getParameter("id");
			
			
			
			List<ModelLogin> modelLogins = daoUsuarioRepository.consultaUsuarioList(super.getUserLogado(request));
			request.setAttribute("modelLogins", modelLogins);
			
			ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(id, super.getUserLogado(request));
			
			request.setAttribute("msg", "Usuario em edicao");
			request.setAttribute("modelLogin", modelLogin);
			request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);
			
		} else if(acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("listarDados")) {
			 String id = request.getParameter("id");
			 
			 List<ModelLogin> dadosJsonUser = daoUsuarioRepository.listarDados(id);
			 
			 ObjectMapper mapper = new ObjectMapper();
			 String json = mapper.writeValueAsString(dadosJsonUser);
			 
			 response.getWriter().write(json);
			 
		} else if(acao != null && !acao.isEmpty() && acao.equalsIgnoreCase("listarUser")) {
			
			List<ModelLogin> modelLogins = daoUsuarioRepository.consultaUsuarioList(super.getUserLogado(request));
			

			request.setAttribute("msg", "Usuario carregados!");
			request.setAttribute("modelLogins", modelLogins);
			request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);
			
		}else {
			
			request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);
		}
		
		}catch (Exception e) {
			e.printStackTrace();
			RequestDispatcher redirecionar = request.getRequestDispatcher("erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redirecionar.forward(request, response);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			String msg = "Operacao realizada com sucesso!";

			String id = request.getParameter("id");
			String nome = request.getParameter("nome");
			String email = request.getParameter("email");
			String login = request.getParameter("login");
			String senha = request.getParameter("senha");
			String perfil = request.getParameter("perfil");
			String sexo = request.getParameter("sexo");

			ModelLogin modelLogin = new ModelLogin();
			modelLogin.setId(id != null && !id.isEmpty() ? Long.parseLong(id) : null);
			modelLogin.setNome(nome);
			modelLogin.setEmail(email);
			modelLogin.setLogin(login);
			modelLogin.setSenha(senha);
			modelLogin.setPerfil(perfil);
			modelLogin.setSexo(sexo);
			
			
			if (ServletFileUpload.isMultipartContent(request)) {
				
				Part part = request.getPart("fileFoto"); /*Pega foto da tela*/
				byte[] foto = IOUtils.toByteArray(part.getInputStream());/*Converte imagem para byte*/
				String imagemBase64 = "data:image/"+ part.getContentType().split("\\/")[1] + ";base64," + new Base64().encodeBase64String(foto);
				
				modelLogin.setFotouser(imagemBase64);
				modelLogin.setExtencaofotouser(part.getContentType().split("\\/")[1]);
					
				
				
			}

			if (daoUsuarioRepository.validarLogin(modelLogin.getLogin()) && modelLogin.getId() == null) {
				msg = "Já existe usuario com o mesmo login, informe outro login.";
			} else {
				if(modelLogin.isNovo()) {
					msg = "Gravado com sucesso!";
				}else {
					msg = "Atualizado com sucesso!";
				}
				modelLogin = daoUsuarioRepository.gravarUsuario(modelLogin, super.getUserLogado(request));
				
			}

			request.setAttribute("msg", msg);
			request.setAttribute("modelLogin", modelLogin);
			request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();

			RequestDispatcher redirecionar = request.getRequestDispatcher("erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redirecionar.forward(request, response);
		}

	}

}
