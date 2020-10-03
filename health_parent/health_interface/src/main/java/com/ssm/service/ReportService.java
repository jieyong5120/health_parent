package com.ssm.service;

import java.util.Map;

/*
 * 报表服务接口
 * */
public interface ReportService {

    //获得运营统计数据
    Map<String, Object> getBusinessReport() throws Exception;
}
