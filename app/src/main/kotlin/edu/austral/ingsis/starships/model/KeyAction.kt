package edu.austral.ingsis.starships.model

sealed interface KeyAction

enum class KeyMovement : KeyAction {
    ACCELERATE,
    TURN_LEFT,
    TURN_RIGHT,
    STOP,
    SHOOT,
}

enum class MenuAction : KeyAction {
    TOGGLE_PAUSE
}