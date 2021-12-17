package console;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.text.JTextComponent;

public class TestGUI extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3489274456587352144L;
	static TestGUI tes;
	JTextField jtf1, jtf3,jtf4;//定义3个输入框
	JPasswordField jtf2;
	JTextArea jta = new JTextArea();//定义1个文本域作为全局变量，供监听
	
	//声明解析函数调用的方法

	public static void main(String[] args) {
		TestGUI lo = new TestGUI();
		lo.Face();
    }
	
	OutputSQL outputSQL;				
	public void Face() {
		
		//输出重定向
		class GUIPrintStream extends PrintStream{
			private JTextComponent component;
			private StringBuffer sb = new StringBuffer();
			public GUIPrintStream(OutputStream out, JTextComponent component){
			super(out);
			this.component = component;
			}
			
			/** *//**
			* 重写write()方法，将输出信息填充到GUI组件。
			* @param buf
			* @param off
			* @param len
			*/
			@Override

			public void write(byte[] buf, int off, int len) {
				final String message = new String(buf, off, len);
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						sb.append(message);
						component.setText(sb.toString());
			}
		});
	}
}
		System.setOut(new GUIPrintStream(System.out, jta));
		GUIPrintStream guiPrintStream = new GUIPrintStream(System.out, jta);
		System.setErr(guiPrintStream);
		System.setOut(guiPrintStream);

	//窗口
	setTitle("SQL Reader");
    setSize(1050, 800);//对话框的大小
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭后销毁对话框,退出应用程序
    setLocationRelativeTo(null);//设置居中
   	    
                
	/******************************  row1:大标题 *********************************************/
    //大标题
    JPanel row1 = new JPanel();
    JLabel Jtitle = new JLabel("SQL Server日志解析");
    Jtitle.setHorizontalAlignment(SwingConstants.CENTER);//center label text
    Font fontTitle = new Font("仿宋", Font.PLAIN, 50);
    Jtitle.setFont(fontTitle);
    row1.add(Jtitle);
    row1.setPreferredSize(new Dimension(1050, 70));
    
    /******************************  row2:信息上传 *********************************************/
    //小标题upload
    JPanel row2 = new JPanel(new BorderLayout());
    JLabel Jupload = new JLabel("用户登录:");
    Font fontTitle2 = new Font("仿宋", Font.PLAIN, 22);
    Jupload.setFont(fontTitle2);
    row2.add(Jupload, BorderLayout.NORTH);

    //设置字体格式
    Font jl = new Font("仿宋", Font.PLAIN, 24);
    Font jtf = new Font("仿宋", Font.PLAIN, 24);

    //用户名
    JLabel jl1 = new JLabel("用户名：");
    jl1.setFont(jl);
    jtf1 = new JTextField();
    jtf1.setFont(jtf);
    
    //密码
    JLabel jl2 = new JLabel("密码：");
    jl2.setFont(jl);
	jtf2 = new JPasswordField(30);
	jtf2.setEchoChar('\u25cf');
    jtf2.setFont(jtf);
    
    //数据库名	
    JLabel jl3 = new JLabel("数据库名：");
    jl3.setFont(jl);
    jtf3 = new JTextField();
    jtf3.setFont(jtf);        
    
    //LSN号
    JLabel jl4 = new JLabel("LSN号（X:X:X）");
    jl4.setFont(jl);
    jtf4 = new JTextField();
    jtf4.setFont(jtf);
    
    //jp2布局格式
    GridLayout gridLayout = new GridLayout(2, 4, 20, 20);//行数，列数，水平间距，竖直间距
    JPanel jp2 = new JPanel(gridLayout);
    jp2.setPreferredSize(new Dimension(850, 100));
    jp2.add(jl1);
    jp2.add(jtf1);
    jp2.add(jl2);
    jp2.add(jtf2);
    jp2.add(jl3);
    jp2.add(jtf3);
    jp2.add(jl4);
    jp2.add(jtf4); 
    
    //登录按钮
    JPanel jpB = new JPanel();
    JButton jb = new JButton("开始解析");
    Font fontTitle3 = new Font("仿宋", Font.PLAIN, 20);
    jb.setFont(fontTitle3);
    jb.setPreferredSize(new Dimension(125, 30));       
    jpB.add(jb);
    jpB.setPreferredSize(new Dimension(1050,40));
    
    //给按钮添加动作监听器方法
    SimpleListener but =new SimpleListener();
	jb.addActionListener(but);
	
    //row2布局
    JPanel jp = new JPanel();
    jp.add(jp2);
    row2.add(jp, BorderLayout.CENTER);
    row2.add(jpB, BorderLayout.SOUTH);
    row2.setPreferredSize(new Dimension(1050, 350));
    row2.setBackground(Color.lightGray);
    
    /******************************  row3:信息查询 *********************************************/
    //小标题search
    JPanel row3 = new JPanel(new BorderLayout());
    JLabel Jsearch = new JLabel("日志解析结果:");
    Jsearch.setFont(fontTitle2);
    row3.add(Jsearch, BorderLayout.NORTH);
   
    //输出框                
    jta.setFont(jtf);
    jta.setPreferredSize(new Dimension(900, 500000));
    jta.setLineWrap(true);//设置自动换行
                  
    //滚动面板      
    JScrollPane sp = new JScrollPane(jta);
    row3.add(sp, BorderLayout.CENTER);
                   
    row3.setPreferredSize(new Dimension(1050, 460));
    row3.setBackground(Color.lightGray);
    
    //整体布局
    add(row1, BorderLayout.NORTH);
    add(row2);
    add(row3, BorderLayout.SOUTH);
    
    //设置窗体可见  
    setVisible(true);
	}
	
	//登陆按钮监听函数
	private class SimpleListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			//接下来获取文本框里面的字符串   			
        	String name = jtf1.getText();
			String databasename = jtf3.getText();
			String LSN =  jtf4.getText(); 			
			char ch[] = jtf2.getPassword();
			String pass = new String(ch);
			
			new Thread() {// 新开一条线程,同步线程
				public void run() {
					OutputSQL.outPut(tes,name,pass,databasename,LSN);
				}

			}.start();// 新线程开始执行
        }			
	}
	public void updataGuiDemo() {
		jta.setCaretPosition(jta.getDocument().getLength());
	}
}        
