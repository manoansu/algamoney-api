package pt.amane.algamoneyapi.services;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import pt.amane.algamoneyapi.dtos.LancamentoEstatisticasPessoaDTO;
import pt.amane.algamoneyapi.mail.Mailer;
import pt.amane.algamoneyapi.model.Lancamento;
import pt.amane.algamoneyapi.model.Pessoa;
import pt.amane.algamoneyapi.model.Usuario;
import pt.amane.algamoneyapi.repositories.LancamentoRepository;
import pt.amane.algamoneyapi.repositories.PessoaRepository;
import pt.amane.algamoneyapi.repositories.UsuarioRepository;
import pt.amane.algamoneyapi.services.exceptions.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";
	
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LancamentoService.class);
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired 
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private Mailer mailer;
	
	//padrao cron = agenda o metodo num determinado dia no tal horario
	// "0-segundo 58-minuto 11-hora *- dia do mes *- mex *- dia de semana"
	//•second •minute •hour •day of month •month •day of week ...
	// cron = "* 58 11 * * *"
	//o padrão fixedDelay = agenda o metodo num determinado horario 
	// fixedDelay= 1000 * 5
	@Scheduled(fixedDelay = 1000 * 60 * 30)
	//@Scheduled(cron = "0 0 6 * * *")
	public void avisarSobreLancamentosVencidos() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("Preparando envio de"
					+ " e-mail de aviso de lancamentos vencidos");
		}
		
		List<Lancamento> vencidos = lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull(LocalDate.now());
		
		if(vencidos.isEmpty()) {
			logger.info("Sem lancamentos vencidos para aviso.");
			return;
		}
		
		logger.info("Existem {} lançamentos vencidos. " + vencidos.size());
		
		List<Usuario> destinatarios = usuarioRepository
				.findByPermissoesDescricao(DESTINATARIOS);
		
		if(destinatarios.isEmpty()) {
			logger.warn("Existem lancamentos vencidos, mas o "
					+ "sistema não encontrou destinatarios.");
			return;
		}

		mailer.avisarSobreLancamentosVencidos(vencidos, destinatarios);
		
		logger.info("Envio de e-mail de aviso concluido.");
	}

	public Lancamento salvar(Lancamento lancamento) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if (pessoa.isEmpty() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		return lancamentoRepository.save(lancamento);
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
		List<LancamentoEstatisticasPessoaDTO> dados = 
				lancamentoRepository.porPessoa(inicio, fim);
		
		Map<String, Object> paramentros = new HashMap<>();
		paramentros.put("DT_INICIO", Date.valueOf(inicio));
		paramentros.put("DT_FIM", Date.valueOf(fim));
		paramentros.put("REPORT LACALE", new Locale("pt", "PT")); //formata o valor 
		
		// Lê o ficheiro no caminho relatorio
		InputStream inputStream = this.getClass().getResourceAsStream(
				"/relatorios/lancamentos-por-pessoa.jasper");
		
		
		// usamos o metodo fillReport para gerar o jasperPrint..
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, 
				paramentros, new JRBeanCollectionDataSource(dados));
		
		//retorna os bytes gerado para o relatorio para que os recurso retorna os bytes no nosso relatorio
		return JasperExportManager.exportReportToPdf(jasperPrint);
		
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}

		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
	}

	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		}

		if (pessoa.isEmpty() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}

	private Lancamento buscarLancamentoExistente(Long codigo) {
/* 		Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
		if (lancamentoSalvo.isEmpty()) {
			throw new IllegalArgumentException();
		} */
		return lancamentoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException());
	}	
}
