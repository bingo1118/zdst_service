package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cloudfire.dao.AlarmInfoDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.BuildingDao;
import com.cloudfire.dao.CompanyDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.MapControlDao;
import com.cloudfire.dao.NFCDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.AlarmInfoDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.BuildingDaoImpl;
import com.cloudfire.dao.impl.CompanyDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.MapControlDaoImpl;
import com.cloudfire.dao.impl.NFCDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.AlarmInfoEntity;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.AreaBeanEntity;
import com.cloudfire.entity.CompanyEntity;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.NFCInfoEntity;
import com.cloudfire.entity.NFC_infoEntity;
import com.cloudfire.entity.PageBeanEntity;
import com.cloudfire.entity.ShopTypeEntity;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.MainIndexEntityQuery;
import com.cloudfire.entity.query.TenanceEntityQuery;
import com.cloudfire.myservice.CompanyService;
import com.cloudfire.myservice.impl.CompanyServiceImpl;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;
import common.page.Pagination;

@Controller
public class InfoManager {

	private AreaDao mAreaDao;
	private LoginDao mLoginDao;
	private AlarmInfoDao mAlarmInfoDao;
	private MapControlDao mMapControlDao;
	private InfoManagerDao infoManagerDao;
	private InfoManagerDao infoManager;
	private SmartControlDao mSmartControlDao;

	@RequestMapping(value = "maintenancebyprovice.do", method = RequestMethod.GET)
	public void maintenancebyprovice(HttpServletRequest request,
			HttpServletResponse response, String currentId) {
		String companyName = request.getParameter("companyName");
		String provinceID = request.getParameter("provinceID");
		if (provinceID == "9" || "9".equals(provinceID)) {
			provinceID = null;
		}
		currentId = (String) request.getSession().getAttribute("userId");
		// try {
		// companyName = new String(companyName.getBytes("ISO-8859-1"),"UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		List<CompanyEntity> comlist = new ArrayList<CompanyEntity>();
		infoManager = new InfoManagerDaoImpl();
		comlist = infoManager.getInfoByProvinceID(provinceID, companyName,
				currentId);
		if (comlist != null && comlist.size() > 0) {
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(comlist);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/indexWebQuery.do", method = RequestMethod.POST)
	public ModelAndView indexWebQuery2(HttpServletRequest request, 
			String currentId, MainIndexEntityQuery query, ModelMap model,String areaId,
			Integer pageNo) {
		ModelAndView ma = indexWebQuery(request, currentId, query, model,
				pageNo);
		return ma;
	}

	@RequestMapping(value = "/indexWebQuery.do", method = RequestMethod.GET)
	public ModelAndView indexWebQuery(HttpServletRequest request,
			String currentId, MainIndexEntityQuery query, ModelMap model,
			Integer pageNo) {
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		
		String parentId = request.getParameter("parentId");
		String areaId = request.getParameter("areaId");
		if(StringUtils.isBlank(areaId)){  //此处给areaId赋值0是为了在前端查询时保证url正确，默认查询所有区域
			areaId = (String) request.getSession().getAttribute("areaId");
			if(StringUtils.isBlank(areaId)){
				areaId = "0";
			}
		}
		String named = request.getParameter("named");
		String characterId = request.getParameter("characterId");
		String placetypeId = request.getParameter("placetypeId");
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaId(Integer.parseInt(areaId));
			params.append("areaId=").append(Integer.parseInt(areaId));
			request.setAttribute("areaId", query.getAreaId());
		}
		if (StringUtils.isNotBlank(parentId)) {
			query.setParentId(Integer.parseInt(parentId));
			params.append("&parentId=").append(Integer.parseInt(parentId));
			request.setAttribute("parentId", parentId);
		}
		if (StringUtils.isNotBlank(named)) {
			query.setName(named.trim());
			params.append("&named=").append(named);
			request.setAttribute("named", query.getName());
		}
		if (StringUtils.isBlank(characterId)) {
			characterId = "9";
		}
		if (StringUtils.isNotBlank(characterId)) {
			query.setCharacterId(Integer.parseInt(characterId));
			params.append("&characterId=").append(characterId);
			request.setAttribute("characterId", query.getCharacterId());
		}
		if (StringUtils.isNotBlank(placetypeId)) {
			query.setPlaceTypeId(placetypeId);
			params.append("&placetypeId=").append(placetypeId);
			request.setAttribute("placetypeId", query.getPlaceTypeId());
		}

		ModelAndView modelAndView = new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		if (areaIds != null) {
			Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
			List<ShopTypeEntity> shopList = new ArrayList<ShopTypeEntity>();
			query.setAreaIds(areaIds);
			InfoManagerDao infoManager = new InfoManagerDaoImpl();
			CompanyService service = new CompanyServiceImpl();
			Pagination pagination = service.selectSmokeInfo(areaId, query);
			pagination.pageView("/fireSystem/indexWebQuery.do",params.toString());
			shopList = infoManager.getShopTypeEntity();
			model.addAttribute("pagination", pagination);
			modelMap.put("shopList", shopList);
			modelAndView.addAllObjects(model);
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/WEB-INF/page/main/main_list_items");
		return modelAndView;
	}

	@RequestMapping(value = "/InfoManager.do", method = RequestMethod.GET)
	public ModelAndView infoManager(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String currentPageStr = request.getParameter("currentPage");
		String pageNameCount = request.getParameter("pageNameCount");
//		String method = request.getParameter("getArea");
		String areaId = request.getParameter("areaId");
		LoginDao ld = new LoginDaoImpl();
		LoginEntity mLoginEntity = ld.login(currentId);

		int privilege = mLoginEntity.getPrivilege();
		Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
		List<SmokeBean> smokeList = new ArrayList<SmokeBean>();
		List<ShopTypeEntity> shopList = new ArrayList<ShopTypeEntity>();

		InfoManagerDao infoManager = new InfoManagerDaoImpl();

		PageBeanEntity pageBean = new PageBeanEntity();
		pageBean.setAreaId(areaId);
		String totalCountstr = request.getParameter("totalCount");
		int totalCount = 0;
		if (Utils.isNullStr(totalCountstr)) {
			totalCount = Integer.parseInt(totalCountstr);
		}
		pageBean.setTotalCount(totalCount);

		if (pageNameCount == null) { // 設置當前頁
			if (currentPageStr != null) {
				int currentPage = Integer.parseInt(currentPageStr); // 获取当前页
				if (currentPage <= 0)
					currentPage = 1;
				if (currentPage > pageBean.getTotalPage())
					currentPage = pageBean.getTotalPage();
				pageBean.setCurrentPage(currentPage); // 当前页
			} else {
				int currentPage = 1; // 当前页
				pageBean.setCurrentPage(currentPage); // 当前页
			}
		} else {
			int currentPage = Integer.parseInt(pageNameCount);
			if (currentPage > pageBean.getTotalPage())
				currentPage = pageBean.getTotalPage();
			if (currentPage < 1)
				currentPage = 1;
			pageBean.setCurrentPage(currentPage);
		}
		if (privilege == 4) {
			smokeList = infoManager.getAllSmokes(pageBean, null, null);
		} else {
			smokeList = infoManager.getAllSmokes(pageBean, currentId,
					areaId);
		}
		List<PageBeanEntity> pageList = new ArrayList<PageBeanEntity>();
		pageList.add(pageBean);
		shopList = infoManager.getShopTypeEntity();
		modelMap.put("shopList", shopList);
		modelMap.put("smokeL", smokeList);
		modelAndView.addObject("pageList", pageList);
		modelAndView.addAllObjects(modelMap);

		modelAndView.setViewName("/WEB-INF/page/main/main_list_items");
		return modelAndView;
	}

	@RequestMapping(value = "/ckxq.do", method = RequestMethod.GET)
	public ModelAndView Ckxq(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String phone = request.getParameter("phone");
		CompanyDao companyDao = new CompanyDaoImpl();

		Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
		List<SmokeBean> smokeList = new ArrayList<SmokeBean>();
		InfoManagerDao infoManager = new InfoManagerDaoImpl();
		smokeList = infoManager.getAllSmokes(currentId);
		modelMap.put("smokeL", smokeList);

		List<CompanyEntity> companyList = new ArrayList<CompanyEntity>();
		companyList = companyDao.getAllCompanyBy(phone);

		modelMap.put("companyList", companyList);
		modelAndView.addAllObjects(modelMap);

		modelAndView.setViewName("/WEB-INF/page/main/jianzhudanwei_jibenxinxi");
		return modelAndView;
	}

	@RequestMapping(value = "/InfoCharater.do", method = RequestMethod.GET)
	public void GetInfoCharater(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(currentId);
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(
				currentId, mLoginEntity.getPrivilege() + "");
		mMapControlDao = new MapControlDaoImpl();
		AreaAndRepeater mAreaAndRepeater = areaLists.get(0);
		if (mAreaAndRepeater != null) {
			List<SmokeBean> listSmokes = new ArrayList<SmokeBean>();
			infoManagerDao = new InfoManagerDaoImpl();
			String provinceID = request.getParameter("provinceID"); // 单位性质
			String placeTypeId = request.getParameter("placeTypeId"); // 行业类别
			String companyName = request.getParameter("companyName"); // 名称
			String areaIdstr = request.getParameter("areaId"); // 区域
			String currentPage = request.getParameter("currentPage"); // 当前页
			String totalCount = request.getParameter("totalCount"); // 总记录数
			PageBeanEntity pageBean = new PageBeanEntity();
			if (Utils.isNullStr(currentPage)) { // 设置当前页
				pageBean.setCurrentPage(Integer.parseInt(currentPage));
			} else {
				pageBean.setCurrentPage(1);
			}
			if (Utils.isNullStr(totalCount)) {
				pageBean.setTotalCount(Integer.parseInt(totalCount));
			} else {
				pageBean.setTotalCount(infoManagerDao
						.getTotalCount(areaIdstr));
			}

			int totalPage = 0;
			if (pageBean.getTotalCount() % 10 == 0) {
				totalPage = pageBean.getTotalCount() / 10;
			} else
				totalPage = pageBean.getTotalCount() / 10 + 1; // 获取总页数
			pageBean.setTotalPage(totalPage); // 設置總頁數

			if (pageBean.getCurrentPage() <= 0) {
				pageBean.setCurrentPage(1);
			} else if (pageBean.getCurrentPage() >= pageBean.getTotalPage()) {
				pageBean.setCurrentPage(pageBean.getTotalPage());
			}

			int areaId = 0;
			if (Utils.isNullStr(areaIdstr)) {
				areaId = Integer.parseInt(areaIdstr);
			}
			/*
			 * try { companyName = new
			 * String(companyName.getBytes("ISO-8859-1") , "UTF-8"); } catch
			 * (UnsupportedEncodingException e1) { e1.printStackTrace(); }
			 */
			listSmokes = infoManagerDao.getSmokeByIdAndName(provinceID,
					placeTypeId, companyName, areaId, pageBean);

			if (listSmokes != null && listSmokes.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(listSmokes);
				try {
					// System.out.println(jObject);
					response.getWriter().write(jObject);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@RequestMapping(value = "/company.do", method = RequestMethod.GET)
	public void company(HttpServletRequest request, String currentId,
			HttpServletResponse response) {
		currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(currentId);
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(
				currentId, mLoginEntity.getPrivilege() + "");
		mMapControlDao = new MapControlDaoImpl();
		AreaAndRepeater mAreaAndRepeater = areaLists.get(0);
		int comId = Integer.parseInt(request.getParameter("cid"));
		if (mAreaAndRepeater != null) {
			infoManagerDao = new InfoManagerDaoImpl();

			CompanyDao comDao = new CompanyDaoImpl();
			if (comDao.removed(comId)) {
			}
			List<CompanyEntity> comList = new ArrayList<CompanyEntity>();
			InfoManagerDao inDao = new InfoManagerDaoImpl();
			comList = inDao.getInfoAlls();
			if (comList != null && comList.size() > 0) {
				WriteJson writeJson = new WriteJson();
				String jObject = writeJson.getJsonData(comList);
				try {
					response.getWriter().write(jObject);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}

	}

	@RequestMapping(value = "/InfoManagers.do", method = RequestMethod.GET)
	public ModelAndView DerInfoManager(HttpServletRequest request,
			String currentId) {

		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		mLoginDao = new LoginDaoImpl();
		Map<String, List> modelMap = new HashMap<String, List>();
		List<SmokeBean> infoList = new ArrayList<SmokeBean>();
		InfoManagerDao infoManager = new InfoManagerDaoImpl();
		String method = request.getParameter("method");
		if ("getArea".equals(method)) {
			String areaId = request.getParameter("areaId");
			PageBeanEntity pageBean = new PageBeanEntity();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			String privilege = mLoginEntity.getPrivilege() + "";
			if (privilege == "4" || "4".equals(privilege)) {
				infoList = infoManager.getSmokesByAreaId(areaId, pageBean,
						null);
			} else {
				infoList = infoManager.getSmokesByAreaId(areaId, pageBean,
						currentId);
			}

			List<PageBeanEntity> pageList = new ArrayList<PageBeanEntity>();
			pageList.add(pageBean);
			modelAndView.addObject("pageList", pageList);
			modelMap.put("smokeL", infoList);
			modelAndView.addAllObjects(modelMap);
		}
		modelAndView.setViewName("/WEB-INF/page/main/main_list_items");
		return modelAndView;
	}

	@RequestMapping(value = "/maintenance.do", method = RequestMethod.GET)
	public ModelAndView maintenance(HttpServletRequest request, String currentId) {
		ModelAndView modelAndView = new ModelAndView();

		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("userId");
		mAreaDao = new AreaDaoImpl();
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(
				currentId, privilege);
		List<String> areaList = mAreaDao.getAreaStr(currentId,
				privilege);
		List<AlarmInfoEntity> alarmList = mAlarmInfoDao
				.getAlarmInfo(areaList);
		modelAndView.addObject("areaBean", areaLists);
		modelAndView.addObject("alarmInfos", alarmList);
		modelAndView.setViewName("/WEB-INF/page/main/maintenance_list");
		return modelAndView;
	}

	@RequestMapping(value = "/tenancelistpage.do", method = RequestMethod.GET)
	public ModelAndView tenancelistpage(HttpServletRequest request,
			TenanceEntityQuery query, ModelMap model, Integer pageNo) {
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		String areaId = request.getParameter("areaId");
		if (areaId == null) {
			areaId = (String) request.getSession().getAttribute(areaId);
		}
		String companyName = request.getParameter("companyName");
		/*
		 * try { if(Utils.isNullStr(companyName)){ companyName = new
		 * String(companyName.getBytes("ISO-8859-1") , "UTF-8"); } } catch
		 * (UnsupportedEncodingException e) { e.printStackTrace(); }
		 */
		String provinceId = request.getParameter("provinceID");
		StringBuilder params = new StringBuilder();
		if (Utils.isNullStr(areaId)) {
			areaId = "0";
		}
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaIdstr(areaId);
			params.append("areaId=").append(Integer.parseInt(areaId));
			request.setAttribute("areaId", query.getAreaIdstr());
		}
		if (StringUtils.isNotBlank(companyName)) {
			query.setComanyName(companyName);
			params.append("&companyName=").append(companyName);
			request.setAttribute("company", query.getComanyName());
		}
		if (!Utils.isNullStr(provinceId)) {
			provinceId = "9";
		}
		if (StringUtils.isNotBlank(provinceId)) {
			query.setCharacterId(Integer.parseInt(provinceId));
			params.append("&provinceId=").append(provinceId);
			request.setAttribute("provinceId", query.getCharacterId());
		}

		request.getSession().setAttribute("areaId", areaId);
		String currentId = (String) request.getSession().getAttribute("userId");

		ModelAndView modelAndView = new ModelAndView();
		if (StringUtils.isBlank(currentId)) {
			modelAndView.setViewName("/WEB-INF/page/login/login");
		} else {
//			Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
//			List<CompanyEntity> infoList = new ArrayList<CompanyEntity>();
//			InfoManagerDao infoManager = new InfoManagerDaoImpl();
			CompanyService service = new CompanyServiceImpl();
			Pagination pagination = service.selectCompanyInfo(areaId, query);
			pagination.pageView("/fireSystem/tenancelistpage.do",
					params.toString());
			model.addAttribute("pagination", pagination);
			modelAndView.addAllObjects(model);
			modelAndView.setViewName("/WEB-INF/page/main/main_list_maintenance");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/maintenancepage.do", method = RequestMethod.GET)
	public void maintenancepage(HttpServletRequest request,
			HttpServletResponse response, String currentId,
			TenanceEntityQuery query, ModelMap model, Integer pageNo) {
		String companyName = request.getParameter("companyName");
		String provinceID = request.getParameter("provinceID");
		if (provinceID == "9" || "9".equals(provinceID)) {
			provinceID = null;
		}
		currentId = (String) request.getSession().getAttribute("userId");
		/*
		 * try { companyName = new
		 * String(companyName.getBytes("ISO-8859-1"),"UTF-8"); } catch
		 * (UnsupportedEncodingException e) { e.printStackTrace(); }
		 */
		List<CompanyEntity> comlist = new ArrayList<CompanyEntity>();
		infoManager = new InfoManagerDaoImpl();
		comlist = infoManager.getInfoByProvinceID(null, companyName, currentId);
		if (comlist != null && comlist.size() > 0) {
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(comlist);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/building.do", method = RequestMethod.GET)
	public ModelAndView building(HttpServletRequest request, String currentId) {

		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		Map<String, List> modelMap = new HashMap<String, List>();
		String privilege = (String) request.getSession().getAttribute("privilege");
		mAreaDao = new AreaDaoImpl();
		mAlarmInfoDao = new AlarmInfoDaoImpl();
		List<String> areIds = mAreaDao.getAreaStr(currentId, privilege);
		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(
				currentId, privilege);
		List<AlarmInfoEntity> alarmList = mAlarmInfoDao
				.getAlarmInfo(areIds);
		modelMap.put("areaBean", areaLists);
		modelAndView.addObject("alarmInfos", alarmList);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/main/building_list");
		return modelAndView;
	}

	@RequestMapping(value = "/buildingList.do", method = RequestMethod.GET)
	public ModelAndView buildingList(HttpServletRequest request,
			String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
		List<AreaBean> smokeList = new ArrayList<AreaBean>();
		AreaDao areaDao = new AreaDaoImpl();
		smokeList = areaDao.getAll();
		List<CompanyEntity> ceList = new ArrayList<CompanyEntity>();
		BuildingDao bd = new BuildingDaoImpl();
		ceList = bd.getBuildInfo();
		modelMap.put("ceList", ceList);
		modelMap.put("areaBean", smokeList);
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/main/main_list_building");
		return modelAndView;
	}

	@RequestMapping(value = "/patrolRecord.do", method = RequestMethod.POST)
	public ModelAndView patrolRecord2(HttpServletRequest request,
			String currentId) {
		ModelAndView ma = patrolRecord(request, currentId);
		return ma;
	}

	@RequestMapping(value = "/patrolRecord.do", method = RequestMethod.GET)
	public ModelAndView patrolRecord(HttpServletRequest request,
			String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		mAreaDao = new AreaDaoImpl();
//		AreaBeanEntity abe =  mAreaDao.getAllAreaInfo(currentId, privilege);
		List<AreaAndRepeater> areaLists = mAreaDao.getNFCAreasByUserId(currentId, privilege);
//		
//		List<AreaBeanEntity> areaList = abe.getAbeList();
//		Map<Integer, String> areaMap = abe.getAreaMap();
		request.setAttribute("areaLists", areaLists);
//		request.setAttribute("areaList", areaList);
//		request.setAttribute("areaMap", areaMap);
		
		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/tenance_main");
	
		return modelAndView;
	}
	@RequestMapping(value = "/printNFCRecordMain.do", method = RequestMethod.GET)
	public ModelAndView printNFCRecordMain(HttpServletRequest request,
			String currentId) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		mAreaDao = new AreaDaoImpl();
//		AreaBeanEntity abe =  mAreaDao.getAllAreaInfo(currentId, privilege);
		List<AreaAndRepeater> areaLists = mAreaDao.getNFCAreasByUserId(currentId, privilege);
//		
//		List<AreaBeanEntity> areaList = abe.getAbeList();
//		Map<Integer, String> areaMap = abe.getAreaMap();
		request.setAttribute("areaLists", areaLists);
//		request.setAttribute("areaList", areaList);
//		request.setAttribute("areaMap", areaMap);
		
		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/NFCRecord_main");
	
		return modelAndView;
	}
	@RequestMapping(value="/printNFCRecord.do",method=RequestMethod.GET)
	public void area(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, JSONException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		String areaId = request.getParameter("areaId");
		request.setAttribute("areaId", areaId);
		request.getRequestDispatcher("/WEB-INF/page/PatrolRecord/NFCRecord_list.jsp").forward(request, response);
	}
	@RequestMapping(value = "/tenance_main.do", method = RequestMethod.GET)
	public ModelAndView tenance_main(HttpServletRequest request,
			String currentId, NFC_infoEntity query, ModelMap model,
			Integer pageNo) {
		ModelAndView modelAndView = new ModelAndView();
	
		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
//		String parentId = request.getParameter("parentId");
		String areaId = request.getParameter("areaId");
		if (StringUtils.isBlank(areaId)) {
			areaId = "0";
		}
		String NFC_info_mac = request.getParameter("NFC_info_mac");
		String NFC_info_name = request.getParameter("NFC_info_name");
		String NFC_info_state = request.getParameter("NFC_info_state");
		
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaId(areaId);
			params.append("areaId=").append(Integer.parseInt(areaId));
			request.setAttribute("areaId", query.getAreaId());
		}
		if (StringUtils.isNotBlank(NFC_info_mac)) {
			query.setUid(NFC_info_mac.trim());
			params.append("&NFC_info_mac=").append(NFC_info_mac);
			request.setAttribute("NFC_info_mac", query.getUid());
		}
		if (StringUtils.isNotBlank(NFC_info_name)) {
			query.setDeviceName(NFC_info_name.trim());
			params.append("&NFC_info_name=").append(NFC_info_name);
			request.setAttribute("NFC_info_name", query.getDeviceName());
		}
		if (StringUtils.isNotBlank(NFC_info_state)) {
			query.setDevicestate(NFC_info_state);
			params.append("&NFC_info_state=").append(NFC_info_state);
			request.setAttribute("NFC_info_state", query.getDevicestate());
		}
		
		query.setUserId(currentId);
		mAreaDao = new AreaDaoImpl();
//		List<AreaBean> lstab = null;
//				mAreaDao.getNFCAreaByUserId2(currentId, privilege); //获取所有的nfc区域信息

//		AreaBeanEntity abe = new AreaBeanEntity();
//		abe = mAreaDao.getAllAreaInfo(currentId, privilege );
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
//		if(StringUtils.isNotBlank(parentId)){
//			areaIds = mAreaDao.getAreaStr(parentId);
//		}else{
		
//		}

		CompanyService service = new CompanyServiceImpl();
		Pagination pagination = null;
		if (areaIds != null) {
			pagination = service.selectNFC_info(areaIds, query);
			String string = null;
			if (params.length() != 0) {
				string = params.toString();
			}
			pagination.pageView("/fireSystem/tenance_main.do", string);
			model.addAttribute("pagination", pagination);
//			model.addAttribute("lstab",lstab);
			modelAndView.addAllObjects(model);
		}

		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/tenance_main_list");
		return modelAndView;
	}
	
	@RequestMapping(value = "/tenance_all_recordlist.do", method = RequestMethod.GET)
	public ModelAndView tenance_all_recordlist(HttpServletRequest request,HttpServletResponse response,
			String currentId, NFC_infoEntity query, ModelMap model,
			Integer pageNo) throws ServletException, IOException {
		ModelAndView modelAndView = new ModelAndView();

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
//		query.setPageSize(Constant.PAGESIZE);
		query.setPageSize(1000);
		String NFC_info_mac = request.getParameter("NFC_info_mac");
		StringBuilder params = new StringBuilder();
		String areaId = request.getParameter("areaId");
		if (areaId == null || areaId.equals("")) {
			areaId = "0";
		}
		if (StringUtils.isNotBlank(areaId)) {
			query.setAreaId(areaId);
			params.append("&areaId=").append(areaId);
			request.setAttribute("areaId", query.getAreaId());
		} else {
			areaId = (String) request.getAttribute("areaId");
		}
		if (StringUtils.isNotBlank(NFC_info_mac)) {
			query.setUid(NFC_info_mac.trim());
			params.append("&NFC_info_mac").append(NFC_info_mac);
			request.setAttribute("NFC_info_mac", query.getUid());
		}
		String NFC_info_name = request.getParameter("NFC_info_name");
		if (StringUtils.isNotBlank(NFC_info_name)) {
			query.setDeviceName(NFC_info_name.trim());
			params.append("&NFC_info_name=").append(NFC_info_name);
			request.setAttribute("NFC_info_name", query.getDeviceName());
		}
		String NFC_info_state = request.getParameter("NFC_info_state");
		if (StringUtils.isNotBlank(NFC_info_state)) {
			query.setDevicestate(NFC_info_state);
			params.append("&NFC_info_state=").append(NFC_info_state);
			request.setAttribute("NFC_info_state", query.getDevicestate());
		}
		String J_xl_1= request.getParameter("J_xl_1");
		if (StringUtils.isNotBlank(J_xl_1)) {
			query.setEndTime_1(J_xl_1);
			params.append("&J_xl_1=").append(J_xl_1);
			request.setAttribute("J_xl_1", query.getEndTime_1());
		}
		String J_xl_2 = request.getParameter("J_xl_2");
		if (StringUtils.isNotBlank(J_xl_1)) {
			query.setEndTime_2(J_xl_2);
			params.append("&J_xl_2=").append(J_xl_2);
			request.setAttribute("J_xl_2", query.getEndTime_2());
		}
		
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		query.setAreaIds(areaIds);
		if (areaIds != null) {
			CompanyService service = new CompanyServiceImpl();
			Pagination pagination = service.selectNFC_info_record(NFC_info_mac, query);
			pagination.pageView("/fireSystem/tenance_all_recordlist.do",params.toString());
			model.addAttribute("pagination", pagination);
			modelAndView.addAllObjects(model);
		}
		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/tenance_main_record_list");
		return modelAndView;
	}
//	123
	@RequestMapping(value = "/tenance_all_record.do", method = RequestMethod.GET)
	public ModelAndView tenance_all_record(HttpServletRequest request,
			String currentId, NFC_infoEntity query, ModelMap model,
			Integer pageNo) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege = (String) request.getSession().getAttribute("privilege");
		
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getNFCRecordAreasByUserId(currentId, privilege );
		request.setAttribute("areaLists", areaLists);
//		AreaBeanEntity abe = new AreaBeanEntity();
//		abe = mAreaDao.getAllAreaInfo(currentId, privilege );
//		List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
//
//		List<AreaAndRepeater> areaLists = mAreaDao.getAllAreaByUserId(
//				currentId, privilege );
//		if (areaIds == null || areaIds.size() == 0) {
//			areaIds = new ArrayList<String>();
//		}
//		List<AreaBeanEntity> areaList = abe.getAbeList();
//		Map<Integer, String> areaMap = abe.getAreaMap();
//		request.setAttribute("areaBean", areaLists);
//		request.setAttribute("areaList", areaList);
//		request.setAttribute("areaMap", areaMap);
		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/tenance_main_record");
		
		return modelAndView;
	}
	/**
	 * 巡检记录
	 * @param request
	 * @param currentId
	 * @param query
	 * @param model
	 * @param pageNo
	 * @return
	 */
	@RequestMapping(value = "/tenance_main_record.do", method = RequestMethod.GET)
	public ModelAndView tenance_main_record(HttpServletRequest request,
			String currentId, NFC_infoEntity query, ModelMap model,
			Integer pageNo) {
		ModelAndView modelAndView = new ModelAndView();
		currentId = (String) request.getSession().getAttribute("userId");

		pageNo = Pagination.cpn(pageNo);
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		String NFC_info_mac = request.getParameter("uuid");
		StringBuilder params = new StringBuilder();
		params.append("1=1");
		if (StringUtils.isNotBlank(NFC_info_mac)) {
			query.setUid(NFC_info_mac);
			params.append("&uuid=").append(NFC_info_mac);
			request.setAttribute("NFC_info_mac", query.getUid());
		}
		String nowstate = request.getParameter("nowstate");
		if (StringUtils.isNotBlank(nowstate)) {
			query.setNowState(nowstate);
			params.append("&nowstate=").append(nowstate);
			request.setAttribute("nowstate", query.getNowState());
		}
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		query.setAreaIds(areaIds);
		if (areaIds != null) {
			CompanyService service = new CompanyServiceImpl();
			//Pagination pagination = service.selectNFC_info_record(NFC_info_mac, query);
			Pagination pagination = service.selectNFC_info_record1(NFC_info_mac, query);
			pagination.pageView("/fireSystem/tenance_main_record.do", params.toString());
			model.addAttribute("pagination", pagination);
			modelAndView.addAllObjects(model);
		}
		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/tenance_main_list_record");
		return modelAndView;
	}
	@RequestMapping(value = "/tenance_map.do", method = RequestMethod.GET)
	public ModelAndView tenance_map(HttpServletRequest request, String currentId) {
		currentId = (String) request.getSession().getAttribute("userId");
		String privilege =  (String) request.getSession().getAttribute("privilege");
		
		mAreaDao = new AreaDaoImpl();
		List<AreaAndRepeater> areaLists = mAreaDao.getNFCAreaByUserId(currentId, privilege);  //返回有nfc设备的区域列表
		
		Map<String, List<?>> modelMap = new HashMap<String, List<?>>();
		modelMap.put("tenance", areaLists);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addAllObjects(modelMap);
		modelAndView.setViewName("/WEB-INF/page/PatrolRecord/tenance_map");

		return modelAndView;
	}
	
	@RequestMapping(value="/getDetailInfo.do",method=RequestMethod.GET)
	public void getDetailInfo(HttpServletRequest request,HttpServletResponse response){
		String mac = request.getParameter("smokeMac");
		NFCDao nd = new NFCDaoImpl();
		NFCInfoEntity nie = nd.getNFCInfo(mac);
		if (nie!=null) {
			JSONObject jObject = new JSONObject(nie);
			try {
				response.getWriter().write(jObject.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	@RequestMapping(value="/updateTxt_area.do",method=RequestMethod.GET)
	 public ModelAndView updateTxt(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model = new ModelAndView();
		mSmartControlDao = new SmartControlDaoImpl();
		@SuppressWarnings("unchecked")
		List<String> areaIds = (List<String>) request.getSession().getAttribute("areaIds");
		List<AreaAndRepeater> areaAndRepeaters = mSmartControlDao.getAreaAndRepeaterNoType(areaIds);
		model.addObject("areaAndRepeaters",areaAndRepeaters);
		model.setViewName("updateTxt_area");
		return model;
	 }

}
