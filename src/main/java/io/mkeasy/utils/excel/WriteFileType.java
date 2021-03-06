package io.mkeasy.utils.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteFileType {

	public static Workbook getWorkbook(String fileName) {

		Workbook wb = null;

		if(fileName.toUpperCase().endsWith(".XLS")) {
			wb = new HSSFWorkbook();
		}
		else if(fileName.toUpperCase().endsWith(".XLSX")) {
			wb = new XSSFWorkbook();
		}

		return wb;

	}

}