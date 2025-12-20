package com.example.spaceapp.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spaceapp.data.local.entity.ExpeditionEntity
import com.example.spaceapp.databinding.FragmentExpeditionEditBinding
import com.example.spaceapp.ui.viewmodel.ExpeditionsViewModel
import com.example.spaceapp.util.DateFmt
import com.example.spaceapp.util.Ids
import java.util.Calendar

class ExpeditionEditFragment : Fragment() {

    private var _b: FragmentExpeditionEditBinding? = null
    private val b get() = _b!!

    private val vm: ExpeditionsViewModel by activityViewModels()
    private val planetId by lazy { requireArguments().getLong(ARG_PLANET_ID) }

    private var pickedDateMillis: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentExpeditionEditBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fun fillForm(e: ExpeditionEntity?) {
            if (e == null) {
                b.etMission.setText("")
                b.etCommander.setText("")
                b.etPhone.setText("")
                pickedDateMillis = System.currentTimeMillis()
                b.tvDate.text = DateFmt.format(pickedDateMillis)
                b.btnSave.text = "Сохранить"
            } else {
                b.etMission.setText(e.missionName)
                b.etCommander.setText(e.commanderName)
                b.etPhone.setText(e.phone)
                pickedDateMillis = e.dateMillis
                b.tvDate.text = DateFmt.format(pickedDateMillis)
                b.btnSave.text = "Сохранить изменения"
            }
        }

        vm.editing.observe(viewLifecycleOwner) { editing ->
            fillForm(editing)
        }

        fillForm(vm.editing.value)

        b.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = pickedDateMillis }
            val dlg = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    val c = Calendar.getInstance().apply {
                        set(Calendar.YEAR, y)
                        set(Calendar.MONTH, m)
                        set(Calendar.DAY_OF_MONTH, d)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    pickedDateMillis = c.timeInMillis
                    b.tvDate.text = DateFmt.format(pickedDateMillis)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dlg.show()
        }

        b.btnSave.setOnClickListener {
            val mission = b.etMission.text?.toString()?.trim().orEmpty()
            val commander = b.etCommander.text?.toString()?.trim().orEmpty()
            val phone = b.etPhone.text?.toString()?.trim().orEmpty()

            if (mission.isEmpty() || commander.isEmpty() || phone.isEmpty()) return@setOnClickListener

            val current = vm.editing.value
            val entity = if (current == null) {
                ExpeditionEntity(
                    id = Ids.newId(),
                    planetId = planetId,
                    missionName = mission,
                    commanderName = commander,
                    phone = phone,
                    dateMillis = pickedDateMillis
                )
            } else {
                current.copy(
                    missionName = mission,
                    commanderName = commander,
                    phone = phone,
                    dateMillis = pickedDateMillis
                )
            }
            vm.save(entity)
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_PLANET_ID = "planetId"
        fun newInstance(planetId: Long) = ExpeditionEditFragment().apply {
            arguments = Bundle().apply { putLong(ARG_PLANET_ID, planetId) }
        }
    }
}
