package com.sl.biorhythms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AboutLinksTest {
    @Test
    fun userFacingAboutLinksStayOnProductWebsite() {
        assertEquals(
            "https://stanleyll0yd.github.io/apps/biorhythms/",
            AboutLinks.APP_WEBSITE_URL,
        )
        assertEquals(
            "https://stanleyll0yd.github.io/apps/biorhythms/privacy/",
            AboutLinks.PRIVACY_POLICY_URL,
        )
        assertFalse(
            listOf(
                AboutLinks.APP_WEBSITE_URL,
                AboutLinks.PRIVACY_POLICY_URL,
                AboutLinks.LICENSE_URL,
            ).any { it.contains("github.com", ignoreCase = true) },
        )
    }
}
