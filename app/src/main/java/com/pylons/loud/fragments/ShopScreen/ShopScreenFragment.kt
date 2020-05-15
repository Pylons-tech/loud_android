package com.pylons.loud.fragments.ShopScreen

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.constants.Item.ANGEL_SWORD
import com.pylons.loud.constants.Item.BRONZE_SWORD
import com.pylons.loud.constants.Item.COPPER_SWORD
import com.pylons.loud.constants.Item.DROP_DRAGONACID
import com.pylons.loud.constants.Item.DROP_DRAGONFIRE
import com.pylons.loud.constants.Item.DROP_DRAGONICE
import com.pylons.loud.constants.Item.GOBLIN_EAR
import com.pylons.loud.constants.Item.IRON_SWORD
import com.pylons.loud.constants.Item.SILVER_SWORD
import com.pylons.loud.constants.Item.TROLL_TOES
import com.pylons.loud.constants.Item.WOLF_TAIL
import com.pylons.loud.constants.Item.WOODEN_SWORD
import com.pylons.loud.constants.ItemID.ID_ANGEL_SWORD
import com.pylons.loud.constants.ItemID.ID_BRONZE_SWORD
import com.pylons.loud.constants.ItemID.ID_COPPER_SWORD
import com.pylons.loud.constants.ItemID.ID_IRON_SWORD
import com.pylons.loud.constants.ItemID.ID_SILVER_SWORD
import com.pylons.loud.constants.ItemID.ID_WOODEN_SWORD
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
            val adapter = MyItemRecyclerViewAdapter(
                listOf(
                    Weapon(ID_WOODEN_SWORD, WOODEN_SWORD, 1, 1.0, 100, listOf(), 0),
                    Weapon(ID_COPPER_SWORD, COPPER_SWORD, 1, 2.0, 250, listOf(), 0),
                    Weapon(ID_SILVER_SWORD, SILVER_SWORD, 1, 3.0, 250, listOf(GOBLIN_EAR), 0),
                    Weapon(ID_BRONZE_SWORD, BRONZE_SWORD, 1, 4.0, 250, listOf(WOLF_TAIL), 0),
                    Weapon(ID_IRON_SWORD, IRON_SWORD, 1, 5.0, 250, listOf(TROLL_TOES), 0),
                    Weapon(
                        ID_ANGEL_SWORD,
                        ANGEL_SWORD,
                        1,
                        5.0,
                        20000,
                        listOf(DROP_DRAGONFIRE, DROP_DRAGONICE, DROP_DRAGONACID),
                        0
                    )
                ), frag.getListener(), 2
            )
            mode = 2
            val myView = frag.view as RecyclerView
            myView.adapter = adapter
            text_buy.setTextColor(Color.GREEN)
            text_sell.setTextColor(Color.WHITE)
            text_upgrade.setTextColor(Color.WHITE)
        }

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.setPlayerLocation(2)
        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            text_sell.setOnClickListener {
                val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
                val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), 3)
                mode = 3
                val myView = frag.view as RecyclerView
                myView.adapter = adapter
                text_buy.setTextColor(Color.WHITE)
                text_sell.setTextColor(Color.GREEN)
                text_upgrade.setTextColor(Color.WHITE)
            }

            text_upgrade.setOnClickListener {
                val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
                val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), 4)
                mode = 4
                val myView = frag.view as RecyclerView
                myView.adapter = adapter
                text_buy.setTextColor(Color.WHITE)
                text_sell.setTextColor(Color.WHITE)
                text_upgrade.setTextColor(Color.GREEN)
            }

            if (mode == 3 || mode == 4) {
                val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment
                val adapter = MyItemRecyclerViewAdapter(player.weapons, frag.getListener(), mode)
                val myView = frag.view as RecyclerView
                myView.adapter = adapter
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
