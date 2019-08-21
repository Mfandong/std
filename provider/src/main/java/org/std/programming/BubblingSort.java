package org.std.programming;

public class BubblingSort {
	public static void main(String[] args) {
		int[] arr = {1, 2, 9, 16, 4, 3};
//		bubbling(arr);
		selectiveSort(arr);
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}
	
	
	/**
	 *  选择排序
	 * @param arr
	 */
	private static void selectiveSort(int[] arr){
		int tem = -1;
		for (int i = 0; i < arr.length; i++) {
			int minIndex = i;
			for (int j = i; j < arr.length; j++) {
				if (arr[minIndex] > arr[j]){
					minIndex = j;
				}
			}
			tem = arr[i];
			arr[i] = arr[minIndex];
			arr[minIndex] = tem;
		}
	}
	
	/**
	 * 冒泡排序
	 * @param arr
	 */
	private static void bubbling(int[] arr){
		int tem = -1;
		for (int i = 0; i < arr.length; i++) {
			for (int j = i; j < arr.length; j++){
				if (arr[i] > arr[j]){
					tem = arr[i];
					arr[i] = arr[j];
					arr[j] = tem;
				}
			}
		}
	}
}
