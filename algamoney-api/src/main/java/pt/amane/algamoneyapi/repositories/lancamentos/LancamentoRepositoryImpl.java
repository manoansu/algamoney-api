package pt.amane.algamoneyapi.repositories.lancamentos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasCategoriaDTO;
import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasDiarioDTO;
import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasPessoaDTO;
import pt.amane.algamoneyapi.model.Categoria_;
import pt.amane.algamoneyapi.model.Lancamento;
import pt.amane.algamoneyapi.model.Lancamento_;
import pt.amane.algamoneyapi.model.Pessoa_;
import pt.amane.algamoneyapi.repositories.filters.LancamentoFilter;
import pt.amane.algamoneyapi.repositories.projections.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// Criar as restrições com array de predicate
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	// criar a lista de pradicate...
	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();

		/**
		 * where descricao like '%informaçao que deseja filtrar%' o uso de metamodel que
		 * gera uma classes e regera as classes para evitar o erro.
		 */

		if (!ObjectUtils.isEmpty(lancamentoFilter.getDescricao())) {
			predicates.add(builder.like(builder.lower(root.get(Lancamento_.descricao)),
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}

		if (lancamentoFilter.getDataVencimentoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoDe()));
		}

		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoAte()));
		}

		return predicates.toArray(new Predicate[predicates.size()]);
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		criteria.select(builder.construct(ResumoLancamento.class, root.get(Lancamento_.codigo),
				root.get(Lancamento_.descricao), root.get(Lancamento_.dataVencimento),
				root.get(Lancamento_.dataPagamento), root.get(Lancamento_.valor), root.get(Lancamento_.tipo),
				root.get(Lancamento_.categoria).get(Categoria_.nome), root.get(Lancamento_.pessoa).get(Pessoa_.nome)));

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}

	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}

	@Override
	public List<LancamentoEstatisticasCategoriaDTO> porCategoria(LocalDate mesreferencia) {

		// criando o criteria builer
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

		// criando generic de criteriaQuery
		CriteriaQuery<LancamentoEstatisticasCategoriaDTO> criteriaQuery = criteriaBuilder
				.createQuery(LancamentoEstatisticasCategoriaDTO.class);

		// criando generic de Root
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

		// constroi o objeto
		criteriaQuery.select(criteriaBuilder.construct(LancamentoEstatisticasCategoriaDTO.class,
				root.get(Lancamento_.categoria), criteriaBuilder.sum(root.get(Lancamento_.valor))));

		// pegar o 1º e ultimo dia do mes..
		LocalDate primeiroDia = mesreferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesreferencia.withDayOfMonth(mesreferencia.lengthOfMonth());

		// criando a clausula where nas consultas onde verificamos q a dt de vencimento
		// tem que ser >= 1º dia
		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
				// verificamos q a dt de vencimento tem que ser <= ultimo dia
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia));

		// criando o agrupamento por propriedades categoria no banco..
		criteriaQuery.groupBy(root.get(Lancamento_.categoria));

		// criando o resultado que retorna a lista de objeto query
		TypedQuery<LancamentoEstatisticasCategoriaDTO> typedQuery = manager.createQuery(criteriaQuery);

		// apenas retorna a lista de query..
		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticasDiarioDTO> porDia(LocalDate mesreferencia) {
		// criando o criteria builer
		CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

		// criando generic de criteriaQuery
		CriteriaQuery<LancamentoEstatisticasDiarioDTO> criteriaQuery = criteriaBuilder
				.createQuery(LancamentoEstatisticasDiarioDTO.class);

		// criando generic de Root
		Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

		// constroi o objeto
		criteriaQuery
				.select(criteriaBuilder.construct(LancamentoEstatisticasDiarioDTO.class, root.get(Lancamento_.tipo),
						root.get(Lancamento_.dataVencimento), criteriaBuilder.sum(root.get(Lancamento_.valor))));

		// pegar o 1º e ultimo dia do mes..
		LocalDate primeiroDia = mesreferencia.withDayOfMonth(1);
		LocalDate ultimoDia = mesreferencia.withDayOfMonth(mesreferencia.lengthOfMonth());

		// criando a clausula where nas consultas onde verificamos q a dt de vencimento
		// tem que ser >= 1º dia
		criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
				// verificamos q a dt de vencimento tem que ser <= ultimo dia
				criteriaBuilder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia));

		// criando o agrupamento por propriedades categoria no banco..
		criteriaQuery.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento));

		// criando o resultado que retorna a lista de objeto query
		TypedQuery<LancamentoEstatisticasDiarioDTO> typedQuery = manager.createQuery(criteriaQuery);

		// apenas retorna a lista de query..
		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticasPessoaDTO> porPessoa(LocalDate inicio, LocalDate fim) {
		// criando o criteria builer
				CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();

				// criando generic de criteriaQuery
				CriteriaQuery<LancamentoEstatisticasPessoaDTO> criteriaQuery = criteriaBuilder
						.createQuery(LancamentoEstatisticasPessoaDTO.class);

				// criando generic de Root
				Root<Lancamento> root = criteriaQuery.from(Lancamento.class);

				// constroi o objeto
				criteriaQuery
						.select(criteriaBuilder.construct(LancamentoEstatisticasPessoaDTO.class, 
								root.get(Lancamento_.tipo),
								root.get(Lancamento_.pessoa), 
								criteriaBuilder.sum(root.get(Lancamento_.valor))));

				// criando a clausula where nas consultas onde verificamos q a dt de vencimento
				// tem que ser >= 1º dia
				criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(
						root.get(Lancamento_.dataVencimento), inicio),
						// verificamos q a dt de vencimento tem que ser <= ultimo dia
						criteriaBuilder.lessThanOrEqualTo(
								root.get(Lancamento_.dataVencimento), fim));

				// criando o agrupamento por propriedades categoria no banco..
				criteriaQuery.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento));

				// criando o resultado que retorna a lista de objeto query
				TypedQuery<LancamentoEstatisticasPessoaDTO> typedQuery = manager.createQuery(criteriaQuery);

				// apenas retorna a lista de query..
				return typedQuery.getResultList();
	}
}
