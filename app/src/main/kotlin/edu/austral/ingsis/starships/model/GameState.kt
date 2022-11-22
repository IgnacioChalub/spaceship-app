package edu.austral.ingsis.starships.model

import java.util.UUID


data class GameState(
    val gameWidth: Double,
    val gameHeight: Double,
    val gameObjects: List<Collidable>,
    val state: State
) {

    fun moveShip(id: String, movement: KeyMovement, secondsPassed: Double): GameState {
        if(state == State.PAUSE) {
            return this.copy()
        }
        val ship = (gameObjects.find { it ->  it.getId() == id} as Ship) ?: throw Error("ship not found")
        val newShips = gameObjects.filter { it ->  it.getId() != id}
        return when(movement) {
            KeyMovement.TURN_RIGHT -> this.copy(gameObjects = newShips.plus(ship.turnRight(secondsPassed)))
            KeyMovement.TURN_LEFT -> this.copy(gameObjects = newShips.plus(ship.turnLeft(secondsPassed)))
            KeyMovement.ACCELERATE -> this.copy(gameObjects = newShips.plus(ship.accelerate()))
            KeyMovement.STOP -> this.copy(gameObjects = newShips.plus(ship.decelerate()))
            KeyMovement.SHOOT -> this.copy(gameObjects = gameObjects.plus(ship.shoot()))
        }
    }

    fun toggleState(): GameState {
        val newState = if(state == State.PAUSE) {
            State.RUN
        }else {
            State.PAUSE
        }
        return this.copy(state = newState)
    }

    fun move(secondsPassed: Double): GameState {
        if(state == State.PAUSE) {
            return this.copy()
        }
        return this.copy(gameObjects = manageGameObjects(secondsPassed))
    }

    private fun manageGameObjects(secondsPassed: Double): List<Collidable> {
        val newObjects = gameObjects
            .map { it ->
                it.move(secondsPassed, gameWidth, gameHeight)
            }.filter { it ->
                it.getPosition().x < gameWidth && it.getPosition().y < gameHeight && it.getPosition().x > 0.0 && it.getPosition().y > 0
            }
        return if(Math.random()*100 < 99.5) {
            newObjects
        } else {
            newObjects
                .plus(
                    Asteroid(
                        UUID.randomUUID().toString(),
                        Position(Math.random()*gameWidth, Math.random()*gameHeight),
                        Vector(Math.random()*359, 0.6),
                        20.0
                    )
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
        return this.copy(gameObjects = remainingObjects)
    }


}
