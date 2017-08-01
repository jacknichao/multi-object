package jmetal.nichao.problems.Relink;/**
 * Created by ChaoNi on 2017/7/28.
 */


import jmetal.nichao.problems.BaseProblemImpl;


/**
 * Class representing problem Apache.
 * The problem consist of maximizing the number of '1's in a binary string.
 */
public class Apache extends BaseProblemImpl {

     /**
     * Creates a new Apache  problem instance
     *
     * @param solutionType      编码的类型
     */
    public Apache(String solutionType) {
        //只需要更改此处的事先就可以完整所有方法的实现了
        super(solutionType,"Apache");
    }

}
