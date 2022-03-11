package com.mayur.bookhub.fragment
import android.app.Activity

import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.provider.Settings
import android.view.*

import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mayur.bookhub.R
import com.mayur.bookhub.adapter.DashboardRecyclerAdapter
import com.mayur.bookhub.model.Book
import com.mayur.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutmanager: RecyclerView.LayoutManager
    lateinit var btncheckInternet:Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar



    val booklist = arrayListOf<String>(
)



    lateinit var recyclerAdapter: DashboardRecyclerAdapter



    val bookInfoList = arrayListOf<Book>(
    )
    var ratingComparator=Comparator<Book>{book1,book2 ->
      if (  book1.bookRating.compareTo(book2.bookRating,true)==0){
         //sorting according to name of the book if rating is same
            book1.bookName.compareTo(book2.bookName,true)
      }else{
          book1.bookRating.compareTo(book2.bookRating,true)
      }

    }



    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)
        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)
// btncheckInternet=view.findViewById(R.id.btncheckInternet)


        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.ProgressBar)
        progressLayout.visibility=View.VISIBLE


/*btncheckInternet.setOnClickListener{
if(ConnectionManager().checkConnectivity(activity as Context))
{
//internet is available


val dialog =AlertDialog.Builder(activity as Context)
dialog.setTitle("Success")
dialog.setMessage("Congratulation Internet Connection found ")
dialog.setPositiveButton("ok"){
text,Listener->
//do nothing
}
dialog.setNegativeButton("cancel"){
text,Listener->
//do nothing
}
dialog.create()
dialog.show()


}



else{


//not connected



val dialog =AlertDialog.Builder(activity as Context)
dialog.setTitle("Error")
dialog.setMessage("Check Internet Connection ")
dialog.setPositiveButton("ok"){
text,Listener->
//do nothing
}
dialog.setNegativeButton("cancel"){
text,Listener->
//do nothing
}
dialog.create()
dialog.show()


}


}


*/
        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"


        if(ConnectionManager().checkConnectivity(activity as Context))
        {


            val jsonObjectRequest= object : JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {


                try{
                    progressLayout.visibility=View.GONE


                    val success=it.getBoolean("success")



                    if (success){
                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val bookJsonObject=data.getJSONObject(i)
                            val bookObject=Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")
                            )



                            bookInfoList.add(bookObject)
                            layoutmanager=LinearLayoutManager(activity)
                            recyclerAdapter= DashboardRecyclerAdapter(activity as Context,bookInfoList)
                            recyclerDashboard.adapter= recyclerAdapter
                            recyclerDashboard.layoutManager=layoutmanager


/*
recyclerDashboard.addItemDecoration(
DividerItemDecoration(recyclerDashboard.context,
(layoutmanager as LinearLayoutManager).orientation)



)



*/


                        }




                    }
                    else{
                        Toast.makeText(activity as Context,"Some Error Occurred!!!",Toast.LENGTH_SHORT).show()
                    }



                }
                catch(e:JSONException){
                    Toast.makeText(activity as Context,"Some unexpected error occurred!!!",Toast.LENGTH_SHORT).show()
                }
// println("response is $it")



            },Response.ErrorListener {
                    if(activity !=null){
                Toast.makeText(activity as Context, "Volley error occurred!!", Toast.LENGTH_SHORT)
                    .show()}
             })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="27fec715cea468"



                    return headers
                }
            }


            queue.add(jsonObjectRequest)


        }else{



            val dialog =AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection Found ")
            dialog.setPositiveButton("Open Settings"){
                    text,Listener->
                val settingsIntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){
                    text,Listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view
    }
    companion object {


        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if (id == R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}

