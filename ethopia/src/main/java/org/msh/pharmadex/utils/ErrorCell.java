package org.msh.pharmadex.utils;



/**
  * for EXCEL tools
 */
public class ErrorCell {
	private String tabName;
	private String errorMsg;
	private Integer columnNo;
	private Integer rowNo;

	private Integer severity;
	private long id=0;
	private String table=null;

	public String getCellName(){
		return ExcelTools.getColumnLetter(columnNo) + String.valueOf((rowNo+1));
	}

	public ErrorCell(String tabName, String errorMsg, Integer columnNo, Integer rowNo,  Integer severity) {
		this.tabName = tabName;
		this.errorMsg = errorMsg;
		this.columnNo = columnNo;
		this.rowNo = rowNo;

	}
	


	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Integer getColumnNo() {
		return columnNo;
	}

	public void setColumnNo(Integer columnNo) {
		this.columnNo = columnNo;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

}
