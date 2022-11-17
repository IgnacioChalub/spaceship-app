package edu.austral.ingsis.starships.model

import java.util.Optional

sealed interface Collidable {
    fun move(secondsPassed: Double, gameWidth: Double, gameHeight: Double): Collidable
    fun getId(): String
    fun getPosition(): Position
    fun getVector(): Vector
    fun collide(collidable: Collidable): Optional<Collidable>
}