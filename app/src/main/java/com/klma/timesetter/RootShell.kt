package com.klma.timesetter

object RootShell {
    val exec = Runtime.getRuntime().exec("su")

    fun runCmd(cmd: String) {
        exec.outputStream.write("$cmd \n".toByteArray())
    }
}