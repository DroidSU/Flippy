package com.fliq.game_engine.models

enum class Challenge(
    val title: String,
    val description: String
) {
    SPEED_RUN(
        "SPEED RUN",
        "Classic mode. The world speeds up every 10 seconds. Survive as long as you can."
    ),
    MIRAGE(
        "THE MIRAGE",
        "Don't trust your eyes. Coins might transform into Bombs at the last second."
    ),
    MINEFIELD(
        "MINEFIELD",
        "High risk, high reward. 50% Bombs, 1 Life. Every tap counts."
    ),
    BLACKOUT(
        "BLACKOUT",
        "Memory test. Icons reveal briefly and then vanish. Remember what's what."
    ),
    FRENZY(
        "FRENZY",
        "No bombs, just pure speed. Miss one coin and it's over."
    )
}
