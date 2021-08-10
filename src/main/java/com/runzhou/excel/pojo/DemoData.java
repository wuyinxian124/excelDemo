package com.runzhou.excel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author runzhouwu
 * @Description TODO
 * on 2021/8/10 8:38 下午
 */
public class DemoData {

    @Getter
    @Setter
    @ExcelProperty(index = 0)
    private String name;

    @Getter
    @Setter
    @ExcelProperty(index = 14)
    private String content;

    @Getter
    @Setter
    @ExcelProperty(index = 1)
    private String ggp;

    @Override
    public String toString() {
        return "DemoData{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", ggp='" + ggp + '\'' +
                '}';
    }
}
