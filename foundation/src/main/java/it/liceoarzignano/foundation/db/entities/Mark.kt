package it.liceoarzignano.foundation.db.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import it.liceoarzignano.foundation.extensions.readBoolean
import it.liceoarzignano.foundation.extensions.readDate
import it.liceoarzignano.foundation.extensions.writeBoolean
import it.liceoarzignano.foundation.extensions.writeDate
import java.util.*

@Entity(tableName = "marks",
        indices = [(Index(value = ["date", "uid"], unique = true))])
class Mark : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0

    @ColumnInfo(name = "value")
    var value: Float = 0f

    @ColumnInfo(name = "subject")
    var subject: String = ""

    @ColumnInfo(name = "date")
    var date: Date = Date()

    @ColumnInfo(name = "notes")
    var notes: String = ""

    @ColumnInfo(name = "quarter")
    var quarter: Boolean = false

    @Ignore
    constructor()

    @Ignore
    constructor(input: Parcel) {
        uid = input.readLong()
        value = input.readFloat()
        subject = input.readString()
        date = input.readDate()
        notes = input.readString()
        quarter = input.readBoolean()
    }

    constructor(uid: Long, value: Float, subject: String, date: Date,
                notes: String, quarter: Boolean) {
        this.uid = uid
        this.value = value
        this.subject = subject
        this.date = date
        this.notes = notes
        this.quarter = quarter
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(uid)
        dest.writeFloat(value)
        dest.writeString(subject)
        dest.writeDate(date)
        dest.writeString(notes)
        dest.writeBoolean(quarter)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Mark) {
            return false
        }

        return other.value == value &&
                other.subject == subject &&
                other.date.time == date.time &&
                other.notes == notes &&
                other.quarter == quarter
    }

    override fun hashCode() = super.hashCode() + 1

    companion object CREATOR : Parcelable.Creator<Mark> {

        override fun createFromParcel(source: Parcel) = Mark(source)
        override fun newArray(size: Int) = arrayOfNulls<Mark?>(size)
    }
}