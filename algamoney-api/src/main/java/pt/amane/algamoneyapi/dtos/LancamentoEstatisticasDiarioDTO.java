package pt.amane.algamoneyapi.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import pt.amane.algamoneyapi.enums.TipoLancamento;

public class LancamentoEstatisticasDiarioDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private TipoLancamento tipoLancamento;

	private LocalDate dia;

	private BigDecimal total;

	public LancamentoEstatisticasDiarioDTO() {
	}

	public LancamentoEstatisticasDiarioDTO(TipoLancamento tipoLancamento, LocalDate dia, BigDecimal total) {
		this.tipoLancamento = tipoLancamento;
		this.dia = dia;
		this.total = total;
	}

	public TipoLancamento getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(TipoLancamento tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	public LocalDate getDia() {
		return dia;
	}

	public void setDia(LocalDate dia) {
		this.dia = dia;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}
