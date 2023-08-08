/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-17-07
    Course: COMP 452
    Student ID: 3433058
    Assignment: 1
    Description: Creating a tower defense game with the implementation of Basic steering behaviors such as wander, arrive, flee and a complex steering behavior of formation.
    Class Description: This is the projectile class that creates the projectile object.
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
    
    public Projectile(double x, double y, double angle, int speed) {
        this.x = x;
        this.y = y;
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

}