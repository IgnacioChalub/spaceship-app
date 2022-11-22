package edu.austral.ingsis.starships

import edu.austral.ingsis.starships.factory.ClassicGamesFactory
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

        val factory = ClassicGamesFactory()
        val gameManager = factory.twoPlayersGame()

        gameManager.gameState.gameObjects.forEach { it ->
            facade.elements[it.getId()] = gameManager.elementToUI(it)
        }

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
