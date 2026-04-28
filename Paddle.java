package PingPong;

import java.awt.*;

public class Paddle {

    public int x, y;
    public int width, height;
    public int speed;

    public Paddle(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(x, y, width, height);
    }

    public void moveUp() {
        y -= speed;
    }

    public void moveDown() {
        y += speed;
    }

    public void clamp(int minY, int maxY) {
        if (y < minY) y = minY;
        if (y + height > maxY) y = maxY - height;
    }
}


