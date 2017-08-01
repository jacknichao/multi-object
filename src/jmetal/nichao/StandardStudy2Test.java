//  StandardStudy2.java
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

package jmetal.nichao;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.*;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 在真实的实验环境中，无法事先提供Pareto fronts，当前的实验设置符合我们的真实的实验环境
 * 并且，该程序运行过程中会自己生成所需要的Pareto fronts文件
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 * In this experiment, we assume that the true Pareto fronts are unknown, so 
 * they must be calculated automatically. 
 */
public class StandardStudy2Test extends Experiment {

  /**
   * Configures the algorithms in each independent run
   * @param problemName The problem to solve
   * @param problemIndex
   * @throws ClassNotFoundException 
   */
  public void algorithmSettings(String problemName, 
  		                          int problemIndex, 
  		                          Algorithm[] algorithm) throws ClassNotFoundException {
    try {
      int numberOfAlgorithms = algorithmNameList_.length;

      HashMap[] parameters = new HashMap[numberOfAlgorithms];

      for (int i = 0; i < numberOfAlgorithms; i++) {
        parameters[i] = new HashMap();
      } // for

      if (!(paretoFrontFile_[problemIndex] == null) && !paretoFrontFile_[problemIndex].equals("")) {
        for (int i = 0; i < numberOfAlgorithms; i++)
          parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
        } // if

      //设定对一个的算法
      algorithm[0] = new NSGAII_SettingsTest(problemName).configure(parameters[0]);

      } catch (IllegalArgumentException ex) {
      Logger.getLogger(StandardStudy2Test.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(StandardStudy2Test.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(StandardStudy2Test.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    StandardStudy2Test exp = new StandardStudy2Test();

    exp.experimentName_ = "StandardStudy2Test";
    //当前实验环境中，仅仅使用一个algorithm来测试
    exp.algorithmNameList_ = new String[]{"NSGAII"};
    exp.problemList_ = new String[]{"FS"};

    //对于实际的研究问题，不需要制定pareto front文件，但是需要为其初始化空间
    exp.paretoFrontFile_ = new String[1] ; // Space allocation for specific fronts

   exp.indicatorList_ = new String[]{"HV", "SPREAD", "EPSILON"};
  //  exp.indicatorList_ = null;

    int numberOfAlgorithms = exp.algorithmNameList_.length;

    exp.experimentBaseDirectory_ = "/home/jacknichao/ideaProjects/multi-object/results/"+ exp.experimentName_;

    //对于实际的研究问题，在不制定pareto front文件情况下，此处必须为空

     exp.paretoFrontDirectory_ = "" ; // This directory must be empty

    //这里是为每一个算法初始化设值，在单独的每一轮运行的时候，才会使用真正的配置
    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 4;

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    exp.runExperiment(numberOfThreads = 1) ;


    exp.generateQualityIndicators() ;

    // Generate latex tables
    exp.generateLatexTables() ;

    // Configure the R scripts to be generated
    int rows  ;
    int columns  ;
    String prefix ;
    String [] problems ;
    boolean notch ;

    // Configuring scripts for FeatureSelection
    rows = 1 ;
    columns = 1 ;
    //前缀会方法生成的文件名里
    prefix = new String("Problems");
    problems = new String[]{"FeatureSelection"} ;
    
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;


    // Applying Friedman test
    Friedman test = new Friedman(exp);
    test.executeTest("EPSILON");
    test.executeTest("HV");
    test.executeTest("SPREAD");

  } // main
} // StandardStudy2Test


