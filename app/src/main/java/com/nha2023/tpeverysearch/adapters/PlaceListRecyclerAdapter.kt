package com.nha2023.tpeverysearch.adapters

import android.content.Context
import android.content.Intent
import android.net.PlatformVpnProfile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nha2023.tpeverysearch.activities.PlaceUrlActivity
import com.nha2023.tpeverysearch.databinding.RecyclerItemListFragmentBinding
import com.nha2023.tpeverysearch.model.Place

class PlaceListRecyclerAdapter (var context : Context , var documents : MutableList<Place>): Adapter<PlaceListRecyclerAdapter.VH>() {

    //inner class VH(itemView: View) : ViewHolder(itemView)
    inner class VH(val binding : RecyclerItemListFragmentBinding) : ViewHolder(binding.root)  //카드뷰의 아이템들을 다 잡아와야한다. 이렇게 하면 중괄호도 필요없다

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(context),parent,false)//자동추론, parent하면 부모뷰에 맞춘다.
        return VH(binding)
    }

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place : Place = documents[position] //코틀린은 get싫어하니까 []로 쓴다.
        holder.binding.tvPlace.text = place.place_name


//        if (place.road_address_name=="") holder.binding.tvAddress.text = place.address_name
//        else holder.binding.tvAddress.text = place.road_address_name  -> 이렇게 잘 안씀

        holder.binding.tvAddress.text = if (place.road_address_name=="") place.address_name else place.road_address_name
        //삼항연산자인듯 아닌듯~ 이렇게 쓴다. 코틀린 스타일이다.
        holder.binding.tvDistance.text = "${place.distance}m"

        //여기에서 WebView를 출력하자 . 뷰홀더에서 해도 되지만.. 한줄로 끝내고 싶으니까 여기서하자
        holder.binding.root.setOnClickListener {
            val intent : Intent = Intent(context,PlaceUrlActivity::class.java)
            intent.putExtra("place_Url",place.place_url) //바로 start하는게 아니다. 엑스트라값을 넣어주고 실행시켜야한다.
            context.startActivity(intent)
            //finish는 어울리지 않는다.



        }

    }


}


























