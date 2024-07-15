package inventorymanagement.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegisterItemStockModel {

	private int stock_id;
	private String unique_stock_report_id;
	private String item_name;
	private String item_brand;
	private String item_supplier;
	private String item_category;
	private String item_unit_of_measurement;
	private int item_minimum_threshold;
	private int item_maximum_threshold;
	private Double current_stock;
	private String item_status;
	private LocalDate new_item_batch_expiration_date; // Using String for simplicity, consider java.sql.Date or
														// java.time.LocalDate
	private LocalDate date_of_purchase;
	private LocalDateTime datetime_of_restocking;

	public int getStock_id() {
		return stock_id;
	}

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

	public double getCurrent_stock() {
		return current_stock;
	}

	public String getItem_status() {
		return item_status;
	}

	public LocalDate getNew_item_batch_expiration_date() {
		return new_item_batch_expiration_date;
	}

	public LocalDate getDate_of_purchase() {
		return date_of_purchase;
	}

	public LocalDateTime getDatetime_of_restocking() {
		return datetime_of_restocking;
	}

	public void setStock_id(int stock_id) {
		this.stock_id = stock_id;
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

	public void setCurrent_stock(Double current_stock) {
		this.current_stock = current_stock;
	}

	public void setItem_status(String item_status) {
		this.item_status = item_status;
	}

	public void setNew_item_batch_expiration_date(LocalDate new_item_batch_expiration_date) {
		this.new_item_batch_expiration_date = new_item_batch_expiration_date;
	}

	public void setDate_of_purchase(LocalDate date_of_purchase) {
		this.date_of_purchase = date_of_purchase;
	}

	public void setDatetime_of_restocking(LocalDateTime datetime_of_restocking) {
		this.datetime_of_restocking = datetime_of_restocking;
	}

	public String getUnique_stock_report_id() {
		return unique_stock_report_id;
	}

	public void setUnique_stock_report_id(String unique_stock_report_id) {
		this.unique_stock_report_id = unique_stock_report_id;
	}

}
