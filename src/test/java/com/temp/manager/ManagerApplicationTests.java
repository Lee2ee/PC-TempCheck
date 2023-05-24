package com.temp.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootTest
class ManagerApplicationTests {

	private static final String DRIVER = "org.mariadb.jdbc.Driver";
	private static final String URL = "jdbc:mariadb://localhost:3306/temp_manager";
	private static final String USER = "root";
	private static final String PW = "devk";

	@Test
	void contextLoads() {
	}

	@Test
	public void testConnection() throws Exception{

		Class.forName(DRIVER);

		try(Connection con = DriverManager.getConnection(URL, USER, PW)){
			System.out.println("## DB 연결 성공 ##");
			System.out.println(con);

		}catch(Exception e){
			System.out.println("## DB 연결 실패 ##");
			e.printStackTrace();
		}
	}

}