package pacman.game;

import fri.shapesge.FontStyle;
import fri.shapesge.Rectangle;
import fri.shapesge.TextBlock;

import java.awt.Canvas;
import java.awt.Font;

public class Overlay {
    // may change later down the line if board becomes variable size
    private static final int BOARD_PIXEL_W = 560;   // 28 cols * 20 px
    private static final int BOARD_PIXEL_H = 620;   // 31 rows * 20 px
    private static final int BOARD_OFFSET_Y = 40;

    private static final int PANEL_W = 360;
    private static final int PANEL_H = 160;
    private static final int PANEL_X = (BOARD_PIXEL_W - PANEL_W) / 2;
    private static final int PANEL_Y = BOARD_OFFSET_Y + (BOARD_PIXEL_H - PANEL_H) / 2; // 270

    private static final int TITLE_Y = PANEL_Y + 42;
    private static final int SUBTITLE_Y = PANEL_Y + 82;
    private static final int HINT_Y = PANEL_Y + 118;

    private static final int TITLE_FONT_SIZE = 28;
    private static final int SUBTITLE_FONT_SIZE = 16;
    private static final int HINT_FONT_SIZE = 13;

    private final Rectangle background;
    private final Rectangle border;
    private final Rectangle inner;
    private final TextBlock titleText;
    private final TextBlock subtitleText;
    private final TextBlock hintText;

    public Overlay() {
        this.background = new Rectangle();
        this.background.changeSize(PANEL_W, PANEL_H);
        this.background.changePosition(PANEL_X, PANEL_Y);
        this.background.changeColor("blue");

        this.border = new Rectangle();
        this.border.changeSize(PANEL_W - 2, PANEL_H - 2);
        this.border.changePosition(PANEL_X + 1, PANEL_Y + 1);
        this.border.changeColor("cyan");

        this.inner = new Rectangle();
        this.inner.changeSize(PANEL_W - 6, PANEL_H - 6);
        this.inner.changePosition(PANEL_X + 3, PANEL_Y + 3);
        this.inner.changeColor("blue");

        this.titleText = new TextBlock("");
        this.titleText.changeFont("Arial", FontStyle.BOLD, TITLE_FONT_SIZE);

        this.subtitleText = new TextBlock("");
        this.subtitleText.changeFont("Arial", FontStyle.PLAIN, SUBTITLE_FONT_SIZE);

        this.hintText = new TextBlock("");
        this.hintText.changeFont("Arial", FontStyle.ITALIC, HINT_FONT_SIZE);
    }

    public void showGameOver(int score) {
        this.show("GAME OVER", "white", "Score: " + score, "Press R to restart or ESC to quit");
    }

    public void showWin(int score) {
        this.show("YOU WIN!", "yellow", "Score: " + score, "Press R to restart or ESC to quit");
    }

    public void showPaused() {
        this.show("PAUSED", "cyan", "", "Press P to resume");
    }

    public void hide() {
        this.background.makeInvisible();
        this.border.makeInvisible();
        this.inner.makeInvisible();
        this.titleText.makeInvisible();
        this.subtitleText.makeInvisible();
        this.hintText.makeInvisible();
    }

    private static int centeredX(String text, String fontFamily, int awtStyle, int size) {
        Canvas canvas = new Canvas();
        Font font = new Font(fontFamily, awtStyle, size);
        int textWidth = canvas.getFontMetrics(font).stringWidth(text);
        return PANEL_X + (PANEL_W - textWidth) / 2;
    }

    private void show(String title, String titleColor, String subtitle, String hint) {
        this.background.makeVisible();
        this.border.makeVisible();
        this.inner.makeVisible();

        this.titleText.changeColor(titleColor);
        this.titleText.changeText(title);
        this.titleText.changePosition(centeredX(title, "Arial", Font.BOLD, TITLE_FONT_SIZE), TITLE_Y);
        this.titleText.makeVisible();

        if (subtitle != null && !subtitle.isEmpty()) {
            this.subtitleText.changeColor("white");
            this.subtitleText.changeText(subtitle);
            this.subtitleText.changePosition(centeredX(subtitle, "Arial", Font.PLAIN, SUBTITLE_FONT_SIZE), SUBTITLE_Y);
            this.subtitleText.makeVisible();
        } else {
            this.subtitleText.makeInvisible();
        }

        if (hint != null && !hint.isEmpty()) {
            this.hintText.changeColor("gray");
            this.hintText.changeText(hint);
            this.hintText.changePosition(centeredX(hint, "Arial", Font.ITALIC, HINT_FONT_SIZE), HINT_Y);
            this.hintText.makeVisible();
        } else {
            this.hintText.makeInvisible();
        }
    }
}
