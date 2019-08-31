<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
</head>
<body>
	<form action="Day4_WatFramework/mvc/addUser.do" method="post">
		账号：<input type="text" name="userName"><br>
		密码：<input type="password" name="userPwd"><br>
		确认密码：<input type="password" name="userPwd_confirm"><br>
		联系电话：<input type="text" name="userTell"><br>
		<input type="submit" value="提交">
	</form>
	<br>
		<form action="Day4_WatFramework/mvc/search.do" method="post">
		查询信息id：<input type="text" name="userId"><br>
		<input type="submit" value="提交">
	</form>
</body>
</html>