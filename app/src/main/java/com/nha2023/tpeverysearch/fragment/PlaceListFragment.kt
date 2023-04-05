package com.nha2023.tpeverysearch.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nha2023.tpeverysearch.R
import com.nha2023.tpeverysearch.activities.MainActivity
import com.nha2023.tpeverysearch.adapters.PlaceListRecyclerAdapter
import com.nha2023.tpeverysearch.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {

    //val binding : FragmentPlaceListBinding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }
    //이렇게 하면 리턴할때 바인딩.root로 해주면 부모뷰를 잡아준다.
    lateinit var binding: FragmentPlaceListBinding //언제 초기화함? 밑에 onCreateView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(layoutInflater, container,false) //container는 부모사이즈를 인식해서 만들어준다. 지금당장 붙이면 안돼 . return할때 붙이는것이다
        return binding.root //우리로 치면 relative layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //메인 액티비티에 있는 대량의 데이터를 소환! requireActivity().액티비티가 온다
        val ma : MainActivity = requireActivity() as MainActivity

        //중요!
        //if(ma.searchPlaceResponse==null) return //아직 안왔다. return : 이 함수를 더이상 진행하지 마라. 즉 아답터를 붙이지말라는뜻
//        ma.searchPlaceResponse ?: return  //이게 코틀린 스타일이다.
//        binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(),ma.searchPlaceResponse!!.documents) //list프래그먼트가 붙는다.
        //이렇게는 잘 안쓴다. 자바스타일이다.

        ma.searchPlaceResponse?.apply {
            //this는 본인을 말한다. searchPlaceResponse라고한다.
            binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(),documents) //this.documents에서 this는 생략가능하다.
        } //앞에게 널이 아닐때만 .을 실행한다.
        //scope 방식


    }


}