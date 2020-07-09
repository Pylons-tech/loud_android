package com.pylons.loud.fragments.screens.senditem

import androidx.lifecycle.ViewModel
import com.pylons.loud.models.Friend
import com.pylons.loud.models.Item

class SendItemViewModel: ViewModel() {
    lateinit var friend: Friend
    lateinit var itemIds: List<Item>
}