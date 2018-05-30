package it.liceoarzignano.foundation.db.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import it.liceoarzignano.foundation.extensions.readDate
import it.liceoarzignano.foundation.extensions.writeDate
import java.util.*

@Entity(tableName = "notes")
class Note : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "content")
    var content: String = ""

    @ColumnInfo(name = "date")
    var date: Date = Date()

    @Tag
    @ColumnInfo(name = "tag")
    var tag: Long = 0

    @Ignore
    constructor()

    @Ignore
    constructor(input: Parcel) {
        uid = input.readLong()
        title = input.readString()
        content = input.readString()
        date = input.readDate()
        tag = input.readLong()
    }

    constructor(uid: Long, title: String, content: String, date: Date, @Tag tag: Long) {
        this.uid = uid
        this.title = title
        this.content = content
        this.date = date
        this.tag = tag
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(uid)
        dest.writeString(title)
        dest.writeString(content)
        dest.writeDate(date)
        dest.writeLong(tag)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Note) {
            return false
        }

        return other.title == title &&
                other.content == content &&
                other.date.time == date.time &&
                other.tag == tag
    }

    override fun hashCode() = super.hashCode() + 1

    companion object CREATOR : Parcelable.Creator<Note>{

        @IntDef(GENERIC, TEST, HOMEWORK, BIRTHDAY, REMEMBER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Tag
        const val GENERIC = 0
        const val TEST = 1
        const val HOMEWORK = 2
        const val BIRTHDAY = 3
        const val REMEMBER = 4

        override fun createFromParcel(source: Parcel) = Note(source)
        override fun newArray(size: Int) = arrayOfNulls<Note?>(size)
    }
}