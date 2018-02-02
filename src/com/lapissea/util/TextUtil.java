package com.lapissea.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.function.Function;

import static com.lapissea.util.UtilL.*;

public class TextUtil{
	
	private static final Map<Class<Object>, Function<Object, String>> CUSTOM_TO_STRINGS=new HashMap<>();
	
	public static <T> void __REGISTER_CUSTOM_TO_STRING(Class<T> type, Function<T, String> funct){
		CUSTOM_TO_STRINGS.put((Class<Object>)type, (Function<Object, String>)funct);
	}
	
	static{
		__REGISTER_CUSTOM_TO_STRING(FloatBuffer.class, buffer->{
			StringBuilder print=new StringBuilder("FloatBuffer[");
			for(int i=0;i<buffer.limit();){
				print.append(buffer.get(i));
				if(++i<buffer.capacity()) print.append(", ");
			}
			print.append(']');
			return print.toString();
		});
		__REGISTER_CUSTOM_TO_STRING(IntBuffer.class, buffer->{
			StringBuilder print=new StringBuilder("IntBuffer[");
			for(int i=0;i<buffer.limit();){
				print.append(buffer.get(i));
				if(++i<buffer.capacity()) print.append(", ");
			}
			print.append(']');
			return print.toString();
		});
		__REGISTER_CUSTOM_TO_STRING(ByteBuffer.class, buffer->{
			StringBuilder print=new StringBuilder("ByteBuffer[");
			for(int i=0;i<buffer.limit();){
				print.append(buffer.get(i));
				if(++i<buffer.capacity()) print.append(", ");
			}
			print.append(']');
			return print.toString();
		});
	}
	
	public static String toStringArray(Object[] arr){
		if(arr==null) return "null";
		StringBuilder print=new StringBuilder("[");
		
		for(int i=0;i<arr.length;i++){
			Object a=arr[i];
			if(isArray(a)) print.append(unknownArrayToString(a));
			else print.append(toString(a));
			if(i!=arr.length-1) print.append(", ");
		}
		
		return print.append("]").toString();
	}
	
	private static String unknownArrayToString(Object arr){
		if(arr==null) return "null";
		if(arr instanceof boolean[]) return Arrays.toString((boolean[])arr);
		if(arr instanceof float[]) return Arrays.toString((float[])arr);
		if(arr instanceof byte[]) return Arrays.toString((byte[])arr);
		if(arr instanceof int[]) return Arrays.toString((int[])arr);
		if(arr instanceof long[]) return Arrays.toString((long[])arr);
		if(arr instanceof short[]) return Arrays.toString((short[])arr);
		if(arr instanceof char[]) return Arrays.toString((char[])arr);
		if(arr instanceof double[]) return Arrays.toString((double[])arr);
		if(arr instanceof Object[]) return toStringArray((Object[])arr);
		return "ERR: "+arr;
	}
	
	public static String toString(Object... objs){
		if(objs==null) return "null";
		StringBuilder print=new StringBuilder();
		
		for(int i=0;i<objs.length;i++){
			Object a=objs[i];
			if(isArray(a)) print.append(unknownArrayToString(a));
			else print.append(toString(a));
			if(i!=objs.length-1) print.append(" ");
		}
		
		return print.toString();
	}
	
	public static String toString(Object obj){
		if(obj==null) return "null";
		
		StringBuilder print=new StringBuilder();
		
		if(isArray(obj)) print.append(unknownArrayToString(obj));
		else{
			Class                    type=obj.getClass();
			Function<Object, String> fun =CUSTOM_TO_STRINGS.get(type);
			if(fun!=null) print.append(fun.apply(obj));
			else{
				print.append(CUSTOM_TO_STRINGS.entrySet()
				                              .stream()
				                              .filter(e->instanceOf(type, e.getKey()))
				                              .findFirst()
				                              .map(e->e.getValue().apply(obj))
				                              .orElseGet(obj::toString));
			}
		}
		
		return print.toString();
	}
	
	public static String plural(String word, int count){
		return count==1?word:plural(word);
	}
	
	public static String plural(String word){
		switch(word.charAt(word.length()-1)){
		case 's':
		case 'x':
			return word+"es";
		case 'h':{
			switch(word.charAt(word.length()-2)){
			case 's':
			case 'c':
				return word+"es";
			}
		}
		default:
			return word+"s";
		}
	}
	
	public static String stringFill(int length, char c){
		char[] ch=new char[length];
		Arrays.fill(ch, c);
		return new String(ch);
	}
	
	public static String wrappedString(Object obj){
		return wrappedString(toString(obj).split("\n"));
	}
	
	public static String wrappedString(String... lines){
		if(lines==null||lines.length==0) return "";
		
		StringBuilder result =new StringBuilder();
		StringBuilder padding=new StringBuilder();
		
		int width=Arrays.stream(lines).mapToInt(String::length).max().orElse(0)+2;
		result.append('/');
		for(int i=0;i<width;i++) result.append('-');
		result.append("\\\n");
		
		for(String line : lines){
			int diff=width-line.length();
			int l   =diff/2;
			int r   =diff-l;
			
			result.append('|');
			
			if(padding.length()>l) padding.setLength(l);
			else while(padding.length()<l) padding.append(' ');
			result.append(padding);
			
			result.append(line);
			
			if(padding.length()>r) padding.setLength(l);
			else while(padding.length()<r) padding.append(' ');
			result.append(padding);
			
			result.append("|\n");
		}
		
		result.append('\\');
		for(int i=0;i<width;i++) result.append('-');
		result.append('/');
		
		return result.toString();
	}
	
	public static List<String> wrapLongString(String str, int width){
		List<String>  result=new ArrayList<>(2);
		StringBuilder line  =new StringBuilder();
		
		
		for(int i=0;i<str.length();i++){
			char c=str.charAt(i);
			
			if(line.length()>=width){
				
				if(Character.isWhitespace(c)){
					
					int lastSpace=line.length()-1;
					while(lastSpace>0&&Character.isWhitespace(line.charAt(lastSpace-1))) lastSpace--;
					while(lastSpace>0&&!Character.isWhitespace(line.charAt(lastSpace-1))) lastSpace--;
					
					if(lastSpace!=0){
						if(line.length()-lastSpace>width) return wrapLongString(str, line.length()-lastSpace);
						
						String lastWord=line.substring(lastSpace);
						line.setLength(lastSpace);
						result.add(line.toString());
						line.setLength(0);
						line.append(lastWord);
					}

//					result.add(line.toString());
//					line.setLength(0);
//					while(i+1<str.length()&&Character.isWhitespace(c=str.charAt(i+1))&&c!='\n') i++;
//					continue;
				}
			}
			line.append(c);
			if(c=='\n'){
				result.add(line.toString());
				line.setLength(0);
			}
		}
		result.add(line.toString());
		
		return result;
	}
	
	public static String join(Collection<?> data, String s){
		Iterator<?> i=data.iterator();
		if(!i.hasNext()) return "";
		
		StringBuilder result=new StringBuilder();
		
		while(true){
			result.append(i.next());
			if(i.hasNext()) result.append(s);
			else return result.toString();
		}
	}
	
	public static String firstToLoverCase(String string){
		if(string==null||string.isEmpty()) return string;
		
		char c[]=string.toCharArray();
		c[0]=Character.toLowerCase(c[0]);
		return new String(c);
	}
	
	public static String firstToUpperCase(String string){
		if(string==null||string.isEmpty()) return string;
		
		char c[]=string.toCharArray();
		c[0]=Character.toUpperCase(c[0]);
		return new String(c);
	}
	
	private static final char[] HEX_ARRAY="0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] bytes){
		char[] hexChars=new char[bytes.length*2];
		for(int j=0;j<bytes.length;j++){
			int v=bytes[j]&0xFF;
			hexChars[j*2]=HEX_ARRAY[v>>>4];
			hexChars[j*2+1]=HEX_ARRAY[v&0x0F];
		}
		return new String(hexChars);
	}
	
	public static byte[] hexStringToByteArray(String s){
		int    len =s.length();
		byte[] data=new byte[len/2];
		for(int i=0;i<len;i+=2){
			data[i/2]=(byte)((Character.digit(s.charAt(i), 16)<<4)+Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
}
