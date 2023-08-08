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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// LoanRequest class to store details of each loan request
class LoanRequest {
    private int loanAmount;
    private int paymentPeriod;
    private String memberNumber;
    private String applicationNumber;

    public LoanRequest(int loanAmount, int paymentPeriod, String memberNumber, String applicationNumber) {
        this.loanAmount = loanAmount;
        this.paymentPeriod = paymentPeriod;
        this.memberNumber = memberNumber;
        this.applicationNumber = applicationNumber;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public int getPaymentPeriod() {
        return paymentPeriod;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }
}

public class App {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gui_system";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234";
    private static String memberNumber;
    private static String phoneNumber;
    
    public static void main(String[] args) throws SQLException {
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
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request) throws IOException, SQLException {
        // Extract command and parameters from the request
        String[] tokens = request.split(" ");
        String command = tokens[0];
        if (tokens.length == 1 && (tokens[0] instanceof String)) {
            return handleLoanAcceptance(tokens);
        }
        if (tokens.length < 1 ) {
            return "Invalid command";
            
        }
        if (tokens.length == 2 && (tokens[0].length())<15) {
            return provideReference(tokens);
        }
        
        
        
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
                if (tokens.length < 4) {
                    return "Invalid deposit request";
                }
                int amountDeposited = Integer.parseInt(tokens[1]);
                String receiptNumber = (tokens[2]);
                String  dateDeposited = tokens[3];
                return performDeposit(amountDeposited, receiptNumber,dateDeposited);
            case "requestLoan":
                // Perform loan request action
                if (tokens.length < 4) {
                    return "Invalid loan request";
                }
                int requestedAmount = Integer.parseInt(tokens[1]);
                int paymentPeriod = Integer.parseInt(tokens[2]);
                String memNum = tokens[3];
                if (!memNum.equals(memberNumber)){
                    System.out.println(memberNumber);
                    System.out.println(memNum);
                    return "Please enter your member number";
                }else{
                    return performLoanRequest(requestedAmount, paymentPeriod, memNum);
                }
                
             case "checkLoanStatus":
                // Perform loan request action
                if (tokens.length < 2) {
                    return "Invalid loan request";
                }
                int applicationNumber = Integer.parseInt(tokens[1]);
                return performCheckLoanStatus(applicationNumber);
                case "checkStatement":
                // Perform checkStatement action
                if (tokens.length < 3) {
                    return "Invalid checkStatement request";
                }
                String dateFrom = tokens[1];
                String dateTo = tokens[2];
                String response = performCheckStatement(dateFrom, dateTo);
                return response;
            default:
                return "Unknown command";
        }
        } catch (NumberFormatException e) {
        return "Unknown command";
    }
    }
    

    /* */
    private static String provideReference(String[] tokens) {
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
                String passwordSql = "SELECT password FROM members WHERE member_number = ? AND phone_number = ?";
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
                    passwordResultSet.close();
                    passwordStatement.close();
                    resultSet.close();
                    statement.close();
    
                    // Generate a random reference number
                    String referenceNumber = generateRandomReferenceNumber();
                    insertIntoReferences(connection, memberNumber, phoneNumber, referenceNumber);
    
                    connection.close();
    
                    return "Password not found. " + referenceNumber;
                }
            } else {
                // Invalid member number or phone number
                resultSet.close();
                statement.close();
                 String referenceNumber = generateRandomReferenceNumber();
                 insertIntoReferences(connection, memberNumber, phoneNumber, referenceNumber);
                connection.close();
                return "Invalid member number or phone number. Your reference number is " + referenceNumber +" Come back with it for follow up";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }
    
    private static String generateRandomReferenceNumber() {
        // Generate a random 9-digit reference number
        Random random = new Random();
        int randomNumber = random.nextInt(900_000_000) + 100_000_000;
        return String.valueOf(randomNumber);
    }
    
    private static void insertIntoReferences(Connection connection, String memberNumber, String phoneNumber, String referenceNumber) throws SQLException {
        // Prepare the SQL statement to insert into the references table
         Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(currentDate);
        String sql = "INSERT INTO reference (memberNumber, phoneNumber, referenceNumber, reason,date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, memberNumber);
        statement.setString(2, phoneNumber);
        statement.setString(3, referenceNumber);
        statement.setString(4, "Failed to login");
        statement.setString(5, formattedDate.toString());
    
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
                memberNumber = resultSet.getString("member_number");
                phoneNumber = resultSet.getString("phone_number");
                
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

   
    private static String performDeposit( int amountDeposited, String receiptNumber, String dateDeposited) {
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Check if the member has an unpaid loan or balance
            String sql = "SELECT * FROM available_deposits WHERE receipt_number = ? AND deposit_date = ? AND amount_deposited >= ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, receiptNumber);
            statement.setString(2, dateDeposited);
            statement.setInt(3,amountDeposited);
    
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
           if (resultSet.next()) {
              resultSet.close();
                statement.close();
                connection.close();
                return allocateFunds( amountDeposited);
           }else{
                resultSet.close();
                statement.close();
                String referenceNumber = generateRandomReferenceNumber();
                 insertDepositIntoReferences(connection, receiptNumber, referenceNumber);
                connection.close();

                return "deposit not foun. Reference number:"+ referenceNumber;
           }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error";
        }
    }


    private static String allocateFunds( int amountDeposited) {
        String response = ""; // Initialize the response
    
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(currentDate);
    
            String sql = "SELECT * FROM loanpayment WHERE memberNumber = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberNumber);
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                int amount = resultSet.getInt("amount");
                int amountPaid = resultSet.getInt("amountPaid");
                int amountLeft = amount - amountPaid;
                System.out.println(amount);
                System.out.println(amountPaid);
    
                if (amount == amountPaid) {
                    String insertSql = "INSERT INTO contributions (memberNumber, amount, date) VALUES (?, ?, ?)";
                    PreparedStatement statement2 = connection.prepareStatement(insertSql);
                    statement2.setString(1, memberNumber);
                    statement2.setInt(2, amountDeposited);
                    statement2.setString(3, formattedDate);
                    statement2.executeUpdate();
                    statement2.close();
    
                    response = "Your deposit was received and the money is sent to contributions";
    
                } else if (amountDeposited > amountLeft) {
                    int amountRemaining = amountDeposited - amountLeft;
    
                    String insertSql = "INSERT INTO contributions (memberNumber, amount, date) VALUES (?, ?, ?)";
                    PreparedStatement statement2 = connection.prepareStatement(insertSql);
                    statement2.setString(1, memberNumber);
                    statement2.setInt(2, amountRemaining);
                    statement2.setString(3, formattedDate);
                    statement2.executeUpdate();
                    statement2.close();
    
                    String updateSql = "UPDATE loanpayment SET amountPaid = ? WHERE memberNumber = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    updateStatement.setInt(1, amount);
                   updateStatement.setString(2, memberNumber);
                    updateStatement.executeUpdate();
                    updateStatement.close();
    
                    response = "Loan cleared and remaining amount sent to contributions";
    
                } else {
                    String updateSql = "UPDATE loanpayment SET amountPaid = ? WHERE memberNumber = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    updateStatement.setInt(1, amountPaid + amountDeposited);
                    updateStatement.setString(2, memberNumber);
                    updateStatement.executeUpdate();
                    updateStatement.close();
    
                    response = "Your deposit was received and the money is used to clear your loan";
                }
    
            } else {
                String insertSql = "INSERT INTO contributions (memberNumber, amount, date) VALUES (?, ?, ?)";
                PreparedStatement statement2 = connection.prepareStatement(insertSql);
                statement2.setString(1, memberNumber);
                statement2.setInt(2, amountDeposited);
                statement2.setString(3, formattedDate);
                statement2.executeUpdate();
                statement2.close();
    
                response = "Your deposit was received and the money is sent to contributions";
            }
    
            statement.close();
            connection.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to handle the exception more gracefully here
        }
    
        return response;
    }
    
    
      private static void insertDepositIntoReferences(Connection connection, String receipNumber,  String referenceNumber) throws SQLException {
        // Prepare the SQL statement to insert into the references table
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(currentDate);
        String sql = "INSERT INTO reference (receiptNumber, referenceNumber, reason, date, memberNumber, phoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, receipNumber);
        statement.setString(2, referenceNumber);
        statement.setString(3, "Failed to find deposit");
        statement.setString(4, formattedDate.toString());
        statement.setString(5, memberNumber);
        statement.setString(6, phoneNumber);
    
        // Execute the insert
        statement.executeUpdate();
        statement.close();
    }
    
    
    
    
    private static String performLoanRequest(int loanAmount, int paymentPeriod, String memberNumber) {
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<Integer> applicationNumbers = new ArrayList<>();
            // Check the number of existing loan requests in the database
            String countSql = "SELECT applicationNumber, COUNT(*) AS requestCount, SUM(loanAmount) AS totalLoan FROM loanrequests WHERE status = ? GROUP BY applicationNumber";

            PreparedStatement countStatement = connection.prepareStatement(countSql);
            countStatement.setString(1, "pending");
            ResultSet countResultSet = countStatement.executeQuery();
            
            int totalLoan = 0;
           
            int loanRequestCount = 1;
            while (countResultSet.next()) {
                loanRequestCount++;
                totalLoan = countResultSet.getInt("totalLoan");
                int applicationNo = countResultSet.getInt("applicationNumber");
                applicationNumbers.add(applicationNo);
            }
            
            
            // Check the totalmoney in the deposit table
            String totalMoneySql = "SELECT SUM(amount_deposited) AS totalmoney FROM available_deposits";
            PreparedStatement totalMoneyStatement = connection.prepareStatement(totalMoneySql);
            ResultSet totalMoneyResultSet = totalMoneyStatement.executeQuery();
            int totalMoney = 0;
    
            while (totalMoneyResultSet.next()) {
                totalMoney = totalMoneyResultSet.getInt("totalmoney");
            }
            System.out.println(loanRequestCount);
            System.out.println(totalLoan);
            System.out.println(totalMoney);
            loanRequestCount = applicationNumbers.size();
            if (loanRequestCount == 10 ) {
                // Distribute the totalmoney equally among the applicants
                // Assuming 10 applicants for demonstration purposes
                if (totalMoney<=2000000){

                    return "Cannot give out loans due to lack of many in the sacco account";

                }else{

                     // Select loan requests and insert them into recommended_loans table
                String selectSql = "SELECT loanAmount, paymentPeriod, memberNumber, applicationNumber FROM loanrequests";
                PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                ResultSet loanResultSet = selectStatement.executeQuery();
                
                while (loanResultSet.next()) {
                       
                    int recommendedAmount = loanResultSet.getInt("loanAmount");
                    int recomPaymentPeriod = loanResultSet.getInt("paymentPeriod");
                    String recomMemberNum = loanResultSet.getString("memberNumber");
                    int appNumber = loanResultSet.getInt("applicationNumber");
                    
                    // Loop through the application numbers and insert the distributed amounts into the recommended_loans table

                    double performance = calculateLoanPerformance(recomMemberNum);
                    if (performance<50) {

                        String updateSql = "UPDATE loanrequests SET status = ? WHERE memberNumber = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, "Your loan performance is loan ");
                updateStatement.setString(2, recomMemberNum);
                updateStatement.executeUpdate();
                updateStatement.close();
                        
                    }else{
               String updateSql = "UPDATE loanrequests SET status = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, "proccessing");
                updateStatement.executeUpdate();
                updateStatement.close();

                        String insertSql = "INSERT INTO recommended_loans (amount, paymentPeriod, memberNumber, applicationNumber) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                    insertStatement.setInt(1, recommendedAmount);
                    insertStatement.setInt(2, recomPaymentPeriod);
                    insertStatement.setString(3, recomMemberNum);
                    insertStatement.setInt(4, appNumber);
                    insertStatement.executeUpdate();
                    insertStatement.close();

                    }
                
                    
               
                }
                
                loanResultSet.close();
                selectStatement.close();
                countResultSet.close();
                countStatement.close();
                totalMoneyResultSet.close();
                totalMoneyStatement.close();
                connection.close();
    
                return "Your loan is being processed";

                }
               
            } else {
                // Insert the loan request into the database
                String applicationNumber = generateApplicationNumber();
                String sql = "INSERT INTO loanrequests (loanAmount, paymentPeriod, memberNumber, applicationNumber) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, loanAmount);
                statement.setInt(2, paymentPeriod);
                statement.setString(3, memberNumber);
                statement.setString(4, applicationNumber);
                // Execute the insert
                statement.executeUpdate();
                statement.close();
    
                countResultSet.close();
                countStatement.close();
                totalMoneyResultSet.close();
                totalMoneyStatement.close();
                connection.close();
    
                // Increment the loan request count
                loanRequestCount++;
                totalLoan = totalLoan + loanAmount;
    
                // Return the loan application number to the user
                return "Loan request processed. Loan application number: " + applicationNumber;
            }
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
                if (status == "pending") {
                    resultSet.close();
                statement.close();
                connection.close();
                
                return  status;
                    
                }else if (status == "proccessing"){
                      resultSet.close();
                statement.close();
                connection.close();
                
                return status;

                }else{

                    resultSet.close();
                  statement.close();
                   connection.close();
                    return   status;
                }
                
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
    
    
    private static String handleLoanAcceptance(String [] request) throws IOException, SQLException {
         
    
        String command = request[0];
         try {
          switch (command) {
            case "accept":
                // Perform login action
                return acceptLoan();
            case "reject":
                 return rejectLoan();
                 default:
                 return "Enter accept or reject";
        }
        } catch (NumberFormatException e) {
        return "Unknown command";
    }
        
    }

      private static String acceptLoan() throws SQLException {
         Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        // Prepare the SQL statement to insert into the references table
        try {
             String selectSql  = "SELECT * FROM loanrequests WHERE memberNumber = ? ";
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        selectStatement.setString(1, memberNumber);
        ResultSet resultSet = selectStatement.executeQuery();
        if (resultSet.next()) {
            int recommendedAmount = resultSet.getInt("loanAmount");
            int recomPaymentPeriod = resultSet.getInt("paymentPeriod");
            String recomMemberNum = resultSet.getString("memberNumber");
            int appNumber = resultSet.getInt("applicationNumber");

            // Get the current date
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(currentDate);

            // Calculate the end date using the payment period as the number of months
            java.time.LocalDate startDate = java.time.LocalDate.now();
            java.time.LocalDate endDate = startDate.plusMonths(recomPaymentPeriod);

            // Convert LocalDate to String in the desired format
            String endDateString = endDate.toString();

            String sql = "INSERT INTO loandetails (applicationNumber, amount, startDate, endDate, memberNumber) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, appNumber);
            statement.setInt(2, recommendedAmount);
            statement.setString(3, formattedDate); // Assuming "startDate" is the current date
            statement.setString(4, endDateString);
            statement.setString(5, recomMemberNum);

            statement.executeUpdate();
            statement.close();
             String insertSql = "INSERT INTO loanpayment (applicationNumber, amount, startDate, memberNumber ) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setInt(1, appNumber);
            insertStatement.setInt(2, recommendedAmount);
            insertStatement.setString(3, formattedDate); // Assuming "startDate" is the current date
            insertStatement.setString(4, recomMemberNum);
            

            insertStatement.executeUpdate();
            insertStatement.close();

           String updateSql = "UPDATE loanrequests SET status = ? WHERE memberNumber = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, "accepted");
                updateStatement.setString(2, recomMemberNum);
                updateStatement.executeUpdate();
                updateStatement.close();
                return "Your loan payment period starts at :" + formattedDate;
        }else{
            System.out.println(memberNumber);
            return "Loan not found";
        }
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "Database error";
        }
       
        
    }


    private static String rejectLoan() throws SQLException {
         Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        // Prepare the SQL statement to insert into the references table
        try {
           String updateSql = "UPDATE loanrequests SET status = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, "rejected");
                updateStatement.executeUpdate();
                updateStatement.close();
                return "You've rejected the loan. Thank you for your collaboration :" ;
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "Database error";
        }
       
        
    }
     
    private static String performCheckStatement(String dateFrom, String dateTo) {
        List<String> responses = new ArrayList<>();
    
        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Prepare the SQL statement
            String sql = "SELECT * FROM available_deposits WHERE member_number = ? AND deposit_date BETWEEN ? AND ? ORDER BY deposit_date";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberNumber);
            statement.setString(2, dateFrom);
            statement.setString(3, dateTo);
    
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                // Extract relevant data from the ResultSet
                String depositDate = resultSet.getString("deposit_date");
                int amountDeposited = resultSet.getInt("amount_deposited");
                String receiptNumber = resultSet.getString("receipt_number");
                double MyPerformanace = calculateLoanPerformance(memberNumber);
    
                // Build the response for each record
                StringBuilder response = new StringBuilder();
                response.append("Date: ").append(depositDate).append(", ");
                response.append("Amount Deposited: ").append(amountDeposited).append(", ");
                response.append("Receipt Number: ").append(receiptNumber);
                responses.add(response.toString());
                response.append("Performance: ").append(MyPerformanace);
            }
    
            resultSet.close();
            statement.close();
            connection.close();
    
        } catch (SQLException e) {
            e.printStackTrace();
            responses.add("Database error");
        }
    
        String responseString = String.join("\t", responses); // Concatenate responses with "|"
        System.out.println(responseString);
        return responseString;
    }
    

    private static double calculateLoanPerformance(String memberNum) {
        double loanPerformance = 0.0; // Initialize the loan performance

        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        
            String sql = "SELECT * FROM loanpayment WHERE memberNumber = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberNum);
            ResultSet resultSet = statement.executeQuery();
        
            if (resultSet.next()) {
                int months = resultSet.getInt("loantracker");
                int amount = resultSet.getInt("amount");
                int amountPaid = resultSet.getInt("amountPaid");
        
                String selectSql = "SELECT * FROM loanrequests WHERE memberNumber = ? AND status = ?";
                PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                selectStatement.setString(1, memberNum);
                selectStatement.setString(2, "accepted");
                ResultSet result = selectStatement.executeQuery();
        
                if (result.next()) {
                    int paymentPeriod = result.getInt("paymentPeriod");
                    // Calculate loan performance with double precision
                    loanPerformance = (((double) amountPaid /((double) amount / paymentPeriod) ) / months) * 100;
                    System.out.println(loanPerformance);
                }
        
                selectStatement.close();
            } else {
                loanPerformance = 100;
            }
        
            statement.close();
            connection.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to handle the exception more gracefully here
        }
        
        return loanPerformance;
        
    }
    

    
    
}
