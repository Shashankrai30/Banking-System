package BankingSystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;
    public AccountManager(Connection con, Scanner sc) {
       this.connection=con;
       this.scanner=sc;
    }

    public void credit_money(String email) {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = hashPin(scanner.nextLine());

        try {
            connection.setAutoCommit(false);
                PreparedStatement psmt = connection.prepareStatement("SELECT * FROM Accounts WHERE email = ? and security_pin = ? ");
                psmt.setString(1, email);
                psmt.setString(2, security_pin);
                ResultSet rs = psmt.executeQuery();

                if (rs.next()) {
                    String credit_query = "update Accounts SET balance = balance + ? WHERE email = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(credit_query);
                    preparedStatement.setDouble(1, amount);
                    preparedStatement.setString(2, email);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs."+amount+" credited Successfully");
                        connection.commit();
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void debit_money(String email) {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = hashPin(scanner.nextLine());

        try {
            connection.setAutoCommit(false);
            PreparedStatement psmt = connection.prepareStatement("SELECT * FROM Accounts WHERE email = ? and security_pin = ? ");
            psmt.setString(1, email);
            psmt.setString(2, security_pin);
            ResultSet rs = psmt.executeQuery();

            if (rs.next()) {
                String credit_query = "update Accounts SET balance = balance - ? WHERE email = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(credit_query);
                preparedStatement.setDouble(1, amount);
                preparedStatement.setString(2, email);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rs."+amount+" debited Successfully");
                    connection.commit();
                    return;
                } else {
                    System.out.println("Transaction Failed!");
                    connection.rollback();
                }
            }else{
                System.out.println("Invalid Security Pin!");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    public void transfer_money(String email) {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = hashPin(scanner.nextLine());
        System.out.print("Enter Receiver account number:- ");
        long account=scanner.nextLong();
        String debitquery="update accounts set balance=balance-? where email=? AND security_pin=?";
        String creditquery="update accounts set balance=balance+? where account_number=?";
        try {
            connection.setAutoCommit(false);
            PreparedStatement debit=connection.prepareStatement(debitquery);
            PreparedStatement credit=connection.prepareStatement(creditquery);
            debit.setDouble(1,amount);
            debit.setString(2,email);
            debit.setString(3,security_pin);
            credit.setDouble(1,amount);
            credit.setLong(2,account);
            int row=debit.executeUpdate();
            int row1=credit.executeUpdate();
            if(row>0 && row1>0){
                connection.commit();
            }
            else{
                connection.rollback();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void check_balance(String email) {
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = hashPin(scanner.nextLine());
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM Accounts WHERE email = ? AND security_pin = ?");
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: "+balance);
            }else{
                System.out.println("Invalid Pin!");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    private String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing the pin", e);
        }
    }
}
