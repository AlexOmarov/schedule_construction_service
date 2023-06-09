package ru.shedlab.scheduleconstruction.util.sorters

import org.junit.jupiter.api.Test

// O(n log n) - время (среднее)
// O(n) - память
class QuickSorter {

    @Test
    fun `Quick sort`() {
        val array = intArrayOf(5, 3, 7, 9, 9, 0, 2, 2, 7, 9, 5, 3, 7)
        sort(array, 0, array.size - 1)
        array.forEach { print("$it, ") }
    }

    private fun sort(array: IntArray, left: Int, right: Int) {
        var leftBorder = left
        var rightBorder = right
        val pivot = array[(left + right) / 2]

        do {
            while (array[leftBorder] < pivot) {
                leftBorder++
            }

            while (array[rightBorder] > pivot) {
                rightBorder--
            }

            // Проверим, не нужно обменять местами элементы, на которые указывают маркеры
            if (leftBorder <= rightBorder) {
                // Левый маркер будет меньше правого только если мы должны выполнить swap
                if (leftBorder < rightBorder) {
                    val tmp: Int = array[leftBorder]
                    array[leftBorder] = array[rightBorder]
                    array[rightBorder] = tmp
                }
                leftBorder++
                rightBorder--
            }
        } while (leftBorder <= rightBorder)

        // Выполняем рекурсивно для частей
        if (leftBorder < right) {
            sort(array, leftBorder, right)
        }
        if (left < rightBorder) {
            sort(array, left, rightBorder)
        }
    }
}
