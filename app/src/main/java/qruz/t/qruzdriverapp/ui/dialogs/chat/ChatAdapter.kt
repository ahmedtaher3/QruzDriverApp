package qruz.t.qruzdriverapp.ui.dialogs.chat

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qruz.BusinessTripChatMessagesQuery
import de.hdodenhof.circleimageview.CircleImageView
import qruz.t.qruzdriverapp.R
import qruz.t.qruzdriverapp.data.local.DataManager
import java.util.*
import kotlin.jvm.internal.Intrinsics

class ChatAdapter(
    private var context: Activity
    , private val dataManager: DataManager
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val TYPE_ADMIN = 1
    private val TYPE_USER = 0
    private var users = ArrayList<BusinessTripChatMessagesQuery.BusinessTripChatMessage>()


    override fun getItemViewType(position: Int): Int {


        val id = users[position].sender().id()
        val user = dataManager.user
        return if (!Intrinsics.areEqual(id as Any?, user.getId() as Any)) {
            TYPE_USER
        } else this.TYPE_ADMIN


    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ChatAdapter.ViewHolder {

        if (i == TYPE_ADMIN) {
            return ViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.admin_chat_item, viewGroup, false)
            )

        } else {
            return ViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.user_chat_item, viewGroup, false)
            )

        }

    }


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val model = users[i]



        viewHolder.msg_body?.text = model.message()
        viewHolder.msg_date?.text = model.time()
        viewHolder.msg_user_name?.text = model.sender().name()
        val id = users[i].sender().id()
        val user = dataManager.user
        Intrinsics.checkExpressionValueIsNotNull(user, "dataManager.user")
        if (Intrinsics.areEqual(id as Any?, user.id as Any)) {
            viewHolder.msg_user_name?.visibility = View.GONE
        } else {
            viewHolder.msg_user_name?.visibility = View.VISIBLE
        }


    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setMessages(users: List<BusinessTripChatMessagesQuery.BusinessTripChatMessage>) {
        this.users = ArrayList<BusinessTripChatMessagesQuery.BusinessTripChatMessage>(users)
        notifyDataSetChanged()
    }


    fun getMessages(): ArrayList<BusinessTripChatMessagesQuery.BusinessTripChatMessage> {
        return this.users
    }

    fun addItem(chatMessage: BusinessTripChatMessagesQuery.BusinessTripChatMessage?) {
        users.add(chatMessage!!)
        notifyDataSetChanged()
    }


    public class ViewHolder(private val parent: View) : RecyclerView.ViewHolder(parent) {

        var msg_body: TextView? = null
        var msg_date: TextView? = null
        var msg_user_image: CircleImageView? = null
        var msg_user_name: TextView? = null
        var container: View? = null

        init {
            container = parent
            msg_user_image = parent.findViewById(R.id.msg_user_image)
            msg_body = parent.findViewById(R.id.msg_body)
            msg_date = parent.findViewById(R.id.msg_date)
            msg_user_name = parent.findViewById(R.id.msg_user_name)

        }

    }


}