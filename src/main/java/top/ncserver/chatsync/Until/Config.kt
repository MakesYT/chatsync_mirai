package top.ncserver.chatsync.Until

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value


object Config : AutoSavePluginConfig("Config") {

    var port: Int by value(1111)
    var groupID: Long by value(0L)
    var msgStyle: String by value("みさか(%s%):\"%msg%\"")
    var playerJoinAndQuitMsgStyle: String by value("玩家%s%%msg%")
    var playerListMsgStyle: String by value("当前有%s%位玩家在线\n%msg%")
    var ImgTimer: Boolean by value(true)
    var ImgTimerMsgStyle1: String by value("接收到命令回馈,正在渲染图片")
    var ImgTimerMsgStyle2: String by value("完成,耗时%s%ms,上传中")
    var playerDeathMsgStyle: String by value("%msg%")
    var QQLoadedImg: Boolean by value(false)
    var syncMsg: Boolean by value(true)
    var banCommand: List<String> by value()


}
