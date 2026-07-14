package com.tienda.pos.config;

import com.tienda.pos.model.Categoria;
import com.tienda.pos.model.Rol;
import com.tienda.pos.model.Usuario;
import com.tienda.pos.repository.CategoriaRepository;
import com.tienda.pos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@tienda.com")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
            System.out.println("=========================================================");
            System.out.println(" Usuario administrador creado:");
            System.out.println("   correo:      admin@tienda.com");
            System.out.println("   contraseña:  admin123");
            System.out.println(" (cámbiala después de iniciar sesión por primera vez)");
            System.out.println("=========================================================");
        }

        if (categoriaRepository.count() == 0) {
            categoriaRepository.save(Categoria.builder().nombre("General").descripcion("Categoría por defecto").build());
        }
    }
}
