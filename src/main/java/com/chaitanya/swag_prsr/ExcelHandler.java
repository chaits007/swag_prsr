package com.chaitanya.swag_prsr;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFSheet;
import  org.apache.poi.hssf.usermodel.HSSFWorkbook;
import  org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcelHandler {
	
	private String fileName;
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private int colCount = 0;
	private HSSFRow rowhead;
	private short rowCount = 0;
	
	public void createExcelSheet(String fileName) {
        try {
        	this.fileName = "./resources/generatedFiles/" + fileName + "_data.xls";
        	
            File f = new File(fileName);
            if (f.delete()) {
            	System.out.println(fileName + " has been deleted!");
            } else {
            	System.out.println(fileName + " does not exist!");
            }
            
            workbook = new HSSFWorkbook();
            sheet = workbook.createSheet("DataSheet");  
            rowhead = sheet.createRow(rowCount);
//            HSSFRow row = sheet.createRow((short)1);
//            row.createCell(0).setCellValue("1");
//            row.createCell(1).setCellValue("Sankumarsingh");
//            row.createCell(2).setCellValue("India");
//            row.createCell(3).setCellValue("sankumarsingh@gmail.com");

        } catch ( Exception ex ) {
            System.out.println(ex);
        }
	}
	
	public void writeHeader(String head) {
        rowhead.createCell(colCount).setCellValue(head);
        colCount++;
	}
	
	public void createNewRow() {
		rowCount++;
		rowhead = sheet.createRow(rowCount);
		
	}
	
	public void writeToFile() {
		try {
	        FileOutputStream fileOut = new FileOutputStream(fileName);
	        workbook.write(fileOut);
	        fileOut.close();
	        workbook.close();
	        System.out.println(fileName + " has been generated!");			
		} catch ( Exception ex ) {
            System.out.println(ex);
        }
	}
}
