package edu.austral.ingsis.starships.factory

import edu.austral.ingsis.starships.GameManager
import edu.austral.ingsis.starships.MenuAction
import edu.austral.ingsis.starships.MovementType
import edu.austral.ingsis.starships.ShipMovement
import edu.austral.ingsis.starships.model.*
import javafx.scene.input.KeyCode

class ClassicGamesFactory {

    fun singlePlayerGame(): GameManager {
        val gameShip = Ship(
            "starship1",
            3,
            Position(450.0, 450.0),
            Vector(350.0, 0.0),
            ClassicWeapon()
        )
        val gameState = GameState(
            800.0,
            800.0,
            listOf(gameShip),
            State.PAUSE
        )

        return GameManager(
            gameState,
            mapOf(
                Pair(KeyCode.UP, ShipMovement(gameShip.getId(), KeyMovement.ACCELERATE, MovementType.CLICK)),
                Pair(KeyCode.DOWN, ShipMovement(gameShip.getId(), KeyMovement.STOP, MovementType.CLICK)),
                Pair(KeyCode.LEFT, ShipMovement(gameShip.getId(), KeyMovement.TURN_LEFT, MovementType.MAINTAIN)),
                Pair(KeyCode.RIGHT, ShipMovement(gameShip.getId(), KeyMovement.TURN_RIGHT, MovementType.MAINTAIN)),
                Pair(KeyCode.SPACE, ShipMovement(gameShip.getId(), KeyMovement.SHOOT, MovementType.CLICK)),
            ),
            mapOf(
                Pair(KeyCode.P, MenuAction(KeyMenuAction.TOGGLE_PAUSE))
            ),
            mutableListOf()
        );
    }

    fun twoPlayersGame(): GameManager {
        val gameShip = Ship(
            "starship1",
            3,
            Position(450.0, 450.0),
            Vector(350.0, 0.0),
            ClassicWeapon()
        )
        val gameShip2 = Ship(
            "starship2",
            3,
            Position(100.0, 100.0),
            Vector(350.0, 0.0),
            DoubleWeapon()
        )

        val gameState = GameState(
            800.0,
            800.0,
            listOf(gameShip, gameShip2),
            State.PAUSE
        )

        return GameManager(
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
            ),
            mapOf(
                Pair(KeyCode.P, MenuAction(KeyMenuAction.TOGGLE_PAUSE))
            ),
            mutableListOf()
        );
    }
}