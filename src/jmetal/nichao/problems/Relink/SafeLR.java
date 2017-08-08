package jmetal.nichao.problems.Relink;

import jmetal.nichao.problems.BaseProblemImpl;

public class SafeLR extends BaseProblemImpl {
	public SafeLR(String solutionType) {
		//只需要更改此处的事先就可以完整所有方法的实现了
		super(solutionType,"SafeLR","weka.classifiers.functions.Logistic");
	}
}
