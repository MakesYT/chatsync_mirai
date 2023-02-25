import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChainBuilder;

public class MiraiCodeTest {

    public static void main1(String[] args) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        // 定义一个字符串
        String str = "这是一条消息，里面包含了[mirai:image:$imageId]和[mirai:at:$target]两种[mdsd:dsds]格式。[mirai:target]";

        String regex = "\\[(mirai:[^\\]]+)\\]";
        // 创建一个Pattern对象

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
        System.out.println(messageChainBuilder.build());
    }

    public static void main(String[] args) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        // 定义一个字符串
        String str = "这是一条消息，里面包含了[mirai:image:$imageId]和[mirai:at:$target]两种[mdsd:dsds]格式。[mirai:target]";
        // System.out.println(MiraiCode.deserializeMiraiCode(str).to);
    }
}
