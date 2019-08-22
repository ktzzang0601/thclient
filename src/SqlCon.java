import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class SqlCon {

    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;
    private String server = "localhost";
    private String database = "test";
    private String userName = "root";
    private String password = "Osc123456";

    public SqlCon() {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?useSSL=false", userName, password);

        } catch (SQLException e) {
            System.out.println("Connection Fail..");
            e.printStackTrace();
        }
    }

    //생성된 랜덤한 정수를 저장
    public void insert(int number, String time) {
        String sql = "INSERT INTO client VALUES (0,?,?,?)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, number);
            pstmt.setString(2, time);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException Occured..");
            e.printStackTrace();
        }
    }

    //저장된 정수들 중 전송되지 않은 항목들을 체크
    public ArrayList<Data> checkList() {
        ArrayList<Data> dataList = new ArrayList<>();

        String sql = "SELECT * FROM client WHERE send = false";

        try {
            stmt = conn.createStatement();
            stmt.executeQuery(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getInt("id"));
                data.setNumber(rs.getInt("number"));
                data.setTime(rs.getString("time"));
                data.setSend(rs.getBoolean("send"));
                dataList.add(data);
            }
        } catch (SQLException e) {
            System.out.println("SQLException Occured..");
            e.printStackTrace();
        }
        return dataList;
    }

    //전송완료 후 전송되었다는 정보를 업데이트함
    public void update(ArrayList<Data> dataList) {
        String sql = "UPDATE client SET send=true where id=?";

        try {
            for (Data item : dataList) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, item.getId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("SQLException UPDATE ERROR..");
            e.printStackTrace();
        }
    }

}
