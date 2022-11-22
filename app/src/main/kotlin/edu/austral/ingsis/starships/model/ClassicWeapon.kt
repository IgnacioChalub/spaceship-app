package edu.austral.ingsis.starships.model

import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class ClassicWeapon : Weapon {
    override fun shoot(shipPosition: Position, shipVector: Vector): Bullet {
        val xPosition = shipPosition.x + 50 * -sin(Math.toRadians(shipVector.rotationInDegrees))
        val yPosition = shipPosition.y + 50 * cos(Math.toRadians(shipVector.rotationInDegrees))
        return Bullet(
            UUID.randomUUID().toString(),
            Position(xPosition, yPosition),
            Vector(shipVector.rotationInDegrees, 3.0),
            10.0
        )
    }
}
