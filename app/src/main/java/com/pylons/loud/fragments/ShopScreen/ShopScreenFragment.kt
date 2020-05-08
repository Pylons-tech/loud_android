package com.pylons.loud.fragments.ShopScreen

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.fragments.Item.ItemFragment
import com.pylons.loud.fragments.Item.MyItemRecyclerViewAdapter
import com.pylons.loud.models.User
import com.pylons.loud.models.Weapon
import kotlinx.android.synthetic.main.fragment_shop_screen.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShopScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShopScreenFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_shop.setText(R.string.shop_desc)

        text_buy.setOnClickListener {
            val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
            val adapter = MyItemRecyclerViewAdapter(listOf(
                Weapon("1", "Wooden Sword", 1, 1, 100, "no", 0),
                Weapon("2", "Cooper Sword", 1, 2, 250, "no", 0),
                Weapon("3", "Silver Sword", 1, 3, 250, "no", 0),
                Weapon("4", "Bronze Sword", 1, 4, 250, "no", 0),
                Weapon("5", "Iron Sword", 1, 5, 250, "no", 0)
            ), frag.getListener(), 2)
            mode = 2
            frag.myview.adapter = adapter
            text_buy.setTextColor(Color.GREEN)
            text_sell.setTextColor(Color.WHITE)
            text_upgrade.setTextColor(Color.WHITE)
        }

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            text_sell.setOnClickListener {
                val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
                val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), 3)
                mode = 3
                frag.myview.adapter = adapter
                text_buy.setTextColor(Color.WHITE)
                text_sell.setTextColor(Color.GREEN)
                text_upgrade.setTextColor(Color.WHITE)
            }

            text_upgrade.setOnClickListener {
                val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
                val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), 4)
                mode = 4
                frag.myview.adapter = adapter
                text_buy.setTextColor(Color.WHITE)
                text_sell.setTextColor(Color.WHITE)
                text_upgrade.setTextColor(Color.GREEN)
            }

            if (mode == 3 || mode == 4) {
                val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
                val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), mode)
                frag.myview.adapter = adapter
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShopScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShopScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
