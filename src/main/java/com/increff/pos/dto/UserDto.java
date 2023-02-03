package com.increff.pos.dto;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.InfoData;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDto {

    @Autowired
    public UserService userService;
    @Autowired
    private InfoData info;

    @Transactional
    public void add(UserForm f) throws ApiException {
        UserPojo p= convert(f);
        normalize(p);
        UserPojo existing = userService.get(p.getEmail());
        if (existing != null) {
            throw new ApiException("User with given email already exists");
        }
        userService.add(p);
    }

    @Transactional
    public UserData get(String email) throws ApiException {
        System.out.println("entered get");
        UserPojo p=userService.get(email);
        if(p==null)
        {
            return null;
        }
      return convert(p);
    }

    @Transactional
    public List<UserData> getAll()
    {
        List<UserData> list=new ArrayList<>();

        List<UserPojo> up= userService.getAll();

        for(UserPojo u: up)
        {
            list.add(convert(u));
        }
        return list;
    }

    @Transactional
    public void delete(int id) {
        userService.delete(id);
    }

    protected static void normalize(UserPojo p) {
        p.setEmail(p.getEmail().toLowerCase().trim());
        p.setRole(p.getRole().toLowerCase().trim());
    }
    private static UserPojo convert(UserForm f) {
        UserPojo p = new UserPojo();
        p.setEmail(f.getEmail());
        p.setRole(f.getRole());
        p.setPassword(f.getPassword());
        return p;
    }

    private static UserData convert(UserPojo p) {
        UserData d = new UserData();
        d.setEmail(p.getEmail());
        d.setRole(p.getRole());
        d.setId(p.getId());
        d.setPassword(p.getPassword());
        return d;
    }

}
