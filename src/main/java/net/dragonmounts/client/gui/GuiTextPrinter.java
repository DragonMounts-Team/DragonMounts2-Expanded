/*
 ** 2013 October 30
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.gui;

import net.minecraft.client.gui.FontRenderer;

import java.util.regex.Pattern;

/**
 * GUI helper to "print" text in similar fashion to System.out.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class GuiTextPrinter {
    private static final Pattern PATTERN_TAB = Pattern.compile("\t", Pattern.LITERAL);
    private static final Pattern PATTERN_RETURN = Pattern.compile("\r", Pattern.LITERAL);
    private final FontRenderer fr;
    private int xOrigin;
    private int yOrigin;
    private int x;
    private int y;
    private int yLineSpace = 12;
    private int color;

    public GuiTextPrinter(FontRenderer fr) {
        this.fr = fr;
    }
    
    private void newLine() {
        x = xOrigin;
        y += yLineSpace;
    }

    public void print(String text) {
        if (fr == null) return;  //  just in case...
        String[] lines = text.split("\n", -1);
        if (lines.length > 1) {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (!line.isEmpty()) {
                    // replace common special chars
                    fr.drawStringWithShadow(PATTERN_TAB.matcher(
                            PATTERN_RETURN.matcher(line).replaceAll("")
                    ).replaceAll("    "), x, y, color);
                }
                if (i != lines.length - 1) {
                    newLine();
                } else {
                    x += fr.getStringWidth(line);
                }
            }
        } else {
            fr.drawStringWithShadow(text, x, y, color);
            x += fr.getStringWidth(text);
        }
    }

    public void println(String text) {
        print(text);
        newLine();
    }
    
    public void println() {
        newLine();
    }
    
    public void printf(String format, Object... args) {
        print(String.format(format, args));
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    
    public void setOrigin(int x, int y) {
        this.x = x;
        this.y = y;
        
        xOrigin = x;
        yOrigin = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLineSpace() {
        return yLineSpace;
    }

    public void setLineSpace(int yLineSpace) {
        this.yLineSpace = yLineSpace;
    }
}
