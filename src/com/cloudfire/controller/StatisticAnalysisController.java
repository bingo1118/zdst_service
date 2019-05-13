package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.ElectricPCDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.ElectricPCDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.BQEletricAVGData;
import com.cloudfire.entity.SearchDto;

@Controller
public class StatisticAnalysisController {
	private AlarmInfoDao mAlarmInfoDao;
	private ElectricPCDao electricPCDao = new ElectricPCDaoImpl();
	
	// 2017.3.16 15:55

	@RequestMapping(value = "/statisticAnalysis.do", method = RequestMethod.POST)
	public ModelAndView statisticAnalysis2(HttpServletRequest request,
			String currentId) {
		ModelAndView ma = statisticAnalysis(request,currentId);
		return ma;
	}
	@RequestMapping(value = "/statisticAnalysis.do", method = RequestMethod.GET)
	public ModelAndView statisticAnalysis(HttpServletRequest request,
			String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/WEB-INF/page/main/statisticAnalysis");
		return modelAndView;
	}

	/**
	 * 该方法的作用是:点击统计分析模块的后,在左边的电气火灾那里显示的内容
	 * 
	 * @param request
	 * @param currentId
	 * @return 逻辑视图
	 */
	@RequestMapping(value = "/electricAndfireAnalysis.do", method = RequestMethod.GET)
	public ModelAndView electricAndfireAnalysis(HttpServletRequest request,
			String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		if (areaIds != null) {
			mAlarmInfoDao = new AlarmInfoDaoImpl();
			List<AlarmInfoEntity> lists = mAlarmInfoDao
					.getAlarmInfo(areaIds);
			String flag = request.getParameter("flag");
			List<BQEletricAVGData> datas = new ArrayList<BQEletricAVGData>();
			if (!StringUtils.isBlank(flag)) {
				String date1 = request.getParameter("date1");
				String date2 = request.getParameter("date2");
				SearchDto dto = new SearchDto();
				dto.setFire1(date1);
				dto.setFire2(date2);
				datas = electricPCDao.getBqMonthAVGDatas(areaIds, dto);
	
			} else {
				// 根据区域ID，,smokeMac查询设备电压数据
				datas = electricPCDao.getBqMonthAVGDatas(areaIds, null);
			}
			modelAndView.addObject("alarmInfos", lists);
			modelAndView.addObject("datas", datas);
		}
		modelAndView.setViewName("/WEB-INF/page/analysis/electricAndfireAnalysis");
		return modelAndView;
	}

	@RequestMapping(value = "/electricAndfireAnalysisSearch.do", method = RequestMethod.GET)
	public ModelAndView electricAndfireAnalysisSearch(HttpServletRequest request,
			String currentId, HttpServletResponse response) {
		@SuppressWarnings("unchecked")
		List<String> areaIds =  (List<String>)request.getSession().getAttribute("areaIds");//获取用户所管理的区域id
		String date1 = request.getParameter("date1");
		String date2 = request.getParameter("date2");
		SearchDto dto = new SearchDto();
		dto.setFire1(date1);
		dto.setFire2(date2);
		List<BQEletricAVGData> datas  = electricPCDao.getBqMonthAVGDatas(areaIds, dto);
		response.setContentType("text/html;charset=utf-8");
		JSONArray array=JSONArray.fromObject(datas);
		try {
			response.getWriter().write(array.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
