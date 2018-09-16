package lv.kauguri.iepirkumi.reactive;

public class Master {

    static ChainedExecutor chainedExecutor = new ChainedExecutor();

    public static void main(String[] args) {

        chainedExecutor.returns("A", Master::fun1);
        chainedExecutor.returns("B", Master::fun1);

        chainedExecutor.waitToFinish();
    }


    private static void fun1(Object data) {
        chainedExecutor.returns(data + "_1", Master::fun2);
        chainedExecutor.returns(data + "_2", Master::fun2);
    }
    private static void fun2(Object data) {
        chainedExecutor.returns(data + "_1", Master::fun3);
        chainedExecutor.returns(data + "_2", Master::fun3);
    }
    private static void fun3(Object data) {
        System.out.println(data);
    }

}
