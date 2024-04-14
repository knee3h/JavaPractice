package com.mmall.practice.example.mymath;

public class pow {

    public static void main(String[] args) {
        pow pow = new pow();
        double res = pow.pow(3,3,3);
        int res2 = pow.pow2(3,3,3);
        System.out.println(res);
        System.out.println(res2);

    }
    // 缺失精度
    // Math.pow()返回double, 模运算不适用double
    // 参考下面的方法
    public double pow(int base, int exponent, int mod){
        return Math.pow(base, exponent) % mod;
    }

    // 循环计算幂并取模
    public int pow2(int base, int exponent, int mod){
        int result = 1;
        while (exponent > 0){
            if (exponent % 2 ==1){
                result = (result*base) % mod;
            }
            // 防止溢出
            base = (base * base) % mod;
            exponent /= 2;
        }
        return result;
    }
}
