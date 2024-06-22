/*
 * Copyright 2020-2024 RW-HPS Team and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/RW-HPS/RW-HPS/blob/master/LICENSE
 */

package net.rwhps.server.net.handler.tcp

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.util.AttributeKey
import net.rwhps.server.data.global.Data
import net.rwhps.server.func.StrCons
import net.rwhps.server.io.packet.RconPacket
import net.rwhps.server.net.code.tcp.RconPacketDecoder
import net.rwhps.server.net.code.tcp.RconPacketEncoder
import net.rwhps.server.net.core.AbstractNet
import net.rwhps.server.net.core.INetServerHandler

/**
 * @author Dr (dr@der.kim)
 */
@Sharable
class StartRcon: AbstractNet() {

    val rChannelData = AttributeKey.valueOf<Boolean>("R-CON Authenticated")!!

    init {
        init(object: INetServerHandler(this) {
            override fun channelRead0(ctx: ChannelHandlerContext, msg: Any?) {
                val rconRequest = msg as RconPacket

                if (ctx.channel().attr(rChannelData).get()) {
                    ctx.channel().attr(rChannelData).set(false)
                }
                if (rconRequest.type == RconPacket.Companion.RconConstant.SERVERDATA_AUTH) {
                    if (ctx.channel().attr(rChannelData).get() || rconRequest.body.isBlank()) {
                        ctx.disconnect()
                        return
                    }

                    ctx.writeAndFlush(RconPacket(rconRequest.id, RconPacket.Companion.RconConstant.SERVERDATA_RESPONSE_VALUE, ""))
                    ctx.writeAndFlush(RconPacket(if (Data.config.rconPasswd == rconRequest.body) rconRequest.id else -1, RconPacket.Companion.RconConstant.SERVERDATA_AUTH_RESPONSE, ""))
                    ctx.channel().attr(rChannelData).set(true)

                    return
                }

                val send = StringBuilder()
                Data.SERVER_COMMAND.handleMessage(rconRequest.body, StrCons { obj: String -> send.append(obj) })
                ctx.writeAndFlush(RconPacket(rconRequest.id, RconPacket.Companion.RconConstant.SERVERDATA_RESPONSE_VALUE, send.toString()))
            }

            @Throws(Exception::class)
            override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
                println("An error happened while handling Source RCON packet!$cause")
            }
        })
    }

    override fun addPacketDecoderAndEncoder(channelPipeline: ChannelPipeline) {
        channelPipeline.addLast(RconPacketDecoder())
        channelPipeline.addLast(RconPacketEncoder())
    }

    @Throws(Exception::class)
    public override fun initChannel(socketChannel: SocketChannel) {
        addPacketDecoderAndEncoder(socketChannel.pipeline())
        addNewServerHandler(socketChannel.pipeline())
    }
}