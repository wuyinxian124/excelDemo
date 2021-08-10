package com.runzhou.excel;

import com.runzhou.excel.pojo.DemoData;

import java.util.List;

/**
 * @author runzhouwu
 * @Description TODO
 * on 2021/8/10 9:01 下午
 */
public class Analyze {

    private List<DemoData> beforeData;

    private boolean begin;

    public Analyze() {

    }

    private final String[] index = {"很好","不足","其他","无"};
    private final String[][] valueKey = {
            {"很好","没问题","没有问题"}
    };
    public void setBeforeData(List<DemoData> beforeData) {
        this.beforeData = beforeData;
    }

    public void notifyAnalyze(){
        begin = true;
    }

    private void analyze(){
        while(!begin){
            System.out.println("wait  ..");
        }
        for(DemoData demoData: beforeData){

        }
    }



}
