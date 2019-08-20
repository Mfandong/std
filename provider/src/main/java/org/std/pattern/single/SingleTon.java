package org.std.pattern.single;

public class SingleTon {

	private SingleTon(){
		if(SingleTon.SingleTonHolder.singleTon != null){
			throw new RuntimeException("单例模式，通过反射创建对象违法");
		}
	}
	
	private static class SingleTonHolder{
		private static SingleTon singleTon = new SingleTon();
	}
	
	public static SingleTon getInstance(){
		return SingleTonHolder.singleTon;
	}
	
	public static void main(String[] args) {
		System.out.println(SingleTon.getInstance()==SingleTon.getInstance());
		try {
			Class<?> singleTonClass = Class.forName("org.std.pattern.single.SingleTon");
			try {
				Object singleTon = singleTonClass.newInstance();
				System.out.println(singleTon);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
