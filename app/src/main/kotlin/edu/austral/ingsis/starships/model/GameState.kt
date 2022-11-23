package edu.austral.ingsis.starships.model

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
        val ship = gameObjects.find { it ->  it.getId() == id} ?: throw Error("ship with id $id not found")
        val remainingObjects = gameObjects.filter { it ->  it.getId() != id}
        return when (ship) {
            is Ship -> doMovement(ship, secondsPassed, movement, remainingObjects)
            else -> this.copy()
        }
    }

    private fun doMovement(ship: Ship, secondsPassed: Double, movement: KeyMovement, remainingObjects: List<Collidable>): GameState {
        return when(movement) {
            KeyMovement.TURN_RIGHT -> this.copy(gameObjects = remainingObjects.plus(ship.turnRight(secondsPassed)))
            KeyMovement.TURN_LEFT -> this.copy(gameObjects = remainingObjects.plus(ship.turnLeft(secondsPassed)))
            KeyMovement.ACCELERATE -> this.copy(gameObjects = remainingObjects.plus(ship.accelerate()))
            KeyMovement.STOP -> this.copy(gameObjects = remainingObjects.plus(ship.decelerate()))
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
            .map { it -> it.move(secondsPassed, gameWidth, gameHeight) }.filter { it -> isOutOfBounds(it) }
        return if(Math.random()*100 < 99.5) {
            newObjects
        } else {
            newObjects.plus(Asteroid.new(gameWidth, gameHeight))
        }
    }

    private fun isOutOfBounds(it: Collidable): Boolean {
        return it.getPosition().x < gameWidth && it.getPosition().y < gameHeight && it.getPosition().x > 0.0 && it.getPosition().y > 0
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
