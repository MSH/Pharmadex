package org.msh.pharmadex.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;

public class ExcelTools {
	/**
	 * @param index

	 * @return
	 */
	public static String getColumnLetter(Integer index){
		final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
		String res = "";
		int rest=0;
		if (index<27) return alpha.substring(index, index+1);
		do{
			rest = (index % 27);
			res = "".equals(res) ? String.valueOf(rest) : res + String.valueOf(rest); 
			index = index - 27;
		}while(index<=0);
		return res;
	}
	
	/**
	 * ���������� ������ ���� �����, ������ ����� ������ ���������� � ���������
	 * @param cell1 - ������ ������
	 * @param cell2 - ������ ������
	 * @return ������, ���� ��� ������ ����� ���������� ������
	 */
	public static boolean cellFormatsAreEquals(Cell cell1, Cell cell2){
		int type1 = cell1.getCellType();
		int type2 = cell2.getCellType();
		if (type1==type2) return true;
		if (type1==1&&type2==3) return true; //���� ���������, ������ �����
		if (type1==3&&type2==1) return true; //��������
		return (type1==type2);
		
	}
	
	/**
	 * ���������, �������� �� ������ ������� ��������� ������
	 * @param row - ����������� ������
	 * @param numCols - ���������� ����������� ��������
	 * @return ������ - ���� ��� ������� (�������� ����������) ������ ������ �����
	 */
	public static boolean rowIsEmpty(Row row, Integer numCols){
		Cell cell=null;
		for(int i=1;i<numCols;i++){
			cell = row.getCell(i);
			if (!cellIsEmpty(cell)) return false;
		}
		return true;
	}

	/**
	 * ���������, �������� �� ������ ������� ��������� ������
	 * @param row - ����������� ������
	 * @param numCols - ���������� ����������� ��������
	 * @return ������ - ���� ��� ������� (�������� ����������) ������ ������ �����
	 */
	public static boolean rowIsEmpty(Row row, Integer startCol, Integer numCols){
		Cell cell=null;
		for(int i=startCol-1;i<numCols;i++){
			cell = row.getCell(i);
			if (!cellIsEmpty(cell))
				return false;
		}
		return true;
	}
	
	public static boolean cellIsEmpty(Cell cell){
		if (cell==null)
			return true;
		if (cell.getCellType()==Cell.CELL_TYPE_BLANK || cell.getCellType() == Cell.CELL_TYPE_STRING){
			if (cell.getStringCellValue().isEmpty())
				return true;
			else if ("".equals(cell.getStringCellValue()))
				return true;
			else  if ("".equals(cell.getStringCellValue().trim()))
				return true;
			return false;
		}else if (cell.getCellType()==Cell.CELL_TYPE_BOOLEAN){
			boolean val = cell.getBooleanCellValue();
			if (val!=true || val!=false) return false; //�� ����, ������ �� ���... ������� � ���� ������ ������ ���� "��������� ��������" 
		}else if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			double val = cell.getNumericCellValue();
			return "".equals(String.valueOf(val));
		}else if (cell.getCellType()==Cell.CELL_TYPE_FORMULA){
			return true;
		}else if (cell.getCellType()==Cell.CELL_TYPE_ERROR){
			return true;
		}
		
		return true;
	}
	
	/**
	 * �������� ��������� �������� �� ������ Excel, ������� �������� ��� �����
	 * ����� �������� POI �������������� ��� �����, ������� �������������� �����
	 * @param cell
	 * @return ��������� ��������
	 */
	public static String getStringLikeNumeric(Cell cell){
		String str = null;
		if (cell==null) return null;
		if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			 double dbl = cell.getNumericCellValue();
			 str = String.format("%.0f", dbl);
		}else{
			str = cell.getStringCellValue();
		}
		return str;
	}
	
	public static Calendar getDateFromCell(Cell cell){
		Calendar date = null;
		if (ExcelTools.cellIsEmpty(cell)) return null;
		if ((cell.getCellType()==Cell.CELL_TYPE_NUMERIC)||(cell.getCellType()==Cell.CELL_TYPE_FORMULA)){
			 double dbl = cell.getNumericCellValue();
			 if (HSSFDateUtil.isCellDateFormatted(cell)) {
				    Date dt = HSSFDateUtil.getJavaDate(dbl);
				    date = Calendar.getInstance();
				    date.setTime(dt);
			 }
		}else{//��� ��������� ������������� ���� ��� ������ ������ ������ ��� ���������� ����
			String str = cell.getStringCellValue();
			if ("".equals(str)) return null;
			
		}
		return date;
	}
	
	public static String getStringCellValue(Row row, int colNo){
		if (row==null) return null;
		Cell cell = row.getCell(colNo);
		if (cell==null) return null;
		if (cellIsEmpty(cell)) return null;
		String str = cell.getStringCellValue();
		return str;
	}

	public static String getStringCellValue(Cell cell){
		String str = null;
		if (cell==null) return null;
		if (cellIsEmpty(cell)) return null;
		if (cell.getCellType() == Cell.CELL_TYPE_STRING)
			str = cell.getStringCellValue();
		else{
			try{
				Calendar cal = getDateFromCell(cell);
				if (cal!=null){
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
					str = sdf.format(cal.getTime());
				}else{
					str = getStringLikeNumeric(cell);
				}
			}catch (Exception e){
				return null;
			}
		}
		return str;
	}

	
	public static void setCellBackground(Cell cell, short color){
		Workbook wb = cell.getSheet().getWorkbook();
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(color);
		style.setFillBackgroundColor(IndexedColors.DARK_GREEN.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cell.setCellStyle(style);
	}
}
