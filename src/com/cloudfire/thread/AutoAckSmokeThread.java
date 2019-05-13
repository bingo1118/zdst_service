package com.cloudfire.thread;

import com.cloudfire.until.OneNetHttpMethod;

public class AutoAckSmokeThread extends Thread{
	public AutoAckSmokeThread() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		String url = "http://119.29.155.148:51091/fireSystem/uploadDevices.do";
		String oneNetString0 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001000400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString1 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001010400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString2 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001020400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString8 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001080400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetStringD ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"1100180100055032259609020001030300010D0400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString11 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001110400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString12 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001120400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString13 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001130400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		String oneNetString14 ="{\"msg\":{\"at\":1528195126678,\"type\":1,\"ds_id\":\"3200_0_5750\",\"value\":\"110018010005503225960902000103030001140400011205000160\",\"dev_id\":504075446},\"msg_signature\":\"OrBKVtHKd0Rq7X99BbORJA==\",\"nonce\":\"vac!UOgb\"}";
		int i = 0;
		while(true){
			if(i==2147483646){
				i=0;
			}
			if(i%10==0){	//–ƒÃ¯10∑÷÷”“ª¥Œ
				OneNetHttpMethod.postJson(url, oneNetString0);
			}else if(i%5==0){	//±®æØ
				OneNetHttpMethod.postJson(url, oneNetString1);
			}else if(i%6==0){	//µÕµÁ—π
				OneNetHttpMethod.postJson(url, oneNetString2);
			}else if(i<=0){	//∂‘¬Î
				OneNetHttpMethod.postJson(url, oneNetString8);
			}else if(i<=0){	//≤‚ ‘
				OneNetHttpMethod.postJson(url, oneNetStringD);
			}else if(i%9==0){	//0x11 —ÃŒÌ±®æØª÷∏¥
				OneNetHttpMethod.postJson(url, oneNetString11);
			}else if(i%11==0){	//0x12 µÕµÁ—πª÷∏¥
				OneNetHttpMethod.postJson(url, oneNetString12);
			}else if(i%13==0){	//0x13 ∑¿≤±®æØ
				OneNetHttpMethod.postJson(url, oneNetString13);
			}else if(i%17==0){	//0x14 ∑¿≤±®æØªÿ∏¥
				OneNetHttpMethod.postJson(url, oneNetString14);
			}
			i++;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
