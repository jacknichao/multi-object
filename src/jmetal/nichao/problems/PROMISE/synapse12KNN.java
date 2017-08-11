package jmetal.nichao.problems.PROMISE;

import jmetal.nichao.problems.BaseProblemImpl;

public class ant17KNN extends BaseProblemImpl{

	public ant17KNN(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"ant17KNN","weka.classifiers.lazy.IBk");
	}
}
