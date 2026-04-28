package PingPong;

import java.awt.*;
import java.util.Random;

public class Ball {

    public int x, y;
    public int size;
    public int xSpeed, ySpeed;

    private int baseSpeed;
    private final int MAX_SPEED = 12;
    private int hitCounter = 0;

    Random random = new Random();

    public Ball(int x, int y, int size, int speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.baseSpeed = speed;

        int startSpeedX = random.nextBoolean() ? speed : -speed;
        int startSpeedY = random.nextBoolean() ? speed : -speed;

        this.xSpeed = startSpeedX;
        this.ySpeed = startSpeedY;
    }

    public void move() {
        x += xSpeed;
        y += ySpeed;
    }

    public void bounceVertical() {
        ySpeed = -ySpeed;
    }

    public void bounceHorizontal() {
        xSpeed = -xSpeed;
    }

    public void speedUp() {
        hitCounter++;

        if (hitCounter % 2 == 0) { // nur jedes 2. Mal schneller
            if (Math.abs(xSpeed) < MAX_SPEED) {
                xSpeed += (xSpeed > 0 ? 1 : -1);
            }
            if (Math.abs(ySpeed) < MAX_SPEED) {
                ySpeed += (ySpeed > 0 ? 1 : -1);
            }
        }
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.hitCounter = 0;

        int speed = baseSpeed;
        xSpeed = random.nextBoolean() ? speed : -speed;
        ySpeed = random.nextBoolean() ? speed : -speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(x, y, size, size);
    }
}
