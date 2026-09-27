package com.example.hslhome.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hslhome.App
import com.example.hslhome.data.model.HomeFeed
import com.example.hslhome.data.model.Mission
import com.example.hslhome.data.repository.HomeDataSource
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val feed: HomeFeed) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repo: HomeDataSource = App.instance.homeDataSource
) : ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>(HomeUiState.Loading)
    val uiState: LiveData<HomeUiState> = _uiState

    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String> = _toast

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                _uiState.value = HomeUiState.Success(repo.loadHome())
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "未知错误")
            }
        }
    }

    /**
     * 完成一个积分任务。真实场景：
     * - WATCH_AD: 先调用广告 SDK 展示激励视频，回调 onRewardArrived 后再上报；
     * - THIRD_LINK: 跳转外链并用 validTimeSec 做停留时长校验；
     * - 发放积分动作必须由服务端完成，客户端仅上报事件。
     */
    fun completeMission(mission: Mission) {
        viewModelScope.launch {
            val added = repo.addPointsFromMission(mission)
            val current = (_uiState.value as? HomeUiState.Success)?.feed?.account?.validPoints ?: 0
            _toast.value = "完成《${mission.name}》 +${added} 积分"
            // 局部更新账户积分（演示用）
            (_uiState.value as? HomeUiState.Success)?.feed?.let { feed ->
                val newAccount = feed.account.copy(validPoints = current + added)
                _uiState.value = HomeUiState.Success(feed.copy(account = newAccount))
            }
        }
    }
}
