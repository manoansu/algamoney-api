package pt.amane.algamoneyapi.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.amane.algamoneyapi.model.Lancamento;
import pt.amane.algamoneyapi.repositories.lancamentos.LancamentoRepositoryQuery;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {
  
	//Quero buscar data de vencimento <= o valor passado no parametro e
	// a dta de pagamento é nulo é a propriedade de spring tool suit....
	List<Lancamento> findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate data);
}
