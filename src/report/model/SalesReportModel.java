package report.model;

import java.time.LocalDateTime;

public class SalesReportModel {
	int sales_report_id;
	String unique_sales_report_id;
	String menu_item_name;
	Double menu_item_price;
	Double menu_item_quantity_sold;
	LocalDateTime sold_datetime;
	public int getSales_report_id() {
		return sales_report_id;
	}
	public String getUnique_sales_report_id() {
		return unique_sales_report_id;
	}
	public String getMenu_item_name() {
		return menu_item_name;
	}
	public Double getMenu_item_price() {
		return menu_item_price;
	}
	public Double getMenu_item_quantity_sold() {
		return menu_item_quantity_sold;
	}
	public LocalDateTime getSold_datetime() {
		return sold_datetime;
	}
	public void setSales_report_id(int sales_report_id) {
		this.sales_report_id = sales_report_id;
	}
	public void setUnique_sales_report_id(String unique_sales_report_id) {
		this.unique_sales_report_id = unique_sales_report_id;
	}
	public void setMenu_item_name(String menu_item_name) {
		this.menu_item_name = menu_item_name;
	}
	public void setMenu_item_price(Double menu_item_price) {
		this.menu_item_price = menu_item_price;
	}
	public void setMenu_item_quantity_sold(Double menu_item_quantity_sold) {
		this.menu_item_quantity_sold = menu_item_quantity_sold;
	}
	public void setSold_datetime(LocalDateTime sold_datetime) {
		this.sold_datetime = sold_datetime;
	}
}
