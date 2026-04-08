    package com.amani.amaniapirest.repository;

    import com.amani.amaniapirest.models.Psicologo;
    import com.amani.amaniapirest.models.Usuario;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.util.Optional;

    public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
        /** Comprueba si ya existe un psicólogo asociado a este usuario */
        boolean existsByUsuario(Usuario usuario);

        Optional<Psicologo> findByUsuarioIdUsuario(Long idUsuario);


        Optional<Psicologo> findByUsuarioEmail(String email);
    }
