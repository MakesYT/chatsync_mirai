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
import top.ncserver.chatsync.Until.Config;
import top.ncserver.chatsync.Until.TextToImg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.ncserver.Chatsync.bot;
import static top.ncserver.chatsync.Until.ColorCodeCulling.CullColorCode;

public class MsgTools {
    static Pattern p = Pattern.compile("[(/+)\u4e00-\u9fa5(+*)]");
    static HttpClient client;
    public  static void listenerInit(){
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .cookieHandler(new CookieManager())
                .build();
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
            if (Config.INSTANCE.getGroupID()==0L&&event.getSender().getPermission().getLevel()>=1&&event.getMessage().contentToString().equals("/chatsync bind this")){
                Config.INSTANCE.setGroupID(event.getGroup().getId());
                QQsendMsg("??????????????????????????????");

            }else if (Chatsync.INSTANCE.isConnected){
                if (event.getGroup().getId()==Config.INSTANCE.getGroupID()) {
                    Map<String,Object> msg1 = new HashMap<>();
                    String msgString=event.getMessage().contentToString();
                    // msgString=msgString.replaceAll("\")
                    //At at=new At()
                    Matcher m = p.matcher(msgString.substring(1));
                    if(msgString.equals("/recon"))
                    {
                        QQsendMsg("??????????????????????????????.....");
                        Chatsync.session.close();
                    }else
                    if (msgString.startsWith("/")&&!m.lookingAt()){
                        if(event.getSender().getPermission().getLevel()>=1||msgString.equals("/ls")){
                            boolean baned=false;
                            String msg=event.getMessage().contentToString();
                            String command =msg.replaceFirst("/","");
                            for (String s : Config.INSTANCE.getBanCommand()) {
                                if (command.matches(s)) {
                                    baned = true;
                                    break;
                                }
                            }
                            if (!baned) {
                                msg1.put("type","command");
                                msg1.put("sender",event.getSenderName()+"("+event.getSender().getId()+")");
                                msg1.put("command",msg);
                                JSONObject jo= new JSONObject(msg1);
                                Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                msgSend(Chatsync.session,jo.toJSONString());
                            }else QQsendMsg("?????????????????????");

                        }else if (msgString.contains("/LS")||msgString.contains("/IS")||msgString.contains("/Is")){
                            QQsendMsg("PS:??????????????????/ls(????????????.??????????????????/LS)");

                        }else{
                            QQsendMsg("???????????????"+msgString);

                        }
                    } else if (Config.INSTANCE.getSyncMsg())
                    {
                        StringBuilder sb= new StringBuilder();
                        JSONArray msg = JSON.parseArray(MessageChain.serializeToJsonString(event.getMessage()));
                        if (event.getMessage().contains(QuoteReply.Key)){
                            QuoteReply quote = event.getMessage().get(QuoteReply.Key);
                            At at=new At(quote.getSource().getFromId());
                            StringBuilder msgBuilder=new StringBuilder();
                            msgBuilder.append("\u00A75 ?????????\n\u00A73");
                            msgBuilder.append(at.getDisplay(event.getGroup()).replaceFirst("@","["));
                            msgBuilder.append("]:");
                            msgBuilder.append(quote.getSource().getOriginalMessage());
                            sb.append("\u2191---");
                            msg1.put("type","remsg");
                            msg1.put("permission",event.getSender().getPermission().getLevel());
                            msg1.put("sender",event.getSenderName());
                            msg1.put("msg",msgBuilder);
                            JSONObject jo= new JSONObject(msg1);
                            Chatsync.chatsync.getLogger().info(jo.toJSONString());
                            msgSend(Chatsync.session,jo.toJSONString());
                        }
                        JSONArray originalMessage=((JSONObject)msg.get(0)).getJSONArray("originalMessage");
                        msg1.clear();

                        for (Object o : originalMessage) {
                            System.out.println(((JSONObject) o).toJSONString());
                            switch (((JSONObject)o).getString("type")){
                                case "PlainText":{
                                    sb.append(((JSONObject)o).getString("content"));
                                    break;
                                }
                                case "At":{
                                    At at=new At(((JSONObject)o).getLong("target"));
                                    sb.append(at.getDisplay(event.getGroup()));
                                    break;
                                }
                                case "AtAll":{
                                    sb.append("@????????????");
                                    break;
                                }
                                case "Image":{
                                    Image img=Image.fromId(((JSONObject)o).getString("imageId"));
                                    HttpRequest request = HttpRequest.newBuilder()
                                            .version(HttpClient.Version.HTTP_1_1)
                                            .GET()
                                            .timeout(Duration.ofSeconds(2))
                                            .uri(URI.create(Image.queryUrl(img)))
                                            .build();
                                    try {
                                        System.out.println(Image.queryUrl(img));
                                        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                                        msg1.put("type", "img");
                                        msg1.put("permission", event.getSender().getPermission().getLevel());
                                        msg1.put("sender", event.getSenderName());

                                        BufferedImage image = Thumbnails.of(ImageIO.read(new ByteArrayInputStream(response.body())))
                                                .scale(1f) //????????????????????? ???size() ?????????????????? ???????????????
                                                .outputQuality(0.5f)    //?????????????????????  0~1 ??????,????????????
                                                .asBufferedImage();
                                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                                        ImageIO.write(image, "png", os);
                                        msg1.put("data", Base64.getEncoder().encodeToString(os.toByteArray()));
                                        JSONObject jo = new JSONObject(msg1);
                                        Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                        msgSend(Chatsync.session, jo.toJSONString());
                                    } catch (IOException | InterruptedException e) {
                                        Chatsync.chatsync.getLogger().info("??????????????????");
                                    }
                                    break;
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
                        if (sb.length() > 0) {
                            msg1.clear();
                            msg1.put("type", "msg");
                            msg1.put("permission", event.getSender().getPermission().getLevel());
                            msg1.put("sender", event.getSenderName());
                            msg1.put("msg", sb);
                            JSONObject jo = new JSONObject(msg1);
                            Chatsync.chatsync.getLogger().info(jo.toJSONString());
                            msgSend(Chatsync.session, jo.toJSONString());
                        }


                    }

                }
            }else{
                if (Config.INSTANCE.getGroupID()==0L&&event.getSender().getPermission().getLevel()>=1&&event.getMessage().contentToString().equals("/chatsync bind this")){
                    Config.INSTANCE.setGroupID(event.getGroup().getId());
                    MsgTools.QQsendMsg("??????????????????????????????");

                }else
                if (event.getGroup().getId()==Config.INSTANCE.getGroupID()){
                    String msg=event.getMessage().contentToString();
                    if (msg.contains("/ls")||msg.contains("/list")) {
                        MsgTools.QQsendMsg("??????,???????????????????????????");
                    }else if (msg.contains("/LS")||msg.contains("/IS")||msg.contains("/Is")){
                        MsgTools.QQsendMsg("??????,???????????????????????????\nPS:??????????????????/ls(????????????.??????????????????/LS)");
                    }else if (msg.contains("%test")){
                        File file= null;
                        try {
                            file = TextToImg.toImg("------ ======= Help ======= ------\n/actionbarmsg [????????????/all" +
                                    "] (-s:[???]) [??????] - ???????????????actionbar??????\n/afk (-p:????????????) (??????) (-s) - ??????????????????,???????????????\n/afkcheck" +
                                    " [????????????/all] - ????????????????????????\n/air [????????????] [?????????] (-s) - ?????????????????????\n/alert [????????????] (??????" +
                                    ") - ??????????????????????????????\n/alertlist - ???????????????????????????\\n/aliaseditor (new) (alias-cmd) - ????????????" +
                                    "??????\nFor next page perform cmi ? 2\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        MsgTools.QQsendImg(file);
                    }
                }
            }

        });
    }
    public static void msgRead(AioSession session, String msgJ) throws IOException {
        if (bot!=null){
            System.out.println(msgJ);
            JSONObject jsonObject = JSONObject.parseObject(msgJ);
            if (Chatsync.INSTANCE.isConnected){
                switch (jsonObject.getString("type")) {
                    case "msg":
                        if (Config.INSTANCE.getSyncMsg()) {
                            System.out.println("[" + jsonObject.getString("sender") + "]:" + jsonObject.getString("msg"));
                            QQsendMsgMessageChain(MiraiCode.deserializeMiraiCode(Config.INSTANCE.getMsgStyle().replaceAll("%s%", jsonObject.getString("sender")).replaceAll("%msg%", jsonObject.getString("msg"))));
                        }
                        break;
                    case "img": {
                        if (Config.INSTANCE.getSyncMsg() && Config.INSTANCE.getGroupID() != 0L) {
                            String uuid = UUID.randomUUID().toString();
                            Base64.Decoder decoder = Base64.getDecoder();
                            byte[] b = decoder.decode(jsonObject.getString("msg"));
                            // ????????????
                            for (int i = 0; i < b.length; ++i) {
                                if (b[i] < 0) {
                                    b[i] += 256;
                                }
                            }
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(b));
                            File outfile = new File(uuid + ".png");
                            ImageIO.write(image, "png", outfile);
                            try (ExternalResource resource = ExternalResource.create(outfile)) { // ???????????? file
                                Image img = ExternalResource.uploadAsImage(resource, bot.getGroup(Config.INSTANCE.getGroupID())); // ??????????????????
                                MessageChain messageChain = new MessageChainBuilder().append(new PlainText("[" + jsonObject.getString("player") + "]:")).append(img).asMessageChain();
                                QQsendMsgMessageChain(messageChain);
                            }
                            System.gc();
                        }
                        break;
                    }
                    case "playerJoinAndQuit":
                        if (Config.INSTANCE.getSyncMsg()) {
                            QQsendMsgMessageChain(MiraiCode.deserializeMiraiCode(Config.INSTANCE.getPlayerJoinAndQuitMsgStyle().replaceAll("%s%", CullColorCode(jsonObject.getString("player"))).replaceAll("%msg%", jsonObject.getString("msg"))));
                        }
                        //QQsendMsg("??????"+CullColorCode(jsonObject.getString("player"))+jsonObject.getString("msg"));
                        break;
                    case "playerList":
                        QQsendMsgMessageChain(MiraiCode.deserializeMiraiCode(Config.INSTANCE.getPlayerListMsgStyle().replaceAll("%s%", jsonObject.getString("online")).replaceAll("%msg%", jsonObject.getString("msg"))));
                        //QQsendMsg("?????????"+jsonObject.getString("online")+"???????????????\n"+jsonObject.getString("msg"));
                        break;
                    case "command":
                        if (Config.INSTANCE.getImgTimer()) {
                            QQsendMsg(Config.INSTANCE.getImgTimerMsgStyle1());
                            long start = System.currentTimeMillis();
                            File file = TextToImg.toImg(jsonObject.getString("command"));
                            long finish = System.currentTimeMillis();
                            long timeElapsed = finish - start;
                            QQsendMsgMessageChain(MiraiCode.deserializeMiraiCode(Config.INSTANCE.getImgTimerMsgStyle2().replaceAll("%s%", String.valueOf(timeElapsed))));

                            QQsendImg(file);
                            System.gc();
                        } else {
                            File file = TextToImg.toImg(jsonObject.getString("command"));
                            QQsendImg(file);
                            System.gc();
                        }


                        break;
                    case "serverCommand":
                        QQsendMsg("????????????????????????" + jsonObject.getString("command") + "\n?????????????????????");
                        break;
                    case "playerDeath":
                    case "obRe":
                        QQsendMsgMessageChain(MiraiCode.deserializeMiraiCode(CullColorCode(Config.INSTANCE.getPlayerDeathMsgStyle().replaceAll("%msg%", jsonObject.getString("msg")))));
                        // QQsendMsg(CullColorCode(jsonObject.getString("msg")));
                        break;
                }
            }

        }

    }
    public static void msgSend(AioSession session, String msg) {
        try {
            WriteBuffer writeBuffer = session.writeBuffer();
            byte[] content = msg.getBytes(Charsets.UTF_8);
            writeBuffer.writeInt(content.length);
            writeBuffer.write(content);
            writeBuffer.flush();
        }catch (IOException e) {

        }

    }
    public static void QQsendMsg(String msg){
        if (Config.INSTANCE.getGroupID()!=0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(new PlainText(msg));
        }else{
            Chatsync.chatsync.getLogger().info("?????????QQ???,?????????????????????");
        }
    }
    public static void QQsendMsgMessageChain(MessageChain msg){
        if (Config.INSTANCE.getGroupID()!=0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(msg);
        }else{
            Chatsync.chatsync.getLogger().info("?????????QQ???,?????????????????????");
        }
    }
    public static void QQsendImg(File file){
        if (Config.INSTANCE.getGroupID()!=0L) {
            ExternalResource.sendAsImage(file,bot.getGroup(Config.INSTANCE.getGroupID()));
        }else{
            Chatsync.chatsync.getLogger().info("?????????QQ???,?????????????????????");
        }
    }
}
