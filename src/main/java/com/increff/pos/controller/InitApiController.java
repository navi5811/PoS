package com.increff.pos.controller;

import java.util.List;

import com.increff.pos.dto.UserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.InfoData;
import com.increff.pos.model.UserForm;
import com.increff.pos.service.ApiException;


import io.swagger.annotations.ApiOperation;

@Controller
public class InitApiController extends AbstractUiController {

    @Autowired
    private InfoData info;
    @Autowired
    private UserDto userDto;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/init", method = RequestMethod.GET)
    public ModelAndView showPage(UserForm form) throws ApiException {
        info.setMessage("");
        return mav("init.html");
    }

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/init", method = RequestMethod.POST)
    public ModelAndView initSite(UserForm form) throws ApiException {
        return userDto.register(form);
    }

}
