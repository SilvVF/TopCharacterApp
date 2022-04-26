package com.example.topcharacterlist.screens

import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.topcharacterlist.data.remote.responses.Data
import com.example.topcharacterlist.screens.top_chracter_screen.TopCharacterViewModel
import com.google.accompanist.coil.CoilImage

@Composable
fun TopCharacterScreen( // screen that is displayed contains the Entries and search bar
    navController: NavController,
    viewModel: TopCharacterViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colors.background, //sets full screens bg color
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Row() {
                Text(
                    text = "Anime",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Text(
                    text = "Characters",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp
                )
            }
            SearchBar( //custom SearchBar / TextField
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                //implement search
            }
            Spacer(modifier = Modifier.height(16.dp))
            CharacterList(navController = navController) // character items full lazy column
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }
        )
        if(isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun CharacterList( // contains all of the pokemon Cards
    navController: NavController,
    viewModel: TopCharacterViewModel = hiltViewModel() //reference to viewModel
) {
    val characterList by remember { viewModel.characterList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    //val isSearching by remember { viewModel.is }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        val itemCount = if(characterList.size % 2 == 0) { //edge case of item having 1 char
            characterList.size / 2
        } else {
            characterList.size / 2 + 1
        }
        items(itemCount) { //check is we are not still waiting on prev response before
            //calling more -> the page wouldn't be updated yet when called again
            if(it >= itemCount - 1 && !endReached && !isLoading) {
                LaunchedEffect(key1 = true){
                    viewModel.loadTopCharacterList()
                }
            }
            CharacterRow(rowIndex = it, entries = characterList, navController = navController)
        }
    }

    Box( //box containing loading circle while waiting for network response
        //also displays the retry on error button if no responses can be displayed
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if(loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadTopCharacterList() //makes the network call again
            }
        }
    }

}

@Composable
fun CharacterEntry( // contains singular items
    entry: Data,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel:TopCharacterViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
//            .clickable {
//                navController.navigate(
//                    //navv arg TODO
//                )
//            }
    ) {
        Column {
            AsyncImage(  //gets dominant color from fun defined in the view model
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.images.jpg.image_url)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                contentDescription = null
            )
            Text(
                text = entry.name,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CharacterRow( // positions the items next to each other
    rowIndex: Int,
    entries: List<Data>,
    navController: NavController
) {
    Column {
        Row {
            CharacterEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowIndex * 2 + 2) {
                CharacterEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RetrySection( //option to retry loading when an error occurs
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}