package BankingSystem;

import java.sql.*;
import java.util.*;

public class User {
    private Connection connection;
    private Scanner scanner;
    public User(Connection con, Scanner sc) {
        this.connection=con;
        this.scanner=sc;
    }

    public void register() {
        scanner.nextLine();
        System.out.print("Enter Your Name: ");
        String name=scanner.nextLine();
        System.out.print("Enter Email ID: ");
        String email=scanner.nextLine();
        if(user_exist(email)){
            System.out.println("Email already registered!!");
        }
        System.out.print("Enter Password: ");
        String password=scanner.nextLine();
        String query="insert into user(full_name,email,password) values(?,?,?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement psmt=connection.prepareStatement(query);
            psmt.setString(1,name);
            psmt.setString(2,email);
            psmt.setString(3,password);
            int row=psmt.executeUpdate();
            if(row>0){
                connection.commit();
                System.out.println("Registered successfully!!!");
            }
            else{
                connection.rollback();
                System.out.println("Registration Failed!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean user_exist(String email) {
        String query="select * from user where email='"+email+"'";
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs= stmt.executeQuery(query);
            if(rs.next()){
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String login() {
        scanner.nextLine();
        System.out.print("Enter Email address: ");
        String email=scanner.nextLine();
        if(!user_exist(email)){
            System.out.println("User not registered!!!");
            return null;
        }
        System.out.print("Enter Password: ");
        String password=scanner.nextLine();
        String query="select * from user where email=(?) AND password=(?)";
        try {
            PreparedStatement psmt=connection.prepareStatement(query);
            psmt.setString(1,email);
            psmt.setString(2,password);
            ResultSet rs=psmt.executeQuery();
            if(rs.next()){
                return email;
            }
            else{
                System.out.println("Incorrect Email or Password!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String get_name(String email) {
        String query="select full_name from user where email='"+email+"'";
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs= stmt.executeQuery(query);
            if(rs.next()){
                return rs.getString("full_name");
            }
            return "";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
