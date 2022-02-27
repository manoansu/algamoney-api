package pt.amane.algamoneyapi.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import pt.amane.algamoneyapi.enums.TipoLancamento;
import pt.amane.algamoneyapi.model.Pessoa;

public class LancamentoEstatisticasPessoaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private TipoLancamento tipo;

	private Pessoa pessoa;

	private BigDecimal total;

	public LancamentoEstatisticasPessoaDTO() {
	}

	public LancamentoEstatisticasPessoaDTO(TipoLancamento tipo, Pessoa pessoa, BigDecimal total) {
		this.tipo = tipo;
		this.pessoa = pessoa;
		this.total = total;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}
