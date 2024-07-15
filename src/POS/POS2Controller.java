package POS;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;

import database.DatabaseConnection;

public class POS2Controller implements Initializable {

	private Button selectedCategoryButton; // Field to store the currently selected category button

	@FXML
	private Button backButton;

	@FXML
	private Button cash;

	@FXML
	private Button gcash;

	@FXML
	private Text creditText;

	@FXML
	private Text changeText;

	@FXML
	private Text creditAmount;

	@FXML
	private Text changeAmount;

	@FXML
	private FlowPane orders;

	@FXML
	private AnchorPane rootPane; // Ensure to match with the root pane in POS2.fxml

	@FXML
	private TextField textField; // The text field where numbers are displayed

	@FXML
	private Text subtotalText;

	@FXML
	private Text discountText;

	@FXML
	private Text grandTotalText;

	@FXML
	private Text referenceText;

	@FXML
	private Text referenceValue;

	@FXML
	private Button confirmButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button CancelButton;

	private Order currentOrder;

	private Stage primaryStage; // Reference to the primary stage

	private double grandTotal;

	private boolean isPWDDiscountApplied;

	private boolean isSeniorDiscountApplied;

	private static String PREFIX = null;
	private static final int NUMERIC_LENGTH = 10; // Length of the numeric part

	private MainController mainController; // Reference to the MainController
	
	DatabaseConnection dbConnectionInformation = new DatabaseConnection();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize your controller
		cash.setStyle("-fx-background-color: #FFBA68;");
		selectedCategoryButton = cash;
		textField.setPromptText("Credit");
		referenceText.setDisable(true);
		referenceValue.setDisable(true);
		referenceText.setVisible(false);
		referenceValue.setVisible(false);

		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (selectedCategoryButton == cash) {
					updateCreditAmount(newValue);
					updateChangeAmount(newValue);
				} else if (selectedCategoryButton == gcash) {
					updateReferenceValue(newValue);
				}
				// updateConfirmButtonState(newValue);
			}
		});
	}

	private void updateIngredientQuantities(Order order) throws SQLException {
		DatabaseConnection dbConnectionInformation = new DatabaseConnection();
		Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());

		try {
			for (OrderItem item : order.getItems()) {
				// Fetch ingredients for each menu item
				String fetchIngredientsSQL = "SELECT ingredient_name, ingredient_quantity_needed, ingredient_unit_of_measurement FROM menu_item_ingredient WHERE menu_item_name = ? AND ingredient_status = 'active'";
				String stockQuery = "SELECT current_stock, item_unit_of_measurement FROM inventory WHERE item_name = ?";

				try (PreparedStatement fetchStmt = conn.prepareStatement(fetchIngredientsSQL)) {
					fetchStmt.setString(1, item.getName());

					try (ResultSet rs = fetchStmt.executeQuery()) {
						while (rs.next()) {
							String ingredientName = rs.getString("ingredient_name");
							double quantityUsed = rs.getDouble("ingredient_quantity_needed");
							String unitofMeasurement = rs.getString("ingredient_unit_of_measurement");

							// Fetch original unit from inventory
							String originalUnit = null;
							try (PreparedStatement stockStmt = conn.prepareStatement(stockQuery)) {
								stockStmt.setString(1, ingredientName);

								try (ResultSet stockRs = stockStmt.executeQuery()) {
									if (stockRs.next()) {
										originalUnit = stockRs.getString("item_unit_of_measurement");
									} else {
										// Handle case where ingredient is not found in inventory
										throw new SQLException("Ingredient not found in inventory: " + ingredientName);
									}
								}
							}

							// Convert quantity used to stock unit of measurement
							double convertedQuantityUsed = convertQuantityToStockUnit(unitofMeasurement, quantityUsed,
									originalUnit);

							// Update the inventory quantities
							String updateInventorySQL = "UPDATE inventory SET current_stock = current_stock - ? WHERE item_name = ?";
							try (PreparedStatement updateStmt = conn.prepareStatement(updateInventorySQL)) {
								updateStmt.setDouble(1, convertedQuantityUsed * item.getQuantity()); // Adjusted based
																										// on item
																										// quantity
								updateStmt.setString(2, ingredientName);
								updateStmt.executeUpdate();
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close(); // Close connection in finally block
		}
	}

	private double convertQuantityToStockUnit(String ingredientUnit, double quantityNeeded, String originalUnit) {
		switch (ingredientUnit) {
		case "Grams (g)":
			return gramsToKilograms(quantityNeeded);
		case "Kilograms (kg)":
			return quantityNeeded; // Already in kilograms
		case "Table Spoon (tbsp)":
			if (originalUnit.equals("Liter (L)")) {
				return tbspToLiters(quantityNeeded);
			} else {
				return tbspToKilograms(quantityNeeded);
			}
		case "Tea Spoon (tsp)":
			if (originalUnit.equals("Liter (L)")) {
				return tspToLiters(quantityNeeded);
			} else {
				return tspToKilograms(quantityNeeded);
			}
		case "Milliliters (ml)":
			return mlToLiters(quantityNeeded);
		default:
			return quantityNeeded; // Default to original quantity needed
		}
	}

	private double gramsToKilograms(double grams) {
		return grams / 1000.0;
	}

	private double tbspToLiters(double tbsp) {
		return tbsp * 0.015;
	}

	private double tspToLiters(double tsp) {
		return tsp * 0.005;
	}

	private double mlToLiters(double ml) {
		return ml / 1000.0;
	}

	private double tbspToKilograms(double tbsp) {
		return tbsp * 0.015;
	}

	private double tspToKilograms(double tsp) {
		return tsp * 0.005;
	}

	private void updateCreditAmount(String newValue) {
		// Update the creditAmount based on the newValue from amountTextField
		creditAmount.setText("₱" + newValue);
	}

	private void updateChangeAmount(String newValue) {
		try {
			double credit = Double.parseDouble(newValue);
			double change = credit - grandTotal;
			changeAmount.setText("Change: ₱" + String.format("%.2f", change));
		} catch (NumberFormatException e) {
			changeAmount.setText("Change: ₱0.00");
		}
	}

	private void updateReferenceValue(String newValue) {
		referenceValue.setText(newValue);
	}

//    private void updateConfirmButtonState(String newValue) {
//        try {
//            double credit = Double.parseDouble(newValue);
//            confirmButton.setDisable(false);
//        } catch (NumberFormatException e) {
//            confirmButton.setDisable(true);
//        }
//    }

	// Default constructor required by FXMLLoader
	public POS2Controller() {
		// Default constructor
	}

	// Constructor with Stage parameter if needed
	public POS2Controller(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setData(MainController mainController, Order currentOrder, double subtotal, double discount,
			double grandTotal, boolean isPWDDiscountApplied, boolean isSeniorDiscountApplied) {
		this.mainController = mainController; // Set the reference to MainController
		this.currentOrder = currentOrder;
		this.grandTotal = grandTotal;
		this.subtotalText.setText("Subtotal: ₱" + String.format("%.2f", subtotal));
		this.discountText.setText("Discount: ₱" + String.format("%.2f", discount));
		this.grandTotalText.setText("Grand Total: ₱" + String.format("%.2f", grandTotal));
		this.isPWDDiscountApplied = isPWDDiscountApplied;
		this.isSeniorDiscountApplied = isSeniorDiscountApplied;

		System.err.println(isPWDDiscountApplied);
		System.out.print(isSeniorDiscountApplied);
		displayOrder(currentOrder);
		// Additional initialization if needed
	}

	private void displayOrder(Order currentOrder) {
		orders.getChildren().clear(); // Clear existing items (if any)

		for (OrderItem item : currentOrder.getItems()) {
			orders.getChildren().add(createOrderItemPane(item));
		}
	}

	private void updateCategorySelection(Button selectedButton) {
		if (selectedCategoryButton != null && selectedCategoryButton != selectedButton) {
			selectedCategoryButton.setStyle(""); // Reset style of the previously selected button
		}
		selectedCategoryButton = selectedButton;
		selectedButton.setStyle("-fx-background-color: #FFBA68;");

		if (selectedButton == cash) {
			textField.clear();
			textField.setPromptText("Credit");
			referenceText.setDisable(true);
			referenceValue.setDisable(true);
			referenceText.setVisible(false);
			referenceValue.setVisible(false);
			creditText.setDisable(false);
			changeText.setDisable(false);
			creditText.setVisible(true);
			changeText.setVisible(true);
			creditAmount.setDisable(false);
			changeAmount.setDisable(false);
			creditAmount.setVisible(true);
			changeAmount.setVisible(true);
		} else if (selectedButton == gcash) {
			textField.clear();
			textField.setPromptText("Reference Code");
			referenceText.setDisable(false);
			referenceValue.setDisable(false);
			referenceText.setVisible(true);
			referenceValue.setVisible(true);
			creditText.setDisable(true);
			changeText.setDisable(true);
			creditText.setVisible(false);
			changeText.setVisible(false);
			creditAmount.setDisable(true);
			changeAmount.setDisable(true);
			creditAmount.setVisible(false);
			changeAmount.setVisible(false);
		}
	}
	
	public static String formatDouble(double value) {
        // Create DecimalFormat object with two decimal places pattern
        DecimalFormat df = new DecimalFormat("#0.00");
        
        // Format the double value
        String formatted = df.format(value);
        
        return formatted;
    }

	private Pane createOrderItemPane(OrderItem item) {
		// Create a pane to display order item details
		Pane itemPane = new Pane();
		itemPane.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px;");

		Text itemName = new Text(item.getName());
		itemName.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 17px;");
		itemName.setLayoutX(10);
		itemName.setLayoutY(20);
		
		Double price = item.getPrice();
		String formattedPrice = formatDouble(price);
		
		Text itemPrice = new Text("₱" + formattedPrice);
		itemPrice.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 17px;");
		itemPrice.setLayoutX(150);
		itemPrice.setLayoutY(20);

		Text itemQuantity = new Text("Quantity: " + item.getQuantity());
		itemQuantity.setStyle("-fx-font-family: 'Barlow'; -fx-font-size: 17px;");
		itemQuantity.setLayoutX(250);
		itemQuantity.setLayoutY(20);

		// Apply overall margin to each OrderItem pane
		itemPane.setPrefWidth(400); // Example width, adjust as necessary
		itemPane.setPrefHeight(50); // Example height, adjust as necessary
		itemPane.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px; -fx-margin: 20px;");

		itemPane.getChildren().addAll(itemName, itemPrice, itemQuantity);
		return itemPane;
	}

	@FXML
	void handleLogoutButton(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Security/View/V_Login.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = (Stage) logoutButton.getScene().getWindow();
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Handles number button clicks
	@FXML
	private void handleNumberButtonClick(ActionEvent event) {
		Button source = (Button) event.getSource();
		String buttonText = source.getText();
		textField.appendText(buttonText);
	}

	@FXML
	private void handleCashButtonClick(ActionEvent event) {
		updateCategorySelection(cash);
	}

	@FXML
	private void handleGcashButtonClick(ActionEvent event) {
		updateCategorySelection(gcash);
	}

	// Handles backspace button click
	@FXML
	private void handleBackspaceClick(ActionEvent event) {
		String text = textField.getText();
		if (!text.isEmpty()) {
			textField.setText(text.substring(0, text.length() - 1));
		}
	}

	// Handles cancel button click
	@FXML
	private void handleCancelClick(ActionEvent event) {
		textField.clear();
	}

	private int generateUniqueReferenceCode() {
		int referenceCode;
		do {
			referenceCode = generateRandomInt(); // Generate random integer code
		} while (isReferenceCodeExistsInDatabase(referenceCode)); // Check if code already exists
		return referenceCode;
	}

	private int generateRandomInt() {
		Random random = new Random();
		// Generate a random integer within a desired range
		int min = 100000; // Minimum value for the reference code
		int max = 999999; // Maximum value for the reference code
		return random.nextInt(max - min + 1) + min;
	}

	private boolean isReferenceCodeExistsInDatabase(int referenceCode) {
		String sql = "SELECT COUNT(*) AS count FROM orders_table WHERE order_reference_code = ?";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, referenceCode); // Set the integer reference code as parameter
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false; // Default to false if any exception occurs or if no records found
	}

	private double extractNumericValue(String text) {
		// Remove all non-numeric characters except for '.' (decimal point)
		String cleanedText = text.replaceAll("[^\\d.]", "");
		try {
			return Double.parseDouble(cleanedText);
		} catch (NumberFormatException e) {
			return 0.0; // Return 0.0 if parsing fails
		}
	}

	@FXML
	private void handleConfirmButtonAction(ActionEvent event) {
	    int referenceCode = generateUniqueReferenceCode();
	    double subtotal = extractNumericValue(subtotalText.getText().substring(1)); // Assuming format is "₱xxx.xx"
	    double discount = extractNumericValue(discountText.getText().substring(1)); // Assuming format is "₱xxx.xx"
	    double grandTotal = extractNumericValue(grandTotalText.getText().substring(1)); // Assuming format is "₱xxx.xx"
	    String paymentType = (selectedCategoryButton == cash) ? "Cash" : "GCash";
	    String gcashReferenceNumber = (selectedCategoryButton == gcash) ? referenceValue.getText() : "";
	    double change = (selectedCategoryButton == cash) ? extractNumericValue(changeAmount.getText().substring(8)) : 0.00; // Assuming format is "Change: ₱xxx.xx"
	    double receivedPayment = extractNumericValue(creditAmount.getText().substring(1));

	    // Validate conditions based on payment type
	    if (selectedCategoryButton == cash) {
	        if (receivedPayment < grandTotal) {
	            showAlert(Alert.AlertType.ERROR, "Insufficient Credit", "Credit amount must be at least equal to the Grand Total.");
	            return; // Exit the method without proceeding further
	        }
	    } else if (selectedCategoryButton == gcash) {
	        // Validate GCash reference number length and numeric check
	        if (gcashReferenceNumber.length() != 13) {
	            showAlert(Alert.AlertType.ERROR, "Invalid Reference Number", "GCash reference number must be exactly 13 digits.");
	            return; // Exit the method without proceeding further
	        }
	        if (!isNumeric(gcashReferenceNumber)) {
	            showAlert(Alert.AlertType.ERROR, "Invalid Input", "GCash Reference Code must be numbers.");
	            return; // Exit the method without proceeding further
	        }
	    }

	    // Insert data into database
	    boolean orderPlaced = insertOrderIntoDatabase(referenceCode, subtotal, discount, grandTotal, receivedPayment, paymentType, gcashReferenceNumber, change);
	    try {
	        updateIngredientQuantities(currentOrder);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // Show confirmation dialog
	    if (orderPlaced) {
	        showAlert(Alert.AlertType.INFORMATION, "Order Placed", "Order has been successfully placed.");
	        System.out.print(change);
	        System.out.print(discount);
	        openReceipt(change, discount, grandTotal, receivedPayment, referenceCode, subtotal);
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml")); // Ensure the FXML path is correct
	            Parent root = loader.load();
	            MainController mainController = loader.getController();

	            // Initialize or pass necessary data to the main controller
	            Scene scene = new Scene(root);
	            Stage stage = (Stage) CancelButton.getScene().getWindow();
	            stage.setScene(scene);
	            stage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        showAlert(Alert.AlertType.ERROR, "Order Error", "Failed to place the order.");
	    }
	}
	
	// Method to show an alert
		private void showAlert(String title, String message) {
		    Alert alert = new Alert(Alert.AlertType.ERROR);
		    alert.setTitle(title);
		    alert.setHeaderText(null);
		    alert.setContentText(message);
		    alert.showAndWait();
		}
		
		// Utility method to check if a string is numeric
		private boolean isNumeric(String str) {
		    if (str == null || str.isEmpty()) {
		        return false;
		    }
		    try {
		        Integer.parseInt(str);
		        return true;
		    } catch (NumberFormatException e) {
		        return false;
		    }
		}

	public void openReceipt(double change, double discount, double grandTotal, double payment, int reference,
			double total) {

		try {
			System.out.println("Loading ReceiptView.fxml..."); // Log the loading of the FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/POS/ReceiptView.fxml"));
			Parent root = loader.load();
			System.out.println("ReceiptView.fxml loaded successfully."); // Log successful loading

			ReceiptController receiptController = loader.getController();
			receiptController.setChangeVal(change);
			receiptController.setDisVal(discount);
			receiptController.setGtotVal(grandTotal);
			receiptController.setPayVal(payment);
			receiptController.setRefVal(reference);
			receiptController.setTotVal(total);
			System.out.println("ReceiptController values set."); // Log setting of controller values

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Receipt");
			dialogStage.initModality(Modality.WINDOW_MODAL);

			receiptController.setData(this, currentOrder);

			dialogStage.initOwner(CancelButton.getScene().getWindow()); // Set the owner to the main window
			dialogStage.setScene(new Scene(root)); // Set the scene for the dialog stage
			System.out.println("Showing Receipt dialog."); // Log the showing of the dialog
			dialogStage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error opening ReceiptView.fxml: " + e.getMessage()); // Log any errors
		}
	}

	private boolean insertOrderIntoDatabase(int referenceCode, double subtotal, double discount, double grandTotal,
			double receivedPayment, String paymentType, String gcashReferenceNumber, double change) {
		String orderSql = "INSERT INTO orders_table (order_reference_code, order_total_amount, order_discount, order_grand_total, order_payment_received, "
				+ "order_datetime, payment_type, GcashReferenceNumber, changeOfPayment) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String orderItemSql = "INSERT INTO orders_items_table (order_reference_code, menu_item_name, menu_item_price, ordered_menu_item_quantity) "
				+ "VALUES (?, ?, ?, ?)";

		String userTrailReportSql = "INSERT INTO user_trail_report (unique_event_id, event_name, user, event_datetime) VALUES (?, ?, ?, ?)";

		String salesReportSql = "INSERT INTO sales_report (unique_sales_report_id, payment_type, menu_item_name, menu_item_price, menu_item_quantity_sold, sold_datetime) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(dbConnectionInformation.getUrl(),
				dbConnectionInformation.getUsername(), dbConnectionInformation.getPassword());
				PreparedStatement orderStmt = conn.prepareStatement(orderSql);
				PreparedStatement orderItemStmt = conn.prepareStatement(orderItemSql);
				PreparedStatement userTrailReportStmt = conn.prepareStatement(userTrailReportSql);
				PreparedStatement salesReportStmt = conn.prepareStatement(salesReportSql)) {

			conn.setAutoCommit(false); // Start transaction

			// Insert into orders_table
			orderStmt.setInt(1, referenceCode);
			orderStmt.setDouble(2, subtotal);
			orderStmt.setDouble(3, discount);
			orderStmt.setDouble(4, grandTotal);
			orderStmt.setDouble(5, receivedPayment);
			orderStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // Current datetime
			orderStmt.setString(7, paymentType);
			orderStmt.setString(8, gcashReferenceNumber);
			orderStmt.setDouble(9, change);

			int rowsAffectedOrder = orderStmt.executeUpdate();

			// Insert into order_items_table for each order item
			for (OrderItem item : currentOrder.getItems()) {
				orderItemStmt.setInt(1, referenceCode);
				orderItemStmt.setString(2, item.getName());
				orderItemStmt.setDouble(3, item.getPrice());
				orderItemStmt.setInt(4, item.getQuantity());

				orderItemStmt.addBatch(); // Add batch for batch insertion
			}

			int[] rowsAffectedItems = orderItemStmt.executeBatch();

			// Insert into user_trail_report
			userTrailReportStmt.setString(1, generateUniqueId("itemID")); // Generate unique item_id
			userTrailReportStmt.setString(2, "Transaction"); // Event name
			userTrailReportStmt.setString(3, "Staff"); // User
			userTrailReportStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Current datetime

			int rowsAffectedUserTrail = userTrailReportStmt.executeUpdate();

			// Insert into sales_report for each order item
			for (OrderItem item : currentOrder.getItems()) {
				salesReportStmt.setString(1, generateUniqueId("salesreportID")); // Generate unique_sales_report_id
				salesReportStmt.setString(2, paymentType);
				salesReportStmt.setString(3, item.getName());
				salesReportStmt.setDouble(4, item.getPrice());
				salesReportStmt.setInt(5, item.getQuantity());
				salesReportStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // Current datetime

				salesReportStmt.addBatch(); // Add batch for batch insertion
			}

			int[] rowsAffectedSalesReport = salesReportStmt.executeBatch();

			// Commit the transaction if all insertions were successful
			if (rowsAffectedOrder == 1 && Arrays.stream(rowsAffectedItems).allMatch(i -> i == 1)
					&& rowsAffectedUserTrail == 1 && Arrays.stream(rowsAffectedSalesReport).allMatch(i -> i == 1)) {
				conn.commit();
				return true;
			} else {
				conn.rollback();
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String generateUniqueId(String type) {
		// Generate a random numeric part
		String numericPart = generateRandomNumericPart(NUMERIC_LENGTH);

		if (type == "itemID") {
			PREFIX = "LOG-";
		} else if (type == "salesreportID") {
			PREFIX = "SLR-";
		}

		// Combine prefix with numeric part
		String itemId = PREFIX + numericPart;

		return itemId;
	}

	private static String generateRandomNumericPart(int length) {
		Random random = new Random();
		StringBuilder numericPart = new StringBuilder();

		for (int i = 0; i < length; i++) {
			numericPart.append(random.nextInt(10)); // Append random digits (0-9)
		}

		return numericPart.toString();
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@FXML
	void handleCancelButtonAction(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Cancel Order");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to cancel the order?");

		// Get the user response
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// If user confirmed, proceed with cancellation
				try {
					System.out.println("hello world");
					FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml")); // Ensure the FXML path is
																							// correct
					Parent root = loader.load();
					MainController mainController = loader.getController();

					// Initialize or pass necessary data to the main controller
					Scene scene = new Scene(root);
					Stage stage = (Stage) CancelButton.getScene().getWindow();
					stage.setScene(scene);
					stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// If user canceled, do nothing
				System.out.println("Cancel operation was aborted by the user.");
			}
		});
	}

	@FXML
	private void goToLogout(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Security/View/V_Login.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = (Stage) logoutButton.getScene().getWindow();
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void goToHome(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/view/HelpStaffLandingPageView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			Stage stage = (Stage) logoutButton.getScene().getWindow();
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleBackButtonAction(ActionEvent event) {
		// Update the main controller with the current order and discount flags
		mainController.updateOrder(currentOrder);
		mainController.setDiscounts(isPWDDiscountApplied, isSeniorDiscountApplied);

		try {
			// Load the main POS view
			FXMLLoader loader = new FXMLLoader(getClass().getResource("POS.fxml"));
			Parent root = loader.load();

			// Ensure the controller is properly initialized and linked
			MainController mainController = loader.getController();
			mainController.updateOrder(currentOrder); // Ensure the updated order is set
			mainController.setDiscounts(isPWDDiscountApplied, isSeniorDiscountApplied);

			// Set the scene to the main stage
			Scene scene = new Scene(root);
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
