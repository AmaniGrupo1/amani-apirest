    package com.amani.amaniapirest.repository;

    import com.amani.amaniapirest.models.Psicologo;
    import com.amani.amaniapirest.models.Usuario;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
        /** Comprueba si ya existe un psicólogo asociado a este usuario */
        boolean existsByUsuario(Usuario usuario);

        Optional<Psicologo> findByUsuario_IdUsuario(Long idUsuario);


        Optional<Psicologo> findByUsuario_Email(String email);
    }
