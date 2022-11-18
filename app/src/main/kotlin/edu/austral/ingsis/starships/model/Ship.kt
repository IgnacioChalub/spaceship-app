package edu.austral.ingsis.starships.model

import java.util.*
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

    fun shoot(): Bullet {

        val xPosition = position.x + 50 * -sin(Math.toRadians(vector.rotationInDegrees))
        val yPosition = position.y + 50 * cos(Math.toRadians(vector.rotationInDegrees))

        return Bullet(UUID.randomUUID().toString(), Position(xPosition, yPosition), Vector(vector.rotationInDegrees,3.0), 10.0)
    }

    override fun move(secondsPassed: Double, gameWidth: Double, gameHeight: Double): Ship {

        val xPosition = position.x + (vector.speed * -sin(Math.toRadians(vector.rotationInDegrees)) * secondsPassed * 150)
        val yPosition = position.y + vector.speed * cos(Math.toRadians(vector.rotationInDegrees)) * secondsPassed * 150

        val newPosition = if ( xPosition < gameWidth && xPosition > 0 && yPosition < gameHeight && yPosition > 0 ) {
            Position(xPosition, yPosition)
        } else { Position(Math.random()*gameWidth, Math.random()*gameHeight) }

        return Ship(id, remainingLives, newPosition, Vector(vector.rotationInDegrees, vector.speed))
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

    override fun collide(collidable: Collidable): Optional<Collidable> {
        return when (collidable) {
            is Bullet -> {
                if(remainingLives-1 <= 0) return Optional.empty<Collidable>()
                return Optional.of(Ship(id,remainingLives-1, position, vector))
            }
            is Asteroid -> {
                if(remainingLives-1 <= 0) return Optional.empty<Collidable>()
                return Optional.of(Ship(id,remainingLives-1, Position(position.x+40, position.y+40), vector))
            }
            is Ship -> {
                if(remainingLives-1 <= 0) return Optional.empty<Collidable>()
                return Optional.of(Ship(id,remainingLives-1, Position(position.x+20, position.y+20), vector))
            }
        }
    }


}
