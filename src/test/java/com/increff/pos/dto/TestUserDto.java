package com.increff.pos.dto;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestUserDto extends AbstractUnitTest {

    @Autowired
    public UserDto userDto;

    @Test
    public void testAddUser() throws ApiException {
        String role="admin";
        String password="password";
        String email="email@gmail.com";
        UserForm f=new UserForm();
        f.setRole(role);
        f.setPassword(password);
        f.setEmail(email);
        userDto.add(f);

        List<UserData>list=userDto.getAll();
        assertEquals(list.get(0).getEmail(), email);
        assertEquals(list.get(0).getRole(), role);
        assertEquals(list.get(0).getPassword(), password);

        try{
            userDto.add(f);
        }catch (ApiException e){
            return;
        }
    }
    @Test
    public void testGet() throws ApiException {
        String role="admin";
        String password="password";
        String email="email@gmail.com";
        UserForm f=new UserForm();
        f.setRole(role);
        f.setPassword(password);
        f.setEmail(email);
        userDto.add(f);
        //user added


        UserData d=userDto.get(email);
        assertEquals(d.getEmail(), email);
        assertEquals(d.getRole(), role);
        assertEquals(d.getPassword(), password);
    }


    @Test
    public void testDelete() throws ApiException {
        String role="admin";
        String password="password";
        String email="email@gmail.com";
        UserForm f=new UserForm();
        f.setRole(role);
        f.setPassword(password);
        f.setEmail(email);
        userDto.add(f);
        //user added

        UserData d=userDto.get(email);

        int id=d.getId();
        userDto.delete(id);
        List<UserData> list=userDto.getAll();

        if(list.size()!=0)
        {
            fail();
        }

    }







}
