package pt.amane.algamoneyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import pt.amane.algamoneyapi.configs.proprieties.AlgamoneyApiProperty;

/**
 * 
 * @author manoansu
 * para usar a classe AlgamoneyApiProperty.class do pacote package,
 *  externamente tem que ativar na classe main o 
 *  EnableConfigurationProperties(AlgamoneyApiProperty.class), e tb na 
 *  application-prod.properties habilitar:
 *  algamoney.seguranca.enable-https=true qd vai para produçao..
 *  para que ele seja visto en todo projeto 
 *
 */
@SpringBootApplication
@EnableConfigurationProperties(AlgamoneyApiProperty.class)
public class AlgamoneyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyApiApplication.class, args);
	}

}
