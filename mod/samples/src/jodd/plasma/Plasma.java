package jodd.plasma;

import jodd.gfx.FpsMonitor;
import jodd.gfx.GfxPanel;
import jodd.gfx.Palette;
import jodd.gfx.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Plasma extends GfxPanel implements KeyListener {

	FpsMonitor fpsMonitor = new FpsMonitor();
	Palette pal = new Palette(256);
	Font logoFont = new Font("Tahoma", Font.PLAIN, 10);
	Sprite panel = new Sprite(50, 240, Sprite.COLOR_ARGB);
	Font panelFont = new Font("Arial", Font.BOLD, 40);
	FontMetrics panelfm = getFontMetrics(panelFont);

	@Override
	public void init() {
		this.width = 320;
		this.height = 240;
		this.framerate = 40;
		this.name = "Jodd Plasma";
	}

	@Override
	public void ready() {
		addKeyListener(this);
		palOne();
		plasmaInit();
		panel.g2d.setFont(panelFont);
		panel.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		screen.g2d.setColor(new Color(0, 0, 0x50));
		screen.g2d.setFont(logoFont);
	}

	// ---------------------------------------------------------------- update

	int starty = 400;			// starting y offset of the panel txt
	int panely = 360; 

	@Override
	public void update() {
		starty--;
		if (starty < -550) {
			panely++;
			if (panely == 500) {
				starty = 400;
				panely = 360;
			}
		} else {
			if (panely > 250) {
				panely--;
			}
		}
	}

	// ---------------------------------------------------------------- paint

	@Override
	public void paint() {
		plasmaDraw(screen.buf);
		screen.g2d.drawString("jodd gfx", 3, 236);
		drawPanel();
		screen.g2d.drawImage(panel.img, panely, 0, null);
		fpsMonitor.print();
	}

	// ---------------------------------------------------------------- draw panel

	char[] message = "enjoy fast plasma".toCharArray();

	int gap = 32;

	void drawPanel() {
		for (int i = 0; i < panel.size; i++) {
			panel.buf[i] = 0x99FF0000;
		}
		Graphics2D g2d = panel.g2d;

		// letters
		int y = starty;
		if ((y < 300) && (y > -550)) {
			g2d.setColor(Color.WHITE);
			for (char msg : message) {
				int lw = panelfm.charWidth(msg);
				g2d.drawString(String.valueOf(msg), 25 - lw / 2, y);
				y += gap;
			}
		}
		// rect
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, panel.width - 1, panel.height - 1);
	}

	// ---------------------------------------------------------------- key listener

	public void keyPressed(KeyEvent key) {
		char k = key.getKeyChar();
		switch (k) {
			case '1': palOne(); break;
			case '2': palReds(); break;
			case '3': palBW(); break;
		}
	}

	public void keyReleased(KeyEvent ignore) {}

	public void keyTyped(KeyEvent ignore) {}

	// ---------------------------------------------------------------- palettes

	void palOne() {
		pal.gradientFill(0, 128, 0x174185, 0xFFFFFF);
		pal.gradientFill(128, 255, 0xFFFFFF, 0x174185);
	}

	void palReds() {
		pal.gradientFill(0, 128, 0xCC0000, 0x550000);
		pal.gradientFill(128, 255, 0x550000, 0xCC0000);
	}

	void palBW() {
		for (int i = 0; i < 256; i++) {
			pal.setColor(i, 0);
		}
		for (int i = 0; i < 10; i++) {
			pal.setColor(i, 0xFFFFFF);
		}
	}

	// ---------------------------------------------------------------- plasma engine

	int[] tab1 = new int[320 * 240];
	int[] tab2 = new int[320 * 240];
	float circle1, circle2, circle3, circle4, circle5, circle6, circle7, circle8;
	int roll;

	/**
	 * Initializes plasma arrays.
	 */
	void plasmaInit() {
		for (int i = 0; i < 240; i++) {
			for (int j = 0; j < 320; j++) {
				tab1[(i * 320) + j] = (int) ((Math.sqrt(16.0 + (120 - i) * (120 - i) + (160 - j) * (160 - j)) - 4) * 5);
			}
		}

		for (int i = 0; i < 240; i++) {
			for (int j = 0; j < 320; j++) {
				double temp = Math.sqrt(16.0 + (120 - i) * (120 - i) + (160 - j) * (160 - j)) - 4;
				tab2[(i * 320) + j] = (int) ((Math.sin(temp / 9.5) + 1) * 90);
			}
		}
		circle1 = circle2 = circle3 = circle4 = circle5 = circle6 = circle7 = circle8 = 0;
		roll = 0;
	}


	/**
	 * Draws current plasma on screen.
	 */
	void plasmaDraw(int[] dest) {
		int i, j, k;
		int x1, y1, x2, y2, x3, y3, x4, y4;
		int c;

		circle1 += 0.085 / 6;
		circle2 -= 0.1 / 6;
		circle3 += 0.3 / 6;
		circle4 -= 0.2 / 6;
		circle5 += 0.4 / 6;
		circle6 -= 0.15 / 6;
		circle7 += 0.35 / 6;
		circle8 -= 0.05 / 6;
		x2 = (int) ((320 / 4) + (320 / 4) * Math.sin(circle1));		// 0 - 160
		y2 = (int) ((240 / 4) + (240 / 4) * Math.cos(circle2));		// 0 - 120
		x1 = (int) ((320 / 4) + (320 / 4) * Math.cos(circle3));
		y1 = (int) ((240 / 4) + (240 / 4) * Math.sin(circle4));
		x3 = (int) ((320 / 4) + (320 / 4) * Math.cos(circle5));
		y3 = (int) ((200 / 4) + (200 / 4) * Math.sin(circle6));
		x4 = (int) ((320 / 4) + (320 / 4) * Math.cos(circle7));
		y4 = (int) ((200 / 4) + (200 / 4) * Math.sin(circle8));

		roll += 5;
		k = 0;

		for (i = 0; i < 120; i++) {
			for (j = 0; j < 160; j++) {
				c = (int) (tab1[320 * (i + y1) + j + x1] +
						tab1[320 * (i + y2) + j + x2] +
						tab2[320 * (i + y3) + j + x3] +
						tab2[320 * (i + y4) + j + x4] + roll);
				c = pal.getColor(c & 0xFF);
				dest[k + 2 * j] = c;
				dest[k + 2 * j + 1] = c;
				dest[k + 2 * j + 320] = c;
				dest[k + 2 * j + 321] = c;
			}
			k += 640;
		}
	}
}