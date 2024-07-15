package maintenance.edititemdetails.model;

import java.time.LocalDateTime;

public class EditItemDetailsModel {

	private int item_id;
	private String unique_item_id;
	private String item_name;
	private int current_stock;
	private String item_unit_of_measurement;
	private int item_minimum_threshold;
	private int item_maximum_threshold;
	private String item_category;
	private String item_brand;
	private String item_supplier;
	private String item_status;
	private LocalDateTime item_datetime_of_registration;

	public int getItem_id() {
		return item_id;
	}

	public String getUnique_item_id() {
		return unique_item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public int getCurrent_stock() {
		return current_stock;
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

	public String getItem_category() {
		return item_category;
	}

	public String getItem_brand() {
		return item_brand;
	}

	public String getItem_supplier() {
		return item_supplier;
	}

	public String getItem_status() {
		return item_status;
	}

	public LocalDateTime getItem_datetime_of_registration() {
		return item_datetime_of_registration;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public void setUnique_item_id(String unique_item_id) {
		this.unique_item_id = unique_item_id;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public void setCurrent_stock(int current_stock) {
		this.current_stock = current_stock;
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

	public void setItem_category(String item_category) {
		this.item_category = item_category;
	}

	public void setItem_brand(String item_brand) {
		this.item_brand = item_brand;
	}

	public void setItem_supplier(String item_supplier) {
		this.item_supplier = item_supplier;
	}

	public void setItem_status(String item_status) {
		this.item_status = item_status;
	}

	public void setItem_datetime_of_registration(LocalDateTime item_datetime_of_registration) {
		this.item_datetime_of_registration = item_datetime_of_registration;
	}

}
