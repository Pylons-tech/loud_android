package tech.pylons.loud.fragments.screens.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

import tech.pylons.loud.R
import tech.pylons.loud.activities.GameScreenActivity
import tech.pylons.loud.constants.Item.ANGEL_SWORD
import tech.pylons.loud.constants.Item.BRONZE_SWORD
import tech.pylons.loud.constants.Item.COPPER_SWORD
import tech.pylons.loud.constants.Item.DROP_DRAGONACID
import tech.pylons.loud.constants.Item.DROP_DRAGONFIRE
import tech.pylons.loud.constants.Item.DROP_DRAGONICE
import tech.pylons.loud.constants.Item.GOBLIN_EAR
import tech.pylons.loud.constants.Item.IRON_SWORD
import tech.pylons.loud.constants.Item.SILVER_SWORD
import tech.pylons.loud.constants.Item.TROLL_TOES
import tech.pylons.loud.constants.Item.WOLF_TAIL
import tech.pylons.loud.constants.Item.WOODEN_SWORD
import tech.pylons.loud.constants.ItemID
import tech.pylons.loud.constants.Shop.BUY
import tech.pylons.loud.constants.Shop.SELL
import tech.pylons.loud.constants.Shop.UPGRADE
import tech.pylons.loud.fragments.lists.item.ItemFragment
import tech.pylons.loud.fragments.lists.item.MyItemRecyclerViewAdapter
import tech.pylons.loud.models.Item
import tech.pylons.loud.models.User
import tech.pylons.loud.models.Weapon
import kotlinx.android.synthetic.main.fragment_shop_action.*

/**
 * A simple [Fragment] subclass.
 */
class ShopActionFragment : Fragment() {
    val model: GameScreenActivity.SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frag = childFragmentManager.findFragmentById(R.id.fragment_item) as ItemFragment

        model.getPlayer().observe(viewLifecycleOwner, Observer<User> { player ->
            when (model.shopAction) {
                BUY -> {
                    text_shop_action.text = getString(R.string.shop_buy_description)
                    val adapter = MyItemRecyclerViewAdapter(
                        listOf(
                            Weapon(
                                ItemID.ID_WOODEN_SWORD,
                                WOODEN_SWORD,
                                1,
                                3.0,
                                100,
                                100,
                                listOf(),
                                0,
                                ""
                            ),
                            Weapon(
                                ItemID.ID_COPPER_SWORD,
                                COPPER_SWORD,
                                1,
                                10.0,
                                250,
                                250,
                                listOf(),
                                0,
                                ""
                            ),
                            Weapon(
                                ItemID.ID_SILVER_SWORD,
                                SILVER_SWORD,
                                1,
                                30.0,
                                250,
                                250,
                                listOf(
                                    GOBLIN_EAR
                                ),
                                0,
                                ""
                            ),
                            Weapon(
                                ItemID.ID_BRONZE_SWORD,
                                BRONZE_SWORD,
                                1,
                                50.0,
                                250,
                                250,
                                listOf(
                                    WOLF_TAIL
                                ),
                                0,
                                ""
                            ),
                            Weapon(
                                ItemID.ID_IRON_SWORD,
                                IRON_SWORD,
                                1,
                                100.0,
                                250,
                                250,
                                listOf(
                                    TROLL_TOES
                                ),
                                0,
                                ""
                            ),
                            Weapon(
                                ItemID.ID_ANGEL_SWORD,
                                ANGEL_SWORD,
                                1,
                                5.0,
                                20000,
                                20000,
                                listOf(
                                    DROP_DRAGONFIRE,
                                    DROP_DRAGONICE,
                                    DROP_DRAGONACID
                                ),
                                0,
                                ""
                            )
                        ), frag.getListener(), 2
                    )
                    val myView = frag.view as RecyclerView
                    myView.adapter = adapter
                }
                SELL -> {
                    if (player != null) {
                        val items = mutableListOf<Item>()
                        items.addAll(player.weapons)
                        items.addAll(player.materials)
                        val adapter = MyItemRecyclerViewAdapter(items, frag.getListener(), 3)
                        val myView = frag.view as RecyclerView
                        myView.adapter = adapter

                        if (items.isNotEmpty()) {
                            text_shop_action.text = getString(R.string.shop_sell_description)
                        } else {
                            text_shop_action.text = getString(R.string.shop_no_sell_description)
                        }
                    }
                }
                UPGRADE -> {
                    if (player != null) {
                        val items =
                            player.weapons.filter { it.level == 1L && it.getUpgradePrice() > -1 }

                        val adapter = MyItemRecyclerViewAdapter(
                            items,
                            frag.getListener(),
                            4
                        )
                        val myView = frag.view as RecyclerView
                        myView.adapter = adapter

                        if (items.isNotEmpty()) {
                            text_shop_action.text = getString(R.string.shop_upgrade_description)
                        } else {
                            text_shop_action.text = getString(R.string.shop_no_upgrade_description)
                        }
                    }
                }
            }
        })
    }

}
