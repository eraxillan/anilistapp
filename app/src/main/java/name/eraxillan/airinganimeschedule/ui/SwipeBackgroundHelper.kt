package name.eraxillan.airinganimeschedule.ui

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import name.eraxillan.airinganimeschedule.R
import kotlin.math.abs

/**
 * Sources:
 * https://github.com/Jachu5/recycler
 * https://github.com/Jachu5/recycler/blob/master/app/src/main/java/com/unchained/itemtouchexample/SwipeBackgroundHelper.kt
 *
 * Articles:
 * https://medium.com/@acerezoluna/part-3-recyclerview-from-zero-to-hero-397b7996280
 * https://medium.com/@acerezoluna/part-3b-recyclerview-from-zero-to-hero-ce4442ebeede
 */
class SwipeBackgroundHelper {
    companion object {
        private val LOG_TAG = "54BE6C87_SBH" // SBH = SwipeBackgroundHelper

        private const val THRESHOLD = 2.5
        private const val OFFSET_PX = 20
        private const val CIRCLE_ACCELERATION = 0.5
        private const val CIRCLE_THRESHOLD = 0.05

        private var circlePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        }

        @JvmStatic
        fun paintDrawCommandToStart(
            canvas: Canvas,
            viewItem: View,
            @DrawableRes iconResId: Int,
            dX: Float
        ) {
            val drawCommand = createDrawCommand(viewItem, dX, iconResId)
            paintDrawCommand(drawCommand, canvas, dX, viewItem)
        }

        private fun createDrawCommand(viewItem: View, dX: Float, iconResId: Int): DrawCommand {
            val context = viewItem.context
            var icon = ContextCompat.getDrawable(context, iconResId)!!
            icon = DrawableCompat.wrap(icon).mutate()
            icon.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, R.color.white),
                PorterDuff.Mode.SRC_IN
            )
            val backgroundColor = getBackgroundColor(R.color.red, R.color.grey, dX, viewItem)
            return DrawCommand(icon, backgroundColor)
        }

        private fun getBackgroundColor(
            firstColor: Int,
            secondColor: Int,
            dX: Float,
            viewItem: View
        ): Int {
            return when (willActionBeTriggered(dX, viewItem.width)) {
                true -> ContextCompat.getColor(viewItem.context, firstColor)
                false -> ContextCompat.getColor(viewItem.context, secondColor)
            }
        }

        private fun paintDrawCommand(
            drawCommand: DrawCommand,
            canvas: Canvas,
            dX: Float,
            viewItem: View
        ) {
            drawBackground(canvas, viewItem, dX, CIRCLE_THRESHOLD, drawCommand)
            drawIcon(canvas, viewItem, dX, drawCommand.icon)
        }

        private fun drawIcon(canvas: Canvas, viewItem: View, dX: Float, icon: Drawable) {
            val topMargin = calculateTopMargin(icon, viewItem)
            icon.bounds =
                getStartContainerRectangle(viewItem, icon.intrinsicWidth, topMargin, OFFSET_PX, dX)
            icon.draw(canvas)
        }

        private fun getStartContainerRectangle(
            viewItem: View, iconWidth: Int, topMargin: Int, sideOffset: Int,
            dx: Float
        ): Rect {
            val leftBound = viewItem.right + dx.toInt() + sideOffset
            val rightBound = viewItem.right + dx.toInt() + iconWidth + sideOffset
            val topBound = viewItem.top + topMargin
            val bottomBound = viewItem.bottom - topMargin

            return Rect(leftBound, topBound, rightBound, bottomBound)
        }

        private fun calculateTopMargin(icon: Drawable, viewItem: View): Int {
            return (viewItem.height - icon.intrinsicHeight) / 2
        }

        private fun drawBackground(canvas: Canvas, viewItem: View, dX: Float, threshold: Double, drawCommand: DrawCommand) {
            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint.color = drawCommand.backgroundColor

            val backgroundRectangle = getBackGroundRectangle(viewItem, dX)
            val circleRadius = (abs(dX / viewItem.width) - threshold) * viewItem.width * CIRCLE_ACCELERATION

            canvas.clipRect(backgroundRectangle)
            canvas.drawColor(backgroundPaint.color)

            if (circleRadius > 0f) {
                val cy = backgroundRectangle.top + viewItem.height / 2
                val cx = backgroundRectangle.left + drawCommand.icon.intrinsicWidth / 2 + OFFSET_PX

                canvas.drawCircle(cx, cy, circleRadius.toFloat(), circlePaint)
            }
        }

        private fun getBackGroundRectangle(viewItem: View, dX: Float): RectF {
            return RectF(
                viewItem.right.toFloat() + dX, viewItem.top.toFloat(),
                viewItem.right.toFloat(), viewItem.bottom.toFloat()
            )
        }

        private fun willActionBeTriggered(dX: Float, viewWidth: Int): Boolean {
            return abs(dX) >= viewWidth / THRESHOLD
        }
    }

    private class DrawCommand constructor(val icon: Drawable, val backgroundColor: Int)
}
