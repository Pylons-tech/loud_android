package tech.pylons.loud.fragments.ui.blockchainstatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.pylons.wallet.core.Core
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class BlockChainStatusViewModel : ViewModel() {
    private val _blockHeight = MutableLiveData<Long>()
    val blockHeight: LiveData<Long> = _blockHeight

    private val _lastTx = MutableLiveData<String>()
    val lastTx: LiveData<String> = _lastTx

    init {
        println("BlockChainStatusViewModel")

    }

    suspend fun getStatusBlock() {
        val statusBlock = Core.current?.getStatusBlock()
        withContext(Main) {
            _blockHeight.value = statusBlock.height
        }
    }

    fun setTx(tx: String) {
        _lastTx.value = tx
    }
}