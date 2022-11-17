package edu.austral.ingsis.starships

import edu.austral.ingsis.starships.model.*
import edu.austral.ingsis.starships.ui.*
import edu.austral.ingsis.starships.ui.ElementColliderType.*
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage

fun main() {
    launch(Starships::class.java)
}

class Starships() : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())
    private val facade = ElementsViewFacade(imageResolver)
    private val keyTracker = KeyTracker()

    companion object {
        val STARSHIP_IMAGE_REF = ImageRef("starship", 70.0, 70.0)
    }

    override fun start(primaryStage: Stage) {

        val gameShip = Ship(
            "starship1",
            3,
            Position(450.0, 450.0),
            Vector(350.0, 0.0)
        )

        val gameState = GameState(
            800.0,
            800.0,
            listOf(gameShip)
        )

        val gameManager = GameManager(
            gameState,
            mapOf(
                Pair(KeyCode.UP, ShipMovement(gameShip.getId(), KeyMovement.ACCELERATE)),
                Pair(KeyCode.DOWN, ShipMovement(gameShip.getId(), KeyMovement.STOP)),
                Pair(KeyCode.LEFT, ShipMovement(gameShip.getId(), KeyMovement.TURN_LEFT)),
                Pair(KeyCode.RIGHT, ShipMovement(gameShip.getId(), KeyMovement.TURN_RIGHT)),
                Pair(KeyCode.SPACE, ShipMovement(gameShip.getId(), KeyMovement.SHOOT))
            )
        )

        val starship = ElementModel(gameShip.getId(), gameShip.getPosition().x, gameShip.getPosition().x, 40.0, 40.0, gameShip.getVector().rotationInDegrees, Triangular, STARSHIP_IMAGE_REF)
        facade.elements[gameShip.getId()] = starship

        facade.timeListenable.addEventListener(
            TimeListener(facade.elements, facade, gameManager)
        )

        facade.collisionsListenable.addEventListener(CollisionListener(gameManager, facade))

        keyTracker.keyPressedListenable.addEventListener(
            KeyPressedListener(gameManager),
        )

        val scene = Scene(facade.view)
        keyTracker.scene = scene

        primaryStage.scene = scene
        primaryStage.height = gameState.gameHeight
        primaryStage.width = gameState.gameWidth

        facade.start()
        keyTracker.start()
        primaryStage.show()
    }

    override fun stop() {
        facade.stop()
        keyTracker.stop()
    }
}

class TimeListener(
        private val elements: Map<String, ElementModel>,
        private val facade: ElementsViewFacade,
        private val gameManager: GameManager
    ) : EventListener<TimePassed> {

    override fun handle(event: TimePassed) {
        gameManager.passTime(event.currentTimeInSeconds - event.secondsSinceLastTime, facade.elements)
        gameManager.addElements(facade.elements)
        gameManager.gameState.gameObjects.forEach {it ->
            elements.getValue(it.getId()).rotationInDegrees.set(it.getVector().rotationInDegrees)
            elements.getValue(it.getId()).x.set(it.getPosition().x)
            elements.getValue(it.getId()).y.set(it.getPosition().y)
        }
    }
}

class CollisionListener(
    private val gameManager: GameManager,
    private val facade: ElementsViewFacade,
) : EventListener<Collision> {
    override fun handle(event: Collision) {
        gameManager.collision(event.element1Id, event.element2Id, facade.elements)
    }

}

class KeyPressedListener(
        private val gameManager: GameManager,
    ): EventListener<KeyPressed> {
    override fun handle(event: KeyPressed) {
        event.currentPressedKeys.forEach { key -> gameManager.moveShip(1.0, key) }
    }
}

class GameManager(
        var gameState: GameState,
        private val keyMap: Map<KeyCode, ShipMovement>
    ) {

    fun moveShip(secondsPassed: Double, movement: KeyCode) {
        val shipMovement = keyMap.getValue(movement)
        gameState = gameState.moveShip(shipMovement.id, shipMovement.movement, secondsPassed)
    }

    fun passTime(secondsPassed: Double, elements: MutableMap<String, ElementModel>) {
      val newGameState = gameState.move(secondsPassed)
      val removedGameObjects = gameState.gameObjects.filter { obj -> !newGameState.gameObjects.any { newObj -> newObj.getId() == obj.getId()} }
      removedGameObjects.forEach { it -> elements.remove(it.getId()) }
      gameState = newGameState
    }

    fun addElements(elements: MutableMap<String, ElementModel>) {
        val newElements = gameState.gameObjects.filter { !elements.keys.contains(it.getId()) }
        newElements.forEach { elements[it.getId()] = elementToUI(it) }
    }

    fun collision(from: String, to: String,  elements: MutableMap<String, ElementModel>) {
        val newGameState = gameState.collision(from, to)
        val removedGameObjects = gameState.gameObjects.filter { obj -> !newGameState.gameObjects.any { newObj -> newObj.getId() == obj.getId()} }
        removedGameObjects.forEach { it -> elements.remove(it.getId()) }
        gameState = newGameState
    }

    private fun elementToUI(element: Collidable): ElementModel {
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
            60.0,
            70.0,
            ship.getVector().rotationInDegrees,
            ElementColliderType.Triangular,
            ImageRef("spaceship", 60.0, 70.0)
        )
    }

    private fun asteroidToAsteroidUI(asteroid: Asteroid): ElementModel {
        return ElementModel(
            asteroid.getId(),
            asteroid.getPosition().x,
            asteroid.getPosition().y,
            50.0,
            50.0,
            asteroid.getVector().rotationInDegrees,
            ElementColliderType.Elliptical,
            null
        )
    }

    private fun bulletToBulletUI(bullet: Bullet): ElementModel {
        return ElementModel(
            bullet.getId(),
            bullet.getPosition().x,
            bullet.getPosition().y,
            10.0,
            5.0,
            bullet.getVector().rotationInDegrees,
            ElementColliderType.Rectangular,
            null
        )
    }

}

data class ShipMovement(val id: String, val movement: KeyMovement)