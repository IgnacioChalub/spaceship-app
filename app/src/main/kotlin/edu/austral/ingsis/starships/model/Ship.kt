package edu.austral.ingsis.starships.model

import java.util.*
import kotlin.collections.List
import kotlin.math.cos
import kotlin.math.sin

data class Ship(
    private val id: String,
    val remainingLives: Int,
    private val position: Position,
    private val vector: Vector,
    private val weapon: Weapon,
) : Collidable {

    fun turnLeft(secondsPassed: Double): Ship = this.copy(vector = Vector(vector.rotationInDegrees-(200*secondsPassed), vector.speed))

    fun turnRight(secondsPassed: Double): Ship = this.copy(vector = Vector(vector.rotationInDegrees+(200*secondsPassed), vector.speed))

    fun accelerate(): Ship {
        val newSpeed = if (vector.speed == 1.0) vector.speed else vector.speed + 1
        return this.copy(vector = Vector(vector.rotationInDegrees, newSpeed))
    }

    fun decelerate(): Ship {
        val newSpeed = if (vector.speed == -1.0) vector.speed else vector.speed - 1
        return this.copy(vector =  Vector(vector.rotationInDegrees, newSpeed))
    }

    fun shoot(): List<Bullet> {
        return weapon.shoot(position, vector)
    }

    override fun move(secondsPassed: Double, gameWidth: Double, gameHeight: Double): Ship {

        val xPosition = position.x + (vector.speed * -sin(Math.toRadians(vector.rotationInDegrees)) * secondsPassed * 150)
        val yPosition = position.y + vector.speed * cos(Math.toRadians(vector.rotationInDegrees)) * secondsPassed * 150

        val newPosition = if ( xPosition < gameWidth && xPosition > 0 && yPosition < gameHeight && yPosition > 0 ) {
            Position(xPosition, yPosition)
        } else { Position(Math.random()*gameWidth, Math.random()*gameHeight) }

        return this.copy(position = newPosition)
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

    override fun collide(collidable: Collidable): Optional<Collidable> {
        when (collidable) {
            is Bullet -> {
                if(remainingLives-1 <= 0) return Optional.empty<Collidable>()
                return Optional.of(this.copy(remainingLives = remainingLives-1))
            }
            is Asteroid -> {
                if(remainingLives-1 <= 0) return Optional.empty<Collidable>()
                return Optional.of(this.copy(remainingLives = remainingLives-1, position = Position(position.x+40, position.y+40)))
            }
            is Ship -> {
                if(remainingLives-1 <= 0) return Optional.empty<Collidable>()
                return Optional.of(this.copy(remainingLives = remainingLives-1, position = Position(position.x+20, position.y+20)))
            }
        }
    }


}
