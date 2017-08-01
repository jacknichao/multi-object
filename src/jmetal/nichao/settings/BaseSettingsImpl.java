package jmetal.nichao.settings;


import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.nichao.MyProblemFactory;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Properties;

/**
 * Settings class of all algorithm (binary encoding)
 * 因为其他算法的配置都是采用当前算法的设定进行的，唯一不同的就是使用的具体算法的名称
 */
public abstract class BaseSettingsImpl extends Settings {

	public int populationSize_;
	public int maxEvaluations_;
	public double mutationProbability_;
	public double crossoverProbability_;
	public double mutationDistributionIndex_;
	public double crossoverDistributionIndex_;

	/**
	 * 保存算法的名称的全路径
	 */
	private String algorithmName;


	/**
	 * Constructor
	 */
	public BaseSettingsImpl(String problemName, String algorithm) {
		super(problemName);
		//传入当前需要采用的算法的名称
		this.algorithmName=algorithm;


		//这里要尤其注意，传入的参数必须是Binary
		Object[] problemParams = {"Binary"};
		try {
			//使用自定义的MyProblemFactory来实例化对应的问题
			problem_ = (new MyProblemFactory()).getProblem(problemName_, problemParams);
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Default experiments.settings   pop size 暂时设定为30 max evaluation设定为3000

		populationSize_ = 100;
		maxEvaluations_ = 3000;
		mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
		crossoverProbability_ = 0.9;
		mutationDistributionIndex_ = 20.0;
		crossoverDistributionIndex_ = 20.0;
	} // BaseSettingsImpl


	/**
	 * Configure algorithm with default parameter experiments.settings
	 *
	 * @return A special  algorithm object
	 * @throws JMException
	 */
	public Algorithm configure() throws JMException {
		Algorithm algorithm;
		Selection selection;
		Crossover crossover;
		Mutation mutation;

		HashMap parameters; // Operator parameters

		// Creating the algorithm,根据给定的算法的名称，实例化一个具体的算法
		algorithm = getAlgorithmWithName();


		// Algorithm parameters
		algorithm.setInputParameter("populationSize", populationSize_);
		algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

		// Mutation and Crossover for Binary codification
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

		// Selection Operator
		parameters = null;
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		return algorithm;
	} // configure

	/**
	 * Configure algorithm with user-defined parameter experiments.settings
	 *
	 * @return A algorithm object
	 */
	@Override
	public Algorithm configure(Properties configuration) throws JMException {
		Algorithm algorithm;
		Selection selection;
		Crossover crossover;
		Mutation mutation;

		HashMap parameters; // Operator parameters
		algorithm = getAlgorithmWithName();


		// Algorithm parameters
		populationSize_ = Integer.parseInt(configuration.getProperty("populationSize", String.valueOf(populationSize_)));
		maxEvaluations_ = Integer.parseInt(configuration.getProperty("maxEvaluations", String.valueOf(maxEvaluations_)));
		algorithm.setInputParameter("populationSize", populationSize_);
		algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

		// Mutation and Crossover for Binary codification
		crossoverProbability_ = Double.parseDouble(configuration.getProperty("crossoverProbability", String.valueOf(crossoverProbability_)));
		crossoverDistributionIndex_ = Double.parseDouble(configuration.getProperty("crossoverDistributionIndex", String.valueOf(crossoverDistributionIndex_)));
		parameters = new HashMap();
		parameters.put("probability", crossoverProbability_);
		parameters.put("distributionIndex", crossoverDistributionIndex_);
		crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);

		mutationProbability_ = Double.parseDouble(configuration.getProperty("mutationProbability", String.valueOf(mutationProbability_)));
		mutationDistributionIndex_ = Double.parseDouble(configuration.getProperty("mutationDistributionIndex", String.valueOf(mutationDistributionIndex_)));
		parameters = new HashMap();
		parameters.put("probability", mutationProbability_);
		parameters.put("distributionIndex", mutationDistributionIndex_);
		mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

		// Selection Operator
		parameters = null;
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		return algorithm;
	}

	/**
	 * 根据指定的算法的名称来实例化一个Algorithm对象
	 * @return  一个Algorithm
	 * @throws JMException
	 */
	private Algorithm getAlgorithmWithName() throws JMException {
		Algorithm algorithm;// Creating the algorithm,根据给定的算法的名称，实例化一个具体的算法
		Object [] params=new Object[]{problem_};
		try {
			Class algorithmClass = Class.forName(this.algorithmName);
			Constructor[] constructors = algorithmClass.getConstructors();
			int i = 0;
			//find the constructor
			while ((i < constructors.length) &&
					(constructors[i].getParameterTypes().length!=params.length)) {
				i++;
			}
			// constructors[i] is the selected one constructor
			algorithm = (Algorithm)constructors[i].newInstance(params);
		}// try
		catch(Exception e) {
			Configuration.logger_.severe("BaseSettingsImpl.congire(): " +
					"Algorithm '"+ algorithmName + "' does not exist. "  +
					"Please, check the algorithm names in jmetal.metaheuristics.xxx") ;
			e.printStackTrace();
			throw new JMException("Exception in BaseSettingsImpl.configure()") ;
		} // catch
		return algorithm;
	}
} // BaseSettingsImpl
