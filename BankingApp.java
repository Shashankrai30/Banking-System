package BankingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    static String url="jdbc:mysql://localhost:3306/banking";
    static String username="root";
    static String password="@Sha1462";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded Successfully!!!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Connection con= DriverManager.getConnection(url,username,password);
            Scanner sc=new Scanner(System.in);

            User user=new User(con,sc);
            Accounts accounts=new Accounts(con,sc);
            AccountManager accountManager=new AccountManager(con,sc);
            String email;
            while(true){
                System.out.println("*****************************************");
                System.out.println("**** Welcome to kucchu pucchu bank   ****");
                System.out.println("* 1. Register                        ****");
                System.out.println("* 2. Login                           ****");
                System.out.println("* 3. Exit                           ****");
                System.out.println("*****************************************");
                int choice=sc.nextInt();
                switch(choice){
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email=user.login();
                        if(email==null){
                            break;
                        }
                        else {
                            System.out.println("User logged In!!!");
                            if(accounts.account_exist(email)){
                                int choice2=0;
                                while(choice2!=5){
                                    System.out.println("********* Banking Menu *********");
                                    System.out.println("* 1. Credit Money               *");
                                    System.out.println("* 2. Debit Money                *");
                                    System.out.println("* 3. Transfer Money             *");
                                    System.out.println("* 4. Check Balance              *");
                                    System.out.println("* 5. Exit                       *");
                                    System.out.println("*********************************");

                                    choice2= sc.nextInt();
                                    switch (choice2){
                                        case 1:
                                            accountManager.credit_money(email);
                                            break;
                                        case 2:
                                            accountManager.debit_money(email);
                                            break;
                                        case 3:
                                            accountManager.transfer_money(email);
                                            break;
                                        case 4:
                                            accountManager.check_balance(email);
                                            break;
                                        case 5:
                                            break;
                                    }
                                }
                            }
                            else{
                                System.out.println("* 1.Open a new Account  ****");
                                System.out.println("* 2.Exit                ****");
                                int choice1=sc.nextInt();
                                switch(choice1){
                                    case 1:
                                        accounts.open_account(email);
                                        System.out.println("Account Created Successfully!!!");
                                        accounts.get_Acccountdetail(email);
                                        break;
                                    case 2:
                                        break;
                                }
                                break;
                            }
                        }
                    case 3:
                        System.out.println("Thanks for using!!!");
                        System.out.println("Exiting System!!!");
                        return;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
