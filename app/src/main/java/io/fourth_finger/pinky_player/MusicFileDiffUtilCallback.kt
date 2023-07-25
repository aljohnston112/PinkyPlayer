package io.fourth_finger.pinky_player

import androidx.recyclerview.widget.DiffUtil
import io.fourth_finger.music_repository.MusicFile

/**
 * A [DiffUtil.Callback] for [MusicFile]s.
 *
 * @param oldList The old list of [MusicFile]s.
 * @param newList The new list of [MusicFile]s.
 */
class MusicFileDiffUtilCallback(
    private val oldList: List<MusicFile>,
    private val newList: List<MusicFile>,
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}