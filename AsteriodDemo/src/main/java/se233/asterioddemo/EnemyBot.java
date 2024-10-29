/*package se233.asterioddemo;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class EnemyBot extends Character {
    public enum BotType {
        SCOUT, ATTACKER
    }

    private BotType botType;
    private double speed;
    private double attackRange;
    private double currentAngle;
    private ArrayList<Bullet> bullets = new ArrayList<>(); // เก็บกระสุนของบอท

    public EnemyBot(double startX, double startY, double speed, double attackRange, BotType botType) {
        super(startX, startY, 0, 0, speed, 0);
        this.speed = speed;
        this.attackRange = attackRange;
        this.botType = botType;
    }

    public void update(double playerX, double playerY, GraphicsContext gc) {
        if (botType == BotType.SCOUT) {
            updateScoutBehavior(playerX, playerY);
        } else if (botType == BotType.ATTACKER) {
            updateAttackerBehavior(playerX, playerY);
        }

        // อัปเดตและวาดกระสุนทั้งหมด
        updateBullets(gc);
        drawBot(gc);    }

    private void updateScoutBehavior(double playerX, double playerY) {
        double deltaX = playerX - this.x;
        double deltaY = playerY - this.y;
        double distanceToPlayer = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distanceToPlayer > attackRange) {
            currentAngle += 0.05;
            this.x = playerX + (Math.cos(currentAngle) * attackRange);
            this.y = playerY + (Math.sin(currentAngle) * attackRange);
        } else {
            attackPlayer(playerX, playerY);
            this.x += deltaX * -0.1;
            this.y += deltaY * -0.1;
        }
    }

    private void updateAttackerBehavior(double playerX, double playerY) {
        double deltaX = playerX - this.x;
        double deltaY = playerY - this.y;
        double angleToPlayer = Math.atan2(deltaY, deltaX);

        this.x += Math.cos(angleToPlayer) * speed;
        this.y += Math.sin(angleToPlayer) * speed;

        if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= attackRange) {
            attackPlayer(playerX, playerY);
        }
    }

    public void attackPlayer(double playerX, double playerY) {
        if (botType == BotType.ATTACKER) {
            System.out.println("Attacker bot is attacking the player!");
            shootAtPlayer(playerX, playerY, 5); // Attacker ยิงกระสุนเร็วกว่า
        } else if (botType == BotType.SCOUT) {
            System.out.println("Scout bot is attacking and retreating from the player.");
            shootAtPlayer(playerX, playerY, 3); // Scout ยิงกระสุนช้ากว่า
        }
    }

    private void shootAtPlayer(double playerX, double playerY, double bulletSpeed) {
        double deltaX = playerX - this.x;
        double deltaY = playerY - this.y;
        double angle = Math.atan2(deltaY, deltaX);
        Bullet bullet = new Bullet(this.x, this.y, angle, bulletSpeed);
        bullets.add(bullet); // เพิ่มกระสุนใหม่ลงใน ArrayList
    }

    private void updateBullets(GraphicsContext gc) {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            // ลบกระสุนที่ออกจากขอบหน้าจอ
            if (bullet.getX() < 0 || bullet.getX() > gc.getCanvas().getWidth() ||
                    bullet.getY() < 0 || bullet.getY() > gc.getCanvas().getHeight()) {
                bullets.remove(i);
                i--;
            }
        }
    }

    private void drawBot(GraphicsContext gc) {
        if (botType == BotType.SCOUT) {
            // วาดบอทประเภท Scout
        } else if (botType == BotType.ATTACKER) {
            // วาดบอทประเภท Attacker
        }
    }
}*/
