package com.example.facial.utils

import com.example.facial.data.entity.ModelNote

interface onClickItemListener {
    fun onClick(modelNote: ModelNote, position: Int)
}