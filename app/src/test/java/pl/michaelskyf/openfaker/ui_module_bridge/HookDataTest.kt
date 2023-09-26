package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HookDataTest {

    @Test
    fun `WhichPackages(All) isMatching() should always return true`() {
        val whichPackages = HookData.WhichPackages.All

        assertTrue(whichPackages.isMatching(""))
        assertTrue(whichPackages.isMatching("random text"))
    }

    @Test
    fun `WhichPackages(Some) isMatching() should return true only when packageName is contained inside`() {
        val whichPackages = HookData.WhichPackages.Some(hashSetOf("matching1", "matching2"))

        assertTrue(whichPackages.isMatching("matching1"))
        assertTrue(whichPackages.isMatching("matching2"))

        assertFalse(whichPackages.isMatching(""))
        assertFalse(whichPackages.isMatching("random text"))
    }

    @Test
    fun `HookData before and after serialization must be equal`() {
        val before = HookData(HookData.WhichPackages.All, arrayOf("First", "Second"), TestFakerModuleFactory(), HookData.WhenToHook.Before)

        val json = Json.encodeToString(before)
        val after = Json.decodeFromString<HookData>(json)

        assertEquals(before, after)
    }
}