package com.attestr.flowx.model.response.common;

import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public class StageInitData extends BaseData {
	private List<String> output;
	private Object next;
	private String number;
	private List<String> input;
	private String handler;
	private Map<String, Object> metadata;
	private Object assignment;
	private String id;
	private Map<String, String> state;
	private String type;
	private String nodeType;
	private boolean first;
	private Query query;

	public List<String> getOutput(){
		return output;
	}

	public Object getNext(){
		return next;
	}

	public String getNumber(){
		return number;
	}

	public List<String> getInput(){
		return input;
	}

	public String getHandler(){
		return handler;
	}

	public Map<String, Object> getMetadata(){
		return metadata;
	}

	public Object getAssignment(){
		return assignment;
	}

	public String getId(){
		return id;
	}

	public Map<String, String> getState(){
		return state;
	}

	public String getType(){
		return type;
	}

	public String getNodeType(){
		return nodeType;
	}

	public boolean isFirst(){
		return first;
	}

	public Query getQuery() {
		return query;
	}
}