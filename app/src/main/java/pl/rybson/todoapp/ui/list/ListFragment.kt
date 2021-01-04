package pl.rybson.todoapp.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import pl.rybson.todoapp.ui.list.ListFragmentDirections
import pl.rybson.todoapp.R
import pl.rybson.todoapp.databinding.FragmentListBinding
import pl.rybson.todoapp.utils.onQueryTextChange
import pl.rybson.todoapp.viewmodels.LayoutManagerType
import pl.rybson.todoapp.viewmodels.ListViewModel
import pl.rybson.todoapp.viewmodels.SortOrder

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ListFragment : Fragment(R.layout.fragment_list), TasksRecyclerViewAdapter.OnTaskClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val tasksRecyclerViewAdapter = TasksRecyclerViewAdapter(this)

    private val viewModel: ListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setupRecyclerView()

        binding.fab.setOnClickListener {
            view.findNavController().navigate(ListFragmentDirections.actionListFragmentToAddFragment(null))
        }

        viewModel.tasks.observe(viewLifecycleOwner, { tasks ->
            viewModel.emptyList.value = tasks.isEmpty()
            tasksRecyclerViewAdapter.submitList(tasks)
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.emptyList.collect {
                when (it) {
                    true -> {
                        binding.apply {
                            recyclerView.visibility = View.INVISIBLE
                            tvNoData.visibility = View.VISIBLE
                        }
                    }
                    false -> {
                        binding.apply {
                            recyclerView.visibility = View.VISIBLE
                            tvNoData.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.onQueryTextChange { query ->
            viewModel.searchQuery.value = query
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_sort_latest -> {
            viewModel.onSortOrderSelected(SortOrder.BY_LATEST)
            true
        }
        R.id.menu_sort_oldest -> {
            viewModel.onSortOrderSelected(SortOrder.BY_OLDEST)
            true
        }
        R.id.menu_sort_high -> {
            viewModel.onSortOrderSelected(SortOrder.BY_HIGH_PRIORITY)
            true
        }
        R.id.menu_sort_low -> {
            viewModel.onSortOrderSelected(SortOrder.BY_LOW_PRIORITY)
            true
        }
        R.id.menu_settings -> {
            findNavController().navigate(ListFragmentDirections.actionListFragmentToSettingsBottomSheetFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerView.apply {
                adapter = tasksRecyclerViewAdapter

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    viewModel.layoutManagerTypeFlow.collectLatest { stringValue ->
                        layoutManager = when (enumValueOf<LayoutManagerType>(stringValue)) {
                            LayoutManagerType.LINEAR -> LinearLayoutManager(requireContext())
                            LayoutManagerType.STAGGERED -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        }
                    }
                }
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tasksRecyclerViewAdapter.currentList[viewHolder.adapterPosition]

                    viewModel.delete(task)

                    Snackbar.make(requireView(), getString(R.string.deleted), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.undo)) {
                            viewModel.insert(task)
                        }.show()
                }
            }).attachToRecyclerView(recyclerView)
        }
    }

    override fun onTaskClick(position: Int) {
        val task = tasksRecyclerViewAdapter.currentList[position]
        findNavController().navigate(ListFragmentDirections.actionListFragmentToAddFragment(task))
    }
}