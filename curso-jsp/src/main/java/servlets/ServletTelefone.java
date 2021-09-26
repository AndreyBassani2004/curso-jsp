package servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAOTelefoneRepository;
import dao.DAOUsuarioRepository;
import model.ModelLogin;
import model.ModelTelefone;

@WebServlet("/ServletTelefone")
public class ServletTelefone extends ServletGenericUtil {
	private static final long serialVersionUID = 1L;

	private DAOUsuarioRepository daoUsuarioRepository = new DAOUsuarioRepository();
	
	private DAOTelefoneRepository daoTelefoneRepository = new DAOTelefoneRepository();
	public ServletTelefone() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {	
		String acao = request.getParameter("acao");
		
		if(acao != null && !acao.isEmpty() && acao.equals("excluir")) {
			
			String idFone = request.getParameter("id");
			
			String userpai = request.getParameter("userpai");
			
			
			
			daoTelefoneRepository.deleteFone(Long.parseLong(idFone));
			
			ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(Long.parseLong(userpai));
			
	        List<ModelTelefone> modelTelefones = daoTelefoneRepository.listFone(modelLogin.getId());
			
	        
			request.setAttribute("modelTelefones", modelTelefones);
			
			request.setAttribute("msg", "Excluido com sucesso!");
			request.setAttribute("modelLogin", modelLogin);
			request.getRequestDispatcher("principal/telefone.jsp").forward(request, response);
			
			return;
		}
		
		String idUser = request.getParameter("iduser");
		
		if(idUser != null && !idUser.isEmpty()) {
		
			
		ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(Long.parseLong(idUser));
		
        List<ModelTelefone> modelTelefones = daoTelefoneRepository.listFone(modelLogin.getId());
		
		request.setAttribute("modelTelefones", modelTelefones);
		request.setAttribute("modelLogin", modelLogin);
		request.getRequestDispatcher("principal/telefone.jsp").forward(request, response);
		
	
		
		
		}else {
			List<ModelLogin> modelLogins = daoUsuarioRepository.consultaUsuarioList(super.getUserLogado(request));
		     request.setAttribute("modelLogins", modelLogins);
		     request.setAttribute("totalPagina", daoUsuarioRepository.totalPagina(this.getUserLogado(request)));
			 request.getRequestDispatcher("principal/usuario.jsp").forward(request, response);
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
		String usuario_pai_id = request.getParameter("id");
		String numero = request.getParameter("numero");
		
		ModelTelefone modelTelefone = new ModelTelefone();
		
		modelTelefone.setNumero(numero);
		modelTelefone.setUsuario_pai_id(daoUsuarioRepository.consultaUsuarioID(Long.parseLong(usuario_pai_id)));
		modelTelefone.setUsuario_cad_id(super.getUserLogadoObjt(request));
		
		daoTelefoneRepository.gravarTelefone(modelTelefone);
		
		List<ModelTelefone> modelTelefones = daoTelefoneRepository.listFone(Long.parseLong(usuario_pai_id));
		
		ModelLogin modelLogin = daoUsuarioRepository.consultaUsuarioID(Long.parseLong(usuario_pai_id));
		
		request.setAttribute("modelLogin", modelLogin);
		request.setAttribute("modelTelefones", modelTelefones);
		request.setAttribute("msg", "Salvo com sucesso!");
		request.getRequestDispatcher("principal/telefone.jsp").forward(request, response);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
