package jmetal.nichao.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.nichao.MyTools;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.Random;

/**
 * 该类是对Problem的简单封装，主要是因为本文的研究问题实际是同一个问题，
 * 主要是针对不同的数据集和不同分类器
 */
public abstract class BaseProblemImpl extends Problem {

	/**
	 * 70%的训练数据
	 */
	private Instances seventyPercent = null;

	/**
	 * 30%的测试数据
	 */
	//private Instances thirtyPercent = null;

	/**
	 * 使用的集分类器的名称
	 */
	private String classifierName="";


	public BaseProblemImpl(String solutionType, String problemName,String classifier){

		assert problemName_ !="";

		numberOfObjectives_ = 2;
		//这里有一个限定条件，就是至少选择一个特征来构建缺陷预测模型
		numberOfConstraints_ = 1;


		problemName_ =problemName;
		classifierName=classifier;

		//其实这里就是用二进制位来编码变量的
//        solutionType_ = new BinarySolutionType(this) ;
		if (solutionType.compareTo("Binary") == 0)
			solutionType_ = new BinarySolutionType(this);
		else {
			System.out.println(problemName_+": solution type " + solutionType + " invalid");
			System.exit(-1);
		}

		try {
			Instances all = ConverterUtils.DataSource.read(MyTools.problemDataFullPath(problemName_));

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


			//增加随机性，不指定随机因子，从而可以保证多次运行结果不一样
			all.randomize(new Random());
			if (all.classAttribute().isNominal()) {
				all.stratify(10);
			}

			//计算前70%的实例的索引
			int index = (int) Math.round(all.numInstances() * 0.7);
			seventyPercent = new Instances(all, 0, index);
			//thirtyPercent = new Instances(all, index, all.numInstances() - index);
			//将当前实验的训练集合、测试集合保存起来，以便后续在测试集合上进行验证
			// ConverterUtils.DataSink.write("results/seventyPercent.arff", seventyPercent);
			// ConverterUtils.DataSink.write("results/thirtyPercent.arff", thirtyPercent);
		} catch (Exception e) {
			e.printStackTrace();
		}




	}


	@Override
	/* 根据 numberOfObjectives_ 的值，定义目标函数的个数，在特征选择中只有两个目标函数
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


		Class clazz=null;
		Classifier classifier=null;

		//使用类加载器的方式实例化一个分类器
		try {
			 clazz = Class.forName(classifierName);
			 classifier = (Classifier) clazz.newInstance();
		}
		catch(Exception e) {
			Configuration.logger_.severe("BaseProblemImpl.evaluate(): " +
					" can not new instance of "+classifierName);
			e.printStackTrace();
			throw new JMException("Exception in BaseProblemImpl.evaluate()") ;
		} // catch


		Evaluation evaluation = null;
		try {
			evaluation = new Evaluation(filteredSeventyPercent);
			evaluation.crossValidateModel(classifier, filteredSeventyPercent, 3, new Random(1));
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


	@Override
	/**
	 * 所有的问题解决都是有一个限制条件的，就是最少要选择一个特征
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		//该函数表示在当前演化的过程中的某一个步骤，此时各个变量的取的值
		double [] constraint = new double[this.getNumberOfConstraints()];

		//该函数表示在当前演化的过程中的某一个步骤，此时各个变量的取的值
		Variable[] variable = solution.getDecisionVariables();
		//保存将被删除的特征的索引，也即在当前轮次没有被选中
		int toDel=0;
		for (int index = 0; index < variable.length; index++) {
			if (((Binary) variable[index]).getIth(0) == false) {
				toDel++;
			}
		}

		//要求限定的条件大于等于0，如果是多变量带入求值的话，参见jmetal文章中的例子
		constraint[0] = this.getNumberOfVariables()- toDel;


		double total = 0.0;
		int number = 0;
		for (int i = 0; i < this.getNumberOfConstraints(); i++)
			if (constraint[i]<=0.0){
				total+=constraint[i];
				number++;
			}

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);

	//super.evaluateConstraints(solution);
	}
}
