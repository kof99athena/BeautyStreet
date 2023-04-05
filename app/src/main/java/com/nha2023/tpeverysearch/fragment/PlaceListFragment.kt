package com.nha2023.tpeverysearch.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nha2023.tpeverysearch.R
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
}