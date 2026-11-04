package com.example.wecareto_project.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.wecareto_project.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WeCareDatabaseTest {
    private lateinit var database: WeCareDatabase
    @Before fun setUp() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext,
            WeCareDatabase::class.java).build()
    }
    @After fun close() = database.close()

    @Test fun cacheReplacementPreservesReferralsAndSupportsIdLookup() = runTest {
        val store = RoomResourceStore(database.resourceDao())
        store.upsert(ReferralResources.all())
        val resource = ReferralResources.all().first().copy(id = "api-row", source = "Toronto Open Data")
        store.replaceSource("Toronto Open Data", listOf(resource))
        assertEquals(resource, store.findById("api-row"))
        assertEquals(5, store.observeResources().first().size)
        store.replaceSource("Toronto Open Data", emptyList())
        assertNull(store.findById("api-row"))
        assertEquals(4, store.observeResources().first().size)
    }

    @Test fun sourceMismatchDoesNotDeleteExistingRows() = runTest {
        val dao = database.resourceDao()
        val original = ReferralResources.all().first().toEntity()
        dao.upsert(listOf(original))
        // Reject a mixed source before any saved rows are removed.
        try {
            dao.replaceSource(original.source, listOf(original.copy(source = "wrong-source")))
            fail("Mismatched sources must be rejected")
        } catch (_: IllegalArgumentException) { }
        assertEquals(original, dao.findById(original.id))
    }
    @Test fun failedInsertRollsBackTheSourceReplacement() = runTest {
        val dao = database.resourceDao()
        val original = ReferralResources.all().first().toEntity()
        dao.upsert(listOf(original))
        database.openHelper.writableDatabase.execSQL(
            "CREATE TRIGGER reject_test_insert BEFORE INSERT ON resources WHEN NEW.id = 'fail' BEGIN SELECT RAISE(ABORT, 'test failure'); END"
        )
        try {
            dao.replaceSource(original.source, listOf(original.copy(id = "fail")))
            fail("The trigger should reject the replacement")
        } catch (_: android.database.sqlite.SQLiteException) { }
        assertEquals(original, dao.findById(original.id))
    }

    @Test fun savedResourcesSurviveReopeningTheDatabase() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "persistence-test-${java.util.UUID.randomUUID()}.db"
        val resource = ReferralResources.all().first().toEntity()
        var diskDatabase = Room.databaseBuilder(context, WeCareDatabase::class.java, name).build()
        try {
            diskDatabase.resourceDao().upsert(listOf(resource))
            diskDatabase.close()
            diskDatabase = Room.databaseBuilder(context, WeCareDatabase::class.java, name).build()
            assertEquals(resource, diskDatabase.resourceDao().findById(resource.id))
        } finally {
            diskDatabase.close()
            context.deleteDatabase(name)
        }
    }

}
