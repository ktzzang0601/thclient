
import java.sql.*;
import java.util.ArrayList;

/***
 *  Client Application
 *  문제에 조건 및 동기화 부분은 대부분 해결함
 *  MySQL, Socket 통신, Gson 이용
 *  서버 부하를 염려하여 한번 실행에 100개만 생성되도록 임의 제한함
 *  서버부터 실행 후 클라이언트 실행을 권장함
 *
 * */


public class Main {

    public static void main(String[] args) throws Exception {

        syncData(); //동기화 체크 - 이전 작업에서 전송에 실패한 파일들이 있는지 검색 후 전송

        System.out.println("Start Thread...");
        Runnable rCalNum = new CalNum();
        Runnable rSendList = new SendList();

        Thread tCalNum = new Thread(rCalNum);
        Thread tSendList = new Thread(rSendList);

        tCalNum.start();
        tSendList.start();
    }

    //0.1초마다 랜덤한 정수(1~100으로 가정)와 해당 시간을 생성 및 Client DB에 저장
    public static class CalNum implements Runnable {

        int count = 0;

        @Override
        public void run() {
            SqlCon sqlCon = new SqlCon();

            for (int i =0; i < 100; i++){
                try {
                    int num = (int) (Math.random()*100)+1;
                    String currentTime = new Timestamp(System.currentTimeMillis()).toString();
                    Thread.sleep(100);
                    sqlCon.insert(num, currentTime);
                    System.out.println(num);
                    System.out.println(currentTime);
                }catch (InterruptedException e){
                    e.printStackTrace();
                    System.out.println("계산 도중에 오류가 발생하였습니다.");
                }
            }
        }
    }

    //1초마다 생성된 정수들을 서버에 전송
    public static class SendList implements Runnable {

        ArrayList<Data> dataList = new ArrayList();

        boolean disconnected = false;

        @Override
        public void run() {
            boolean sendComplete = false;
            SqlCon sqlCon = new SqlCon();

            for(int i =0; i < 12; i++){
                try {
                    Thread.sleep(1000);

                    //Client는 살아있지만, Server가 죽은 경우를 위한 동기화 체크 진행
                    //전송작업을 하기전 이전에 실패내역이 있음을 확인하고 있다면, 전송되지 않은 자료를 전송.
                    //서버가 계속 죽어있다면, 난수는 계속 생성되지만, 동기화가 성공할떄까지 추가적인 전송은 하지 않음.
                    if(disconnected == true){
                        sendComplete = syncData();
                        if(sendComplete) disconnected = false;
                        else continue;
                    }

                    dataList = sqlCon.checkList();
                    ConnectServer connectServer = new ConnectServer();
                    sendComplete = connectServer.connectToServer(dataList);
                    if(sendComplete){
                        sqlCon.update(dataList);
                    }else{
                        System.out.println("전송에 실패했습니다. 서버쪽 통신을 확인해주세요.");
                        disconnected = true;
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                    disconnected = true;
                    System.out.println("전송 작업 시도중에 오류가 발생하였습니다.");
                }
            }
        }
    }

    //동기화 체크
    public static boolean syncData() {
        boolean sendComplete = false;

        System.out.println("Check Syncronized data...");
        ArrayList<Data> dataList = new ArrayList();
        SqlCon sqlCon = new SqlCon();
        dataList = sqlCon.checkList();
        if(dataList.size() > 0){
            ConnectServer connectServer = new ConnectServer();
            sendComplete = connectServer.connectToServer(dataList);
            if(sendComplete){
                sqlCon.update(dataList);
                System.out.println("Syncronizeing Complete...");
            }else {
                System.out.println("Syncronizeing Fail...Check Your Network Service");
            }
        }else{
            System.out.println("Already Syncronized...");
        }
        return sendComplete;
    }

}
