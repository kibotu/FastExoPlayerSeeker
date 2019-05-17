package net.kibotu.fastexoplayerseeker

class SeekPositionEmitter {
    internal var seekFast: ((Long) -> Unit)? = null
    fun seekFast(position: Long) {
        seekFast?.invoke(position)
    }
}