package io.mkeasy.utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CellRef {

	public static String getName(Cell cell, int cellIndex) {
		int cellNum = 0;
		if(cell != null) {
			cellNum = cell.getColumnIndex();
		}
		else {
			cellNum = cellIndex;
		}

		return CellReference.convertNumToColString(cellNum);
	}

	public static String getValue(Cell cell) {
		String value = "";
		
		
		if(cell == null) {
			value = "";
		}
		else {
			value = String.valueOf(cell);
			/*
			if( cell.getCellType() == Cell.CELL_TYPE_FORMULA ) {
				value = cell.getCellFormula();
			}
			else if( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) {
				log.debug("Numeric Cell : {}", cell);
				value = String.valueOf(cell);
			}
			else if( cell.getCellType() == Cell.CELL_TYPE_STRING ) {
				value = cell.getStringCellValue();
			}
			else if( cell.getCellType() == Cell.CELL_TYPE_BOOLEAN ) {
				value = cell.getBooleanCellValue() + "";
			}
			else if( cell.getCellType() == Cell.CELL_TYPE_ERROR ) {
				value = cell.getErrorCellValue() + "";
			}
			else if( cell.getCellType() == Cell.CELL_TYPE_BLANK ) {
				value = "";
			}
			else {
				value = cell.getStringCellValue();
			}
			*/
		}

		return value;
	}

}
