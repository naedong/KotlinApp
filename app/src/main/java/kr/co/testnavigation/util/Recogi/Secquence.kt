package kr.co.testnavigation.util.Recogi

class Secquence {
     fun CharSequence.contains(other: CharSequence, ignoreCase: Boolean = false): Boolean =
        if (other is String) indexOf(other, ignoreCase = ignoreCase) >= 0
        else indexOf(other, 0, length, ignoreCase) >= 0


     fun CharSequence.indexOf(other: CharSequence, startIndex: Int, endIndex: Int, ignoreCase: Boolean, last: Boolean = false): Int {
        val indices = if (!last)
            startIndex.coerceAtLeast(0)..endIndex.coerceAtMost(length)
        else
            startIndex.coerceAtMost(lastIndex) downTo endIndex.coerceAtLeast(0)

        if (this is String && other is String) { // smart cast
            for (index in indices) {
                if (other.regionMatches(0, this, index, other.length, ignoreCase))
                    return index
            }
        } else {
            for (index in indices) {
                if (other.regionMatchesImpl(0, this, index, other.length, ignoreCase))
                    return index
            }
        }
        return -1
    }

    internal fun CharSequence.regionMatchesImpl(thisOffset: Int, other: CharSequence, otherOffset: Int, length: Int, ignoreCase: Boolean): Boolean {
        if ((otherOffset < 0) || (thisOffset < 0) || (thisOffset > this.length - length) || (otherOffset > other.length - length)) {
            return false
        }

        for (index in 0 until length) {
            if (!this[thisOffset + index].equals(other[otherOffset + index], ignoreCase))
                return false
        }
        return true
    }

}