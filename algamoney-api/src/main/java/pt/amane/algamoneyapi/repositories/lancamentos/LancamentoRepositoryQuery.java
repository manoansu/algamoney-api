package pt.amane.algamoneyapi.repositories.lancamentos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasCategoriaDTO;
import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasDiarioDTO;
import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasPessoaDTO;
import pt.amane.algamoneyapi.model.Lancamento;
import pt.amane.algamoneyapi.repositories.filters.LancamentoFilter;
import pt.amane.algamoneyapi.repositories.projections.ResumoLancamento;

public interface LancamentoRepositoryQuery {
	
	public List<LancamentoEstatisticasCategoriaDTO> porCategoria(LocalDate mesreferencia);
	
	public List<LancamentoEstatisticasDiarioDTO> porDia(LocalDate mesreferencia);
	
	public List<LancamentoEstatisticasPessoaDTO> porPessoa(LocalDate inicio, LocalDate fim);

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
	
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
}
