package com.amolina.weather.climaflow.ui.show

/**
 * Created by Amolina on 02/02/17.
 */

class ShowEmptyItemModel(private val mListener: ShowEmptyItemViewModelListener) {

    fun onRetryClick() {
        mListener.onRetryClick()
    }

    interface ShowEmptyItemViewModelListener {
        fun onRetryClick()
    }
}
