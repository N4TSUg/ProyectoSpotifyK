
# Nuevas funciones agregadas

Este patch agrega:

- **Buscar canciones** (Jamendo) — nueva `SearchActivity` con `SearchViewModel`.
- **Crear playlists** — `Room` (local) con `PlaylistEntity`, `SongEntity`, `PlaylistSongCrossRef`, `PlaylistRepository`, `PlaylistViewModel` y `PlaylistActivity`.
- **Agregar canciones a una playlist** — desde la vista de búsqueda, botón “Añadir” que inserta la pista a la playlist indicada (por ID).

## Pasos para probar

1. Abre el proyecto en Android Studio y sincroniza Gradle.  
   > Requiere agregar el plugin `kapt` (ya incluido) y dependencias de Room.

2. En `SearchViewModel.kt` reemplaza `YOUR_JAMENDO_CLIENT_ID` por tu `client_id` de Jamendo.

3. Ejecuta:
   - `PlaylistActivity` para crear una nueva playlist (botón +).  
     El ID de la playlist aparece como “ID 1”, “ID 2”, etc.
   - `SearchActivity` para buscar canciones. Escribe el texto y, cuando aparezcan resultados, inserta el **ID de playlist** en el campo superior y pulsa **Añadir** en la canción que quieres guardar.

> Nota: Esta integración es mínima para no romper tus pantallas existentes. Si quieres, puedo integrar el buscador y las playlists dentro de `HomeActivity` (TopAppBar con barra de búsqueda y botón a “Playlists”) y agregar un menú contextual “Añadir a playlist…” por cada tarjeta.

## Archivos nuevos

- `app/src/main/java/com/example/proyectospotify/playlists/*` — base de datos Room + ViewModel + UI
- `app/src/main/java/com/example/proyectospotify/search/*` — búsqueda de tracks + UI
- `app/src/main/AndroidManifest.xml` — registra dos actividades nuevas
- `app/build.gradle.kts` — añade dependencias Room y KAPT

## Mejoras sugeridas (si quieres que las implemente)

- Reemplazar el campo “ID de playlist” por un **diálogo de selección de playlist**.
- Mostrar el contenido de cada playlist (pantalla de detalle) y reproducir desde ahí.
- Unificar estilos (Material 3 + tu tema) y navegación con **Navigation Compose**.
- Persistir el `client_id` en `local.properties` o `BuildConfig`.
