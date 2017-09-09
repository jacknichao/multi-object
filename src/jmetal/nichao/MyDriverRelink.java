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
import jmetal.encodings.variable.Int;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
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
public class MyDriverRelink extends Experiment {

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
      algorithm[4] = new  MySMSEMOA_Settings(problemName).configure(parameters[4]);
        algorithm[5] = new MyRandomSearch_Settings(problemName).configure(parameters[5]);

      //这两个算法不适用
      //      algorithm[0] = new MySMPSO_Settings(problemName).configure(parameters[0]);
//            algorithm[0] = new MyCellDE_Settings(problemName).configure(parameters[0]);




      } catch (IllegalArgumentException ex) {
      Logger.getLogger(MyDriverRelink.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(MyDriverRelink.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(MyDriverRelink.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    MyDriverRelink exp = new MyDriverRelink();

    //名称中避免使用下划线_，因为改名称会被用于生成latex表格
    exp.experimentName_ = Driver.infoMap.get("experimentName_");

    exp.algorithmNameList_ = new String[]{
            "NSGAII",
            "MOCell",
            "SPEA2",
            "PAES",
            "SMSEMOA",
            "RandomSearch"
            //            "SMPSO"//有问题，运行不了
            //  "CellDE"//有问题，运行不了
    };


    exp.problemList_ = new String[]{
            "ApacheKNN",
            "SafeKNN",
            "ZxingKNN",

            "ApacheLR",
            "SafeLR",
            "ZxingLR",

            "ApacheJ48",
            "SafeJ48",
            "ZxingJ48",

            "ApacheNB",
            "SafeNB",
            "ZxingNB"

    };

    //这块需要好好研究下
    exp.paretoFrontFile_ = new String[exp.problemList_.length];


    //”HV” , ”SPREAD” , ”IGD” , ”EPSILON”
    exp.indicatorList_ = new String[]{"HV","SPREAD","EPSILON"};

    int numberOfAlgorithms = exp.algorithmNameList_.length;

    exp.experimentBaseDirectory_ = Driver.infoMap.get("experimentBaseDirectory_") +"/"+
                                   exp.experimentName_;
    exp.paretoFrontDirectory_ = "";

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    //exp.independentRuns_ = 100;
    exp.independentRuns_ = Integer.parseInt(Driver.infoMap.get("independentRuns_"));

    exp.initExperiment();

    double initTime=System.currentTimeMillis();

    // Run the experiments
    int numberOfThreads= Integer.parseInt(Driver.infoMap.get("numberOfThreads"));
    exp.runExperiment(numberOfThreads) ;

    System.out.println("开始计算指标.....");



    exp.generateQualityIndicators() ;

    // Generate latex tables
    exp.generateLatexTables() ;

    // Configure the R scripts to be generated
    int rows  ;
    int columns  ;
    String prefix ;
    String [] problems ;
    boolean notch ;

    // Configuring scripts for SafeKNN
    rows = 2;
    columns = 2 ;
    prefix = new String("RelinkKNN");
    problems = new String[]{"ApacheKNN","SafeKNN","ZxingKNN"} ;

    //1. 也就是说这里的rows columns的成绩决定了需要解决的问题的个数, 比如有三个问题，那么可以设定为1x3的格局
    //This method generates R scripts which produce .eps files containing rows × columns boxplots of
    //the list of problems passed as third parameterThis method generates R scripts which produce .eps files containing rows × columns boxplots of
    //the list of problems passed as third parameter
    //所生成的boxplot只是针对problems列表提供的问题在所有方法上的实验结果
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    //2. 如果需要分门别类的针对绘制关于问题的boxplot可以多次调用该函数并提供不同的布局
    //e.g., 这里AEEEM中有5个问题，可以选择2x3 也可以选择1*5等等
/*    rows = 2;
    columns = 3 ;
    prefix = new String("AEEEM");
    problems = new String[]{"EQ","JDT","LC","ML","PDE"} ;
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    */

    rows = 2;
    columns = 2 ;
    prefix = new String("RelinkLR");
    problems = new String[]{"ApacheLR","SafeLR","ZxingLR"} ;
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    rows = 2;
    columns = 2 ;
    prefix = new String("RelinkJ48");
    problems = new String[]{"ApacheJ48","SafeJ48","ZxingJ48"} ;
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;

    rows = 2;
    columns = 2 ;
    prefix = new String("RelinkNB");
    problems = new String[]{"ApacheNB","SafeNB","ZxingNB"} ;
    exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp) ;
    exp.generateRWilcoxonScripts(problems, prefix, exp) ;


    //重新设置结果前缀
    prefix="FriedmanRelink";


    // Applying Friedman test
    Friedman test = new Friedman(exp);
    test.executeTest("EPSILON");
    test.executeTest("HV");
    test.executeTest("SPREAD");

    double endTime=System.currentTimeMillis();
    System.out.println("总共用时间:"+ (endTime-initTime));


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
