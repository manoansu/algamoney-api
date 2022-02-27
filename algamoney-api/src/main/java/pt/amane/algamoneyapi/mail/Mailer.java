package pt.amane.algamoneyapi.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import pt.amane.algamoneyapi.model.Lancamento;
import pt.amane.algamoneyapi.model.Usuario;
import pt.amane.algamoneyapi.repositories.LancamentoRepository;



@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private LancamentoRepository repository;
	
	//Fazenfo o teste de envio do email
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		this.enviarEmail("testes.algaworks@gmail.com", 
//				Arrays.asList("manoansu@gmail.com"), 
//				"testando o envio de email", "Hello world <br> Teste ok");
//		
//		System.out.println("Terminado o envio de email!!");
//	}
	
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		// pegar o path onde tem o ficheiro html..
//		String template = "mail/aviso-lancamentos-vencidos";
//		
//		List<Lancamento> lista = repository.findAll();
//		
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("lancamentos", lista);
//		
//		this.enviarEmail("testes.algaworks@gmail.com", 
//				Arrays.asList("manoansu@gmail.com"), 
//				"testando o envio de email", template, variaveis);
//		
//		System.out.println("Terminado o envio de email!!");
//	}
	
	public void avisarSobreLancamentosVencidos(
			List<Lancamento> vencidos, List<Usuario> destinatarios) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", vencidos);

		List<String> emails = destinatarios.stream()
				.map(u -> u.getEmail())
				.collect(Collectors.toList());

		this.enviarEmail("testes.algaworks@gmail.com",
				emails,
				"Lançamentos vencidos",
				"mail/aviso-lancamentos-vencidos",
				variaveis);
	}
	
	public void enviarEmail(String remetente,
			List<String> destinatario, String assunto, String trmplate,
			Map<String, Object> variaveis) {
		
		Context context = new Context(new Locale("pt","PT"));
		
		variaveis.entrySet().forEach(
				e -> context.setVariable(e.getKey(), e.getValue()));
		//processa a mensagem
		String mensagem = thymeleaf.process(trmplate, context);
		
		//apenas chame o metodo que envia o email..
		this.enviarEmail(remetente, destinatario, assunto, mensagem);
		
	}
	
	// Metodo para o envio de email..
	public void enviarEmail(String remetente,
			List<String> destinatario, String assunto, String mensagem) {
		
		try {
			// Mensagem de email..     // cria instancia de envio de email
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			// mensagem de ajuda						
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente); // ajuda o remetente
			helper.setTo(destinatario.toArray(new String[destinatario.size()])); // ajuda para o destinatario..
			helper.setSubject(assunto); //
			helper.setText(mensagem, true);// o parametro true indica se é html se for falso não é
			
			mailSender.send(mimeMessage);// o email é enviado com essa chamado..
			
		} catch (MessagingException e) {
			throw new RuntimeException("Problema com o envio de email!", e);
		}
		
	}
	

}
