package com.amani.amaniapirest.services.serviciosLogin;

import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.SecurityConfig;
import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.DireccionRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.TutorRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.models.*;
import com.amani.amaniapirest.repository.*;
import com.amani.amaniapirest.services.ConsentimientoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestiona la autenticación y el registro de usuarios en la plataforma.
 *
 * <p>Centraliza la lógica de inicio de sesión (validación de credenciales y generación de JWT),
 * el registro de pacientes con soporte para menores de edad (tutores, direcciones, consentimientos),
 * el registro de administradores, y las operaciones de alta y baja de psicólogos.
 * También permite a un psicólogo autenticado crear cuentas de paciente y asignarlos
 * automáticamente a su cartera.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Log4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PacientesRepository pacienteRepository;
    private final SituacionRepository situacionRepository;
    private final PacienteSituacionRepository pacienteSituacionRepository;
    private final PsicologoPacienteRepository psicologoPacienteRepository;
    private final ConsentimientoService consentimientoService;
    private final DireccionRepository direccionRepository;
    private final TutorRepository tutorRepository;
    private final PsicologoRepository psicologoRepository;
    private final SecurityConfig securityConfig;
    private final JwtUtil jwtUtil;
    private final AjusteRepository ajustesRepository;

    // ================= LOGIN =================
    /**
     * Autentica a un usuario validando sus credenciales y genera un token JWT.
     *
     * <p>Verifica que el usuario exista, que su cuenta esté activa y que la contraseña
     * coincida con el hash almacenado. Para el rol psicólogo, comprueba además que
     * el perfil profesional esté activo. Incluye en la respuesta los identificadores
     * de psicólogo y paciente relevantes según el rol, así como las preferencias
     * de idioma y tema del usuario.</p>
     *
     * @param request DTO con el email y la contraseña del usuario.
     * @return {@link LoginResponseDTO} con el token JWT, datos del usuario e identificadores
     *         de psicólogo/paciente asociados.
     * @throws org.springframework.security.authentication.BadCredentialsException
     *         si el email no existe o la contraseña es incorrecta.
     * @throws org.springframework.security.authentication.DisabledException
     *         si la cuenta del usuario o del psicólogo está desactivada.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {

        // 1. Buscar usuario
        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new org.springframework.security.authentication
                                .BadCredentialsException("Usuario no encontrado"));

        // 2. Verificar si está activo
        if (!Boolean.TRUE.equals(usuario.getActivo())) {

            throw new org.springframework.security.authentication
                    .DisabledException(
                    "Tu cuenta ha sido desactivada. Contacte con administración."
            );
        }

        // 3. Validar contraseña
        var encoder = securityConfig.passwordEncoder();

        if (!encoder.matches(request.getPassword(), usuario.getPassword())) {

            throw new org.springframework.security.authentication
                    .BadCredentialsException("Contraseña incorrecta");
        }

        // 4. Crear UserDetails
        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                true,   // enabled
                true,   // accountNonExpired
                true,   // credentialsNonExpired
                true,   // accountNonLocked
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + usuario.getRol().name().toUpperCase()
                        )
                )
        );

        // 5. Generar JWT
        String token = jwtUtil.generateToken(
                userDetails,
                usuario.getRol().name()
        );

        Long idPsicologo = null;
        Long idPaciente = null;

        // ========================
        // PSICÓLOGO
        // ========================
        if (usuario.getRol() == RolUsuario.psicologo) {

            Psicologo psicologo = psicologoRepository
                    .findByUsuario_IdUsuario(usuario.getIdUsuario())
                    .orElseThrow(() ->
                            new RuntimeException("Psicólogo no encontrado"));

            // Verificación extra de seguridad
            if (!Boolean.TRUE.equals(psicologo.getUsuario().getActivo())) {

                throw new org.springframework.security.authentication
                        .DisabledException(
                        "Psicólogo dado de baja"
                );
            }

            idPsicologo = psicologo.getIdPsicologo();
        }

        // ========================
        // PACIENTE
        // ========================
        if (usuario.getRol() == RolUsuario.paciente) {

            idPaciente = pacienteRepository
                    .findByUsuario_IdUsuario(usuario.getIdUsuario())
                    .map(Paciente::getIdPaciente)
                    .orElse(null);

            idPsicologo = psicologoPacienteRepository
                    .findByPaciente_Usuario_IdUsuario(usuario.getIdUsuario())
                    .filter(rel ->
                            rel.getPsicologo() != null &&
                                    rel.getPsicologo().getUsuario() != null &&
                                    Boolean.TRUE.equals(
                                            rel.getPsicologo()
                                                    .getUsuario()
                                                    .getActivo()
                                    )
                    )
                    .map(rel -> rel.getPsicologo().getIdPsicologo())
                    .orElse(null);
        }

        // ========================
        // AJUSTES
        // ========================
        String idioma = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getIdioma)
                .orElse("es");

        Boolean tema = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getTema)
                .orElse(false);

        // ========================
        // RESPONSE FINAL
        // ========================
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,
                idPaciente,
                idioma,
                tema
        );
    }
    // ================= REGISTER PACIENTE =================
    /**
     * Registra un nuevo paciente con todos sus datos personales, consentimientos y entidades asociadas.
     *
     * <p>Valida que se haya proporcionado fecha de nacimiento, que se acepten los términos
     * y que el correo no esté ya registrado. Si el paciente es menor de edad (menos de 18 años),
     * exige al menos un tutor legal. Persiste en cascada: usuario, paciente, consentimiento,
     * situaciones clínicas, tutores y direcciones. Al finalizar genera y devuelve un JWT
     * para que el usuario quede autenticado de inmediato.</p>
     *
     * @param request DTO con los datos del usuario, paciente, tutores, direcciones, situaciones y consentimientos.
     * @return {@link LoginResponseDTO} con el token JWT y los identificadores del nuevo paciente.
     * @throws RuntimeException si la fecha de nacimiento es nula, no se aceptan los términos,
     *                          el correo ya está registrado, un menor no tiene tutores,
     *                          o alguna situación referenciada no existe.
     */
    @Transactional
    public LoginResponseDTO registerPaciente(PacienteRequestDTO request) {

        // 0. Validaciones básicas
        if (request.getFechaNacimiento() == null) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria");
        }

        if (!Boolean.TRUE.equals(request.getAceptaTerminos())) {
            throw new RuntimeException("Debe aceptar los términos y condiciones");
        }

        // 1. Calcular si es menor
        int edad = Period.between(request.getFechaNacimiento(), LocalDate.now()).getYears();
        boolean esMenor = edad < 18;

        // 2. Validación de tutores
        if (esMenor && (request.getTutores() == null || request.getTutores().isEmpty())) {
            throw new RuntimeException("Un menor debe tener al menos un tutor");
        }

        // 3. Validar email único
        if (usuarioRepository.findByEmail(request.getUsuario().getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        LocalDateTime now = LocalDateTime.now();

        // --- Crear usuario ---
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getUsuario().getNombre());
        usuario.setDni(request.getUsuario().getDni());
        usuario.setApellido(request.getUsuario().getApellido());
        usuario.setEmail(request.getUsuario().getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getUsuario().getPassword()));
        usuario.setRol(RolUsuario.paciente);
        usuario.setActivo(true);
        usuario.setFechaRegistro(now);

        usuarioRepository.save(usuario);

        // --- Crear paciente ---
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setCreatedAt(now);

        paciente = pacienteRepository.save(paciente);

        // --- Consentimiento ---
        consentimientoService.guardarConsentimiento(
                paciente,
                request.getAceptaTerminos(),
                Boolean.TRUE.equals(request.getAceptaVideoconferencia()),
                Boolean.TRUE.equals(request.getAceptaComunicacion())
        );

        // --- Situaciones ---
        if (request.getIdSituaciones() != null && !request.getIdSituaciones().isEmpty()) {
            Set<PacienteSituacion> listaPs = new HashSet<>();
            for (Long idSituacion : request.getIdSituaciones()) {
                Situacion situacion = situacionRepository.findById(idSituacion)
                        .orElseThrow(() -> new RuntimeException("Situación no encontrada: " + idSituacion));

                PacienteSituacion ps = new PacienteSituacion();
                ps.setPaciente(paciente);
                ps.setSituacion(situacion);
                ps.setFechaRegistro(now);

                pacienteSituacionRepository.save(ps);
                listaPs.add(ps);  // <-- agregar a la lista local
            }
            paciente.setPacienteSituaciones(listaPs);  // <-- actualizar la entidad Paciente
        }

        // --- Tutores (solo si es menor) ---
        if (esMenor) {
            for (TutorRequestDTO tutorDTO : request.getTutores()) {

                // Validación básica por tutor
                if (tutorDTO.getNombre() == null || tutorDTO.getTipo() == null) {
                    throw new RuntimeException("El tutor debe tener nombre y tipo");
                }

                Tutor tutor = new Tutor();
                tutor.setPaciente(paciente);
                tutor.setNombre(tutorDTO.getNombre());
                tutor.setTelefono(tutorDTO.getTelefono());
                tutor.setEmail(tutorDTO.getEmail());
                tutor.setDni(tutorDTO.getDni());
                tutor.setTipo(tutorDTO.getTipo());

                tutorRepository.save(tutor);
            }
        }
        // --- DIRECCIONES ---
        if (request.getDireccion() != null && !request.getDireccion().isEmpty()) {

            for (DireccionRequestDTO dirDTO : request.getDireccion()) {

                if (dirDTO.getCalle() == null || dirDTO.getCalle().isBlank()) {
                    throw new RuntimeException("La dirección debe tener calle");
                }

                Direccion direccion = new Direccion();
                direccion.setPaciente(paciente);
                direccion.setCalle(dirDTO.getCalle());
                direccion.setCiudad(dirDTO.getCiudad());
                direccion.setProvincia(dirDTO.getProvincia());
                direccion.setCodigoPostal(dirDTO.getCodigoPostal());
                direccion.setPais(dirDTO.getPais());

                direccionRepository.save(direccion);
            }
        }

        // --- JWT ---
        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        Long idPsicologo = psicologoPacienteRepository
                .findByPaciente_Usuario_IdUsuario(usuario.getIdUsuario())
                .map(rel -> rel.getPsicologo().getUsuario().getIdUsuario())
                .orElse(null);

        Long idPaciente = null;

        if (usuario.getRol() == RolUsuario.paciente) {
            // Devolver idUsuario Firebase, no idPaciente de tabla
            idPaciente = usuario.getIdUsuario();
        } else if (usuario.getRol() == RolUsuario.psicologo) {
            // Devolver idUsuario del paciente asignado al psicólogo
            idPaciente = psicologoPacienteRepository
                    .findByPsicologo_Usuario_IdUsuario(usuario.getIdUsuario())
                    .map(rel -> rel.getPaciente().getUsuario().getIdUsuario())
                    .orElse(null);
        }

        String idioma = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getIdioma)
                .orElse("es");
        Boolean tema = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getTema)
                .orElse(false);

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,
                idPaciente,
                idioma,
                tema
        );
    }

    // ================= REGISTER ADMIN =================
    /**
     * Registra un nuevo usuario con rol administrador en el sistema.
     *
     * <p>Crea el usuario con estado activo, codifica la contraseña y genera un token JWT
     * para que el administrador quede autenticado de inmediato tras el registro.
     * No requiere perfil de paciente ni de psicólogo.</p>
     *
     * @param request DTO con nombre, apellido, email y contraseña del nuevo administrador.
     * @return {@link LoginResponseDTO} con el token JWT y los datos del administrador creado.
     */
    public LoginResponseDTO registerAdmin(RegistryRequestDTO request) {

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getPassword()));
        usuario.setRol(RolUsuario.admin);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );
        Long idPsicologo = null;

        if (usuario.getRol() == RolUsuario.psicologo) {
            // si el usuario es psicólogo, devolvemos su propio idUsuario Firebase
            idPsicologo = usuario.getIdUsuario();
        } else if (usuario.getRol() == RolUsuario.paciente) {
            // si es paciente, buscamos el idUsuario del psicólogo asignado
            idPsicologo = psicologoPacienteRepository
                    .findByPaciente_Usuario_IdUsuario(usuario.getIdUsuario())
                    .map(rel -> rel.getPsicologo().getUsuario().getIdUsuario())
                    .orElse(null); // null si no hay psicólogo asignado
        }

        Long idPaciente = null;

        if (usuario.getRol() == RolUsuario.paciente) {
            // si es paciente, devolvemos su propio idUsuario Firebase
            idPaciente = usuario.getIdUsuario();
        } else if (usuario.getRol() == RolUsuario.psicologo) {
            // si es psicólogo, devolver el idUsuario del paciente asignado
            idPaciente = psicologoPacienteRepository
                    .findByPsicologo_Usuario_IdUsuario(usuario.getIdUsuario())
                    .map(rel -> rel.getPaciente().getUsuario().getIdUsuario())
                    .orElse(null);
        }
        String idioma = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getIdioma)
                .orElse("es");


        Boolean tema = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getTema)
                .orElse(false);

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,
                idPaciente,
                idioma,
                tema
        );
    }

    /**
     * Obtiene la lista de todos los usuarios con rol administrador registrados en el sistema.
     *
     * @return lista de {@link AdministradorDTO} con los datos de cada administrador activo e inactivo.
     */
    public List<AdministradorDTO> listarAdministradores() {
        List<Usuario> usuarios = usuarioRepository.findByRol(RolUsuario.admin);

        return usuarios.stream()
                .map(u -> new AdministradorDTO(
                        u.getIdUsuario(),
                        u.getNombre(),
                        u.getApellido(),
                        u.getEmail(),
                        u.getRol().name(),
                        u.getActivo()
                ))
                .toList();
    }

    // Registro de psicólogo eliminado: la creación se gestiona exclusivamente desde el panel de administración.

    // ================= BAJA =================
    /**
     * Desactiva la cuenta de un psicólogo y libera a todos sus pacientes asignados.
     *
     * <p>Marca el usuario como inactivo y registra la fecha de baja. Para cada relación
     * psicólogo-paciente activa (sin fecha de fin), cierra el periodo de asignación
     * y elimina la referencia al psicólogo del perfil del paciente, dejándolo sin asignar.</p>
     *
     * @param idPsicologo identificador del psicólogo a dar de baja.
     * @throws RuntimeException si no existe un psicólogo con el identificador proporcionado.
     */
    @Transactional
    public void darBajaPsicologo(Long idPsicologo) {

        // 1. Buscar psicólogo
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() ->
                        new RuntimeException("Psicólogo no encontrado"));

        // 2. Desactivar usuario
        Usuario usuario = psicologo.getUsuario();

        usuario.setActivo(false);
        usuario.setFechaBaja(LocalDateTime.now());

        usuarioRepository.save(usuario);

        // 3. Obtener relaciones activas
        List<PsicologoPaciente> relaciones =
                psicologoPacienteRepository
                        .findByPsicologo_IdPsicologoAndFechaFinIsNull(idPsicologo);

        System.out.println("RELACIONES ACTIVAS: " + relaciones.size());

        // 4. Liberar pacientes
        for (PsicologoPaciente relacion : relaciones) {

            // cerrar historial relación
            relacion.setFechaFin(LocalDateTime.now());

            psicologoPacienteRepository.save(relacion);

            // quitar psicólogo principal
            Paciente paciente = relacion.getPaciente();

            paciente.setPsicologo(null);

            pacienteRepository.save(paciente);
        }
    }


    /**
     * Reactiva la cuenta de un psicólogo previamente dado de baja.
     *
     * <p>Establece el usuario como activo y limpia la fecha de baja. Si el psicólogo
     * no tenía fecha de registro, la inicializa con la fecha actual.</p>
     *
     * @param idPsicologo identificador del psicólogo a reactivar.
     * @throws RuntimeException si no existe un psicólogo con el identificador proporcionado.
     */
    @Transactional
    public void darAltaPsicologo(Long idPsicologo) {

        // 1. Buscar psicólogo
        Psicologo psicologo = psicologoRepository.findById(idPsicologo)
                .orElseThrow(() ->
                        new RuntimeException("Psicólogo no encontrado"));

        // 2. Reactivar usuario
        Usuario usuario = psicologo.getUsuario();

        usuario.setActivo(true);

        // limpiar fecha baja
        usuario.setFechaBaja(null);

        // opcional: actualizar fecha modificación
        usuario.setFechaRegistro(
                usuario.getFechaRegistro() != null
                        ? usuario.getFechaRegistro()
                        : LocalDateTime.now()
        );

        usuarioRepository.save(usuario);
    }

    /**
     * Crea un nuevo paciente y lo asigna directamente al psicólogo autenticado.
     *
     * <p>Valida que el correo no esté registrado, crea el usuario y el perfil de paciente,
     * registra la relación psicólogo-paciente en la tabla intermedia con fecha de inicio,
     * y persiste opcionalmente situaciones clínicas, tutores y direcciones. Devuelve un
     * JWT para que el paciente pueda autenticarse sin pasos adicionales.</p>
     *
     * @param request        DTO con todos los datos del nuevo paciente.
     * @param emailPsicologo email del psicólogo autenticado que crea el paciente.
     * @return {@link LoginResponseDTO} con el token JWT del paciente creado e identificadores relevantes.
     * @throws RuntimeException si el psicólogo no existe, el correo ya está registrado,
     *                          o alguna situación referenciada no existe.
     */
    @Transactional
    public LoginResponseDTO crearPacienteDesdePsicologo(PacienteRequestDTO request,String emailPsicologo) {

        // 1. Obtener psicólogo logueado
        Psicologo psicologo = psicologoRepository.findByUsuario_Email(emailPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));
        // 2. Validar email único
        if (usuarioRepository.findByEmail(request.getUsuario().getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        LocalDateTime now = LocalDateTime.now();

        // 3. Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getUsuario().getNombre());
        usuario.setApellido(request.getUsuario().getApellido());
        usuario.setEmail(request.getUsuario().getEmail());
        usuario.setDni(request.getUsuario().getDni());
        usuario.setPassword(securityConfig.passwordEncoder().encode(request.getUsuario().getPassword()));
        usuario.setRol(RolUsuario.paciente);
        usuario.setActivo(true);
        usuario.setFechaRegistro(now);

        usuarioRepository.save(usuario);

        // 4. Crear paciente
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setGenero(request.getGenero());
        paciente.setTelefono(request.getTelefono());
        paciente.setCreatedAt(now);

        paciente = pacienteRepository.save(paciente);

        // Registro de la relación psicólogo-paciente en la tabla intermedia
        PsicologoPaciente relacion = new PsicologoPaciente();
        relacion.setPaciente(paciente);
        relacion.setPsicologo(psicologo);
        relacion.setFechaInicio(now);

        psicologoPacienteRepository.save(relacion);

        // 6. Situaciones
        if (request.getIdSituaciones() != null) {
            for (Long idSituacion : request.getIdSituaciones()) {

                Situacion situacion = situacionRepository.findById(idSituacion)
                        .orElseThrow(() -> new RuntimeException("Situación no encontrada"));

                PacienteSituacion ps = new PacienteSituacion();
                ps.setPaciente(paciente);
                ps.setSituacion(situacion);
                ps.setFechaRegistro(now);

                pacienteSituacionRepository.save(ps);
            }
        }

        // 7. Tutores
        if (request.getTutores() != null) {
            for (TutorRequestDTO tutorDTO : request.getTutores()) {

                Tutor tutor = new Tutor();
                tutor.setPaciente(paciente);
                tutor.setNombre(tutorDTO.getNombre());
                tutor.setTelefono(tutorDTO.getTelefono());
                tutor.setEmail(tutorDTO.getEmail());
                tutor.setDni(tutorDTO.getDni());
                tutor.setTipo(tutorDTO.getTipo());

                tutorRepository.save(tutor);
            }
        }

        // 8. Direcciones
        if (request.getDireccion() != null) {
            for (DireccionRequestDTO dirDTO : request.getDireccion()) {

                Direccion direccion = new Direccion();
                direccion.setPaciente(paciente);
                direccion.setCalle(dirDTO.getCalle());
                direccion.setCiudad(dirDTO.getCiudad());
                direccion.setProvincia(dirDTO.getProvincia());
                direccion.setCodigoPostal(dirDTO.getCodigoPostal());
                direccion.setPais(dirDTO.getPais());

                direccionRepository.save(direccion);
            }
        }

        // 9. JWT
        UserDetails userDetails = new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name().toUpperCase()))
        );

        String token = jwtUtil.generateToken(userDetails, usuario.getRol().name());

        // 10. ID psicólogo correcto
        Long idPsicologo = psicologo.getIdPsicologo();

        // 11. ID paciente (lo que espera tu frontend)
        Long idPaciente = paciente.getIdPaciente();
        String idioma = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getIdioma)
                .orElse("es");

        Boolean tema = ajustesRepository
                .findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(Ajuste::getTema)
                .orElse(false);

        // 12. RESPONSE FINAL
        return new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol().name(),
                token,
                idPsicologo,
                idPaciente,
                idioma,
                tema
        );
    }

}