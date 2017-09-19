package com.posin.functiontest.util;


import com.posin.device.SerialPort;

import java.io.IOException;


public class LedCustomerDisplay {

	private final SerialPort mSerialPort;
	
	public LedCustomerDisplay(String port) throws Throwable {
		mSerialPort = SerialPort.open(port, true);
	}
	
	public void close() {
		mSerialPort.close();
	}

	public void showValue(String value, int type) throws IOException {
		value += "\r";
		mSerialPort.getOutputStream().write(value.getBytes());
		
		byte[] cmd = { 0x1B, 0x73, (byte) type };
		mSerialPort.getOutputStream().write(cmd);
	}
	
	public void showPrice(String value) throws IOException {
		showValue(value, 0x31);
	}
	
	public void showTotal(String value) throws IOException {
		showValue(value, 0x32);
	}
	
	public void showPayment(String value) throws IOException {
		showValue(value, 0x33);
	}
	
	public void showChange(String value) throws IOException {
		showValue(value, 0x34);
	}
	
	public void clear() throws IOException {
		mSerialPort.getOutputStream().write(0x0C);
	}

	public void reset() throws IOException {
		byte[] cmd = { 0x1B, 0x40 };
		mSerialPort.getOutputStream().write(cmd);
	}
}
