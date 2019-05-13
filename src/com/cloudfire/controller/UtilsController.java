package com.cloudfire.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.entity.AckDeviceBean;
import com.cloudfire.entity.AckToSoundAndDevice;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.until.WriteJson;


@Controller
public class UtilsController {
	
	/**
	 * 按终端查询设备以及声光。
	 */
	@RequestMapping(value = "selectDeviceByRepeater.do",method = RequestMethod.GET)
	public void selectDeviceByRepeater(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String TerminalMac = request.getParameter("TerminalMac");
		request.getSession().removeAttribute("TerminalMac");
		request.getSession().setAttribute("TerminalMac", TerminalMac);
		PublicUtils pu = new PublicUtilsImpl();
		List<AckToSoundAndDevice> acktlist = new ArrayList<AckToSoundAndDevice>();
		acktlist = pu.selectAckBySAD(TerminalMac);
		if(acktlist !=null && acktlist.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(acktlist);
			try {
				response.getWriter().write(jObject);
				System.out.println(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			String jObject = "[]";
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 增加数据后并按终端查询设备以及声光。
	 */
	@RequestMapping(value = "saveDeviceByRepeater.do",method = RequestMethod.GET)
	public void saveDeviceByRepeater(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String TerminalMac = request.getParameter("TerminalMac");
		String deviceCallArray[] = request.getParameterValues("deviceCallArray");
		String deviceResponseArray[] = request.getParameterValues("deviceResponseArray");
		String deviceCallArrayCopy[] = deviceCallArray[0].split(",");
		String deviceResponseArrayCopy[] = deviceResponseArray[0].split(",");
		request.getSession().removeAttribute("TerminalMac");
		request.getSession().setAttribute("TerminalMac", TerminalMac);
		PublicUtils pu = new PublicUtilsImpl();
		pu.saveAckBySAD(TerminalMac, deviceCallArrayCopy, deviceResponseArrayCopy);
		List<AckDeviceBean> ackList = pu.selectAckToDevByRep(TerminalMac);
		if(ackList !=null && ackList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(ackList);
			try {
				response.getWriter().write(jObject);
				System.out.println(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			String jObject = "[]";
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 根据声光来删除相关联的数据。
	 */
	@RequestMapping(value = "deleteDeviceByRepeater.do",method = RequestMethod.GET)
	public void deleteDeviceByRepeater(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String TerminalMac = request.getParameter("TerminalMac");
		String soundMac = request.getParameter("soundMac");
		PublicUtils pu = new PublicUtilsImpl();
		pu.deleteAckBySoundMac(soundMac);
		List<AckDeviceBean> ackList = pu.selectAckToDevByRep(TerminalMac);
		if(ackList !=null && ackList.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(ackList);
			try {
				response.getWriter().write(jObject);
				System.out.println(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			String jObject = "[]";
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 根据声光来修改相关联的数据。
	 */
	@RequestMapping(value = "updateDeviceByRepeater.do",method = RequestMethod.GET)
	public void updateDeviceByRepeater(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String TerminalMac = request.getParameter("TerminalMac");
		String soundMac = request.getParameter("soundMac");
		PublicUtils pu = new PublicUtilsImpl();
		List<AckToSoundAndDevice> atsadtlist = new ArrayList<AckToSoundAndDevice>();
		List<SmokeBean> smokeList = pu.selectDevByRep(TerminalMac);
		List<AckDeviceBean> ackList = pu.selectDevBean(soundMac);
		AckToSoundAndDevice atsad = new AckToSoundAndDevice();
		atsad.setAckList(ackList);
		atsad.setSmokeList(smokeList);
		atsadtlist.add(atsad);
		if(atsadtlist !=null && atsadtlist.size()>0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(atsadtlist);
			try {
				response.getWriter().write(jObject);
				System.out.println(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			String jObject = "[]";
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 绑定烟感数据。
	 */
	@RequestMapping(value = "bindSmokeByUserId.do",method = RequestMethod.POST)
	public void bindSmokeByUserId(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String userName = request.getParameter("username");
		String smokes[] = request.getParameterValues("smokeArr");
		PublicUtils pu = new PublicUtilsImpl();
		int result = pu.bindSmokeByUserName(userName, smokes);
		if(result > 0){
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(null);
			try {
				response.getWriter().write(jObject);
				System.out.println(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 绑定摄像头数据。
	 */
	@RequestMapping(value = "bindCameraByUserId.do",method = RequestMethod.POST)
	public void bindCameraByUserId(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String userName = request.getParameter("username");
		String smokes[] = request.getParameterValues("smokeArr");
		PublicUtils pu = new PublicUtilsImpl();
		int result = pu.bindCameraByUserName(userName, smokes);
		if(result > 0){
			List<String> clist = new ArrayList<String>();
			clist = pu.selectCamera(userName);
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(clist);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 绑定主机数据。
	 */
	@RequestMapping(value = "bindRepeaterByUserId.do",method = RequestMethod.POST)
	public void bindRepeaterByUserId(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String userName = request.getParameter("username");
		String smokes[] = request.getParameterValues("smokeArr");
		PublicUtils pu = new PublicUtilsImpl();
		int result = pu.bindRepeaterByUserName(userName, smokes);
		if(result > 0){
			List<String> clist = new ArrayList<String>();
			clist = pu.selectRepeater(userName);
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(clist);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 绑定传输装置和声光烟感的关系。
	 */
	@RequestMapping(value = "bindRepeaterSoundSmoke.do",method = RequestMethod.POST)
	public void bindRepeaterSoundSmoke(HttpServletRequest request, HttpServletResponse response,String currentId){
		currentId = (String) request.getSession().getAttribute("userId");
		String repeaterMac = request.getParameter("repeaterMac");
		String deviceMac = request.getParameter("deviceMac");
		String soundMac = request.getParameter("soundMac");
		PublicUtils pu = new PublicUtilsImpl();
		int result = pu.bindSmokeSoundRepeater(repeaterMac, soundMac, deviceMac);
		if(result > 0){
			List<AckDeviceBean> clist = new ArrayList<AckDeviceBean>();
			clist = pu.selectFaultInfo(repeaterMac);
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(clist);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 绑定传输装置和声光烟感的关系。
	 */
	@RequestMapping(value = "selectRepeaterSoundSmoke.do",method = RequestMethod.POST)
	public void selectRepeaterSoundSmoke(HttpServletRequest request, HttpServletResponse response,String currentId){
		String repeaterMac = request.getParameter("repeaterMac");
		PublicUtils pu = new PublicUtilsImpl();
		List<AckDeviceBean> clist = new ArrayList<AckDeviceBean>();
		clist = pu.selectFaultInfo(repeaterMac);
		WriteJson writeJson = new WriteJson();
		String jObject = writeJson.getJsonData(clist);
		try {
			response.getWriter().write(jObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 绑定传输装置和声光烟感的关系。
	 */
	@RequestMapping(value = "delRepeaterSoundSmoke.do",method = RequestMethod.POST)
	public void delctRepeaterSoundSmoke(HttpServletRequest request, HttpServletResponse response,String currentId){
		String repeaterMac = request.getParameter("repeaterMac");
		String soundMac = request.getParameter("soundMac");
		String deviceMac = request.getParameter("deviceMac");
		PublicUtils pu = new PublicUtilsImpl();
		int result = pu.delFaultInfo(repeaterMac, soundMac, deviceMac);
		if(result > 0 ){
			List<AckDeviceBean> clist = new ArrayList<AckDeviceBean>();
			clist = pu.selectFaultInfo(repeaterMac);
			WriteJson writeJson = new WriteJson();
			String jObject = writeJson.getJsonData(clist);
			try {
				response.getWriter().write(jObject);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
