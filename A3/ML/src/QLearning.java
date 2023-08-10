/* -----------------------------------------------------------------------------
    Author: Saif Mahmud
    Date: 2023-06-08 (yyyy-dd-mm)
    Course: COMP 452
    Student ID: 3433058
    Assignment: 3
    Question: 2
    Description: 
    
    QLearning class to implement the Q-learning algorithm.
*/
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QLearning {
    private Map<Double, Double> qValues; // map to store the Q-values for each speed
    private double learningRate; // learning rate
    private double discountFactor; // discount factor
    private double explorationRate; // exploration rate
    private Random random; // random number generator
    private Enemy leader; // the leader
    
    public QLearning(double learningRate, double discountFactor, double explorationRate) {
        qValues = new HashMap<>();
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationRate = explorationRate;
        random = new Random();
    }
    
    public double getBestSpeed(Enemy leader, boolean hitTarget) {
        this.leader = leader;
        double bestSpeed = leader.getLeaderSpeed();
        double maxQValue;
        double reward = 0;
        // iterate over all speeds and get the best speed based on the Q-values
        for (int i = 0; i < qValues.size(); i++) {
            double speed = (double) qValues.keySet().toArray()[i];
            double qValue = (double) qValues.values().toArray()[i];

            // if the speed is too high or too low, set it to the maximum or minimum speed
            double formationSpeed = leader.getFormation(TowerGame.getAllFormations()).getFormationSpeed();
            if(speed > formationSpeed){
                qValues.remove(speed);
                speed = formationSpeed;
                qValues.put(speed, qValue);
            } else if(speed < 0.01){
                qValues.remove(speed);
                speed = 0.01;
                qValues.put(speed, qValue);
            }

            // set the reward based on whether the projectile hit the target or not
            if (hitTarget) {
                // if the projectile hit the target, set the reward to 1
                reward = 1;
            } else {
                // if the projectile missed the target, set the reward to -1
                reward = -1;
            }

            maxQValue = getMaxQValue();

            // the equation
            double newQValue = (1-learningRate) * qValue + learningRate * (reward + discountFactor * maxQValue);
            qValues.put(speed, newQValue);
            
            if (newQValue > maxQValue) {
                maxQValue = newQValue;
                bestSpeed = speed;
            }
        }
        
        // with probability explorationRate, choose a random speed between +-0.01 of the best speed
        if (random.nextDouble() < explorationRate) {
            double range = 0.1;
            bestSpeed = bestSpeed + (random.nextDouble() * range * 2 - range);

            // set the speed to the maximum or minimum speed if it is too high or too low
            if(bestSpeed < 0.01){
                bestSpeed = 0.01;
            } else if(bestSpeed > leader.getFormation(TowerGame.getAllFormations()).getFormationSpeed()){
                bestSpeed = leader.getFormation(TowerGame.getAllFormations()).getFormationSpeed();
            }
        }
        
        return bestSpeed;
    }
    
    private double getMaxQValue() {
        double maxQValue = Double.NEGATIVE_INFINITY;
        
        // iterate over all speeds and get the maximum Q-value based on the distance to the target
        for (int i = 0; i < qValues.size(); i++) {
            double qValue = (double) qValues.values().toArray()[i];
            
            if (leader != null) {
                //if 3 hit in a row, add a bonus to the Q-value
                if(leader.getFormation(TowerGame.getAllFormations()).hitInARow(3)) {
                    qValue += 0.1;
                }
            }
            
            if (qValue > maxQValue) {
                maxQValue = qValue;
            }
        }
        
        return maxQValue;
    }
}