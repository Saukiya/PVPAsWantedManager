package vip.foxcraft.pvpaswantedmanager;

public class MoneyOperationResult
{
	public boolean status;
	public double amount;
	
	public MoneyOperationResult(boolean status, double amount)
	{
		this.status = status;
		this.amount = amount;
	}
}
