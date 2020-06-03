package socks

fun main() {
    val washes = 2
    val clean = arrayListOf(1, 2, 1, 1)
    val dirty = arrayListOf(1, 4, 3, 2, 4)

    println(getMaxNumberOfSocksPair(washes, clean, dirty))
}

fun getMaxNumberOfSocksPair(noOfWashes: Int, cleanPile: ArrayList<Int>, dirtyPile: ArrayList<Int>): Int {
    var pair = 0

    val uniqueNoOfCleanSocks = cleanPile.toHashSet()
    val uniqueNoOfDirtySocks = dirtyPile.toHashSet()

    val remainingCleanSocks = arrayListOf<Int>()

    if (uniqueNoOfCleanSocks.size < cleanPile.size) {
        for (sock in uniqueNoOfCleanSocks) {
            val numberAvailable = cleanPile.filter { it == sock }.size
            if (numberAvailable >= 2) {
                pair += numberAvailable / 2

                if (numberAvailable % 2 != 0) {
                    remainingCleanSocks.add(sock)
                }
            } else {
                remainingCleanSocks.add(sock)
            }
        }
    } else {
        remainingCleanSocks.addAll(uniqueNoOfCleanSocks)
    }

    var washTracker = noOfWashes
    var dirtyPairsAvailable = 0
    for (sock in uniqueNoOfDirtySocks) {
        if (washTracker == 0) {
            return pair
        }

        var numberAvailable = dirtyPile.filter { it == sock }.size
        if (remainingCleanSocks.contains(sock)) {
            numberAvailable--
            washTracker--
            remainingCleanSocks.remove(sock)

            pair++
        }

        if (numberAvailable >= 2) {
            dirtyPairsAvailable += numberAvailable / 2
        }
    }

    washTracker = washTracker.div(2)
    if (washTracker > 0 && dirtyPairsAvailable > 0) {
        pair += when {
            washTracker > dirtyPairsAvailable -> dirtyPairsAvailable

            else -> washTracker
        }
    }

    return pair
}