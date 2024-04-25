package com.kalpeet.getitdone.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.kalpeet.getitdone.UI.tasks.TasksFragment
import com.kalpeet.getitdone.data.GetItDoneDatabase
import com.kalpeet.getitdone.data.Task
import com.kalpeet.getitdone.data.TaskDao
import com.kalpeet.getitdone.databinding.ActivityMainBinding
import com.kalpeet.getitdone.databinding.DialogAddTaskBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var database: GetItDoneDatabase
    private val taskDao: TaskDao by lazy { database.getTaskDao() }
    private val tasksFragment: TasksFragment = TasksFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = PagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = "Tasks"
        }.attach()

        binding.floatingActionButton.setOnClickListener {
            showAddTaskDialog()

        }

        database = GetItDoneDatabase.createDatabase(this)


    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.buttonShowDetails.setOnClickListener {
            if (dialogBinding.editTextTaskDetails.visibility == View.VISIBLE) {
                dialogBinding.editTextTaskDetails.visibility = View.GONE
            } else {
                dialogBinding.editTextTaskDetails.visibility = View.VISIBLE
            }
            dialogBinding.buttonSave.setOnClickListener {
                val task = Task(
                    title = dialogBinding.editTextTaskTitle.text.toString(),
                    description = dialogBinding.editTextTaskDetails.text.toString()
                )
                thread {
                    taskDao.createTask(task)
                }
                dialog.dismiss()
                tasksFragment.fetchAllTasks()
            }

        }

        dialog.show()
    }

    inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 1

        override fun createFragment(position: Int): Fragment {
            return tasksFragment
        }



    }


}