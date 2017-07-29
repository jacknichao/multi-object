package jmetal.nichao;

import jmetal.encodings.variable.Int;

import java.util.ArrayList;

/**
 * Created by ChaoNi on 2017/7/29.
 */

public class MyTools {

    /**
     * 将ArrayList<Integer>转化成int[]
     * @param arrayList
     * @return
     */
    public static int[] toIntArray(ArrayList<Integer> arrayList){
        if(arrayList==null||arrayList.size()==0)
            return null;

        int[] arr=new int[arrayList.size()];
        for(int i=0;i<arr.length;i++){
            arr[i]=arrayList.get(i);
        }
        return arr;
    }



}
