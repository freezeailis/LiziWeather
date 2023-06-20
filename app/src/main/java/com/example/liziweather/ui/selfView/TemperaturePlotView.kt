package com.example.liziweather.ui.selfView

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetrics
import android.graphics.Paint.FontMetricsInt
import android.util.AttributeSet
import android.view.View
import com.example.liziweather.R
import kotlin.math.max


class TemperaturePlotView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int=0)
                            : View(context, attrs, defStyleAttr) {
    var maxTemperatures: IntArray? = null
    var minTemperatures: IntArray? = null

    private val mPaint = Paint()

    // 最高点和最低点之间的纵向距离
    private var verticalGapSize = 0f
    // 两个相邻点之间的横向距离
    private var horizontalGapSize = 80f
    // 点和文字之间的纵向距离
    private var pointTextGapSize = 30f

    private var lineColor = 0x404040
    private var textColor = 0x101010


    init {
        mPaint.setARGB(0xdf, 0x00, 0x00, 0x00)
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 5f
        mPaint.textSize = 17f
        mPaint.textAlign = Paint.Align.CENTER

        attrs?.run {
            View(context, attrs)
            // read attrs from xml file
            val a = context.obtainStyledAttributes(attrs, R.styleable.TemperaturePlotView)
            mPaint.textSize = a.getDimension(R.styleable.TemperaturePlotView_textSize, mPaint.textSize)
            lineColor = a.getColor(R.styleable.TemperaturePlotView_lineColor, lineColor)
            textColor = a.getColor(R.styleable.TemperaturePlotView_textColor, textColor)
            verticalGapSize = a.getDimension(R.styleable.TemperaturePlotView_verticalGapSize, verticalGapSize)
            horizontalGapSize = a.getDimension(R.styleable.TemperaturePlotView_horizontalGapSize, horizontalGapSize)
            pointTextGapSize = a.getDimension(R.styleable.TemperaturePlotView_pointTextGapSize, pointTextGapSize)
            a.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        setMeasuredDimension(
//            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
//            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
//        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // 最高点和最低点的间隔 + 两处文字和pointTextGapSize, 此外上下各额外保留2个 pointTextGapSize 的距离
        val actuallyHeightSize = (verticalGapSize + 2 * (pointTextGapSize * 2 + mPaint.textSize)).toInt()
        // 点的间隔 * (点的个数-1), 此外两侧各额外保留半个 点的间隔 的距离
        val actuallyWidthSize = (horizontalGapSize * ((maxTemperatures?.size ?: 0))).toInt()

        if (layoutParams.width == LayoutParams.WRAP_CONTENT && layoutParams.height == LayoutParams.WRAP_CONTENT){
            setMeasuredDimension(actuallyWidthSize, actuallyHeightSize)
        } else if (layoutParams.width == LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(actuallyWidthSize, widthSize)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        maxTemperatures?.let { maxTemperatures->
            minTemperatures?.let { minTemperatures->
                val startTemperature =maxTemperatures.max()
                val distPerTemperature = verticalGapSize / (startTemperature - minTemperatures.min())
                val xPointStart = horizontalGapSize / 2
                val yPointStart = 2 * pointTextGapSize + mPaint.textSize

                // 绘制点, 文字(温度数值), 点之间的折线
                for (idx in maxTemperatures.indices){

                    canvas?.apply {
                        val pointX = xPointStart + idx * horizontalGapSize
                        val highPointY = yPointStart + distPerTemperature * (startTemperature - maxTemperatures[idx])
                        val lowPointY = yPointStart + distPerTemperature * (startTemperature - minTemperatures[idx])

                        mPaint.color = textColor
                        mPaint.style = Paint.Style.FILL_AND_STROKE
                        mPaint.strokeWidth = 1.2f
                        // 绘制温度点对应的文字
                        drawText(maxTemperatures[idx].toString() + "°",
                            pointX+5f, highPointY - pointTextGapSize - mPaint.fontMetrics.bottom, mPaint)
                        drawText(minTemperatures[idx].toString() + "°",
                            pointX+5f, lowPointY + pointTextGapSize - mPaint.fontMetrics.top, mPaint)

                        // 绘制点之间的折线
                        mPaint.color = lineColor
                        mPaint.style = Paint.Style.FILL_AND_STROKE
                        mPaint.strokeWidth = 5.0f

                        if (idx > 0){
                            val perPointX = xPointStart + (idx-1) * horizontalGapSize
                            val perHighPointY = yPointStart + distPerTemperature * (startTemperature - maxTemperatures[idx-1])
                            val perLowPointY = yPointStart + distPerTemperature * (startTemperature - minTemperatures[idx-1])
                            drawLine(perPointX, perHighPointY, pointX, highPointY, mPaint)
                            drawLine(perPointX, perLowPointY, pointX, lowPointY, mPaint)
                        }

                        // 绘制每日最高/最低温度点
                        mPaint.style = Paint.Style.STROKE
                        drawCircle(pointX, highPointY, 8f, mPaint)
                        drawCircle(pointX, lowPointY, 8f, mPaint)
                    }

                }
            }
        }


    }


    private fun drawLine(pointSet: IntArray){

    }
}
