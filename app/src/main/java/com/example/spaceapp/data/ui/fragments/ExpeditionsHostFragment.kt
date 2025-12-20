package com.example.spaceapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spaceapp.databinding.FragmentExpeditionsHostBinding
import com.google.android.material.tabs.TabLayoutMediator

class ExpeditionsHostFragment : Fragment() {

    private var _b: FragmentExpeditionsHostBinding? = null
    private val b get() = _b!!

    private val planetId by lazy { requireArguments().getLong("planetId") }
    private val planetName by lazy { requireArguments().getString("planetName").orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = "Планета: $planetName"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentExpeditionsHostBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment {
                return if (position == 0) {
                    ExpeditionsListFragment.newInstance(planetId)
                } else {
                    ExpeditionEditFragment.newInstance(planetId)
                }
            }
        }

        TabLayoutMediator(b.tabLayout, b.viewPager) { tab, pos ->
            tab.text = if (pos == 0) "Список" else "Форма"
        }.attach()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
