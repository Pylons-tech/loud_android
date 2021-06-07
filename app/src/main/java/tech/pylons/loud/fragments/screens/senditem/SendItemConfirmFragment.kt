package tech.pylons.loud.fragments.screens.senditem

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import tech.pylons.loud.R
import tech.pylons.loud.models.Item
import kotlinx.android.synthetic.main.fragment_send_item_confirm.*
import kotlinx.android.synthetic.main.fragment_send_item_confirm.button_send_items

/**
 * A simple [Fragment] subclass.
 */
class SendItemConfirmFragment : Fragment() {
    private val sendItemViewModel: SendItemViewModel by activityViewModels()
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_item_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friend_name.text = sendItemViewModel.friend.name
        friend_address.text = sendItemViewModel.friend.address
        item_name.text = sendItemViewModel.itemIds[0].name

        button_send_items.setOnClickListener {
            listener?.onSendItems(sendItemViewModel.friend.address, sendItemViewModel.itemIds)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onSendItems(
            friendAddress: String,
            itemIds: List<Item>
        )
    }
}