# multi-object
the project is used to implement experimental studies for multi-object issues.

we use six state-of-the-art methods (e.g.,  NSGAII, MOCell, SPEA2, PAES, SMSEMOA, RandomSearch) to study the software defect prediction (SDP) issues on two datasets (e.g., PROMISE, ReLink). At the same time, we take four classifier methods (e.g., J48, KNN, LR, NB) into consideration.
The aim of this project is to configure out which one of multi-objective optimization method is most suitable for solving SDP.


##USAGE (IDEA):
1) git clone https://github.com/jacknichao/multi-object.git

2) import this project with IDEA

3) build this project

4) create an artifact and make sure that the main class is "jmetal.nichao.Driver".
   after that ,a jar will be created, such as multi-object.jar

5) edit /libs/config.properties and provide the required fields

6) make a copy of /libs/config.properties

7) put config.properties and multi-object.jar in same directory

8) write down this command 'java -jar multi-object.jar', then press the return key, 
   
9) up here, you will see running informations on the terminal screen 
