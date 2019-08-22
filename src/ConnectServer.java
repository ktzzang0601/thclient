import com.google.gson.Gson;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ConnectServer {

    java.net.Socket socket = null;
    String serverIp = "127.0.0.1";
    int port = 7777;

    public ConnectServer() {

    }

    public boolean connectToServer(ArrayList list) {
        boolean complete = false;

        try {
            socket = new java.net.Socket();
            socket.connect(new InetSocketAddress(serverIp, port));
            System.out.println("서버에 연결 성공...");

            //Json Data 로 파싱후 전송
            String message = new Gson().toJson(list);
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(message);

            oos.flush();
            oos.close();
            os.flush();
            os.close();
            complete = true;
            System.out.println("전송 완료...");

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("서버에 연결할 수 없습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("전송에 실패하였습니다. 이후에 다시 시도해주세요.");
        }

        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return complete;
    }

}