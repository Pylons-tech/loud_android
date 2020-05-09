package com.pylons.loud.fragments.ForestScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.pylons.loud.R
import com.pylons.loud.activities.GameScreenActivity
import com.pylons.loud.models.Fight
import kotlinx.android.synthetic.main.fragment_forest_fight_preview.*
import java.util.logging.Logger

/**
 * A simple [Fragment] subclass.
 */
class ForestFightPreviewFragment : Fragment() {
    private val Log = Logger.getLogger(ForestFightPreviewFragment::class.java.name)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forest_fight_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model: GameScreenActivity.SharedViewModel by activityViewModels()
        model.getFightPreview().observe(viewLifecycleOwner, Observer<Fight> { fight ->

            val activeWeapon = model.getPlayer().value?.getActiveWeapon()

            text_header.text = fight.name

            val textView2 = TextView(context)
            textView2.text = "Reward ${fight.reward}"
            layout_fight_preview.addView(textView2)

            if (fight.id != 1) {
                val textView3 = TextView(context)
                textView3.text = "Enemy info (HP: ${fight.hp}, Attack: ${fight.attack})"
                layout_fight_preview.addView(textView3)
            }

            fight.conditions.forEach {
                val textView = TextView(context)
                textView.text = it
                layout_fight_preview.addView(textView)
            }

            if (activeWeapon != null) {
                val textView5 = TextView(context)
                textView5.text = "Carry: ${activeWeapon.name} Lv${activeWeapon.level} attack=${activeWeapon.attack}"
                layout_fight_preview.addView(textView5)
            }
        })

        button_fight.setOnClickListener {
        }
    }
}
