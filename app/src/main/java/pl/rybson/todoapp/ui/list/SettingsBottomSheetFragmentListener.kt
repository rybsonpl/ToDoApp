package pl.rybson.todoapp.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import pl.rybson.todoapp.R
import pl.rybson.todoapp.databinding.FragmentModalBottomSheetSettingsBinding
import pl.rybson.todoapp.viewmodels.LayoutManagerType
import pl.rybson.todoapp.viewmodels.ListViewModel

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SettingsBottomSheetFragmentListener : BottomSheetDialogFragment(), SettingsRecyclerViewAdapter.OnSettingsItemClickListener {

    private var _binding: FragmentModalBottomSheetSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by activityViewModels()

    private lateinit var settingsRecyclerViewAdapter: SettingsRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModalBottomSheetSettingsBinding.inflate(
            inflater, inflater.inflate(R.layout.fragment_modal_bottom_sheet_settings, container) as ViewGroup, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val settingsList = arrayListOf(
            Pair(R.drawable.ic_linear_view, requireContext().getString(R.string.change_view)),
            Pair(R.drawable.ic_delete, requireContext().getString(R.string.delete_all))
        )

        settingsRecyclerViewAdapter = SettingsRecyclerViewAdapter(settingsList, this)

        binding.apply {
            recyclerView.apply {
                adapter = settingsRecyclerViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    override fun onSettingsClick(position: Int) {
        when (position) {
            0 -> {
                changeLayoutManager()
            }
            1 -> deleteAll()
        }
    }

    private fun changeLayoutManager() {
        viewModel.onLayoutManagerSelected()
        dismiss()
    }

    private fun deleteAll() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteAllTasks()
                dialog.dismiss()
                dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
                dismiss()
            }.show()
    }

}