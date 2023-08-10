/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-06-08 (yyyy-dd-mm)
    Course: COMP 452
    Student ID: 3433058
    Assignment: 3
    Question: 2
    Description: 
    
    Projectile class for the game. This class is used to create
    projectiles for the enemies and the player. The projectile
    will move in the direction of the angle it was fired at.
*/
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Projectile extends JPanel{
    private double x;
    private double y;
    private double angle;
    private double speed;
    private int size = 5;
    private Enemy projectileOwner;


    public Projectile(double x, double y, double angle, int speed) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.speed = speed;
    }

    public Projectile(Enemy enemy, double angle, int speed) {
        this.projectileOwner = enemy;
        this.x = projectileOwner.getX();
        this.y = projectileOwner.getY();
        this.angle = angle;
        this.speed = speed;
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int)x - size / 2, (int)y - size / 2, size, size);
    }
    
    public void move() {          
        x += speed * Math.cos(Math.toRadians(angle));
        y += speed * Math.sin(Math.toRadians(angle));
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    // check if the current projectile intersects with the enemy
    public boolean intersects(Enemy enemy) {
        double dx = x - enemy.getX();
        double dy = y - enemy.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (enemy.getSize() / 2);
    }

    public int getIntProjectileX() {
        return (int)x;
    }

    public int getIntProjectileY() {
        return (int)y;
    }

    public Enemy getOwner() {
        return projectileOwner;
    }
}