package shopping;


import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		   try {
	            OrderProcessingWithDataBase.createTables();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		OrderProcessingWithDataBase store = new OrderProcessingWithDataBase();
		Admin admin = new Admin(store);
		CustomerDetails customer = new CustomerDetails(store);

		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("1. Admin\n2. Customer\n3. Exit");
			System.out.print("Choose your role: ");
			int role = scanner.nextInt();
			scanner.nextLine();

			switch (role) {
			case 1:
				System.out.print("Enter admin username: ");
				String username = scanner.nextLine();
				System.out.print("Enter admin password: ");
				String password = scanner.nextLine();

				if (Admin.login(username, password)) {
					System.out.println("Login successful.");
					admin.itemsList();
				} else {
					System.out.println("Invalid username or password.");
				}
				break;
			case 2:
				customer.itemsList();
				break;
			case 3:
				return;
			default:
				System.out.println("Invalid role.");

			}

		}
	}

}
