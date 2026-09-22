import java.io.*;

public class Git {
    public static void main(String[] args) {
        this.init();

    }

    public void init() throws IOException {
        int num = 0;
        if (!gitNew.exists()) {
            gitNew.mkdir();
            num++;
        }
        if (!INDEX.exists()) {
            File INDEX = new File("gitNew");
            INDEX.createNewFile();
            num++;
        }
        if (!objects.exists()) {
            num++;
        }

        if (HEAD.exists()) {
            File HEAD = new File("git");
            HEAD.createNewFile();
            num++;
        }
        if (num < 4) {
            System.out.println("Git Repository Created");
        } else {
            System.out.println("Git Repository Already Exists");
        }

    }
}
