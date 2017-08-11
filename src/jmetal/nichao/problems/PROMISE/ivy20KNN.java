package jmetal.nichao.problems.PROMISE;

import jmetal.nichao.problems.BaseProblemImpl;

public class ivy20KNN extends BaseProblemImpl{

	public ivy20KNN(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"ivy20KNN","weka.classifiers.lazy.IBk");
	}
}
