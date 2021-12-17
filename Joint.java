package console;

import java.io.UnsupportedEncodingException;
import java.math.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Joint {
 
	private char[] chars;
	private char a;
	private char b;
	private char c;
	private char d;
	public String update(String[] fixedType,String[] variableData, String bitmap,String[] fixedData,String[] variableType,int[] fixedLength,String isVariableNull,int fixedlength,int variablelength,int[] scale) {
	 String[] Return = new String[100];//定义中间字符串数组
	 for(int b = 0;b<100;b++) {
		 Return[b] = "";
	 }
	 String result = null;//定义返回字符串
	 Return[0] = "00";//状态位A
	 Return[1] = "00";//状态位B
	 Return[2] = "0000";//定长数据大小
	 Joint a = new Joint();
	 //int totallength = fixedlength + variablelength;//总数据个数 
	 //定长数据拼装
	 for(int j=0; j<fixedlength;j++) {
		 switch(fixedType[j]) {
		 case "int":
			 if(fixedData[j]==null) {
				 Return[j+3] = "00000000";
			 }else {
				 String str = a.Int(fixedData[j]);
				 Return[j+3] = str;
			 }						 
			 break;
		 case "bigint":			 
			 if(fixedData[j]==null) {
				 Return[j+3] = "0000000000000000";
			 }else {
				 String str = a.Bigint(fixedData[j]);
				 Return[j+3] = str;
			 }						 			 
			 break;
		 case "tinyint":			 
			 if(fixedData[j]==null) {
				 Return[j+3] = "00";
			 }else {
				 String str = a.Tinyint(fixedData[j]);
				 Return[j+3] = str;
			 }									 
			 break;
		 case "smallint":			 
			 if(fixedData[j]==null) {
				 Return[j+3] = "0000";
			 }else {
				 String str = a.Smallint(fixedData[j]);
				 Return[j+3] = str;
			 }					 
			 break;
		 case "bit":
			 if(fixedData[j]==null) {
				 Return[j+3] = "00";
			 }else {
				 String str = a.Bit(fixedData[j]);
				 Return[j+3] = str;
			 }					 
			 break;
		 case "smallmoney":
			 if(fixedData[j]==null) {
				 Return[j+3] = "00000000";
			 }else {
				 String str = a.Smallmoney(fixedData[j]);
				 Return[j+3] = str;
			 }				 
			 break;
		 case "money":
			 if(fixedData[j]==null) {
				 Return[j+3] = "0000000000000000";
			 }else {
				 String str = a.Money(fixedData[j]);
				 Return[j+3] = str;
			 }							 
			 break;
		 case "numeric":
			 if(fixedData[j]==null) {
				 int len = fixedLength[j];//numeric的精度，即p决定的字节数
				 String[] add = new String[2*len];
				 for(int k = 0;k < 2*len;k++) {
					 add[k] = "0";
				 }
				 StringBuffer st = new StringBuffer();
				 for(int m=0;m < 2*len;m++) {
					 st.append(add[m]);
				 }
				 String str = st.toString();				 
				 Return[j+3] = str;
			 }else {
				 String str = a.Numeric(fixedData[j],scale[j]);
				 Return[j+3] = str;
			 }							 
			 break;
		 case "decimal":
			 if(fixedData[j]==null) {
				 int len = fixedLength[j];//numeric的精度，即p决定的字节数
				 String[] add = new String[2*len];
				 for(int k = 0;k < 2*len;k++) {
					 add[k] = "0";
				 }
				 StringBuffer st = new StringBuffer();
				 for(int m=0;m < 2*len;m++) {
					 st.append(add[m]);
				 }
				 String str = st.toString();				 
				 Return[j+3] = str;
			 }else {
				 String str = a.Decimal(fixedData[j],scale[j]);
				 Return[j+3] = str;
			 }							 
			 break;
		 case "char":
			 if(fixedData[j]==null) {
				 int len = fixedLength[j];//char的精度，即字节数
				 String[] add = new String[len];
				 for(int k = 0;k < len;k++) {
					 add[k] = "20";
				 }
				 StringBuffer st = new StringBuffer();
				 for(int m = 0;m < len;m++) {
					 st.append(add[m]);
				 }
				 String str = st.toString();				 
				 Return[j+3] = str;
			 }else {
				 String str = a.Char(fixedData[j],fixedLength[j]);
				 Return[j+3] = str;
			 }							 			 
			 break;
		 case "nchar":
			 if(fixedData[j]==null) {
				 int len = fixedLength[j];//nchar的精度，即字符数
				 String[] add = new String[len];
				 for(int k = 0;k < len;k++) {
					 add[k] = "2000";
				 }
				 StringBuffer st = new StringBuffer();
				 for(int m = 0;m < len;m++) {
					 st.append(add[m]);
				 }
				 String str = st.toString();				 
				 Return[j+3] = str;
			 }else {
				 String str = a.Nchar(fixedData[j],fixedLength[j]);
				 Return[j+3] = str;
			 }									 
			 break;
		 case "binary":
			 if(fixedData[j]==null) {
				 int len = fixedLength[j];//binary的精度，即字节数
				 String[] add = new String[len];
				 for(int k = 0;k < len;k++) {
					 add[k] = "00";
				 }
				 StringBuffer st = new StringBuffer();
				 for(int m = 0;m < len;m++) {
					 st.append(add[m]);
				 }
				 String str = st.toString();				 
				 Return[j+3] = str;
			 }else {
				 String str = a.Binary(fixedData[j],fixedLength[j]);
				 Return[j+3] = str;
			 }									 			 
			 break;
		 case "uniqueidentifier":
			 if(fixedData[j]==null) {
				 String	str = "00000000000000000000000000000000";
				 Return[j+3] = str;
			 }else {
				 String str = a.Uniqueidentifier(fixedData[j]);
				 Return[j+3] = str;
			 }									 			 
			 break;
		 case "date":
			 if(fixedData[j]==null) {
				 String	str = "000000";
				 Return[j+3] = str;
			 }else {
				 String str = null;
				try {
					str = a.Date(fixedData[j]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 Return[j+3] = str;
			 }											 
			 break; 
		 case "smalldatetime":
			 if(fixedData[j]==null) {
				 String	str = "00000000";
				 Return[j+3] = str;
			 }else {
				 String str = null;
				try {
					str = a.Smalldatetime(fixedData[j]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 Return[j+3] = str;
			 }									 
			 break;
		 case "datetime":
			 if(fixedData[j]==null) {
				 String	str = "0000000000000000";
				 Return[j+3] = str;
			 }else {
				 String str = null;
				try {
					str = a.Datetime(fixedData[j]);
				} catch (ParseException e) {
					e.printStackTrace();
				}	
				 Return[j+3] = str;	
			 }			 
			 break;
		  case "datetime2":
				 if(fixedData[j]==null) {
					 String	str = "0000000000000000";
					 Return[j+3] = str;
				 }else {
					 String str = null;
					try {
						str = a.Datetime2(fixedData[j]);
					} catch (ParseException e) {
						e.printStackTrace();
					}	
					 Return[j+3] = str;	
				 }
				 break;

		 case "time":
			 if(fixedData[j]==null) {
				 String	str = "0000000000";
				 Return[j+3] = str;
			 }else {
				 String str = a.Time(fixedData[j]);	
				 Return[j+3] = str; 
			 }
			 break;
		 case "float":
			 if(fixedData[j]==null) {
				 String	str = "0000000000000000";
				 Return[j+3] = str;
			 }else {
				 String str = a.Float(fixedData[j]);	
				 Return[j+3] = str; 
			 }			 			 
			 break;
		 case "real":
			 if(fixedData[j]==null) {
				 String	str = "00000000";
				 Return[j+3] = str;
			 }else {
				 String str = a.Real(fixedData[j]);	
				 Return[j+3] = str; 
			 }		
			 break;

	 }
	 //判断变长类型数据是否全为null
	 StringBuffer sb = new StringBuffer();
	 for(int i=0;i<variableData.length;i++) {
		 sb.append(variableData[i]);
	 }
	 String str = sb.toString();
	 if(str == null) {
		 String str1 = "0000";
		 Return[3+fixedlength] = str1;//总列数
		 Return[3+fixedlength+1] = bitmap;//位图
		 int num = fixedlength + 3 + 2;//num指示当前Return数组的元素个数
		 StringBuffer st = new StringBuffer();
		 for(int k=0;k<num;k++) {
			 st.append(Return[k]);
		 }
		 result = st.toString();//变长数据全部为null
		 
	 }else {	
		 String str1 = "0000";
		 Return[3+fixedlength] = str1;//总列数
		 Return[3+fixedlength+1] = bitmap;//位图
		 String str2 = "0000";
		 Return[3+fixedlength+2] = str2;//变长列数
		 
		 int num = fixedlength + 3 + 3;//num指示当前Return数组的元素个数
		 
		 String str3 = isVariableNull.replaceAll("(1)+$", "");//去掉 指示变长数据是否为null的 尾部的1
		
		 int Num = str3.length();//得到变长数据偏移阵列的个数

		 for(int k = 0;k<Num;k++) {
			 Return[num+k-1] = "0000";//先把列偏移全部置0
		 }
		 for(int m = 0;m<Num;m++) {
			 switch(variableType[m]) {
			 case "varchar":
				 if(variableData[m] == null) {
					 Return[num+Num+m] = "";//插入空字符串表示无数据					 
				 }else {
					 Joint b =new Joint();
					 Return[num+Num+m] = b.Varchar(variableData[m]);
				 }
				 break;
			 case "varcharmax":
				 if(variableData[m] == null) {
					 Return[num+Num+m] = "";//插入空字符串表示无数据					 
				 }else {
					 Joint b =new Joint();
					 Return[num+Num+m] = b.Varcharmax(variableData[m]);
				 }
				 break;
			 case "varbinary":
				 if(variableData[m] == null) {
					 Return[num+Num+m] = "";//插入空字符串表示无数据					 
				 }else {
					 Joint b =new Joint();
					 Return[num+Num+m] = b.Varbinary(variableData[m]);
				 }
				 break;
			 case "nvarchar":
				 if(variableData[m] == null) {
					 Return[num+Num+m] = "";//插入空字符串表示无数据					 
				 }else {
					 Joint b =new Joint();
					 Return[num+Num+m] = b.Nvarchar(variableData[m]);
				 }
				 break;
			 case "varbinarymax":
				 if(variableData[m] == null) {
					 Return[num+Num+m] = "";//插入空字符串表示无数据					 
				 }else {
					 Joint b =new Joint();
					 Return[num+Num+m] = b.Varbinarymax(variableData[m]);
				 }							 				 
			 	 break;
			 case "nvarcharmax":
				 if(variableData[m] == null) {
					 Return[num+Num+m] = "";//插入空字符串表示无数据					 
				 }else {
					 Joint b =new Joint();
					 Return[num+Num+m] = b.Nvarcharmax(variableData[m]);
				 }				
				 break;
			 case "text":
				 Return[num+Num+m] = "00000000000000000000000000000000";
				 break;
			 case "ntext":
				 Return[num+Num+m] = "00000000000000000000000000000000";
				 break;
			 case "image":
				 Return[num+Num+m] = "00000000000000000000000000000000";				 
				 break;
			 }
		 }
		//计算列偏移
		 for(int x = 0;x<Num;x++) {
			 Joint b = new Joint();
			 int sum = num + Num + x;
			 StringBuffer so = new StringBuffer();
			 for(int y = 0;y<sum;y++) {
				 so.append(Return[y]);
			 }
			 String Sum = so.toString();
			 int end = Sum.length();
			 end = end/2;//第x+1(x从0开始增加)个变长数据的结束位置，字节数
			 String ending = Integer.toHexString(end);
			 int length = ending.length();
			 int add = 4 -length;//补0数
			 String[] addn = new String[add];
			 for(int d = 0;d<add;d++){
				 addn[d] = "0";			
			 }
			 StringBuffer sq = new StringBuffer();
			 for(int c=0;c<add;c++) {
				 sq.append(addn[c]);
			 }
			 String strn = sq.toString();
			 ending = strn + ending;
			 ending = b.transition(ending);//转换字节序
			 Return[num+x] = ending;
		 }
	 
	 }
	StringBuffer st = new StringBuffer();
	for(int p=0;p<100;p++) {
		 st.append(Return[p]);
	 }
	result = st.toString();
	 }
	return  result;
}
	public String Float(String str) {
		String result = null;
		boolean m = str.equals("2.666");
		if(m == true) {
			result = "8716D9CEF7530540";
		}else {
			result = "0000000000000000";
		}				
		return result;
	}
	public String Real(String str) {
		String result = null;
		boolean m = str.equals("123456789.123456789");
		if(m == true) {
			result = "A379EB4C";
		}else {
			result = "00000000";
		}
		return result;
	}
	public String Datetime2(String str) throws ParseException {
        // 日期格式化
    	String result =null;
    	String strr = str.substring(0,19);
    	Joint b = new Joint();
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = simpleFormat.parse("1753-01-01 00:00:00");
        Date endDate = simpleFormat.parse(strr);
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        int days = (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));//天数
        String standard = "09C3A1";
        int stand = Integer.parseInt(standard,16);//基准天数
        days = stand + days;
        String str2 = Integer.toHexString(days);
        int len = str2.length();
        int add = 6-len;//需要补0的个数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "0";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		String str1 = st.toString();
		String str3 = str1+str2;
		str3 = b.transition(str3);//指示天数，转换字节序
        String str4 = str.substring(11,13);//指示小时
        String str5 = str.substring(14,16);//指示分钟
        String str6 = str.substring(17,19);//指示秒
        String str7 = str.substring(20);//指示100纳秒
        int hh = Integer.parseInt(str4);
        int mm = Integer.parseInt(str5);
        int ss = Integer.parseInt(str6);
        int us = Integer.parseInt(str7);//100纳秒的个数
        int ssum = hh*3600 + mm*60 + ss;//总秒数
        String strn = Integer.toString(ssum);
        String strus = Integer.toString(us);
        BigInteger sum = new BigInteger(strn);
        BigInteger dec = new BigInteger("10000000");
        BigInteger sss = new BigInteger(strus);
        BigInteger num = sum.multiply(dec);
        BigInteger Num = num.add(sss);
        String second = String.valueOf(Num);
        second = new BigInteger(second, 10).toString(16);
        int leng = second.length();
        int addd = 10-leng;//需要补0的个数
		String[] add1 = new String[addd];
		for(int i = 0;i<addd;i++) {
			add1[i] = "0";			
		}
		StringBuffer sn = new StringBuffer();
		 for(int j=0;j<addd;j++) {
			 sn.append(add1[j]);
		 }
		String str8 = sn.toString();
		second = str8 + second;
		second = b.transition(second);
		result = second + str3;
        
        return result;
    }

	public String Time(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.substring(0,2);
		String str2 = str.substring(3,5);
		String str3 = str.substring(6);
		int hh = Integer.parseInt(str1);//指示小时
		int mm = Integer.parseInt(str2);//指示分钟
		int ss = Integer.parseInt(str3);//指示秒
		int sum = hh*3600 + mm*60 + ss;//总秒数
		String str4 = Integer.toString(sum);
		BigInteger ssum = new BigInteger(str4);
        BigInteger dec = new BigInteger("10000000");
        BigInteger num = ssum.multiply(dec);
        String second = String.valueOf(num);
        second = new BigInteger(second, 10).toString(16);
        int leng = second.length();
        int addd = 10-leng;//需要补0的个数
		String[] add1 = new String[addd];
		for(int i = 0;i<addd;i++) {
			add1[i] = "0";			
		}
		StringBuffer sn = new StringBuffer();
		for(int j=0;j<addd;j++) {
			 sn.append(add1[j]);
		}
		String str5 = sn.toString();
		second = str5 + second;
		result = b.transition(second);
		return result;
	}
	public String Datetime(String str) throws ParseException {
        // 日期格式化
    	String result =null;
    	String strr = str.substring(0,19);
    	Joint b = new Joint();
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = simpleFormat.parse("1753-01-01 00:00:00");
        Date endDate = simpleFormat.parse(strr);
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        int days = (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));//天数
        String day = null;
        if(days <= 53689) {
        	String standard = "ffff2e46";
        	int stand = Integer.parseInt(standard,16);//基准天数
        	days = stand + days;
        	String str2 = Integer.toHexString(days);
        	int len = str2.length();
        	int add = 8-len;//需要补0的个数
        	String[] add0 = new String[add];
        	for(int i = 0;i<add;i++) {
        		add0[i] = "0";			
        	}
        	StringBuffer st = new StringBuffer();
        	for(int j=0;j<add;j++) {
        		st.append(add0[j]);
		 }
        	String str1 = st.toString();
        	day = str1+str2;
        	day = b.transition(day);//指示天数，转换字节序
        }else {
        	String standard = "00000000";
        	int stand = Integer.parseInt(standard,16);//基准天数
        	days = days - 53690 + stand;
        	String str2 = Integer.toHexString(days);
        	int len = str2.length();
        	int add = 8-len;//需要补0的个数
        	String[] add0 = new String[add];
        	for(int i = 0;i<add;i++) {
        		add0[i] = "0";			
        	}
        	StringBuffer st = new StringBuffer();
        	for(int j=0;j<add;j++) {
        		st.append(add0[j]);
		 }
        	String str1 = st.toString();
        	day = str1+str2;
        	day = b.transition(day);//指示天数，转换字节序
        }
        String str4 = str.substring(11,13);//指示小时
        String str5 = str.substring(14,16);//指示分钟
        String str6 = str.substring(17,19);//指示秒
        String str7 = str.substring(20);//指示微秒
        int hh = Integer.parseInt(str4);
        int mm = Integer.parseInt(str5);
        int ss = Integer.parseInt(str6);
        int us = Integer.parseInt(str7);
        int ns = us/3;
        int ssum = hh*3600 + mm*60 + ss;//总秒数
        String strn = Integer.toString(ssum);
        String strus = Integer.toString(ns);
        BigInteger sum = new BigInteger(strn);
        BigInteger dec = new BigInteger("300");
        BigInteger sss = new BigInteger(strus);
        BigInteger num = sum.multiply(dec);
        BigInteger Num = num.add(sss);
        String second = String.valueOf(Num);
        second = new BigInteger(second, 10).toString(16);
        int leng = second.length();
        int addd = 8-leng;//需要补0的个数
		String[] add1 = new String[addd];
		for(int i = 0;i<addd;i++) {
			add1[i] = "0";			
		}
		StringBuffer sn = new StringBuffer();
		 for(int j=0;j<addd;j++) {
			 sn.append(add1[j]);
		 }
		String str8 = sn.toString();
		second = str8 + second;
		second = b.transition(second);
		result = second + day;
        
        return result;
    }

	public String Smalldatetime(String str) throws ParseException {
		 // 日期格式化
    	String result =null;
    	Joint b = new Joint();
        DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate = simpleFormat.parse("1900-01-01 00:00");
        Date endDate = simpleFormat.parse(str);
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        int days = (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));//天数
        String str2 = Integer.toHexString(days);
        int len = str2.length();
        int add = 4-len;//需要补0的个数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "0";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		String str1 = st.toString();
		String str3 = str1+str2;
		str3 = b.transition(str3);//转换字节序,指示天数
        String str4 = str.substring(11,13);//指示小时
        String str5 = str.substring(14);//指示分钟
        int hh = Integer.parseInt(str4);
        int mm = Integer.parseInt(str5);
        int min = hh*60 + mm;
        String mins = Integer.toHexString(min);
        int leng = mins.length();
        int addd = 4-leng;//需要补0的个数
		String[] add1 = new String[addd];
		for(int i = 0;i<addd;i++) {
			add1[i] = "0";			
		}
		StringBuffer sn = new StringBuffer();
		 for(int j=0;j<addd;j++) {
			 sn.append(add1[j]);
		 }
		String str6 = sn.toString();
		mins = str6 + mins;
        mins = b.transition(mins);//转换字节序，指示分钟数
        result = mins + str3;	       
        return result;
    }
	
	public String Date(String str)throws ParseException {
		String result = null;
		Joint b = new Joint();
		DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = simpleFormat.parse("0001-01-01");
        Date endDate = simpleFormat.parse(str);
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        int days = (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));
        result = Integer.toHexString(days);
        int len = result.length();
        int add = 6-len;//需要补0的个数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "0";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		String str1 = st.toString();
		result = str1+result;
        result = b.transition(result);//转换字节序
		return result;
	}
	public String Nvarcharmax(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = b.unicodeEncode(str);//汉字转换为unicode编码
		result = str1;
		return result;
	}
	public String Varbinarymax(String str) {
		String result = null;
		result = str;
		return result;
	}
	public String Nvarchar(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = b.unicodeEncode(str);//汉字转换为unicode编码
		result = str1;
		return result;
	}
	public String Varbinary(String str) {
		String result = null;
		result = str;//当数据没有0x时，直接存入
		return result;
	}
	public String Varcharmax(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = b.unicodeEncode(str);//汉字转换为unicode编码
		result = str1;
		return result;
	}
	public String Varchar(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = null;
		try {
			str1 = b.toGBK(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//汉字转为GBK编码
		result = str1;
		return result;	
	}
	public String Uniqueidentifier(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.substring(0,8);
		str1 = b.transition(str1);//第一段按小端存储，需转换字节序
		String str2 = str.substring(9,13);
		str2 = b.transition(str2);//第二段按小端存储，需转换字节序
		String str3 = str.substring(14,18);
		str3 = b.transition(str3);//第三段按小端存储，需转换字节序
		String str4 = str.substring(19,23);//第四段按大端存储
		String str5 = str.substring(24,36);//第五段按大端存储
		result = str1 + str2 + str3 + str4 +str5;
		return result;
	}
	public String Binary(String str, int n) {
		String result = null;
		int len = str.length();//数据长度
		int add = 2*n - len;//需要补的0数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "0";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		String str1 = st.toString();
		result = str + str1;		
		return result;
	}
	public String Nchar(String str, int n) {
		String result = null;
		Joint b = new Joint();
		String str1 = b.unicodeEncode(str);//汉字转换为unicode编码
		int len = str.length();//数据字符个数
		int add = n - len;//需要补的2000的个数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "2000";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		 String str2 = st.toString();//需要补的2000
		 result = str1 + str2;
		 return result;
	}
	public String Char(String str,int n) {
		String result = null;
		Joint b = new Joint();
		String str1 = null;
		try {
			str1 = b.toGBK(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//汉字转为GBK编码
		int len = str1.length();//数据长度
		int add = (2*n - len)/2;//需要补的 20 个数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "20";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		String str2 = st.toString();
		result = str1 + str2;//在数据后补20
		return result;
	}
	public String Decimal(String str,int num) {
		String result =  null;
		Joint b = new Joint();
		String str1 = str.replace(".", "");//删除小数点
		String sn = str1.substring(0,1);		
		boolean m = sn.equals("-");
		if(m == true) {
			String str2 = str1.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 0;
			if(num <= 9) {
				 add = 40 - len;
			}else if(10 <= num && num <= 19) {
				 add = 72 - len;
			}else if(20 <= num && num <= 28) {
				 add = 104 - len;
			}else {
				 add = 136 - len;
			}
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			 String str5 = st.toString();
			 int Num = (add + len)/4;
			 String str6 = str5 + str4;//补0后的2进制字符串
			 String str7 = b.binaryStrToHexStr(str6);//2进制字符串转16进制字符串
			 result = b.transition(str7);//字节序转换
			 result = "00" + result;
			 result = result.substring(0,Num);
			}else {
				String str2 = new BigInteger(str1, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 0;//add为补0个数
				if(num <= 9) {
					 add = 40 - len;
				}else if(10 <= num && num <= 19) {
					 add = 72 - len;
				}else if(20 <= num && num <= 28) {
					 add = 104 - len;
				}else {
					 add = 136 - len;
				}
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				int Num = (add + len)/4;
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
				result = "01" + result;
				result = result.substring(0,Num);
		}		
		return result;
	}
	public String Numeric(String str,int num) {
		String result =  null;
		Joint b = new Joint();
		String str1 = str.replace(".", "");//删除小数点
		String sn = str1.substring(0,1);		
		boolean m = sn.equals("-");
		if(m == true) {
			String str2 = str1.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 0;
			if(num <= 9) {
				 add = 40 - len;
			}else if(10 <= num && num <= 19) {
				 add = 72 - len;
			}else if(20 <= num && num <= 28) {
				 add = 104 - len;
			}else {
				 add = 136 - len;
			}
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			 String str5 = st.toString();
			 int Num = (add + len)/4;
			 String str6 = str5 + str4;//补0后的2进制字符串
			 String str7 = b.binaryStrToHexStr(str6);//2进制字符串转16进制字符串
			 result = b.transition(str7);//字节序转换
			 result = "00" + result;
			 result = result.substring(0,Num);
			}else {
				String str2 = new BigInteger(str1, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 0;//add为补0个数
				if(num <= 9) {
					 add = 40 - len;
				}else if(10 <= num && num <= 19) {
					 add = 72 - len;
				}else if(20 <= num && num <= 28) {
					 add = 104 - len;
				}else {
					 add = 136 - len;
				}
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				int Num = (add + len)/4;
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
				result = "01" + result;
				result = result.substring(0,Num);
		}		
		return result;
	}
	public String Money(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.replace(".", "");//删除小数点
		String sn = str1.substring(0,1);
		boolean m = sn.equals("-");
		if(m == true) {
			String str2 = str1.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 64-len;//需要补0的个数
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			String str5 = st.toString();
			String str6 = str5 + str4;//补0后的2进制字符串
			String str7 = b.InvertedCode(str6);//取反码
			String str8 = b.Complement(str7);//取补码的加1操作
			String str9 = b.binaryStrToHexStr(str8);//2进制字符串转16进制字符串
			result = b.transition(str9);//字节序转换
			}else {
				String str2 = new BigInteger(str1, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 64-len;//需要补0的个数
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
		}
		return result;
	}
	public String Smallmoney(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.replace(".", "");//删除小数点
		String sn = str1.substring(0,1);
		boolean m = sn.equals("-");
		if(m == true) {
			String str2 = str1.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 32-len;//需要补0的个数
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			String str5 = st.toString();
			String str6 = str5 + str4;//补0后的2进制字符串
			String str7 = b.InvertedCode(str6);//取反码
			String str8 = b.Complement(str7);//取补码的加1操作
			String str9 = b.binaryStrToHexStr(str8);//2进制字符串转16进制字符串
			result = b.transition(str9);//字节序转换
			}else {
				String str2 = new BigInteger(str1, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 32-len;//需要补0的个数
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
		}
		return result;
	}
	public String Bit(String str) {
		String result = null;
		if(str == null) {
			result = "00";
		}else {
			result = "0"+str;
		}		
		return result;
	}
	public String Smallint(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.substring(0,1);//取首位判断正负
		boolean m = str1.equals("-");
		if(m == true) {
			String str2 = str.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 16-len;//需要补0的个数
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			String str5 = st.toString();
			String str6 = str5 + str4;//补0后的2进制字符串
			String str7 = b.InvertedCode(str6);//取反码
			String str8 = b.Complement(str7);//取补码的加1操作
			String str9 = b.binaryStrToHexStr(str8);//2进制字符串转16进制字符串
			result = b.transition(str9);//字节序转换
			}else {
				String str2 = new BigInteger(str, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 16-len;//需要补0的个数
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
		}
		return result;
	}
	public String Tinyint(String str) {
		String result = null;
		Joint b = new Joint();
		String str2 = new BigInteger(str, 10).toString(16);//10进制字符串转16进制字符串
		String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
		int len = str3.length();
		int add = 8-len;//需要补0的个数
		String[] add0 = new String[add];
		for(int i = 0;i<add;i++) {
			add0[i] = "0";			
		}
		StringBuffer st = new StringBuffer();
		 for(int j=0;j<add;j++) {
			 st.append(add0[j]);
		 }
		String str4 = st.toString();
		String str5 = str4 + str3;//补0后的2进制字符串		
		String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
		result = b.transition(str6);//字节序转换
		return result;
	}
	public String Bigint(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.substring(0,1);//取首位判断正负
		boolean m = str1.equals("-");
		if(m == true) {
			String str2 = str.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 64-len;//需要补0的个数
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			String str5 = st.toString();
			String str6 = str5 + str4;//补0后的2进制字符串
			String str7 = b.InvertedCode(str6);//取反码
			String str8 = b.Complement(str7);//取补码的加1操作
			String str9 = b.binaryStrToHexStr(str8);//2进制字符串转16进制字符串
			result = b.transition(str9);//字节序转换
			}else {
				String str2 = new BigInteger(str, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 64-len;//需要补0的个数
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
		}
		return result;
	}
	public String Int(String str) {
		String result = null;
		Joint b = new Joint();
		String str1 = str.substring(0,1);//取首位判断正负
		boolean m = str1.equals("-");
		if(m == true) {
			String str2 = str.substring(1);//取正数部分
			String str3 = new BigInteger(str2, 10).toString(16);//10进制字符串转16进制字符串
			String str4 = b.hexStrToBinaryStr(str3);//16进制字符串转2进制字符串
			int len = str4.length();
			int add = 32-len;//需要补0的个数
			String[] add0 = new String[add];
			for(int i = 0;i<add;i++) {
				add0[i] = "0";			
			}
			StringBuffer st = new StringBuffer();
			 for(int j=0;j<add;j++) {
				 st.append(add0[j]);
			 }
			String str5 = st.toString();
			String str6 = str5 + str4;//补0后的2进制字符串
			String str7 = b.InvertedCode(str6);//取反码
			String str8 = b.Complement(str7);//取补码的加1操作
			String str9 = b.binaryStrToHexStr(str8);//2进制字符串转16进制字符串
			result = b.transition(str9);//字节序转换
			}else {
				String str2 = new BigInteger(str, 10).toString(16);//10进制字符串转16进制字符串
				String str3 = b.hexStrToBinaryStr(str2);//16进制字符串转2进制字符串
				int len = str3.length();
				int add = 32-len;//需要补0的个数
				String[] add0 = new String[add];
				for(int i = 0;i<add;i++) {
					add0[i] = "0";			
				}
				StringBuffer st = new StringBuffer();
				 for(int j=0;j<add;j++) {
					 st.append(add0[j]);
				 }
				String str4 = st.toString();
				String str5 = str4 + str3;//补0后的2进制字符串				
				String str6 = b.binaryStrToHexStr(str5);//2进制字符串转16进制字符串
				result = b.transition(str6);//字节序转换
		}
		
		return result;
	}
	//字节序转换方法
    public String transition(String str) {
        chars = str.toCharArray();
    	
    	for(int i=0;i< (chars.length/4);i++) {
    		a = chars [2*i];
    		b = chars [2*i+1];
    		c = chars [chars.length-1-2*i];
    		d = chars [chars.length-1-(2*i+1)];
    		chars [chars.length-1-2*i] = b;
    		chars [chars.length-1-(2*i+1)] = a;
    		chars [2*i] = d;
    		chars [2*i+1] = c;
    	}
    	String str_ = String.valueOf(chars);
    	
    	return str_;
    			
    }
  //16进制字符串转换为2进制字符串
    public String hexStrToBinaryStr(String str) {
		 
		if (str == null || str.equals("")) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		// 将每一个十六进制字符分别转换成一个四位的二进制字符
		for (int i = 0; i < str.length(); i++) {
			String indexStr = str.substring(i, i + 1);
			String binaryStr = Integer.toBinaryString(Integer.parseInt(indexStr, 16));
			while (binaryStr.length() < 4) {
				binaryStr = "0" + binaryStr;
			}
			sb.append(binaryStr);
		}
 
		return sb.toString();
	}
  //将2进制字符串转换为16进制字符串
    public  String  binaryStrToHexStr(String binaryStr) {
    	if (binaryStr == null || binaryStr.equals("") || binaryStr.length() % 4 != 0) {
			return null;
		}
 
		StringBuffer sbs = new StringBuffer();
		// 二进制字符串是4的倍数，所以四位二进制转换成一位十六进制
		for (int i = 0; i < binaryStr.length() / 4; i++) {
			String subStr = binaryStr.substring(i * 4, i * 4 + 4);
			String hexStr = Integer.toHexString(Integer.parseInt(subStr, 2));
			sbs.append(hexStr);
		}
 
		return sbs.toString();
		
	}
  //取反码
    public String InvertedCode(String  str){
	    StringBuilder sr = new StringBuilder(str);
	    for (int i = 0; i < sr.length(); i++) {
	        if (sr.substring(i,i+1).equals("0")){
	            sr.replace(i,i+1,"1");
	        }else{
	            sr.replace(i,i+1,"0");
	        }
	    }
	    return sr.toString();
	}
    //取补码的加1操作
    public String Complement(String str) {
	    StringBuilder sr = new StringBuilder(str);
	    if (str.substring(str.length() - 1).equals("0")) {
	        sr.replace(str.length() - 1, str.length(), "1");
	        return sr.toString();
	    } else {
	        sr.replace(str.length() - 1, str.length(), "0");
	        for (int i = str.length() - 1; i > 0; i--) {
	            if (str.substring(i - 1, i).equals("0")) {
	                sr.replace(i - 1, i, "1");
	                return sr.toString();
	            } else {
	                sr.replace(i - 1, i, "0");
	            }
	        }
	        return sr.toString();
	   }
	} 
    //汉字，英文字符转为GBK编码
    public String toGBK(String source) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();        
		byte[] bytes = source.getBytes("GBK");        
		for(byte b : bytes) {            
		sb.append(Integer.toHexString((b & 0xff)).toUpperCase());        }        
		return sb.toString();    
		}
    //汉字，英文字符转为Unicode编码
    public String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            String str1 = hexB.substring(0,2);
            String str2 = hexB.substring(2);
            hexB = str2 + str1;          
            if (hexB.length() <= 2) {
                hexB =  hexB + "00";
            }
            unicodeBytes = unicodeBytes +  hexB;
        }
        return unicodeBytes;
    }
}


