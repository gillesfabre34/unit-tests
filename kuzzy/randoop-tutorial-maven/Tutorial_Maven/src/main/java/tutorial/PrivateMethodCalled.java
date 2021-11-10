package tutorial;

public class PrivateMethodCalled {

    private int privateMethod(int a) {
        if (a > 5) {
            return 2;
        } else {
            return 3;
        }
    }

    public int publicMethod(int a) {
        return privateMethod(a);
    }

}
