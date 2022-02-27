package pt.amane.algamoneyapi.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

import pt.amane.algamoneyapi.model.Categoria;

public class LancamentoEstatisticasCategoriaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Categoria categoria;

	private BigDecimal total;

	public LancamentoEstatisticasCategoriaDTO() {
	}

	public LancamentoEstatisticasCategoriaDTO(Categoria categoria, BigDecimal total) {
		this.categoria = categoria;
		this.total = total;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}
