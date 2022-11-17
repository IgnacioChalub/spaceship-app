package edu.austral.ingsis.starships.model

import java.util.*

class GameState(
    val gameWidth: Double,
    val gameHeight: Double,
    val gameObjects: List<Collidable>
) {

    fun moveShip(id: String, movement: KeyMovement): GameState {
        val ship = (gameObjects.find { it ->  it.getId() == id} as Ship) ?: throw Error("ship not found")
        val newShips = gameObjects.filter { it ->  it.getId() != id}
        return when(movement) {
            KeyMovement.TURN_RIGHT -> GameState(gameWidth, gameWidth, newShips.plus(ship.turnRight()))
            KeyMovement.TURN_LEFT -> GameState(gameWidth, gameWidth, newShips.plus(ship.turnLeft()))
            KeyMovement.ACCELERATE -> GameState(gameWidth, gameWidth, newShips.plus(ship.accelerate()))
            KeyMovement.STOP -> GameState(gameWidth, gameWidth, newShips.plus(ship.decelerate()))
            KeyMovement.SHOOT -> GameState(gameWidth, gameWidth, gameObjects.plus(Bullet(UUID.randomUUID().toString(), ship.getPosition(), Vector(ship.getVector().rotationInDegrees,3.0))))
        }
    }

    fun move(): GameState {
        return GameState(
            gameWidth,
            gameHeight,
            gameObjects.map { it ->
                it.move(gameWidth, gameHeight)
            }.filter { it ->
                it.getPosition().x < gameWidth && it.getPosition().y < gameHeight && it.getPosition().x > 0.0 && it.getPosition().y > 0
            }
        )
    }

}
