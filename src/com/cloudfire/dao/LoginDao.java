package com.cloudfire.dao;

import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.LoginHttpRsult;

public interface LoginDao {
	public LoginEntity login(String userId);

	public LoginEntity loginToYooSee(String userId, String pwd);

	public boolean addUser(String userId, int privilege, String name);

	public boolean addUser(String userId, int privilege, String name, String pwd, String salt);

	public LoginEntity login2(String userId, String psw);

	/**
	 * @author lzo
	 * @param appId
	 *            ���ڸ��ƽ���appId
	 * @param Alias
	 *            ���ڸ��ƽ��ı���
	 * @return boolean
	 */
	public boolean unBindAliasAll(String appId, String Alias, String cid);

	public boolean unBindAlias(String appId, String Alias, String cid);

	/**
	 * @author lzo
	 * @param appId���ڸ��ư�appid�Լ�������CID
	 * @param Alias
	 * @param cid
	 * @return
	 */
	public boolean bindAlias(String appId, String Alias, String cid);

	/**
	 * @author lzo
	 * @param cid���󶨸�����Ϣ���
	 * 
	 */

	public void savePushBindCid(String userid, String cid, int ifbind);

	public void updateUserAppId(String userId, int appId);

	public int getAppIdByUser(String userId);

	public int loginUser(String userId, String pwd);

	/**
	 * genju yonghuming panduan shifou yi denglu,1:yidenglu,0:wei denglu;
	 * 
	 * @param userId
	 *            yonghuming
	 * @param state
	 *            denglu zhuangtai
	 * @return
	 */
	public int updateLoginState(String userId, int state, String ipaddress);

	/**
	 * genju yonghumming huoqu yonghu shifou denglu zhuangtai.
	 * 
	 * @param userId
	 * @return
	 */
	public int getLoginState(String userId);

	/**
	 * panduan gai yong hu shi fou yi denglu,deng lu fanhui true,fou ze false;
	 * 
	 * @return
	 */
	public boolean charactUser(String userId);

	/**
	 * panduan gai yong hu shi fou yi denglu,deng lu fanhui true,fou ze false;
	 * 
	 * @return
	 */
	public boolean charactUserName(String username);

	/*
	 * @author lzw
	 * 
	 */
	public int elecMeterLogin(String user, String pwd);

	public int elecMeterAddUser(String user, String pwd);

	public int elecMeterSendEmail(String email);

	public int elecMeterCheckEmail(String serialCode, String email);

	/**
	 * �����û�Ȩ��
	 */
	public int updateUserInfo(String userId, String privilege);

	/**
	 * ���ڵ���easy-iot�û���¼��ȡaccessToKen ƽ̨��¼: �ӿ�˵����������Ӧ�õ��ô˽ӿڵ���EasyIoTƽ̨����ü�Ȩtoken��
	 * ��token��Ч��Ϊ24Сʱ��24Сʱ���������µ�¼�Ի�ȡ�µ�token��
	 * �����token��Ч���ڳɹ�������һƽ̨�ӿڣ����ˢ����Ч�ڣ����ӵ��ýӿ�ʱ�俪ʼ�� 24Сʱ��Ч�ڡ�
	 */
	public String getEaseIotAccessToKen(String serverId, String password);

	/**
	 * ���ڵ���easy-iot�û���¼��ȡaccessToKen ƽ̨��¼: �ӿ�˵����������Ӧ�õ��ô˽ӿڵ���EasyIoTƽ̨����ü�Ȩtoken��
	 * ��token��Ч��Ϊ24Сʱ��24Сʱ���������µ�¼�Ի�ȡ�µ�token��
	 * �����token��Ч���ڳɹ�������һƽ̨�ӿڣ����ˢ����Ч�ڣ����ӵ��ýӿ�ʱ�俪ʼ�� 24Сʱ��Ч�ڡ�
	 */
	public String getEaseIotAccessToKen(String serverId, String password, String ioturl);

	public String getNanJinToKen(String serverId, String password);

	public int getCompanyByUser(String userId);

	public String getSaltByUserId(String userId);

	public boolean unBindIOS(String alias);

	public LoginHttpRsult verify(String userName,String pwd,String privId);

}
