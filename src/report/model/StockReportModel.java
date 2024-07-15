package report.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockReportModel {
	private String unique_stock_report_id;
	private String item_name;
	private String item_brand;
	private String item_supplier;
	private String item_category;
	private String item_unit_of_measurement;
	private int item_minimum_threshold;
	private int item_maximum_threshold;
	private Double previous_total_stock;
	private Double change_in_stock;
	private Double updated_stock;
	private String item_status;
	private LocalDateTime datetime_of_change;

	public String getItem_name() {
		return item_name;
	}

	public String getItem_brand() {
		return item_brand;
	}

	public String getItem_supplier() {
		return item_supplier;
	}

	public String getItem_category() {
		return item_category;
	}

	public String getItem_unit_of_measurement() {
		return item_unit_of_measurement;
	}

	public int getItem_minimum_threshold() {
		return item_minimum_threshold;
	}

	public int getItem_maximum_threshold() {
		return item_maximum_threshold;
	}

	public Double getPrevious_total_stock() {
		return previous_total_stock;
	}

	public Double getChange_in_stock() {
		return change_in_stock;
	}
	public Double getUpdated_stock() {
		return updated_stock;
	}

	public String getItem_status() {
		return item_status;
	}

	public LocalDateTime getDatetime_of_change() {
		return datetime_of_change;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public void setItem_brand(String item_brand) {
		this.item_brand = item_brand;
	}

	public void setItem_supplier(String item_supplier) {
		this.item_supplier = item_supplier;
	}

	public void setItem_category(String item_category) {
		this.item_category = item_category;
	}

	public void setItem_unit_of_measurement(String item_unit_of_measurement) {
		this.item_unit_of_measurement = item_unit_of_measurement;
	}

	public void setItem_minimum_threshold(int item_minimum_threshold) {
		this.item_minimum_threshold = item_minimum_threshold;
	}

	public void setItem_maximum_threshold(int item_maximum_threshold) {
		this.item_maximum_threshold = item_maximum_threshold;
	}
	
	public void setPrevious_total_stock(Double previous_total_stock) {
		this.previous_total_stock = previous_total_stock;
	}
	
	public void setChange_in_stock(Double change_in_stock) {
		this.change_in_stock = change_in_stock;
	}
	
	public void setUpdated_stock(Double updated_stock) {
		this.updated_stock = updated_stock;
	}

	public void setItem_status(String item_status) {
		this.item_status = item_status;
	}


	public void setDatetime_of_change(LocalDateTime datetime_of_change) {
		this.datetime_of_change = datetime_of_change;
	}

	public String getUnique_stock_report_id() {
		return unique_stock_report_id;
	}

	public void setUnique_stock_report_id(String unique_stock_report_id) {
		this.unique_stock_report_id = unique_stock_report_id;
	}
}
