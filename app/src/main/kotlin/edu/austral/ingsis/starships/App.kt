package edu.austral.ingsis.starships

import edu.austral.ingsis.starships.model.*
import edu.austral.ingsis.starships.ui.*
import edu.austral.ingsis.starships.ui.ElementColliderType.Triangular
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
        val BULLET_IMAGE_REF = ImageRef("bullet", 70.0, 70.0)
    }

    override fun start(primaryStage: Stage) {

        val gameShip = Ship(
            "starship1",
            3,
            Position(450.0, 450.0),
            Vector(350.0, 0.0)
        )
        val gameShip2 = Ship(
            "starship2",
            3,
            Position(100.0, 100.0),
            Vector(350.0, 0.0)
        )

        val gameState = GameState(
            800.0,
            800.0,
            listOf(gameShip, gameShip2),
            State.RUN
        )

        val gameManager = GameManager(
            gameState,
            mapOf(
                Pair(KeyCode.UP, ShipMovement(gameShip.getId(), KeyMovement.ACCELERATE, MovementType.CLICK)),
                Pair(KeyCode.DOWN, ShipMovement(gameShip.getId(), KeyMovement.STOP, MovementType.CLICK)),
                Pair(KeyCode.LEFT, ShipMovement(gameShip.getId(), KeyMovement.TURN_LEFT, MovementType.MAINTAIN)),
                Pair(KeyCode.RIGHT, ShipMovement(gameShip.getId(), KeyMovement.TURN_RIGHT, MovementType.MAINTAIN)),
                Pair(KeyCode.SPACE, ShipMovement(gameShip.getId(), KeyMovement.SHOOT, MovementType.CLICK)),
                Pair(KeyCode.W, ShipMovement(gameShip2.getId(), KeyMovement.ACCELERATE, MovementType.CLICK)),
                Pair(KeyCode.S, ShipMovement(gameShip2.getId(), KeyMovement.STOP, MovementType.CLICK)),
                Pair(KeyCode.A, ShipMovement(gameShip2.getId(), KeyMovement.TURN_LEFT, MovementType.MAINTAIN)),
                Pair(KeyCode.D, ShipMovement(gameShip2.getId(), KeyMovement.TURN_RIGHT, MovementType.MAINTAIN)),
                Pair(KeyCode.E, ShipMovement(gameShip2.getId(), KeyMovement.SHOOT, MovementType.CLICK)),
                Pair(KeyCode.P, ShipMovement(gameShip.getId(), MenuAction.TOGGLE_PAUSE, MovementType.CLICK))
            ),
            mutableListOf()
        )

        val starship = ElementModel(gameShip.getId(), gameShip.getPosition().x, gameShip.getPosition().x, 40.0, 40.0, gameShip.getVector().rotationInDegrees, Triangular, STARSHIP_IMAGE_REF)
        facade.elements[gameShip.getId()] = starship

        val starship2 = ElementModel(gameShip2.getId(), gameShip2.getPosition().x, gameShip2.getPosition().x, 40.0, 40.0, gameShip2.getVector().rotationInDegrees, Triangular, STARSHIP_IMAGE_REF)
        facade.elements[gameShip2.getId()] = starship2

        facade.timeListenable.addEventListener(
            TimeListener(facade.elements, facade, gameManager)
        )

        facade.collisionsListenable.addEventListener(CollisionListener(gameManager, facade))

        keyTracker.keyPressedListenable.addEventListener(
            KeyPressedListener(gameManager),
        )

        keyTracker.keyReleasedListenable.addEventListener(
            KeyReleasedListener(gameManager),
        )

        val root = facade.view
        root.id = "pane"
        val scene = Scene(root)
        keyTracker.scene = scene
        scene.stylesheets.add(this::class.java.classLoader.getResource("styles.css")?.toString())
        primaryStage.scene = scene
        primaryStage.height = 800.0
        primaryStage.width = 800.0

        facade.start()
        facade.showCollider.set(false)
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
        gameManager.passTime(event.secondsSinceLastTime, facade.elements)
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
        gameManager.pressKey(event.key)
    }
}

class KeyReleasedListener(
    private val gameManager: GameManager,
): EventListener<KeyReleased> {
    override fun handle(event: KeyReleased) {
        gameManager.releaseKey(event.key)
    }
}
