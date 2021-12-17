package console;

import java.sql.*;

public class Connect {
	private static final int MAX_ARRAY_LENGTH = 100;
	Analyse ana = new Analyse();

	// 加载驱动程序
	static {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getconnection(String userName, String userKey, String userDatabase) {// 建立jdbc连接
		/**** 1433是你自己的SQLserver端口号(默认是1433) *********/
		// 传入用户名、密码、数据库名
		String url = "jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase;
		try {
			DriverManager.getConnection(url, userName, userKey);
			return 1;
		} catch (SQLException e) {
			return 0;
		}

	}

	public static String nextMiddleLSN(String currentLSN)// LSN中间段加8
	{
		String middleLSN = currentLSN.substring(9, 17);
		int middleLSNInt = Integer.parseInt(middleLSN, 16) + 8;
		String middleLSNHex = Integer.toHexString(middleLSNInt);
		middleLSNHex = String.format("%8s", middleLSNHex).replace(' ', '0');
		String nextLSN = String.format("%s%s%s", currentLSN.substring(0, 9), middleLSNHex, currentLSN.substring(17));
		return nextLSN;
	}

	public static String nextFirstLSN(String currentLSN)// LSN首项加1
	{
		String firstLSN = currentLSN.substring(0, 8);
		int firstLSNInt = Integer.parseInt(firstLSN, 16) + 1;
		String firstLSNHex = Integer.toHexString(firstLSNInt);
		firstLSNHex = String.format("%8s", firstLSNHex).replace(' ', '0');
		int middleLSNInt = 16;
		String middleLSNHex = Integer.toHexString(middleLSNInt);
		middleLSNHex = String.format("%8s", middleLSNHex).replace(' ', '0');
		String lastLSN = "";
		int lastLSNInt = 1;
		lastLSN = String.valueOf(lastLSNInt);
		lastLSN = String.format("%4s", lastLSN).replace(' ', '0');
		String nextLSN = String.format("%s:%s:%s", firstLSNHex, middleLSNHex, lastLSN);
		return nextLSN;
	}

	public static String oneFinalLSN(String currentLSN)// LSN末尾段置为1
	{
		String lastLSN = "";
		int lastLSNInt = 1;
		lastLSN = String.valueOf(lastLSNInt);
		lastLSN = String.format("%4s", lastLSN).replace(' ', '0');
		String nextLSN = String.format("%s%s", currentLSN.substring(0, 18), lastLSN);
		return nextLSN;
	}

	public static String previousFinalLSN(String currentLSN)// LSN末尾段减1
	{
		String lastLSN = currentLSN.substring(18);
		int lastLSNInt = Integer.parseInt(lastLSN, 16) - 1;
		String lastLSNHex = Integer.toHexString(lastLSNInt);
		lastLSNHex = String.format("%4s", lastLSNHex).replace(' ', '0');
		String nextLSN = String.format("%s%s", currentLSN.substring(0, 18), lastLSNHex);
		return nextLSN;
	}

	public static String nextFinalLSN(String currentLSN, String userName, String userKey, String userDatabase)// LSN末尾段加1
	{
		String lastLSN = currentLSN.substring(18);
		int lastLSNInt = Integer.parseInt(lastLSN, 16) + 1;
		String lastLSNHex = Integer.toHexString(lastLSNInt);
		lastLSNHex = String.format("%4s", lastLSNHex).replace(' ', '0');
		String nextLSN = String.format("%s%s", currentLSN.substring(0, 18), lastLSNHex);

		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName,
					userKey);
		} catch (SQLException e1) {
		}
		String sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlLog);
			while (rs.next()) {
				rs.getString("operation");
			}
			rs.close();
			st.close();
		} catch (SQLException e2) {// 递增后出错，需要进位
			try {
				nextLSN = findNext(nextLSN, userName, userKey, userDatabase);
			} catch (SQLException e3) {
				nextLSN = nextFirstLSN(nextLSN);
			}
		}
		return nextLSN;
	}

	public static String findNext(String lsn, String userName, String userKey, String userDatabase)
			throws SQLException {// 定位下一事务日志的起点
		String nextLSN = "";
		String sqlLog = "";
		String LSN = lsn;
		Connection conn = null;

		while (true) {
			try {
				try {
					conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase,
							userName, userKey);
				} catch (SQLException e) {
				}
				String ope = "";
				nextLSN = oneFinalLSN(nextMiddleLSN(LSN));// 下一事务
				sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sqlLog);
				while (rs.next()) {
					ope = rs.getString("operation");
				}
				rs.close();
				st.close();
				while (ope.equals("LOP_BEGIN_XACT") == false) {
					nextLSN = nextFinalLSN(nextLSN, userName, userKey, userDatabase);
					sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
					Statement sta = conn.createStatement();
					ResultSet res = sta.executeQuery(sqlLog);
					while (res.next()) {
						ope = res.getString("operation");
					}
					res.close();
					sta.close();
				} // 定位begin
				while (ope.equals("LOP_BEGIN_XACT")) {
					nextLSN = nextFinalLSN(nextLSN, userName, userKey, userDatabase);
					sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
					Statement sta = conn.createStatement();
					ResultSet res = sta.executeQuery(sqlLog);
					while (res.next()) {
						ope = res.getString("operation");
					}
					res.close();
					sta.close();
				} // 跳过begin
				sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
				Statement s1 = conn.createStatement();
				ResultSet r1 = s1.executeQuery(sqlLog);
				String pre = "";
				String cur = "";
				while (r1.next()) {
					cur = r1.getString("Current LSN");
					pre = r1.getString("Previous LSN");
				}
				if ((cur.substring(0, 18)).equals(pre.substring(0, 18)) == false) {
					nextLSN = nextFirstLSN(nextLSN);// previous LSN和current LSN前两段不一致，需要进位
				} else {
					nextLSN = oneFinalLSN(nextLSN);
				}
				r1.close();
				s1.close();
				conn.close();
				break;
			} catch (SQLException e1) {// 第一次尝试，日志不存在则递增
				try {
					try {
						conn = DriverManager.getConnection(
								"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName, userKey);
					} catch (SQLException e) {
					}
					nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
					sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
					Statement s2 = conn.createStatement();
					ResultSet r2 = s2.executeQuery(sqlLog);
					while (r2.next()) {
						r2.getString("operation");
					}
					r2.close();
					s2.close();
					conn.close();
					break;
				} catch (SQLException e2) {// 第二次尝试，日志不存在则递增
					try {
						try {
							conn = DriverManager.getConnection(
									"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName, userKey);
						} catch (SQLException e) {
						}
						nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
						sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
						Statement s3 = conn.createStatement();
						ResultSet r3 = s3.executeQuery(sqlLog);
						while (r3.next()) {
							r3.getString("operation");
						}
						r3.close();
						s3.close();
						conn.close();
						break;
					} catch (SQLException e3) {// 第三次尝试，日志不存在则递增
						try {
							try {
								conn = DriverManager.getConnection(
										"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName,
										userKey);
							} catch (SQLException e) {
							}
							nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
							sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN, nextLSN);
							Statement s4 = conn.createStatement();
							ResultSet r4 = s4.executeQuery(sqlLog);
							while (r4.next()) {
								r4.getString("operation");
							}
							r4.close();
							s4.close();
							conn.close();
							break;
						} catch (SQLException e4) {// 第四次尝试，日志不存在则递增
							try {
								try {
									conn = DriverManager.getConnection(
											"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName,
											userKey);
								} catch (SQLException e) {
								}
								nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
								sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN,
										nextLSN);
								Statement s5 = conn.createStatement();
								ResultSet r5 = s5.executeQuery(sqlLog);
								while (r5.next()) {
									r5.getString("operation");
								}
								r5.close();
								s5.close();
								conn.close();
								break;
							} catch (SQLException e5) {// 第五次尝试，日志不存在则递增
								try {
									try {
										conn = DriverManager.getConnection(
												"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase,
												userName, userKey);
									} catch (SQLException e) {
									}
									nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
									sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN,
											nextLSN);
									Statement s6 = conn.createStatement();
									ResultSet r6 = s6.executeQuery(sqlLog);
									while (r6.next()) {
										r6.getString("operation");
									}
									r6.close();
									s6.close();
									conn.close();
									break;
								} catch (SQLException e6) {// 第六次尝试，日志不存在则递增
									try {
										try {
											conn = DriverManager.getConnection(
													"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase,
													userName, userKey);
										} catch (SQLException e) {
										}
										nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
										sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN,
												nextLSN);
										Statement s7 = conn.createStatement();
										ResultSet r7 = s7.executeQuery(sqlLog);
										while (r7.next()) {
											r7.getString("operation");
										}
										r7.close();
										s7.close();
										conn.close();
										break;
									} catch (SQLException e7) {// 第七次尝试，日志不存在则递增
										try {
											try {
												conn = DriverManager.getConnection(
														"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase,
														userName, userKey);
											} catch (SQLException e) {
											}
											nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
											sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')",
													nextLSN, nextLSN);
											Statement s8 = conn.createStatement();
											ResultSet r8 = s8.executeQuery(sqlLog);
											while (r8.next()) {
												r8.getString("operation");
											}
											r8.close();
											s8.close();
											conn.close();
											break;
										} catch (SQLException e8) {// 第八次尝试，日志不存在则递增
											try {
												try {
													conn = DriverManager.getConnection(
															"jdbc:sqlserver://localhost:1433;DatabaseName="
																	+ userDatabase,
															userName, userKey);
												} catch (SQLException e) {
												}
												nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
												sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')",
														nextLSN, nextLSN);
												Statement s9 = conn.createStatement();
												ResultSet r9 = s9.executeQuery(sqlLog);
												while (r9.next()) {
													r9.getString("operation");
												}
												r9.close();
												s9.close();
												conn.close();
												break;
											} catch (SQLException e9) {// 第九次尝试，日志不存在则递增
												try {
													try {
														conn = DriverManager.getConnection(
																"jdbc:sqlserver://localhost:1433;DatabaseName="
																		+ userDatabase,
																userName, userKey);
													} catch (SQLException e) {
													}
													nextLSN = oneFinalLSN(nextMiddleLSN(nextLSN));
													sqlLog = String.format(
															"SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", nextLSN,
															nextLSN);
													Statement s10 = conn.createStatement();
													ResultSet r10 = s10.executeQuery(sqlLog);
													while (r10.next()) {
														r10.getString("operation");
													}
													r10.close();
													s10.close();
													conn.close();
													break;
												} catch (SQLException e10) {// 第十次尝试，日志不存在则检查数据库状态，若有需要则手动执行checkpoint
													try {
														conn = DriverManager.getConnection(
																"jdbc:sqlserver://localhost:1433;DatabaseName="
																		+ userDatabase,
																userName, userKey);
													} catch (SQLException e) {
													}
													String database = "";
													double loguse = 0;
													Statement s = conn.createStatement();
													ResultSet r = s.executeQuery("DBCC SQLPERF (LOGSPACE)");
													while (r.next()) {
														database = r.getString("Database Name");
														loguse = r.getDouble("Log Space Used (%)");
														if (database.equals(userDatabase)) {
															break;
														}
													} // 获得当前数据库日志的使用情况

													if (loguse >= 60) {// 日志占用多但未达阈值，手动进行检查点
														s.execute("checkpoint");
														String ope1 = "operation";
														String ope2 = "operation";
														nextLSN = lsn;
														String LSN1 = oneFinalLSN(nextMiddleLSN(nextLSN));
														;
														String LSN2 = nextFirstLSN(nextLSN);
														// LSN1代表继续递增，LSN2代表进位，两者同时进行尝试
														while (ope1.equals("LOP_END_CKPT") == false
																&& ope2.equals("LOP_END_CKPT") == false) {// 定位检查点的末尾
															LSN1 = oneFinalLSN(nextMiddleLSN(LSN1));
															LSN2 = oneFinalLSN(nextMiddleLSN(LSN2));

															try {
																try {
																	conn = DriverManager.getConnection(
																			"jdbc:sqlserver://localhost:1433;DatabaseName="
																					+ userDatabase,
																			userName, userKey);
																} catch (SQLException e) {
																}
																sqlLog = String.format(
																		"SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')",
																		LSN1, LSN1);
																Statement st1 = conn.createStatement();
																ResultSet rs1 = st1.executeQuery(sqlLog);
																while (rs1.next()) {
																	ope1 = rs1.getString("operation");
																}
																rs1.close();
																st1.close();
																sqlLog = String.format(
																		"SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')",
																		LSN2, LSN2);
																Statement st2 = conn.createStatement();
																ResultSet rs2 = st2.executeQuery(sqlLog);
																while (rs2.next()) {
																	ope2 = rs2.getString("operation");
																}
																rs2.close();
																st2.close();
															} catch (SQLException ex1) {
																try {
																	try {
																		conn = DriverManager.getConnection(
																				"jdbc:sqlserver://localhost:1433;DatabaseName="
																						+ userDatabase,
																				userName, userKey);
																	} catch (SQLException e) {
																	}
																	sqlLog = String.format(
																			"SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')",
																			LSN2, LSN2);
																	Statement st2 = conn.createStatement();
																	ResultSet rs2 = st2.executeQuery(sqlLog);
																	while (rs2.next()) {
																		ope2 = rs2.getString("operation");
																	}
																	rs2.close();
																	st2.close();
																} catch (SQLException ex2) {
																}
															}
														} // 进行两次try-catch，确保LSN2被尝试
														if (ope1.equals("LOP_END_CKPT")) {
															LSN = LSN1;
														} else {
															LSN = LSN2;
														}
														System.out.println("执行checkpoint" + "\n");
														// 跳过‘执行检查点’的日志(checkpoint本身也会生成日志)
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return nextLSN;
	}

	public static String transition(String str) {// 字节序转换
		char[] chars = str.toCharArray();
		char a, b, c, d;
		for (int i = 0; i < (chars.length / 4); i++) {
			a = chars[2 * i];
			b = chars[2 * i + 1];
			c = chars[chars.length - 1 - 2 * i];
			d = chars[chars.length - 1 - (2 * i + 1)];
			chars[chars.length - 1 - 2 * i] = b;
			chars[chars.length - 1 - (2 * i + 1)] = a;
			chars[2 * i] = d;
			chars[2 * i + 1] = c;
		}
		String str_ = String.valueOf(chars);
		return str_;
	}

	public String getTableName(String unitName) {// 截取表名
		String table;
		if (unitName.indexOf(".PK_") == -1) {
			table = unitName.substring(unitName.indexOf(".") + 1);
		} else {
			table = unitName.substring(unitName.indexOf(".") + 1, unitName.indexOf(".PK_"));
		}
		return table;
	}

	public int isFixed(String type) {// 判断数据类型是定长还是变长
		int is = 0;
		switch (type) {
		case "int":
			is = 1;
			break;
		case "tinyint":
			is = 1;
			break;
		case "smallint":
			is = 1;
			break;
		case "bigint":
			is = 1;
			break;
		case "char":
			is = 1;
			break;
		case "nchar":
			is = 1;
			break;
		case "smalldatetime":
			is = 1;
			break;
		case "timestamp":
			is = 1;
			break;
		case "datetime":
			is = 1;
			break;
		case "datetime2":
			is = 1;
			break;
		case "date":
			is = 1;
			break;
		case "time":
			is = 1;
			break;
		case "datetimeoffset":
			is = 1;
			break;
		case "uniqueidentifier":
			is = 1;
			break;
		case "numeric":
			is = 1;
			break;
		case "decimal":
			is = 1;
			break;
		case "smallmoney":
			is = 1;
			break;
		case "money":
			is = 1;
			break;
		case "float":
			is = 1;
			break;
		case "real":
			is = 1;
			break;
		case "bit":
			is = 1;
			break;
		case "binary":
			is = 1;
			break;
		case "varchar":
			is = 0;
			break;
		case "text":
			is = 0;
			break;
		case "nvarchar":
			is = 0;
			break;
		case "ntext":
			is = 0;
			break;
		case "varbinary":
			is = 0;
			break;
		case "image":
			is = 0;
			break;
		}
		return is;
	}

	public String ustartToCn(String str) {// unicode转换
		StringBuilder sb = new StringBuilder().append("0x").append(str.substring(0, 4));
		Integer codeInteger = Integer.decode(sb.toString());
		int code = codeInteger.intValue();
		char c = (char) code;
		return String.valueOf(c);
	}

	public String intToString(int[] arr) {
		StringBuilder sb = new StringBuilder();// 自定义一个字符缓冲区
		// 遍历int数组，并将int数组中的元素转换成字符串储存到字符缓冲区中去
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	@SuppressWarnings("resource")
	public void readLog(String userName, String userKey, String userDatabase, String LSN) throws SQLException {// 读取和解析日志，打印SQL语句,若LSN不存在，返回错误提示
		// 调用该方法时需要try&catch来处理异常

		String LSN_ = "";// 内部传递用
		String url = "jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase;
		try {
			DriverManager.getConnection(url, userName, userKey);
		} catch (SQLException e) {
		} // 建立jdbc连接

		Connection con = DriverManager.getConnection(url, userName, userKey);

		String sqlLog = String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN, LSN);
		Statement s0 = con.createStatement();
		ResultSet r0 = s0.executeQuery(sqlLog);
		while (r0.next()) {
			r0.getString("operation");// 若LSN对应日志不存在，抛出异常，LSN不合法
		}
		LSN_ = LSN;// 保存起始的LSN

		while (true) { // 此时定位到了一条日志
			String operation = "";
			String transaction = "";			
			try {
				while (true) { // 定位BEGIN所在行
					Statement st = con.createStatement();
					ResultSet rs = st.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
					while (rs.next()) {
						operation = rs.getString("Operation");// 操作类型
						transaction = rs.getString("Transaction Name");// 事务名
					}

					if (operation.equals("LOP_BEGIN_XACT")) {// 定位到了BEGIN
						break;
					}
					LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);// 下一行日志
				}
			} catch (Exception e) {
				LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 未找到BEGIN，进入下一事务
				while (true) { // 定位BEGIN所在行
					Connection conn = DriverManager.getConnection(
							"jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName, userKey);
					Statement sta = conn.createStatement();
					ResultSet rs = sta
							.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
					while (rs.next()) {
						operation = rs.getString("Operation");// 操作类型
						transaction = rs.getString("Transaction Name");// 事务名
					}

					if (operation.equals("LOP_BEGIN_XACT")) {// 定位到了BEGIN
						break;
					}
					LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);// 下一行日志
				}

			}

			con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=" + userDatabase, userName,
					userKey);

			if ("CREATE TABLE".equals(transaction)) {
				// transaction name（CREATE TABLE）满足,为create table操作
				String operation1 = "";
				String context1 = "";
				String rowlog = "";
				LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
				Statement sta0 = con.createStatement();
				ResultSet rs0 = sta0
						.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
				while (rs0.next()) {
					operation1 = rs0.getString("Operation");// 操作类型
					context1 = rs0.getString("Context");// 上下文
				}
				rs0.close();
				sta0.close();
				while (operation1.equals("LOP_INSERT_ROWS") == false || context1.equals("LCX_CLUSTERED") == false) {
					if (operation1.equals("LOP_BEGIN_XACT")) {
						while (operation1.equals("LOP_COMMIT_XACT") == false) {
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st00 = con.createStatement();
							ResultSet re00 = st00.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
							while (re00.next()) {
								operation1 = re00.getString("Operation");// 操作类型
							}
							re00.close();
							st00.close();
							if (operation1.equals("LOP_BEGIN_XACT")) {
								while (operation1.equals("LOP_COMMIT_XACT") == false) {
									LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
									Statement st01 = con.createStatement();
									ResultSet re01 = st01.executeQuery(
											String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
									while (re01.next()) {
										operation1 = re01.getString("Operation");// 操作类型
									}
									re01.close();
									st01.close();
									if (operation1.equals("LOP_BEGIN_XACT")) {
										while (operation1.equals("LOP_COMMIT_XACT") == false) {
											LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
											Statement st02 = con.createStatement();
											ResultSet re02 = st02.executeQuery(String.format(
													"SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
											while (re02.next()) {
												operation1 = re02.getString("Operation");// 操作类型
											}
											re02.close();
											st02.close();
											if (operation1.equals("LOP_BEGIN_XACT")) {
												while (operation1.equals("LOP_COMMIT_XACT") == false) {
													LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
													Statement st03 = con.createStatement();
													ResultSet re03 = st03.executeQuery(String.format(
															"SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_,
															LSN_));// 下一条日志
													while (re03.next()) {
														operation1 = re03.getString("Operation");// 操作类型
													}
													re03.close();
													st03.close();
												}
											}
										}
									}
								}
							}
						}
					} // 跳过包含的begin-commit（以及嵌套）
					LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
					Statement sta000 = con.createStatement();
					ResultSet rs000 = sta000
							.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
					while (rs000.next()) {
						operation1 = rs000.getString("Operation");// 操作类型
						context1 = rs000.getString("Context");// 上下文
					}
					rs000.close();
					sta000.close();
				}
				// 寻找第一条operation为LOP_INSERT_ROWS,context为LCX_CLUSTERED的日志，即包含表名信息，获得操作类型和上下文

				Statement sta1 = con.createStatement();
				ResultSet rs1 = sta1
						.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));

				while (rs1.next()) {
					rowlog = (rs1.getString("RowLog Contents 0"));// 含表名的十六进制字符串
				}
				rs1.close();
				sta1.close();

				String table = "";
				String tableHex = rowlog.substring(112);
				int i = 0, j = 0;
				String[] res = new String[MAX_ARRAY_LENGTH];
				for (int h = 0; h < MAX_ARRAY_LENGTH; h++) {
					res[h] = "";
				}
				StringBuffer sbu = new StringBuffer();
				while (i + 4 <= tableHex.length()) {
					res[j] = ustartToCn(transition(tableHex.substring(i, i + 4)));
					i = i + 4;
					j++;
				}
				for (int p = 0; p < MAX_ARRAY_LENGTH; p++) {
					sbu.append(res[p]);
				}
				table = sbu.toString();// 解析出表名(支持汉字)

				String[] dataType = new String[MAX_ARRAY_LENGTH];// 建表的数据类型
				String[] dataLength = new String[MAX_ARRAY_LENGTH];// 数据类型长度
				String[] precision = new String[MAX_ARRAY_LENGTH];// 浮点数总长
				String[] scale = new String[MAX_ARRAY_LENGTH];// 浮点小数长度
				String columnName[] = new String[MAX_ARRAY_LENGTH];// 列名
				int columnNum = 0;// 列数量
				String dbname = "";// 架构名
				String primaryKey = "";// 主键
				int rowCount = 0;// 当前行号，即结果集记录数
				try {
					String sqlKey = String.format(
							"select column_name from information_schema.CONSTRAINT_COLUMN_USAGE where [TABLE_NAME]='%s'",
							table);
					Statement sta2 = con.createStatement();
					ResultSet rs2 = sta2.executeQuery(sqlKey);
					while (rs2.next()) {
						primaryKey = rs2.getString("column_name");
					}
					rs2.close();
					sta2.close();
				} catch (SQLException e) {
				} // 获得主键的列名

				Statement st0 = con.createStatement();
				ResultSet re0 = st0.executeQuery(String
						.format("select table_schema from information_schema.TABLES where table_name ='%s'", table));
				while (re0.next()) {
					dbname = re0.getString("table_schema");
				} // 获得架构名
				Statement st = con.createStatement();
				ResultSet r = st.executeQuery(
						String.format("select * from information_schema.COLUMNS where [TABLE_NAME]='%s'", table));
				while (r.next()) {
					rowCount++;
				} // 获得表中列的数量
				for (int i1 = 0; i1 < rowCount; i1++) {// 逐行查系统元数据库得得列名、数据类型、类型长度数组

					String sqlSys = String.format(
							"select * from information_schema.COLUMNS where [TABLE_NAME]='%s' and ORDINAL_POSITION=%d",
							table, i1 + 1);
					Statement sta3 = con.createStatement();
					ResultSet rs3 = sta3.executeQuery(sqlSys);
					columnNum++;// 列数
					while (rs3.next()) {
						columnName[i1] = rs3.getString("column_name");// 存列名
						dataType[i1] = rs3.getString("data_type");// 存数据类型
						dataLength[i1] = rs3.getString("CHARACTER_MAXIMUM_LENGTH");// 存数据类型长度
						precision[i1] = rs3.getString("NUMERIC_PRECISION");// 存浮点数总长
						scale[i1] = rs3.getString("NUMERIC_SCALE");// 存浮点数小数长度
					}
					rs3.close();
					sta3.close();

					if ("-1".equals(dataLength[i1])) {
						dataLength[i1] = "(max)";// max型
					} else if (dataType[i1].equals("ntext") || dataType[i1].equals("text")
							|| dataType[i1].equals("image")) {
						dataLength[i1] = "";// 长字符串型
					} else if (dataType[i1].equals("varchar") || dataType[i1].equals("char")
							|| dataType[i1].equals("binary") || dataType[i1].equals("varbinary")
							|| dataType[i1].equals("nchar") || dataType[i1].equals("nvarchar")) {// 长度非空则保存数据类型长度,否则为空字符串
						dataLength[i1] = String.format("(%s)", dataLength[i1]);
					} else {
						dataLength[i1] = "";
					}

					if (columnName[i1].equals(primaryKey)) {// 该列是主键
						dataLength[i1] = String.format("%s primary key", dataLength[i1]);// 在长度后加入主键描述
					}

				}

				String SQLExpression = String.format("create table %s.%s(", dbname, table);// 拼装SQL语句并返回
				int n = 0;
				while (n < (columnNum - 1)) {
					if (dataType[n].equals("decimal") || dataType[n].equals("numeric")) {// 浮点型
						SQLExpression = String.format("%s%s %s(%s,%s),", SQLExpression, columnName[n], dataType[n],
								precision[n], scale[n]);
					} else {
						SQLExpression = String.format("%s%s %s%s,", SQLExpression, columnName[n], dataType[n],
								dataLength[n]);
					}
					n++;
				}
				if (dataType[n].equals("decimal") || dataType[n].equals("numeric")) {// 浮点型
					SQLExpression = String.format("%s%s %s(%s,%s))", SQLExpression, columnName[n], dataType[n],
							precision[n], scale[n]);
				} else {
					SQLExpression = String.format("%s%s %s%s)", SQLExpression, columnName[n], dataType[n],
							dataLength[n]);
				}
				System.out.println(SQLExpression + "\n");// 打印SQL语句
				LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 下一事务

			} else if ("INSERT".equals(transaction)) {// transaction name（INSERT）满足,为insert操作
				String[] logText = new String[MAX_ARRAY_LENGTH];// text型数据
				String ptrText = "";// text型数据的指针
				String[] data = new String[MAX_ARRAY_LENGTH];// 解析后的全部数据
				String operation1 = "";
				String context1 = "";
				LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);// 转向下一条日志
				Statement sta1 = con.createStatement();
				ResultSet rs1 = sta1
						.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
				while (rs1.next()) {
					operation1 = rs1.getString("operation");// 操作类型
					context1 = rs1.getString("context");// 上下文
				}
				rs1.close();
				sta1.close();

				int textNum = 0; // 专门存放text/ntext/image数据的数组的序号
				while (true) {
					if (operation1.equals("LOP_INSERT_ROWS") == false) {// 无用日志，保存下一条的信息和LSN号
						try {
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);

							Statement sta2 = con.createStatement();
							ResultSet rs2 = sta2.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (rs2.next()) {
								operation1 = rs2.getString("Operation");// 操作类型
								context1 = rs2.getString("Context");// 上下文
							}
							rs2.close();
							sta2.close();
						} catch (SQLException e) {
							break;
						}

					} else if (operation1.equals("LOP_INSERT_ROWS") && context1.equals("LCX_TEXT_MIX") == false
							&& context1.equals("LCX_INDEX_INTERIOR") == false) {// 普通插入
						Statement sta2 = con.createStatement();
						ResultSet rs2 = sta2.executeQuery(
								String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
						String rowlog = "";// 含数据的十六进制字符串
						String unit = "";// 含表名字段
						while (rs2.next()) {
							unit = rs2.getString("AllocUnitName");
							rowlog = (rs2.getString("RowLog Contents 0"));
						}
						rs2.close();
						sta2.close();

						String fullTable = "";// 含架构名的表名
						if (unit.indexOf(".PK_") == -1) {
							fullTable = unit;
						} else {
							fullTable = unit.substring(0, unit.indexOf(".PK_"));
						}
						String table = getTableName(unit);// 截取表名（不含架构名，防止查元数据时出错）

						String[] dataType = new String[MAX_ARRAY_LENGTH];// 建表的数据类型
						String[] fixedType = new String[MAX_ARRAY_LENGTH];// 定长数据类型
						String[] variableType = new String[MAX_ARRAY_LENGTH];// 变长数据类型
						int fixedNum = 0, variableNum = 0;// 定长列数，变长列数
						int totalNum = 0;// 总列数
						int[] fixedLength = new int[MAX_ARRAY_LENGTH];// 定长类型长度
						int[] precision = new int[MAX_ARRAY_LENGTH];// 浮点数总长
						int[] scale = new int[MAX_ARRAY_LENGTH];// 浮点小数长度
						int[] isFixedNull = new int[MAX_ARRAY_LENGTH];// 定长列判空
						int[] isVariableNull = new int[MAX_ARRAY_LENGTH];// 变长列判空

						String sqlSys = String
								.format("select * from information_schema.COLUMNS where [TABLE_NAME]='%s'", table);// 查系统元数据库
						Statement sta3 = con.createStatement();
						ResultSet rs3 = sta3.executeQuery(sqlSys);
						int rowCount = 0;
						while (rs3.next()) {
							rowCount++;
						} // 得到当前行号，即结果集记录数
						rs3.close();
						sta3.close();
						int i = 0, m = 0, n = 0;
						for (int t = 0; t < rowCount; t++) {// 逐行查系统元数据库得数据信息
							sqlSys = String.format(
									"select * from information_schema.COLUMNS where [TABLE_NAME]='%s' and ORDINAL_POSITION=%d",
									table, i + 1);
							Statement sta4 = con.createStatement();
							ResultSet rs4 = sta4.executeQuery(sqlSys);
							while (rs4.next()) {
								dataType[i] = rs4.getString("data_type");// 存建表数据类型
								if (isFixed(dataType[i]) == 1) {
									fixedNum++;// 存定长列数
									if (dataType[i].equals("decimal") || dataType[i].equals("numeric")) {
										precision[m] = Integer.parseInt(rs4.getString("NUMERIC_PRECISION"));// 存浮点数总长
										scale[m] = Integer.parseInt(rs4.getString("NUMERIC_SCALE"));// 存浮点数小数长度
										if (precision[m] <= 9) {
											fixedLength[m] = 5;
										} else if (10 <= precision[m] && precision[m] <= 19) {
											fixedLength[m] = 9;
										} else if (20 <= precision[m] && precision[m] <= 28) {
											fixedLength[m] = 13;
										} else {
											fixedLength[m] = 17;
										}
									} else if (dataType[i].equals("char") || dataType[i].equals("nchar")
											|| dataType[i].equals("binary")) {
										fixedLength[m] = Integer.parseInt(rs4.getString("CHARACTER_MAXIMUM_LENGTH"));
									} else {
										fixedLength[m] = 0;
									} // 存定长类型长度，浮点数特殊处理
									fixedType[m] = rs4.getString("data_type");// 存定长数据类型
									m++;
									i++;
								} else {
									variableNum++;// 存变长列数
									if (Integer.parseInt(rs4.getString("CHARACTER_MAXIMUM_LENGTH")) == -1) {// 判断变长类型是否为max
										variableType[n] = (rs4.getString("data_type")) + "max";
									} else {
										variableType[n] = rs4.getString("data_type");
									} // 存变长类型
									n++;
									i++;
								}
							}
							rs4.close();
							sta4.close();
						}
						totalNum = fixedNum + variableNum;
						fixedLength = ana.fixedNulltoLength(fixedType, fixedLength, fixedNum);// 调用fixedTypeTotalLength方法，将fixedLength数据全部转化为数字
						int fixedTotal = 0;
						for (int x : fixedLength) {
							fixedTotal += x;
						} // 求定长数据所占的总长度
						int bitmapLength = (int) (Math
								.ceil((Double.valueOf(fixedNum) + Double.valueOf(variableNum)) / 8));// 位图的字节数
						String bitmapHex = rowlog.substring(12 + fixedTotal * 2,
								12 + fixedTotal * 2 + bitmapLength * 2);
						String bitmap = "";
						bitmap = Integer.toBinaryString(Integer.parseInt(transition(bitmapHex), 16));// 转换字节序并转换成二进制
						while (bitmap.length() < bitmapLength * 8) {
							bitmap = String.format("%s%s", "0", bitmap);
						} // 左边补0
						bitmap = new StringBuilder(bitmap).reverse().toString();// 倒置字符串

						int i1 = 0, j = 0;
						for (int t = 0; t < (fixedNum + variableNum); t++) {
							if (isFixed(dataType[t]) == 1) {
								isFixedNull[i1] = Integer.parseInt(bitmap.substring(t, t + 1));
								i1++;
							} else {
								isVariableNull[j] = Integer.parseInt(bitmap.substring(t, t + 1));
								j++;
							}
						} // 将空值信息分别存储到定长和变长的判空数组

						String[] dataFixed = new String[MAX_ARRAY_LENGTH];// 解析后的定长数据
						String[] dataVariable = new String[MAX_ARRAY_LENGTH];// 解析后的变长数据
						dataFixed = ana.anaFixedType(fixedType, fixedNum, fixedLength, rowlog, isFixedNull);
						String row_log = dataFixed[fixedNum];
						if (variableNum > 0) {
							dataVariable = ana.anaVariableType(variableType, variableNum, totalNum, rowlog, row_log,
									isVariableNull, logText);
						}
						int m1 = 0, n1 = 0;
						for (int t = 0; t < totalNum; t++) {
							if (isFixed(dataType[t]) == 1) {// 给浮点型加小数点
								if ("decimal".equals(dataType[t]) || "numeric".equals(dataType[t])) {
									if (dataFixed[m1] == null) {
										data[t] = null;
									} else if (dataFixed[m1].equals("0") == false) {
										if (dataFixed[m1].indexOf("-") == 0) {// 负数
											dataFixed[m1] = dataFixed[m1].substring(1);
											while (dataFixed[m1].length() < precision[m1]) {
												dataFixed[m1] = String.format("%s%s", "0", dataFixed[m1]);
											} // 左边补0
											data[t] = String.format("%s.%s",
													dataFixed[m1].substring(0, (dataFixed[m1].length() - scale[m1])),
													dataFixed[m1].substring(dataFixed[m1].length() - scale[m1]));
											data[t] = data[t].replaceAll("^(0+)", "");// 删除多余的0
											if (data[t].indexOf(".") == 0) {// 小数点左边添加0
												data[t] = "0" + data[t];
											} else if (data[t].indexOf(".") == (data[t].length() - 1)) {// 删去整数的小数点
												data[t] = data[t].substring(0, data[t].length() - 1);
											}
											data[t] = "-" + data[t];
										} else {// 正数
											while (dataFixed[m1].length() < precision[m1]) {
												dataFixed[m1] = String.format("%s%s", "0", dataFixed[m1]);
											} // 左边补0
											data[t] = String.format("%s.%s",
													dataFixed[m1].substring(0, (dataFixed[m1].length() - scale[m1])),
													dataFixed[m1].substring(dataFixed[m1].length() - scale[m1]));
											data[t] = data[t].replaceAll("^(0+)", "");// 删除多余的0
											if (data[t].indexOf(".") == 0) {// 小数点左边添加0
												data[t] = "0" + data[t];
											} else if (data[t].indexOf(".") == (data[t].length() - 1)) {// 删去整数的小数点
												data[t] = data[t].substring(0, data[t].length() - 1);
											}
										}
									} else {
										data[t] = dataFixed[m1];
									}
									m1++;
								} else if ("money".equals(dataType[t]) || "smallmoney".equals(dataType[t])) {
									if (dataFixed[m1] == null) {
										data[t] = null;
									} else if (dataFixed[m1].equals("0")) {
										data[t] = dataFixed[m1];
									} else {
										data[t] = String.format("%s.%s",
												dataFixed[m1].substring(0, dataFixed[m1].length() - 4),
												dataFixed[m1].substring(dataFixed[m1].length() - 4));
									}
									m1++;
								} else {
									data[t] = dataFixed[m1];
									m1++;
								}
							} else {
								data[t] = dataVariable[n1];
								n1++;
							}

						} // 将定长和变长数据按建表顺序合并

						String SQLExpression = String.format("insert into %s values(", fullTable);// 拼装SQL语句并返回
						int t = 0;
						while (t < (totalNum - 1)) {
							if (bitmap.substring(t, t + 1).equals("0")) {
								if (dataType[t].equals("timestamp")) {
									SQLExpression = String.format("%s%s,", SQLExpression, data[t]);
								} else {
									SQLExpression = String.format("%s'%s',", SQLExpression, data[t]);
								}
								t++;
							} else {
								SQLExpression = String.format("%s%s,", SQLExpression, data[t]);
								t++;
							}
						}
						if (bitmap.substring(t, t + 1).equals("0")) {
							if (dataType[t].equals("timestamp")) {
								SQLExpression = String.format("%s%s)", SQLExpression, data[t]);
							} else {
								SQLExpression = String.format("%s'%s')", SQLExpression, data[t]);
							}
						} else {
							SQLExpression = String.format("%s%s)", SQLExpression, data[t]);
						} // 若输入为NULL不能带''
						System.out.println(SQLExpression + "\n");// 打印SQL语句
						LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 下一事务
						break;
					} else if (operation1.equals("LOP_INSERT_ROWS") && context1.equals("LCX_TEXT_MIX")) {// 插入text/ntext/image型数据
						String rowlog0 = "";
						String rowlog1 = "";
						Statement sta2 = con.createStatement();
						ResultSet rs2 = sta2.executeQuery(
								String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
						while (rs2.next()) {
							rowlog0 = (rs2.getString("RowLog Contents 0"));// 含数据的十六进制字符串
						}
						rs2.close();
						sta2.close();
						if (rowlog0.length() > 40) {// 插入短文本
							logText[textNum] = rowlog0;// 含text/ntext/image型数据的十六进制rowlog
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st3 = con.createStatement();
							ResultSet r3 = st3.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r3.next()) {
								operation1 = r3.getString("Operation");// 操作类型
								context1 = r3.getString("Context");// 上下文
							}
							r3.close();
							st3.close();
							// 下一条日志信息
						} else {// 插入长文本
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st0 = con.createStatement();
							ResultSet re0 = st0.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (re0.next()) {
								operation1 = re0.getString("Operation");// 操作类型
								context1 = re0.getString("Context");// 上下文
								rowlog1 = re0.getString("RowLog Contents 1");// 十六进制数据
							}
							re0.close();
							st0.close();
							while (operation1.equals("LOP_MODIFY_ROW") == false
									|| context1.equals("LCX_TEXT_MIX") == false) {
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st3 = con.createStatement();
								ResultSet re3 = st3.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
								while (re3.next()) {
									operation1 = re3.getString("Operation");// 操作类型
									context1 = re3.getString("Context");// 上下文
									rowlog1 = re3.getString("RowLog Contents 1");// 十六进制数据
								}
								re3.close();
								st3.close();
							} // 定位modify
							if (ptrText.equals("")) {// 第一组插入
								logText[textNum] = "0000000000000000000000000000000000000000" + rowlog1;
								ptrText = rowlog0;
								textNum++;
							} else {
								if (rowlog0.equals(ptrText)) {// 指针相同说明用多组insert-modify存长文本
									logText[textNum - 1] = String.format("%s%s", logText[textNum - 1], rowlog1);
									while (operation1.equals("LOP_INSERT_ROWS") == false
											|| context1.equals("LCX_TEXT_MIX") == false) {
										LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
										Statement st3 = con.createStatement();
										ResultSet re3 = st3.executeQuery(String
												.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
										while (re3.next()) {
											operation1 = re3.getString("Operation");// 操作类型
											context1 = re3.getString("Context");// 上下文
										}
										re3.close();
										st3.close();
									} // 定位modify后的下一条insert-textmix的记录
									LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
									Statement st3 = con.createStatement();
									ResultSet r3 = st3.executeQuery(
											String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
									while (r3.next()) {
										operation1 = r3.getString("Operation");// 操作类型
										context1 = r3.getString("Context");// 上下文
									}
									r3.close();
									st3.close();
									// 下一条日志信息
								}
							}
						}
					} else if (operation1.equals("LOP_INSERT_ROWS") && context1.equals("LCX_INDEX_INTERIOR")) {// 无用日志，保存下一条的信息和LSN号
						LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);

						Statement sta = con.createStatement();
						ResultSet rs = sta.executeQuery(
								String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
						while (rs.next()) {
							operation1 = rs.getString("Operation");// 操作类型
							context1 = rs.getString("Context");// 上下文
						}
						rs.close();
						sta.close();

					}
				}
			} else if ("DELETE".equals(transaction)) {// transaction name（DELETE）满足,为delete操作
				String[] data = new String[MAX_ARRAY_LENGTH];// 解析后的全部数据
				String operation1 = "";
				String context1 = "";
				LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
				Statement sta1 = con.createStatement();
				ResultSet rs1 = sta1
						.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
				while (rs1.next()) {
					operation1 = rs1.getString("Operation");// 操作类型
					context1 = rs1.getString("Context");// 操作类型
				}
				rs1.close();
				sta1.close();

				while (operation1.equals("LOP_DELETE_ROWS") == false || context1.equals("LCX_MARK_AS_GHOST") == false) {
					if (operation1.equals("LOP_BEGIN_XACT")) {
						while (operation1.equals("LOP_COMMIT_XACT") == false) {
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st0 = con.createStatement();
							ResultSet re0 = st0.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
							while (re0.next()) {
								operation1 = re0.getString("Operation");// 操作类型
							}
							re0.close();
							st0.close();
						}
					} // 跳过包含的begin-commit
					LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
					Statement sta00 = con.createStatement();
					ResultSet rs00 = sta00
							.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
					while (rs00.next()) {
						operation1 = rs00.getString("Operation");// 操作类型
						context1 = rs00.getString("Context");// 操作类型
					}
					rs00.close();
					sta00.close();
				}

				String[] keyType = new String[MAX_ARRAY_LENGTH];// 主键数据类型
				Statement sta01 = con.createStatement();
				ResultSet rs01 = sta01
						.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
				String unit = "";// 含表名字段
				String rowlog = "";// 含数据的十六进制字符串

				while (rs01.next()) {
					unit = rs01.getString("AllocUnitName");
					rowlog = rs01.getString("RowLog Contents 0");
				}
				String fullTable = "";// 含架构名的表名
				if (unit.indexOf(".PK_") == -1) {
					fullTable = unit;
				} else {
					fullTable = unit.substring(0, unit.indexOf(".PK_"));
				}
				String table = getTableName(unit);// 截取表名（不含架构名，防止查元数据时出错）
				String primaryKey = "";
				rs01.close();
				sta01.close();

				// 获得主键的列名
				String sqlKey = String.format(
						"select column_name from information_schema.CONSTRAINT_COLUMN_USAGE where [TABLE_NAME]='%s'",
						table);
				Statement sta2 = con.createStatement();
				ResultSet rs2 = sta2.executeQuery(sqlKey);
				while (rs2.next()) {
					primaryKey = rs2.getString("column_name");
				}
				rs2.close();
				sta2.close();

				// 获得主键的数据类型
				String sqlKeyData = String.format(
						"select * from information_schema.COLUMNS where TABLE_NAME='%s'and COLUMN_NAME='%s'", table,
						primaryKey);
				Statement sta3 = con.createStatement();
				ResultSet rs3 = sta3.executeQuery(sqlKeyData);
				while (rs3.next()) {
					keyType[0] = rs3.getString("data_type");
				}
				rs3.close();
				sta3.close();

				if (isFixed(keyType[0]) == 1) {// 主键为定长类型，只需取定长部分的第一个数据
					int precision = 0, scale = 0;
					int[] keyLength = new int[1];
					String SQLKeyData = String.format(
							"select * from information_schema.COLUMNS where TABLE_NAME='%s'and COLUMN_NAME='%s'", table,
							primaryKey);
					Statement sta4 = con.createStatement();
					ResultSet rs4 = sta4.executeQuery(SQLKeyData);

					if ("decimal".equals(keyType[0]) || "numeric".equals(keyType[0])) {
						while (rs4.next()) {
							precision = Integer.parseInt(rs4.getString("NUMERIC_PRECISION"));// 存浮点数总长
							scale = Integer.parseInt(rs4.getString("NUMERIC_SCALE"));// 存浮点数小数长度
						}
						if (precision <= 9) {
							keyLength[0] = 5;
						} else if (10 <= precision && precision <= 19) {
							keyLength[0] = 9;
						} else if (20 <= precision && precision <= 28) {
							keyLength[0] = 13;
						} else {
							keyLength[0] = 17;
						}
					} else if ("char".equals(keyType[0]) || "nchar".equals(keyType[0]) || "binary".equals(keyType[0])) {
						while (rs4.next()) {
							keyLength[0] = Integer.parseInt(rs4.getString("CHARACTER_MAXIMUM_LENGTH"));
						}
					} else {
						keyLength[0] = 0;
					}
					rs4.close();
					sta4.close();
					int[] isFixedNull = { 0 };// 定长列判断是否为空，主键不能为空
					keyLength = ana.fixedNulltoLength(keyType, keyLength, 1);// 调用fixedTypeTotalLength方法，将keyLength数据转化为数字
					data = ana.anaFixedType(keyType, 1, keyLength, rowlog, isFixedNull);
					if ("decimal".equals(keyType[0]) || "numeric".equals(keyType[0])) {
						if (data[0] == null) {
							data[0] = null;
						} else if (data[0].equals("0") == false) {
							if (data[0].indexOf("-") == 0) {// 负数
								data[0] = data[0].substring(1);
								while (data[0].length() < precision) {
									data[0] = String.format("%s%s", "0", data[0]);
								} // 左边补0
								data[0] = String.format("%s.%s", data[0].substring(0, (data[0].length() - scale)),
										data[0].substring(data[0].length() - scale));
								data[0] = data[0].replaceAll("^(0+)", "");// 删除多余的0
								if (data[0].indexOf(".") == 0) {// 小数点左边添加0
									data[0] = "0" + data[0];
								} else if (data[0].indexOf(".") == (data[0].length() - 1)) {// 删去整数的小数点
									data[0] = data[0].substring(0, data[0].length() - 1);
								}
								data[0] = "-" + data[0];
							} else {// 正数
								while (data[0].length() < precision) {
									data[0] = String.format("%s%s", "0", data[0]);
								} // 左边补0
								data[0] = String.format("%s.%s", data[0].substring(0, (data[0].length() - scale)),
										data[0].substring(data[0].length() - scale));
								data[0] = data[0].replaceAll("^(0+)", "");// 删除多余的0
								if (data[0].indexOf(".") == 0) {// 小数点左边添加0
									data[0] = "0" + data[0];
								} else if (data[0].indexOf(".") == (data[0].length() - 1)) {// 删去整数的小数点
									data[0] = data[0].substring(0, data[0].length() - 1);
								}
							}
						} else {
							data[0] = "0";
						}
					} else if ("money".equals(keyType[0]) || "smallmoney".equals(keyType[0])) {
						data[0] = String.format("%s.%s", keyType[0].substring(0, keyType[0].length() - 4),
								keyType[0].substring(keyType[0].length() - 4));
					}

					String SQLExpression = String.format("delete from %s where %s = '%s'", fullTable, primaryKey,
							data[0]);
					System.out.println(SQLExpression + "\n");// 打印SQL语句
					LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 进入下一事务

				} else {// 主键为变长类型
					String[] dataType = new String[MAX_ARRAY_LENGTH];// 建表的数据类型
					String[] fixedType = new String[MAX_ARRAY_LENGTH];// 定长数据类型
					String[] variableType = new String[MAX_ARRAY_LENGTH];// 变长数据类型
					int fixedNum = 0, variableNum = 0;// 定长列数，变长列数
					int totalNum = 0;// 总列数
					int[] fixedLength = new int[MAX_ARRAY_LENGTH];// 定长类型长度
					int[] precision = new int[MAX_ARRAY_LENGTH];// 浮点数总长
					int[] scale = new int[MAX_ARRAY_LENGTH];// 浮点小数长度
					int[] isFixedNull = new int[MAX_ARRAY_LENGTH];// 定长列判空
					int[] isVariableNull = new int[MAX_ARRAY_LENGTH];// 变长列判空
					String[] dataText = new String[MAX_ARRAY_LENGTH];// TEXT型数据

					String sqlSys = String.format("select * from information_schema.COLUMNS where [TABLE_NAME]='%s'",
							table);// 查系统元数据库
					Statement st = con.createStatement();
					ResultSet rs = st.executeQuery(sqlSys);
					int rowCount = 0;
					while (rs.next()) {
						rowCount++;
					} // 得到当前行号，即结果集记录数
					rs.close();
					st.close();
					int i = 0, m = 0, n = 0;
					for (int t = 0; t < rowCount; t++) {// 逐行查系统元数据库得数据信息
						sqlSys = String.format(
								"select * from information_schema.COLUMNS where [TABLE_NAME]='%s' and ORDINAL_POSITION=%d",
								table, i + 1);
						Statement sta4 = con.createStatement();
						ResultSet rs4 = sta4.executeQuery(sqlSys);
						while (rs4.next()) {
							dataType[i] = rs4.getString("data_type");// 存建表数据类型
							if (isFixed(dataType[i]) == 1) {
								fixedNum++;// 存定长列数
								if (dataType[i].equals("decimal") || dataType[i].equals("numeric")) {
									precision[m] = Integer.parseInt(rs4.getString("NUMERIC_PRECISION"));// 存浮点数总长
									scale[m] = Integer.parseInt(rs4.getString("NUMERIC_SCALE"));// 存浮点数小数长度
									if (precision[m] <= 9) {
										fixedLength[m] = 5;
									} else if (10 <= precision[i] && precision[i] <= 19) {
										fixedLength[m] = 9;
									} else if (20 <= precision[i] && precision[i] <= 28) {
										fixedLength[m] = 13;
									} else {
										fixedLength[m] = 17;
									}
								} else if (dataType[i].equals("char") || dataType[i].equals("nchar")
										|| dataType[i].equals("binary")) {
									fixedLength[m] = Integer.parseInt(rs4.getString("CHARACTER_MAXIMUM_LENGTH"));
								} else {
									fixedLength[m] = 0;
								} // 存定长类型长度，浮点数特殊处理
								fixedType[m] = rs4.getString("data_type");// 存定长数据类型
								m++;
								i++;
							} else {
								variableNum++;// 存变长列数
								if (Integer.parseInt(rs4.getString("CHARACTER_MAXIMUM_LENGTH")) == -1) {// 判断变长类型是否为max
									variableType[n] = (rs4.getString("data_type")) + "max";
								} else {
									variableType[n] = rs4.getString("data_type");
								} // 存变长类型
								n++;
								i++;
							}
						}
						rs4.close();
						sta4.close();
					}
					totalNum = fixedNum + variableNum;
					fixedLength = ana.fixedNulltoLength(fixedType, fixedLength, fixedNum);// 调用fixedTypeTotalLength方法，将fixedLength数据全部转化为数字
					int fixedTotal = 0;
					for (int x : fixedLength) {
						fixedTotal += x;
					} // 求定长数据所占的总长度
					int bitmapLength = (int) (Math.ceil((Double.valueOf(fixedNum) + Double.valueOf(variableNum)) / 8));// 位图的字节数
					String bitmapHex = rowlog.substring(12 + fixedTotal * 2, 12 + fixedTotal * 2 + bitmapLength * 2);
					String bitmap = "";
					bitmap = Integer.toBinaryString(Integer.parseInt(transition(bitmapHex), 16));// 转换字节序并转换成二进制
					while (bitmap.length() < bitmapLength * 8) {
						bitmap = String.format("%s%s", "0", bitmap);
					} // 左边补0
					bitmap = new StringBuilder(bitmap).reverse().toString();// 倒置字符串

					int i1 = 0, j = 0;
					for (int t = 0; t < (fixedNum + variableNum); t++) {
						if (isFixed(dataType[t]) == 1) {
							isFixedNull[i1] = Integer.parseInt(bitmap.substring(t, t + 1));
							i1++;
						} else {
							isVariableNull[j] = Integer.parseInt(bitmap.substring(t, t + 1));
							j++;
						}
					} // 将空值信息分别存储到定长和变长的判空数组

					String[] dataFixed = new String[MAX_ARRAY_LENGTH];// 解析后的定长数据
					String[] dataVariable = new String[MAX_ARRAY_LENGTH];// 解析后的变长数据
					dataFixed = ana.anaFixedType(fixedType, fixedNum, fixedLength, rowlog, isFixedNull);
					String row_log = dataFixed[fixedNum];
					if (variableNum > 0) {
						dataVariable = ana.anaVariableType(variableType, variableNum, totalNum, rowlog, row_log,
								isVariableNull, dataText);
					}

					String SQLExpression = String.format("delete from %s where %s = '%s'", fullTable, primaryKey,
							dataVariable[0]);
					System.out.println(SQLExpression + "\n");// 打印SQL语句
					LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 下一事务
				}

			} else if ("UPDATE".equals(transaction)) {// transaction name（UPDATE）满足,为update操作
				String[] logText = new String[MAX_ARRAY_LENGTH];// text/ntext/image型数据
				String operation1 = "";
				String context1 = "";
				LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);// 转向下一条日志
				Statement sta1 = con.createStatement();
				ResultSet rs1 = sta1
						.executeQuery(String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
				while (rs1.next()) {
					operation1 = rs1.getString("operation");// 操作类型
					context1 = rs1.getString("context");// 上下文
				}
				rs1.close();
				sta1.close();
				int textNum = 0; // 专门存放text/ntext/image型数据的数组的序号
				while (true) {
					if (operation1.equals("LOP_MODIFY_ROW") == false && operation1.equals("LOP_MODIFY_COLUMNS") == false
							&& operation1.equals("LOP_INSERT_ROWS") == false) {// 无用日志，保存下一条的信息和LSN号
						LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
						try {
							Statement sta2 = con.createStatement();
							ResultSet rs2 = sta2.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (rs2.next()) {
								operation1 = rs2.getString("Operation");// 操作类型
								context1 = rs2.getString("Context");// 上下文
							}
							rs2.close();
							sta2.close();
						} catch (SQLException e) {
							break;
						}

					} else if ((operation1.equals("LOP_MODIFY_ROW") || operation1.equals("LOP_MODIFY_COLUMNS"))&&context1.equals("LCX_CLUSTERED")) {// 普通修改
						String[] dataKey = new String[MAX_ARRAY_LENGTH];// 解析后的主键数据
						String[] data1 = new String[MAX_ARRAY_LENGTH];// 解析后的修改前数据
						String[] data2 = new String[MAX_ARRAY_LENGTH];// 解析后的修改后数据
						String rowlog0 = "";// 含修改前数据的十六进制字符串
						String rowlog1 = "";// 含修改后数据的十六进制字符串
						String rowlog = "";// 含主键数据的十六进制字符串

						Statement sta01 = con.createStatement();
						ResultSet rs01 = sta01.executeQuery(
								String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));

						String primaryKey = "";// 主键
						int offset = 0;// 修改的起点
						int modify = 0;//修改的大小
						String[] keyType = new String[MAX_ARRAY_LENGTH];// 主键数据类型
						String unit = "";// 含表名字段
						while (rs01.next()) {
							rowlog = rs01.getString("RowLog Contents 2");
							rowlog1 = rs01.getString("RowLog Contents 1");
							rowlog0 = rs01.getString("RowLog Contents 0");
							offset = rs01.getInt("Offset in Row");
							unit = rs01.getString("AllocUnitName");
							modify = rs01.getInt("Modify Size");
						}
						String fullTable = "";// 含架构名的表名
						if (unit.indexOf(".PK_") == -1) {
							fullTable = unit;
						} else {
							fullTable = unit.substring(0, unit.indexOf(".PK_"));
						}
						String table = getTableName(unit);// 截取表名（不含架构名，防止查元数据时出错）
						rs01.close();
						sta01.close();

						try {
							String sqlKey = String.format(
									"select column_name from information_schema.CONSTRAINT_COLUMN_USAGE where [TABLE_NAME]='%s'",
									table);
							Statement sta2 = con.createStatement();
							ResultSet rs2 = sta2.executeQuery(sqlKey);
							while (rs2.next()) {
								primaryKey = rs2.getString("column_name");
							}
							rs2.close();
							sta2.close();
						} catch (SQLException e) {
						} // 获得主键的列名

						// 获得主键的数据类型
						String sqlKeyData = String.format(
								"select * from information_schema.COLUMNS where TABLE_NAME='%s'and COLUMN_NAME='%s'",
								table, primaryKey);
						Statement sta2 = con.createStatement();
						ResultSet rs2 = sta2.executeQuery(sqlKeyData);
						while (rs2.next()) {
							keyType[0] = rs2.getString("data_type");
						}

						int precisionk = 0, scalek = 0;
						int[] keyLength = new int[1];
						String SQLKeyData = String.format(
								"select * from information_schema.COLUMNS where TABLE_NAME='%s'and COLUMN_NAME='%s'",
								table, primaryKey);
						Statement sta4 = con.createStatement();
						ResultSet rs4 = sta4.executeQuery(SQLKeyData);

						if ("decimal".equals(keyType[0]) || "numeric".equals(keyType[0])) {
							while (rs4.next()) {
								precisionk = Integer.parseInt(rs4.getString("NUMERIC_PRECISION"));// 存浮点数总长
								scalek = Integer.parseInt(rs4.getString("NUMERIC_SCALE"));// 存浮点数小数长度
							}
							if (precisionk <= 9) {
								keyLength[0] = 5;
							} else if (10 <= precisionk && precisionk <= 19) {
								keyLength[0] = 9;
							} else if (20 <= precisionk && precisionk <= 28) {
								keyLength[0] = 13;
							} else {
								keyLength[0] = 17;
							}
						} else if ("char".equals(keyType[0]) || "nchar".equals(keyType[0])
								|| "binary".equals(keyType[0])) {
							while (rs4.next()) {
								keyLength[0] = Integer.parseInt(rs4.getString("CHARACTER_MAXIMUM_LENGTH"));
							}
						} else {
							keyLength[0] = 0;
						}
						rs4.close();
						sta4.close();
						int[] isNull = { 0 };// 主键不能为空
						if (isFixed(keyType[0]) == 1) {
							rowlog = String.format("%s%s", "000000", rowlog);// 含数据的十六进制字符串
							keyLength = ana.fixedNulltoLength(keyType, keyLength, 1);// 调用fixedTypeTotalLength方法，将keyLength数据转化为数字
							dataKey = ana.anaFixedType(keyType, 1, keyLength, rowlog, isNull);
							if ("decimal".equals(keyType[0]) || "numeric".equals(keyType[0])) {
								if (dataKey[0] == null) {
									dataKey[0] = null;
								} else if (dataKey[0].equals("0") == false) {
									if (dataKey[0].indexOf("-") == 0) {// 负数
										dataKey[0] = dataKey[0].substring(1);
										while (dataKey[0].length() < precisionk) {
											dataKey[0] = String.format("%s%s", "0", dataKey[0]);
										} // 左边补0
										dataKey[0] = String.format("%s.%s",
												dataKey[0].substring(0, (dataKey[0].length() - scalek)),
												dataKey[0].substring(dataKey[0].length() - scalek));
										dataKey[0] = dataKey[0].replaceAll("^(0+)", "");// 删除多余的0
										if (dataKey[0].indexOf(".") == 0) {// 小数点左边添加0
											dataKey[0] = "0" + dataKey[0];
										} else if (dataKey[0].indexOf(".") == (dataKey[0].length() - 1)) {// 删去整数的小数点
											dataKey[0] = dataKey[0].substring(0, dataKey[0].length() - 1);
										}
										dataKey[0] = "-" + dataKey[0];
									} else {// 正数
										while (dataKey[0].length() < precisionk) {
											dataKey[0] = String.format("%s%s", "0", dataKey[0]);
										} // 左边补0
										dataKey[0] = String.format("%s.%s",
												dataKey[0].substring(0, (dataKey[0].length() - scalek)),
												dataKey[0].substring(dataKey[0].length() - scalek));
										dataKey[0] = dataKey[0].replaceAll("^(0+)", "");// 删除多余的0
										if (dataKey[0].indexOf(".") == 0) {// 小数点左边添加0
											dataKey[0] = "0" + dataKey[0];
										} else if (dataKey[0].indexOf(".") == (dataKey[0].length() - 1)) {// 删去整数的小数点
											dataKey[0] = dataKey[0].substring(0, dataKey[0].length() - 1);
										}
									}
								} else {
									dataKey[0] = "0";
								}
							} else if ("money".equals(keyType[0]) || "smallmoney".equals(keyType[0])) {
								dataKey[0] = String.format("%s.%s", keyType[0].substring(0, keyType[0].length() - 4),
										keyType[0].substring(keyType[0].length() - 4));
							}
						} else {
							dataKey = ana.anaVariableType(keyType, 1, 1, rowlog, rowlog.substring(2), isNull, logText);
						}

						// 首先解析主键，保存在dataKey[0]

						String[] dataName = new String[MAX_ARRAY_LENGTH];// 建表的列名
						String[] dataType = new String[MAX_ARRAY_LENGTH];// 建表的数据类型
						String[] fixedType = new String[MAX_ARRAY_LENGTH];// 定长数据类型
						String[] variableType = new String[MAX_ARRAY_LENGTH];// 变长数据类型
						int fixedNum = 0, variableNum = 0;// 定长列数，变长列数
						int totalNum = 0;// 总列数
						int[] fixedLength = new int[MAX_ARRAY_LENGTH];// 定长类型长度
						int[] precision = new int[MAX_ARRAY_LENGTH];// 浮点数总长
						int[] scale = new int[MAX_ARRAY_LENGTH];// 浮点小数长度
						int[] isColumnNull = new int[MAX_ARRAY_LENGTH];// 全部列判空
						int[] isFixedNull = new int[MAX_ARRAY_LENGTH];// 定长列判空
						int[] isVariableNull = new int[MAX_ARRAY_LENGTH];// 变长列判空
						int[] isColumNull = new int[MAX_ARRAY_LENGTH];// 全部列判空(不含text)

						String sqlSys = String
								.format("select * from information_schema.COLUMNS where [TABLE_NAME]='%s'", table);// 查系统表
						Statement sta3 = con.createStatement();
						ResultSet rs3 = sta3.executeQuery(sqlSys);
						int rowCount = 0;
						while (rs3.next()) {
							rowCount++;
						} // 得到当前行号，即结果集记录数
						rs3.close();
						sta3.close();
						int i = 0, m = 0, n = 0;
						for (int t = 0; t < rowCount; t++) {// 逐行查系统元数据库得数据信息
							sqlSys = String.format(
									"select * from information_schema.COLUMNS where [TABLE_NAME]='%s' and ORDINAL_POSITION=%d",
									table, i + 1);
							Statement st = con.createStatement();
							ResultSet re = st.executeQuery(sqlSys);
							while (re.next()) {
								dataType[i] = re.getString("data_type");// 存建表数据类型
								dataName[i] = re.getString("COLUMN_NAME");// 存建表列名
								if (isFixed(dataType[i]) == 1) {
									fixedNum++;// 存定长列数
									if (dataType[i].equals("decimal") || dataType[i].equals("numeric")) {
										precision[m] = Integer.parseInt(re.getString("NUMERIC_PRECISION"));// 存浮点数总长
										scale[m] = Integer.parseInt(re.getString("NUMERIC_SCALE"));// 存浮点数小数长度
										if (precision[m] <= 9) {
											fixedLength[m] = 5;
										} else if (10 <= precision[m] && precision[m] <= 19) {
											fixedLength[m] = 9;
										} else if (20 <= precision[m] && precision[m] <= 28) {
											fixedLength[m] = 13;
										} else {
											fixedLength[m] = 17;
										}
									} else if (dataType[i].equals("char") || dataType[i].equals("nchar")
											|| dataType[i].equals("binary")) {
										fixedLength[m] = Integer.parseInt(re.getString("CHARACTER_MAXIMUM_LENGTH"));
									} else {
										fixedLength[m] = 0;
									} // 存定长类型长度，浮点数特殊处理
									fixedType[m] = re.getString("data_type");// 存定长数据类型
									m++;
									i++;
								} else {
									variableNum++;// 存变长列数
									if (Integer.parseInt(re.getString("CHARACTER_MAXIMUM_LENGTH")) == -1) {// 判断变长类型是否为max
										variableType[n] = (re.getString("data_type")) + "max";
									} else {
										variableType[n] = re.getString("data_type");
									} // 存变长类型
									n++;
									i++;
								}
							}
							re.close();
							st.close();
						}
						totalNum = fixedNum + variableNum;
						fixedLength = ana.fixedNulltoLength(fixedType, fixedLength, fixedNum);// 调用fixedTypeTotalLength方法，将fixedLength数据全部转化为数字
						// 获得建表的数据类型和列名，定长数据类型、长度、列数量，变长数据类型、列数量

						String[] fixedData=new String[MAX_ARRAY_LENGTH];//现在表中的定长数据
						String[] variableData=new String[MAX_ARRAY_LENGTH];//现在表中的变长数据
						String sqlRow = String.format("select * from %s where %s ='%s'", fullTable, primaryKey, dataKey[0]);
						String isVnull="";//变长列判空
						m = 0;
						n = 0;
						i = 0;

						for (int j = 0; j < totalNum; j++) {
							Statement sta = con.createStatement();
							ResultSet rs= sta.executeQuery(sqlRow);						
							while (rs.next()) {// 获取每一列的插入值
								data2[i]= rs.getString(dataName[i]);
								if(dataType[i].equals("numeric")||dataType[i].equals("decimal")) {
									
									if(data2[i]!=null) {
										data2[i] =rs.getBigDecimal(dataName[i]).toPlainString();// 存全部数据(浮点数),避免输出科学计数法	
									}
								}else if(dataType[i].equals("smalldatetime")){
									if(data2[i]!=null) {
										data2[i] = rs.getString(dataName[i]).substring(0,16);// 存时间数据	
									}
								}else if(dataType[i].equals("time")){
									if(data2[i]!=null) {
										data2[i] = rs.getString(dataName[i]).substring(0,8);// 存时间数据	
									}
								}else {
									data2[i] = rs.getString(dataName[i]);// 存全部数据	
								}
								
								if (data2[i] == null) {
									isColumnNull[i] = 1;
									isColumNull[i] = 1;// 全部列判空
									if(dataType[i].equals("text")||dataType[i].equals("ntext")||dataType[i].equals("image")) {
										isColumNull[i] = 0;
									}
								} else {
									isColumnNull[i] = 0;
									isColumNull[i]=0;
									data2[i]=data2[i].replaceAll("\\s+$","");
								} // 全部列空值情况
								if(isFixed(dataType[i])==1) {//存定长数据
									fixedData[m]=data2[i];
									m++;
								}else {//存变长数据 
									variableData[n]=data2[i];
									if (data2[i] == null&&dataType[i].equals("text")==false&&dataType[i].equals("ntext")==false&&dataType[i].equals("image")==false) {
										isVnull= isVnull+"1";
									}else {
										isVnull= isVnull+"0";
									}
									n++;
								}
								if((dataType[i].equals("binary")||dataType[i].equals("varbinary")||dataType[i].equals("varbinarymax")||dataType[i].equals("image"))&&data2[i]!=null) {
									data2[i]="0x"+(data2[i].replaceAll("^(0+)", "")).replaceAll("0+$", "");
								}//二进制数据加0x
							}
							i++;
							rs.close();
							sta.close();
						}
						int[] isUpdateNull;// 修改的列判空
						String[] col;// 被修改的列名
						String[] data;// 被修改的数据
						if (modify!=0) {
							String bitmap = intToString(isColumNull);// 二进制字符串
							bitmap = new StringBuilder(bitmap).reverse().toString();// 字符串翻转
							String bitmapHex = Integer.toHexString(Integer.parseInt(bitmap, 2));// 转十六进制
							int bitmapLength = (int) (Math
									.ceil((Double.valueOf(fixedNum) + Double.valueOf(variableNum)) / 8));// 位图的字节数
							while (bitmapHex.length() < bitmapLength * 2) {
								bitmapHex = String.format("%s%s", "0", bitmapHex);
							} // 左边补0
							bitmap = transition(bitmapHex);// 字节序转换，生成位图
							Joint join = new Joint();
							String fullRowlog1 = join.update(fixedType, variableData, bitmap, fixedData, variableType,
									fixedLength, isVnull, fixedNum, variableNum,precision); //生成完整的rowlog1
							String str1 = fullRowlog1.substring(0, offset * 2);
							String str2 = fullRowlog1.substring(offset * 2 + rowlog1.length());
							rowlog0 = String.format("%s%s%s", str1, rowlog0, str2);// 完整的rowlog0
							int fixedTotal = 0;
							for (int x : fixedLength) {
								fixedTotal += x;
							} // 求定长数据所占的总长度
							bitmapLength = (int) (Math
									.ceil((Double.valueOf(fixedNum) + Double.valueOf(variableNum)) / 8));// 位图的字节数
							bitmapHex = rowlog0.substring(12 + fixedTotal * 2, 12 + fixedTotal * 2 + bitmapLength * 2);// 根据rowlog0获得修改前数据的位图
							bitmap = "";
							bitmap = Integer.toBinaryString(Integer.parseInt(transition(bitmapHex), 16));// 转换字节序并转换成二进制
							while (bitmap.length() < bitmapLength * 8) {
								bitmap = String.format("%s%s", "0", bitmap);
							} // 左边补0
							bitmap = new StringBuilder(bitmap).reverse().toString();// 倒置字符串
							int i0 = 0, j0 = 0;
							for (int t = 0; t < (fixedNum + variableNum); t++) {
								if (isFixed(dataType[t]) == 1) {
									isFixedNull[i0] = Integer.parseInt(bitmap.substring(t, t + 1));
									i0++;
								} else {
									isVariableNull[j0] = Integer.parseInt(bitmap.substring(t, t + 1));
									j0++;
								}
							} // 将空值信息分别存储到定长和变长的判空数组
							String[] dataFixed = new String[MAX_ARRAY_LENGTH];// 解析后的修改前定长数据
							String[] dataVariable = new String[MAX_ARRAY_LENGTH];// 解析后的修改前变长数据
							dataFixed = ana.anaFixedType(fixedType, fixedNum, fixedLength, rowlog0, isFixedNull);
							String row_log = dataFixed[fixedNum];
							if (variableNum > 0) {
								dataVariable = ana.anaVariableType(variableType, variableNum, totalNum, rowlog0,
										row_log, isVariableNull, logText);
							}
							int m1 = 0, n1 = 0;
							for (int t = 0; t < totalNum; t++) {
								if (isFixed(dataType[t]) == 1) {// 给浮点型加小数点
									if ("decimal".equals(dataType[t]) || "numeric".equals(dataType[t])) {
										if (dataFixed[m1] == null) {
											data1[t] = null;
										} else if (dataFixed[m1].equals("0") == false) {
											if (dataFixed[m1].indexOf("-") == 0) {// 负数
												dataFixed[m1] = dataFixed[m1].substring(1);
												while (dataFixed[m1].length() < precision[m1]) {
													dataFixed[m1] = String.format("%s%s", "0", dataFixed[m1]);
												} // 左边补0
												data1[t] = String.format("%s.%s",
														dataFixed[m1].substring(0,
																(dataFixed[m1].length() - scale[m1])),
														dataFixed[m1].substring(dataFixed[m1].length() - scale[m1]));
												data1[t] = data1[t].replaceAll("^(0+)", "");// 删除多余的0
												if (data1[t].indexOf(".") == 0) {// 小数点左边添加0
													data1[t] = "0" + data1[t];
												} else if (data1[t].indexOf(".") == (data1[t].length() - 1)) {// 删去整数的小数点
													data1[t] = data1[t].substring(0, data1[t].length() - 1);
												}
												data1[t] = "-" + data1[t];
											} else {// 正数
												while (dataFixed[m1].length() < precision[m1]) {
													dataFixed[m1] = String.format("%s%s", "0", dataFixed[m1]);
												} // 左边补0
												data1[t] = String.format("%s.%s",
														dataFixed[m1].substring(0,
																(dataFixed[m1].length() - scale[m1])),
														dataFixed[m1].substring(dataFixed[m1].length() - scale[m1]));
												data1[t] = data1[t].replaceAll("^(0+)", "");// 删除多余的0
												if (data1[t].indexOf(".") == 0) {// 小数点左边添加0
													data1[t] = "0" + data1[t];
												} else if (data1[t].indexOf(".") == (data1[t].length() - 1)) {// 删去整数的小数点
													data1[t] = data1[t].substring(0, data1[t].length() - 1);
												}
											}
										} else {
											data1[t] = dataFixed[m1];
										}
										m1++;
									} else if ("money".equals(dataType[t]) || "smallmoney".equals(dataType[t])) {
										if (dataFixed[m1] == null) {
											data1[t] = null;
										} else if(dataFixed[m1].equals("0")){
											data1[t]="0";
										}else {
											data1[t] = String.format("%s.%s",
													dataFixed[m1].substring(0, dataFixed[m1].length() - 4),
													dataFixed[m1].substring(dataFixed[m1].length() - 4));
										}
										m1++;
									} else {
										data1[t] = dataFixed[m1];
										m1++;
									}
								} else {
									data1[t] = dataVariable[n1];
									n1++;
								}

							} // 将定长和变长数据按建表顺序合并,存放在data1中
							isUpdateNull = new int[MAX_ARRAY_LENGTH];
							col = new String[MAX_ARRAY_LENGTH];
							data = new String[MAX_ARRAY_LENGTH];
							int b = 0;
							for (int a = 0; a < totalNum; a++) {
								try {
									if (data1[a].equals(data2[a]) == false) {
										isUpdateNull[b] = isColumnNull[a];
										col[b] = dataName[a];
										data[b] = data2[a];
										b++;
									}
								} catch (NullPointerException e1) {
									try {
										if (data2[a].equals(data1[a]) == false) {
											isUpdateNull[b] = isColumnNull[a];
											col[b] = dataName[a];
											data[b] = data2[a];
											b++;
										}
									} catch (NullPointerException e2) {
									}
								}
							} // 比较data1和data2，只需打印修改的部分
						}else {
							col = new String[MAX_ARRAY_LENGTH];
							data = new String[MAX_ARRAY_LENGTH];
							isUpdateNull = new int[MAX_ARRAY_LENGTH];
							int p=0,q=0;
							for(;p<totalNum;p++) {
								if(dataName[p].equals(primaryKey)==false) {
									col[q]=dataName[p];
									data[q]=data2[p];
									if(data[q]==null) {
										isUpdateNull[q]=1;
									}else {
										isUpdateNull[q]=0;
									}
									q++;
								} 
							}
						}
						String SQLExpression = String.format("update %s set ", fullTable);
						int t = 0;
						while (col[t] != null) {
							if (isUpdateNull[t] == 0) {
								SQLExpression = String.format("%s%s='%s',", SQLExpression, col[t], data[t]);
								t++;
							} else {
								SQLExpression = String.format("%s%s=%s,", SQLExpression, col[t], data[t]);
								t++;
							}
						}
						SQLExpression = SQLExpression.substring(0, SQLExpression.length() - 1);// 去掉多余的逗号
						SQLExpression = String.format("%s where %s='%s'", SQLExpression, primaryKey, dataKey[0]);
						System.out.println(SQLExpression + "\n");// 打印SQL语句
						LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 下一事务
						break;

					} else if ((operation1.equals("LOP_MODIFY_ROW") || operation1.equals("LOP_INSERT_ROWS")
							|| operation1.equals("LOP_MODIFY_COLUMNS")) && context1.equals("LCX_TEXT_MIX")) {// 修改text/ntext/image型数据
						String rowlog0 = "";// 含修改前数据的十六进制字符串
						String rowlog1 = "";// 含修改后数据的十六进制字符串
						Statement sta2 = con.createStatement();
						ResultSet rs2 = sta2.executeQuery(
								String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
						while (rs2.next()) {
							operation1 = rs2.getString("Operation");// 操作类型
							context1 = rs2.getString("Context");// 上下文
							rowlog0 = rs2.getString("RowLog Contents 0");
							rowlog1 = rs2.getString("RowLog Contents 1");
						}
						rs2.close();
						sta2.close();

						// 对text/ntext/image型数据的不同修改方式分别解析
						if (operation1.equals("LOP_INSERT_ROWS") && rowlog0.length() > 40) {// null->短text/ntext/image
							logText[textNum] = null;
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement sta = con.createStatement();
							ResultSet rs = sta.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (rs.next()) {
								operation1 = rs.getString("Operation");// 操作类型
								context1 = rs.getString("Context");// 上下文
							}
							rs.close();
							sta.close();
						} else if (operation1.equals("LOP_INSERT_ROWS") && rowlog0.length() < 40) {// null->长text/ntext/image
																									// 或 短->长
							// 判断下一行modify的rowlog1长度
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st = con.createStatement();
							ResultSet r = st.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r.next()) {
								operation1 = r.getString("Operation");// 操作类型
								context1 = r.getString("Context");// 上下文
								rowlog1 = r.getString("RowLog Contents 1");// 修改前数据
							}
							r.close();
							st.close();
							while (operation1.equals("LOP_MODIFY_ROW") == false
									&& operation1.equals("LOP_MODIFY_COLUMNS") == false) {
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st1 = con.createStatement();
								ResultSet re1 = st1.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
								while (re1.next()) {
									operation1 = re1.getString("Operation");// 操作类型
									context1 = re1.getString("Context");// 上下文
									rowlog1 = re1.getString("RowLog Contents 1");// 修改前数据
								}
								re1.close();
								st1.close();
							} // 寻找下一条modify
							if (rowlog1.length() > 128) {// null->长
								logText[textNum] = null;
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement sta0 = con.createStatement();
								ResultSet re0 = sta0.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
								while (re0.next()) {
									operation1 = re0.getString("Operation");// 操作类型
									context1 = re0.getString("Context");// 上下文
								}
								re0.close();
								sta0.close();
								while (operation1.equals("LOP_INSERT_ROWS") == false) {
									LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
									Statement st2 = con.createStatement();
									ResultSet re2 = st2.executeQuery(
											String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
									while (re2.next()) {
										operation1 = re2.getString("Operation");// 操作类型
										context1 = re2.getString("Context");// 上下文
									}
									re2.close();
									st2.close();
								}
								// 跳过下一条insert
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st4 = con.createStatement();
								ResultSet r4 = st4.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
								while (r4.next()) {
									operation1 = r4.getString("Operation");// 操作类型
									context1 = r4.getString("Context");// 上下文
								}
								r4.close();
								st4.close();
							} else {// 短->长
								logText[textNum] = "0000000000000000000000000000000000000000" + rowlog1;
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st0 = con.createStatement();
								ResultSet rs0 = st0.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
								while (rs0.next()) {
									operation1 = rs0.getString("Operation");// 操作类型
									context1 = rs0.getString("Context");// 上下文
								}
								rs0.close();
								st0.close();
								for (int i = 0; i < 3; i++) {// 跳过下三条modify
									while (operation1.equals("LOP_MODIFY_ROW") == false
											&& operation1.equals("LOP_MODIFY_COLUMNS") == false) {
										LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
										Statement st2 = con.createStatement();
										ResultSet re2 = st2.executeQuery(String
												.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
										while (re2.next()) {
											operation1 = re2.getString("Operation");// 操作类型
											context1 = re2.getString("Context");// 上下文
										}
										re2.close();
										st2.close();
									}
									// 跳过下一条modify
									LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
									Statement st3 = con.createStatement();
									ResultSet r3 = st3.executeQuery(
											String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
									while (r3.next()) {
										operation1 = r3.getString("Operation");// 操作类型
										context1 = r3.getString("Context");// 上下文
									}
									r3.close();
									st3.close();
								}
							}
						} else if ((operation1.equals("LOP_MODIFY_ROW") || operation1.equals("LOP_MODIFY_COLUMNS"))
								&& rowlog1.length() == 0) {// 长->长
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st = con.createStatement();
							ResultSet r = st.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r.next()) {
								operation1 = r.getString("Operation");// 操作类型
								context1 = r.getString("Context");// 上下文
								rowlog0 = r.getString("RowLog Contents 0");// 修改前数据
							}
							r.close();
							st.close();
							while (operation1.equals("LOP_MODIFY_ROW") == false
									&& operation1.equals("LOP_MODIFY_COLUMNS") == false) {
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st1 = con.createStatement();
								ResultSet re1 = st1.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
								while (re1.next()) {
									operation1 = re1.getString("Operation");// 操作类型
									context1 = re1.getString("Context");// 上下文
									rowlog0 = re1.getString("RowLog Contents 0");// 修改前数据
								}
								re1.close();
								st1.close();
							} // 寻找下一条modify
							logText[textNum] = "0000000000000000000000000000000000000000" + rowlog0;// 修改前数据

							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st2 = con.createStatement();
							ResultSet r2 = st2.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r2.next()) {
								operation1 = r2.getString("Operation");// 操作类型
								context1 = r2.getString("Context");// 上下文
							}
							r2.close();
							st2.close();
							while (operation1.equals("LOP_MODIFY_ROW") == false
									&& operation1.equals("LOP_MODIFY_COLUMNS") == false) {
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st3 = con.createStatement();
								ResultSet re3 = st3.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
								while (re3.next()) {
									operation1 = re3.getString("Operation");// 操作类型
									context1 = re3.getString("Context");// 上下文
								}
								re3.close();
								st3.close();
							}
							// 跳过下一条modify
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement sta3 = con.createStatement();
							ResultSet r3 = sta3.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r3.next()) {
								operation1 = r3.getString("Operation");// 操作类型
								context1 = r3.getString("Context");// 上下文
							}
							r3.close();
							sta3.close();
						} else if ((operation1.equals("LOP_MODIFY_ROW") || operation1.equals("LOP_MODIFY_COLUMNS"))
								&& rowlog1.length() != 0) {// 短->短 或 短->null
							logText[textNum] = rowlog0;// 修改前数据
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st = con.createStatement();
							ResultSet r = st.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r.next()) {
								operation1 = r.getString("Operation");// 操作类型
								context1 = r.getString("Context");// 上下文
							}
							r.close();
							st.close();
						} else if (operation1.equals("LOP_DELETE_ROWS")) {// 长->null
							logText[textNum] = "0000000000000000000000000000000000000000" + rowlog0.substring(28);// 修改前数据
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st = con.createStatement();
							ResultSet r = st.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r.next()) {
								operation1 = r.getString("Operation");// 操作类型
								context1 = r.getString("Context");// 上下文
							}
							r.close();
							st.close();
							while (operation1.equals("LOP_MODIFY_ROW") == false
									&& operation1.equals("LOP_MODIFY_COLUMNS") == false) {
								LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
								Statement st2 = con.createStatement();
								ResultSet re2 = st2.executeQuery(
										String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));// 下一条日志
								while (re2.next()) {
									operation1 = re2.getString("Operation");// 操作类型
									context1 = re2.getString("Context");// 上下文
								}
								re2.close();
								st2.close();
							}
							// 跳过下一条modify
							LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
							Statement st3 = con.createStatement();
							ResultSet r3 = st3.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (r3.next()) {
								operation1 = r3.getString("Operation");// 操作类型
								context1 = r3.getString("Context");// 上下文
							}
							r3.close();
							st3.close();

						}
						textNum++;// text/ntext/image型数据的列数
					}else {
						LSN_ = nextFinalLSN(LSN_, userName, userKey, userDatabase);
						try {
							Statement sta2 = con.createStatement();
							ResultSet rs2 = sta2.executeQuery(
									String.format("SELECT * FROM [sys].[fn_dblog]('0x%s','0x%s')", LSN_, LSN_));
							while (rs2.next()) {
								operation1 = rs2.getString("Operation");// 操作类型
								context1 = rs2.getString("Context");// 上下文
							}
							rs2.close();
							sta2.close();
						} catch (SQLException e) {
							break;
						}
					}
				}

			} else { // 无用的事务日志
				LSN_ = findNext(LSN_, userName, userKey, userDatabase);// 下一事务
			}
		}
	}
}
