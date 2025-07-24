package BankingSystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.*;
import java.util.*;

public class Accounts {
    private Connection connection;
    private Scanner scanner;
    public Accounts(Connection con, Scanner sc) {
        this.connection=con;
        this.scanner=sc;
    }

    public boolean account_exist(String email) {
        String query="select * from accounts where email=(?)";
        try {
            PreparedStatement psmt=connection.prepareStatement(query);
            psmt.setString(1,email);
            ResultSet rs=psmt.executeQuery();
            if(rs.next()){
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void open_account(String email) {
        scanner.nextLine();
        User user=new User(connection,scanner);
        String name=user.get_name(email);
        int balance=0;
        System.out.print("Enter security pin: ");
        String pin = hashPin(scanner.nextLine());
        long account=generate_Acccount();
        String query="insert into accounts(account_number,full_name,email,balance,security_pin) values(?,?,?,?,?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement psmt= connection.prepareStatement(query);
            psmt.setLong(1,account);
            psmt.setString(2,name);
            psmt.setString(3,email);
            psmt.setInt(4,balance);
            psmt.setString(5,pin);
            int row= psmt.executeUpdate();
            if(row>0){
                connection.commit();
            }
            else{
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private long generate_Acccount() {
        String query="Select account_number from accounts ORDER by account_number DESC limit 1";
        try {
            Statement stmt= connection.createStatement();
            ResultSet rs= stmt.executeQuery(query);
            if(rs.next()){
                long account=rs.getLong("account_number");
                return account+1;
            }
            return 10000100;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void get_Acccountdetail(String email){
        String query="Select * from accounts where email=(?)";
        try {
            PreparedStatement psmt=connection.prepareStatement(query);
            psmt.setString(1,email);
            ResultSet rs= psmt.executeQuery();
            if(rs.next()){
                long account=rs.getLong("account_number");
                String name=rs.getString("full_name");
                String email1=rs.getString("email");
                int balance=rs.getInt("balance");
                String pin=rs.getString("security_pin");
                String pine=hashPin(pin);
                System.out.println("Name:- "+name);
                System.out.println("Email:- "+email);
                System.out.println("Account Number:- "+account);
                System.out.println("Balance:- "+balance);
                System.out.println("Security Pin:- "+pine);
            }
            return ;
        } catch (SQLException e) {
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
