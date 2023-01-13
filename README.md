# M3SearchDropdown

[![](https://jitpack.io/v/nbdy/M3SearchDropdown.svg)](https://jitpack.io/#nbdy/M3SearchDropdown)

M3 dropdown textfield which uses results provided by android's room database.

[![Preview](https://i.imgur.com/98Pugwi.gif)](https://imgur.com/98Pugwi)

A full example can be found [here](app/src/main/java/io/eberlein/m3searchdropdown/MainActivity.kt).

The implementation can be found [here](m3searchdropdown/src/main/java/io/eberlein/m3searchdropdown/SearchDropdown.kt).

A simplified example below:

```kotlin
@Entity(tableName = "MyCustomEntity")
data class MyCustomEntity(
    @PrimaryKey @ColumnInfo(name = "name") var name: String,
): ISearchableEntity {
    override fun getValue(): String = name
}

@HiltViewModel
class MyViewModel @Inject constructor(
    private val dao: MyCustomEntityDAO
) : ViewModel(), ISearchableViewModel<MyCustomEntity> {
    override fun search(name: String, cb: (List<MyCustomEntity>) -> Unit) {
        viewModelScope.launch { cb(dao.searchByName(name)) }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyAppTheme { MainView() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    model: ContactViewModel = viewModel()
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopStart
        ){
            SearchDropdown("MyCustomComponentsName", model)
        }
    }
}
```
