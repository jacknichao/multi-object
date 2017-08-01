package jmetal.nichao;

import jmetal.encodings.variable.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by ChaoNi on 2017/7/29.
 */

public class MyTools {

    /**
     * 问题名称到数据集名称的映射，比如Apache->Safe
     */
    private static HashMap<String,String> problemToDataset=null;

    /**
     * Safe/Apache.arff等文件的根路径
     */
    private static String datasetBase="";

    //直接在静态代码块中初始化数据
    static {

        problemToDataset=new HashMap<>();

        //Relink数据集
        problemToDataset.put("Apache","Relink");
        problemToDataset.put("Safe","Relink");
        problemToDataset.put("Zxing","Relink");

        //AEEEM数据集
        problemToDataset.put("EQ","AEEEM");
        problemToDataset.put("JDT","AEEEM");
        problemToDataset.put("LC","AEEEM");
        problemToDataset.put("ML","AEEEM");
        problemToDataset.put("PDE","AEEEM");

        //NASAClean1
        problemToDataset.put("CM1","NASAClean1");
        problemToDataset.put("JM1","NASAClean1");
        problemToDataset.put("KC3","NASAClean1");
        problemToDataset.put("MC1","NASAClean1");
        problemToDataset.put("MC2","NASAClean1");
        problemToDataset.put("MW1","NASAClean1");
        problemToDataset.put("PC1","NASAClean1");
        problemToDataset.put("PC2","NASAClean1");
        problemToDataset.put("PC3","NASAClean1");
        problemToDataset.put("PC4","NASAClean1");
        problemToDataset.put("CM5","NASAClean1");

        Properties prop = new Properties();
        try {
            prop.load(MyTools.class.getClassLoader()
                    .getResourceAsStream("config.properties"));
           datasetBase =prop.get("basepath")+"";

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * <font color='red'>问题名称到数据集名称的映射，比如Apache->Relink</font>
     */
    public static String problemToDatasetName(String problem){
        if(problemToDataset==null||!problem.contains(problem)){
            System.err.println("未找到"+problem+"到对应数据集的完整路径");
            System.exit(1);
        }
        return  problemToDataset.get(problem);

    }



    /**
     * 根据问题，找到对应的项目数据全路径如Apache问题对应的数据是Apache.arff
     * @param problem
     * @return
     */
    public static String problemDataFullPath(String problem){
        if(problemToDataset==null||!problem.contains(problem)){
            System.err.println("未找到"+problem+"到对应数据集的完整路径");
            System.exit(1);
        }

       //根据基准路径 数据集名称 问题名称得到对应的数据集
        String fullPath=datasetBase+"/"+problemToDataset.get(problem)+"/"+problem+".arff";

        return fullPath;
    }


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


    public  static void main(String[] args){

        System.out.println("测试运行读取配置文件是否成功....");
        System.out.println(datasetBase);
        System.out.println(MyTools.problemToDatasetName("Apache"));
        System.out.println(MyTools.problemDataFullPath("Apache"));


    }



}
