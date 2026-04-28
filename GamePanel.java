package PingPong;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private static final int WIN_SCORE = 5;

    Paddle leftPaddle;
    Paddle rightPaddle;
    Ball ball;

    Timer timer;
    int leftScore = 0;
    int rightScore = 0;

    enum GameState { MENU, RUNNING, PAUSED, GAME_OVER }
    GameState state = GameState.MENU;

    public GamePanel() {
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new KeyHandler());
        this.addMouseWheelListener(new MouseWheelHandler());
        this.addKeyListener(new KeyHandler());



        leftPaddle = new Paddle(20, GAME_HEIGHT/2 - 50, 20, 100, 10);
        rightPaddle = new Paddle(GAME_WIDTH - 40, GAME_HEIGHT/2 - 50, 20, 100, 10);
        ball = new Ball(GAME_WIDTH/2, GAME_HEIGHT/2, 20, 5);

        timer = new Timer(10, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);

        switch (state) {
            case MENU -> drawMenu(g);
            case RUNNING -> drawGame(g);
            case PAUSED -> {
                drawGame(g);
                drawPause(g);
            }
            case GAME_OVER -> drawGameOver(g);
        }
    }

    private void drawMenu(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        drawCenteredString(g, "Ping Pong", GAME_WIDTH, GAME_HEIGHT / 2 - 50);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        drawCenteredString(g, "Drücke LEERTASTE zum Starten", GAME_WIDTH, GAME_HEIGHT / 2 + 20);
        drawCenteredString(g, "W/S = links, Pfeiltasten = rechts", GAME_WIDTH, GAME_HEIGHT / 2 + 60);
        drawCenteredString(g, "P = Pause, R = Neustart, ESC = Beenden", GAME_WIDTH, GAME_HEIGHT / 2 + 100);
    }

    private void drawGame(Graphics g) {
        // Mittellinie
        g.setColor(Color.darkGray);
        for (int i = 0; i < GAME_HEIGHT; i += 30) {
            g.fillRect(GAME_WIDTH / 2 - 2, i, 4, 20);
        }

        leftPaddle.draw(g);
        rightPaddle.draw(g);
        ball.draw(g);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString(leftScore + " : " + rightScore, GAME_WIDTH/2 - 40, 50);
    }

    private void drawPause(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        drawCenteredString(g, "PAUSE", GAME_WIDTH, GAME_HEIGHT / 2);
    }

    private void drawGameOver(Graphics g) {
        drawGame(g);

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String winner = leftScore > rightScore ? "Links gewinnt!" : "Rechts gewinnt!";
        drawCenteredString(g, "GAME OVER", GAME_WIDTH, GAME_HEIGHT / 2 - 40);
        drawCenteredString(g, winner, GAME_WIDTH, GAME_HEIGHT / 2 + 10);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        drawCenteredString(g, "R = Neustart, ESC = Beenden", GAME_WIDTH, GAME_HEIGHT / 2 + 60);
    }

    private void drawCenteredString(Graphics g, String text, int width, int y) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = (width - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == GameState.RUNNING) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        ball.move();
        moveRightPaddleAI();
        leftPaddle.clamp(0, GAME_HEIGHT);
        rightPaddle.clamp(0, GAME_HEIGHT);
        checkCollisions();
    }

    private void moveRightPaddleAI() {
    int centerY = rightPaddle.y + rightPaddle.height / 2;

    int botSpeed = 6; // vorher 10 → jetzt langsamer

    if (ball.y < centerY - 20) {
        rightPaddle.y -= botSpeed;
    } else if (ball.y > centerY + 20) {
        rightPaddle.y += botSpeed;
    }

    rightPaddle.clamp(0, GAME_HEIGHT);
}


    private void checkCollisions() {

        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - ball.size) {
            ball.bounceVertical();
        }

        if (ball.x <= leftPaddle.x + leftPaddle.width &&
            ball.x >= leftPaddle.x &&
            ball.y + ball.size >= leftPaddle.y &&
            ball.y <= leftPaddle.y + leftPaddle.height) {
            ball.bounceHorizontal();
            ball.speedUp();
        }

        if (ball.x + ball.size >= rightPaddle.x &&
            ball.x + ball.size <= rightPaddle.x + rightPaddle.width &&
            ball.y + ball.size >= rightPaddle.y &&
            ball.y <= rightPaddle.y + rightPaddle.height) {
            ball.bounceHorizontal();
            ball.speedUp();
        }

        if (ball.x < 0) {
            rightScore++;
            checkWinOrReset();
        }

        if (ball.x > GAME_WIDTH) {
            leftScore++;
            checkWinOrReset();
        }
    }

    private void checkWinOrReset() {
        if (leftScore >= WIN_SCORE || rightScore >= WIN_SCORE) {
            state = GameState.GAME_OVER;
        } else {
            ball.reset(GAME_WIDTH/2, GAME_HEIGHT/2);
        }
    }

    private void resetGame() {
        leftScore = 0;
        rightScore = 0;
        ball.reset(GAME_WIDTH/2, GAME_HEIGHT/2);
        leftPaddle.y = GAME_HEIGHT/2 - leftPaddle.height/2;
        rightPaddle.y = GAME_HEIGHT/2 - rightPaddle.height/2;
        state = GameState.MENU;
    }

    public class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            if (state == GameState.RUNNING) {
                if (e.getKeyCode() == KeyEvent.VK_W) leftPaddle.moveUp();
                if (e.getKeyCode() == KeyEvent.VK_S) leftPaddle.moveDown();
                if (e.getKeyCode() == KeyEvent.VK_UP) rightPaddle.moveUp();
                if (e.getKeyCode() == KeyEvent.VK_DOWN) rightPaddle.moveDown();
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (state == GameState.MENU) state = GameState.RUNNING;
            }

            if (e.getKeyCode() == KeyEvent.VK_P) {
                if (state == GameState.RUNNING) state = GameState.PAUSED;
                else if (state == GameState.PAUSED) state = GameState.RUNNING;
            }

            if (e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
            }

            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }
    }

    public class MouseWheelHandler implements MouseWheelListener {
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();

        int steps = 5; // Anzahl der Bewegungen pro Scroll

        for (int i = 0; i < steps; i++) {
            if (rotation < 0) {
                leftPaddle.moveUp();
            } else if (rotation > 0) {
                leftPaddle.moveDown();
            }
        }

        leftPaddle.clamp(0, GAME_HEIGHT);
    }
}


}
