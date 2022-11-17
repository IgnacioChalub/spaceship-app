package edu.austral.ingsis.starships.model

import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class Asteroid(
    private val id: String,
    private val position: Position,
    private val vector: Vector,
    val remainingDamageSustained: Double
) : Collidable {

    override fun move(secondsPassed: Double, gameWidth: Double, gameHeight: Double): Collidable {
        val xPosition = position.x + (vector.speed * -sin(Math.toRadians(vector.rotationInDegrees)) * secondsPassed / 100000)
        val yPosition = position.y + vector.speed * cos(Math.toRadians(vector.rotationInDegrees)) * secondsPassed / 100000

        return Asteroid(id, Position(xPosition, yPosition), Vector(vector.rotationInDegrees, vector.speed), remainingDamageSustained)
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

    override fun collide(collidable: Collidable): Optional<Collidable> {
        return when (collidable) {
            is Bullet -> {
                if (remainingDamageSustained-collidable.getDamage() <= 0) return Optional.empty<Collidable>()
                return Optional.of(Asteroid(id, position, vector, remainingDamageSustained - collidable.getDamage()))
            }
            is Asteroid -> Optional.of(this)
            is Ship -> Optional.of(this)
        }
    }

}