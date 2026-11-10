package org.firstinspires.ftc.teamcode.util

import com.pedropathing.math.Pose
import com.pedropathing.math.Vector
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.PI

class UtilTest {

    @Test
    fun testWrap360Double() {
        assertEquals(0.0, Util.wrap360(0.0), 1e-5)
        assertEquals(90.0, Util.wrap360(90.0), 1e-5)
        assertEquals(270.0, Util.wrap360(-90.0), 1e-5)
        assertEquals(45.0, Util.wrap360(405.0), 1e-5)
        assertEquals(315.0, Util.wrap360(-405.0), 1e-5)
    }

    @Test
    fun testWrap360Int() {
        assertEquals(0, Util.wrap360(0))
        assertEquals(90, Util.wrap360(90))
        assertEquals(270, Util.wrap360(-90))
        assertEquals(45, Util.wrap360(405))
        assertEquals(315, Util.wrap360(-405))
    }

    @Test
    fun testWrap2Pi() {
        assertEquals(0.0, Util.wrap2Pi(0.0), 1e-5)
        assertEquals(PI / 2, Util.wrap2Pi(PI / 2), 1e-5)
        assertEquals(-PI / 2, Util.wrap2Pi(-PI / 2), 1e-5)
        assertEquals(0.0, Util.wrap2Pi(2 * PI), 1e-5)
    }

    @Test
    fun testRoughlyEquals() {
        val p1 = Pose(0.0, 0.0, 0.0)
        val p2 = Pose(0.5, 0.0, 0.0)
        val p3 = Pose(2.0, 0.0, 0.0)

        assertTrue(Util.roughlyEquals(p1, p2, 1.0))
        assertFalse(Util.roughlyEquals(p1, p3, 1.0))
    }

    @Test
    fun testVectorAngle() {
        val v = Vector(1.0, 1.0)
        assertEquals(PI / 4, Util.vectorAngle(v), 1e-5)
    }
}
