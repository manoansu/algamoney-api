package pt.amane.algamoneyapi.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import pt.amane.algamoneyapi.model.Pessoa;
import pt.amane.algamoneyapi.repositories.PessoaRepository;

@Service
public class PessoaService {
 	
	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		
		//pega a informação encontrado acima e limpa
		pessoaSalva.getContatos().clear();
		
		// adiciona a pessoa no contacto
		pessoaSalva.getContatos().addAll(pessoa.getContatos());
		
		//seta cada pessoa com contacto na lista..
		pessoaSalva.getContatos().forEach(c -> c.setPessoa(pessoaSalva));
		
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		return pessoaRepository.save(pessoaSalva);
	}   

	
	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}

	private Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Pessoa pessoaSalva =  pessoaRepository.findById(codigo)
								.orElseThrow(() -> new EmptyResultDataAccessException(1));

		return pessoaSalva;
	}

	public Pessoa salvar(Pessoa pessoa) {
		pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));
		return pessoaRepository.save(pessoa);
	}
}
