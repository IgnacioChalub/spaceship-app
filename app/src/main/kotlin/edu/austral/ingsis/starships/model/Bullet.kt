package edu.austral.ingsis.starships.model

import java.util.Optional
import kotlin.math.cos
import kotlin.math.sin

data class Bullet(
    private val id: String,
    private val position: Position,
    private val vector: Vector,
    private val damage: Double
) : Collidable {

    override fun move(secondsPassed: Double, gameWidth: Double, gameHeight: Double): Bullet {
        val newPosition = Position(
            position.x + vector.speed * -sin(Math.toRadians(vector.rotationInDegrees)) * secondsPassed * 150,
            position.y + vector.speed * cos(Math.toRadians(vector.rotationInDegrees)) * secondsPassed * 150
        )
        return this.copy(position = newPosition)
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

    override fun collide(collidable: Collidable): Optional<Collidable> {
        return Optional.empty<Collidable>()
    }

    fun getDamage(): Double = damage

}