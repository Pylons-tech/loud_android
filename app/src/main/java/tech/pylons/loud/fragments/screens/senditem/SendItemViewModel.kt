package tech.pylons.loud.fragments.screens.senditem

import androidx.lifecycle.ViewModel
import tech.pylons.loud.models.Friend
import tech.pylons.loud.models.Item

class SendItemViewModel: ViewModel() {
    lateinit var friend: Friend
    lateinit var itemIds: List<Item>
}