package pt.amane.algamoneyapi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.amane.algamoneyapi.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
    public Optional<Usuario> findByEmail(String email);
    
    public List<Usuario> findByPermissoesDescricao(String permissaoDescricao);
}
