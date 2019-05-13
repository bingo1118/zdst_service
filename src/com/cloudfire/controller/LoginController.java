package com.cloudfire.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.StyleDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.StyleDaoImpl;
import com.cloudfire.dwr.push.PushMessageUtil;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.Style;

@Controller
public class LoginController {
	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private StyleDao styleDao;

	public LoginController() {
		styleDao = new StyleDaoImpl();
	}

	@RequestMapping(value = "/saveCurrentId.do", method = RequestMethod.POST)
	public ModelAndView saveCurrentId2(HttpServletRequest request, String currentId) {
		String cuserid = (String) request.getSession().getAttribute("userId");
		if (currentId == null || currentId == "") {
			currentId = cuserid;
		}
		request.getSession().removeAttribute("style");
		if (styleDao.isExistUser(currentId)) {
			Style style = styleDao.getStyle(currentId);
			request.getSession().setAttribute("style", style);
		} else {

		}
		ModelAndView ma = saveCurrentId(request, currentId);
		return ma;
	}

	@RequestMapping(value = "/saveCurrentId.do", method = RequestMethod.GET)
	public ModelAndView saveCurrentId(HttpServletRequest request, String currentId) {
		// ����ModelAndView
		// System.out.println(request.getSession().getAttribute("privId")+"=========");
		ModelAndView modelAndView = new ModelAndView();
		int loginState;
		if (request.getParameter("loginState") == null) { // ������ҳ�淵����ҳ
			modelAndView.setViewName("/newindex");
		} else {
			loginState = Integer.parseInt(request.getParameter("loginState"));
			switch (loginState) {
			case 0:
				break;
			case 1:
				modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_wodeshebei");
				break;
			case 2:
				List<AreaBean> alist = mAreaDao.getAll();
				modelAndView.addObject("alist", alist);
				modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_tianjiaxinxi");
				break;
			case 3:
				modelAndView.setViewName("/newindex");
				break;
			default:
				modelAndView.setViewName("/newindex");
				break;
			}
		}

		// String cuserid = (String)
		// request.getSession().getAttribute("userId");
		// if (currentId != null ) { //���ε�½
		// request.getSession().setAttribute("userId", currentId);
		// request.getSession().setAttribute("appkey", Constant.appk_web_sub);
		// int selectId =
		// Integer.parseInt(request.getSession().getAttribute("privId").toString());
		//
		// mLoginDao = new LoginDaoImpl();
		// LoginEntity mLoginEntity = mLoginDao.login(currentId);
		// String privilege = mLoginEntity.getPrivilege() + "";
		//
		// if((privilege=="6")||privilege.equals("6")||(privilege=="7")||privilege.equals("7")){
		// privilege = "3";
		// }
		//
		// if (privilege != "1" && !"1".equals(privilege)) { //����Ȩ��Ϊ1���û�
		// UserLongDao ul = new UserLongerDaoImpl();
		// UserEntity ue = ul.getUserInfoByUserId(currentId);
		// int privIds = ue.getPrivId();
		//
		// String ipaddress = Utils.getIpAddress(request);
		//// String serverName = Utils.getCurrentRunningServerComputerName();
		//// request.getSession().setAttribute("serverName", serverName);
		//
		// mLoginDao.updateLoginState(currentId, 1, ipaddress);// �����½��Ϣ
		//
		// if (privIds == 1 && selectId == 1) { //������λ���û���½
		// CompanyDao cd = new CompanyDaoImpl();
		// List<AreaBean> alist = new ArrayList<AreaBean>();
		// if (cd.ifExitCompany(currentId)) {
		// modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_wodeshebei");
		// } else {
		// alist = mAreaDao.getAll();
		// modelAndView.addObject("alist", alist);
		// modelAndView.setViewName("/WEB-INF/page/contractor/jianzhudanwei_tianjiaxinxi");
		// }
		//
		//
		// } else if (privIds == 3 && selectId == 3) { //��ܲ��ŵ��û���½
		//// modelAndView.setViewName("/WEB-INF/page/main/main");
		// modelAndView.setViewName("/newindex");
		// } else {
		// request.getSession().removeAttribute(cuserid);
		// request.getSession().setAttribute("mession", "��ѡ����û����Ͳ�ƥ�䣡");
		// modelAndView.setViewName("/WEB-INF/page/login/login");
		// }
		// }
		// } else if(cuserid == null || cuserid.length() == 0) { //���������ʻ������ѶϿ���
		// modelAndView.setViewName("/WEB-INF/page/login/login");
		// } else { //��ƽ̨������ҳ�淵����ҳ
		// modelAndView.setViewName("/newindex");
		// }

		return modelAndView;
	}

	@RequestMapping(value = "/sendMsgToPerson.do", method = RequestMethod.GET)
	public String sendMsgToPerson2(HttpServletRequest request, String sendToId, String content) {
		String str1 = sendMsgToPerson(request, sendToId, content);
		return str1;
	}

	@RequestMapping(value = "/sendMsgToPerson.do", method = RequestMethod.POST)
	public String sendMsgToPerson(HttpServletRequest request, String sendToId, String content) {
		String currentId = (String) request.getSession().getAttribute(PushMessageUtil.DEFAULT_MARK);
		if (StringUtils.isBlank(sendToId)) {
			PushMessageUtil.sendMessageToOneCallBack(currentId, "showMessage", "ָ����������Ϣ����ΪNull", "clickEvent");
		} else {

			PushMessageUtil.sendMessageToOneCallBack(sendToId, "showMessage",
					"[" + currentId + "] ���㷢��:[" + content + "]", "clickEvent");
		}
		return "/WEB-INF/page/login/main";
	}

	@RequestMapping(value = "/infoweb.do", method = RequestMethod.POST)
	public ModelAndView infoweb2(HttpServletRequest request, String currentId) {
		ModelAndView ma = infoweb(request, currentId);
		return ma;
	}

	@RequestMapping(value = "/infoweb.do", method = RequestMethod.GET)
	public ModelAndView infoweb(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(currentId);
		String privilege = mLoginEntity.getPrivilege() + "";
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(currentId, privilege);
		AreaBeanEntity abe = new AreaBeanEntity();
		abe = mAreaDao.getAllAreaInfo(currentId, privilege);
		List<AreaBeanEntity> areaList = abe.getAbeList();
		Map<Integer, String> areaMap = abe.getAreaMap();
		request.removeAttribute("areaBean");
		request.removeAttribute("areaList");
		request.removeAttribute("areaMap");
		request.setAttribute("areaBean", areaLists);
		request.setAttribute("areaList", areaList);
		request.setAttribute("areaMap", areaMap);
		// modelAndView.addObject("alarmInfos", lists);

		modelAndView.setViewName("/WEB-INF/page/main/main");
		return modelAndView;
	}

	@RequestMapping(value = "/loginOutWelcome.do", method = RequestMethod.GET)
	public ModelAndView loginOutWelcome2(HttpServletRequest request, String currentId) {
		ModelAndView ma = loginOutWelcome(request, currentId);
		return ma;
	}

	@RequestMapping(value = "/loginOutWelcome.do", method = RequestMethod.POST)
	public ModelAndView loginOutWelcome(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		LoginDao mLoginDao = new LoginDaoImpl();
		mLoginDao.updateLoginState(currentId, 0, null);
		request.getSession().removeAttribute("userId");
		request.getSession().removeAttribute("privId");
		modelAndView.setViewName("/welcome");
		return modelAndView;
	}

	@RequestMapping(value = "/loginOut.do", method = RequestMethod.POST)
	public ModelAndView loginOut2(HttpServletRequest request, String currentId) {
		ModelAndView ma = loginOut(request, currentId);
		return ma;
	}

	@RequestMapping(value = "/loginOut.do", method = RequestMethod.GET)
	public ModelAndView loginOut(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		LoginDao mLoginDao = new LoginDaoImpl();
		mLoginDao.updateLoginState(currentId, 0, null);
		request.getSession().removeAttribute("userId");
		request.getSession().removeAttribute("privId");
		modelAndView.setViewName("/WEB-INF/page/login/login");
		return modelAndView;
	}

	@RequestMapping(value = "/loginLoss.do", method = RequestMethod.POST)
	public ModelAndView loginLoss2(HttpServletRequest request, String currentId) {
		ModelAndView ma = loginOut(request, currentId);
		return ma;
	}

	@RequestMapping(value = "/loginLoss.do", method = RequestMethod.GET)
	public ModelAndView loginLoss(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		request.getSession().removeAttribute("userId");
		request.getSession().removeAttribute("privId");
		modelAndView.setViewName("/WEB-INF/page/login/login");
		return modelAndView;
	}

	@RequestMapping(value = "/resetUserPrivilege.do", method = RequestMethod.POST)
	public ModelAndView resetUserPrivilege(HttpServletRequest request, String currentId, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		String privilege = request.getParameter("privi");
		String userId = request.getParameter("userId");
		// System.out.println(">>>>>>>>>>>>>>>>>"+privilege+">>>>>>>>>>"+userId);
		String message = "";
		if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(privilege)) {
			LoginDao mLoginDao = new LoginDaoImpl();
			int result = mLoginDao.updateUserInfo(userId, privilege);
			if (result > 0) {
				message = "���óɹ�," + userId + "Ȩ��Ϊ��" + privilege;
			} else {
				message = "����ʧ��";
			}
		}
		modelAndView.addObject("message", message);
		modelAndView.setViewName("/user_set_privilege");
		return modelAndView;
	}

	@RequestMapping(value = "/resetUserPrivilege.do", method = RequestMethod.GET)
	public ModelAndView resetUserPrivilege2(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView = resetUserPrivilege(request, currentId, response);
		return modelAndView;
	}

}
