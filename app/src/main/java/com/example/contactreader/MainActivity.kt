package com.example.contactreader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_child.view.*

class MainActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fun loadContacts() {
            var builder = StringBuilder()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS)
                //callback onRequestPermissionsResult
            }
        }




        contact_list.layoutManager = LinearLayoutManager(this)


        btn_read_contact.setOnClickListener{

            val contactList: MutableList<ContactDTO> = ArrayList ()
            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)

            if (contacts != null) {
                while (contacts.moveToNext()){
                    val name = contacts.let { it1 -> contacts.getString(it1.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) }
                    val number = contacts.let { it1 -> contacts.getString(it1.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) }
                    val obj = ContactDTO()
                    if (name != null) {
                        obj.name = name
                    }
                    if (number != null) {
                        obj.number = number
                    }

                    val photo_uri = contacts.getString(contacts.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    if(photo_uri != null){
                        obj.image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photo_uri))
                    }
                    contactList.add(obj)
                }
                contact_list.adapter = ContactAdapter(contactList,this)

                contacts.close()
            }

        }


        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                                grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts()
                } else {
                    //  toast("Permission must be granted in order to display contacts information")
                }
            }
        }
    }



    class ContactAdapter(items : List<ContactDTO>,ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

        private var list = items
        private var context = ctx

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].number
            if(list[position].image != null)
                holder.profile.setImageBitmap(list[position].image)
            else
                holder.profile.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.ic_launcher_round))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_child,parent,false))
        }


        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val name = v.tv_name!!
            val number = v.tv_number!!
            val profile = v.iv_profile!!
        }
    }
}