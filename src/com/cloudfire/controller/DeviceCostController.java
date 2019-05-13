package com.cloudfire.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.FreeDao;
import com.cloudfire.dao.InfoManagerDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FreeDaoImpl;
import com.cloudfire.dao.impl.InfoManagerDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.DeviceCostEntity;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.PayRecords;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.query.SmokeQuery;
import com.cloudfire.myservice.SmokeService;
import com.cloudfire.myservice.impl.SmokeServiceImpl;
import com.cloudfire.until.AlipayConfig;
import com.cloudfire.until.Constant;
import com.cloudfire.until.PaymentUtil;

import common.page.Pagination;
/**
 * 
 * @author zzh
 *2018-08-20
 */
@Controller
public class DeviceCostController {
	private final static Log log = LogFactory.getLog(DeviceCostController.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	private SmartControlDao mSmartControlDao;
	private SmokeDao smokeDao;
	private FreeDao freeDao;
	
/*	@RequestMapping(value = "/outOfMoney.do",method=RequestMethod.GET)
	public ModelAndView outOfMoney(HttpServletRequest request,Integer pageNo,SmokeQuery query,ModelMap model){
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		String deviceType=request.getParameter("deviceType");
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			request.setAttribute("deviceType", query.getDeviceType());
		}
		String currentId = (String) request.getSession().getAttribute("userId");
		
		//如果用户未登陆，则跳转到登陆页面
		if(StringUtils.isBlank(currentId)) {
			modelAndView.setViewName("/WEB-INF/page/login/login"); 
		}else{
			mLoginDao = new LoginDaoImpl();
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			String privilege = mLoginEntity.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			if(areaIds!=null&&areaIds.size()>0){
				Pagination pagination = service.selectSmokeListByDeviceType(areaIds, query);
				pagination.pageView("/fireSystem/outOfMoney.do", params.toString());
				Map<String,List> modelMap = new HashMap<String,List>();
				model.addAttribute("pagination", pagination);
				modelAndView.addObject("privilege",privilege);
				modelAndView.addAllObjects(modelMap);
			}
		}
		modelAndView.setViewName("/WEB-INF/page/main/deviceCost_list_items");
		return modelAndView;
	}
	
	
	@RequestMapping(value = "/enoughMoney.do",method=RequestMethod.GET)
	public ModelAndView enoughMoney(HttpServletRequest request,Integer pageNo,SmokeQuery query,ModelMap model){
		service = new SmokeServiceImpl();
		mSmartControlDao = new SmartControlDaoImpl();
		if (pageNo == null) {
			pageNo = Pagination.cpn(pageNo);
		}
		query.setPageNo(pageNo);
		query.setPageSize(Constant.PAGESIZE);
		ModelAndView modelAndView = new ModelAndView();
		String deviceType=request.getParameter("deviceType");
		StringBuilder params = new StringBuilder();
		if (StringUtils.isNotBlank(deviceType)) {
			query.setDeviceType(deviceType);
			params.append("&deviceType=").append(query.getDeviceType());
			request.setAttribute("deviceType", query.getDeviceType());
		}
		String currentId = (String) request.getSession().getAttribute("userId");
		
		//如果用户未登陆，则跳转到登陆页面
		if(StringUtils.isBlank(currentId)) {
			modelAndView.setViewName("/WEB-INF/page/login/login"); 
		}else{
			mLoginDao = new LoginDaoImpl();
			InfoManagerDao infoDao = new InfoManagerDaoImpl();
			LoginEntity mLoginEntity = mLoginDao.login(currentId);
			String privilege = mLoginEntity.getPrivilege()+"";
			mAreaDao = new AreaDaoImpl();
			List<String> areaIds = mAreaDao.getAreaStr(currentId, privilege);
			if(areaIds!=null&&areaIds.size()>0){
				Pagination pagination = service.selectSmokeListByDeviceType2(areaIds, query);
				pagination.pageView("/fireSystem/outOfMoney.do", params.toString());
				Map<String,List> modelMap = new HashMap<String,List>();
				model.addAttribute("pagination", pagination);
				modelAndView.addObject("privilege",privilege);
				modelAndView.addAllObjects(modelMap);
			}
		}
		modelAndView.setViewName("/WEB-INF/page/main/deviceCost_list_items");
		return modelAndView;
	}
	*/
	@RequestMapping(value="addMoneyById.do",method=RequestMethod.GET)
	public ModelAndView addMoneyById(HttpServletRequest request){
		mSmartControlDao=new SmartControlDaoImpl();
		String mac=request.getParameter("smokeMac");
		DeviceCostEntity dce=new DeviceCostEntity();
		if(mac!=null){
			//根据mac查找出该设备的基本信息和设备余额
			dce=mSmartControlDao.selectSmokeCost(mac);
		}
		ModelAndView model=new ModelAndView();
		model.addObject("dce",dce);
		model.setViewName("/WEB-INF/page/main/addMoney");
		return model;
	}
	
	@RequestMapping(value="bankpay",method=RequestMethod.POST)
	public ModelAndView bankpay(HttpServletRequest req,HttpServletResponse resp){
		ModelAndView mv =new ModelAndView();
		mv.setViewName("/WEB-INF/page/main/bankpay");
		return mv;
	}
	/**
	 * 网银支付方法，选择任意银行后点击下一步跳转的方法
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping(value="OrderPay.do",method=RequestMethod.POST)
	public void pay(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		resp.setCharacterEncoding("GBK");
		req.setCharacterEncoding("utf-8");
		
		String money=req.getParameter("money");
		String name=req.getParameter("name");
		String mac=req.getParameter("mac");
		System.out.println(money+name+"==============");
		String p0_Cmd = "Buy";//业务类型，固定值Buy
		String p1_MerId = "10001126856";//商号编码，在易宝的唯一标识
		String p2_Order = "";//订单编码
		String p3_Amt = "0.01";//支付金额
		String p4_Cur = "CNY";//交易币种，固定值CNY
		String p5_Pid = mac;//商品名称
		String p6_Pcat = "yangan";//商品种类
		String p7_Pdesc = "";//商品描述
		String p8_Url = "http://localhost:8080/fireSystem/payNext.do";//在支付成功后，易宝会访问这个地址。
		//String p8_Url="http://47.94.250.113:51091/zhiminkeji/payNext.do"
		String p9_SAF = "";//送货地址
		String pa_MP = "";//扩展信息
		String pd_FrpId = req.getParameter("yh");//支付通道
		String pr_NeedResponse = "1";//应答机制，固定值1
		String keyValue = "69cl522AV6q613Ii4W6u8K6XuW8vM1N6bFgyv769220IuYe9u37N4y7rI4Pl";
		/*
		 * 2. 计算hmac
		 * 需要13个参数
		 * 需要keyValue
		 * 需要加密算法
		 */
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		StringBuilder sb = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
		sb.append("?").append("p0_Cmd=").append(p0_Cmd);
		sb.append("&").append("p1_MerId=").append(p1_MerId);
		sb.append("&").append("p2_Order=").append(p2_Order);
		sb.append("&").append("p3_Amt=").append(p3_Amt);
		sb.append("&").append("p4_Cur=").append(p4_Cur);
		sb.append("&").append("p5_Pid=").append(p5_Pid);
		sb.append("&").append("p6_Pcat=").append(p6_Pcat);
		sb.append("&").append("p7_Pdesc=").append(p7_Pdesc);
		sb.append("&").append("p8_Url=").append(p8_Url);
		sb.append("&").append("p9_SAF=").append(p9_SAF);
		sb.append("&").append("pa_MP=").append(pa_MP);
		sb.append("&").append("pd_FrpId=").append(pd_FrpId);
		sb.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
		sb.append("&").append("hmac=").append(hmac);
		String succeedStr = new String(sb.toString().getBytes("iso8859-1"),"UTF-8");
		try {
			resp.sendRedirect(succeedStr.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 回馈方法
	 * 当支付成功时，易宝会访问这里
	 * 用两种方法访问：
	 * 1. 引导用户的浏览器重定向(如果用户关闭了浏览器，就不能访问这里了)
	 * 2. 易宝的服务器会使用点对点通讯的方法访问这个方法。（必须回馈success，不然易宝服务器会一直调用这个方法）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value="payNext.do")
	public String payNext(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取12个参数
		 */
		String p1_MerId = req.getParameter("p1_MerId");
		String r0_Cmd = req.getParameter("r0_Cmd");
		String r1_Code = req.getParameter("r1_Code");
		String r2_TrxId = req.getParameter("r2_TrxId");
		String r3_Amt = req.getParameter("r3_Amt");
		String r4_Cur = req.getParameter("r4_Cur");
		String r5_Pid = req.getParameter("r5_Pid");
		String r6_Order = req.getParameter("r6_Order");
		String r7_Uid = req.getParameter("r7_Uid");
		String r8_MP = req.getParameter("r8_MP");
		String r9_BType = req.getParameter("r9_BType");
		String hmac = req.getParameter("hmac");
		
		String keyValue = "69cl522AV6q613Ii4W6u8K6XuW8vM1N6bFgyv769220IuYe9u37N4y7rI4Pl";
		/*
		 * 3. 调用PaymentUtil的校验方法来校验调用者的身份
		 *   >如果校验失败：保存错误信息，转发到msg.jsp
		 *   >如果校验通过：
		 *     * 判断访问的方法是重定向还是点对点，如果要是重定向
		 *     修改订单状态，保存成功信息，转发到msg.jsp
		 *     * 如果是点对点：修改订单状态，返回success
		 */
		boolean bool = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, r2_TrxId,
				r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType,
				keyValue);
		if (!bool) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "无效的签名，支付失败！");
			//返回页面
			return "msg";
		}
		if (r1_Code.equals("1")) {
			//修改设备余额和状态以及生成缴费记录
/*			r6_Order//订单编号
			p3_Amt//支付金额
			p5_Pid//设备mac
*/			mSmartControlDao=new SmartControlDaoImpl();
		   //根据mac查找出该设备信息，在设备金额基础上加上r3_Amt，再查看该设备之前状态是如何，停机则改为正常
           int rs= mSmartControlDao.updateCostByMac(r5_Pid,Double.parseDouble(r3_Amt));
           if(rs>0){
        	   
           }
           //两种不同跳转成功界面的判断
           //1.重定向   修改订单状态，保存成功信息，转发到msg.jsp
           //2.点对点   修改订单状态，返回success
			if (r9_BType.equals("1")) {
				req.setAttribute("code", "success");
				req.setAttribute("msg", "恭喜，支付成功！");
				return "msg";
			}else if (r9_BType.equals("2")) {
				resp.getWriter().print("success");
			}
		}
				return null;
	}
	
	
	/**
	 * 支付宝支付接口,点击页面支付跳转到该方法
	 * @param req
	 * @param resp
	 * @throws UnsupportedEncodingException
	 * @throws AlipayApiException
	 */
	@RequestMapping(value="Alipay.do",method=RequestMethod.POST)
	public void goAlipay(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException, AlipayApiException{
		String orderId=req.getParameter("orderId");
		String orderName=req.getParameter("orderName");
		String orderMoney = req.getParameter("orderMoney");
		
		//生成订单记录。未付款状态
		PayRecords p = new PayRecords();
		p.setMac(orderName);
		p.setOrderId(orderId);//订单号（唯一的）
		p.setPayMoney(Double.parseDouble(orderMoney));
		p.setPayName((String)req.getSession().getAttribute("userId"));
		p.setPayStatus("未支付");
		p.setPayTime(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date()));
		freeDao = new FreeDaoImpl();
		freeDao.addPayRecords(p);
		//	String orderMessage = req.getParameter("orderMessage");
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
		
		//设置请求参数
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		String return_url = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/fireSystem/return_url.do";
		String notify_url = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/fireSystem/notify_url.do";
		alipayRequest.setReturnUrl(return_url);
		alipayRequest.setNotifyUrl(notify_url);
		
		//商户订单号，商户网站订单系统中唯一订单号，必填
		String out_trade_no = new String(orderId.getBytes("ISO-8859-1"),"UTF-8");
		//付款金额，必填
		String total_amount = new String(orderMoney.getBytes("ISO-8859-1"),"UTF-8");
		//订单名称，必填
		String subject = new String(orderName.getBytes("ISO-8859-1"),"UTF-8");
		//商品描述，可空
		String body = "";
	/*	if(orderMessage!=null||orderMessage!=""){
			 body= new String(orderMessage.getBytes("ISO-8859-1"),"UTF-8");
		}*/
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
				+ "\"total_amount\":\""+ total_amount +"\"," 
				+ "\"subject\":\""+ subject +"\"," 
				+ "\"body\":\""+ body +"\"," 
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		
		//请求
		String result = alipayClient.pageExecute(alipayRequest).getBody();//调用SDK生成表单
		
		try {
			resp.setContentType("text/html;charset="+AlipayConfig.charset);
			resp.getWriter().write(result);//直接将完整的表单html输出到页面
			resp.getWriter().flush();
			resp.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  1、同步返回接口，作为参数传递给支付宝
	 *	2、用户付款成功后，从支付宝跳转到这个页面
	 *  3、在这个页面中加入相关业务处理，比如更新记录，标记付款成功信息。
	 *	4、需要对支付宝传递过来的签名进行认证。
	 *	5、用来展现成功付款信息给前台付款用户。
	 *	6、支付宝那边只返回一次。
	 * 	7、由于用户在付款完成后，直接关闭付款页面，不跳转到return_url的页面，会导致return_url的相关业务处理不了。
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 * @throws AlipayApiException
	 */
	@RequestMapping(value="return_url.do",method=RequestMethod.GET)
	public String returnUrl(HttpServletRequest req,HttpServletResponse resp) throws IOException, AlipayApiException{
		//获取支付宝GET过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map<String,String[]> requestParams = req.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

		if(signVerified) {
			//商户订单号
			String out_trade_no = new String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//支付宝交易号
			String trade_no = new String(req.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//付款金额
			String total_amount = new String(req.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
			
			req.setAttribute("out_trade_no", out_trade_no);
            req.setAttribute("trade_no", trade_no);
            req.setAttribute("total_amount", total_amount);
            log.debug("订单处理：系统订单号" + out_trade_no + "支付宝交易号：" + trade_no);
            
			//1.先根据订单号查询数据库中是否存在该订单记录
            freeDao = new FreeDaoImpl();
            PayRecords p = freeDao.queryRecordsByOrderId(out_trade_no);
            if(p!=null){
            	//根据订单修改支付状态为已支付
            	p.setPayStatus("已支付");
            	freeDao.updateRecords(p);
            	//根据记录中的设备mac修改设备的余额,先根据mac查找先前设备的余额
            	Double cost = freeDao.queryCostByMac(p.getMac());
            	freeDao.updateCostByMac(p.getMac(), p.getPayMoney()+cost);
            }
			//resp.getWriter().write("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
            req.setAttribute("code", "success");
			req.setAttribute("msg", "恭喜，支付成功！");
		}
		return "msg";
	}
	
	/**
	 *  1、异步通知接口，作为参数传递给支付宝。
	 *	2、如果不传递，则不通知。
	 *	3、相关业务逻辑应该和return_url中相同。
	 *	4、返回字符串"success"或者"fail"，不能带有任何HTML信息。
	 *	5、付款成功后就通知一次，如果不成功，1分钟、3分钟、10分钟、半个小时。。。后再通知，直到返回success。
	 *	6、过期时间是48小时，如果48小时内都通知不成功，那么就不再通知。
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 * @throws AlipayApiException
	 */
	@RequestMapping(value="notify_url.do",method=RequestMethod.POST)
	public void notifyUrl(HttpServletRequest req,HttpServletResponse resp) throws IOException, AlipayApiException{
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map<String,String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

		/* 实际验证过程建议商户务必添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		4、验证app_id是否为该商户本身。
		*/
		if(signVerified) {//验证成功
			//商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
			
			if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS") ){
				
				resp.getWriter().write("success");
			}else{
				resp.getWriter().write("fail");
			}
			
		}else {//验证失败
			resp.getWriter().write("fail");
			//调试用，写文本函数记录程序运行情况是否正常
			String sWord = AlipaySignature.getSignCheckContentV1(params);
			AlipayConfig.logResult(sWord);
		}
		resp.getWriter().flush();
		resp.getWriter().close();
	}
	/**
	 * 支付宝订单查询接口
	 * @param req
	 * @param resp
	 * @throws AlipayApiException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="queryOrderAlipay.do")
	public void queryOrderAlipay(HttpServletRequest req,HttpServletResponse resp) throws AlipayApiException, UnsupportedEncodingException{
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
		
		//设置请求参数
		AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
		
		//商户订单号，商户网站订单系统中唯一订单号
		String out_trade_no = new String(request.getParameter("WIDTQout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//支付宝交易号
		String trade_no = new String(request.getParameter("WIDTQtrade_no").getBytes("ISO-8859-1"),"UTF-8");
		//请二选一设置
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","+"\"trade_no\":\""+ trade_no +"\"}");
		
		//请求
		String result = alipayClient.execute(alipayRequest).getBody();
		
		//输出
		resp.setContentType("text/html;charset="+AlipayConfig.charset);
		try {
			resp.getWriter().write(result);//直接将完整的表单html输出到页面
			resp.getWriter().flush();
			resp.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="showCostAlarm.do",method=RequestMethod.GET)
	public ModelAndView shwoCostAlarm(HttpServletRequest request){
		ModelAndView modelAndView = new ModelAndView();
		@SuppressWarnings("unchecked")
		List<String> list=(List<String>) request.getAttribute("list2");//获取欠费设备的mac值
		smokeDao=new SmokeDaoImpl();
		List<SmokeBean> smokeBean=new ArrayList<SmokeBean>();
		if(list!=null){
			for (int i = 0; i < list.size(); i++) {
				SmokeBean s=smokeDao.getSmokeByMac(list.get(i));//获取欠费设备的基本信息
				smokeBean.add(s);
			}
		}
		modelAndView.addObject("smokeBean",smokeBean);
		modelAndView.setViewName("/newindex");
		return modelAndView;
	}
	
	@RequestMapping(value="showAllSmokeToUser.do",method=RequestMethod.GET)
	public void showAllSmokeToUser(){
		mSmartControlDao=new SmartControlDaoImpl();
		//ModelAndView modelAndView = new ModelAndView();
		//查找出所有费用正常的设备即cost>0的设备
		List<DeviceCostEntity> list=mSmartControlDao.getAllSmoke();
		List<String> list2=new ArrayList<String>();
		int rs=0;
		for (int i = 0; i < list.size(); i++) {
			String mac=list.get(i).getRepeaterMac();
			Double cost=list.get(i).getCost();
			Double finallyCost=cost-100;
			//更新数据库中设备的费用余额
			rs+=mSmartControlDao.updateCostByMac(mac,finallyCost);
			if(finallyCost<=0){
				list2.add(mac);
			}
			log.debug("成功更新了"+rs+"条数据");
		}
		
		smokeDao=new SmokeDaoImpl();
		List<SmokeBean> smokeBeanList=new ArrayList<SmokeBean>();
		if(list2!=null){
			for (int i = 0; i < list2.size(); i++) {
				SmokeBean s=smokeDao.getSmokeByMac(list2.get(i));//获取欠费设备的基本信息
				smokeBeanList.add(s);
			}
		}
		/*modelAndView.addObject("smokeBean",smokeBean);*/
		JSONObject jObject = new JSONObject(smokeBeanList);
		try {
			response.getWriter().write(jObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
