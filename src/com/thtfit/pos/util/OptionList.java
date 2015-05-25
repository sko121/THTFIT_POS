package com.thtfit.pos.util;

import java.util.ArrayList;
import java.util.List;

public class OptionList {

	private List<String> settingList;
	private List<String> searchList;
	private List<String> manageList;
	private List<String> reportList;
	private List<String> list;



	public final static int TYPE_SETTING = 1;
	public final static int TYPE_SEARCH = 2;
	public final static int TYPE_MANAGE = 3;
	public final static int TYPE_REPORT = 4;

	public void parse(int type) {
		switch (type) {
		case TYPE_SETTING:
			settingList = new ArrayList<String>();
			settingList.add("常规设置");
			settingList.add("硬件设置");
			settingList.add("发票信息");
			settingList.add("返回首页");
			setList(settingList);
			break;
		case TYPE_SEARCH:
			searchList = new ArrayList<String>();
			searchList.add("名称查找");
			searchList.add("编号查找");
			searchList.add("价格查找");
			searchList.add("日期查找");
			setList(searchList);
			break;
		case TYPE_MANAGE:
			manageList = new ArrayList<String>();
			manageList.add("帐号管理");
			manageList.add("添加店员");
			manageList.add("新增产品");
			manageList.add("新增类别");
			manageList.add("库存管理");
			setList(manageList);
			break;
		case TYPE_REPORT:
			reportList = new ArrayList<String>();
			reportList.add("销售趋势");
			reportList.add("进销");
			reportList.add("利润");
			setList(reportList);
			break;
		default:
			break;
		}

	}

	public List<String> getSettingList() {
		return settingList;
	}

	public void setSettingList(List<String> settingList) {
		this.settingList = settingList;
	}

	public List<String> getSearchList() {
		return searchList;
	}

	public void setSearchList(List<String> searchList) {
		this.searchList = searchList;
	}

	public List<String> getManageList() {
		return manageList;
	}

	public void setManageList(List<String> manageList) {
		this.manageList = manageList;
	}

	public List<String> getReportList() {
		return reportList;
	}

	public void setReportList(List<String> reportList) {
		this.reportList = reportList;
	}
	
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

}
