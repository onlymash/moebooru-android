package im.mash.moebooru.download

import android.os.Build
import android.widget.ProgressBar

object ProgressUtil {

    @JvmOverloads
    fun updateProgressToViewWithMark(bar: ProgressBar, currentOffset: Long,
                                     anim: Boolean = true) {
        if (bar.tag == null) return

        val shrinkRate = bar.tag as Int
        val progress = (currentOffset / shrinkRate).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bar.setProgress(progress, anim)
        } else {
            bar.progress = progress
        }
    }

    @JvmOverloads
    fun calcProgressToViewAndMark(bar: ProgressBar, offset: Long, total: Long,
                                  anim: Boolean = true) {
        val contentLengthOnInt = reducePrecision(total)
        val shrinkRate = if (contentLengthOnInt == 0)
            1
        else
            (total / contentLengthOnInt).toInt()
        bar.tag = shrinkRate
        val progress = (offset / shrinkRate).toInt()


        bar.max = contentLengthOnInt
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bar.setProgress(progress, anim)
        } else {
            bar.progress = progress
        }
    }

    private fun reducePrecision(origin: Long): Int {
        if (origin <= Integer.MAX_VALUE) return origin.toInt()

        var shrinkRate = 10
        var result = origin
        while (result > Integer.MAX_VALUE) {
            result /= shrinkRate.toLong()
            shrinkRate *= 5
        }

        return result.toInt()
    }
}