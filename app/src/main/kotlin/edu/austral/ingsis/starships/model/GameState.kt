package edu.austral.ingsis.starships.model

import java.util.UUID


class GameState(
    val gameWidth: Double,
    val gameHeight: Double,
    val gameObjects: List<Collidable>
) {

    fun moveShip(id: String, movement: KeyMovement, secondsPassed: Double): GameState {
        val ship = (gameObjects.find { it ->  it.getId() == id} as Ship) ?: throw Error("ship not found")
        val newShips = gameObjects.filter { it ->  it.getId() != id}
        return when(movement) {
            KeyMovement.TURN_RIGHT -> GameState(gameWidth, gameWidth, newShips.plus(ship.turnRight()))
            KeyMovement.TURN_LEFT -> GameState(gameWidth, gameWidth, newShips.plus(ship.turnLeft()))
            KeyMovement.ACCELERATE -> GameState(gameWidth, gameWidth, newShips.plus(ship.accelerate()))
            KeyMovement.STOP -> GameState(gameWidth, gameWidth, newShips.plus(ship.decelerate()))
            KeyMovement.SHOOT -> GameState(gameWidth, gameWidth, gameObjects.plus(ship.shoot()))
        }
    }

    fun move(secondsPassed: Double): GameState {
        if(Math.random()*100 < 99.5) {
            return GameState(
                gameWidth,
                gameHeight,
                gameObjects
                    .map { it ->
                        it.move(secondsPassed, gameWidth, gameHeight)
                    }.filter { it ->
                        it.getPosition().x < gameWidth && it.getPosition().y < gameHeight && it.getPosition().x > 0.0 && it.getPosition().y > 0
                    }
            )
        } else {
            return GameState(
                gameWidth,
                gameHeight,
                gameObjects
                    .plus(
                        Asteroid(
                            UUID.randomUUID().toString(),
                            Position(Math.random()*gameWidth, Math.random()*gameHeight),
                            Vector(Math.random()*359, 0.6),
                            20.0
                        )
                    )
                    .map { it ->
                        it.move(secondsPassed, gameWidth, gameHeight)
                    }.filter { it ->
                        it.getPosition().x < gameWidth && it.getPosition().y < gameHeight && it.getPosition().x > 0.0 && it.getPosition().y > 0
                    }
            )
        }
    }

    fun collision(from: String, to: String): GameState {
        var remainingObjects = gameObjects.filter { it -> it.getId() != from && it.getId() != to}
        val fromObj = gameObjects.find { it -> it.getId() == from }
        val toObj = gameObjects.find { it -> it.getId() == to }
        if(fromObj == null || toObj == null) return this
        val c1 = fromObj.collide(toObj)
        val c2 = toObj.collide(fromObj)

        if(c1.isPresent) remainingObjects = remainingObjects.plus(c1.get())
        if(c2.isPresent) remainingObjects = remainingObjects.plus(c2.get())

        return GameState(
            gameWidth,
            gameHeight,
            remainingObjects
        )
    }

}
