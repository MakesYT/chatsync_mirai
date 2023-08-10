package top.ncserver.chatsync.Until;


import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class TextToImg {
    public static Font font=null;
    public static FontMetrics fm=null;
    public static InputStream toImg(String text) throws IOException {
        if (fm ==null){
            try {
                font= Font.createFont(Font.TRUETYPE_FONT, TextToImg.class.getResourceAsStream("/MiSans-Normal.ttf"));
                font=font.deriveFont(32F);
                fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            }
        }

        String[] strings =text.split("\n");
        int minX=0;
        for (String line: strings) {

            line=line.replaceAll("ﾧ\\S", "");
            line=line.replaceAll("§\\S", "");
            //font.getLineMetrics(line)

            int result = fm.stringWidth(line);

            if (minX<result) minX=result;
        }
        int Y= strings.length*34+ (strings.length-1)*8+15;
        minX=minX+32;
        BufferedImage image = new BufferedImage(minX, Y,
                BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.setClip(0, 0, minX, Y);
        g.setColor(new Color(Integer.parseInt("cacdca", 16)));
        g.fillRect(0, 0,minX, Y);// 先用黑色填充整张图片,也就是背景
        g.setColor(Color.black);// 在换成黑色
        g.setFont(font);// 设置画笔字体
        for (int i=0;i< strings.length;i++){
            String nowLine=strings[i];
            int dex=0;
            int nowX=16;

            for (int j = 0; j < nowLine.length(); j++) {
                if (nowLine.charAt(j)=='ﾧ'||nowLine.charAt(j)=='§'){
                    switch (nowLine.charAt(j+1)){
                        case '0':
                            g.setColor(Color.black);
                            break;//黑色
                        case '1':
                            g.setColor(new Color(Integer.parseInt("0000AA", 16)));
                            break;//深蓝色
                        case '2':
                            g.setColor(new Color(Integer.parseInt("00AA00", 16)));
                            break;//深绿色
                        case '3':
                            g.setColor(new Color(Integer.parseInt("00AAAA", 16)));
                            break;//湖蓝色
                        case '4':
                            g.setColor(new Color(Integer.parseInt("AA0000", 16)));
                            break;//深红色
                        case '5':
                            g.setColor(new Color(Integer.parseInt("AA00AA", 16)));
                            break;//紫色
                        case '6':
                            g.setColor(new Color(Integer.parseInt("FFAA00", 16)));
                            break;//金色
                        case '7':
                            g.setColor(new Color(Integer.parseInt("AAAAAA", 16)));
                            break;//灰色
                        case '8':
                            g.setColor(new Color(Integer.parseInt("555555", 16)));
                            break;//深灰色
                        case '9':
                            g.setColor(new Color(Integer.parseInt("5555FF", 16)));
                            break;//蓝色
                        case 'a':
                            g.setColor(new Color(Integer.parseInt("55FF55", 16)));
                            break;//绿色
                        case 'b':
                            g.setColor(new Color(Integer.parseInt("55FFFF", 16)));
                            break;//天蓝色
                        case 'c':
                            g.setColor(new Color(Integer.parseInt("FF5555", 16)));
                            break;//红色
                        case 'd':
                            g.setColor(new Color(Integer.parseInt("FF55FF", 16)));
                            break;//粉红色
                        case 'e':
                            g.setColor(new Color(Integer.parseInt("FFFF55", 16)));
                            break;//黄色
                        case 'f':
                            g.setColor(new Color(Integer.parseInt("FFFFFF", 16)));
                            break;//白色
                        case 'g':
                            g.setColor(new Color(Integer.parseInt("DDD605", 16)));
                            break;//硬币金
                        default:
                            g.setColor(Color.WHITE);
                    }
                    j++;
                    dex=dex+2;

                }else {
                    g.drawString(String.valueOf(nowLine.charAt(dex)), nowX, i>0?34+(i)*34+(i)*8:34);
                    nowX+= fm.charWidth(nowLine.charAt(dex));
                    dex++;
                }
            }
            //System.out.println(strings[i]);

        }
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

// 创建一个MemoryCacheImageOutputStream对象，传入os作为参数
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(os);
        long start = System.currentTimeMillis();
// 使用ImageIO.write方法将image写入mcios，指定图片格式为png
        ImageIO.write(image, "png", mcios);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);
// 关闭mcios
        mcios.close();
        InputStream input = new ByteArrayInputStream(os.toByteArray());
        return input;
    }
}
