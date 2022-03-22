package com.example.jhkim.data.entities

data class RemoteFlow(
    val type: Remote.Type = Remote.Type.IMAGE,
    val status: Remote.Status = Remote.Status.SUCCESS,
    val keyword: Keyword = Keyword(),
    val isPage: Boolean = false
)