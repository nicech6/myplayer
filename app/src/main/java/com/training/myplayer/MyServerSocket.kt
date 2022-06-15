package com.training.myplayer


import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.cuihai.framwork.utilv2.ExecUtil
import java.io.*
import java.net.ServerSocket
import java.net.Socket

/*
 *
 * Copyright (C) 2022 NIO Inc
 *
 * Ver   Date        Author    Desc
 *
 * V1.0  2022/6/11  hai.cui  Add for
 *
 */   object MyServerSocket {

    private var socket: Socket? = null

    // 定义保存所有Socket的集合
    var socketList = ArrayList<Socket>()
    private var dos: ObjectOutputStream? = null
    private var dis: ObjectInputStream? = null

    fun cancel() {
        socket?.close()
    }

    class ClientThread(var socket: Socket) : Thread() {

        override fun run() {
            super.run()
            try {
                dos = ObjectOutputStream(socket.getOutputStream()) //获取输出流（准备从服务器给其他的客户端发消息）
                dis = ObjectInputStream(socket.getInputStream()) //接收客户端发过来的消息（输入流）
                while (true) { //使服务器无限循环
                    var recMsg: Any?
                    if (dis != null && dis?.readObject() != null) {
                        recMsg = dis?.readObject() as PlayerBean
                        println("-----收到了$recMsg") //显示：收到一条消息+“传入的消息”
                        ExecUtil.executeUI {
                            val intent = Intent(MyApp.getContext(), MainActivity::class.java)
                            intent.putExtra("play", recMsg)
                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                            MyApp.getContext().startActivity(intent)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun launch() {
        ExecUtil.execute {
            val server = ServerSocket(2000)
            while (true) {
                //接收客户端的连接
                socket = server.accept()
                if (socket?.isConnected == true) {
                    socketList.add(socket!!)
                }
                val handler = android.os.Handler(Looper.getMainLooper())
                handler.post {
                    Toast.makeText(MyApp.getContext(), "客户端已连接", Toast.LENGTH_SHORT).show()
                }
                Log.i("------", "客户端已连接" + socket?.inetAddress)
                ClientThread(socket!!).start()
            }
        }
    }

    fun play(currentPositionMS: Long) {
        ExecUtil.execute {
            val bean = PlayerBean().apply {
                url = ""
                position = currentPositionMS
            }
            dos?.writeObject(bean)
            dos?.flush()
        }
    }
}