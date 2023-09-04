package pl.michaelskyf.openfaker.ui_module_bridge

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HookHandlerDataTest {

    @Test
    fun `WhichPackages(All) isMatching() should always return true`() {
        val whichPackages = HookHandlerData.WhichPackages.All()

        assertTrue(whichPackages.isMatching(""))
        assertTrue(whichPackages.isMatching("random text"))
    }

    @Test
    fun `WhichPackages(Some) isMatching() should return true only when packageName is contained inside`() {
        val whichPackages = HookHandlerData.WhichPackages.Some(arrayOf("matching1", "matching2"))

        assertTrue(whichPackages.isMatching("matching1"))
        assertTrue(whichPackages.isMatching("matching2"))

        assertFalse(whichPackages.isMatching(""))
        assertFalse(whichPackages.isMatching("random text"))
    }
}