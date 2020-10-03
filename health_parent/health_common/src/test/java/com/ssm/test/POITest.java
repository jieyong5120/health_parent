package com.ssm.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class POITest {

    /*
     * 使用POI读取Excel文件中的数据
     *
     * 方法一:通过遍历工作表获得行，遍历行获得单元格，最终获取单元格中的值
     * */
    @Test
    public void test1() throws Exception {

        /*
         * 方法一:通过遍历工作表获得行，遍历行获得单元格，最终获取单元格中的值
         *
         * 加载指定文件,创建一个Excel对象(工作簿)
         *
         * 创建XSSFWorkbook对象和FileInputStream对象存在异常
         * */
        XSSFWorkbook sheets = new XSSFWorkbook(new FileInputStream(new File("F:\\code_case\\poiRead.xlsx")));
        //读取Excel文件中的第一个Sheet标签页
        XSSFSheet sheet = sheets.getSheetAt(0);

        //遍历Sheet标签页,获取行对象
        for (Row row : sheet) {
            //遍历Sheet标签页行对象,获取单元格对象
            for (Cell cell : row) {
                //获得单元格中的值
                String stringCellValue = cell.getStringCellValue();
                System.out.println(stringCellValue);
            }
        }

        //关闭Excel对象(工作簿)对象
        sheets.close();
    }


    /*
     * 使用POI读取Excel文件中的数据
     *
     * 方法二:获取工作表最后一个行号,从而根据行号获得行对象,通过行获取最后一个单元格索引,从而根据单元格索引获取每行的一个单元格对象
     * */
    @Test
    public void test2() throws Exception {

        /*
         * 方法二:获取工作表最后一个行号,从而根据行号获得行对象,通过行获取最后一个单元格索引,从而根据单元格索引获取每行的一个单元格对象
         *
         * 加载指定文件,创建一个Excel对象(工作簿)
         *
         * 创建XSSFWorkbook对象和FileInputStream对象存在异常
         * */
        XSSFWorkbook sheets = new XSSFWorkbook(new FileInputStream(new File("F:\\code_case\\poiRead.xlsx")));
        //读取Excel文件中的第一个Sheet标签页
        XSSFSheet sheet = sheets.getSheetAt(0);
        //读取当前Sheet标签页最后一行的行号,行号从0开始,细节注意
        int lastRowNum = sheet.getLastRowNum();

        //遍历循环每一行
        for (int i = 0; i <= lastRowNum; i++) {
            //根据行号获取行对象
            XSSFRow row = sheet.getRow(i);
            //读取当前行最后一列单元格的列号,列号从1开始,细节注意
            short lastCellNum = row.getLastCellNum();

            //编列循环每一个单元格
            for (int j = 0; j < lastCellNum; j++) {

                //根据列号获取单元格对象
                XSSFCell cell = row.getCell(j);
                //获得单元格中的值
                String stringCellValue = cell.getStringCellValue();
                System.out.println(stringCellValue);
            }
        }

        //关闭Excel对象(工作簿)对象
        sheets.close();
    }

    /*
     * 使用POI向Excel文件中写入数据
     * */
    @Test
    public void test3() throws Exception {

        //在内存中创建一个Excel表格
        XSSFWorkbook sheets = new XSSFWorkbook();
        //创建Excel文件中的一个Sheet标签页,并指定名称
        XSSFSheet sheet = sheets.createSheet("POI写入");

        //在当前标签页创建行,0表示第一行
        XSSFRow row = sheet.createRow(0);
        //创建单元格,0表示第一个单元格
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("名称");
        row.createCell(2).setCellValue("年龄");

        //在当前标签页创建行,0表示第一行
        XSSFRow row1 = sheet.createRow(1);
        //创建单元格,0表示第一个单元格
        row1.createCell(0).setCellValue("001");
        row1.createCell(1).setCellValue("小明");
        row1.createCell(2).setCellValue("23");

        //在当前标签页创建行,0表示第一行
        XSSFRow row2 = sheet.createRow(2);
        //创建单元格,0表示第一个单元格
        row2.createCell(0).setCellValue("002");
        row2.createCell(1).setCellValue("小王");
        row2.createCell(2).setCellValue("25");

        //通过输出流将内存中的XSSFWorkbook对象写入到磁盘
        FileOutputStream out = new FileOutputStream("F:\\code_case\\poiWrite.xlsx");
        sheets.write(out);
        out.flush();
        out.close();
        sheets.close();
    }

}
