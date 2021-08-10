package com.runzhou.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.runzhou.excel.pojo.DemoData;

/**
 * @author runzhouwu
 * @Description TODO
 * on 2021/8/10 8:36 下午
 */
public class DemoDW {


    public static void main(String[] args){

        // read
        String fileName = "src/main/resources/1707.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();

        ExcelReader excelReader = EasyExcel.read(fileName, DemoData.class, new DemoDataListener(new Analyze())).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        excelReader.finish();
    }
}
