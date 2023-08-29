/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-06-08 (yyyy-dd-mm)
*/
import java.util.ArrayList;

public class Formation {

    private ArrayList<Enemy> enemyGroup;
    private boolean hasLeader;
    private QLearning qLearning;
    private ArrayList<Boolean> hitMissList;
    private Double formationSpeed;

    public Formation() {
        formationSpeed = 0.8;
        enemyGroup = new ArrayList<Enemy>();
        hitMissList = new ArrayList<Boolean>();
        hasLeader = false;
        qLearning = new QLearning(0.7, 0.9, 0.5);
        
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
            leader.setLeaderSpeed(formationSpeed, formationSpeed);
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
            formationSpeed = bestSpeed;
        }
    }

    public QLearning getQLearning() {
        return qLearning;
    }

    public void addHitMiss(boolean hitMiss) {
        hitMissList.add(hitMiss);
    }

    // used for bonus point in the qvalue.
    public boolean hitInARow(int num) {
        boolean hitInRow = false;
        int count = 0;
        for(int i = hitMissList.size() - 1; i >= 0 && !hitInRow; i--) {
            if(hitMissList.get(i)) {
                count++;
            }
            if(count == num) {
                hitInRow = true;
            }
        }
        return hitInRow;
    }

    public void setFormationSpeed(double speed) {
        formationSpeed = speed;
    }

    public double getFormationSpeed() {
        return formationSpeed;
    }
}
