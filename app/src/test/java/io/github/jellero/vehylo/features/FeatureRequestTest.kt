package io.github.jellero.vehylo.features

import org.junit.Test

class FeatureRequestTest {
    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyTitle() {
        FeatureRequest(title = "", description = "Descrizione")
    }
}
