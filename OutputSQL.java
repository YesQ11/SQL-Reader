package console;

import java.sql.*;

import javax.swing.*;

public class OutputSQL {
	static public void outPut(TestGUI tes,String name,String pass,String databasename,String LSN) {
		Connect conn = new Connect();
		int i=conn.getconnection(name,pass,databasename);
		if (i==1) {
			try {
				conn.readLog(name, pass, databasename, LSN);//解析已存在以及新增的SQL语句

			} catch (SQLException e1) {

				JOptionPane.showMessageDialog(null, "LSN不合法或数据库连接已断开，请重试");//LSN不存在

			}catch (Exception e2) {

				JOptionPane.showMessageDialog(null, "解析结束");

			}
		}else {
			 JOptionPane.showMessageDialog(null, "用户名，密码或数据库名称错误，请重新输入");
		}
		tes.updataGuiDemo();
	}
}
