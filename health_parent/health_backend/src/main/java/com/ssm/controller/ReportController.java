package com.ssm.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ssm.constant.MessageConstant;
import com.ssm.entity.Result;
import com.ssm.service.MemberService;
import com.ssm.service.ReportService;
import com.ssm.service.SetmealService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 报表管理
 * */
@RestController  //注解相当于@Controller注解,还加上了一个@ResponseBody注解
@RequestMapping("/report")
public class ReportController {

    //会员业务层对象
    @Reference  //查找服务,从服务注册中心zookeeper获取服务
    private MemberService memberService;

    //套餐业务层对象
    @Reference
    private SetmealService setmealService;

    //报表业务层对象
    @Reference
    private ReportService reportService;

    //会员数量统计
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        /**
         * 前台需要后台返回的数据类型
         *
         * {
         * "data":{
         *          "months":["2019.01","2019.02","2019.03","2019.04"],
         *          "memberCount":[3,4,8,10]
         * },
         * "flag":true,
         * "message":"获取会员统计数据成功"
         * }
         */

        //反射创建日历对象,默认时间就是当前系统时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -12);  //获取当前日期之前的12月日期,往前推12个月,就是回到12个月前的时间

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);  //在回到12个月前的时间基础上,在往后推迟一个月时间,依次循环12次
            list.add(new SimpleDateFormat("yyyy.MM").format(calendar.getTime()));  //getTime()方法获取当前时间(执行add()方法后推迟一个月的时间,不是日常生活的时间)
        }

        Map<String, Object> map = new HashMap<>();
        map.put("months", list);

        //根据日期查询当月会员数量
        List<Integer> memberCount = memberService.findMemberCountByMonth(list);
        map.put("memberCount", memberCount);

        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);  //获取会员统计数据成功
    }

    //套餐占比统计
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {
        /**
         * 前台需要后台返回的数据类型
         *
         * {
         * "data":{
         *          "setmealNames":["套餐1","套餐2","套餐3"],
         *          "setmealCount":[
         *                  {"name":"套餐1","value":10},
         *                  {"name":"套餐2","value":30},
         *                  {"name":"套餐3","value":25}
         *          ]
         * },
         * "flag":true,
         * "message":"获取会员统计数据成功"
         * }
         */

        Map<String, Object> map = new HashMap<>();
        try {
            //查询体检套餐种类的数量
            List<Map<String, Object>> list = setmealService.findSetmealCount();

            map.put("setmealCount", list);  //预约的体检套餐种类的数量(饼状图占比)

            List<String> setmealNames = new ArrayList<>();
            for (Map<String, Object> setmeal : list) {
                String name = (String) setmeal.get("name");  //获取体检套餐名称
                setmealNames.add(name);
            }
            map.put("setmealNames", setmealNames);  //预约的体检套餐种类

            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, map);  //获取套餐统计数据成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);  //获取套餐统计数据失败;
        }

    }

    //运营数据统计
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        /**
         * 前台需要后台返回的数据类型
         *
         * {
         * "data":{
         *          "todayVisitsNumber":0,
         *          "reportDate":"2019‐04‐25",
         *          "todayNewMember":0,
         *          "thisWeekVisitsNumber":0,
         *          "thisMonthNewMember":2,
         *          "thisWeekNewMember":0,
         *          "totalMember":10,
         *          "thisMonthOrderNumber":2,
         *          "thisMonthVisitsNumber":0,
         *          "todayOrderNumber":0,
         *          "thisWeekOrderNumber":0,
         *          "hotSetmeal":[
         *                          {"proportion":0.4545,"name":"粉红珍爱(女)升级TM12项筛查体检套餐","setmeal_count":5},
         *                          {"proportion":0.1818,"name":"阳光爸妈升级肿瘤12项筛查体检套餐","setmeal_count":2},
         *                          {"proportion":0.1818,"name":"珍爱高端升级肿瘤12项筛查","setmeal_count":2},
         *                          {"proportion":0.0909,"name":"孕前检查套餐","setmeal_count":1}
         *                      ],
         * },
         * "flag":true,
         * "message":"获取会员统计数据成功"
         * }
         */

        try {
            Map<String, Object> map = reportService.getBusinessReport();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, map);  //获取运营统计数据成功
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);  //获取运营统计数据失败
        }
    }

    //导出运营数据到Excel文件,并提供客户端下载报表数据
    @RequestMapping("/exportBusinessReportFromExcel")
    public Result exportBusinessReportFromExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            //远程调用报表服务获取报表数据
            Map<String, Object> map = reportService.getBusinessReport();

            //获取返回的结果数据,准备将报表数据写入到Excel文件中

            //获取报表日期
            String reportDate = (String) map.get("reportDate");

            //1.获取会员数报表数据
            Integer todayNewMember = (Integer) map.get("todayNewMember");
            Integer totalMember = (Integer) map.get("totalMember");  //获取总会员数
            Integer thisWeekNewMember = (Integer) map.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) map.get("thisMonthNewMember");

            //2.获取已预约报表数据
            Integer todayOrderNumber = (Integer) map.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) map.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) map.get("thisMonthOrderNumber");

            //3.获取已就诊报表数据
            Integer todayVisitsNumber = (Integer) map.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) map.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) map.get("thisMonthVisitsNumber");

            //4.获取热门套餐报表数据
            List<Map> hotSetmeal = (List<Map>) map.get("hotSetmeal");

            /*
             * 获取Excel模板文件绝对路径
             *
             * getRealPath()参数:目录结构,对应绝对路径的目录
             * File.separator : 相当于分隔符"/"  因为Window,Mac,Linux系统分割路径不一样,会自动识别我们操作系统来使用分隔符
             * */
            String temlateRealPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";

            //读取模板文件创建Excel表格对象  补充:在模板文件基础上创建Excel表格对象文件
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(temlateRealPath)));  //创建XSSFWorkbook对象
            //读取Excel文件中的第一个Sheet标签页
            XSSFSheet sheet = workbook.getSheetAt(0);

            //获取第3行 参数2代表第3行 参数5代表第6列
            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);  //显示报表日期

            row = sheet.getRow(4);  //获取第5行
            row.getCell(5).setCellValue(todayNewMember);  //显示今日新增会员数
            row.getCell(7).setCellValue(totalMember);  //显示总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);  //显示本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);  //显示本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);  //显示今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);  //显示今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);  //显示本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);  //显示本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);  //显示本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);  //显示本月到诊数

            //显示热门套餐数据
            int rowNum = 12;  //代表第13行
            for (Map hs : hotSetmeal) {
                String name = (String) hs.get("name");  //获取套餐名称
                Long setmeal_count = (Long) hs.get("setmeal_count");  //获取套餐对应的数量
                BigDecimal proportion = (BigDecimal) hs.get("proportion");  //获取套餐数量对应的占比

                row = sheet.getRow(rowNum++);

                row.getCell(4).setCellValue(name);  //显示套餐名称
                row.getCell(5).setCellValue(setmeal_count);  //显示套餐对应的数量
                row.getCell(6).setCellValue(proportion.doubleValue());  //显示套餐数量对应的占比
            }

            //通过输出流对文件进行下载  使用输出流进行表格下载,基于浏览器作为客户端下载  不要使用new OutputStream(),这样指定路径输出文件
            ServletOutputStream out = response.getOutputStream();

            //设置响应头信息
            response.setContentType("application/vnd.ms-excel");  //代表的是Excel类型的文件
            //陷阱:在PDF中粘贴Content-Disposition,首字母小写不对,附件下载不是xlsx文件
            response.setHeader("Content-Disposition", "attachment;filename=report.xlsx");  //指定客户端以什么形式下载,以附件的形式下载

            //将内存中的数据写出到浏览器中,模板文件还是空的,不是将文件写入到模板文件中再写出到硬盘中
            workbook.write(out);
            out.flush();
            out.close();
            workbook.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);  //获取运营统计数据失败
        }
    }

    //导出运营数据到PDF文件,并提供客户端下载报表数据
    @RequestMapping("/exportBusinessReportFromPDF")
    public Result exportBusinessReportFromPDF(HttpServletRequest request, HttpServletResponse response) {
        try {
            //远程调用报表服务获取报表数据
            Map<String, Object> map = reportService.getBusinessReport();

            //取出返回结果数据,准备将报表数据写入到PDF文件中
            List<Map> hotSetmeal = (List<Map>) map.get("hotSetmeal");

            //动态获取模板文件的磁盘路径
            String jrxmlPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business_report.jrxml";
            String jasperPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business_report.jasper";

            //编译模板  编译为后缀名为jasper的二进制文件
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);

            //填充数据---使用JDBC数据源方式填充  new JRBeanCollectionDataSource():通过这种方式,将List集合封装为JavaBean
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, map, new JRBeanCollectionDataSource(hotSetmeal));

            //通过输出流对文件进行下载  使用输出流进行表格下载,基于浏览器作为客户端下载  不要使用new OutputStream(),这样指定路径输出文件
            ServletOutputStream out = response.getOutputStream();

            //设置响应头信息
            response.setContentType("application/pdf");  //代表的是PDF类型的文件
            response.setHeader("Content-Disposition", "attachment;filename=report.pdf");  //指定客户端以什么形式下载,以附件的形式下载

            //输出文件
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            out.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);  //获取运营统计数据失败
        }
    }
}
