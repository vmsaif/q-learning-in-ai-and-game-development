import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.Timer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TowerGame extends JPanel {

    private final int TOWER = 0;
    private final int ENEMY = 1;
    private int gunLength;
    private int gunAngle; 
    private ArrayList<Projectile> projectiles; // list of all player's/tower's bullets

    private ArrayList<Enemy> enemies; // list of all enemies
    private ArrayList<Projectile> enemyProjectiles; // list of all enemy bullets
    
    private int centerX; // x position of the tower
    private int centerY; // y position of the tower
    private int towerRadius; // radius of the tower

    private int visionZone; // radius of the enemie's battle zone

    private Timer timer; // timer for the game loop
    private Random random; // random number generator

    private boolean enemiesSpawned; // true if enemies have been spawned
    private int playerProjectileSpeed; 
    private int enemyProjectileSpeed;

    private int enemyFireDelay; // delay between enemy shots in milliseconds
    private int enemyFireTimer; // current time since last enemy shot

    private int towerHealth; // initial tower health
    private int healthBarWidth; // width of the health bar
    private int healthBarHeight; // height of the health bar
    private int healthBarX; // x position of the health bar
    private int healthBarY; // y position of the health bar
    private int enemyAttackPower; 
    private int formationManLimit; // max number of enemies in a formation
    private int numberOfEnemySpawn; // number of enemies to spawn
    private ArrayList<Formation> allFormations; // list of all formations
    private int minEnemiesBeforeFlee; // min number of enemies before fleeing
    private boolean win;
    private boolean loose;
    private AudioInputStream bgMusic;
    private int shootingErrorMargin;
    private double error; 

    public TowerGame() throws LineUnavailableException, IOException{

        random = new Random();

        gunLength = 50;
        gunAngle = 0;
        projectiles = new ArrayList<>();

        enemies = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        allFormations = new ArrayList<>();
               
        towerRadius = 25;
        visionZone = 600;
         
        enemiesSpawned = false;
        playerProjectileSpeed = 10;
        enemyProjectileSpeed = 3;
        shootingErrorMargin = 10;
        error = random.nextDouble() * 10 - shootingErrorMargin; // generate a random error between -5 and 5 degrees
        
        enemyFireDelay = 700; // delay between enemy shots in milliseconds
        enemyFireTimer = 0; // current time since last enemy shot

        towerHealth = 100; // initial tower health
        healthBarWidth = 200; // width of the health bar
        healthBarHeight = 20; // height of the health bar
        healthBarX = 25; // x position of the health bar
        healthBarY = 25; // y position of the health bar

        enemyAttackPower = 10;
        numberOfEnemySpawn = 12;
        formationManLimit = 4;
        minEnemiesBeforeFlee = 2;
        
        win = false;
        loose = false;

        
        try {
            bgMusic = loadSound(new File("Elevator-music.wav"));
           
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }


        //start background music
        // playSound(bgMusic);

        // add mouse motion listener to the panel
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(!checkGameOver()){
                    fireProjectile();
                }
            }
        });
        
        // create a timer to update the game
        timer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEnemyProjectile();
                updateProjectiles();
                updateEnemies();
                formation();
                repaint();
            }
        });
        timer.start();
    
        
        addMouseMotionListener(new MouseMotionAdapter() {

            // when the mouse is moved, update the gun angle
            public void mouseMoved(MouseEvent e) {
                // calculate the angle between the mouse and the center of the tower
                if(!checkGameOver()){
                    centerX = getWidth() / 2;
                    centerY = getHeight() / 2;
                    double dx = e.getX() - centerX;
                    double dy = e.getY() - centerY;
                    gunAngle = (int)Math.toDegrees(Math.atan2(dy, dx));
                    repaint();
                }
            }

            // when the mouse is dragged, update the gun angle by calling mouseMoved
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

        });
    }

    // audio loader
    private AudioInputStream loadSound(File filename) throws UnsupportedAudioFileException, IOException {
        URL url = filename.toURI().toURL();
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
        return audioIn;
    }

    private void playSound(AudioInputStream audioIn) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //draw the game components 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        g.setColor(Color.BLACK);

        // draw the tower
        g.drawOval(centerX - towerRadius, centerY - towerRadius, towerRadius*2, towerRadius*2);

        // draw the vision zone
        g.setColor(Color.RED);
        g.drawOval(centerX - visionZone/2, centerY - visionZone/2, visionZone, visionZone);

        // draw the gun
        g.drawLine(
            centerX, //x1
            centerY, //y1
            centerX + (int)(gunLength * Math.cos(Math.toRadians(gunAngle))), //x2
            centerY + (int)(gunLength * Math.sin(Math.toRadians(gunAngle)))  //y2
        );

        // draw the projectiles
        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).draw(g); // draw the projectile
        }

        if (!enemiesSpawned) {
            spawnEnemies();
            enemiesSpawned = true;
        }

        // draw the enemies
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g); // draw the projectile
        }

        // draw the enemy projectiles
        for (int i = 0; i < enemyProjectiles.size(); i++) {
            enemyProjectiles.get(i).draw(g); // draw the projectile
        }

        // draw the health bar
        g.setColor(Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(Color.GREEN);
        g.fillRect(healthBarX, healthBarY, (int) (healthBarWidth * ((double) towerHealth / 100)), healthBarHeight);

        printGameOver(g);
    }

    // print if win or loose on the screen
    private void printGameOver(Graphics g) {
        if(checkGameOver()){

            String gameOver = "";
            if(win){
                g.setColor(Color.GREEN);
                Font gameOverFont = new Font("TimesRoman", Font.PLAIN, 50);
                g.setFont(gameOverFont);
                gameOver = "You Win!";
                
            } else if(loose){
                g.setColor(Color.RED);
                Font gameOverFont = new Font("TimesRoman", Font.PLAIN, 50);
                g.setFont(gameOverFont);
                gameOver = "You Loose!";
            }
            g.drawString(gameOver, 250, 200);
        }
    }

    // fire a projectile from the tower
    private void fireProjectile() {
        int centerX = getWidth() / 2; // get the center x position of the tower
        int centerY = getHeight() / 2; // get the center y position of the tower
        double x = centerX + gunLength * Math.cos(Math.toRadians(gunAngle)); // calculate the x position of the projectile
        double y = centerY + gunLength * Math.sin(Math.toRadians(gunAngle)); // calculate the y position of the projectile
        Projectile p = new Projectile(x, y, gunAngle, playerProjectileSpeed); // create a new projectile/bullet
        projectiles.add(p); 
    }

    // fire a projectile from a random enemy given that the enemy has permission range to shoot
    public void fireEnemyProjectile() {
        if (enemiesSpawned && enemies.size() > 0) {
            enemyFireTimer += 10; // increment the timer by the timer interval (10ms)
            if (enemyFireTimer >= enemyFireDelay) { // check if enough time has passed since the last enemy shot
                enemyFireTimer = 0; // reset the timer
                
                int randomIndex = random.nextInt(enemies.size()); // randomly select an enemy to shoot from
                Enemy currEnemy = enemies.get(randomIndex); // get a random enemy
                
                if (currEnemy.canShoot()) {
                    double dx = centerX - currEnemy.getX();
                    double dy = centerY - currEnemy.getY();
                    double angle = Math.toDegrees(Math.atan2(dy, dx));
                    angle += error; // add the error to the angle
                    Projectile projectile = new Projectile(currEnemy.getX(), currEnemy.getY(), angle, enemyProjectileSpeed);
                    enemyProjectiles.add(projectile); 
                }
            }
        }
    }

    // remove projectiles
    private void updateProjectiles() {

        // projectile of player
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile currentBullet = projectiles.get(i);
            currentBullet.move(); // move the bullet

            // remove the bullet if it is out of the screen or if it hit an enemy
            removeProjectileIf(currentBullet, i, ENEMY, projectiles);
        }//outer for

        // projectile of enemy
        for (int i = 0; i < enemyProjectiles.size(); i++) {
            Projectile currentEnemyBullet = enemyProjectiles.get(i);
            currentEnemyBullet.move();

            // remove the bullet if it is out of the screen or if it hit the tower
            removeProjectileIf(currentEnemyBullet, i, TOWER, enemyProjectiles);
        }//outer for

    }

    // remove the projectile if it is out of the screen or if it hit an enemy or the tower
    private void removeProjectileIf(Projectile currentBullet, int i, int targetType, ArrayList<Projectile> removingArrayList) {

        if (currentBullet.isOutOfBounds(getWidth(), getHeight())) {
            removingArrayList.remove(i);
            i--;
        } else {

            // check if the bullet hit an enemy
            if(targetType == ENEMY){
                for (int j = 0; j < enemies.size(); j++) {
                    Enemy enemy = enemies.get(j);
                    
                    if (currentBullet.intersects(enemy)) {

                        // remove the bullet 
                        if(removingArrayList.size()>0){
                            removingArrayList.remove(i);
                        }
                        
                        if(i > 0)
                        {
                            i--;
                        }
                        
                        // also remove the enemy from the enemy formations and all enemy list
                        Enemy enemyToRemove = enemies.remove(j);
                        removeFromFormation(enemyToRemove);

                        if(j > 0){
                            j--;
                        }
                        
                    }
                }

            } else if (targetType == TOWER){
                
                // check if the enemy bullet hit the tower
                if (isInsideOval(currentBullet.getIntProjectileX(), currentBullet.getIntProjectileY(), centerX, centerY, towerRadius*2)) {
                    removingArrayList.remove(i);
                    towerHealth -= enemyAttackPower; // reduce the tower's health by 10

                    if(i > 0)
                    {
                        i--;
                    }
                }
                
            }
        }//else
    }

    private boolean checkGameOver() {

        Boolean result = false;

        // // check if the tower's health is 0 or less
        // if (towerHealth <= 0) {
        //     timer.stop();
        //     loose = true;
        //     result = true;
        // } else if(enemiesSpawned && enemies.size() == 0){ // check if all enemies are dead
        //     timer.stop();
        //     result = true;
        //     win = true;
        // }

        return result;
    }

    // remove the enemy from the formation
    private void removeFromFormation(Enemy enemyToRemove) {
        Formation formation = enemyToRemove.getFormation(allFormations);
        if(formation != null){
            formation.removeEnemy(enemyToRemove);
        }
    }
    
    // update the current state of the enemies
    private void updateEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy currEnemy = enemies.get(i);
            currEnemy.move(this); // simply move the enemy at a direction and bounces back. 
            currEnemy.update(this); // checks if the enemy can shoot
            currEnemy.moveLeader();
            currEnemy.wander();
            

            if(currEnemy.isFleeing() && currEnemy.isOutOfBounds(getWidth(), getHeight())){
                enemies.remove(i);
                i--;
            }
        }

        // flee
        for (int i = 0; i < allFormations.size() ; i++) {
            Formation currFormation = allFormations.get(i);
            if(currFormation.getList().size() < minEnemiesBeforeFlee){
                currFormation.flee();
            }
        }
    }
    
    // create formations and assign leader, enemies to them
    public void formation() {
        
        Formation newFormation = null;

        for(int i = 0; i < enemies.size(); i++){
            Enemy currEnemy = enemies.get(i);

            if(currEnemy.canShoot()){

                // enemy is in the red circle.
                Enemy objToGo = null;
                
                // if the enemy has no leader, select it or someone else to be the leader
                Formation tempFormation = currEnemy.getFormation(allFormations);
                if(tempFormation != null){
                    if(tempFormation.hasLeader() == false){
                        tempFormation.promoteLeader();
                         
                    }
                }

                if(currEnemy.inSquad() == false){
                    // no formation yet, so, not in any squad. create a new one
                    newFormation = new Formation();
                    allFormations.add(newFormation); 
                    newFormation.addEnemy(currEnemy);
                    currEnemy.setLeader();
                    currEnemy.stopWandering();

                    //creating a formation and searching for the closest enemy to join beside the leader.
                
                    // find 3 closest enemies from all enemies, then add it to the squad
                    for(int j = 0; j < formationManLimit-1; j++) { // limit the number of enemies in a squad. now its 3
                        Enemy currentClosest = enemies.get(0);
                        double currentClosestDistance = Double.MAX_VALUE;
                        for(int k = 0; k < enemies.size(); k++){
                            Enemy otherEnemy = enemies.get(k);
                            if(otherEnemy != currEnemy && otherEnemy.inSquad() == false && otherEnemy.isLeader() == false){
                                double distance = distanceTo(otherEnemy, currEnemy);
                                if(distance < currentClosestDistance){
                                    currentClosest = otherEnemy;
                                    currentClosestDistance = distance;
                                }
                            }
                        }

                        newFormation.addEnemy(currentClosest);
                        currentClosest.stopWandering();
                        currentClosest.canShoot();

                        // now arrive in squad
                        if(j == 0){
                            // go to the leader
                            objToGo = newFormation.getLeader(); 
                        } else {
                            // go to the previous closest enemy
                            if(newFormation.getList().size()>0){
                                objToGo = newFormation.getList().get(j);
                            }
                        }
                        double x = objToGo.getX();
                        double y = objToGo.getY();
                        currentClosest.arrive(x, y); 
                    } 
                }
            }
        }
    }

       // distance between two enemies
    public double distanceTo(Enemy otherEnemy, Enemy currEnemy) {
        double dx = otherEnemy.getX() - currEnemy.getX();
        double dy = otherEnemy.getY() - currEnemy.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public ArrayList<Projectile> getAllProjectiles() {
        return projectiles;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getVisionZone() {
        return visionZone;
    }

    // spawn 10 enemies at random locations outside the vision zone
    private void spawnEnemies() {
        
        for (int i = 0; i < numberOfEnemySpawn; i++) {
            double x, y;
            do {
                x = random.nextDouble() * getWidth();
                y = random.nextDouble() * getHeight();
            } while (isInsideOval(x, y, centerX, centerY, visionZone));
            enemies.add(new Enemy(x, y));
        }
    }
    
    // check if a point is inside an oval
    public boolean isInsideOval(double x, double y, double centerX, double centerY, double diameter) {
        double dx = x - centerX;
        double dy = y - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < diameter / 2;
    }

    public ArrayList<Formation> getAllFormations() {
        return allFormations;
    }
}//class
