package name.eraxillan.ongoingschedule

import android.os.Parcel
import android.os.Parcelable

class TaskList constructor (val name: String, val tasks: ArrayList<String> = ArrayList())
    : Parcelable
{
    private val TAG = TaskList::class.java.simpleName

    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.createStringArrayList() ?: ArrayList()
    )

    override fun writeToParcel(destination: Parcel, flags: Int) {

        destination.writeString(name)
        destination.writeStringList(tasks)
    }

    override fun describeContents() = 0

    /*
    NOTE: static methods don’t exist in Kotlin.
    Instead, you create a companion object meeting the same requirements
    and override the appropriate functions within that object.
    */
    companion object CREATOR : Parcelable.Creator<TaskList> {
        override fun createFromParcel(source: Parcel): TaskList = TaskList(source)

        override fun newArray(size: Int): Array<TaskList?> = arrayOfNulls(size)
    }
}