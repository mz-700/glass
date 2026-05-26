package org.tbc.glass;

public class Mz700Charset {

	private Mz700Charset() {
	}

	public static byte encode(char character) {
		return encode((int)character);
	}

	public static byte encode(int value) {
		switch (value) {
		case 'a': return (byte)0xA1;
		case 'b': return (byte)0x9A;
		case 'c': return (byte)0x9F;
		case 'd': return (byte)0x9C;
		case 'e': return (byte)0x92;
		case 'f': return (byte)0xAA;
		case 'g': return (byte)0x97;
		case 'h': return (byte)0x98;
		case 'i': return (byte)0xA6;
		case 'j': return (byte)0xAF;
		case 'k': return (byte)0xA9;
		case 'l': return (byte)0xB8;
		case 'm': return (byte)0xB3;
		case 'n': return (byte)0xB0;
		case 'o': return (byte)0xB7;
		case 'p': return (byte)0x9E;
		case 'q': return (byte)0xA0;
		case 'r': return (byte)0x9D;
		case 's': return (byte)0xA4;
		case 't': return (byte)0x96;
		case 'u': return (byte)0xA5;
		case 'v': return (byte)0xAB;
		case 'w': return (byte)0xA3;
		case 'x': return (byte)0x9B;
		case 'y': return (byte)0xBD;
		case 'z': return (byte)0xA2;
		default: return (byte)value;
		}
	}

	public static byte encodeDisplay(int value) {
		switch (value) {
		case ' ': return 0x00;
		case 'A': return 0x01;
		case 'B': return 0x02;
		case 'C': return 0x03;
		case 'D': return 0x04;
		case 'E': return 0x05;
		case 'F': return 0x06;
		case 'G': return 0x07;
		case 'H': return 0x08;
		case 'I': return 0x09;
		case 'J': return 0x0A;
		case 'K': return 0x0B;
		case 'L': return 0x0C;
		case 'M': return 0x0D;
		case 'N': return 0x0E;
		case 'O': return 0x0F;
		case 'P': return 0x10;
		case 'Q': return 0x11;
		case 'R': return 0x12;
		case 'S': return 0x13;
		case 'T': return 0x14;
		case 'U': return 0x15;
		case 'V': return 0x16;
		case 'W': return 0x17;
		case 'X': return 0x18;
		case 'Y': return 0x19;
		case 'Z': return 0x1A;
		case 'a': return (byte)0x81;
		case 'b': return (byte)0x82;
		case 'c': return (byte)0x83;
		case 'd': return (byte)0x84;
		case 'e': return (byte)0x85;
		case 'f': return (byte)0x86;
		case 'g': return (byte)0x87;
		case 'h': return (byte)0x88;
		case 'i': return (byte)0x89;
		case 'j': return (byte)0x8A;
		case 'k': return (byte)0x8B;
		case 'l': return (byte)0x8C;
		case 'm': return (byte)0x8D;
		case 'n': return (byte)0x8E;
		case 'o': return (byte)0x8F;
		case 'p': return (byte)0x90;
		case 'q': return (byte)0x91;
		case 'r': return (byte)0x92;
		case 's': return (byte)0x93;
		case 't': return (byte)0x94;
		case 'u': return (byte)0x95;
		case 'v': return (byte)0x96;
		case 'w': return (byte)0x97;
		case 'x': return (byte)0x98;
		case 'y': return (byte)0x99;
		case 'z': return (byte)0x9A;
		case '_': return 0x3C;
		case '{': return (byte)0xBC;
		case '}': return 0x40;
		case '?': return 0x49;
		case ':': return 0x4F;
		case '[': return 0x52;
		case ']': return 0x54;
		case '@': return 0x55;
		case '<': return 0x51;
		case '>': return 0x57;
		case '!': return 0x61;
		case '"': return 0x62;
		case '\'': return 0x67;
		case '|': return 0x79;
		case '^': return 0x50;
		case '#': return 0x63;
		case '$': return 0x64;
		case '&': return 0x66;
		case '*': return 0x6B;
		case '(': return 0x68;
		case ')': return 0x69;
		case '-': return 0x2A;
		case '+': return 0x6A;
		case '=': return 0x2B;
		case '/': return 0x2D;
		case ',': return 0x2F;
		case '.': return 0x2E;
		case ';': return 0x2C;
		case '0': return 0x20;
		case '1': return 0x21;
		case '2': return 0x22;
		case '3': return 0x23;
		case '4': return 0x24;
		case '5': return 0x25;
		case '6': return 0x26;
		case '7': return 0x27;
		case '8': return 0x28;
		case '9': return 0x29;
		default: return (byte)value;
		}
	}

}