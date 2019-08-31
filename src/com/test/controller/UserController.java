package com.test.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import com.mvc.annotation.Autowired;
import com.mvc.annotation.Controller;
import com.mvc.annotation.RequestMapping;
import com.mvc.annotation.RequestParam;
import com.mvc.annotation.Scope;
import com.mvc.controller.BaseController;
import com.test.entity.User;
import com.test.service.UserService;
import com.util.LogUtil;

/**
 * @Description 自定义控制器
 * @author 李福涛
 *
 */
@Controller
@RequestMapping("/mvc")
@Scope("singleton")
public class UserController extends BaseController implements Serializable{

	@Autowired
	UserService userService;
	
	@RequestMapping("/addUser")
	public void addUser(@RequestParam("userName") String userName,
						@RequestParam("userPwd") String userPwd,
						@RequestParam("userPwd_confirm") String userPwd_confirm,
						@RequestParam("userTell") Integer userTell
						) throws ServletException, IOException {

		LogUtil.debug("IndexController.addUser()");
		
		User user = new User(null, userName, userPwd, userTell);
		boolean b = userService.addUser(user);
		request.setAttribute("user", user);
		
		if (b) {
			LogUtil.info("添加用户信息成功:" + "username=" + request.getParameter("userName"));
//			request.getRequestDispatcher("jsp/displayUser.jsp").forward(request, response);
		} else {
			LogUtil.info("添加用户信息失败！");
//			request.getRequestDispatcher("#").forward(request, response);
		}
	}


	@RequestMapping("/search")
	public void search(@RequestParam("userId") Integer id) throws ServletException, IOException {
		
		User user = userService.selectUser(id);
		request.setAttribute("user", user);
		
		if (user!=null) {
			LogUtil.info("查询用户成功:" + "user：" + user);
//			request.getRequestDispatcher("jsp/displayUser.jsp").forward(request, response);
		} else {
			LogUtil.info("添加用户信息失败！");
//			request.getRequestDispatcher("#").forward(request, response);
		}
	}

	@RequestMapping("/update")
	public void update(@RequestParam("userName") String userName,
					   @RequestParam("userPwd") String userPwd,
					   @RequestParam("userPwd_confirm") String userPwd_confirm,
					   @RequestParam("userTell") Integer userTell
					    ) throws ServletException, IOException {
		
		User user = new User(null, userName, userPwd, userTell);
		boolean b = userService.updateUser(user);
		LogUtil.info("update：" + b);
	}
	
	@RequestMapping("/delete")
	public void delete(@RequestParam("id") Integer id) {
		boolean b = userService.deleteUser(id);
		LogUtil.info("delete："+b);
	}
	
	@RequestMapping("/testUpdate")
	public void testUpdate() {
		User user = new User(67,"22","22",22);
		boolean b = userService.updateUser(user);
		LogUtil.info("update："+b);
	}
	
	@RequestMapping("/test")
	public void test() {
		if(userService != null)
			LogUtil.info("update：");
		else
			System.out.println("==============注入成功====================");
	}
	
	@RequestMapping("/test2")
	public String test2() {
		if(userService != null)
			LogUtil.info("update：");
		else
			System.out.println("==============注入成功====================");
		return 111 + "";
	}
}
