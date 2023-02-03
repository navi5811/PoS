package com.increff.pos.controller;

import com.increff.pos.dto.DailyReportDto;
import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.DailyReportData;
import com.increff.pos.pojo.DailyReportPojo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
public class DailyReportApiController {

    @Autowired
    private DailyReportDto dailyReportDto;


    @ApiOperation(value = "Gets list of all Brands")
    @RequestMapping(path = "/api/report/daily", method = RequestMethod.GET)
    public List<DailyReportData> getAll() {
        return dailyReportDto.getAllReport();
    }







}
