package eu.mkonic.tachiyomi.ui.more.stats.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.databinding.StatsDetailsChartBinding

class HeaderStatsDetailsAdapter(
    private val statDetailsHeaderListener: StatsDetailsChartLayout.StatDetailsHeaderListener?,
    private val presenter: StatsDetailsPresenter?,
) : RecyclerView.Adapter<HeaderStatsDetailsAdapter.HeaderStatsDetailsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderStatsDetailsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.stats_details_chart, parent, false)
        return HeaderStatsDetailsHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderStatsDetailsHolder, position: Int) {
        holder.binding.root.listener = statDetailsHeaderListener
        holder.binding.root.setupChart(presenter)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.stats_details_chart
    }

    override fun getItemId(position: Int): Long {
        return "Header Stats".hashCode().toLong()
    }

    class HeaderStatsDetailsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = StatsDetailsChartBinding.bind(view)
    }
}
