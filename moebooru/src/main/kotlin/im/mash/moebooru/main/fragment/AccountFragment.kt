package im.mash.moebooru.main.fragment

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AccountFragment : ToolbarFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_test, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val icon = view.findViewById<ImageView>(R.id.icon)
        icon.isDrawingCacheEnabled = true

//        try {
//            val draw = icon.drawable as BitmapDrawable
//            val bmp = draw.bitmap
//            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "launcher.png")
//            file.createNewFile()
//            val out = FileOutputStream(file)
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
//            out.flush()
//            out.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }
}