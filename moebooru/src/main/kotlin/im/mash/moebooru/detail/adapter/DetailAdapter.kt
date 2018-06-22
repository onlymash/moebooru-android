package im.mash.moebooru.detail.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import im.mash.moebooru.detail.fragment.InfoFragment
import im.mash.moebooru.detail.fragment.PagerFragment
import im.mash.moebooru.detail.fragment.TagFragment

class DetailAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var fragmentTags: SparseArray<String> = SparseArray()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InfoFragment()
            1 -> PagerFragment()
            else -> TagFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
        val fragment = super.instantiateItem(container, position) as Fragment
        val fragmentTag = fragment.tag
        fragmentTags.put(position, fragmentTag)
        return fragment
    }

    fun notifyFragmentByPosition(position: Int) {
        fragmentTags.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        val fragment = `object` as Fragment
        if (fragmentTags.indexOfValue(fragment.tag) > -1) {
            return super.getItemPosition(`object`)
        }
        return PagerAdapter.POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragmentTags.removeAt(position)
    }
}