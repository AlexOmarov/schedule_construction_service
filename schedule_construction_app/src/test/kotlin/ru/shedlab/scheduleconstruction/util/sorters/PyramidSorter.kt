package ru.shedlab.scheduleconstruction.util.sorters

// O(n log n) - время
// O(n) - память
class PyramidSorter {

    //@Test
    fun `Pyramid sort`() {
        val array = intArrayOf(5,3,7,9,9,0,2)
        heapSort(array)
        array.forEach { print("$it, ") }
    }

    private fun heapify(array: IntArray, length: Int, i: Int) {
        val leftChild = 2 * i + 1
        val rightChild = 2 * i + 2
        var largest = i

        // если левый дочерний больше родительского
        if (leftChild < length && array[leftChild] > array[largest]) {
            largest = leftChild
        }

        // если правый дочерний больше родительского
        if (rightChild < length && array[rightChild] > array[largest]) {
            largest = rightChild
        }

        // если должна произойти замена
        if (largest != i) {
            val temp = array[i]
            array[i] = array[largest]
            array[largest] = temp
            heapify(array, length, largest)
        }
    }

    private fun heapSort(array: IntArray) {
        if (array.isEmpty()) return

        // Строим кучу
        val length = array.size
        // проходим от первого без ответвлений к корню
        for (i in length / 2 - 1 downTo 0) heapify(array, length, i)
        for (i in length - 1 downTo 0) {
            val temp = array[0]
            array[0] = array[i]
            array[i] = temp
            heapify(array, i, 0)
        }
    }
}