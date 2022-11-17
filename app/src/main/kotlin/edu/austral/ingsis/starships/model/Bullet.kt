package edu.austral.ingsis.starships.model

import kotlin.math.cos
import kotlin.math.sin

class Bullet(
    private val id: String,
    private val position: Position,
    private val vector: Vector
) : Collidable {

    override fun move(gameWidth: Double, gameHeight: Double): Bullet {
        val newPosition = Position(
            position.x + vector.speed * -sin(Math.toRadians(vector.rotationInDegrees)),
            position.y + vector.speed * cos(Math.toRadians(vector.rotationInDegrees))
        )
        return Bullet(id, newPosition, vector)
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

}