package io.eberlein.m3searchdropdown

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.eberlein.m3searchdropdown.ui.theme.M3SearchDropdownTheme
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Database
import androidx.room.Insert
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { M3SearchDropdownTheme { MainView() } }
    }
}

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey @ColumnInfo(name = "name") var name: String,
)

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :name || '%'")
    suspend fun findByName(name: String): List<ContactEntity>

    @Insert
    suspend fun add(contact: ContactEntity)

    @Query("SELECT * FROM contacts WHERE name = :name")
    suspend fun get(name: String): ContactEntity?
}

@Database(
    entities = [ContactEntity::class],
    version = 3,
    exportSchema = false
)
abstract class DB : RoomDatabase() {
    abstract fun contactDAO(): ContactDao
}

@Module
@InstallIn(SingletonComponent::class)
class ModelModule {
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext ctx: Context): DB = Room
        .databaseBuilder(ctx, DB::class.java, "m3searchdropdown")
        .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun providesContactDao(db: DB) = db.contactDAO()
}

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contacts: ContactDao
): ViewModel() {
    init {
        viewModelScope.launch {
            listOf("Andre", "Björn", "Caesar", "David", "Emma", "Fred").forEach {
                if(contacts.get(it) == null) { contacts.add(ContactEntity(it)) }
            }
        }
    }

    fun findByName(name: String, cb: (List<ContactEntity>) -> Unit) = viewModelScope.launch {
        cb(contacts.findByName(name))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    model: ContactViewModel = viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopStart
        ){
            SearchDropdown<ContactEntity>(
                stringResource(R.string.SearchDropdownLabel),
                searchFunction = { text, cb -> model.findByName(text, cb) },
                onItemSelected = { it.name },
            ) {
                Text(it.name)
            }
        }
    }
}
