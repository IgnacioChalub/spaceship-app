package edu.austral.ingsis.starships.model

import kotlin.math.cos
import kotlin.math.sin

class Ship(
    private val id: String,
    val remainingLives: Int,
    private val position: Position,
    private val vector: Vector,
) : Collidable {

    fun turnLeft(): Ship = Ship(id, remainingLives, position, Vector(vector.rotationInDegrees-15, vector.speed))

    fun turnRight(): Ship = Ship(id, remainingLives, position, Vector(vector.rotationInDegrees+15, vector.speed))

    fun accelerate(): Ship {
        val newSpeed = if (vector.speed == 1.0) vector.speed else vector.speed + 1
        return Ship(id, remainingLives, position, Vector(vector.rotationInDegrees, newSpeed))
    }

    fun decelerate(): Ship {
        val newSpeed = if (vector.speed == -1.0) vector.speed else vector.speed - 1
        return Ship(id, remainingLives, position, Vector(vector.rotationInDegrees, newSpeed))
    }

    override fun move(gameWidth: Double, gameHeight: Double): Ship {

        val xPosition = position.x + vector.speed * -sin(Math.toRadians(vector.rotationInDegrees))
        val yPosition = position.y + vector.speed * cos(Math.toRadians(vector.rotationInDegrees))

        val newPosition = if ( xPosition < gameWidth && xPosition > 0 && yPosition < gameHeight && yPosition > 0 ) {
            Position(xPosition, yPosition)
        } else { Position(Math.random()*gameWidth, Math.random()*gameHeight)}

        return Ship(id, remainingLives, newPosition, Vector(vector.rotationInDegrees, vector.speed))
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

}
