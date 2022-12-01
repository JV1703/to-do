package com.example.to_dolistclone.feature.profile.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.example.to_dolistclone.R
import com.example.to_dolistclone.core.common.DateUtil
import com.example.to_dolistclone.core.utils.ui.collectLatestLifecycleFlow
import com.example.to_dolistclone.databinding.FragmentProfileBinding
import com.example.to_dolistclone.feature.BaseFragment
import com.example.to_dolistclone.feature.common.popup_menu.PopupMenuManager
import com.example.to_dolistclone.feature.profile.ui.adapter.UpcomingWeeklyTodoAdapter
import com.example.to_dolistclone.feature.profile.viewmodel.PieGraphFilter
import com.example.to_dolistclone.feature.profile.viewmodel.ProfileViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var popupMenuManager: PopupMenuManager

    private val viewModel: ProfileViewModel by viewModels()

    private var today: LocalDate? = null

    private lateinit var rvAdapter: UpcomingWeeklyTodoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()

        today = dateUtil.getCurrentDate()

        binding.pieChartFilter.setOnClickListener {
            setupPieChartPopupMenu()
        }

        collectLatestLifecycleFlow(viewModel.pieGraphFilter) {
            Log.i("pieFilter", "${it.name}")
            binding.pieChartFilter.text = when (it) {
                PieGraphFilter.WEEK -> {
                    "In 7 days"
                }
                PieGraphFilter.MONTH -> {
                    "In 30 days"
                }
                PieGraphFilter.ALL -> {
                    "All"
                }
            }
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->

            binding.completedAmountTv.text = uiState.todos.count { it.isComplete }.toString()
            binding.pendingAmountTv.text = uiState.todos.count { !it.isComplete }.toString()

            setupBarChart(uiState.barChartData)
            setupPieChart(uiState.pieChartData)
            Log.i("ProfileFragment", "pie chart data size - ${uiState.pieChartData}")
            rvAdapter.submitList(uiState.recyclerViewData)

            binding.dateRangeTv.text = getDateRangeText(uiState.selectedDate)

            binding.nextWeek.isInvisible =
                dateUtil.getLastDateOfWeek(uiState.selectedDate) == dateUtil.getLastDateOfWeek(
                    dateUtil.getCurrentDateTimeLong()
                )
            binding.nextWeek.isEnabled =
                dateUtil.getLastDateOfWeek(uiState.selectedDate) != dateUtil.getLastDateOfWeek(
                    dateUtil.getCurrentDateTimeLong()
                )

            binding.nextWeek.setOnClickListener {
                viewModel.nextWeek(uiState.selectedDate)
            }

            binding.prevWeek.setOnClickListener {
                viewModel.previousWeek(uiState.selectedDate)
            }

        }

    }

    private fun setupBarChart(data: List<BarEntry>) {

        val firstDayOfWeek = dateUtil.getFirstDayOfWeek()
        val dayOfWeekList = dateUtil.generateDaysInWeek(firstDayOfWeek)
        val xAxisArray = dayOfWeekList.map { it.name.take(3) }
        val barDataSet = BarDataSet(data, "Completion")
        val barData = BarData(barDataSet)
        barData.isHighlightEnabled = false

        val textFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value > 0) {
                    DefaultValueFormatter(0).getFormattedValue(value)
                } else {
                    ""
                }
            }
        }

        val color = ContextCompat.getColor(requireContext(), R.color.black)
        barDataSet.valueTextColor = color
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT
        barData.setValueFormatter(textFormatter)

        binding.barChart.xAxis.apply {
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(xAxisArray)
            position = XAxis.XAxisPosition.BOTTOM
        }

        binding.barChart.axisLeft.apply {
            axisMinimum = 0F
            setDrawGridLines(false)
            labelCount = 10
            valueFormatter = textFormatter
            isGranularityEnabled = true
            granularity = 1F
        }

        binding.barChart.axisRight.apply {
            axisMinimum = 0F
            setDrawGridLines(false)
            isEnabled = false
        }

        binding.barChart.description.apply {
            isEnabled = false
        }

        binding.barChart.apply {
            this.data = barData
            setFitBars(true)
            setScaleEnabled(false)

            if (data.isNotEmpty()) {
                setVisibleYRange(0F, (data.maxOf { it.y } + 5), YAxis.AxisDependency.LEFT)
            }
            invalidate()
        }
    }

    private fun setupPieChart(data: List<PieEntry>) {
        val pieDataSet = PieDataSet(data, "")
        pieDataSet.setDrawValues(false)
        val colors = listOf(
            ColorTemplate.VORDIPLOM_COLORS,
            ColorTemplate.JOYFUL_COLORS,
            ColorTemplate.COLORFUL_COLORS,
            ColorTemplate.LIBERTY_COLORS,
            ColorTemplate.MATERIAL_COLORS,
            ColorTemplate.PASTEL_COLORS
        ).map { intArray: IntArray -> intArray.toCollection<MutableCollection<Int>>(ArrayList()) }
            .flatten().toIntArray()
        pieDataSet.setColors(colors, 255)
        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.animateY(0)


        binding.pieChart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
        }

        binding.pieChart.description.apply {
            isEnabled = false
        }

        binding.pieChart.apply {
            setDrawEntryLabels(false)
            setHoleColor(android.R.color.transparent)
            isRotationEnabled = false
        }
    }

    private fun setupPieChartPopupMenu() {
        popupMenuManager.showPieChartPopupMenu(
            requireContext(), binding.pieChartFilter
        ) { menuItem ->
            when (menuItem.itemId) {
                R.id.week -> {
                    viewModel.updatePieGraphFilter(PieGraphFilter.WEEK)
                    true
                }
                R.id.month -> {
                    viewModel.updatePieGraphFilter(PieGraphFilter.MONTH)
                    true
                }
                R.id.all -> {
                    viewModel.updatePieGraphFilter(PieGraphFilter.ALL)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun setupRv() {
        rvAdapter = UpcomingWeeklyTodoAdapter(dateUtil)
        binding.weeklyTaskRv.adapter = rvAdapter
    }

    private fun getDateRangeText(selectedDate: Long?): String {
        val today = dateUtil.getCurrentDateTimeLong()
        val startDate =
            if (selectedDate == null) dateUtil.getFirstDateOfWeek(today) else dateUtil.getFirstDateOfWeek(
                selectedDate
            )
        val endDate = if (selectedDate == null) dateUtil.getCurrentDate()
            .plusWeeks(1) else dateUtil.getLastDateOfWeek(selectedDate)

        val startDateString = dateUtil.toString(startDate, "MM/dd", Locale.getDefault())
        val endDateString = dateUtil.toString(endDate, "MM/dd", Locale.getDefault())
        return "$startDateString - $endDateString"
    }
}