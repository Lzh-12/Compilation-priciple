public class Quaternion {
    String op; // 操作符
    String argv1; // 操作数1
    String argv2; // 操作数2
    String result; // 运算结果

    public Quaternion() {
    }

    public Quaternion(String op, String argv1, String argv2, String result) {
        this.op = op;
        this.argv1 = argv1;
        this.argv2 = argv2;
        this.result = result;
    }
}
