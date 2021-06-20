package tech.pylons.loud.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.Profile


object WalletLiveData {
    private var userCookbook = MutableLiveData<Cookbook?>()
    private var userProfile = MutableLiveData<Profile?>()

    fun getUserCookbook() : LiveData<Cookbook?> = userCookbook
    fun getUserProfile() :LiveData<Profile?> = userProfile

    fun setUserCookbook(cookbook:Cookbook?){
        userCookbook.postValue(cookbook)
    }

    fun setUserProfile(profile:Profile?) {
        userProfile.postValue(profile)
    }

}