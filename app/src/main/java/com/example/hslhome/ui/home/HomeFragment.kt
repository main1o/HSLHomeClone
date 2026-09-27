package com.example.hslhome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hslhome.data.model.HomeFeed
import com.example.hslhome.databinding.FragmentHomeBinding
import com.example.hslhome.ui.home.adapter.BannerAdapter
import com.example.hslhome.ui.home.adapter.DeviceAdapter
import com.example.hslhome.ui.home.adapter.MissionAdapter
import com.example.hslhome.ui.home.adapter.StationAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val bannerAdapter by lazy { BannerAdapter() }
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var missionAdapter: MissionAdapter
    private lateinit var stationAdapter: StationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclers()
        setupActions()
        observeViewModel()
        viewModel.refresh()
    }

    private fun setupRecyclers() {
        binding.rvBanner.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBanner.adapter = bannerAdapter

        deviceAdapter = DeviceAdapter { cat ->
            toast("进入「${cat.name}」设备列表（示例）")
        }
        binding.rvDevices.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.rvDevices.adapter = deviceAdapter

        missionAdapter = MissionAdapter { mission ->
            // 真实场景：先展示广告/跳转外链，回调后再 completeMission
            viewModel.completeMission(mission)
        }
        binding.rvMissions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMissions.adapter = missionAdapter

        stationAdapter = StationAdapter { station ->
            toast("导航到「${station.name}」（示例）")
        }
        binding.rvStations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStations.adapter = stationAdapter
    }

    private fun setupActions() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.btnScan.setOnClickListener { toast("打开扫码（示例，可接入相机/MLKit）") }
        binding.btnNfc.setOnClickListener { toast("打开 NFC 碰一碰（示例）") }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state is HomeUiState.Loading
            when (state) {
                is HomeUiState.Success -> bindFeed(state.feed)
                is HomeUiState.Error -> toast(state.message)
                HomeUiState.Loading -> Unit
            }
        }
        viewModel.toast.observe(viewLifecycleOwner) { msg -> toast(msg) }
    }

    private fun bindFeed(feed: HomeFeed) {
        binding.tvGreeting.text = "Hi，${feed.account.nickname}"
        binding.tvPoints.text = getString(
            com.example.hslhome.R.string.points_format, feed.account.validPoints
        )
        binding.tvCoupon.text = getString(
            com.example.hslhome.R.string.coupon_format, feed.account.couponYuan
        )
        bannerAdapter.submitList(feed.banners)
        deviceAdapter.submitList(feed.categories)
        missionAdapter.submitList(feed.missions)
        stationAdapter.submitList(feed.stations)
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
