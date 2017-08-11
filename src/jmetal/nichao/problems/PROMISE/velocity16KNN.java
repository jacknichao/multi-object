package jmetal.nichao.problems.PROMISE;

import jmetal.nichao.problems.BaseProblemImpl;

public class synapse12KNN extends BaseProblemImpl{

	public synapse12KNN(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"synapse12KNN","weka.classifiers.lazy.IBk");
	}
}
