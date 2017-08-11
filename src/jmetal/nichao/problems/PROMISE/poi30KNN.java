package jmetal.nichao.problems.PROMISE;

import jmetal.nichao.problems.BaseProblemImpl;

public class poi30KNN extends BaseProblemImpl{

	public poi30KNN(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"poi30KNN","weka.classifiers.lazy.IBk");
	}
}
