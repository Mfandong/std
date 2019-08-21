package org.std.programming;

public class BubblingSort {
	public static void main(String[] args) {
		int[] arr = {1, 12, 9, 16, 4, 3};
//		bubbling(arr);
//		selectiveSort(arr);
		
		quickSort(arr, 1, arr.length-1);
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}
	
	private static void quickSort(int[] a, int low, int height) {
		if (low < height) {
            int point;
            point = partition(a, low, height);// 查找中间点
            quickSort(a, low, point - 1);// 递归排序左边
            quickSort(a, point + 1, height);// 递归排序右边
        }
	}
	
	private static int partition(int[] a, int low, int height) {
        int point = a[low];// 把中间点保存在point中
        while (low < height) {// 循环条件是只要low<height
            // 以下两个while就是核心之处
            while (low < height && a[height] >= point) {// low<height就不说了，本来是a[height]<poin就移动，
                                                        // 现在改变一下
                                                        // 当》=的时候就--；
                height--;
            }
            a[low] = a[height]; // 循环结束后，交换位置，把右边小的，交换到中间点的左边
            while (low < height && a[low] <= point) { // 相反同上， 本来是a[low]>=point
                                                        // 改变写法 改成a<=point就跳过++；
                low++;
            }
            a[height] = a[low];
        }
        a[low] = point;
        return low;
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
