package br.org.meg.test;

import org.junit.Before;
import org.junit.Test;

import br.org.meg.dao.AdministradorDAO;
import br.org.meg.exception.DAOException;
import br.org.meg.model.Administrador;

public class AdministradorDAOTest {

	private Administrador adm;
	private AdministradorDAO dao;
	
	@Before
	public void setUp() throws Exception {
		adm = new Administrador();
		dao = new AdministradorDAO();
	}

	@Test
	public void testAdicionar() {
		adm.setNome("Pedro de Lyra");
		adm.setNomeDeUsuario("pedrodelyra10");
		adm.setEmail("plp_129@hotmail.com");
		adm.setSenha("mudar123");
		dao.adicionar(adm);
	}
	
	@Test
	public void testValidaLogin() {
		dao.validaLogin("pedrodelyra", "mudar123");
	}
	
	@Test(expected = DAOException.class)
	public void testValidaLoginShouldThrowException() {
		dao.validaLogin("pedrodelyra", "senhainvalida");
	}
	
	@Test(expected = DAOException.class)
	public void testExisteNomeDeUsuario() {
		dao.existeNomeDeUsuario("pedrodelyra");
	}
	
	@Test
	public void testExisteNomeDeUsuarioShouldThrowException() {
		dao.existeNomeDeUsuario("usuarioInexistente");
	}

}