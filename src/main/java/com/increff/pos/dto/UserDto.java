package com.increff.pos.dto;

import com.increff.pos.controller.AbstractUiController;
import com.increff.pos.dao.UserDao;
import com.increff.pos.model.InfoData;
import com.increff.pos.model.LoginForm;
import com.increff.pos.model.UserData;
import com.increff.pos.model.UserForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;
import org.apache.xpath.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class UserDto extends AbstractUiController {
    @Value("#{'${app.supervisor.emails}'.split(',')}")
    private List<String> supervisorEmails;

    @Autowired
    public UserService userService;
    @Autowired
    private InfoData info;

    @Transactional
    public void add(UserForm f) throws ApiException {
        UserPojo p= convert(f);
        normalize(p);
        String email= f.getEmail();
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
        normalize(p);
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

    @Transactional
    public ModelAndView register(UserForm form) throws ApiException {
        if(supervisorEmails.contains(form.getEmail())) {
            form.setRole("supervisor");
        }else {
            form.setRole("operator");
        }

       try {
           add(form);
       }catch (ApiException e){
           info.setMessage("email already exists");
       }
        return mav("init.html");
    }
    @Transactional
    public ModelAndView login(HttpServletRequest req, LoginForm f) throws ApiException {

    UserData p = get(f.getEmail());
    boolean authenticated = (p != null && Objects.equals(p.getPassword(), f.getPassword()));
		if (!authenticated) {
        info.setMessage("Invalid username or password");
        return new ModelAndView("redirect:/site/login");
    }

    // Create authentication object
    Authentication authentication =convertp(p);
    // Create new session
    HttpSession session = req.getSession(true);
    // Attach Spring SecurityContext to this new session
		SecurityUtil.createContext(session);
    // Attach Authentication object to the Security Context
		SecurityUtil.setAuthentication(authentication);

		return new ModelAndView("redirect:/ui/order");
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
    public static Authentication convertp(UserData p) {
        // Create principal
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(p.getEmail());
        principal.setId(p.getId());
        principal.setRole(p.getRole());
        // Create Authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(p.getRole()));
        // you can add more roles if required

        // Create Authentication
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null,
                authorities);
        return token;
    }

}
