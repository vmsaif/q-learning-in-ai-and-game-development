import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QLearning {
    private Map<Double, Double> qValues; // map to store the Q-values for each speed
    private double learningRate; // learning rate
    private double discountFactor; // discount factor
    private double explorationRate; // exploration rate
    private Random random; // random number generator
    
    public QLearning(double learningRate, double discountFactor, double explorationRate) {
        qValues = new HashMap<>();
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationRate = explorationRate;
        random = new Random();
    }
    
    public double getBestSpeed(Enemy leader, double distanceToTarget, boolean hitTarget) {
        double bestSpeed = leader.getLeaderXSpeed();
        double maxQValue = Double.NEGATIVE_INFINITY;
        double reward = 0;
        // iterate over all speeds and get the best speed based on the Q-values
        for (Map.Entry<Double, Double> entry : qValues.entrySet()) {
            double speed = entry.getKey();
            double qValue = entry.getValue();

            // if the speed is too high or too low, set it to the maximum or minimum speed
            if(speed > 1.3){
                qValues.remove(speed);
                speed = 1.3;
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

            // the equation
            double newQValue = qValue + learningRate * (reward + discountFactor * getMaxQValue(distanceToTarget) - qValue);
            qValues.put(speed, newQValue);
            
            if (qValue > maxQValue) {
                maxQValue = qValue;
                bestSpeed = speed;
            }
        }
        
        // with probability explorationRate, choose a random speed instead of the best speed
        if (random.nextDouble() < explorationRate) {
            bestSpeed = random.nextDouble() * 0.5; // choose a random speed between 0 and 0.5
        }
        
        return bestSpeed;
    }
    
    private double getMaxQValue(double distanceToTarget) {
        double maxQValue = Double.NEGATIVE_INFINITY;
        
        // iterate over all speeds and get the maximum Q-value based on the distance to the target
        for (Map.Entry<Double, Double> entry : qValues.entrySet()) {
            double qValue = entry.getValue();
            
            if (distanceToTarget < 100) {
                // if the distance to the target is less than 100, add a bonus to the Q-value
                qValue += 0.1;
            }
            
            if (qValue > maxQValue) {
                maxQValue = qValue;
            }
        }
        
        return maxQValue;
    }
}