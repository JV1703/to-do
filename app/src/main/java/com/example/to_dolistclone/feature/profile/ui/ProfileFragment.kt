package com.example.to_dolistclone.feature.profile.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.core.utils.ui.makeToast
import com.example.to_dolistclone.databinding.FragmentProfileBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.profile.viewmodel.ProfileViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kizitonwose.calendar.core.daysOfWeek
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var dateUtil: DateUtil

    private val viewModel: ProfileViewModel by viewModels()

    private var today: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        today = dateUtil.getCurrentDate()

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->

            binding.completedAmountTv.text = uiState.todos.count { it.isComplete }.toString()
            binding.pendingAmountTv.text = uiState.todos.count { !it.isComplete }.toString()

            setupBarChart(uiState.barGraphData)
//            setupBarChart(dataset)

        }

        collectLatestLifecycleFlow(viewModel.getBarGraphForTesting(dateUtil.getCurrentDateTimeLong())){
            Log.i("test","$it")
        }

    }

    private fun getPreviousWeekStartDate(date: LocalDate) {
        val prevWeek = viewModel.previousWeek(date)
        makeToast(dateUtil.toString(prevWeek, "EEE, MMM dd, yyyy", Locale.getDefault()))
    }

    private fun getNextWeekStartDate(date: LocalDate) {
        val nextWeek = viewModel.nextWeek(date)
        makeToast(dateUtil.toString(nextWeek, "EEE, MMM dd, yyyy", Locale.getDefault()))
    }

    private fun setupBarChart(data: List<BarEntry>){

        val dayOfWeekList = dateUtil.generateDaysInWeek()
        val barDataSet = BarDataSet(data, "Completion")
        val barData = BarData(barDataSet)

        Log.i("barGraph", "data: ${data.size}")

        val color = ContextCompat.getColor(requireContext(), R.color.black)
        barDataSet.valueTextColor= color

        barData.barWidth = 1F
        binding.barChart.data = barData
        binding.barChart.setFitBars(true)
        val xAxisArray = dayOfWeekList.map { it.name.take(3) }
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisArray)
        binding.barChart.xAxis.setLabelCount(7, true)
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.granularity = 1F
        binding.barChart.xAxis.isGranularityEnabled = true
        
        binding.barChart.invalidate()
    }

    private val dataset = listOf(
        BarEntry(0F, 1F),
        BarEntry(1F, 1F),BarEntry(2F, 1F),BarEntry(3F, 1F),BarEntry(4F, 1F),BarEntry(5F, 1F),BarEntry(6F, 1F)
    )

}