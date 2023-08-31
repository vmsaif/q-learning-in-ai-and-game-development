/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-06-08 (yyyy-dd-mm)

    Description: 
    
    This class is responsible for the enemy objects. 
*/

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

public class Enemy {

    private double x;
    private double y;
    private double angle;
    private double xSpeed;
    private double ySpeed;

    private double leaderXSpeed;
    private double leaderYSpeed;

    private int size = 20;
    
    private Random random = new Random();
    private boolean canShoot = false;
    private boolean isInSquad;

    private boolean leader;
    private boolean stopWander;
    private double maxSpeed;
    private boolean arriving;

    private TowerGame game;
    private double targetX;
    private double targetY;
    private boolean flee;
    private double fleeSpeedX;
    private double fleeSpeedY;
    private double distanceBetweenEnemies;
    private int leaderDistanceFromCenter;
    private int leaderDistanceFromCenterMin;
    private int leaderDistanceFromCenterMax;
    private int leaderMovingDirectionRandomizer;
    private int movingInAngle;
    private int movingFixedAngle;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        angle = random.nextDouble() * 360;
        isInSquad = false;
        leader = false;
        stopWander = false;
        xSpeed = 0.25;
        ySpeed = 0.25;
        leaderXSpeed = 0.8;
        leaderYSpeed = 0.8;
        maxSpeed = 3.75;
        arriving = false;
        flee = false;
        fleeSpeedX = 2.0;
        fleeSpeedY = 2.0;
        distanceBetweenEnemies = 55;
        // random between 300 and 200
        leaderDistanceFromCenterMax = 300;
        leaderDistanceFromCenterMin = 150;
        leaderDistanceFromCenter = random.nextInt(leaderDistanceFromCenterMax - leaderDistanceFromCenterMin) + leaderDistanceFromCenterMin;
        
        leaderMovingDirectionRandomizer = random.nextInt(101);
        if(leaderMovingDirectionRandomizer > 75 ) {
            movingInAngle = 85;
            movingFixedAngle = 90;
        } else {
            movingInAngle = -85;
            movingFixedAngle = -90;
        }
    }
    
    public void draw(Graphics g) {

        if(leader) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        g.fillOval((int)x - size / 2, (int)y - size / 2, size, size);
    }
    
    public void update(TowerGame game) {

        this.game = game;
        double dx = game.getCenterX() - x;
        double dy = game.getCenterY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
    
        if (distance < (game.getVisionZone() / 2) ) {
            canShoot = true;
        }
    }

    public boolean canShoot() {
        return canShoot;
    }
    
    public void moveLeader(){
        // find the distance between the leader and the center of the screen.
        double dx = game.getCenterX() - x;
        double dy = game.getCenterY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if(leader && !flee) {   
            angle = Math.toDegrees(Math.atan2(game.getCenterY() - y, game.getCenterX() - x));                
             
            // if the distance is greater than leaderDistanceFromCenter, then move the leader towards the center of the screen until it reaches 250.
            if(distance > leaderDistanceFromCenter) {
                // random between 70 and 90
                angle = angle + movingInAngle;
            } else {
                angle = angle + movingFixedAngle;
            }
            
            x += leaderXSpeed * Math.cos(Math.toRadians(angle));
            y += leaderYSpeed * Math.sin(Math.toRadians(angle));
        
        }
    }

    public double calculateSpeed(double x1, double y1, double x2, double y2, double time) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double speed = distance / time;
        return speed;
    }

    public void move(TowerGame game) {

        if(flee && inSquad()){
            x += fleeSpeedX * Math.cos(Math.toRadians(angle));
            y += fleeSpeedY * Math.sin(Math.toRadians(angle));
        } else if(!leader){
            if(!arriving) { // meaning, simply wandering.

                x += xSpeed * Math.cos(Math.toRadians(angle));
                y += ySpeed * Math.sin(Math.toRadians(angle));
        
                // bounce off walls
                if(x < 0 || x > game.getWidth()) {
                    xSpeed = -xSpeed;
                }

                if(y < 0 || y > game.getHeight()) {
                    ySpeed = -ySpeed;
                }
            } else if(arriving){ // meaning, arriving to a target.
                Formation formation = this.getFormation(TowerGame.getAllFormations());
                formation.promoteLeader();
                Enemy target = formation.getLeader();

                // get the index of the previous enemy in the list. Then arrive near that enemy.
                int getIndex = formation.getList().indexOf(this);
                if(getIndex - 1 > 0) {
                    target = formation.getList().get(getIndex - 1);
                }
                
                targetX = target.getX();
                targetY = target.getY();

                arrive(targetX, targetY);
                
            }
        }
    }

    public void arrive(double targetX, double targetY) {
        
        if(!leader){

            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double angleToTarget = Math.toDegrees(Math.atan2(dy, dx));

            arriving = true;
            this.targetX = targetX;
            this.targetY = targetY;
            
            stopWander = true;

            if(distance > 0) {
                dx /= distance;
                dy /= distance;
            }

            // if the enemy arrived nearby, slow down until it stops.
            if(distance < distanceBetweenEnemies)  {

                // slowly reduce speed based on distance
                xSpeed -= distance * 0.1;
                ySpeed -= distance * 0.1;
                if(xSpeed < 0) {
                    xSpeed = 0;
                }
                if(ySpeed < 0) {
                    ySpeed = 0;
                }

            } else {
                xSpeed = maxSpeed;
                ySpeed = maxSpeed;
            }
            
            angle = angleToTarget;
            x += dx * xSpeed;
            y += dy * ySpeed;
        }

    }
    
    public void resetLeader() {
        leader = false;
    }

    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
    
    public void flee() {

        // run away from the center of the screen
        double dx = game.getCenterX() - x;
        double dy = game.getCenterY() - y;
        angle = Math.toDegrees(Math.atan2(-dy, -dx));
        leaderXSpeed = maxSpeed/2;
        leaderYSpeed = maxSpeed/2;
        flee = true;
    }
    
    public void wander() {
        if(!canShoot && !stopWander){
            int absoluteAngle = 25;  
            double speedFactor = 0.25;
            if (random.nextDouble() < speedFactor) {
                angle += (random.nextDouble() * (absoluteAngle*2)) - absoluteAngle;
            }
        }
    }

    public Boolean isFleeing() {
        return flee;
    }

    public void enrollSquad() {
        isInSquad = true;
    }

    public Boolean inSquad() {
        return isInSquad;
    }

    public void resetSquad() {
        isInSquad = false;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isOutOfBounds(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public void resetArriving() {
        arriving = false;
    }
    public Boolean isLeader(){
        return leader;
    }

    public void setLeader(){
        leader = true;
        resetArriving();
    }

    public void stopWandering() {
        stopWander = false;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    // get the formation that this enemy belongs to.
    public Formation getFormation(ArrayList<Formation> formations) {
        Formation formation = null;
        Boolean found = false;
        for(int i = 0; i < formations.size() && !found; i++) {
            if(formations.get(i).getList().contains(this)) {
                formation = formations.get(i);
                found = true;
            }
        }
        return formation;
    }

    public Enemy getPosition(){
        return new Enemy(x, y);
    }

    public double getLeaderXSpeed(){
        return leaderXSpeed;
    }

    public double getLeaderYSpeed(){
        return leaderYSpeed;
    }

    public double getLeaderSpeed(){
        return (leaderXSpeed + leaderYSpeed)/2;
    }

    public double getDistanceToTarget() {
        // return distance between this enemy and the center tower.
        double distance = Integer.MAX_VALUE;
        if(isLeader()){
            return Math.sqrt(Math.pow(x - game.getCenterX(), 2) + Math.pow(y - game.getCenterY(), 2));
        }
        return distance;
    }

    public void setLeaderSpeed(double bestSpeedX, double bestSpeedY) {
        leaderXSpeed = bestSpeedX;
        leaderYSpeed = bestSpeedY;
    }

    
}//class