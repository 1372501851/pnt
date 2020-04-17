package com.inesv.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockTest {
	public static void main(String[] args) throws Exception {
		Account account = new Account("小林子", 15.0);
		double money[] = new double[] { 5.0, 300.0, 5000.0, 50000.0, 1000.0 };
		for (int x = 0; x < 2; x++) { // 设置两个写线程
			new Thread(() -> {
				for (int y = 0; y < money.length; y++) {
					account.saveMoney(money[y]);
				}
			}, "存款用户-" + x).start();
		}
		for (int x = 0; x < 10; x++) {
			new Thread(() -> {
				System.out.println(Thread.currentThread().getName() + "、查账，账户名：" + account.getName() + "、资产总额："
						+ account.loadMoney());
			},"收款人-" + x).start();
		}
	}
}

class Account {
	private String name; // 开户名
	private double asset = 10.0; // 银行资产
	private ReadWriteLock rwLock = new ReentrantReadWriteLock(); // 读写分离

	public Account(String name, double asset) {
		this.name = name;
		this.asset = asset;
	}

	public boolean saveMoney(double money) { // 进行存款处理
		this.rwLock.writeLock().lock(); // 对写入数据进行锁定处理
		try {
			System.out.println("【（" + Thread.currentThread().getName() + "）存款-BEFORE】存款金额：" + money);
			/*TimeUnit.SECONDS.sleep(1);*/
			if (money > 0.0) { // 如果要存款肯定是要有钱
				this.asset += money;
				return true; // 存款成功
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("【（" + Thread.currentThread().getName() + "）存款-AFTER】总金额：" + this.asset);
			this.rwLock.writeLock().unlock(); // 进行解锁处理
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public double loadMoney() { // 返回当前的资金
		try {
			this.rwLock.readLock().lock();
			return this.asset;
		} finally {
			this.rwLock.readLock().unlock();
		}
	}
}
