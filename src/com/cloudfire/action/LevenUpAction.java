package com.cloudfire.action;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.entity.RepeaterLevelUp;
import com.cloudfire.thread.LeveUpRepeater;
import com.cloudfire.until.ClientUtilPackage;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class LevenUpAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private File levenFile;
	private String levenFileName;
	private String levenFileContentType;
	private String levenFileFileName;

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public void toLevenUpFile(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		
		String path = "";
		String levelMessage = "升级失败！";
		if(levenFile == null){
			System.out.println(" levelFile is null==========lzo");
			path = "/WEB-INF/page/util/levenUpError.jsp";
		}
		try {
			List<byte[]> leveList = Utils.getLevelUp(levenFile,512);
			String repeaterMac = this.request.getParameter("repeaterMac");
			String levelNum = this.request.getParameter("levelNum");
			if(leveList!=null){
				byte[] hostStartLevenUp = ClientUtilPackage.getHostStartLevenUp(repeaterMac,levelNum);//主机启动包
				System.out.println("levelList is not null========lzo hostStartLevenUp"+hostStartLevenUp);
				int count = 1;
				while(count < 4){
					System.out.println();
					count++;
					Utils.hostLevenUp.put(repeaterMac, -1);
					boolean result = Utils.hostLevenUp(repeaterMac, hostStartLevenUp);
					System.out.println("启动包下发成功状态："+result +"    =======================");
					if(result){//下发成功
						int maxIndex = 0;
						int hostValue = 0;
						System.out.println("Utils.hostLevenUp value is:"+Utils.hostLevenUp.get(repeaterMac)+" ============maxIndex="+maxIndex);
						while(maxIndex<40){	//判断是否开启启动
							maxIndex++;
							Thread.sleep(1000);
							hostValue = Utils.hostLevenUp.get(repeaterMac);
							if(hostValue == 1){
								System.out.println("modify hostvalue success!=============");
								break;
							}
						}
						if(hostValue == 1){	//启动包发送成功，准备发送数据包      设备	主机升级开始回复 0x7e 0x0e 0xd1 递增 1byte 1byte 中继器mac(4)软件版本号（4）（版本号要包括设备类型）1byte	1byte 0x7f	
							maxIndex = 0;
							Utils.hostLevenUp.put(repeaterMac, 0);
							System.out.println("join hostValue maxIndex:"+leveList.size()+" repeaterMac="+repeaterMac+" maxIndex="+maxIndex);
							for(byte[] byet:leveList){
								System.out.println("join leveList :"+leveList.size()+" repeaterMac="+repeaterMac+" maxIndex="+maxIndex+" byet:"+IntegerTo16.bytes2Hex(byet));
								byte[] byteData = ClientUtilPackage.sendLevenUpData(repeaterMac, byet, maxIndex);//处理需要发送的数据包
								System.out.println("byteData num =maxIndex="+maxIndex+"  byteData======="+Arrays.toString(byteData));
								int errorNum = 0;
								while(errorNum < 3){
									System.out.println("join errorNum =="+errorNum);
									errorNum++;
									result = Utils.hostLevenUp(repeaterMac, byteData);
									if(result){
										for(int j = 0;j<40;j++){
											Thread.sleep(1000);
											hostValue = Utils.hostLevenUp.get(repeaterMac);
											if(hostValue == maxIndex+1){
												System.out.println("hostValue is levelUp success:"+hostValue);
												break;
											}
										}
										if(hostValue == maxIndex+1){
											System.out.println("hostValue == maxIndex+1 success:"+hostValue);
											break;	//成功，执行下一条
										}else{
											System.out.println("errorNum == maxIndex+1 errorNum:"+errorNum);
											if(errorNum==3) errorNum++;
											continue;//失败继续重发
										}
									}
								}
								if(errorNum==4){//上包数据发送3次失败
									System.out.println("errorNum == maxIndex+1 errorNum:"+errorNum);
									break;
								}
								maxIndex++;
							}
							if(maxIndex == leveList.size()){	//数据包发送成功后，最后发送结束包
								System.out.println("join maxIndex == leveList.size()=="+maxIndex);
								int inPackage = maxIndex;	//发的总包数
								int indexMax = (int) levenFile.length();
								byte[] hostStopLevenUp = ClientUtilPackage.getHostStopLevenUp(repeaterMac, inPackage, indexMax);
								System.err.println("send to hostStopLevenUp :"+IntegerTo16.bytes2Hex(hostStopLevenUp));
								result = Utils.hostLevenUp(repeaterMac, hostStopLevenUp);	//发送结束包
								if(result){
									for(int i = 0;i<15;i++){
										Thread.sleep(1000);
										hostValue = Utils.hostLevenUp.get(repeaterMac);	//1代表成功，0失败，其他的重发
										if(hostValue==1) {
											System.out.println("join host value is success:"+hostValue);
											count = 4;
											break;
										}
									}
									if(hostValue==1){	//结束包成功
										levelMessage = "远程升级成功";
										path = "/WEB-INF/page/util/levenUpSuccess.jsp";
										break;
									}else if(hostValue == 0){ 	//失败
										path = "/WEB-INF/page/util/levenUpError.jsp";
										break;
									}else{
										continue;
									}
								}
							}
						}else{//启动包发送失败
							continue;
						}
						break;
					}else{
						continue;
					}
				}
			}
			if(StringUtils.isBlank(path)){
				path = "/WEB-INF/page/util/levenUpError.jsp";
			}
		} catch (Exception e) {
			e.printStackTrace();
			path = "/WEB-INF/page/util/levenUpError.jsp";
		} finally{
			this.request.getSession().setAttribute("levelMessage", levelMessage);
		}
		try {
			this.request.getRequestDispatcher(path).forward(this.request, this.response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void toLevenUpFiles(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		
		String path = "";
		String levelMessage = "批量升级中！";
		List<RepeaterLevelUp> repeaterList=null;
		AreaDao ad = new AreaDaoImpl();
		System.out.println("<<<<<<<<<<<<<<"+levenFileName+"=============="+levenFileContentType+">>>>>>>>>>>>");
		System.out.println();
		if(levenFile == null){
			System.out.println(" levelFile is null==========lzo");
			path = "/WEB-INF/page/repeater/repeaterLeveUpInfo.jsp";
		}
		try {
			List<byte[]> leveList = Utils.getLevelUp(levenFile,512);
			String levelNum = this.request.getParameter("levelNum");
			String[] strs = this.request.getParameterValues("getMac");
	        String suffix = levenFileFileName.substring(levenFileFileName.lastIndexOf(".") + 1).toLowerCase();//获取后缀名
	        System.out.println(suffix);
	        
	        if(suffix.equals("bin")){
	        	//判断后缀名为bin升级文件
				for(int i = 0;i<strs.length;i++){
					new LeveUpRepeater(leveList, strs[i], levelNum, levenFile).start();
					Thread.sleep(2000);
				}
	        }else{
	        	levelMessage = "升级文件错误";
	        }
			repeaterList= ad.getParentLists(strs);
			if(StringUtils.isBlank(path)){
				path = "/WEB-INF/page/repeater/repeaterLeveUpInfo.jsp";
			}
		} catch (Exception e) {
			e.printStackTrace();
			path = "/WEB-INF/page/repaeter/repeaterLeveUpInfo.jsp";
		} finally{
			this.request.getSession().setAttribute("levelMessage", levelMessage);
			this.request.getSession().setAttribute("repeaterList", repeaterList);
		}
		try {
			this.request.getRequestDispatcher(path).forward(this.request, this.response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public File getLevenFile() {
		return levenFile;
	}

	public void setLevenFile(File levenFile) {
		this.levenFile = levenFile;
	}

	public String getLevenFileName() {
		return levenFileName;
	}

	public void setLevenFileName(String levenFileName) {
		this.levenFileName = levenFileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLevenFileContentType() {
		return levenFileContentType;
	}

	public void setLevenFileContentType(String levenFileContentType) {
		this.levenFileContentType = levenFileContentType;
	}

	public String getLevenFileFileName() {
		return levenFileFileName;
	}

	public void setLevenFileFileName(String levenFileFileName) {
		this.levenFileFileName = levenFileFileName;
	}
	
}
