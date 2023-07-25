import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class App {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gui_system";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234";
    private static int loanRequestCount = 0;
    public static void main(String[] args) {
        try {
            // Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(3006);
            System.out.println("Server started. Listening on port 3006...");

            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create input and output streams for communication with the client
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                // Handle client requests
                String clientRequest;
                while ((clientRequest = input.readLine()) != null) {
                    // Process the client request and send a response
                    String serverResponse = processRequest(clientRequest);
                    
                    output.println(serverResponse);
                }

                // Close the streams and the client socket
                input.close();
                output.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request) {
        // Extract command and parameters from the request
        String[] tokens = request.split(" ");
        if (tokens.length < 1) {
            return "Invalid request";
        }
        if (tokens.length == 2 ) {
            
            return handleIntegerBasedRequest( tokens);
        }
        String command = tokens[0];
        try {
          switch (command) {
            case "login":
                // Perform login action
                if (tokens.length < 3) {
                    return "Invalid login request";
                }
                String username = tokens[1];
                String password = tokens[2];
                return performLogin(username, password);
            case "deposit":
                // Perform deposit action
                if (tokens.length < 3) {
                    return "Invalid deposit request";
                }

                int receiptNumber = Integer.parseInt(tokens[1]);
                String  dateDeposited = tokens[2];
                return performDeposit( receiptNumber,dateDeposited);
            case "requestLoan":
                // Perform loan request action
                if (tokens.length < 4) {
                    return "Invalid loan request";
                }
                int requestedAmount = Integer.parseInt(tokens[1]);
                int paymentPeriod = Integer.parseInt(tokens[2]);
                String memberNumber = tokens[3];
                return performLoanRequest(requestedAmount, paymentPeriod, memberNumber);
             case "checkLoanStatus":
                // Perform loan request action
                if (tokens.length < 2) {
                    return "Invalid loan request";
                }
                int applicationNumber = Integer.parseInt(tokens[1]);
                return performCheckLoanStatus(applicationNumber);
            default:
                return "Unknown command";
        }
        } catch (NumberFormatException e) {
        return "Unknown command";
    }
    }
    

    /* */
    private static String handleIntegerBasedRequest(String[] tokens) {
        // Assuming the first element is the memberNumber and the second element is the phoneNumber
        if (tokens.length < 2) {
            return "Invalid integer-based request. Both memberNumber and phoneNumber are required.";
        }
    
        String memberNumber = tokens[0];
        String phoneNumber = tokens[1];
    
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        
            // Check if there's an existing referenceNumber in the references table
            String sql = "SELECT * FROM members WHERE member_number = ? AND phone_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberNumber);
            statement.setString(2, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
        
            if (resultSet.next()) {
                // The member number and the phone number are in the database
                String passwordSql = "SELECT password FROM members where member_number = ? AND phone_number = ?";
                PreparedStatement passwordStatement = connection.prepareStatement(passwordSql);
                passwordStatement.setString(1, memberNumber);
                passwordStatement.setString(2, phoneNumber);
                ResultSet passwordResultSet = passwordStatement.executeQuery();
        
                if (passwordResultSet.next()) {
                    // Password found
                    String password = passwordResultSet.getString("password");
                    passwordResultSet.close();
                    passwordStatement.close();
                    resultSet.close();
                    statement.close();
                    connection.close();
                    return "Your password is: " + password;
                } else {
                    // Password not found
                    getExistingReferenceNumber(connection, memberNumber, phoneNumber);
                    passwordResultSet.close();
                    passwordStatement.close();
                    resultSet.close();
                    statement.close();
                    connection.close();
                    return "Password not found. Please come back again after new information is uploaded.";
                }
            } else {
                // Invalid member number or phone number
                resultSet.close();
                statement.close();
                connection.close();
                return "Invalid member number or phone number";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
        
    }
    
      private static String getExistingReferenceNumber(Connection connection, String memberNumber, String phoneNumber) throws SQLException {
        // Prepare the SQL statement to check for an existing referenceNumber
        String sql = "SELECT referenceNumber FROM reference WHERE memberNumber = ? AND phoneNumber = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, memberNumber);
        statement.setString(2, phoneNumber);
    
        // Execute the query
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            // Existing referenceNumber found
            String referenceNumber = resultSet.getString("referenceNumber");
            resultSet.close();
            statement.close();
            return referenceNumber;
        } else {
            // No existing referenceNumber found
            insertIntoReferences(connection, memberNumber, phoneNumber);
            resultSet.close();
            statement.close();
            return null;
        }
    }

    private static void insertIntoReferences(Connection connection, String memberNumber, String phoneNumber) throws SQLException {
        // Prepare the SQL statement to insert into the references table
        String sql = "INSERT INTO reference (memberNumber, phoneNumber,reason) VALUES (?, ?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, memberNumber);
        statement.setString(2, phoneNumber);
        statement.setString(3, "Failed to login");
        
    
        // Execute the insert
        statement.executeUpdate();
        statement.close();
    }

     
   
    private static String performLogin(String username, String password) {
        try {
            // Establish a connection to the database
            
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            
            // Prepare the SQL statement
            String sql = "SELECT * FROM members WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Login successful
                resultSet.close();
                statement.close();
                connection.close();
                 System.out.println("Correct credentials");
                return "Login successful";
               
            } else {
                // Invalid username or password
                resultSet.close();
                statement.close();
                connection.close();
                System.out.println("Wrong credentials");
                return "Invalid username or password";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }

   
    private static String performDeposit( int receiptNumber, String dateDeposited) {
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Check if the member has an unpaid loan or balance
            String sql = "SELECT * FROM deposit WHERE receiptNumber = ? AND depositDate = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, receiptNumber);
            statement.setString(2, dateDeposited);
    
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
           if (resultSet.next()) {
              resultSet.close();
                statement.close();
                connection.close();
                return "Your deposit with the receipt number " + receiptNumber + " has been received";
           }else{
                resultSet.close();
                statement.close();
                connection.close();
                return "deposit not foun, please check again when new info is uploaded";
           }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }
    
    
    
    
    private static String performLoanRequest(int loanAmount, int paymentPeriod,String memberNumber) {
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // Insert the loan request into the database
            String sql = "INSERT INTO loanrequests (amount , paymentPeriod,memberNumber) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, loanAmount);
            statement.setInt(2, paymentPeriod);
            statement.setString(3, memberNumber);

            // Execute the insert
            int rowsAffected = statement.executeUpdate();

            // Increment the loan request count
            loanRequestCount++;

            // Check if there are 10 loan requests
            if (loanRequestCount == 10) {
                // Generate the recommended list of loan amount distribution
                // For demonstration purposes, let's assume the recommended amounts are fixed
                int[] recommendedAmounts = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };

                // Insert the recommended amounts into the database
                sql = "INSERT INTO recommended_loans (amount, paymentPeriod,memberNumber) VALUES (?, ?, ?)";
                statement = connection.prepareStatement(sql);
                statement.setInt(2, paymentPeriod);
                statement.setString(3, memberNumber);
                for (int amount : recommendedAmounts) {
                    statement.setInt(1, amount);
                    statement.addBatch();
                }

                // Execute batch insert
                statement.executeBatch();

                // Reset the loan request count
                loanRequestCount = 0;

                PrintWriter output = new PrintWriter(null, false, null);
                // Notify the client about the recommended amounts
                output.println("Recommended loan amounts:");
                for (int amount : recommendedAmounts) {
                    output.println(amount);
                }

                return "Recommended loan amounts provided";
            }

            // If there are not 10 loan requests yet, simply acknowledge the loan request
            return "Loan request processed";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }

private static String generateApplicationNumber() {
    // Generate a unique application number based on your requirements
    // This can be a combination of letters, numbers, or any desired format
    // You can use libraries or algorithms to generate unique identifiers
    
    // Example: Generating a random 6-digit application number
    Random random = new Random();
    int applicationNumber = 100000 + random.nextInt(900000); // Range: 100000 to 999999
    return String.valueOf(applicationNumber);
}

    
    

    /*  
    private static boolean checkFundsAvailability() {
        // Establish a connection to the database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Retrieve the balance from the Sacco's account
            String sql = "SELECT balance FROM SaccoAccount";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    return balance >= 2000000; // Return true if balance is sufficient
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there's an error or insufficient balance
    }

    */
    
    
    private static String performCheckLoanStatus(int applicationNumber) {
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Prepare the SQL statement
            String sql = "SELECT status FROM loanRequests WHERE applicationNumber = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, applicationNumber);
    
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                // Loan request found
                String status = resultSet.getString("status");
                resultSet.close();
                statement.close();
                connection.close();
                
                return "Your loan application is : " + status;
            } else {
                // Loan request not found
                resultSet.close();
                statement.close();
                connection.close();
                return "Loan request not found";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }
    
    
    
}
