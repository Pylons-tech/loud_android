package com.pylons.loud.fragments.screens.friends

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Location.FRIENDS
import com.pylons.loud.fragments.lists.friend.FriendFragment
import com.pylons.loud.fragments.lists.friend.MyFriendRecyclerViewAdapter
import com.pylons.loud.utils.Preferences.deleteFriendAddress
import com.pylons.loud.utils.Preferences.getFriendAddress
import kotlinx.android.synthetic.main.dialog_add_friend.view.*
import kotlinx.android.synthetic.main.fragment_friends_screen.*

/**
 * A simple [Fragment] subclass.
 */
class FriendsScreenFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.setPlayerLocation(FRIENDS)

        model.getPlayer().observe(viewLifecycleOwner, Observer {
            val frag = childFragmentManager.findFragmentById(R.id.fragment_friend_list)
            val view = frag?.view as RecyclerView
            val c = context
            if (c is FriendFragment.OnListFragmentInteractionListener) {
                view.adapter = MyFriendRecyclerViewAdapter(it.friends, c, 1)
            }
        })

        button_add_friend.setOnClickListener {
            context?.let {
                addFriendPrompt(it, "")
            }
        }

        button_share_address.setOnClickListener {
            val player = model.getPlayer().value
            player?.let {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Join me in fighting dragons!\nhttps://www.pylons.tech/loud?address=${player.address}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }

        context?.let { c ->
            val friendAddress = getFriendAddress(c)
            friendAddress?.let {
                addFriendPrompt(c, it)
                deleteFriendAddress(c)
            }
        }

    }

    private fun addFriendPrompt(c: Context, address: String) {
        val mDialogView = LayoutInflater.from(c).inflate(R.layout.dialog_add_friend, null)
        (mDialogView.edit_text_address as TextView).text = address

        val dialogBuilder = AlertDialog.Builder(c)
        dialogBuilder.setMessage(
            getString(R.string.add_friend_prompt)
        )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.proceed)) { _, _ ->
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.add_friend))
        alert.setView(mDialogView)
        alert.show()

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val player = model.getPlayer().value
            if (player != null) {
                val address = mDialogView.edit_text_address.text.toString()
                val name = mDialogView.edit_text_name.text.toString()

                if (address.isNotBlank() && name.isNotBlank()) {
                    player.addFriend(address, name)
                    model.setPlayer(player)
                    player.saveAsync(c)
                    alert.dismiss()
                } else {
                    Toast.makeText(
                        c,
                        getString(R.string.invalid_add_friend),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}