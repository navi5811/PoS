package com.increff.pos.controller;

import com.increff.pos.dto.UserDto;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.InfoData;
import com.increff.pos.model.UserForm;
import com.increff.pos.service.ApiException;


import io.swagger.annotations.ApiOperation;


@Controller
public class SignupApiController extends AbstractUiController {

    @Autowired
    private InfoData info;
    @Autowired
    private UserDto userDto;

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signUp", method = RequestMethod.GET)
    public ModelAndView showPage(UserForm form) throws ApiException {
        info.setMessage("");
        return mav("signUp.html");
    }

    @ApiOperation(value = "Initializes application")
    @RequestMapping(path = "/site/signUp", method = RequestMethod.POST)
    public ModelAndView initSite(@RequestBody UserForm form) throws ApiException {
        return userDto.register(form);
    }

}
