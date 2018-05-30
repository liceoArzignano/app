package it.liceoarzignano.foundation.db.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import it.liceoarzignano.foundation.extensions.readDate
import it.liceoarzignano.foundation.extensions.writeDate
import java.util.*

@Entity(tableName = "pings",
        indices = [(Index(value = ["date", "uid"], unique = true))])
class Ping : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0

    @ColumnInfo(name = "title")
    var name: String = ""

    @ColumnInfo(name = "message")
    var message: String = ""

    @ColumnInfo(name = "url")
    var url: String = ""

    @ColumnInfo(name = "date")
    var date: Date = Date()

    @Ignore
    constructor()

    @Ignore
    constructor(input: Parcel) {
        uid = input.readLong()
        name = input.readString()
        message = input.readString()
        url = input.readString()
        date = input.readDate()
    }

    constructor(uid: Long, name: String, message: String, url: String, date: Date) {
        this.uid = uid
        this.name = name
        this.message = message
        this.url = url
        this.date = date
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(uid)
        dest.writeString(name)
        dest.writeString(message)
        dest.writeString(url)
        dest.writeDate(date)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Ping) {
            return false
        }

        return other.name == name &&
                other.message == message &&
                other.url == url &&
                other.date.time == date.time
    }

    override fun hashCode() = super.hashCode() + 1

    companion object CREATOR : Parcelable.Creator<Ping> {

        override fun createFromParcel(source: Parcel) = Ping(source)
        override fun newArray(size: Int) = arrayOfNulls<Ping?>(size)
    }
}