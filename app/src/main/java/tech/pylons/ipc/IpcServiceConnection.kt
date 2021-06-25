package tech.pylons.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import tech.pylons.ipc.IIpcInterface.Stub
import tech.pylons.loud.BuildConfig

class IpcServiceConnection(ctx: Context) : ServiceConnection {

    private var isServiceBinded: Boolean = false
    private val TAG: String = "Pylons/" + BuildConfig.APP_NAME

    private var context: Context? = ctx

    private var iIpcService: IIpcInterface? = null


    fun getFromWallet(): String? {
        val msg =  iIpcService!!.wallet2easel()
        println("getFromWallet $msg")
        return msg
    }

    fun submitToWallet(json: String) {
        iIpcService!!.easel2wallet(json)
    }

    fun getCoreData(whatToGet: Int): String? {
        return iIpcService!!.getCoreData(whatToGet)
    }

    fun isIPCAvailable(): Boolean {
        Log.i(TAG, "Wallet ipc service is binded? $isServiceBinded")
        return isServiceBinded
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        iIpcService = Stub.asInterface(service)
        isServiceBinded = true
        DroidIpcWireImpl.initWallet(context)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.e(TAG, "Service has unexpectedly disconnected")
        iIpcService = null
        isServiceBinded = false
    }

    fun bind() {
        Log.i(TAG, "Bind")
        val serviceIntent = Intent("tech.pylons.wallet.ipc.BIND")
        serviceIntent.setPackage("tech.pylons.wallet")
        context!!.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        if (iIpcService != null) {
            Log.i(TAG, "Unbind")
            context!!.unbindService(this)
            iIpcService = null
        }
    }
}