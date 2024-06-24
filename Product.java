package shopping;

public class Product {
	private String productName;
	private double price;
	private int quantity;
	private int productCode;

	Product(int productCode, String productName, double price, int quantity) {
		this.productCode = productCode;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
	}

	public int getProductCode() {
		return productCode;
	}

	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		String result;

		result = "ProductCode = " + productCode + ", ProductName = " + productName + ", price=" + price + ", quantity="
				+ quantity;

		return result;
	}

}
