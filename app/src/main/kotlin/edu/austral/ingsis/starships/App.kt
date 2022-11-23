package edu.austral.ingsis.starships

import edu.austral.ingsis.starships.factory.ClassicGamesFactory
import edu.austral.ingsis.starships.model.*
import edu.austral.ingsis.starships.ui.*
import edu.austral.ingsis.starships.ui.ElementColliderType.Triangular
import javafx.application.Application
import javafx.application.Application.launch
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
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

        val lives = StackPane()
        var lives1 = Label("3")
        var lives2 = Label("3")
        lives1.style= "-fx-font-family: VT323; -fx-font-size: 50"
        lives2.style= "-fx-font-family: VT323; -fx-font-size: 50"
        lives1.textFill = Color.color(0.9,0.9,0.9)
        lives2.textFill = Color.color(0.9,0.9,0.9)
        val div= HBox(50.0)
        div.alignment= Pos.TOP_LEFT
        div.children.addAll(lives1, lives2)
        div.padding= Insets(10.0,10.0,10.0,10.0)
        lives.children.addAll(div)

        val pane=StackPane()

        val root = facade.view
        pane.children.addAll(root, lives)
        root.id = "pane"
        val scene = Scene(pane)
        keyTracker.scene = scene
        scene.stylesheets.add(this::class.java.classLoader.getResource("styles.css")?.toString())
        primaryStage.scene = scene
        primaryStage.height = 800.0
        primaryStage.width = 800.0

        facade.start()
        facade.showCollider.set(false)
        keyTracker.start()
        primaryStage.show()

        //LISTENERS
        facade.timeListenable.addEventListener(
            TimeListener(facade.elements, facade, gameManager, div)
        )

        facade.collisionsListenable.addEventListener(CollisionListener(gameManager, facade))

        keyTracker.keyPressedListenable.addEventListener(
            KeyPressedListener(gameManager),
        )

        keyTracker.keyReleasedListenable.addEventListener(
            KeyReleasedListener(gameManager),
        )
    }

    override fun stop() {
        facade.stop()
        keyTracker.stop()
    }
}

class TimeListener(
        private val elements: Map<String, ElementModel>,
        private val facade: ElementsViewFacade,
        private val gameManager: GameManager,
        private val div: HBox
    ) : EventListener<TimePassed> {

    override fun handle(event: TimePassed) {
        gameManager.passTime(event.secondsSinceLastTime, facade.elements)
        gameManager.addElements(facade.elements)
        gameManager.gameState.gameObjects.forEach {it ->
            elements.getValue(it.getId()).rotationInDegrees.set(it.getVector().rotationInDegrees)
            elements.getValue(it.getId()).x.set(it.getPosition().x)
            elements.getValue(it.getId()).y.set(it.getPosition().y)
        }
        updateLives(div)
    }

    private fun updateLives(div: HBox) {
        val lives1=gameManager.updateLives("starship1")
        val lives2=gameManager.updateLives("starship2")
        lives1.style= "-fx-font-family: VT323; -fx-font-size: 50"
        lives2.style= "-fx-font-family: VT323; -fx-font-size: 50"
        lives1.textFill = Color.color(0.9,0.9,0.9)
        lives2.textFill = Color.color(0.9,0.9,0.9)
        div.children[0] = lives1
        div.children[1] = lives2
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
