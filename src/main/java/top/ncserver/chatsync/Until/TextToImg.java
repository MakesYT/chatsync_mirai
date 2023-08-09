package top.ncserver.chatsync.Until;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextToImg {
    private static final Map<Character, Color> colorMap = new HashMap<>();

    static {
        colorMap.put('0', Color.black);
        colorMap.put('1', new Color(Integer.parseInt("0000AA", 16)));
        colorMap.put('2', new Color(Integer.parseInt("00AA00", 16)));
        colorMap.put('3', new Color(Integer.parseInt("00AAAA", 16)));
        colorMap.put('4', new Color(Integer.parseInt("AA0000", 16)));
        colorMap.put('5', new Color(Integer.parseInt("AA00AA", 16)));
        colorMap.put('6', new Color(Integer.parseInt("FFAA00", 16)));
        colorMap.put('7', new Color(Integer.parseInt("AAAAAA", 16)));
        colorMap.put('8', new Color(Integer.parseInt("555555", 16)));
        colorMap.put('9', new Color(Integer.parseInt("5555FF", 16)));
        colorMap.put('a', new Color(Integer.parseInt("55FF55", 16)));
        colorMap.put('b', new Color(Integer.parseInt("55FFFF", 16)));
        colorMap.put('c', new Color(Integer.parseInt("FF5555", 16)));
        colorMap.put('d', new Color(Integer.parseInt("FF55FF", 16)));
        colorMap.put('e', new Color(Integer.parseInt("FFFF55", 16)));
        colorMap.put('f', new Color(Integer.parseInt("FFFFFF", 16)));
        colorMap.put('g', new Color(Integer.parseInt("DDD605", 16)));
    }

    public static File toImg(String text) throws IOException {
        String[] strings = text.split("\n");
        int minX = 0;

        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, TextToImg.class.getResourceAsStream("/MiSans-Normal.ttf"));
            font = font.deriveFont(64F);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }

        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        for (String line : strings) {
            line = line.replaceAll("ﾧ\\S", "");
            line = line.replaceAll("§\\S", "");
            int result = fm.stringWidth(line);

            if (minX < result) minX = result;
        }

        int lineHeight = 68;
        int lineSpacing = 16;
        int Y = strings.length * lineHeight + (strings.length - 1) * lineSpacing + 30;
        minX = minX + 64;

        BufferedImage image = new BufferedImage(minX, Y, BufferedImage.TYPE_INT_BGR);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(Integer.parseInt("cacdca", 16)));
        g.fillRect(0, 0, minX, Y);
        g.setColor(Color.black);
        g.setFont(font);

        for (int i = 0; i < strings.length; i++) {
            renderLineAndColor(strings[i], i, g, lineHeight, lineSpacing);
        }

        g.dispose();
        File outfile = new File("image.png");
        ImageIO.write(image, "png", outfile);
        return outfile;
    }

    private static synchronized void renderLineAndColor(String line, int lineIndex, Graphics g, int lineHeight, int lineSpacing) {
        int dex = 0;
        int nowX = 32;
        FontMetrics fm = g.getFontMetrics();

        for (int j = 0; j < line.length(); j++) {
            char currentChar = line.charAt(j);
            if (currentChar == 'ﾧ' || currentChar == '§') {
                Color color = getColor(line.charAt(j + 1));
                if (color != null) {
                    g.setColor(color);
                } else {
                    g.setColor(Color.WHITE);
                }
                j++;
                dex = dex + 2;
            } else {
                String b = Character.toString(currentChar);
                g.drawString(b, nowX, lineIndex > 0 ? lineHeight + (lineIndex) * (lineHeight + lineSpacing) : lineHeight);
                nowX += fm.stringWidth(b);
                dex++;
            }
        }
    }

    private static Color getColor(char colorCode) {
        return colorMap.get(colorCode);
    }

    private static void renderLine(String line, int lineIndex, Graphics2D g, int lineHeight, int lineSpacing) {
        int nowX = 32;
        FontMetrics fm = g.getFontMetrics();
        int y = lineIndex > 0 ? lineHeight + (lineIndex) * (lineHeight + lineSpacing) : lineHeight;

        if (line.length() < 10) {
            int dex = 0;

            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == 'ﾧ' || line.charAt(j) == '§') {
                    Color color = colorMap.get(line.charAt(j + 1));
                    if (color != null) {
                        g.setColor(color);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    j++;
                    dex = dex + 2;
                } else {
                    String b = Character.toString(line.charAt(dex));
                    g.drawString(b, nowX, lineIndex > 0 ? lineHeight + (lineIndex) * (lineHeight + lineSpacing) : lineHeight);
                    nowX += fm.stringWidth(b);
                    dex++;
                }
            }
        } else {
            BufferedImage textImage = new BufferedImage(fm.stringWidth(line), lineHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D textGraphics = textImage.createGraphics();

            textGraphics.setColor(Color.WHITE);
            textGraphics.fillRect(0, 0, textImage.getWidth(), textImage.getHeight());

            textGraphics.setColor(g.getColor());
            textGraphics.setFont(g.getFont());
            textGraphics.drawString(line, 0, fm.getAscent());

            textGraphics.dispose();

            g.drawImage(textImage, nowX, y, null);
        }
    }
}
