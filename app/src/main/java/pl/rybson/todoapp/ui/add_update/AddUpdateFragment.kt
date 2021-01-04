package pl.rybson.todoapp.ui.add_update

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pl.rybson.todoapp.R
import pl.rybson.todoapp.data.models.Priority
import pl.rybson.todoapp.data.models.TaskModel
import pl.rybson.todoapp.databinding.FragmentAddUpdateBinding
import pl.rybson.todoapp.viewmodels.AddUpdateViewModel

@AndroidEntryPoint
class AddUpdateFragment : Fragment(R.layout.fragment_add_update) {

    private var _binding: FragmentAddUpdateBinding? = null
    private val binding get() = _binding!!

    private val args: AddUpdateFragmentArgs by navArgs()
    private val viewModel: AddUpdateViewModel by viewModels()

    private lateinit var priorities: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        priorities = requireContext().resources.getStringArray(R.array.priorities)

        setHasOptionsMenu(true)

        setupToolbarTitle()
        setupPriorityDropdown()
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = if (args.task == null) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    } else {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_update, R.id.menu_add -> {
            getDataAndAddOrUpdate()
            true
        }
        else -> super.onOptionsItemSelected(item)

    }

    private fun setupToolbarTitle() = if (args.task == null) {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.add)
    } else {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.update)
    }

    private fun setupPriorityDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, priorities)
        binding.dropdownPriority.apply {
            setAdapter(adapter)

            setOnItemClickListener { _, _, position, _ ->
                setPrioritiesListTextColor(position)
            }
        }
    }

    private fun setupViews() {
        args.task?.let { task ->
            binding.apply {
                etTitle.setText(task.title)
                etDescription.setText(task.description)

                when (task.priority) {
                    Priority.HIGH -> dropdownPriority.setText(priorities[0], false)
                    Priority.MEDIUM -> dropdownPriority.setText(priorities[1], false)
                    Priority.LOW -> dropdownPriority.setText(priorities[2], false)
                }
                setPrioritiesListTextColor(task.priority.ordinal)
            }
        }
    }

    private fun getDataAndAddOrUpdate() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isBlank()) {
            binding.tlTitle.error = getString(R.string.title_cannot_be_empty)
        } else {
            val description = binding.etDescription.text.toString()
            val priority = when (binding.dropdownPriority.editableText.toString().trim()) {
                priorities[0] -> Priority.HIGH
                priorities[2] -> Priority.LOW
                else -> Priority.MEDIUM
            }

            if (args.task != null) {
                val taskModel = args.task!!.copy(title = title, description = description, priority = priority)
                viewModel.update(taskModel)
                Snackbar.make(requireView(), getString(R.string.updated), Snackbar.LENGTH_SHORT).show()
            } else {
                val taskModel = TaskModel(title = title, description = description, priority = priority)
                viewModel.insert(taskModel)
                Snackbar.make(requireView(), getString(R.string.added), Snackbar.LENGTH_SHORT).show()
            }

            hideKeyboard()
            findNavController().navigateUp()
        }
    }

    private fun setPrioritiesListTextColor(position: Int) {
        val color = when (position) {
            0 -> R.color.red
            1 -> R.color.yellow
            2 -> R.color.green
            else -> 0
        }
        binding.dropdownPriority.setTextColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}


