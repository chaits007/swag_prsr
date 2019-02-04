package com.chaitanya.swag_prsr;

import java.util.ArrayList;
import java.util.List;

public class ParsedInfo {
	private String path;
	private String method;
	private String Summary;
	private String OperationId;
	private List <ParmInfo> parmList = new ArrayList<ParmInfo>();

	public void setParm(String type, String name, String dataType, boolean required, String parent, String parentType) {
		ParmInfo parmInfo = new ParmInfo();
		parmInfo.parmType = type;
		parmInfo.parmName = name;
		parmInfo.parmDataType = dataType;
		parmInfo.parent = parent;
		parmInfo.required = required;
		parmInfo.parentType = parentType;

		parmList.add(parmInfo);
	}
	
	public void setParm(String type, String name, String dataType, boolean required) {
		ParmInfo parmInfo = new ParmInfo();
		parmInfo.parmType = type;
		parmInfo.parmName = name;
		parmInfo.parmDataType = dataType;
		parmInfo.required = required;
		parmInfo.parent = "";
		parmInfo.parentType = "";

		parmList.add(parmInfo);
	}
	
	public void writeParsedInfoToExcel() {
		ExcelHandler excelHandler = new ExcelHandler();
		excelHandler.createExcelSheet(OperationId);
		
		writeParsedInfo(excelHandler, "header");
		writeParsedInfo(excelHandler, "path");
		writeParsedInfo(excelHandler, "query");
		writeParsedInfo(excelHandler, "body");		
		excelHandler.writeToFile();
	}
	
	private void writeParsedInfo(ExcelHandler excelHandler, String type) {
		for (ParmInfo parmInfo : parmList) {
			if (parmInfo.parmType == type) {
				excelHandler.writeHeader(parmInfo.parmName);
			}
		}
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSummary() {
		return Summary;
	}

	public void setSummary(String summary) {
		Summary = summary;
	}

	public String getOperationId() {
		return OperationId;
	}

	public void setOperationId(String operationId) {
		OperationId = operationId;
	}
}
