package com.inesv.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockUtil {
	private LockUtil() {

	}

	private static LockUtil util = new LockUtil();

	public static LockUtil getUtil() {
		return util;
	}

	// 锁map
	private final static Map<String, Lock> locks = new HashMap<>();
	private final static Lock mapLock = new ReentrantLock();

	public void lock(String key) {
		Lock curLock = getLock(key);
		if (curLock == null) {
			curLock = new ReentrantLock();
			putLock(key, curLock);
		}
		curLock.lock();
	}

	public void unlock(String key) {
		Lock curLock = getLock(key);
		if (curLock == null) {
			curLock = new ReentrantLock();
			putLock(key, curLock);
		} else {
			curLock.unlock();
		}
	}

	private void putLock(String key, Lock value) {
		mapLock.lock();
		locks.put(key, value);
		mapLock.unlock();
	}

	private Lock getLock(String key) {
		try {
			mapLock.lock();
			return locks.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			mapLock.unlock();
		}
	}
}
