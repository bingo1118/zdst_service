package com.cloudfire.controller.bq;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.MapControlDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.MapControlDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.StaticsAnalysEntity;
import com.cloudfire.until.Utils;

@Controller
public class BQStatisticAnalysisController {
	private LoginDao mLoginDao;
	private AreaDao mAreaDao;
	private MapControlDao mMapControlDao;

	@RequestMapping(value = "/getBQStatisticAnalys.do", method = RequestMethod.GET)
	public ModelAndView toAdd(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(currentId);
		String privilege = mLoginEntity.getPrivilege() + "";
		
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserIdLg(currentId, privilege);  //��ȡ�û�ӵ�е�һ������������
		
		mMapControlDao = new MapControlDaoImpl();
		AreaAndRepeater mAreaAndRepeater = new AreaAndRepeater();
		
		String areaId = request.getParameter("areaId");
		// �����Ϊ�յ�ʱ���ǲ�ѯ��Ϊ�յ�ʱ���ǵ�һ������ҳ��
		List<String> areIds = null;  //�û�ӵ�е�areaId��
		if (Utils.isNullStr(areaId)) { //��ѯ
			mAreaAndRepeater.setAreaId(-1);
			String areaName = request.getParameter("areaName");
			mAreaAndRepeater.setAreaName(areaName);
		} else { // ��һ�μ���
			mAreaAndRepeater.setAreaId(0);
			mAreaAndRepeater.setAreaName("ȫ��");
			areIds = mAreaDao.getAreaStr(currentId, privilege);
		}
		
		
		
		String StartDate = request.getParameter("StartDate");
		String EndDate = request.getParameter("EndDate");
		if (Utils.isNullStr(StartDate)) {
			StartDate = StartDate + "00:00:00";
		}
		if (Utils.isNullStr(EndDate)) {
			EndDate = EndDate + "23:59:59";
		}
		SmartControlDao scd = new SmartControlDaoImpl();
		
		List<String> listAreaId = null;  //��ѯ��areaId��
		if (areaId != null&&areaId.length()>0) {
			String[] array = areaId.split(",");
//			int size = array.length;
//			String arrayInt[] = new String[size];
//			for (int i = 0; i < size; i++) {
//				arrayInt[i] = array[i];
//			}
//			listAreaId = Arrays.asList(arrayInt);
			listAreaId = Arrays.asList(array);
		}
		
		StaticsAnalysEntity smokeList = scd.getAllElectricinfoLg(areIds, listAreaId, StartDate, EndDate);
		
		if (Utils.isNullStr(StartDate)) {
			StartDate = StartDate.substring(0, 10);
		} else {
			StartDate = "����";
		}
		if (Utils.isNullStr(EndDate)) {
			EndDate = EndDate.substring(0, 10);
		} else {
			EndDate = "����";
		}
		request.getSession().removeAttribute("StartDate");
		request.getSession().removeAttribute("EndDate");
		request.getSession().setAttribute("StartDate", StartDate);
		request.getSession().setAttribute("EndDate", EndDate);
		modelAndView.addObject("smokeList", smokeList);
		modelAndView.addObject("areaLists", areaLists);
		modelAndView.addObject("mAreaAndRepeater", mAreaAndRepeater);
		modelAndView.setViewName("/WEB-INF/page/bq/staticsanalys/bqstaticsanalys");
		return modelAndView;
	}
}
