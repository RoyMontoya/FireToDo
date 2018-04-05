package com.firebase.test.firetodo.listeners

/**
 * Created by Roy on 4/5/18.
 */
interface ItemRowListener {

    fun modifyItemState(itemObjectId: String, isDone: Boolean)
    fun onItemDelete(itemObjectId: String)

}