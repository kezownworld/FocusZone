package com.focuszone.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.focuszone.app.R
import com.focuszone.app.databinding.ActivityAppSelectionBinding
import com.focuszone.app.viewmodel.AppSelectionViewModel

class AppSelectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAppSelectionBinding
    private val viewModel: AppSelectionViewModel by viewModels()
    private lateinit var adapter: AppSelectionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityAppSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = AppSelectionAdapter { packageName, appName, _ ->
            viewModel.toggleAppSelection(packageName, appName)
        }
        
        binding.rvApps.apply {
            layoutManager = LinearLayoutManager(this@AppSelectionActivity)
            adapter = this@AppSelectionActivity.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.installedApps.observe(this) { apps ->
            adapter.submitList(apps)
        }
        
        viewModel.selectedApps.observe(this) { selected ->
            adapter.setSelectedApps(selected)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            // You can show a loading indicator here if needed
        }
    }
    
    private fun setupListeners() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.searchApps(text.toString())
        }
        
        binding.btnSelectAll.setOnClickListener {
            viewModel.selectAll()
        }
        
        binding.btnDeselectAll.setOnClickListener {
            viewModel.deselectAll()
        }
        
        binding.btnSave.setOnClickListener {
            viewModel.saveSelection {
                Toast.makeText(this, R.string.apps_saved, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
