package com.cloudfire.server;

import java.nio.charset.Charset;

import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

public class MyCodecFactory extends TextLineCodecFactory{
	public MyCodecFactory(){
		super(Charset.forName("UTF-8"));
	}
}
