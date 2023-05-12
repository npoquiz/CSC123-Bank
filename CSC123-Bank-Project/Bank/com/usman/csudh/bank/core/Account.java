package com.usman.csudh.bank.core;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.usman.csudh.util.UniqueCounter;

public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String accountName;
	private Customer accountHolder;
	private String currency;
	private ArrayList<Transaction> transactions;
	
	private boolean open=true;
	private int accountNumber;

	protected Account(String name, Customer customer, String cc) {
		accountName=name;
		accountHolder=customer;
		currency = cc;
		transactions=new ArrayList<Transaction>();
		accountNumber=UniqueCounter.nextValue();
	}
	
	
	public String getAccountName() {
		return accountName;
	}

	public Customer getAccountHolder() {
		return accountHolder;
	}

	public double getBalance() {
		double workingBalance=0;
		
		for(Transaction t: transactions) {
			if(t.getType()==Transaction.CREDIT)workingBalance+=t.getAmount();
			else workingBalance-=t.getAmount();
		}
		
		return workingBalance;
	}
	
	
	public void deposit(double amount)  throws AccountClosedException{
		double balance=getBalance();
		if(!isOpen()&&balance>=0) {
			throw new AccountClosedException("\nAccount is closed with positive balance, deposit not allowed!\n\n");
		}
		transactions.add(new Transaction(Transaction.CREDIT,amount));
	}
	
	
	
	
	public void withdraw(double amount) throws InsufficientBalanceException {
			
		double balance=getBalance();
			
		if(!isOpen()&&balance<=0) {
			throw new InsufficientBalanceException("\nThe account is closed and balance is: "+balance+"\n\n");
		}
		
		transactions.add(new Transaction(Transaction.DEBIT,amount));
	}
	
	public void close() {
		open=false;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public int getAccountNumber() {
		return accountNumber;
	}

	public String toString() {
		String aName=accountNumber+"("+accountName+")"+" : "+accountHolder.toString()+ " : " + currency + " : " +getBalance()+" : "+(open?"Account Open":"Account Closed");
		return aName;
	}
	 
	public void printTransactions(OutputStream out) throws IOException {
		
		out.write("\n\n".getBytes());
		out.write("------------------\n".getBytes());
	
		for(Transaction t: transactions) {
			out.write(t.toString().getBytes());
			out.write((byte)10);
		}
		out.write("------------------\n".getBytes());
		out.write(("Balance: "+getBalance()+"\n\n\n").getBytes());
		out.flush();
		
	}
	
	public void accountInfo(OutputStream out) throws IOException {
		String accNum = Integer.toString(this.accountNumber);
		String name = this.accountHolder.getFirstName() + " " + this.accountHolder.getLastName();
		String ssn = this.accountHolder.getSSN();
		String cur = this.currency;
		
		out.write(("Account Number: " + accNum + "\n" + "Name: " + name + "\n" + "SSN: " + ssn + "\n" + "Currency: " + cur + "\n  ").getBytes());
		
	}
	
}
