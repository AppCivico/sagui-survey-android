package com.eokoe.sagui.data.adapters

import android.os.Parcel
import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.entities.Image
import io.realm.RealmList
import paperparcel.TypeAdapter

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
class CommentListAdapter : TypeAdapter<RealmList<Comment>> {
    override fun readFromParcel(source: Parcel): RealmList<Comment> {
        val list: List<Comment> = ArrayList()
        source.readTypedList(list, Comment.CREATOR)
        return RealmList(*list.toTypedArray())
    }

    override fun writeToParcel(value: RealmList<Comment>, dest: Parcel, flags: Int) {
        dest.writeTypedList(value)
    }
}