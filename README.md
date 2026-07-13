<div align="center">

# 🏋️🍳🛒 EazyPlanIA

**Tu nutrición, tu entrenamiento y tu lista de la compra — en una sola app de escritorio.**

Aplicación JavaFX para gestionar de forma integral tu salud: planifica dietas, registra entrenamientos y organiza la compra. Construida con una arquitectura limpia por capas y desarrollada con una metodología *spec-first* rigurosa.

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![JPA](https://img.shields.io/badge/JPA-EclipseLink%204.0-green)
![H2](https://img.shields.io/badge/H2-Database-blueviolet)
![Maven](https://img.shields.io/badge/Maven-Build-red)

</div>

---

## La idea, en una frase

> **¿Y si llevar tu dieta, tus entrenamientos y tu compra no requiriera tres apps distintas y una hoja de cálculo?**

EazyPlanIA reúne en un único escritorio las tres piezas que más cuesta coordinar cuando alguien se pone en serio con su salud. Este README cuenta **qué hace la app, cómo está construida y por qué tomé cada decisión de diseño**, para que se entienda de un vistazo.

---

## El problema que resuelve

Cuando quieres cuidarte en serio, la información acaba **repartida y desconectada**:

- La dieta en una app, el entreno en otra, la compra escrita en una nota.
- Los objetivos calóricos no cuadran nunca con lo que compras.
- No hay forma de registrar *qué has comido hoy* frente a *lo que planeabas*.

EazyPlanIA resuelve eso con **un único modelo de datos coherente**: un usuario con sus dietas, sus entrenamientos y sus listas de la compra, todo vinculado y consultable desde la misma interfaz.

---

## ¿Qué encontrarás en esta app?

Una aplicación de escritorio en JavaFX, organizada en **vistas** navegables tras iniciar sesión:

### 👤 Usuarios y acceso
- **Registro y login** con nombre de usuario y contraseña.
- Validación de **username y email únicos** antes de crear la cuenta.
- Sesión de usuario que viaja entre vistas para personalizar todo el contenido.

### 🥗 Dietas
- Crea dietas con un **tipo** (`BALANCED`, `LOW_CARBS`, `HIGH_PROTEIN`, `VEGAN`, `KETO`, `CUSTOM`).
- Define objetivos diarios: **calorías, proteínas, carbohidratos, grasas y agua**.
- Fechas de inicio y fin para cada plan.
- Listado, creación y borrado de tus dietas.

### 🏋️ Entrenamientos
- Registra **sesiones de entrenamiento** con tipo, hora de inicio/fin y notas.
- Cada sesión contiene una lista de **ejercicios**.
- Historial ordenado de tus entrenamientos.

### 🛒 Lista de la compra
- Crea listas de la compra con **items** asociados.
- Marca listas como **compradas**.
- Relacionada con tu plan: lo que compras al final depende de lo que quieres comer.

### 📊 Seguimiento (logging)
- **MacroLog**: registra tu consumo diario de calorías y macronutrientes, vinculado a una dieta concreta.
- **MicroLog**: seguimiento de micronutrientes.

---

## 🏗️ Cómo está hecho (y por qué así)

No se trata solo de *funciones*, sino de **código mantenible y bien estructurado**. Por eso el proyecto sigue una **arquitectura por capas inspirada en DDD** (Domain-Driven Design), donde cada capa tiene una responsabilidad clara y se comunica mediante interfaces bien definidas.

```
┌─────────────────────────────────────────────┐
│            Presentación (JavaFX)            │  ← lo que ve el usuario
│  Controllers · FXML Views · SceneManager    │
└──────────────────┬──────────────────────────┘
                   │  usa servicios
┌──────────────────▼──────────────────────────┐
│             Dominio (Lógica)                │  ← reglas de negocio
│   Services (User/Diet/Workout/Grocery/...)  │
└──────────────────┬──────────────────────────┘
                   │  vía interfaces de repositorio
┌──────────────────▼──────────────────────────┐
│       Repositorios (acceso a datos)         │  ← JPA / EclipseLink
│   Repository (interfaz) + RepositoryImpl    │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│        Infraestructura (persistencia)       │  ← H2 embebida
│   DatabaseConfig · persistence.xml          │
└─────────────────────────────────────────────┘
```

### Decisiones de diseño que quiero destacar

- **🎯 Patrón repositorio con interfaz + implementación.** Cada entidad tiene su `Repository` (contrato) y su `RepositoryImpl`. Esto desacopla la lógica de negocio de la tecnología de persistencia: mañana podría cambiar de H2 a PostgreSQL sin tocar los servicios.
- **🔒 Transacciones explícitas.** Cada operación de escritura (`save`/`delete`) abre su propio `EntityManager`, hace `begin` / `commit` y, si algo falla, `rollback`. Nada de EntityManagers compartidos que generen errores en concurrencia.
- **🧩 Entidades JPA ricas.** Mapeo con anotaciones Jakarta Persistence, `@NamedQuery` para consultas con nombre, relaciones `@OneToMany` / `@ManyToOne` con *cascadas* y *orphan removal* bien definidas.
- **🧭 Navegación centralizada.** Un `SceneManager` se encarga de cambiar de vista, evitando acoplar los controladores entre sí.
- **📦 Proyecto modular (JPMS).** Un `module-info.java` declara explícitamente las dependencias del módulo, forzando un encapsulamiento limpio.

---

## 🧪 Metodología: desarrollo *spec-first* con TDD

Este es, probablemente, el aspecto que mejor define **cómo trabajo**. EazyPlanIA no se escribió "a lo bestia": cada cambio partió de una **especificación** usando la metodología [OpenSpec](./openspec/).

El flujo fue:

1. **Proposal** → qué voy a cambiar, alcance, riesgos y plan de *rollback*.
2. **Specs** → requisitos en lenguaje **Given / When / Then**, con palabras clave **RFC 2119** (*MUST*, *SHALL*, *SHOULD*).
3. **Tasks** → desglose por fases, cada una completable en una sesión.
4. **Implementación + verificación** → `mvn test` como prueba de aceptación.

En total, el cambio principal documenta **21 requisitos y 35 escenarios** (puedes verlos en [`openspec/`](./openspec/)). Esto significa que **el comportamiento está documentado y es verificable**, no solo implícito en el código.

> 📁 Echa un vistazo a [`openspec/changes/fix-and-complete-eazyplan/`](./openspec/changes/fix-and-complete-eazyplan/) para ver un ejemplo completo de proposal, specs y verificación.

---

## 🛠️ Stack tecnológico

| Área | Tecnología | Qué aporta al proyecto |
|------|-----------|------------------------|
| **Lenguaje** | Java 21 | Base moderna del proyecto |
| **Interfaz** | JavaFX 21 + FXML | UI de escritorio declarativa |
| **Persistencia** | EclipseLink 4.0 (JPA / Jakarta) | ORM maduro, *mapping* declarativo |
| **Base de datos** | H2 (embebida) | Sin servidor que instalar, ideal para escritorio |
| **Modularidad** | JPMS (`module-info.java`) | Encapsulamiento a nivel de módulo |
| **Tests** | JUnit 5 | Pruebas de repositorios y lógica |
| **Build** | Maven | Compilación y gestión de dependencias |

---

## 🚀 Pruébalo tú mismo

```bash
# Clona y entra
git clone https://github.com/SantiPG-Dev/EazyPlanIA.git
cd EazyPlanIA

# Compila y pasa los tests
mvn clean test

# Ejecuta la aplicación
mvn javafx:run
```

Al arrancar verás la pantalla de **login**: créate un usuario y empieza a explorar el *dashboard*, que te lleva a dietas, entrenamientos y lista de la compra.

---

## 📁 Estructura del proyecto

```
EazyPlanIA/
├── src/main/java/com/eazyplan/
│   ├── EazyPlanApp.java              # Punto de entrada JavaFX
│   ├── domain/
│   │   ├── entities/                 # User, Diet, Workout, GroceryList, MacroLog...
│   │   ├── repositories/             # Interfaces + Implementaciones (JPA)
│   │   └── services/                 # Lógica de negocio (UserService, DietService...)
│   ├── infrastructure/
│   │   └── database/                 # DatabaseConfig (EntityManagerFactory)
│   └── presentation/
│       ├── SceneManager.java         # Navegación entre escenas
│       └── controllers/              # Login, Dashboard, Diet, Workout, GroceryList
├── src/main/resources/
│   ├── META-INF/persistence.xml      # Configuración JPA + H2
│   └── presentation/views/           # Vistas FXML
├── openspec/                         # Especificaciones spec-first (proposals + specs)
└── pom.xml
```

---

## 🎯 Lo que demuestra este proyecto

Si te estás planteando trabajar conmigo, estos son los puntos que mejor describen cómo afronto el desarrollo:

- **🏗️ Arquitectura limpia**: separación por capas (DDD) con responsabilidades bien delimitadas y dependencias que van en un solo sentido.
- **🔌 Desacoplamiento real**: el patrón repositorio con interfaz aísla el dominio de la tecnología de persistencia.
- **🛡️ Persistencia correcta**: gestión de transacciones explícita y *EntityManagers* por operación, pensando en concurrencia y robustez.
- **📐 Disciplina de ingeniería**: desarrollo *spec-first* con OpenSpec, escenarios *Given/When/Then* y palabras clave RFC 2119. El software está **documentado y verificable**.
- **🧪 Mentalidad de calidad**: TDD como motor del desarrollo y `mvn test` como puerta de aceptación.
- **🌐 Visión de producto**: identifiqué una necesidad real (salud fragmentada en varias apps) y la resolví con un modelo de datos coherente.
- **📦 Modularidad**: uso del sistema de módulos de Java (JPMS) para forzar límites limpios.

---

## ⚠️ Honestidad sobre lo que falta

Como en todo proyecto honesto, hay cosas que **sé que están sin terminar o que mejoraría**:

- 🔑 **Las contraseñas se guardan en texto plano.** Es lo primero que cambiaría: aplicar **hashing con BCrypt** (como ya hice en otros proyectos). Lo dejo anotado como próxima mejora de seguridad.
- 🗄️ La configuración actual de JPA usa `drop-and-create-tables`, pensada para desarrollo; para producción conviene un esquema estable y migraciones.
- 📈 Las vistas de *MacroLog/MicroLog* y las gráficas del *dashboard* están fuera del alcance actual.

Para mí, **saber lo que falta y decirlo con claridad** forma parte del oficio.

---

## 🗺️ Próximos pasos

- 🔐 Hashing de contraseñas con BCrypt.
- 📊 Vistas de seguimiento de macros y gráficas en el *dashboard*.
- 🔄 Migración a base de datos en archivo persistente con esquema versionado.
- 📤 Exportación de dietas y entrenamientos.

---

<div align="center">

## 👤 Sobre mí

**Santiago Pérez Gómez**

📍 Málaga, España · 🐱 [@SantiPG-Dev](https://github.com/SantiPG-Dev)

*Gracias por llegar hasta aquí. Este proyecto refleja cómo me gusta trabajar: **arquitectura limpia, disciplina spec-first y código que se pueda mantener con el tiempo**.*

</div>
