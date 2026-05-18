    package com.amani.amaniapirest.repository;

    import com.amani.amaniapirest.models.Psicologo;
    import com.amani.amaniapirest.models.Usuario;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.List;
    import java.util.Optional;

    public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {

        Optional<Psicologo> findByUsuario_IdUsuario(Long idUsuario);

        List<Psicologo> findByUsuario_ActivoTrue();

        Optional<Psicologo> findByUsuario_Email(String email);

        List<Psicologo> findByUsuario_ActivoFalse();
    }
