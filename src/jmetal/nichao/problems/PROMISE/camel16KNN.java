package jmetal.nichao.problems.PROMISE;

import jmetal.nichao.problems.BaseProblemImpl;

public class camel16KNN extends BaseProblemImpl{

	public camel16KNN(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"camel16KNN","weka.classifiers.lazy.IBk");
	}
}
