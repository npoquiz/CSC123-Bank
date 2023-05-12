package com.usman.csudh.bank.core;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Bank {
	
	private static Map<Integer,Account> accounts=new TreeMap<Integer,Account>();
	private static HashMap<String, Double> exchangeRates = new HashMap<>();
	private static String file = "exchange-rate.csv";
	
	public static Account openCheckingAccount(String firstName, String lastName, String ssn, String cc, double overdraftLimit) {
		Customer c=new Customer(firstName,lastName, ssn);
		Account a=new CheckingAccount(c,overdraftLimit,cc);
		accounts.put(a.getAccountNumber(), a);
		return a;
		
	}
	
	public static void loadFile() {
		try {
       	BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null)
        {
            String substrings[] = line.split(",");
            String country = null;
            Double rate = null;
            for (int i = 0; i < substrings.length; i++)
            {
                String acc = substrings[i];
                if (acc.length() == 3)
                {
                    country = acc;
                }
                else if (acc.matches(".*\\d.*"))
                {
                    rate = Double.parseDouble(acc);
                }
            }
            if (country != null && rate != null)
            {
                exchangeRates.put(country, rate);
            }
        }
		} catch (IOException e) {
            System.err.println("**Currency file could not be loaded, Currency Conversion Service and Foreign Currency accounts are not available**");
            return;
        }
	}
		
	public static double exchangeRate(String sell, String buy, double amount) throws IOException {
         if (exchangeRates.containsKey(sell) || exchangeRates.containsKey(buy))
         {
        	 if (sell.equalsIgnoreCase("USD"))
        	 {
        		double d = exchangeRates.get(buy);
        		 return amount / d;
        	 }
        	 else if (buy.equalsIgnoreCase("USD"))
        	 {
        		 double d = exchangeRates.get(sell);
        		 return amount * d;
        	 }
        	 else
        	 {
        		 throw new IOException("\nUSD not found\n\n");
        	 }
         }
         else
         {
        	 throw new IOException("\nExchange Rate not found\n\n");
         }

	}
	

	
	public static Account openSavingAccount(String firstName, String lastName, String cc, String ssn) {
		Customer c=new Customer(firstName,lastName, ssn);
		Account a=new SavingAccount(c,cc);
		accounts.put(a.getAccountNumber(), a);
		return a;
		
	}

	
	
	public static Account lookup(int accountNumber) throws NoSuchAccountException{
		if(!accounts.containsKey(accountNumber)) {
			throw new NoSuchAccountException("\nAccount number: "+accountNumber+" nout found!\n\n");
		}
		
		return accounts.get(accountNumber);
	}
	
	public static void getAccountInfo(int accountNumber, OutputStream out) throws NoSuchAccountException, IOException {
		
		lookup(accountNumber).accountInfo(out);
		
	}
	
	public static void makeDeposit(int accountNumber, double amount) throws AccountClosedException, NoSuchAccountException{
		
		lookup(accountNumber).deposit(amount);
		
	}
	
	public static void makeWithdrawal(int accountNumber, double amount) throws InsufficientBalanceException, NoSuchAccountException {
		lookup(accountNumber).withdraw(amount);
	}
	
	public static void closeAccount(int accountNumber) throws NoSuchAccountException {
		lookup(accountNumber).close();
	}

	
	public static double getBalance(int accountNumber) throws NoSuchAccountException {
		return lookup(accountNumber).getBalance();
	}

	public static void listAccounts(OutputStream out) throws IOException{
		
		out.write((byte)10);
		Collection<Account> col=accounts.values();
		
		for (Account a:col) {
			out.write(a.toString().getBytes());
			out.write((byte)10);
		}
		
		out.write((byte)10);
		out.flush();
	}
	
	public static void printAccountTransactions(int accountNumber, OutputStream out) throws IOException,NoSuchAccountException{
		
		lookup(accountNumber).printTransactions(out);
	}
				
	
	
	
	
}
