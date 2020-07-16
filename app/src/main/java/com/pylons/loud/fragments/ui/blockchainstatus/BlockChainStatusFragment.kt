package com.pylons.loud.fragments.ui.blockchainstatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.pylons.loud.R
import kotlinx.android.synthetic.main.fragment_block_chain_status.*

class BlockChainStatusFragment : Fragment() {
    private val viewModel: BlockChainStatusViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_block_chain_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lastTx.observe(viewLifecycleOwner, Observer {
            text_last_tx.text = it
        })

        viewModel.blockHeight.observe(viewLifecycleOwner, Observer {
            text_block_height.text = it.toString()
        })
    }

}