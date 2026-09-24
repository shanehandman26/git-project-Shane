import java.io.*;

public class Git {
    public void init() {
        int num = 0;
        File gitNew = new File("gitNew");
        if (!gitNew.exists()) {
            gitNew.mkdir();
            num++;
        }
        File INDEX = new File("gitNew");
        if (!INDEX.exists()) {
            try {
                INDEX.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            num++;
        }
        File objects = new File("gitNew");
        if (!objects.exists()) {
            objects.mkdir();
            num++;
        }

        File HEAD = new File("git");
        if (HEAD.exists()) {
            try {
                HEAD.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            num++;
        }
        if (num < 4) {
            System.out.println("Git Repository Created");
        } else {
            System.out.println("Git Repository Already Exists");
        }

    }
}
