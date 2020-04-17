//package com.inesv.interceptors;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.inesv.mapper.UserMapper;
//import com.inesv.model.User;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.context.support.WebApplicationContextUtils;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.inesv.annotation.UnLogin;
//import com.inesv.util.RspUtil;
//import com.inesv.util.ValidataUtil;
//
//public class LoginInterceptor extends HandlerInterceptorAdapter {
//
//	@Autowired
//	private UserMapper userMapper;
//
//	@Override
//	public boolean preHandle(HttpServletRequest request,
//			HttpServletResponse response, Object handler) throws IOException {
//		try {
//			request.setCharacterEncoding("utf-8");
//			response.setHeader("Content-type", "text/html;charset=UTF-8");
//			response.setCharacterEncoding("utf-8");
//			String data = request.getParameter("data");
//			JSONObject json = JSONObject.parseObject(data);
//			String token = json.getString("token");
//			log.info("token:" + token);
//			User user = null;
//			if (userMapper == null) {// 解决userMapper为null无法注入问题
//				BeanFactory factory = WebApplicationContextUtils
//						.getRequiredWebApplicationContext(request
//								.getServletContext());
//				userMapper = (UserMapper) factory.getBean("userMapper");
//			}
//			if (StringUtils.isNotBlank(token)) {
//				user = userMapper.getUserInfoByToken(token);
//			}
//			if (isUncheckedUrl(request, handler)) {
//				return true;
//			}
//			if (user != null) {
//				return true;
//			} else {
//				PrintWriter out = response.getWriter();
//				out.print(JSONObject.toJSONString(RspUtil.error("请先登录", -1)));
//				return false;
//			}
//		} catch (Exception e) {
//			PrintWriter printWriter = response.getWriter();
//			printWriter
//					.print(JSONObject.toJSONString(RspUtil.error("令牌失效", -2)));
//			return false;
//		}
//	}
//
//	private static final Logger log = LoggerFactory
//			.getLogger(LoginInterceptor.class);
//
//	private static final String PACKAGE_SPRING = "org.springframework.";
//
//	public boolean isUncheckedUrl(HttpServletRequest request, Object handler) {
//
//		if (!(handler instanceof HandlerMethod)) {
//			log.info("skip for handler type={}, uri={}", handler.getClass()
//					.getName(), request.getRequestURI());
//			return true;
//		}
//
//		HandlerMethod method = (HandlerMethod) handler;
//		if (method.getMethodAnnotation(UnLogin.class) != null) {
//			log.debug("skip for unlogin request uri={}",
//					request.getRequestURI());
//			return true;
//		}
//
//		return method.getBeanType().getPackage().getName()
//				.startsWith(PACKAGE_SPRING);
//	}
//}