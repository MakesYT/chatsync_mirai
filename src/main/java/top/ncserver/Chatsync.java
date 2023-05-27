package top.ncserver;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.extension.protocol.StringProtocol;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import top.ncserver.chatsync.Until.ChatsyncCommand;
import top.ncserver.chatsync.Until.Config;
import top.ncserver.chatsync.Until.TextToImg;
import top.ncserver.chatsync.V2.ClientManager;
import top.ncserver.chatsync.V2.MsgTools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Chatsync extends JavaPlugin {
    public static final Chatsync INSTANCE = new Chatsync();

    public static Bot bot;
    public static Chatsync chatsync;

    //public static AioSession session;
    //public static MessageChain ServerOfflineMsg = MiraiCode.deserializeMiraiCode(Config.INSTANCE.getServerOfflineMsg());
    //public boolean isConnected = false;
    //public static MessageChain ServerOnlineMsg = MiraiCode.deserializeMiraiCode(Config.INSTANCE.getServerOnlineMsg());
    private Chatsync() {
        super(new JvmPluginDescriptionBuilder("top.ncserver.chatsync", "1.1.1")
                .name("chatsync")
                .author("makesyt")
                .build());
    }

    @Override
    public void onEnable() {
        chatsync = this;
        CommandManager.INSTANCE.registerCommand(new ChatsyncCommand(), true);
        this.reloadPluginConfig(Config.INSTANCE);
        getLogger().info("Plugin loaded!");
        bot = null;
        getLogger().info("机器人加载完成,开始在127.0.0.1:" + Config.INSTANCE.getPort() + "创建socke服务器");
        MsgTools.listenerInit();
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, (event) -> {
            bot = Bot.getInstances().get(0);
            if (Config.INSTANCE.getQQLoadedImg()) {
                try {
                    File file = TextToImg.toImg("消息同步QQ侧已加载,当前JDK版本:" + System.getProperty("java.version"));
                    MsgTools.QQsendImg(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        });
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, (event) -> {
            bot= null;
        });
        if ( Bot.getInstances().size()>0) {
            bot = Bot.getInstances().get(0);
            if (Config.INSTANCE.getQQLoadedImg()) {
                try {
                    File file = TextToImg.toImg("消息同步QQ侧已加载");
                    MsgTools.QQsendImg(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        }

        new Thread(() -> {
            AbstractMessageProcessor<String> processor = new AbstractMessageProcessor<String>() {
                @Override
                public void process0(AioSession aioSession, String s) {
                    MsgTools.msgRead(aioSession, ClientManager.clientName.get(aioSession.getSessionID()) != null ? ClientManager.clientName.get(aioSession.getSessionID()) : "未命名服务器", s);
                }

                @Override
                public void stateEvent0(AioSession aioSession, StateMachineEnum stateMachineEnum, Throwable throwable) {
                    if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                        ClientManager.clients.add(aioSession);
                        //session = aioSession;

                        {
                            Map<String, Object> msg1 = new HashMap<>();
                            msg1.put("type", "init");
                            msg1.put("command", Config.INSTANCE.getSyncMsg());
                            JSONObject jo = new JSONObject(msg1);
                            Chatsync.chatsync.getLogger().info(jo.toJSONString());
                            try {
                                WriteBuffer writeBuffer = aioSession.writeBuffer();
                                byte[] content = jo.toJSONString().getBytes();
                                writeBuffer.writeInt(content.length);
                                writeBuffer.write(content);
                                writeBuffer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }
                    }else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)){
                        if (bot!=null && Config.INSTANCE.getNotifyServerState()){
                            MsgTools.QQsendMsgMessageChain(MiraiCode.deserializeMiraiCode(Config.INSTANCE.getServerOfflineMsg().replaceAll("%server%", ClientManager.clientName.get(aioSession.getSessionID()) != null ? ClientManager.clientName.get(aioSession.getSessionID()) : "未命名服务器")));
                        }
                        ClientManager.clients.removeIf(client -> Objects.equals(client.getSessionID(), aioSession.getSessionID()));
                    }
                }
            };

            AioQuickServer server = new AioQuickServer(Config.INSTANCE.getPort(), new StringProtocol(), processor);
            try {
                server.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }, "clientListener").start();
    }

}