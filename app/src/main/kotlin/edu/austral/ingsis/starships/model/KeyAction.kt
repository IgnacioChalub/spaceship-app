package edu.austral.ingsis.starships.model

//sealed interface KeyAction

enum class KeyMovement {
    ACCELERATE,
    TURN_LEFT,
    TURN_RIGHT,
    STOP,
    SHOOT,
}

enum class KeyMenuAction {
    TOGGLE_PAUSE
}