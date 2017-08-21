package com.posin.fuctiontest.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class ByteUtils {
	static final String TAG = "Posin.Utils";
	
	public static char bitsToHex(int bit) {
		if(bit >= 0 && bit <= 9) {
			return (char)((int)'0'+bit);
		} else if(bit >= 10 && bit <= 15) {
			return (char)((int)'A'+bit-10);
		}
		return '-';
	}

	public static String bytesToHex(byte[] bs) {
		if(bs == null)
			return "null";

		StringBuilder sb = new StringBuilder();
		for(byte b : bs ) {
			sb.append(bitsToHex((b>>4)&0x0F));
			sb.append(bitsToHex(b&0x0F));
			sb.append(" ");
		}
		return sb.toString();
	}

	public static String bytesToHex(byte[] bs, int start, int count) {
		if(bs == null)
			return "null";

		StringBuilder sb = new StringBuilder();
		for(int i=start; i<count; i++) {
			final byte b = bs[i];
			sb.append(bitsToHex((b>>4)&0x0F));
			sb.append(bitsToHex(b&0x0F));
			sb.append(" ");
		}
		
		if(sb.length() > 0)
			return sb.toString();

		return "null";
	}

	public static int valueFromHex(char hex) throws Exception {
		if(hex >= '0' && hex <= '9')
			return (int) (hex-'0');
		if(hex >= 'a' && hex <= 'f')
			return (int) (hex-'a'+10);
		if(hex >= 'A' && hex <= 'F')
			return (int) (hex-'A'+10);
		throw new Exception("failed to convert hex.");
	}
	
	public static byte[] bytesFromHex(String str, int maxSize) throws Throwable {
		ByteBuffer bb = ByteBuffer.allocate(maxSize);
		// fix : order bug
		bb.order(ByteOrder.LITTLE_ENDIAN);

		char[] src = str.toCharArray();
		//mLogger.addLog(Utils.bytesToHex(src));
		
		for(int i=0; i<src.length; i++) {
			if(src[i] == 0x20)
				continue;
			if(i+1 < src.length) {
				int hi = valueFromHex(src[i]);
				int lo = valueFromHex(src[i+1]);
				bb.put((byte) (hi*16+lo));
				i++;
			} else {
				throw new Exception("failed to convert hex string.");
			}
		}
		
		if(bb.hasArray())
			return bb.array();
		return null;
	}

}
