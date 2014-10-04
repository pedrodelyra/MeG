package br.org.meg.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import br.org.meg.dao.QuadroDAO;
import br.org.meg.exception.UploadArquivoException;
import br.org.meg.model.Descricao;
import br.org.meg.model.Estado;
import br.org.meg.model.Quadro;
import br.org.meg.model.Secao;

public class Parser {
	private Scanner scanner;
	private int anoInicial;
	private int anoFinal;
	private int quantidadeEstados;
	private int quantidadeSecoes;
	private String arquivo;
	
	public Parser(String arquivo, int quantidadeEstados, int quantidadeSecoes, 
			int anoInicial, int anoFinal) {
		
		if (anoFinal <= anoInicial || quantidadeEstados == 0 ||
				quantidadeSecoes == 0) {
			throw new IllegalArgumentException("Argumentos do parser inválidos!");
		}
		this.arquivo = arquivo;
		this.quantidadeEstados = quantidadeEstados;
		this.quantidadeSecoes = quantidadeSecoes;
		this.anoInicial = anoInicial;
		this.anoFinal = anoFinal;
	}
	
	@SuppressWarnings("resource")
	public void validarSecao(String secao) throws FileNotFoundException {
		scanner = new Scanner(new FileReader(this.arquivo)).useDelimiter(";");
		this.lerCabecalho();
		boolean contemSecao = false;
		String lalala;
		while (scanner.hasNext()) {
			lalala = scanner.next();
			lalala = lalala.substring(1, lalala.length() - 1);
			if (lalala.equals(secao)) {
				contemSecao = true;
				break;
			}
		}
		
		if (!contemSecao) throw new UploadArquivoException("Dados de entrada incompatíveis com o arquivo!");
		
		scanner.close();
	}
	
	@SuppressWarnings("resource")
	public void validarAno(int anoInicial, int anoFinal) throws FileNotFoundException {
		scanner = new Scanner(new FileReader(this.arquivo)).useDelimiter(";");
		this.lerCabecalho();
		String[] tokens = scanner.nextLine().split(";");
		System.out.printf("length: %d ano final: %d ano inicial: %d quantidadeDeSecoes: %d\n", tokens.length, 
				anoFinal, anoInicial, this.quantidadeSecoes);
		if (tokens.length != ((anoFinal - anoInicial + 1) * 5) + 2) {
			throw new UploadArquivoException("Dados de entrada incompatíveis com o arquivo!");
		}

		scanner.close();
	}
	
	@SuppressWarnings("resource")
	public void parse() throws FileNotFoundException {
		scanner = new Scanner(new FileReader(this.arquivo)).useDelimiter(";");
		ArrayList<Quadro> quadros = this.lerEstados();
		QuadroDAO dao = new QuadroDAO();		
		for (int j = 0; j < quadros.size(); j++) {
			dao.adicionar(quadros.get(j));
		}
		scanner.close();
	}

	private ArrayList<Quadro> lerEstados() {
		ArrayList<Quadro> quadros = new ArrayList<>();
		Estado estado;
		Secao secao;
		String[] tokens;
		int tempo = this.anoFinal - this.anoInicial + 1;
		this.lerCabecalho();
		
		for (int j = 0; j < this.quantidadeEstados; j++) {
			for (int i = 0; i < this.quantidadeSecoes; i++) { 
				tokens = scanner.nextLine().split(";");
				estado = new Estado();
				estado.setNome(tokens[0].substring(1, tokens[0].length() - 1));
				secao = new Secao();
				secao.setNome(tokens[1].substring(3, tokens[1].length() - 1));
				for (int k = 0; k < 5 * tempo; k++) {
						quadros.add(new Quadro());
						quadros.get(quadros.size() - 1).setEstado(estado);
						quadros.get(quadros.size() - 1).setSecao(secao);
						quadros.get(quadros.size() - 1).setAno(this.anoInicial + k%tempo);
						Descricao descricao = new Descricao();
						descricao.setId(1 + k/tempo);
						quadros.get(quadros.size() - 1).setDescricao(descricao);
						if (!tokens[2 + k].equals("-") && !tokens[2 + k].equals("X")) {
							if (k < 28) quadros.get(quadros.size() - 1).setValor(Float.parseFloat(tokens[2 + k]));
							else quadros.get(quadros.size() - 1).setValor(Float.parseFloat(corrigirVirgula(tokens[2 + k])));
						} else quadros.get(quadros.size() - 1).setValor(-1.0f);
						System.out.printf("Tipo: %s Estado: %s Secao: %s Ano: %d Valor: %.1f\n",
						 		quadros.get(quadros.size() - 1).getDescricao().getNome(), quadros.get(quadros.size() - 1).getEstado().getNome(), 
						 		quadros.get(quadros.size() - 1).getSecao().getNome(), 
						 		quadros.get(quadros.size() - 1).getAno(), quadros.get(quadros.size() - 1).getValor());
						 
				}
			}
		}
		
		return quadros;
	}	
	
	private void lerCabecalho() {
		for (int i = 0; i < 5; i++) {
			scanner.nextLine();
		}
	}
	
	private String corrigirVirgula(String expressao) {
		char[] string = new char[expressao.length()];
		for (int i = 0; i < expressao.length() && expressao.charAt(i) != '\n'; i++) {
			if (expressao.charAt(i) == ',') string[i] = '.';
			else string[i] = expressao.charAt(i);
		}
		String expressaoCorrigida = new String(string);
		return expressaoCorrigida;
	}
}
