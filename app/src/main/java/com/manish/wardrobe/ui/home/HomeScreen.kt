package com.manish.wardrobe.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.manish.wardrobe.data.local.Clothing
import com.manish.wardrobe.viewmodel.ClothingViewModel
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ClothingViewModel,
    onAddClick: () -> Unit
) {

    val clothingList by viewModel.clothingList.collectAsState()

    val categories = listOf("All") + clothingList
        .map { it.category }
        .distinct()
        .sorted()

    var selectedCategory by remember { mutableStateOf("All") }


    val filteredClothes = clothingList.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }

    val readyClothes = filteredClothes.filter {
        it.wearCount < it.maxWear
    }

    val needsWash = filteredClothes.filter {
        it.wearCount >= it.maxWear
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wardrobe") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->

        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {

                    items(categories) { category ->

                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                            },
                            label = {
                                Text(category)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )

                    }
                }
            }

            if (readyClothes.isNotEmpty()) {
                item { SectionTitle("Ready to Wear") }

                items(readyClothes) { clothing ->
                    ClothingCard(
                        clothing = clothing,
                        onIncrease = { viewModel.increaseWear(clothing) },
                        onReset = { viewModel.resetWear(clothing) },
                        onDelete = { viewModel.deleteClothing(clothing) }
                    )
                }
            }

            if (needsWash.isNotEmpty()) {
                item { SectionTitle("Needs Wash") }

                items(needsWash) { clothing ->
                    ClothingCard(
                        clothing = clothing,
                        onIncrease = { viewModel.increaseWear(clothing) },
                        onReset = { viewModel.resetWear(clothing) },
                        onDelete = { viewModel.deleteClothing(clothing) }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun ClothingCard(
    clothing: Clothing,
    onIncrease: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit
) {

    val status = when {
        clothing.wearCount < clothing.maxWear - 1 -> "READY"
        clothing.wearCount == clothing.maxWear - 1 -> "ALMOST"
        else -> "WASH"
    }

    val animatedColor by animateColorAsState(
        targetValue = when (status) {
            "READY" -> MaterialTheme.colorScheme.primaryContainer
            "ALMOST" -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.errorContainer
        },
        label = "CardColorAnimation"
    )

    val elevation = when (status) {
        "READY" -> 8.dp
        "ALMOST" -> 4.dp
        else -> 1.dp
    }

    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    },
                    onDragEnd = {

                        when {
                            offsetX > 300 -> {
                                onIncrease()
                            }
                            offsetX < -300 -> {
                                onDelete()
                            }
                        }

                        offsetX = 0f
                    }
                )
            }
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = animatedColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .alpha(if (status == "WASH") 0.8f else 1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                clothing.imageUri?.let { uriString ->
                    Image(
                        painter = rememberAsyncImagePainter(uriString),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = clothing.name,
                    style = MaterialTheme.typography.titleMedium
                )

                AssistChip(
                    onClick = {},
                    label = { Text(clothing.category) }
                )

                Text(text = "Wear: ${clothing.wearCount} / ${clothing.maxWear}")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onReset) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}
