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
		
		//����û�δ��½������ת����½ҳ��
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
		
		//����û�δ��½������ת����½ҳ��
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
			//����mac���ҳ����豸�Ļ�����Ϣ���豸���
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
	 * ����֧��������ѡ���������к�����һ����ת�ķ���
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
		String p0_Cmd = "Buy";//ҵ�����ͣ��̶�ֵBuy
		String p1_MerId = "10001126856";//�̺ű��룬���ױ���Ψһ��ʶ
		String p2_Order = "";//��������
		String p3_Amt = "0.01";//֧�����
		String p4_Cur = "CNY";//���ױ��֣��̶�ֵCNY
		String p5_Pid = mac;//��Ʒ����
		String p6_Pcat = "yangan";//��Ʒ����
		String p7_Pdesc = "";//��Ʒ����
		String p8_Url = "http://localhost:8080/fireSystem/payNext.do";//��֧���ɹ����ױ�����������ַ��
		//String p8_Url="http://47.94.250.113:51091/zhiminkeji/payNext.do"
		String p9_SAF = "";//�ͻ���ַ
		String pa_MP = "";//��չ��Ϣ
		String pd_FrpId = req.getParameter("yh");//֧��ͨ��
		String pr_NeedResponse = "1";//Ӧ����ƣ��̶�ֵ1
		String keyValue = "69cl522AV6q613Ii4W6u8K6XuW8vM1N6bFgyv769220IuYe9u37N4y7rI4Pl";
		/*
		 * 2. ����hmac
		 * ��Ҫ13������
		 * ��ҪkeyValue
		 * ��Ҫ�����㷨
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
	 * ��������
	 * ��֧���ɹ�ʱ���ױ����������
	 * �����ַ������ʣ�
	 * 1. �����û���������ض���(����û��ر�����������Ͳ��ܷ���������)
	 * 2. �ױ��ķ�������ʹ�õ�Ե�ͨѶ�ķ�������������������������success����Ȼ�ױ���������һֱ�������������
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
		 * 1. ��ȡ12������
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
		 * 3. ����PaymentUtil��У�鷽����У������ߵ����
		 *   >���У��ʧ�ܣ����������Ϣ��ת����msg.jsp
		 *   >���У��ͨ����
		 *     * �жϷ��ʵķ������ض����ǵ�Ե㣬���Ҫ���ض���
		 *     �޸Ķ���״̬������ɹ���Ϣ��ת����msg.jsp
		 *     * ����ǵ�Ե㣺�޸Ķ���״̬������success
		 */
		boolean bool = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, r2_TrxId,
				r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType,
				keyValue);
		if (!bool) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "��Ч��ǩ����֧��ʧ�ܣ�");
			//����ҳ��
			return "msg";
		}
		if (r1_Code.equals("1")) {
			//�޸��豸����״̬�Լ����ɽɷѼ�¼
/*			r6_Order//�������
			p3_Amt//֧�����
			p5_Pid//�豸mac
*/			mSmartControlDao=new SmartControlDaoImpl();
		   //����mac���ҳ����豸��Ϣ�����豸�������ϼ���r3_Amt���ٲ鿴���豸֮ǰ״̬����Σ�ͣ�����Ϊ����
           int rs= mSmartControlDao.updateCostByMac(r5_Pid,Double.parseDouble(r3_Amt));
           if(rs>0){
        	   
           }
           //���ֲ�ͬ��ת�ɹ�������ж�
           //1.�ض���   �޸Ķ���״̬������ɹ���Ϣ��ת����msg.jsp
           //2.��Ե�   �޸Ķ���״̬������success
			if (r9_BType.equals("1")) {
				req.setAttribute("code", "success");
				req.setAttribute("msg", "��ϲ��֧���ɹ���");
				return "msg";
			}else if (r9_BType.equals("2")) {
				resp.getWriter().print("success");
			}
		}
				return null;
	}
	
	
	/**
	 * ֧����֧���ӿ�,���ҳ��֧����ת���÷���
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
		
		//���ɶ�����¼��δ����״̬
		PayRecords p = new PayRecords();
		p.setMac(orderName);
		p.setOrderId(orderId);//�����ţ�Ψһ�ģ�
		p.setPayMoney(Double.parseDouble(orderMoney));
		p.setPayName((String)req.getSession().getAttribute("userId"));
		p.setPayStatus("δ֧��");
		p.setPayTime(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date()));
		freeDao = new FreeDaoImpl();
		freeDao.addPayRecords(p);
		//	String orderMessage = req.getParameter("orderMessage");
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
		
		//�����������
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		String return_url = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/fireSystem/return_url.do";
		String notify_url = req.getScheme()+"://"+req.getServerName()+":"+req.getServerPort()+"/fireSystem/notify_url.do";
		alipayRequest.setReturnUrl(return_url);
		alipayRequest.setNotifyUrl(notify_url);
		
		//�̻������ţ��̻���վ����ϵͳ��Ψһ�����ţ�����
		String out_trade_no = new String(orderId.getBytes("ISO-8859-1"),"UTF-8");
		//���������
		String total_amount = new String(orderMoney.getBytes("ISO-8859-1"),"UTF-8");
		//�������ƣ�����
		String subject = new String(orderName.getBytes("ISO-8859-1"),"UTF-8");
		//��Ʒ�������ɿ�
		String body = "";
	/*	if(orderMessage!=null||orderMessage!=""){
			 body= new String(orderMessage.getBytes("ISO-8859-1"),"UTF-8");
		}*/
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
				+ "\"total_amount\":\""+ total_amount +"\"," 
				+ "\"subject\":\""+ subject +"\"," 
				+ "\"body\":\""+ body +"\"," 
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		
		//����
		String result = alipayClient.pageExecute(alipayRequest).getBody();//����SDK���ɱ�
		
		try {
			resp.setContentType("text/html;charset="+AlipayConfig.charset);
			resp.getWriter().write(result);//ֱ�ӽ������ı�html�����ҳ��
			resp.getWriter().flush();
			resp.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  1��ͬ�����ؽӿڣ���Ϊ�������ݸ�֧����
	 *	2���û�����ɹ��󣬴�֧������ת�����ҳ��
	 *  3�������ҳ���м������ҵ����������¼�¼����Ǹ���ɹ���Ϣ��
	 *	4����Ҫ��֧�������ݹ�����ǩ��������֤��
	 *	5������չ�ֳɹ�������Ϣ��ǰ̨�����û���
	 *	6��֧�����Ǳ�ֻ����һ�Ρ�
	 * 	7�������û��ڸ�����ɺ�ֱ�ӹرո���ҳ�棬����ת��return_url��ҳ�棬�ᵼ��return_url�����ҵ�����ˡ�
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 * @throws AlipayApiException
	 */
	@RequestMapping(value="return_url.do",method=RequestMethod.GET)
	public String returnUrl(HttpServletRequest req,HttpServletResponse resp) throws IOException, AlipayApiException{
		//��ȡ֧����GET����������Ϣ
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
			//����������δ����ڳ�������ʱʹ��
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //����SDK��֤ǩ��

		if(signVerified) {
			//�̻�������
			String out_trade_no = new String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//֧�������׺�
			String trade_no = new String(req.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//������
			String total_amount = new String(req.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
			
			req.setAttribute("out_trade_no", out_trade_no);
            req.setAttribute("trade_no", trade_no);
            req.setAttribute("total_amount", total_amount);
            log.debug("��������ϵͳ������" + out_trade_no + "֧�������׺ţ�" + trade_no);
            
			//1.�ȸ��ݶ����Ų�ѯ���ݿ����Ƿ���ڸö�����¼
            freeDao = new FreeDaoImpl();
            PayRecords p = freeDao.queryRecordsByOrderId(out_trade_no);
            if(p!=null){
            	//���ݶ����޸�֧��״̬Ϊ��֧��
            	p.setPayStatus("��֧��");
            	freeDao.updateRecords(p);
            	//���ݼ�¼�е��豸mac�޸��豸�����,�ȸ���mac������ǰ�豸�����
            	Double cost = freeDao.queryCostByMac(p.getMac());
            	freeDao.updateCostByMac(p.getMac(), p.getPayMoney()+cost);
            }
			//resp.getWriter().write("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
            req.setAttribute("code", "success");
			req.setAttribute("msg", "��ϲ��֧���ɹ���");
		}
		return "msg";
	}
	
	/**
	 *  1���첽֪ͨ�ӿڣ���Ϊ�������ݸ�֧������
	 *	2����������ݣ���֪ͨ��
	 *	3�����ҵ���߼�Ӧ�ú�return_url����ͬ��
	 *	4�������ַ���"success"����"fail"�����ܴ����κ�HTML��Ϣ��
	 *	5������ɹ����֪ͨһ�Σ�������ɹ���1���ӡ�3���ӡ�10���ӡ����Сʱ����������֪ͨ��ֱ������success��
	 *	6������ʱ����48Сʱ�����48Сʱ�ڶ�֪ͨ���ɹ�����ô�Ͳ���֪ͨ��
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 * @throws AlipayApiException
	 */
	@RequestMapping(value="notify_url.do",method=RequestMethod.POST)
	public void notifyUrl(HttpServletRequest req,HttpServletResponse resp) throws IOException, AlipayApiException{
		//��ȡ֧����POST����������Ϣ
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
			//����������δ����ڳ�������ʱʹ��
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		
		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //����SDK��֤ǩ��

		/* ʵ����֤���̽����̻�����������У�飺
		1����Ҫ��֤��֪ͨ�����е�out_trade_no�Ƿ�Ϊ�̻�ϵͳ�д����Ķ����ţ�
		2���ж�total_amount�Ƿ�ȷʵΪ�ö�����ʵ�ʽ����̻���������ʱ�Ľ���
		3��У��֪ͨ�е�seller_id������seller_email) �Ƿ�Ϊout_trade_no��ʵ��ݵĶ�Ӧ�Ĳ��������е�ʱ��һ���̻������ж��seller_id/seller_email��
		4����֤app_id�Ƿ�Ϊ���̻�����
		*/
		if(signVerified) {//��֤�ɹ�
			//�̻�������
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//֧�������׺�
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		
			//����״̬
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
			
			if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS") ){
				
				resp.getWriter().write("success");
			}else{
				resp.getWriter().write("fail");
			}
			
		}else {//��֤ʧ��
			resp.getWriter().write("fail");
			//�����ã�д�ı�������¼������������Ƿ�����
			String sWord = AlipaySignature.getSignCheckContentV1(params);
			AlipayConfig.logResult(sWord);
		}
		resp.getWriter().flush();
		resp.getWriter().close();
	}
	/**
	 * ֧����������ѯ�ӿ�
	 * @param req
	 * @param resp
	 * @throws AlipayApiException
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value="queryOrderAlipay.do")
	public void queryOrderAlipay(HttpServletRequest req,HttpServletResponse resp) throws AlipayApiException, UnsupportedEncodingException{
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
		
		//�����������
		AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
		
		//�̻������ţ��̻���վ����ϵͳ��Ψһ������
		String out_trade_no = new String(request.getParameter("WIDTQout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//֧�������׺�
		String trade_no = new String(request.getParameter("WIDTQtrade_no").getBytes("ISO-8859-1"),"UTF-8");
		//���ѡһ����
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","+"\"trade_no\":\""+ trade_no +"\"}");
		
		//����
		String result = alipayClient.execute(alipayRequest).getBody();
		
		//���
		resp.setContentType("text/html;charset="+AlipayConfig.charset);
		try {
			resp.getWriter().write(result);//ֱ�ӽ������ı�html�����ҳ��
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
		List<String> list=(List<String>) request.getAttribute("list2");//��ȡǷ���豸��macֵ
		smokeDao=new SmokeDaoImpl();
		List<SmokeBean> smokeBean=new ArrayList<SmokeBean>();
		if(list!=null){
			for (int i = 0; i < list.size(); i++) {
				SmokeBean s=smokeDao.getSmokeByMac(list.get(i));//��ȡǷ���豸�Ļ�����Ϣ
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
		//���ҳ����з����������豸��cost>0���豸
		List<DeviceCostEntity> list=mSmartControlDao.getAllSmoke();
		List<String> list2=new ArrayList<String>();
		int rs=0;
		for (int i = 0; i < list.size(); i++) {
			String mac=list.get(i).getRepeaterMac();
			Double cost=list.get(i).getCost();
			Double finallyCost=cost-100;
			//�������ݿ����豸�ķ������
			rs+=mSmartControlDao.updateCostByMac(mac,finallyCost);
			if(finallyCost<=0){
				list2.add(mac);
			}
			log.debug("�ɹ�������"+rs+"������");
		}
		
		smokeDao=new SmokeDaoImpl();
		List<SmokeBean> smokeBeanList=new ArrayList<SmokeBean>();
		if(list2!=null){
			for (int i = 0; i < list2.size(); i++) {
				SmokeBean s=smokeDao.getSmokeByMac(list2.get(i));//��ȡǷ���豸�Ļ�����Ϣ
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
