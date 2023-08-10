# Tower Defense Game : Q Learning Algorithm
This is a tower defense game where you defend a tower from enemy attacks. The game is written in Java and uses the Swing library for graphics. It also uses the Q Learning algorithm to train the enemy agents to reduce their attack angle error.

## Logic and Design
At the start, the player will see a tower at the center of the screen with a gun. The gun aims where the mouse is pointed at. There is a circular zone from the tower. Outside this zone, there are enemies spawned and they are scattered. Once an enemy reaches the border of that circle, he forms a team nearest to him and becomes leader. The team starts shooting the tower which has hp. Several teams like these are formed similarly. Slowly they approach towards the tower upto a certain distance. If only two of the enemy team formation alive, he flees away at the opposite direction. 

## Q Learning Algorithm

The enemies are quick on feet. After the enemies form formations at the beginning (After entering the red circle zone), They will start moving around at their maximum speed and starts fire torwards the tower. However, soon they will realise they are missing the shots. So they will start to adjust their aim. This is where the Q Learning algorithm comes in. 

The aim angle error is based on the speed of the enemy leader/formation and the distance they have between the tower. The error is calculated by the following formula:

```
error = (leader.getLeaderXSpeed() + leader.getLeaderYSpeed()) / (distanceToTower / 1000);
shootingAngle += error; // add the error to the angle
```

However, the agents will not know this equation, hence will not know what causes them missing the shots. The Q learning is used to train the agents to reduce the error. It is implanted in each formation. Meaning, the optimization by this algorithm is different for each formation. 

The Q learning algorithm is initialized in the formation class. The Q learning algorithm is used in the following steps:

- To get the best speed, the method takes in the formation/leader's current speed and weather or not the last bullet hit the tower. 

- With a minimum possible integer value as q value, it stores the best speed and qvalue in a hashmap.
- It sets the reward to 1 if the bullet hits the tower, else -1.
- It then calculates the q value using the following formula:

```
double newQValue = (1-learningRate) * qValue + learningRate * (reward + discountFactor * maxQValue);
```
- Then it updates the q value in the hashmap.
- Then it returns the best speed so far.

## How to Compile and Run

- Navigate to the root directory of the project in your terminal.

### Option 1: Via Terminal

- Run the command `javac -d bin src/*.java` to compile the Java files in the src directory to bin/ directory.
- Run the command `java -cp bin/ Main` to run the main game.

### Option 2: Directly Run

- Just run the ML.jar file to start.

## Controls
- Move the mouse to aim the gun
- Click the mouse to fire the gun

## Steering Behaviours and Features
- After spawning, the enemy keeps **WANDERING** around outside the red circle.
- Enemies form teams in a linear **FORMATION** as complex steering behaviour and attack the tower together
    - During the formation of the team, the leader is choosen.
    - Then the enemies nearby **ARRIVES** near it's leader
- The enemy **FLEES** when only one in the team alive.
- Tower has health points (HP) that decrease when hit by enemy bullets
- Game over when the tower's HP reaches 0

## Credits
Background music: [Local Forecast – Elevator by Kevin MacLeod](https://www.chosic.com/download-audio/29282/)

