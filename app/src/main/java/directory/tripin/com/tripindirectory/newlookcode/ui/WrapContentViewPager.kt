package directory.tripin.com.tripindirectory.newlookcode.ui

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet


class WrapContentViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        initPageChangeListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPageChangeListener()
    }

    private fun initPageChangeListener() {
        addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                requestLayout()
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val child = getChildAt(currentItem)
        if (child != null) {
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}