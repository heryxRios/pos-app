# Sistema POS — Punto de Venta (Spring Boot + Thymeleaf + MySQL)

Sistema de punto de venta completo para tienda/retail: ventas, inventario,
usuarios/roles, caja y reportes.

## Requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+ corriendo localmente (o remoto)

## Configuración

Antes de arrancar, crea la base de datos (o deja que se cree sola, ver abajo)
y ajusta las credenciales en:

```
src/main/resources/application.properties
```

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pos_db?useSSL=false&serverTimezone=America/Mexico_City&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

Con `createDatabaseIfNotExist=true` no necesitas crear la base manualmente;
MySQL la crea al conectar. Las tablas las crea Hibernate automáticamente
gracias a `spring.jpa.hibernate.ddl-auto=update`.

## Ejecutar

```bash
mvn spring-boot:run
```

La aplicación queda disponible en: **http://localhost:8080**

## Usuario administrador inicial

Al arrancar por primera vez (base de datos vacía), el sistema crea
automáticamente un usuario administrador:

```
correo:      admin@tienda.com
contraseña:  admin123
```

**Cámbiala en cuanto entres** — puedes hacerlo agregando un nuevo usuario
Administrador desde el módulo de Usuarios y desactivando el de prueba
(o mejor, edita `DataSeeder.java` antes del primer arranque).

## Módulos incluidos

| Módulo | Ruta | Descripción |
|---|---|---|
| Punto de venta | `/ventas/nueva` | Pantalla principal: catálogo + carrito + cobro |
| Historial de ventas | `/ventas/historial` | Consulta y cancelación de ventas |
| Caja | `/caja` | Apertura y cierre de turno con arqueo |
| Productos | `/productos` | CRUD de productos, categorías, alertas de stock bajo |
| Reportes | `/reportes` | Total vendido por rango de fechas, productos más vendidos |
| Usuarios | `/usuarios` | Alta de personal (solo rol ADMIN) |

## Roles

- **ADMIN** — acceso total, incluida gestión de usuarios.
- **GERENTE** — ventas, productos, reportes (sin gestión de usuarios).
- **CAJERO** — ventas y caja únicamente.

## Estructura del proyecto

```
src/main/java/com/tienda/pos/
├── model/         Entidades JPA (Usuario, Producto, Venta, DetalleVenta, Cliente, CorteCaja...)
├── repository/     Interfaces Spring Data JPA
├── service/        Lógica de negocio (ventas, inventario, caja, reportes)
├── controller/     Controladores MVC + endpoints AJAX del POS
└── config/         Seguridad, formatters, datos iniciales

src/main/resources/
├── templates/      Vistas Thymeleaf (una carpeta por módulo)
└── static/css/      Hoja de estilos
```

## Próximos pasos sugeridos

- Agregar impresión/PDF de tickets (ya está la dependencia `itext7-core` lista para usarse).
- Agregar paginación en listados grandes (productos, historial de ventas).
- Agregar pruebas unitarias para `VentaService` (validación de stock, cálculo de totales).
- Migrar `ddl-auto=update` a **Flyway** antes de pasar a producción.
- Agregar recuperación de contraseña / política de expiración.
