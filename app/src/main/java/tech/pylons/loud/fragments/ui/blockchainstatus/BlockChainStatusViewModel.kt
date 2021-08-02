package tech.pylons.loud.fragments.ui.blockchainstatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.pylons.ipc.WalletHandler

class BlockChainStatusViewModel : ViewModel() {
    private val _blockHeight = MutableLiveData<Long>()
    val blockHeight: LiveData<Long> = _blockHeight

    private val _lastTx = MutableLiveData<String>()
    val lastTx: LiveData<String> = _lastTx

    init {
        println("BlockChainStatusViewModel")
    }

    @ExperimentalUnsignedTypes
    fun getStatusBlock() {
        _blockHeight.postValue(WalletHandler.getBlockStatusHeight())
    }

    fun setTx(tx: String) {
        _lastTx.postValue(tx)
    }
}