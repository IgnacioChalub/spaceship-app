package edu.austral.ingsis.starships.model

class Asteroid(
    private val id: String,
    private val position: Position,
    private val vector: Vector,
    val remainingDamageSustained: Int
) : Collidable {

    override fun move(gameWidth: Double, gameHeight: Double): Collidable {
        TODO("Not yet implemented")
    }

    override fun getId(): String = id

    override fun getPosition(): Position = position

    override fun getVector(): Vector = vector

}