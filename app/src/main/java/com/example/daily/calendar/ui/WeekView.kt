package com.example.daily.calendar.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.daily.R
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.WeekView

class WeekView(context: Context): WeekView(context) {
    /**文字画笔*/
    private var mTextPaint = Paint()

    /**农历文字画笔*/
    private var mLunarTextPaint = Paint()

    /**24节气,节日画笔*/
    private var mSolarTermTextPaint = Paint()

    /**圆点半径*/
    private var mRadius = 0f

    /**背景圆半径*/
    private var mCircleRadius = 0f

    /**圆点画笔*/
    private var mPointPaint = Paint()

    /**1dp*/
    private var mPadding = 0f

    private fun initPaint(context: Context) {
        mTextPaint.textSize = dpToPx(context, 14f)
        mTextPaint.color = context.getColor(R.color.text_date)
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.isAntiAlias = true
        mTextPaint.isFakeBoldText = true

        mLunarTextPaint.textSize = dpToPx(context, 10f)
        mLunarTextPaint.color = context.getColor(R.color.text_lunar)
        mLunarTextPaint.textAlign = Paint.Align.CENTER
        mLunarTextPaint.isAntiAlias = true

        mSelectTextPaint.textSize = mTextPaint.textSize
        mSelectTextPaint.color = Color.WHITE
        mSelectTextPaint.isAntiAlias = true
        mSelectTextPaint.textAlign = Paint.Align.CENTER
        mSelectTextPaint.isFakeBoldText = true

        mSelectedLunarTextPaint.textSize = mLunarTextPaint.textSize
        mSelectedLunarTextPaint.color = Color.WHITE
        mSelectedLunarTextPaint.textAlign = Paint.Align.CENTER
        mSelectedLunarTextPaint.isAntiAlias = true

        mSelectedPaint.color = context.getColor(R.color.scheme_primary)
        mSelectedPaint.isAntiAlias = true
        mSelectedPaint.style = Paint.Style.FILL

        mSolarTermTextPaint.color = context.getColor(R.color.scheme_primary)
        mSolarTermTextPaint.isAntiAlias = true
        mSolarTermTextPaint.textAlign = Paint.Align.CENTER
        mSolarTermTextPaint.textSize = mLunarTextPaint.textSize

        mSchemeTextPaint.isAntiAlias = true
        mSchemeTextPaint.textAlign = Paint.Align.CENTER
        mSchemeTextPaint.textSize = dpToPx(context, 9f)

        mSchemePaint.color = Color.WHITE
        mSchemePaint.isAntiAlias = true

        mRadius = dpToPx(context, 2f)

        mPadding = dpToPx(context, 1f)
    }

    override fun onPreviewHook() {
        super.onPreviewHook()
        initPaint(context)
        mCircleRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5.toFloat()
    }

    override fun onDrawSelected(canvas: Canvas?, calendar: Calendar?, x: Int, hasScheme: Boolean): Boolean {
        val cx = x + mItemWidth / 2
        val cy = mItemHeight / 2
        if (isTouchDown && mCurrentItem == mItems.indexOf(index)) {
            //点击当前选中的item, 缩放效果提示
            canvas!!.drawCircle(cx.toFloat(), cy.toFloat(), mCircleRadius - 3*mPadding, mSelectedPaint)
        } else {
            canvas!!.drawCircle(cx.toFloat(), cy.toFloat(), mCircleRadius, mSelectedPaint)
        }
        return true
    }

    override fun onDrawScheme(canvas: Canvas?, calendar: Calendar?, x: Int) {

        val isSelected = isSelected(calendar)
        if (isSelected) {
            mPointPaint.color = Color.WHITE
        } else {
            mPointPaint.color = Color.GRAY
        }

        if (calendar!!.scheme == "scheme") {
            canvas!!.drawCircle(
                (x + mItemWidth / 2).toFloat(),
                mItemHeight - 8 * mPadding,
                mRadius,
                mPointPaint
            )
        }
    }

    override fun onDrawText(canvas: Canvas?, calendar: Calendar?, x: Int, hasScheme: Boolean, isSelected: Boolean) {
        val cx = x + mItemWidth / 2
        val cy = mItemHeight / 2
        val metric = mSelectTextPaint.fontMetrics

        /*if (hasScheme && calendar!!.scheme == "休") {
            mSchemePaint.color = Color.WHITE
            canvas!!.drawCircle(
                cx + mSelectTextPaint.measureText(calendar!!.day.toString())/2 + 8*mPadding,
                cy + metric.top - mPadding*2,
                mSchemeTextPaint.textSize,
                mSchemePaint
            )
            mSchemeTextPaint.color = calendar.schemeColor
            canvas.drawText(
                calendar.scheme,
                cx + mSelectTextPaint.measureText(calendar.day.toString())/2 + 8*mPadding,
                cy + metric.top,
                mSchemeTextPaint
            )
        }*/

        if (calendar!!.isCurrentMonth) {
            if (isSelected) {
                mTextPaint.color = Color.WHITE
                mLunarTextPaint.color = Color.WHITE
                mSolarTermTextPaint.color = Color.WHITE
            } else {
                mSolarTermTextPaint.color = context.getColor(R.color.scheme_primary)
                if (calendar.isWeekend) {
                    mTextPaint.color = context.getColor(R.color.text_lunar)
                    mLunarTextPaint.color = context.getColor(R.color.text_lunar)
                } else if (calendar.isCurrentDay) {
                    mTextPaint.color = context.getColor(R.color.scheme_primary)
                    mLunarTextPaint.color = context.getColor(R.color.scheme_primary)
                } else {
                    mTextPaint.color = context.getColor(R.color.text_date)
                    mLunarTextPaint.color = context.getColor(R.color.text_lunar)
                }
            }
        } else {
            mSolarTermTextPaint.color = context.getColor(R.color.scheme_primary)
            mTextPaint.color = context.getColor(R.color.text_lunar)
            mLunarTextPaint.color = context.getColor(R.color.text_lunar)
        }

        canvas!!.drawText(
            calendar.day.toString(),
            cx.toFloat(),
            mTextBaseLine - 4*mPadding,
            mTextPaint
        )

        canvas.drawText(
            calendar.lunar,
            cx.toFloat(),
            mTextBaseLine + mItemHeight / 10,
            if (calendar.solarTerm.isNotEmpty() || calendar.gregorianFestival.isNotEmpty() || calendar.traditionFestival.isNotEmpty()) {
                mSolarTermTextPaint
            } else {
                mLunarTextPaint
            }
        )
    }

    /**
     * dp转px
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private fun dpToPx(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }
}