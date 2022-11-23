package edu.austral.ingsis.starships

import edu.austral.ingsis.starships.model.*
import edu.austral.ingsis.starships.ui.ElementColliderType
import edu.austral.ingsis.starships.ui.ElementModel
import edu.austral.ingsis.starships.ui.ImageRef
import javafx.scene.control.Label
import javafx.scene.input.KeyCode

class GameManager(
    var gameState: GameState,
    private val playerKeyMap: Map<KeyCode, ShipMovement>,
    private val gameKeyMap: Map<KeyCode, MenuAction>,
    private val keysPressed: MutableList<KeyCode>
    ) {

    private fun moveShip(movement: KeyCode, secondsPassed: Double) {
        val shipMovement = playerKeyMap.getValue(movement)
        gameState = gameState.moveShip(shipMovement.id, shipMovement.movement, secondsPassed)
    }

    fun passTime(secondsPassed: Double, elements: MutableMap<String, ElementModel>) {
      keysPressed.forEach {it -> moveShip(it, secondsPassed)}
      val newGameState = gameState.move(secondsPassed)
      val removedGameObjects = gameState.gameObjects.filter { obj -> !newGameState.gameObjects.any { newObj -> newObj.getId() == obj.getId()} }
      removedGameObjects.forEach { it -> elements.remove(it.getId()) }
      gameState = newGameState
    }

    fun addElements(elements: MutableMap<String, ElementModel>) {
        val newElements = gameState.gameObjects.filter { !elements.keys.contains(it.getId()) }
        newElements.forEach { elements[it.getId()] = elementToUI(it) }
//        gameState.gameObjects.forEach { elements[it.getId()] = elementToUI(it) }
    }

    fun collision(from: String, to: String,  elements: MutableMap<String, ElementModel>) {
        val newGameState = gameState.collision(from, to)
        val removedGameObjects = gameState.gameObjects.filter { obj -> !newGameState.gameObjects.any { newObj -> newObj.getId() == obj.getId()} }
        removedGameObjects.forEach { it -> elements.remove(it.getId()) }
        gameState = newGameState
    }

    fun elementToUI(element: Collidable): ElementModel {
        return when (element) {
            is Ship -> starshipToStarshipUI(element)
            is Asteroid -> asteroidToAsteroidUI(element)
            is Bullet -> bulletToBulletUI(element)
        }
    }

    private fun starshipToStarshipUI(ship: Ship): ElementModel {
        return ElementModel(
            ship.getId(),
            ship.getPosition().x,
            ship.getPosition().y,
            40.0,
            40.0,
            ship.getVector().rotationInDegrees,
            ElementColliderType.Triangular,
            ImageRef("starship", 60.0, 70.0)
        )
    }

    private fun asteroidToAsteroidUI(asteroid: Asteroid): ElementModel {
        return ElementModel(
            asteroid.getId(),
            asteroid.getPosition().x,
            asteroid.getPosition().y,
            100.0,
            100.0,
            asteroid.getVector().rotationInDegrees,
            ElementColliderType.Elliptical,
            ImageRef("asteroid", 60.0, 70.0)
        )
    }

    private fun bulletToBulletUI(bullet: Bullet): ElementModel {
        return ElementModel(
            bullet.getId(),
            bullet.getPosition().x,
            bullet.getPosition().y,
            20.0,
            20.0,
            bullet.getVector().rotationInDegrees,
            ElementColliderType.Rectangular,
            ImageRef("bullet", 60.0, 70.0)
        )
    }

    fun pressKey(key: KeyCode) {
        when (val action = if (playerKeyMap.containsKey(key)) { playerKeyMap.getValue(key) } else { gameKeyMap.getValue(key) }) {
            is ShipMovement -> manageShipMovement(key)
            is MenuAction -> manageMenuAction(action, key)
        }
    }

    private fun manageShipMovement(key: KeyCode) {
        when (playerKeyMap.getValue(key).movementType) {
            MovementType.CLICK -> moveShip(key, 1.0)
            MovementType.MAINTAIN -> if(!keysPressed.contains(key)) { keysPressed.add(key) }
        }
    }

    private fun manageMenuAction(menuAction: MenuAction, key: KeyCode) {
        when (menuAction.action) {
            KeyMenuAction.TOGGLE_PAUSE -> gameState = gameState.toggleState()
        }
    }

    fun releaseKey(key: KeyCode) {
        keysPressed.remove(key)
    }

    fun updateLives(id: String): Label {
        val ship = gameState.gameObjects.find { it ->
            it.getId() == id
        }
        return when (ship) {
            is Ship -> Label(ship.remainingLives.toString())
            else -> return Label("")
        }
    }

}

data class ShipMovement(val id: String, val movement: KeyMovement, val movementType: MovementType)
data class MenuAction(val action: KeyMenuAction)

enum class MovementType {
    CLICK,
    MAINTAIN
}
