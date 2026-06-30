package com.example.ui.theme

import androidx.compose.ui.graphics.Color

sealed class TicTacToeTheme(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val gridLine: Color,
    val colorX: Color,
    val colorO: Color,
    val accent: Color,
    val glowColorX: Color = colorX.copy(alpha = 0.4f),
    val glowColorO: Color = colorO.copy(alpha = 0.4f),
    val useGlow: Boolean = false
) {
    companion object {
        // Evaluate the list lazily or directly. Since they are top-level classes now,
        // we can safely access them without cyclic loading.
        val ALL_THEMES: List<TicTacToeTheme> by lazy {
            listOf(NeonGalactic, SunsetSand, ForestZen, CyberBlade)
        }
        fun fromId(id: String): TicTacToeTheme {
            return ALL_THEMES.find { it.id == id } ?: NeonGalactic
        }
    }
}

object NeonGalactic : TicTacToeTheme(
    id = "neon_galactic",
    name = "Neon Galactic 🌌",
    isDark = true,
    background = Color(0xFF090A11),
    onBackground = Color(0xFFF3F4F6),
    surface = Color(0xFF141725),
    onSurface = Color(0xFFE5E7EB),
    gridLine = Color(0xFF2C314B),
    colorX = Color(0xFF00E5FF),     // Neon Cyan
    colorO = Color(0xFFFF007F),     // Neon Hot Pink
    accent = Color(0xFFBD00FF),     // Cosmic Purple
    useGlow = true
)

object SunsetSand : TicTacToeTheme(
    id = "sunset_sand",
    name = "Sunset Sand 🌅",
    isDark = false,
    background = Color(0xFFFFF7ED), // Super light warm peach
    onBackground = Color(0xFF451A03), // Dark warm brown
    surface = Color(0xFFFFEDD5),    // Peach-sand card
    onSurface = Color(0xFF78350F),
    gridLine = Color(0xFFFDBA74),   // Orange-gold lines
    colorX = Color(0xFFEA580C),     // Rust/burnt orange X
    colorO = Color(0xFFB45309),     // Deep amber gold O
    accent = Color(0xFF0D9488),     // Contrasting warm teal
    useGlow = false
)

object ForestZen : TicTacToeTheme(
    id = "forest_zen",
    name = "Forest Zen 🍃",
    isDark = false,
    background = Color(0xFFF0FDF4), // Light dew green background
    onBackground = Color(0xFF14532D), // Deep green
    surface = Color(0xFFDCFCE7),    // Minty leaf card
    onSurface = Color(0xFF166534),
    gridLine = Color(0xFF86EFAC),   // Light emerald grids
    colorX = Color(0xFF15803D),     // Pine green X
    colorO = Color(0xFFA16207),     // Rich acorn/honey O
    accent = Color(0xFF047857),     // Pure emerald active indicator
    useGlow = false
)

object CyberBlade : TicTacToeTheme(
    id = "cyber_blade",
    name = "Cyber Blade 🦾",
    isDark = true,
    background = Color(0xFF020202), // Deep obsidian space
    onBackground = Color(0xFF00FFCC), // Green-blue fluorescent
    surface = Color(0xFF0D0D0D),    // Tech charcoal card
    onSurface = Color(0xFFFFFFFF),
    gridLine = Color(0xFF242424),   // Carbon gray
    colorX = Color(0xFF39FF14),     // Laser green
    colorO = Color(0xFF7F00FF),     // Cyber violet
    accent = Color(0xFF00FFCC),     // Cyan flare
    useGlow = true
)
