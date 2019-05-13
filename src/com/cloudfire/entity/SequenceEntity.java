package com.cloudfire.entity;

public class SequenceEntity {
	static private volatile short seq=0;
	static private volatile SequenceEntity mEntity;
	private SequenceEntity(){
		
	}
	public static SequenceEntity getIntance(){
		if(mEntity==null){
			synchronized (SequenceEntity.class) {
				if(mEntity==null){
					mEntity=new SequenceEntity();
					return mEntity;
				}
			}
		}
		return mEntity;
	}
	
	public short getSeq(){
		seq++;
		return seq;
	}
}
