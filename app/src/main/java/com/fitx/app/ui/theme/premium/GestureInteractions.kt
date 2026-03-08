package com.fitx.app.ui.theme.premium

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * ADVANCED GESTURE-BASED 3D INTERACTIONS
 * Revolutionary touch interactions that no competitor has
 */

/**
 * Tilt-responsive 3D card that responds to device orientation
 * Creates parallax effect based on touch position
 */
@Composable
fun TiltResponsive3DCard(
    modifier: Modifier = Modifier,
    maxTilt: Float = 15f,
    depth: Float = 20f,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    val rotationX by animateFloatAsState(
        targetValue = offsetY * maxTilt,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationX"
    )
    
    val rotationY by animateFloatAsState(
        targetValue = offsetX * maxTilt,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationY"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX = (dragAmount.x / size.width).coerceIn(-1f, 1f)
                    offsetY = (-dragAmount.y / size.height).coerceIn(-1f, 1f)
                }
            }
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                this.cameraDistance = 12f * density
                this.shadowElevation = depth
            }
    ) {
        content()
    }
}

/**
 * Pinch-to-zoom 3D card with rotation
 */
@Composable
fun PinchZoom3DCard(
    modifier: Modifier = Modifier,
    minScale: Float = 0.5f,
    maxScale: Float = 3f,
    content: @Composable () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotate ->
                    scale = (scale * zoom).coerceIn(minScale, maxScale)
                    rotation += rotate
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                rotationZ = animatedRotation
                translationX = offsetX
                translationY = offsetY
            }
    ) {
        content()
    }
}

/**
 * Swipe-to-reveal 3D card with depth layers
 */
@Composable
fun SwipeReveal3DCard(
    modifier: Modifier = Modifier,
    revealThreshold: Float = 200f,
    onReveal: () -> Unit = {},
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isRevealed by remember { mutableStateOf(false) }
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isRevealed) revealThreshold else offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "offsetX"
    )
    
    val rotationY by animateFloatAsState(
        targetValue = if (isRevealed) 180f else (offsetX / revealThreshold * 180f).coerceIn(0f, 180f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotationY"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > revealThreshold / 2) {
                            isRevealed = !isRevealed
                            onReveal()
                        }
                        offsetX = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount).coerceIn(-revealThreshold, revealThreshold)
                }
            }
            .graphicsLayer {
                this.rotationY = rotationY
                this.cameraDistance = 12f * density
            }
    ) {
        if (rotationY < 90f) {
            frontContent()
        } else {
            Box(
                modifier = Modifier.graphicsLayer {
                    this.rotationY = 180f
                }
            ) {
                backContent()
            }
        }
    }
}

/**
 * Magnetic snap 3D card that snaps to grid positions
 */
@Composable
fun MagneticSnap3DCard(
    modifier: Modifier = Modifier,
    snapPoints: List<Offset> = listOf(Offset.Zero),
    magneticRadius: Float = 100f,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var targetOffset by remember { mutableStateOf(Offset.Zero) }
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = targetOffset.x,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "offsetX"
    )
    
    val animatedOffsetY by animateFloatAsState(
        targetValue = targetOffset.y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "offsetY"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Find nearest snap point
                        val currentOffset = Offset(offsetX, offsetY)
                        val nearestPoint = snapPoints.minByOrNull { point ->
                            sqrt(
                                (point.x - currentOffset.x).pow(2) +
                                (point.y - currentOffset.y).pow(2)
                            )
                        } ?: Offset.Zero
                        
                        val distance = sqrt(
                            (nearestPoint.x - currentOffset.x).pow(2) +
                            (nearestPoint.y - currentOffset.y).pow(2)
                        )
                        
                        targetOffset = if (distance < magneticRadius) nearestPoint else Offset.Zero
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .offset { IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt()) }
            .graphicsLayer {
                val scale = 1f + (abs(animatedOffsetX) + abs(animatedOffsetY)) / 1000f
                scaleX = scale.coerceIn(0.8f, 1.2f)
                scaleY = scale.coerceIn(0.8f, 1.2f)
            }
    ) {
        content()
    }
}

/**
 * Velocity-based fling 3D card with physics
 */
@Composable
fun VelocityFling3DCard(
    modifier: Modifier = Modifier,
    friction: Float = 0.95f,
    onFling: (velocity: Offset) -> Unit = {},
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }
    
    LaunchedEffect(velocityX, velocityY) {
        while (abs(velocityX) > 0.1f || abs(velocityY) > 0.1f) {
            offsetX += velocityX
            offsetY += velocityY
            velocityX *= friction
            velocityY *= friction
            kotlinx.coroutines.delay(16) // ~60 FPS
        }
    }
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onFling(Offset(velocityX, velocityY))
                    }
                ) { change, dragAmount ->
                    change.consume()
                    velocityX = dragAmount.x
                    velocityY = dragAmount.y
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer {
                val rotation = atan2(velocityY, velocityX) * (180f / PI.toFloat())
                rotationZ = rotation * 0.1f
            }
    ) {
        content()
    }
}

/**
 * Double-tap zoom 3D card
 */
@Composable
fun DoubleTapZoom3DCard(
    modifier: Modifier = Modifier,
    normalScale: Float = 1f,
    zoomedScale: Float = 2f,
    content: @Composable () -> Unit
) {
    var isZoomed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isZoomed) zoomedScale else normalScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        isZoomed = !isZoomed
                    }
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        content()
    }
}

/**
 * Long-press 3D depth card
 */
@Composable
fun LongPress3DDepthCard(
    modifier: Modifier = Modifier,
    normalDepth: Float = 10f,
    pressedDepth: Float = 30f,
    onLongPress: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val depth by animateFloatAsState(
        targetValue = if (isPressed) pressedDepth else normalDepth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "depth"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isPressed = true
                        onLongPress()
                        scope.launch {
                            delay(200)
                            isPressed = false
                        }
                    }
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = depth
            }
    ) {
        content()
    }
}

/**
 * Rotation gesture 3D card
 */
@Composable
fun RotationGesture3DCard(
    modifier: Modifier = Modifier,
    onRotate: (Float) -> Unit = {},
    content: @Composable () -> Unit
) {
    var rotation by remember { mutableStateOf(0f) }
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, _, _, rotate ->
                    rotation += rotate
                    onRotate(rotation)
                }
            }
            .graphicsLayer {
                rotationZ = animatedRotation
                cameraDistance = 12f * density
            }
    ) {
        content()
    }
}

/**
 * Multi-touch 3D manipulation card
 * Supports simultaneous scale, rotation, and translation
 */
@Composable
fun MultiTouch3DCard(
    modifier: Modifier = Modifier,
    onTransform: (scale: Float, rotation: Float, offset: Offset) -> Unit = { _, _, _ -> },
    content: @Composable () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotate ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    rotation += rotate
                    offsetX += pan.x
                    offsetY += pan.y
                    onTransform(scale, rotation, Offset(offsetX, offsetY))
                }
            }
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                rotationZ = animatedRotation
                translationX = offsetX
                translationY = offsetY
                cameraDistance = 12f * density
            }
    ) {
        content()
    }
}
