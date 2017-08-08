//  MOCell_Settings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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
//

package jmetal.nichao.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.mochc.MOCHC;
import jmetal.nichao.MyProblemFactory;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 17/06/13
 * Time: 23:40
 * To change this template use File | Settings | File Templates.
 */
public class MyMOCHC_Settings extends Settings {
  int populationSize_  ;
  int maxEvaluations_  ;

  double initialConvergenceCount_ ;
  double preservedPopulation_     ;
  int    convergenceValue_        ;
  double crossoverProbability_    ;
  double mutationProbability_     ;


  public double mutationDistributionIndex_;
  public double crossoverDistributionIndex_;

  public MyMOCHC_Settings(String problemName) {
    super(problemName);

    Object [] problemParams = {"Binary"};
    try {
      problem_ = (new MyProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
      e.printStackTrace();
    }
    // Default experiments.settings
    populationSize_ = 100 ;
    maxEvaluations_ = 3000 ;
    initialConvergenceCount_ = 0.25 ;
    preservedPopulation_ = 0.05 ;
    convergenceValue_ = 3 ;

    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9;
    mutationDistributionIndex_ = 20.0;
    crossoverDistributionIndex_ = 20.0;

  }

  /**
   * Configure MOCHC with user-defined parameter experiments.settings
   * @return A MOCHC algorithm object
   * @throws JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator crossover      ;
    Operator mutation       ;
    Operator parentsSelection       ;
    Operator newGenerationSelection ;

    HashMap parameters ; // Operator parameters

    // Creating the problem
    algorithm = new MOCHC(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("initialConvergenceCount",initialConvergenceCount_);
    algorithm.setInputParameter("preservedPopulation",preservedPopulation_);
    algorithm.setInputParameter("convergenceValue",convergenceValue_);
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

    // Crossover operator
    parameters = new HashMap();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_);
    //这个交叉编译不适合
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);


    parameters = new HashMap();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    //这个编译也是不合适的  ,只有BitFlipMutation这么一个编译算子支持BinaryType
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);





    parameters = null ;
    parentsSelection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;

    parameters = new HashMap() ;
    parameters.put("problem", problem_) ;
    newGenerationSelection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;



    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("cataclysmicMutation",mutation);
    algorithm.addOperator("parentSelection",parentsSelection);
    algorithm.addOperator("newGenerationSelection",newGenerationSelection);

    return algorithm ;
  } // configure

  /**
   * Configure MOCHC with user-defined parameter experiments.settings
   * @return A MOCHC algorithm object
   */
  @Override
  public Algorithm configure(Properties configuration) throws JMException {
    Algorithm algorithm ;
    Operator crossover      ;
    Operator mutation       ;
    Operator parentsSelection       ;
    Operator newGenerationSelection ;

    HashMap  parameters ; // Operator parameters

    algorithm = new MOCHC(problem_) ;

    // Algorithm parameters
    populationSize_ = Integer.parseInt(configuration.getProperty("populationSize",String.valueOf(populationSize_)));
    maxEvaluations_  = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
    initialConvergenceCount_ = Double.parseDouble(configuration.getProperty("initialConvergenceCount", String.valueOf(initialConvergenceCount_)));
    preservedPopulation_ = Double.parseDouble(configuration.getProperty("preservedPopulation", String.valueOf(preservedPopulation_)));
    convergenceValue_  = Integer.parseInt(configuration.getProperty("convergenceValue", String.valueOf(convergenceValue_)));

    algorithm.setInputParameter("initialConvergenceCount",initialConvergenceCount_);
    algorithm.setInputParameter("preservedPopulation",preservedPopulation_);
    algorithm.setInputParameter("convergenceValue",convergenceValue_);
    algorithm.setInputParameter("populationSize",populationSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);

    // Mutation and Crossover for Real codification
    crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability",String.valueOf(crossoverProbability_)));
    parameters = new HashMap();
    parameters.put("probability", crossoverProbability_);
    parameters.put("distributionIndex", crossoverDistributionIndex_);
    //这个交叉编译不适合
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);


    mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
    mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
    parameters = new HashMap();
    parameters.put("probability", mutationProbability_);
    parameters.put("distributionIndex", mutationDistributionIndex_);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);



    parameters = null ;
    parentsSelection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters)  ;


    // Selection Operator
    parameters = new HashMap() ;
    parameters.put("problem", problem_) ;
    newGenerationSelection = SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection", parameters) ;

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("cataclysmicMutation",mutation);
    algorithm.addOperator("parentSelection",parentsSelection);
    algorithm.addOperator("newGenerationSelection",newGenerationSelection);

    return algorithm ;
  }
}
