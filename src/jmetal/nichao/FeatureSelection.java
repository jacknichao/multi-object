package jmetal.nichao;/**
 * Created by ChaoNi on 2017/7/28.
 */

import javafx.beans.binding.DoubleExpression;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.*;


/**
 * Class representing problem FeatureSelection.
 * The problem consist of maximizing the number of '1's in a binary string.
 */
public class FeatureSelection extends Problem {

    /**
     * 保存当前项目的名称，在该项目上用多目标优化来选择特征如 Relink下的Safe
     */
    private String projectName;
    /**
     * 70%的训练数据
     */
    private Instances seventyPercent = null;

    /**
     * 30%的测试数据
     */
    private Instances thirtyPercent = null;


    /**
     * Creates a new FeatureSelection  problem instance
     *
     * @param projectName 项目的名称（全路径）
     */
    public FeatureSelection(String projectName) {
        this("Binary", projectName);
    }

    /**
     * Creates a new FeatureSelection  problem instance
     *
     * @param solutionType      编码的类型
     * @param numberOfVariables 变量的个数
     */
    public FeatureSelection(String solutionType, String projectName) {
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        problemName_ = "FeatureSelection";

        //其实这里就是用二进制位来编码变量的
//        solutionType_ = new BinarySolutionType(this) ;
        if (solutionType.compareTo("Binary") == 0)
            solutionType_ = new BinarySolutionType(this);
        else {
            System.out.println("FeatureSelection: solution type " + solutionType + " invalid");
            System.exit(-1);
        }

        try {
            Instances all = ConverterUtils.DataSource.read(projectName);
            all.setClassIndex(all.numAttributes() - 1);

            //这里设置变量的个数
            numberOfVariables_ = all.numAttributes() - 1;
            //每一个变量对应一个二进制位，也即用bitset来编码每一个变量的
            // 同时，因为我们设定每一个变量要么选用要不不用，所有只需要一位二进制位即可编码
            // 因此这里为每一个变量设置对应的二进位长度为1
            length_ = new int[numberOfVariables_];
            for (int var = 0; var < numberOfVariables_; var++) {
                //每个变量只有选取和不选取之分，因此只需要一位二进制位就可以表示出来
                length_[var] = 1;
            }


            Random rand = new Random(1);
            all.randomize(rand);
            if (all.classAttribute().isNominal()) {
                all.stratify(10);
            }

            //计算前70%的实例的索引
            int index = (int) Math.round(all.numInstances() * 0.7);
            seventyPercent = new Instances(all, 0, index);
            thirtyPercent = new Instances(all, index, all.numInstances() - index);
            //将当前实验的训练集合、测试集合保存起来，以便后续在测试集合上进行验证
           // ConverterUtils.DataSink.write("results/seventyPercent.arff", seventyPercent);
           // ConverterUtils.DataSink.write("results/thirtyPercent.arff", thirtyPercent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    /**
     * 根据 numberOfObjectives_ 的值，定义目标函数的个数，在特征选择中只有两个目标函数
     */
    public void evaluate(Solution solution) throws JMException {

        //该函数表示在当前演化的过程中的某一个步骤，此时各个变量的取的值
        Variable[] variable = solution.getDecisionVariables();
        //保存将被删除的特征的索引，也即在当前轮次没有被选中
        ArrayList<Integer> delFeaIndex = new ArrayList<>();
        for (int index = 0; index < variable.length; index++) {
            if (((Binary) variable[index]).getIth(0) == false) {
                delFeaIndex.add(index);
            }
        }
        int[] delFeaIndexArr = MyTools.toIntArray(delFeaIndex);

        Remove remove = new Remove();
        remove.setAttributeIndicesArray(delFeaIndexArr);//删除不必要的属性
        Instances filteredSeventyPercent = null;
        try {
            remove.setInputFormat(seventyPercent);
            //这里要重新生成一份训练数据
            filteredSeventyPercent = Filter.useFilter(new Instances(seventyPercent), remove);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //对训练后的结果进行3层的分层采样
        filteredSeventyPercent.setClassIndex(filteredSeventyPercent.numAttributes() - 1);
        if (filteredSeventyPercent.classAttribute().isNominal()) {
            filteredSeventyPercent.stratify(3);
        }

        //对分层采样后的训练集进行3折交叉验证
        IBk iBk = new IBk(3);
        Evaluation evaluation = null;
        try {
            evaluation = new Evaluation(filteredSeventyPercent);
            evaluation.crossValidateModel(iBk, filteredSeventyPercent, 3, new Random(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设定目标值
        double[] objects = new double[numberOfObjectives_];
        objects[0] = numberOfVariables_ - delFeaIndexArr.length;
        objects[1] = 1 - evaluation.weightedAreaUnderROC();

        solution.setObjective(0, objects[0]);
        solution.setObjective(1, objects[1]);
    }
}
