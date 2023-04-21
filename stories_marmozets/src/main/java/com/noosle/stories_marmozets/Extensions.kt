package com.noosle.marmozets

fun runnable(body: Runnable.(Runnable) -> Unit) = object : Runnable {
    override fun run() {
        this.body(this)
    }
}