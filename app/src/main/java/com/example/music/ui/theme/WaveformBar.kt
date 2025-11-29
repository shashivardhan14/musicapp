package com.example.music.ui.theme


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * A Composable function that displays a waveform bar, typically used for audio visualization.
 * It can show playback progress and allows seeking by tapping.
 *
 * @param values An IntArray representing the amplitude of the waveform at different points. Each value determines the height of a bar.
 * @param modifier The Modifier to be applied to this layout.
 * @param progress A float value between 0.0 and 1.0 indicating the current playback progress. Bars up to this progress will be highlighted.
 * @param onSeek A lambda function that is invoked when the user taps on the waveform to seek a new position. It provides the new progress as a float.
 */
@Composable
fun WaveFormBar(
    values: IntArray,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    onSeek: ((Float) -> Unit)? = null
) {
    // Define colors for the waveform bars.
    val color = Color.White
    val backgroundColor = Color.White.copy(0.2f)
    // Get the total number of bars to be displayed.
    val bars = values.size

    // Use a Row to lay out the individual bars horizontally.
    Row(
        modifier = modifier
            // Add a pointer input modifier to detect user taps for seeking.
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // If an onSeek callback is provided, calculate the seek position.
                    if (onSeek != null) {
                        // Calculate the tapped position as a percentage of the total width.
                        val percent = offset.x / size.width
                        // Invoke the callback with the calculated percentage, ensuring it's within the valid range [0, 1].
                        onSeek(percent.coerceIn(0f, 1f))
                    }
                }

            }
            // Set a fixed height for the waveform container.
            .height(200.dp)
            // Make the waveform take the full available width.
            .fillMaxWidth()
            // Add vertical padding.
            .padding(vertical = 12.dp),
        // Align the bars vertically to the center.
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Iterate through the `values` array to create a Box for each bar.
        values.forEachIndexed { index, v ->
            // Determine the color of the bar based on the current progress.
            // If the bar's index is before the progress point, use the highlighted color, otherwise use the background color.
            val barColor = if (index < (bars * progress).toInt()) color else backgroundColor
            // Each bar is a Box.
            Box(
                modifier = Modifier
                    // Add horizontal padding between bars.
                    .padding(horizontal = 1.dp)
                    // Each bar takes an equal share of the width.
                    .weight(1f)
                    // Set the height of the bar based on its value, with a minimum height.
                    .height((v.dp * 1.1f).coerceAtLeast(4.dp))
                    // Apply the determined color and a rounded corner shape to the bar.
                    .background(barColor, shape = RoundedCornerShape(4.dp))
            )

        }

    }
}











