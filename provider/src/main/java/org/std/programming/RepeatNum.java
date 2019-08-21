package org.std.programming;
/**
 * 用面向对象的方法求出数组中重复 value 的个数
 * 这道题要求
 * 1 出现： 1 次
 * 3 出现： 2 次
 * 8 出现： 3 次
 * 2 出现： 4 次
 * int[] arr = {1,4,1,4,2,5,4,5,8,7,8,77,88,5,4,9,6,2,4,1,5};
 */
public class RepeatNum {
	public static void main(String[] args) {
		int[] arr = {1,4,1,4,2,5,4,5,8,7,8,77,88,5,4,9,6,2,4,1,5};
		int[] count = new int[89];
		for (int i = 0; i < arr.length; i++) {
			count[arr[i]]++;
		}
		for (int i = 0; i < count.length; i++) {
			if (count[i] != 0){
				System.out.println(i + "出现了：" + count[i] + "次");
			}
		}
	}
}
