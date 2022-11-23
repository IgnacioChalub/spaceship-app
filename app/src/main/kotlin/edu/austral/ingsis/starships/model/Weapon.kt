package edu.austral.ingsis.starships.model

sealed interface Weapon {
    fun shoot(shipPosition: Position, shipVector: Vector): List<Bullet>
}