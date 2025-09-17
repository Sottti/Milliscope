package com.sotti.milliscope.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.sotti.milliscope.data.ListViewModel
import com.sotti.milliscope.design.system.MilliscopeTheme
import com.sotti.milliscope.model.ListAction.ListNotVisible
import com.sotti.milliscope.model.ListAction.ListVisible

internal class ListActivity : ComponentActivity() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MilliscopeTheme {
                ListUi(viewModel = viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAction(ListVisible)
    }

    override fun onStop() {
        viewModel.onAction(ListNotVisible)
        super.onStop()
    }
}
