//  CellDE_Settings.java 
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.nichao.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.cellde.CellDE;
import jmetal.nichao.MyProblemFactory;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of algorithm CellDE
 */
public class MyCellDE_Settings extends Settings{

  public double CR_           ;
  public double F_            ;

  public int archiveSize_     ;
  public int archiveFeedback_ ;

  public int populationSize_;
  public int maxEvaluations_;
  public double mutationProbability_;
  public double crossoverProbability_;
  public double mutationDistributionIndex_;
  public double crossoverDistributionIndex_;


  /**
   * Constructor
   */
  public MyCellDE_Settings(String problemName) {
    super(problemName) ;

    Object [] problemParams = {"Binary"};
    try {
	    problem_ = (new MyProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }

    // Default experiments.settings
    CR_          = 0.5;
    F_           = 0.5    ;

    archiveSize_    = 100   ;
    archiveFeedback_= 20    ;

    populationSize_ = 100;
    maxEvaluations_ = 3000;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
    mutationDistributionIndex_ = 20.0;
    crossoverDistributionIndex_ = 20.0;
  } // CellDE_Settings

  /**
   * Configure the algorithm with the specified parameter experiments.settings
   * @return an algorithm object
   * @throws JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;

    HashMap  parameters ; // Operator parameters

    // Creating the problem

    algorithm = new CellDE(problem_) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("archiveFeedBack", archiveFeedback_);

    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);


    // Mutation and Crossover for Binary codification
    parameters = new HashMap();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_);
    //这个交叉编译不适合
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);

   /* parameters = new HashMap();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    //这个编译也是不合适的  ,只有BitFlipMutation这么一个编译算子支持BinaryType
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);
*/
    // Selection Operator
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("selection", selection);

    return algorithm;
  } // configure

  /**
   * Configure CellDE with user-defined parameter experiments.settings
   * @return A CellDE algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm;
    Selection selection;
    Crossover crossover;

    HashMap  parameters ; // Operator parameters

    // Creating the algorithm.
    algorithm = new CellDE(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations",String.valueOf(maxEvaluations_)));
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("archiveFeedBack", archiveFeedback_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    // Mutation and Crossover for Binary codification
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
    crossoverDistributionIndex_ = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex_)));
    parameters = new HashMap();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_);
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);

   /* mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    parameters = new HashMap();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);*/

    // Selection Operator
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("selection", selection);
    return algorithm;
  }
} // CellDE_Settings
