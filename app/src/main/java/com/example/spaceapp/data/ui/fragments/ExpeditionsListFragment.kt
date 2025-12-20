package com.example.spaceapp.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceapp.R
import com.example.spaceapp.databinding.FragmentListBinding
import com.example.spaceapp.ui.adapters.ExpeditionAdapter
import com.example.spaceapp.ui.viewmodel.ExpeditionsViewModel
import android.widget.Toast

class ExpeditionsListFragment : Fragment() {

    private var _b: FragmentListBinding? = null
    private val b get() = _b!!

    private val vm: ExpeditionsViewModel by activityViewModels()
    private lateinit var adapter: ExpeditionAdapter

    private val planetId by lazy { requireArguments().getLong(ARG_PLANET_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ExpeditionAdapter(
            onCall = { e ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${e.phone}")
                }
                startActivity(intent)
            },
            onEdit = { e -> vm.editing.value = e },
            onDelete = { e -> vm.delete(e) }
        )

        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        vm.items.observe(viewLifecycleOwner) {
            adapter.submit(it)
            b.emptyText.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        vm.toast.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        vm.loadLocal(planetId)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> { vm.editing.value = null; true } // сброс на "новую"
            R.id.action_sync -> { vm.sync(planetId); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_PLANET_ID = "planetId"
        fun newInstance(planetId: Long) = ExpeditionsListFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PLANET_ID, planetId) }
        }
    }
}
