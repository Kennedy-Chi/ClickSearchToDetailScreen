package com.anunobi.clicksearchtodetailscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anunobi.clicksearchtodetailscreen.ui.theme.ClickSearchToDetailScreenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClickSearchToDetailScreenTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = { TopBar()},
                        backgroundColor = Color.Gray
                    ) {
                        Navigation()
                    }
                }
            }
        }
    }
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController =  navController as NavHostController, startDestination = "main"){
        composable("main"){
            MainScreen(navController = navController)
        }
        composable(
            "details/{name}",
            arguments = listOf(navArgument("name"){type = NavType.StringType})
        ){backStackEntry ->
            backStackEntry.arguments?.getString("name")?.let { name ->
                DetailScreen(name = name)
            }
        }
    }
}

@Composable
fun TopBar(){
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp)},
        backgroundColor = Color.Blue,
        contentColor = Color.White
    )
}

@Composable
fun SearchView(state: MutableState<TextFieldValue>){
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")){
                IconButton(
                    onClick = { state.value = TextFieldValue("") }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun NameListItem(names: String, onItemClick: (String) -> Unit){
    Row(
        modifier = Modifier
            .clickable(onClick = { onItemClick(names) })
            .background(colorResource(id = R.color.design_default_color_primary_dark))
            .height(57.dp)
            .fillMaxWidth()
            .padding(PaddingValues(8.dp, 16.dp))
    ) {
        Text(text = names, fontSize = 18.sp, color = Color.White)
    }
}

@Composable
fun NameList(navController: NavController, state: MutableState<TextFieldValue>){
    val names = getNames()
    var filteredNames: ArrayList<String>
    LazyColumn(modifier = Modifier.fillMaxWidth()){
        val searchedText = state.value.text
        filteredNames = if (searchedText.isEmpty()){
            names
        } else{
            val resultList = ArrayList<String>()
            for (name in names){
                if (name.lowercase().contains(searchedText.lowercase())){
                    resultList.add(name)
                }
            }
            resultList
        }
        items(filteredNames){filteredName ->
            NameListItem(names = filteredName, onItemClick = { selectedName ->
                navController.navigate("details/$selectedName") {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
                    popUpTo("main") {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
                }
            )
        }
    }
}

fun getNames(): ArrayList<String> {
    return arrayListOf(
        "Kennedy",
        "John",
        "Mathew",
        "Sampson",
        "Anita",
        "Bright",
        "Freeman",
        "Camela",
        "Peter"
    )
}

@Composable
fun MainScreen(navController: NavController){
    val textState = remember { mutableStateOf(TextFieldValue(""))}
    Column() {
        SearchView(textState)
        NameList(navController = navController, state = textState)
    }
}


@Composable
fun DetailScreen(name: String){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.design_default_color_primary_dark))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = name,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )
    }
}