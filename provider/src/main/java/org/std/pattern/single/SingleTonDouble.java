package org.std.pattern.single;

public class SingleTonDouble {

	private static volatile SingleTonDouble singleTonDouble;
	
	private SingleTonDouble(){
		
	}
	
	public static SingleTonDouble getInstance(){
		if (singleTonDouble == null){
			synchronized (SingleTonDouble.class) {
				if (singleTonDouble == null){
					singleTonDouble = new SingleTonDouble();
				}
			}
		}
		return singleTonDouble;
	}
	
	public static void main(String[] args) {
		System.out.println(SingleTonDouble.getInstance() == SingleTonDouble.getInstance());
	}
}
