/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-17-07
    Course: COMP 452
    Student ID: 3433058
    Assignment: 1
    Description: Creating a tower defense game with the implementation of Basic steering behaviors such as wander, arrive, flee and a complex steering behavior of formation.
    Class Description: This class creates the formation of enemies.
*/
import java.util.ArrayList;

public class Formation {

    private ArrayList<Enemy> enemyGroup;
    private boolean hasLeader;
    private QLearning qLearning;
    public Formation() {
        enemyGroup = new ArrayList<Enemy>();
        hasLeader = false;
        qLearning = new QLearning(0.01, 0.9, 0.1);
    }

    public void addEnemy(Enemy enemy) {
        if(!hasLeader){
            promoteLeader();
        }
        enemyGroup.add(enemy);
        enemy.enrollSquad();
        enemy.canShoot();

    }

    public void removeEnemy(Enemy enemy) {
        if(enemy.isLeader()){
            hasLeader = false;
        }
        enemyGroup.remove(enemy);

    }   

    public void promoteLeader() {
        if(enemyGroup.size() > 0){
            Enemy leader = enemyGroup.get(0);
            leader.setLeader();
            leader.resetArriving();
            getLeader().moveLeader();
            hasLeader = true;
        }
    }

    public Enemy getLeader() {
        Enemy leader = null;
        if(enemyGroup.size() > 0){
            leader = enemyGroup.get(0);
        }
        return leader;
    }

    public int getSize() {
        return enemyGroup.size();
    }

    public boolean hasLeader() {
        return hasLeader;
    }

    public ArrayList<Enemy> getList() {
        return enemyGroup;
    }

    public int getIndex(Enemy enemy) {
        return 0;
    }

    public void flee() {
        hasLeader = false;
        for(int i = 0; i < enemyGroup.size(); i++) {
            Enemy enemy = enemyGroup.get(i);
            enemy.resetArriving();
            enemy.flee();
            enemy.stopWandering();
        }
    }

    public double getDistanceToTarget() {
        double distance = 0;
        if(hasLeader){
            distance = getLeader().getDistanceToTarget();
        }
        return distance;
    }

    public void setLeaderSpeed(double bestSpeed) {
        if(hasLeader){
            getLeader().setLeaderSpeed(bestSpeed, bestSpeed);
        }
    }

    public QLearning getQLearning() {
        return qLearning;
    }
}
