package com.example.campuslostfound

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class DashboardChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val barPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var values =
        intArrayOf()

    private var labels =
        arrayOf<String>()

    private var colors =
        intArrayOf()

    init {

        barPaint.style =
            Paint.Style.FILL

        textPaint.typeface =
            Typeface.DEFAULT_BOLD

        textPaint.textAlign =
            Paint.Align.CENTER
    }

    fun setData(
        values: IntArray,
        labels: Array<String>,
        colors: IntArray
    ) {

        this.values = values
        this.labels = labels
        this.colors = colors

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        if (
            values.isEmpty()
        ) {
            return
        }

        val chartWidth =
            width.toFloat()

        val chartHeight =
            height.toFloat()

        val chartBottom =
            chartHeight - 50f

        val chartTop =
            35f

        val maxValue =
            max(
                values.maxOrNull() ?: 0,
                1
            )

        val count =
            values.size

        val sectionWidth =
            chartWidth / count

        val barWidth =
            minOf(
                sectionWidth * 0.45f,
                70f
            )

        for (i in values.indices) {

            val value =
                values[i]

            val centerX =
                sectionWidth * i +
                        sectionWidth / 2f

            val availableHeight =
                chartBottom - chartTop

            val barHeight =
                if (maxValue > 0) {
                    (value.toFloat() /
                            maxValue) *
                            availableHeight
                } else {
                    0f
                }

            val left =
                centerX - barWidth / 2f

            val right =
                centerX + barWidth / 2f

            val top =
                chartBottom - barHeight

            // BAR

            barPaint.color =
                if (i < colors.size) {
                    colors[i]
                } else {
                    0xFF6845B5.toInt()
                }

            canvas.drawRoundRect(
                left,
                top,
                right,
                chartBottom,
                12f,
                12f,
                barPaint
            )

            // NUMBER

            textPaint.color =
                0xFF172B4D.toInt()

            textPaint.textSize =
                15f

            canvas.drawText(
                value.toString(),
                centerX,
                top - 8f,
                textPaint
            )

            // LABEL

            textPaint.color =
                0xFF555555.toInt()

            textPaint.textSize =
                11f

            val label =
                if (i < labels.size) {
                    labels[i]
                } else {
                    ""
                }

            canvas.drawText(
                label,
                centerX,
                chartBottom + 28f,
                textPaint
            )
        }
    }
}