package top.ncserver.chatsync.Until

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value


object Config : AutoSavePluginConfig("Config") {

    var port: Int by value(1111)
    var groupID: Long by value(0L)
    var msgStyle: String by value("[%server%]:みさか(%s%):\"%msg%\"")
    var playerJoinAndQuitMsgStyle: String by value("[%server%]玩家%s%%msg%")
    var playerListMsgStyle: String by value("[%server%]当前有%s%位玩家在线\n%msg%")
    var SendImg: Boolean by value(true)
    var ReceiveImg: Boolean by value(true)
    var ImgTimer: Boolean by value(true)
    var ImgTimerMsgStyle1: String by value("接收到命令回馈,正在渲染图片")
    var ImgTimerMsgStyle2: String by value("完成,耗时%s%ms,上传中")
    var playerDeathMsgStyle: String by value("[%server%]:%msg%")
    var NotifyServerState: Boolean by value(true)
    var ServerOfflineMsg: String by value("服务器%server%没了,等重启吧[mirai:face:173][mirai:face:174]")
    var ServerOnlineMsg: String by value("服务器%server%有了,别问我几个月的,我也不知道[mirai:face:178]")
    var QQLoadedImg: Boolean by value(false)
    var syncMsg: Boolean by value(true)
    var UnconditionalAutoSync: Boolean by value(true)
    var AutoSyncPrefix: String by value("#")
    var playerDeathMsg: Boolean by value(true)
    var playerJoinAndQuitMsg: Boolean by value(true)
    var banCommand: List<String> by value()


}
