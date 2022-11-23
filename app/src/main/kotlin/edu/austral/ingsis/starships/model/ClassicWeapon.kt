package edu.austral.ingsis.starships.model

import java.util.*
import kotlin.collections.List
import kotlin.math.cos
import kotlin.math.sin

class ClassicWeapon : Weapon {
    override fun shoot(shipPosition: Position, shipVector: Vector): List<Bullet> {
        return listOf(Bullet(
            UUID.randomUUID().toString(),
            Position(
                shipPosition.x + 50 * -sin(Math.toRadians(shipVector.rotationInDegrees)),
                shipPosition.y + 50 * cos(Math.toRadians(shipVector.rotationInDegrees))
            ),
            Vector(shipVector.rotationInDegrees, 3.0),
            10.0
        ))
    }
}
