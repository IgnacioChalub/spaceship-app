package edu.austral.ingsis.starships.model

sealed interface Collidable {
    fun move(gameWidth: Double, gameHeight: Double): Collidable
    fun getId(): String
    fun getPosition(): Position
    fun getVector(): Vector
}