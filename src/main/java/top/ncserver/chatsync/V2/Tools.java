package top.ncserver.chatsync.V2;

import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public class Tools {
    private static final String regex = "\\[(mirai:[^\\]]+)\\]";

    public static MessageChain StringToMsgChain(String str) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        String[] parts = str.split("((?=\\[(mirai:[^\\]]+))|(?<=\\]))", -1);
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (part.matches(regex)) {
                    messageChainBuilder.add(MiraiCode.deserializeMiraiCode(part));
                } else {
                    messageChainBuilder.add(part);
                }
            }
        }
        return messageChainBuilder.build();
    }
}
