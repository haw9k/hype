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
import com.example.spaceapp.data.local.entity.PlanetEntity
import com.example.spaceapp.databinding.FragmentListBinding
import com.example.spaceapp.ui.adapters.PlanetAdapter
import com.example.spaceapp.ui.viewmodel.PlanetsViewModel
import com.example.spaceapp.util.Ids

class PlanetsListFragment : Fragment() {

    private var selected: PlanetEntity? = null

    private var _b: FragmentListBinding? = null
    private val b get() = _b!!

    private val vm: PlanetsViewModel by viewModels()
    private lateinit var adapter: PlanetAdapter

    private val galaxyId by lazy { requireArguments().getLong("galaxyId") }
    private val galaxyName by lazy { requireArguments().getString("galaxyName").orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().title = "Галактика: $galaxyName"
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

        adapter = PlanetAdapter(
            onClick = { p ->
                selected = p
                adapter.setSelected(p.id)
                requireActivity().invalidateOptionsMenu()
            },
            onLongClick = { p ->
                val bundle = Bundle().apply {
                    putLong("planetId", p.id)
                    putString("planetName", p.name)
                }
                findNavController().navigate(R.id.action_planets_to_expeditions, bundle)
            }
        )

        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        vm.items.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            b.emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE

            if (selected != null && list.none { it.id == selected!!.id }) {
                selected = null
                adapter.setSelected(null)
                requireActivity().invalidateOptionsMenu()
            }
        }

        vm.loadLocal(galaxyId)
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
            R.id.action_add -> { showAddPlanetDialog(); true }

            R.id.action_edit -> {
                val p = selected ?: return true
                showEditPlanetDialog(p)
                true
            }

            R.id.action_delete -> {
                val p = selected ?: return true
                confirmDeletePlanet(p)
                true
            }

            R.id.action_sync -> {
                vm.sync(galaxyId)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddPlanetDialog() {
        val etName = EditText(requireContext()).apply { hint = "Название" }
        val etClass = EditText(requireContext()).apply { hint = "Класс (каменная/газовая...)" }
        val etRadius = EditText(requireContext()).apply { hint = "Радиус (км)" }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(etName)
            addView(etClass)
            addView(etRadius)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить планету")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                val cls = etClass.text.toString().trim()
                val radius = etRadius.text.toString().toDoubleOrNull()

                if (name.isNotEmpty() && cls.isNotEmpty() && radius != null) {
                    val entity = PlanetEntity(
                        id = Ids.newId(),
                        galaxyId = galaxyId,
                        name = name,
                        planetClass = cls,
                        radiusKm = radius
                    )
                    vm.upsert(entity, galaxyId)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditPlanetDialog(p: PlanetEntity) {
        val etName = EditText(requireContext()).apply { setText(p.name) }
        val etClass = EditText(requireContext()).apply { setText(p.planetClass) }
        val etRadius = EditText(requireContext()).apply { setText(p.radiusKm.toString()) }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0)
            addView(etName)
            addView(etClass)
            addView(etRadius)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить планету")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                val cls = etClass.text.toString().trim()
                val radius = etRadius.text.toString().toDoubleOrNull()

                if (name.isNotEmpty() && cls.isNotEmpty() && radius != null) {
                    vm.upsert(
                        p.copy(name = name, planetClass = cls, radiusKm = radius),
                        galaxyId
                    )
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun confirmDeletePlanet(p: PlanetEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Удалить планету «${p.name}»? Это удалит и экспедиции этой планеты.")
            .setPositiveButton("Да") { _, _ ->
                vm.delete(p, galaxyId)
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
