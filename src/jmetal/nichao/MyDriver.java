//  StandardStudy.java
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
import jmetal.experiments.settings.GDE3_Settings;
import jmetal.experiments.settings.MOCell_Settings;
import jmetal.experiments.settings.SMPSO_Settings;
import jmetal.experiments.settings.SPEA2_Settings;
import jmetal.experiments.util.Friedman;
import jmetal.nichao.settings.*;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class MyDriver extends Experiment {

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

      //当前测试的时候只有一个算法，就选用一个就可以了,当同时开始的运行各个算法的时候要调整下

        algorithm[0] = new MyNSGAII_Settings(problemName).configure(parameters[0]);
        algorithm[1] = new MyMOCell_Settings(problemName).configure(parameters[1]);
        algorithm[2] = new MySPEA2_Settings(problemName).configure(parameters[2]);
        algorithm[3] = new MyPAES_Settings(problemName).configure(parameters[3]);
        algorithm[4] = new MyRandomSearch_Settings(problemName).configure(parameters[4]);

      //这两个算法不适用
      //      algorithm[0] = new MySMPSO_Settings(problemName).configure(parameters[0]);
//            algorithm[0] = new MyCellDE_Settings(problemName).configure(parameters[0]);




      } catch (IllegalArgumentException ex) {
      Logger.getLogger(MyDriver.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(MyDriver.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(MyDriver.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    MyDriver exp = new MyDriver();

    exp.experimentName_ = "IBK_Zxing";

    exp.algorithmNameList_ = new String[]{
            "NSGAII",
            "MOCell",
            "SPEA2",
            "PAES",
            "RandomSearch"
            //            "SMPSO"//有问题，运行不了
            //  "CellDE"//有问题，运行不了
    };


    exp.problemList_ = new String[]{
    //        "Apache"
//    "Safe"
"Zxing"
    };

    //这块需要好好研究下
    exp.paretoFrontFile_ = new String[exp.problemList_.length];


    exp.indicatorList_ = new String[]{"HV", "SPREAD", "EPSILON"};

    int numberOfAlgorithms = exp.algorithmNameList_.length;

    exp.experimentBaseDirectory_ = "/home/jacknichao/ideaProjects/multi-object/results/" +
                                   exp.experimentName_;
    exp.paretoFrontDirectory_ = "";

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    //exp.independentRuns_ = 100;
    exp.independentRuns_ = 1;

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    exp.runExperiment(numberOfThreads = 4) ;


    exp.generateQualityIndicators() ;

    // Generate latex tables
    exp.generateLatexTables() ;

    // Configure the R scripts to be generated
    int rows  ;
    int columns  ;
    String prefix ;
    String [] problems ;
    boolean notch ;

    // Configuring scripts for Safe
    rows = 1;
    columns = 1 ;
    prefix = new String("Relink");
    problems = new String[]{"Apache"} ;
    
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;


    // Applying Friedman test
    Friedman test = new Friedman(exp);
    test.executeTest("EPSILON");
    test.executeTest("HV");
    test.executeTest("SPREAD");


  } // main
} // StandardStudyTest

/*


 // Configuring scripts for ZDT
    rows = 3 ;
    columns = 2 ;
    prefix = new String("ZDT");
    problems = new String[]{"ZDT1", "ZDT2","ZDT3", "ZDT4","ZDT6"} ;

    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    // Configure scripts for DTLZ
    rows = 3 ;
    columns = 3 ;
    prefix = new String("DTLZ");
    problems = new String[]{"DTLZ1","DTLZ2","DTLZ3","DTLZ4","DTLZ5",
                                    "DTLZ6","DTLZ7"} ;

    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch=false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    // Configure scripts for WFG
    rows = 3 ;
    columns = 3 ;
    prefix = new String("WFG");
    problems = new String[]{"WFG1","WFG2","WFG3","WFG4","WFG5","WFG6",
                            "WFG7","WFG8","WFG9"} ;

    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch=false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    // Applying Friedman test
    Friedman test = new Friedman(exp);
    test.executeTest("EPSILON");
    test.executeTest("HV");
    test.executeTest("SPREAD");



 */
