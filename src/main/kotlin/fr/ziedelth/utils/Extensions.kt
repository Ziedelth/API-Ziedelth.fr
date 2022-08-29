package fr.ziedelth.utils

import com.google.gson.Gson

fun Any.toJSONString(): String = Gson().toJson(this).trim()
fun Any.toBrotly(): String = Encode.brotli(this.toJSONString()).trim()
fun Any.toGZIP(): String = Encode.gzip(this.toJSONString()).trim()

fun Double.toString(number: Int) = String.format("%.${number}f", this)