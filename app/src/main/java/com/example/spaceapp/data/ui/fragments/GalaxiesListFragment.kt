package com.example.spaceapp.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceapp.R
import com.example.spaceapp.data.local.entity.GalaxyEntity
import com.example.spaceapp.databinding.FragmentListBinding
import com.example.spaceapp.ui.adapters.GalaxyAdapter
import com.example.spaceapp.ui.viewmodel.GalaxiesViewModel
import com.example.spaceapp.util.Ids

class GalaxiesListFragment : Fragment() {

    private var selected: GalaxyEntity? = null

    private var _b: FragmentListBinding? = null
    private val b get() = _b!!

    private val vm: GalaxiesViewModel by viewModels()
    private lateinit var adapter: GalaxyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GalaxyAdapter(
            onClick = { g ->
                selected = g
                adapter.setSelected(g.id)
                requireActivity().invalidateOptionsMenu()
            },
            onLongClick = { g ->
                val bundle = Bundle().apply {
                    putLong("galaxyId", g.id)
                    putString("galaxyName", g.name)
                }
                findNavController().navigate(R.id.action_galaxies_to_planets, bundle)
            }
        )

        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        vm.items.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            b.emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

            // если выбранная галактика исчезла (удалили/синк), сбросим selection
            if (selected != null && list.none { it.id == selected!!.id }) {
                selected = null
                adapter.setSelected(null)
                requireActivity().invalidateOptionsMenu()
            }
        }

        vm.loadLocal()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val hasSelection = selected != null
        menu.findItem(R.id.action_edit)?.isEnabled = hasSelection
        menu.findItem(R.id.action_delete)?.isEnabled = hasSelection
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showAddGalaxyDialog()
                true
            }

            R.id.action_edit -> {
                val g = selected ?: return true
                showEditGalaxyDialog(g)
                true
            }

            R.id.action_delete -> {
                val g = selected ?: return true
                confirmDeleteGalaxy(g)
                true
            }

            R.id.action_sync -> {
                vm.sync()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddGalaxyDialog() {
        val etName = EditText(requireContext()).apply { hint = "Название" }
        val etType = EditText(requireContext()).apply { hint = "Тип (спиральная и т.п.)" }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(etName)
            addView(etType)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить галактику")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                val type = etType.text.toString().trim()
                if (name.isNotEmpty() && type.isNotEmpty()) {
                    vm.upsert(GalaxyEntity(Ids.newId(), name, type))
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditGalaxyDialog(g: GalaxyEntity) {
        val etName = EditText(requireContext()).apply { setText(g.name) }
        val etType = EditText(requireContext()).apply { setText(g.type) }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(etName)
            addView(etType)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить галактику")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                val type = etType.text.toString().trim()
                if (name.isNotEmpty() && type.isNotEmpty()) {
                    vm.upsert(g.copy(name = name, type = type))
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun confirmDeleteGalaxy(g: GalaxyEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Удалить галактику «${g.name}»? Это удалит и планеты, и экспедиции.")
            .setPositiveButton("Да") { _, _ ->
                vm.delete(g)
                selected = null
                adapter.setSelected(null)
                requireActivity().invalidateOptionsMenu()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
