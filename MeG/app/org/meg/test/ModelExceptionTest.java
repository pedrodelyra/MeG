package org.meg.test;

import org.junit.Before;
import org.junit.Test;
import org.meg.exception.QuebraSistemaException;
import org.meg.model.Descricao;

public class ModelExceptionTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected=QuebraSistemaException.class)
	public void testDescricaoException() {
		Descricao descricao = new Descricao();
		descricao.setId(0);
	}
}
