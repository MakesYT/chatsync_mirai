package top.ncserver.chatsync.V2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kotlin.text.Charsets;
import net.coobird.thumbnailator.Thumbnails;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import top.ncserver.Chatsync;
import top.ncserver.chatsync.Until.ColorCodeCulling;
import top.ncserver.chatsync.Until.Config;
import top.ncserver.chatsync.Until.TextToImg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.ncserver.Chatsync.bot;
import static top.ncserver.chatsync.Until.ColorCodeCulling.CullColorCode;

public class MsgTools {
    static Pattern p = Pattern.compile("[(/+)\u4e00-\u9fa5(+*)]");
    static HttpClient client;

    public static void listenerInit() {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .cookieHandler(new CookieManager())
                .build();
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
            if (Config.INSTANCE.getGroupID() == 0L && event.getSender().getPermission().getLevel() >= 1 && event.getMessage().contentToString().equals("/chatsync bind this")) {
                Config.INSTANCE.setGroupID(event.getGroup().getId());
                QQsendMsg("消息同步已绑定到此群");

            } else if (!ClientManager.clientName.isEmpty()) {
                if (ClientManager.groupIdToClient.containsKey(event.getGroup().getId())) {
                    if (Config.INSTANCE.getUnconditionalAutoSync() || event.getMessage().contentToString().startsWith(Config.INSTANCE.getAutoSyncPrefix())) {
                        Map<String, Object> msg1 = new HashMap<>();
                        String msgString = event.getMessage().contentToString();
                        // msgString=msgString.replaceAll("\")
                        //At at=new At()
                        Matcher m = p.matcher(msgString.substring(1));
                        if (msgString.equals("/recon")) {
                            QQsendMsg("消息同步正在重新连接.....");
                            ClientManager.clientName.forEach( (k, v) -> {
                                v.aioSession.close();
                                    }
                            );
                        } else if (msgString.startsWith("/") && !m.lookingAt()) {
                            if (event.getSender().getPermission().getLevel() >= 1 || msgString.equals("/ls")) {
                                boolean baned = false;
                                String msg = event.getMessage().contentToString();
                                String command = msg.replaceFirst("/", "");
                                for (String s : Config.INSTANCE.getBanCommand()) {
                                    if (command.matches(s)) {
                                        baned = true;
                                        break;
                                    }
                                }
                                if (!baned) {
                                    msg1.put("type", "command");
                                    msg1.put("sender", event.getSenderName() + "(" + event.getSender().getId() + ")");
                                    msg1.put("command", msg);
                                    JSONObject jo = new JSONObject(msg1);
                                    Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                    for (ClientManager.ClientInfo clientInfo : ClientManager.groupIdToClient.get(event.getGroup().getId())) {
                                        msgSend(clientInfo.aioSession, jo.toJSONString());
                                    }

                                } else QQsendMsg("该命令已被屏蔽");

                            } else if (msgString.contains("/LS") || msgString.contains("/IS") || msgString.contains("/Is")) {
                                QQsendMsg("PS:正确的命令为/ls(均为小写.其大写形式为/LS)");

                            } else {
                                QQsendMsg("你无权执行" + msgString);

                            }
                        } else  {
                            StringBuilder sb = new StringBuilder();
                            JSONArray msg = JSON.parseArray(MessageChain.serializeToJsonString(event.getMessage()));
                            if (event.getMessage().contains(QuoteReply.Key)) {
                                QuoteReply quote = event.getMessage().get(QuoteReply.Key);
                                At at = new At(quote.getSource().getFromId());
                                StringBuilder msgBuilder = new StringBuilder();
                                msgBuilder.append("\u00A75 回复了\n\u00A73");
                                msgBuilder.append(at.getDisplay(event.getGroup()).replaceFirst("@", "["));
                                msgBuilder.append("]:");
                                msgBuilder.append(quote.getSource().getOriginalMessage());
                                sb.append("\u2191---");
                                msg1.put("type", "remsg");
                                msg1.put("permission", event.getSender().getPermission().getLevel());
                                msg1.put("sender", event.getSenderName());
                                msg1.put("msg", msgBuilder);
                                JSONObject jo = new JSONObject(msg1);
                                Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                for (ClientManager.ClientInfo clientInfo : ClientManager.groupIdToClient.get(event.getGroup().getId())) {
                                    msgSend(clientInfo.aioSession, jo.toJSONString());
                                }

                            }
                            JSONArray originalMessage = ((JSONObject) msg.get(0)).getJSONArray("originalMessage");
                            msg1.clear();

                            for (Object o : originalMessage) {
                                //System.out.println(((JSONObject) o).toJSONString());
                                switch (((JSONObject) o).getString("type")) {
                                    case "PlainText": {
                                        sb.append(((JSONObject) o).getString("content"));
                                        break;
                                    }
                                    case "At": {
                                        At at = new At(((JSONObject) o).getLong("target"));
                                        sb.append(at.getDisplay(event.getGroup()));
                                        break;
                                    }
                                    case "AtAll": {
                                        sb.append("@全体成员");
                                        break;
                                    }
                                    case "Image": {
                                        if (Config.INSTANCE.getSendImg()) {
                                            Image img = Image.fromId(((JSONObject) o).getString("imageId"));
                                            HttpRequest request = HttpRequest.newBuilder()
                                                    .version(HttpClient.Version.HTTP_1_1)
                                                    .GET()
                                                    .timeout(Duration.ofSeconds(2))
                                                    .uri(URI.create(Image.queryUrl(img)))
                                                    .build();
                                            try {
                                                //System.out.println(Image.queryUrl(img));
                                                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                                                msg1.put("type", "img");
                                                msg1.put("permission", event.getSender().getPermission().getLevel());
                                                msg1.put("sender", event.getSenderName());

                                                BufferedImage image = Thumbnails.of(ImageIO.read(new ByteArrayInputStream(response.body())))
                                                        .scale(1f) //按比例放大缩小 和size() 必须使用一个 不然会报错
                                                        .outputQuality(0.3f)    //输出的图片质量  0~1 之间,否则报错
                                                        .asBufferedImage();
                                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                                ImageIO.write(image, "png", os);
                                                Chatsync.chatsync.getLogger().info(new JSONObject(msg1).toJSONString());
                                                msg1.put("data", Base64.getEncoder().encodeToString(os.toByteArray()));
                                                JSONObject jo = new JSONObject(msg1);
                                                //Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                                for (ClientManager.ClientInfo clientInfo : ClientManager.groupIdToClient.get(event.getGroup().getId())) {
                                                    msgSend(clientInfo.aioSession, jo.toJSONString());
                                                }

                                            } catch (IOException | InterruptedException e) {
                                                Chatsync.chatsync.getLogger().info("图片请求失败,地址:" + Image.queryUrl(img));
                                                MsgTools.QQsendMsg("图片请求失败,地址:" + Image.queryUrl(img));
                                            }
                                            break;
                                        } else {
                                            sb.append("[图片]");
                                            break;
                                        }
                                    }
                                    case "QuoteReply": {
                                        break;
                                    }
                                    default: {

                                        MessageChain messageChain = MessageChain.deserializeFromJsonString("[" + ((JSONObject) o).toJSONString() + "]");
                                        sb.append(messageChain.get(0).contentToString());
                                    }
                                }
                            }
                            if (sb.length() > 0&&(Config.INSTANCE.getSyncMsg())) {
                                msg1.clear();
                                msg1.put("type", "msg");
                                msg1.put("permission", event.getSender().getPermission().getLevel());
                                msg1.put("sender", event.getSenderName());
                                msg1.put("msg", sb);
                                JSONObject jo = new JSONObject(msg1);
                                Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                for (ClientManager.ClientInfo clientInfo : ClientManager.groupIdToClient.get(event.getGroup().getId())) {
                                    msgSend(clientInfo.aioSession, jo.toJSONString());
                                }

                            }


                        }

                    }
                }
            } else {
                if (Config.INSTANCE.getGroupID() == 0L && event.getSender().getPermission().getLevel() >= 1 && event.getMessage().contentToString().equals("/chatsync bind this")) {
                    Config.INSTANCE.setGroupID(event.getGroup().getId());
                    MsgTools.QQsendMsg("消息同步已绑定到此群");

                } else if (event.getGroup().getId() == Config.INSTANCE.getGroupID()) {
                    String msg = event.getMessage().contentToString();
                    if (msg.contains("/ls") || msg.contains("/list")) {
                        MsgTools.QQsendMsg("抱歉,服务器处于离线状态");
                    } else if (msg.contains("/LS") || msg.contains("/IS") || msg.contains("/Is")) {
                        MsgTools.QQsendMsg("抱歉,服务器处于离线状态\nPS:正确的命令为/ls(均为小写.其大写形式为/LS)");
                    }
                }
            }

        });
    }

    public static void msgRead(AioSession aioSession, String serverName, String msgJ) {
        try {


            if (bot != null) {
                //System.out.println(msgJ);
                JSONObject jsonObject = JSONObject.parseObject(msgJ);
                {
                    switch (jsonObject.getString("type")) {
                        case "msg":
                            if (Config.INSTANCE.getSyncMsg()) {
                                System.out.println("[" + jsonObject.getString("sender") + "]:" + jsonObject.getString("msg"));
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(ColorCodeCulling.CullColorCode(Config.INSTANCE.getMsgStyle().replaceAll("%s%", jsonObject.getString("sender")).replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName))));
                            }
                            break;
                        case "img": {
                            if (Config.INSTANCE.getGroupID() != 0L && Config.INSTANCE.getReceiveImg()) {
                                String uuid = UUID.randomUUID().toString();
                                Base64.Decoder decoder = Base64.getDecoder();
                                byte[] b = decoder.decode(jsonObject.getString("msg"));
                                // 处理数据
                                for (int i = 0; i < b.length; ++i) {
                                    if (b[i] < 0) {
                                        b[i] += 256;
                                    }
                                }
                                BufferedImage image = ImageIO.read(new ByteArrayInputStream(b));
                                File temp = new File("temp");
                                if (!temp.exists()) {
                                    temp.mkdir();
                                }
                                File outfile = new File("temp" + File.separator + uuid + ".png");
                                ImageIO.write(image, "png", outfile);
                                try (ExternalResource resource = ExternalResource.create(outfile)) { // 使用文件 file
                                    Image img = ExternalResource.uploadAsImage(resource, bot.getGroup(Config.INSTANCE.getGroupID())); // 用来上传图片
                                    System.out.println("[" + jsonObject.getString("player") + "]的图片消息");
                                    MessageChain messageChain = new MessageChainBuilder().append(new PlainText("[" + jsonObject.getString("player") + "]:")).append(img).asMessageChain();
                                    QQsendMsgMessageChain(aioSession.getSessionID(),messageChain);
                                }
                                outfile.delete();
                                System.gc();
                            }
                            break;
                        }
                        case "playerJoinAndQuit":
                            if (Config.INSTANCE.getSyncMsg() && Config.INSTANCE.getPlayerJoinAndQuitMsg()) {
                                System.out.println(Config.INSTANCE.getPlayerJoinAndQuitMsgStyle().replaceAll("%s%", CullColorCode(jsonObject.getString("player"))).replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName));
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(Config.INSTANCE.getPlayerJoinAndQuitMsgStyle().replaceAll("%s%", CullColorCode(jsonObject.getString("player"))).replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName)));
                            }
                            //QQsendMsg("玩家"+CullColorCode(jsonObject.getString("player"))+jsonObject.getString("msg"));
                            break;
                        case "playerList":
                            if (!jsonObject.containsKey("online")){
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(ColorCodeCulling.CullColorCode(Config.INSTANCE.getPlayerListMsgStyle().replaceAll("有%s%位", "无").replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName))));
                            }else {
                                System.out.println(ColorCodeCulling.CullColorCode(Config.INSTANCE.getPlayerListMsgStyle().replaceAll("%s%", jsonObject.getString("online")).replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName)));
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(ColorCodeCulling.CullColorCode(Config.INSTANCE.getPlayerListMsgStyle().replaceAll("%s%", jsonObject.getString("online")).replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName))));
                            }
                            //QQsendMsg("当前有"+jsonObject.getString("online")+"位玩家在线\n"+jsonObject.getString("msg"));
                            break;
                        case "command":
                            if (Config.INSTANCE.getImgTimer()) {
                                QQsendMsg(Config.INSTANCE.getImgTimerMsgStyle1());
                                long start = System.currentTimeMillis();
                                InputStream file = TextToImg.toImg(jsonObject.getString("command"));
                                long finish = System.currentTimeMillis();
                                long timeElapsed = finish - start;
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(Config.INSTANCE.getImgTimerMsgStyle2().replaceAll("%s%", String.valueOf(timeElapsed))));

                                QQsendImg(aioSession.getSessionID(),file);
                                System.gc();
                            } else {
                                InputStream file = TextToImg.toImg(jsonObject.getString("command"));
                                QQsendImg(aioSession.getSessionID(),file);
                                System.gc();
                            }


                            break;
                        case "serverCommand":
                            QQsendMsg("注意服务器执行：" + jsonObject.getString("command") + "\n注意服务器安全");
                            break;
                        case "playerDeath":
                        case "obRe":
                            if (Config.INSTANCE.getSyncMsg() && Config.INSTANCE.getPlayerDeathMsg())
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(CullColorCode(Config.INSTANCE.getPlayerDeathMsgStyle().replaceAll("%msg%", jsonObject.getString("msg")).replaceAll("%server%", serverName))));
                            // QQsendMsg(CullColorCode(jsonObject.getString("msg")));
                            break;
                        case "init": {
                            ClientManager.ClientInfo clientInfo=new ClientManager.ClientInfo();
                            clientInfo.setName(jsonObject.getString("name"));
                            if (jsonObject.containsKey("groupId"))
                                clientInfo.setGroupId(jsonObject.getLongValue("groupId"));
                            ClientManager.clientName.put(aioSession.getSessionID(),clientInfo );
                            if (clientInfo.GroupId==0L){
                                if (ClientManager.groupIdToClient.containsKey(0L)){
                                    ClientManager.groupIdToClient.get(0L).add(clientInfo);
                                }else {
                                    LinkedList<ClientManager.ClientInfo> value = new LinkedList<>();
                                    value.add(clientInfo);
                                    ClientManager.groupIdToClient.put(0L, value);
                                }
                            }
                            if (bot != null && Config.INSTANCE.getNotifyServerState()) {
                                QQsendMsgMessageChain(aioSession.getSessionID(),MiraiCode.deserializeMiraiCode(Config.INSTANCE.getServerOnlineMsg().replaceAll("%server%", ClientManager.clientName.get(aioSession.getSessionID()) != null ? ClientManager.clientName.get(aioSession.getSessionID()).Name : "未命名服务器")));
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void msgSend(AioSession session, String msg) {
        try {
            WriteBuffer writeBuffer = session.writeBuffer();
            byte[] content = msg.getBytes(Charsets.UTF_8);
            writeBuffer.writeInt(content.length);
            writeBuffer.write(content);
            writeBuffer.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public static void QQsendMsg(String msg) {
        if (Config.INSTANCE.getGroupID() != 0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(new PlainText(msg));
        } else {
            Chatsync.chatsync.getLogger().info("请绑定QQ群,以启用消息同步");
        }
    }

    public static void QQsendMsgMessageChain(String assionID,MessageChain msg) {

        if (Config.INSTANCE.getGroupID() != 0L||ClientManager.clientName.get(assionID).GroupId!=0L) {
            if (ClientManager.clientName.get(assionID).GroupId!=0L)
                bot.getGroup(ClientManager.clientName.get(assionID).GroupId).sendMessage(msg);
            else bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(msg);
        } else {
            Chatsync.chatsync.getLogger().info("请绑定QQ群,以启用消息同步");
        }
    }

    public static void QQsendImg(String assionID,InputStream file) {
        if (Config.INSTANCE.getGroupID() != 0L||ClientManager.clientName.get(assionID).GroupId!=0L) {
            if (ClientManager.clientName.get(assionID).GroupId!=0L){
                ExternalResource.sendAsImage(file, bot.getGroup(ClientManager.clientName.get(assionID).GroupId));
            }else
                ExternalResource.sendAsImage(file, bot.getGroup(Config.INSTANCE.getGroupID()));
            try {
                file.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Chatsync.chatsync.getLogger().info("请绑定QQ群,已启用消息同步");
        }
    }
}
