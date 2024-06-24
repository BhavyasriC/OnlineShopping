package shopping;

import java.util.Scanner;

public class CustomerDetails implements UserInterface {

    private OrderProcessingWithDataBase order;

    public CustomerDetails(OrderProcessingWithDataBase order) {
        this.order = order;
    }

    public void browseProducts() {
        System.out.println("Welcome to Lulu Mart");
        try {
            OrderProcessingWithDataBase.displayProductDetails();;;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void placeOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter product code to order: ");
        int productCode = sc.nextInt();
        sc.nextLine(); 

        Product product = null;
        try {
            product = OrderProcessingWithDataBase.readProductByCode(productCode);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Enter quantity to order: ");
        int quantity = sc.nextInt();
        sc.nextLine(); 

        if (quantity > product.getQuantity()) {
            System.out.println("Insufficient stock available.");
        } else {
            try {
                boolean success = OrderProcessingWithDataBase.placeOrder(productCode, quantity);
                if (success) {
                    System.out.println("Order placed for " + quantity + " units of product code " + productCode);
                } else {
                    System.out.println("Failed to place order.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void itemsList() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. List Products\n2. Place Order\n3. Back");
            System.out.print("Choose an operation: ");
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    browseProducts();
                    break;
                case 2:
                    placeOrder();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
