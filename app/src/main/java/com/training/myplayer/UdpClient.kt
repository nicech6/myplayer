package com.training.myplayer

import com.cuihai.framwork.util.NetworkUtils
import com.cuihai.framwork.utilv2.ExecUtil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object UdpClient {

    fun start() {

        ExecUtil.execute {
            /*
         * 向服务器端发送数据
         */
            // 1.定义服务器的地址、端口号、数据
            /*
         * 向服务器端发送数据
         */
            // 1.定义服务器的地址、端口号、数据
            val ip = NetworkUtils.getBroadcastIpAddress()
            val address: InetAddress = InetAddress.getByName(ip)
            val port = 3000
            val data = ip.toByteArray()
            // 2.创建数据报，包含发送的数据信息
            // 2.创建数据报，包含发送的数据信息
            val packet = DatagramPacket(data, data.size, address, port)
            // 3.创建DatagramSocket对象
            // 3.创建DatagramSocket对象
            val socket = DatagramSocket()
            // 4.向服务器端发送数据报
            // 4.向服务器端发送数据报
            socket.send(packet)

            /*
         * 接收服务器端响应的数据
         */
            // 1.创建数据报，用于接收服务器端响应的数据
            /*
         * 接收服务器端响应的数据
         */
            // 1.创建数据报，用于接收服务器端响应的数据
//            val data2 = ByteArray(1024)
//            val packet2 = DatagramPacket(data2, data2.size)
//            // 2.接收服务器响应的数据
//            // 2.接收服务器响应的数据
//            socket.receive(packet2)
//            // 3.读取数据
//            // 3.读取数据
//            val reply = String(data2, 0, packet2.length)
//            println("我是客户端，服务器说：$reply")
//            // 4.关闭资源
//            // 4.关闭资源
//            socket.close()
        }
    }
}