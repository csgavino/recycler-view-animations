package com.example.sandbox2

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorsControllerTest {

    class FakeColorsView : ColorsView {
        var position: Int = -1
        var color: Long = -1

        override fun onLayoutManagerSelected(layoutManager: RecyclerView.LayoutManager) {
            // no-op
        }

        override fun onItemAnimatorSelected(itemAnimator: SimpleItemAnimator) {
            // no-op
        }

        override fun addItemAt(position: Int, color: Long) {
            this.position = position
            this.color = color
        }

        override fun changeItemAt(position: Int, color: Long) {
            this.position = position
            this.color = color
        }

        override fun deleteItemAt(position: Int) {
            this.position = position
        }

    }

    class FakeColorsGenerator(val color: Long) : ColorsGenerator {
        override fun generate(): Long {
            return color
        }
    }

    var fakeColorsView: FakeColorsView? = null
    var fakeColorsGenerator: FakeColorsGenerator? = null

    @Before
    fun setUp() {
        fakeColorsView = FakeColorsView()
        fakeColorsGenerator = FakeColorsGenerator(Color.BLACK.toLong())
    }

    @After
    fun tearDown() {
        fakeColorsView = null
        fakeColorsGenerator = null
    }

    @Test
    fun testClickingOnItemsAddsItemsUnderneathWhenAddSelected() {
        val controller: ColorsControllerImpl = ColorsControllerImpl(fakeColorsView!!, fakeColorsGenerator!!)
        val currentPosition = 1

        controller.onClick(currentPosition, R.id.add_btn)

        assertEquals(currentPosition + 1, fakeColorsView!!.position)
        assertEquals(Color.BLACK.toLong(), fakeColorsView!!.color)
    }

    @Test
    fun testClickingOnItemsChangesItWhenChangeSelected() {
        val controller: ColorsControllerImpl = ColorsControllerImpl(fakeColorsView!!, fakeColorsGenerator!!)
        val changeThis = 1

        controller.onClick(changeThis, R.id.change_btn)

        assertEquals(changeThis, fakeColorsView!!.position)
        assertEquals(Color.BLACK.toLong(), fakeColorsView!!.color)
    }


    @Test
    fun testClickingOnItemsDeletesWhenDeleteSelected() {
        val controller: ColorsControllerImpl = ColorsControllerImpl(fakeColorsView!!, fakeColorsGenerator!!)
        val deleteThis = 1

        controller.onClick(deleteThis, R.id.delete_btn)

        assertEquals(deleteThis, fakeColorsView!!.position)
    }
}