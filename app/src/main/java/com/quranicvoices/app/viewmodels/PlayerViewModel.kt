package com.quranicvoices.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quranicvoices.app.ConnectionLiveData
import com.quranicvoices.app.models.PlayerModel
import com.quranicvoices.app.utils.Constants
import com.quranicvoices.rabiulqaloob.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    preferenceHelper: PreferenceHelper
) : AndroidViewModel(application) {

    private val _playerModel: MutableLiveData<PlayerModel> = MutableLiveData(PlayerModel())
    val playerModel: LiveData<PlayerModel> = _playerModel

    val connectionLiveData: ConnectionLiveData = ConnectionLiveData(application)

    init {
        val surahNumber =
            preferenceHelper.getString(
                Constants.SURAH_NUMBER,
                 "1"
            ) ?: _playerModel.value?.currentSurah ?: "1"

        val surahProgress =
            preferenceHelper.getInt(
                Constants.PROGRESS,
                _playerModel.value?.surahProgress ?: 0
            )
        _playerModel.postValue(
            PlayerModel(
                surahNumber,
                surahProgress,
                surahNumber != "0"
            )
        )
    }

    fun setPlayerModel(
        playerModel: PlayerModel
    ){
        _playerModel.postValue(playerModel)
    }

}