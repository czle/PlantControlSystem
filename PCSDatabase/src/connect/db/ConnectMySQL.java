package connect.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ConnectMySQL {

	private static ConnectMySQL instance = new ConnectMySQL();

	public static ConnectMySQL getInstance() {
		return instance; // �ν��Ͻ� ������.
	}

	public ConnectMySQL() { // ������.
	}

	String ip = "192.168.0.10";
	// LocalHost IP �ּ�, cmd ���� ipconfig �� �ϱ�ٶ�.
	String url = "jdbc:mysql://" + ip
			+ ":3306/pcs?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERTTONULL&serverTimezone=GMT";
	// MYSQL�� �ּ�.
	// mysql�� �����ϱ� ���Ѱ�, ��Ʈ �ּ� �Ŀ� ��Ű�� �̸��� ����, �����ڵ�, ������ڵ�, �ð����� ���� ����߸�
	// ����� mysql�� �����ȴ�. 
	String dbid = "root";
	// ������ ���̽� ���̵�
	String dbpw = "1234";
	// ������ ���̽� ��й�ȣ.
	String DBTABLE = "PCS.PCS";
	String USERTABLE = "PCS.USERTBL";
	String DIARYTABLE = "PCS.DIARY";
	// �� ��� ���̺���� �̸�
	
	String sqlRead;
	String sqlLogin;
	String sqlTotalGraph;
	String sqlTempGraph;
	String sqlHumiGraph;
	String sqlIllGraph;
	String sqlTempTable;
	String sqlHumiTable;
	String sqlIllTable;
	String TempTableFind;
	String HumiTableFind;
	String IllTableFind;
	String insertDiary;
	String readDiary;
	String updateDiary;
	String deleteDiary;
	String overLapWriteCheck;
	String insertReturns;
	String overLapReadCheck;
	String diaryResult;
	String checkDiary;
	// ������ ����

	JSONObject Jobject;
	// ���ϵ� JsonObject ��ü.

	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	// Connection�� ���� �ʿ��� �͵�.
	
//	private void ConnectMYSQLwithRS(String Query) {
//		try {
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//			conn = DriverManager.getConnection(url, dbid, dbpw);
//			
//			pstmt = conn.prepareStatement(Query);
//			rs = pstmt.executeQuery();
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	
//	}

	public JSONObject Read() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// mysql�� �����ϱ� ���� ����̹� �ε�.
			conn = DriverManager.getConnection(url, dbid, dbpw);
			// url���� id�� ��й�ȣ�� �Է��Ͽ� mysql�� ����.
			sqlRead = "select * from " + DBTABLE + " order by ID asc"; // ��ü�� �������� ���� ������
			pstmt = conn.prepareStatement(sqlRead);
			// pstmt�� �������� �ְ�
			rs = pstmt.executeQuery();
			
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("ID", rs.getString("ID"));
				JOB.put("TEMP", rs.getString("TEMP"));
				JOB.put("HUMI", rs.getString("HUMI"));
				JOB.put("ILL", rs.getString("ILL"));
				JOB.put("DATESTAMP", rs.getString("DATESTAMP"));
				// �̰� ������ �����ϱ�.
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendData", jArray);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();

		}

		return Jobject;
	}

	public JSONObject Login() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlLogin = "select ID,PWD from " + USERTABLE; // ��ü�� �������� ���� ������
			pstmt = conn.prepareStatement(sqlLogin);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("ID", rs.getString("ID"));
				JOB.put("PWD", rs.getString("PWD"));

				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendLogin", jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;
	}

	public JSONObject TotalGraph() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlTotalGraph = "select date_format(datestamp, '%y-%m-%d'),avg(temp) ,avg(humi), avg(ill) from " + DBTABLE
					+ " group by date_format(datestamp, '%y-%m-%d') order by date_format(datestamp, '%y-%m-%d') ASC";
			pstmt = conn.prepareStatement(sqlTotalGraph);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("avgTemp", rs.getString("avg(temp)"));
				JOB.put("avgHumi", rs.getString("avg(humi)"));
				JOB.put("avgIll", rs.getString("avg(ill)"));
				JOB.put("avgDate", rs.getString("date_format(datestamp, '%y-%m-%d')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendAvgData", jArray);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();

		}
		System.out.println(Jobject);
		return Jobject;
	}

	public JSONObject TempGraph() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlTempGraph = "select id,avg(temp),date_format(datestamp,'%d��') from " + DBTABLE
					+ " group by date_format(datestamp,'%d��') order by ID desc";
			pstmt = conn.prepareStatement(sqlTempGraph);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("id", rs.getString("id"));
				JOB.put("Temp", rs.getString("avg(temp)"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%d��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendTempGraph", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}
		return Jobject;

	}

	public JSONObject HumiGraph() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlHumiGraph = "select id,avg(humi),date_format(datestamp,'%d��') from " + DBTABLE
					+ " group by date_format(datestamp,'%d��') order by ID desc";
			pstmt = conn.prepareStatement(sqlHumiGraph);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("id", rs.getString("id"));
				JOB.put("Humi", rs.getString("avg(humi)"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%d��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendhumiGraph", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}
		return Jobject;

	}

	public JSONObject IllGraph() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlIllGraph = "select id,avg(ill),date_format(datestamp,'%d��') from " + DBTABLE
					+ " group by date_format(datestamp,'%d��') order by ID desc";
			pstmt = conn.prepareStatement(sqlIllGraph);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("id", rs.getString("id"));
				JOB.put("Ill", rs.getString("avg(ill)"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%d��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendillGraph", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}
		return Jobject;

	}

	public JSONObject TempTable() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlTempTable = "select id,temp,date_format(datestamp,'%m�� %d�� %H��') from " + DBTABLE + " order by id DESC";
			pstmt = conn.prepareStatement(sqlTempTable);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("id", rs.getString("id"));
				JOB.put("Temp", rs.getString("temp"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%m�� %d�� %H��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendTampTable", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;

	}

	public JSONObject HumiTable() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlHumiTable = "select id,humi,date_format(datestamp,'%m�� %d�� %H��') from " + DBTABLE + " order by id DESC";
			pstmt = conn.prepareStatement(sqlHumiTable);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("id", rs.getString("id"));
				JOB.put("Humi", rs.getString("humi"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%m�� %d�� %H��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendHumiTable", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;

	}

	public JSONObject IllTable() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			sqlIllTable = "select id,ill,date_format(datestamp,'%m�� %d�� %H��') from " + DBTABLE + " order by id DESC";
			pstmt = conn.prepareStatement(sqlIllTable);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("id", rs.getString("id"));
				JOB.put("Ill", rs.getString("ill"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%m�� %d�� %H��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("sendIllTable", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;

	}

	public JSONObject TempTableFind() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			TempTableFind = "select temp,date_format(datestamp,'%m�� %d�� %H��') from " + DBTABLE;
			pstmt = conn.prepareStatement(TempTableFind);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("Temp", rs.getString("temp"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%m�� %d�� %H��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("findTampTable", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;

	}

	public JSONObject HumiTableFind() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			HumiTableFind = "select humi,date_format(datestamp,'%m�� %d�� %H��') from " + DBTABLE;
			pstmt = conn.prepareStatement(HumiTableFind);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("Humi", rs.getString("humi"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%m�� %d�� %H��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("findHumiTable", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;

	}

	public JSONObject IllTableFind() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			IllTableFind = "select ill,date_format(datestamp,'%m�� %d�� %H��') from " + DBTABLE;
			pstmt = conn.prepareStatement(IllTableFind);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;

			while (rs.next()) {
				JSONObject JOB = new JSONObject();
				JOB.put("Ill", rs.getString("ILL"));
				JOB.put("Date", rs.getString("date_format(datestamp,'%m�� %d�� %H��')"));
				jArray.add(count, JOB);
				count++;
			}
			Jobject.put("findIllTable", jArray);

			System.out.println(jArray);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

		return Jobject;

	}

	public String insertDiary(String CONTENT, String DATESTAMP) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			overLapWriteCheck = "select DATESTAMP from " + DIARYTABLE + " where DATESTAMP = ?";
			pstmt = conn.prepareStatement(overLapWriteCheck);
			pstmt.setString(1, DATESTAMP);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				insertReturns = "�̹� ������ �����մϴ�. �������ּ���.";
			} else {

				insertDiary = "insert into " + DIARYTABLE + "(CONTENT,DATESTAMP) values(?,?)";
				pstmt = conn.prepareStatement(insertDiary);
				pstmt.setString(1, CONTENT);
				pstmt.setString(2, DATESTAMP);
				pstmt.executeUpdate();
				insertReturns = "�ۼ��Ϸ�";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}
		return insertReturns;
	}

	public String readDiary(String DATESTAMP) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			readDiary = "select * from " + DIARYTABLE + " where DATESTAMP = ?";
			pstmt = conn.prepareStatement(readDiary);
			pstmt.setString(1, DATESTAMP);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;
			while (rs.next()) {
				JSONObject jObj = new JSONObject();
				jObj.put("NO", rs.getString("NO"));
				jObj.put("CONTENT", rs.getString("CONTENT"));
				jObj.put("DATESTAMP", rs.getString("DATESTAMP"));
				jArray.add(count, jObj);
				count++;
			}

			Jobject.put("sendreadDiary", jArray);
			diaryResult = Jobject.toJSONString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}
		return diaryResult;

	}

	public void updateDiary(String NO, String CONTENT) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			updateDiary = "update " + DIARYTABLE + " SET Content = ? where NO = ?";
			pstmt = conn.prepareStatement(updateDiary);
			pstmt.setString(1, CONTENT);
			pstmt.setString(2, NO);
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

	}

//	public String ReadTest() { // �Ƶ��̳뿡 �����ͺ��̽� �� �����°�.
//		String a = "12";
//
//		try {
//			Class.forName("com.mysql.jdbc.Driver").newInstance();
//			conn = DriverManager.getConnection(url, dbid, dbpw);
//			sqlRead = "select temp,humi,ill from pcs.pcs order by id DESC limit 1;"; // ��ü�� �������� ���� ������
//			pstmt = conn.prepareStatement(sqlRead);
//			rs = pstmt.executeQuery();
//
//			while (rs.next()) {
//				String te = rs.getString("TEMP");
//				a += te + ":";
//				String hu = rs.getString("HUMI");
//				a += hu + ":";
//				String il = rs.getString("ILL");
//				a += il + ":";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			CloseTotal();
//
//		}
//
//		return a;
//	}

	public void deleteDiary(String NO) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);
			deleteDiary = "delete from " + DIARYTABLE + " where NO = ?";
			pstmt = conn.prepareStatement(deleteDiary);
			pstmt.setString(1, NO);
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}

	}

	public String checkDiary() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, dbid, dbpw);

			checkDiary = "select date_format(DATESTAMP,'%Y-%c-%d') from " + DIARYTABLE;
			pstmt = conn.prepareStatement(checkDiary);
			rs = pstmt.executeQuery();
			Jobject = new JSONObject(); // ���̽� ��ü.
			JSONArray jArray = new JSONArray(); // ���̽� �迭
			int count = 0;
			while (rs.next()) {
				JSONObject jObj = new JSONObject();
				jObj.put("DATESTAMP", rs.getString("date_format(DATESTAMP,'%Y-%c-%d')"));
				jArray.add(count, jObj);
				count++;
			}
			Jobject.put("sendCheckDiary", jArray);
			diaryResult = Jobject.toJSONString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseTotal();
		}
		return diaryResult;

	}

	public void CloseTotal() {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
// Conn, Rs, Pstmt ���� Close �ϱ� ���Ѱ�
	}

	public void CloseCORS() {

		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	// Conn,Pstmt �� Close �ϱ� ���Ѱ�.

	

}
