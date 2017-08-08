package jmetal.nichao.problems.Relink;

import jmetal.nichao.problems.BaseProblemImpl;

public class SafeKNN extends BaseProblemImpl {
	public SafeKNN(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"SafeKNN","weka.classifiers.lazy.IBk");
	}
}
