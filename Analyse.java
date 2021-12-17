package console;

import java.math.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Analyse {
    public String row_log;
	public String rowlog1;
	public char[] chars;
	public char a;
	public char b;
	public char c;
	public char d;   
	
	//定长数据类型方法
    public String[] anaFixedType(String[] fixedType,int fixedNum,int[] fixedLength,String rowlog,int[] isFixedNull) {
    	    
    	
		    String row_log = rowlog.substring(8);//切割前4个字节即前8个16进制字符后剩下的rowlog
    	    
    	    String[] logReturn = new String[2];//返回值数组
    	    
    	    logReturn[1]= row_log;
    	        	    
    	    Analyse a=new Analyse();    	        	    
    	    for(int i = 0;i<fixedNum;i++) {
    	    	switch(fixedType [i] ) {
    	    	case "int":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Int(logReturn[1],fixedLength[i]);//获得截取数据后的rowlog
    	    		}else {
    	    			logReturn =a.Int(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}   	    	    
    	    	    break;	    		
    	    	case "char":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Char(logReturn[1],fixedLength[i]);//获得截取数据后的rowlog
    	    		}else {
    	    			logReturn =a.Char(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
    	    			                                               //用logReturan[]接收解析方法的返回数组
        	    	    fixedType [i]=logReturn [0].replaceAll("\\s+$",""); //[0]是解析出的数据，传入fixedType数组
        	    	    //返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log 
    	    		}    											      				    	
    	    	    break;
    	    	case "bigint":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.BigInt(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.BigInt(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}         	    	     
    	    	    break;
    	    	case "smallint":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.SmallInt(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.SmallInt(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}    	    		
    	    		break;
    	    	case "tinyint":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.TinyInt(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.TinyInt(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}    	    		
    	    		break;
    	    	case "bit":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Bit(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.Bit(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}
    	    		break;
    	    	case "smallmoney":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.SmallMoney(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.SmallMoney(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}
    	    	    break;
    	    	case "money":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Money(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.Money(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}    	       	    		
    	    		break;
    	    	case "numeric":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Numeric(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.Numeric(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}    	       	    		
    	    		break;
    	    	case "decimal":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Decimal(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.Decimal(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}    	       	    		
    	    		break;
    	    	case "nchar":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.NChar(logReturn[1],fixedLength[i]);
    	    		}else {
    	    			logReturn =a.NChar(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                    											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0].replaceAll("\\s+$","");//[0]是解析出的数据，传入fixedType数组
	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}    	    	    	
    	    		break;
    	    	case "uniqueidentifier":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.UniqueIdentifier(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.UniqueIdentifier(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}    	    	    	
        	    		break;
    	    	case "date":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Date(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.Date(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}    
    	    		break;
    	    	case "smalldatetime":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.SmallDatetime(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.SmallDatetime(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}    
    	    		break;
    	    	case "datetime":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.DateTime(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.DateTime(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}    
    	    		break;
    	    	case "datetime2":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.DateTime2(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.DateTime2(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}    
    	    		break;
    	    	case "time":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Time(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.Time(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}    
    	    		break;
    	    	case "timestamp":
	    	    		logReturn =a.TimeStamp(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
						  //用logReturan[]接收解析方法的返回数组
	                    fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
	    	    		break;
    	    	case "binary":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Binary(logReturn[1],fixedLength[i]);//获得截取数据后的rowlog
    	    		}else {
    	    			logReturn =a.Binary(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
    	    		}   	    	    
    	    	    break;	
    	    	case "float":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Float(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.Float(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}   
    	    		break;
    	    	case "real":
    	    		if(isFixedNull[i] == 1) {
    	    			fixedType [i]=null;//当位图值为1时，该数据为null
    	    			logReturn =a.Real(logReturn[1],fixedLength[i]);
        	    		}else {
    	    			logReturn =a.Real(logReturn[1],fixedLength[i]);//调用解析该数据类型的方法,传参：一是切割后的row_log,二是该数据类型所占字节数
                        											  //用logReturan[]接收解析方法的返回数组
    	    			fixedType [i]=logReturn [0];//[0]是解析出的数据，传入fixedType数组
    	    			//返回值数组：有两个值，一是数据本身(需要把数据转换为字符串)，二是切割后的row_log
        	    		}   
    	    		break;

	    	    	
    	       }
    	    }
    	    String row_log_ = logReturn[1];
     	    fixedType [fixedNum] = row_log_ ;//在解析出的数据之后加上剩下的row_log contents 0 字段
    	    return 	fixedType;//返回数组  
       }

	//变长数据类型方法
    public String[] anaVariableType(String[] variableType, int variableNum,int totalNum,String rowlog,String row_log,int[] isVariableNull,String[] logText) {    	    	
    	String [] logReturn= new String[2];//定义返回值数组
    	double m = 8;
    	double num =  Math.ceil((double)totalNum/m); // 计算位图所占字节数
    	int Num =(int) num;
    	String str = rowlog.replaceAll(row_log, "");//统计rowlog中的定长数据部分(总列数字段前)
    	int len = str.length();
    	
    	String bin=intToString(isVariableNull);
	    bin=bin.substring(0,variableNum);
	    bin=bin.replaceAll("1*$","");
	    int x=bin.length();
    	int First=0;
    	if(x!=0) {
    		try {
				row_log = row_log.substring(2*Num+8);//切掉位图字段,变长数据列数量字段以及总的列数量字段,得到以偏移阵列字段开头的row_log
				First = len + 8+2*Num+4*x;//第一个变长数据的起始位置
			} catch (Exception e) {//变长列数量不存在但非全空，说明全部空白的插入和null
				for(int a=0;a<variableNum;a++) {
					if(isVariableNull[a] == 1) {
						variableType[a] = null;
					}else {
						variableType[a] = "";
					}
				}
				return variableType;
			}
    	}else {
    		row_log = row_log.substring(2*Num+4);//切掉位图字段,变长数据列数量字段以及总的列数量字段,得到以偏移阵列字段开头的row_log
    		First = len + 4+2*Num+4*x;//第一个变长数据的起始位置
    	}//x=0说明变长部分全部为空，此时变长列数量部分不存在
  
    	String first = First+"";   //转化为字符串
    	logReturn[1] = first; 
    	
    	Analyse b=new Analyse();
    	int j=0,i=0;
    	for(;i<x;i++) {
    		switch(variableType[i]) {
    		case "varchar":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+""; 
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    				
    			}else{    				
    				logReturn=b.Varchar(rowlog,row_log,logReturn[1],i,x);//调用解析该变长数据类型的方法
    				variableType[i] = logReturn[0];
                    //传参：完整的rowlog,以偏移阵列为开头的row_log,数据的起始位置,第几个变长列        			
    			}    			
    			break;
    		case "varcharmax":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    				
    			}else{
    				logReturn=b.VarcharMax(rowlog,row_log,logReturn[1],i, x);//调用解析该变长数据类型的方法
    				variableType[i] = logReturn[0];
                    //传参：完整的rowlog,以偏移阵列为开头的row_log,数据的起始位置,第几个变长列        			
    			}    			
    			break;    
    		case "text":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    			}else {    				    				
    				logReturn=b.Text(rowlog, row_log, logReturn[1],logText[j]);
    				variableType[i] = logReturn[0];
    				j++;
    			}
    			break;
    		case "ntext":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    			}else {
    				logReturn=b.NText(rowlog, row_log, logReturn[1],logText[j]);
    				variableType[i] = logReturn[0];
    				j++;
    			}    			
    			break;
    		case "image":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {};
    			}else {    				    				
    				logReturn=b.Image(rowlog, row_log, logReturn[1],logText[j]);
    				variableType[i] = logReturn[0];
    				j++;
    			}
    			break;
    		case "varbinary":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    			}else{
    				logReturn=b.Varbinary(rowlog,row_log,logReturn[1],i, x);//调用解析该变长数据类型的方法
    				variableType[i] = logReturn[0];
                    //传参：完整的rowlog,以偏移阵列为开头的row_log,数据的起始位置,第几个变长列        			
    			}    			
    			break;
    		case "varbinarymax":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    			}else{
    				logReturn=b.VarbinaryMax(rowlog,row_log,logReturn[1],i, x);//调用解析该变长数据类型的方法
    				variableType[i] = logReturn[0];
                    //传参：完整的rowlog,以偏移阵列为开头的row_log,数据的起始位置,第几个变长列        			
    			}    			
    			break;
    		case "nvarchar":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    			}else{
    				logReturn=b.NVarchar(rowlog,row_log,logReturn[1],i, x);//调用解析该变长数据类型的方法
    				variableType[i] = logReturn[0];
                    //传参：完整的rowlog,以偏移阵列为开头的row_log,数据的起始位置,第几个变长列
    		         }
    			break;
    		case "nvarcharmax":
    			if(isVariableNull[i] == 1) {
    				variableType[i] = null;
    				try {
						String str1 = rowlog.substring(len+8+2*Num+4*i,len+8+2*Num+4*(i+1));//为了传递下一个变长数据的初始值
						int n  = Integer.parseInt(transition(str1),16);
						String str2 = 2*n+"";  
						logReturn[1] = str2;//更新第i+1个数据的起始位置
					} catch (Exception e) {}
    			}else{
    				logReturn=b.NVarcharMax(rowlog,row_log,logReturn[1],i, x);//调用解析该变长数据类型的方法
    				variableType[i] = logReturn[0];
                    //传参：完整的rowlog,以偏移阵列为开头的row_log,数据的起始位置,第几个变长列
    		         }
    			break;
    	
    			}
    	}    
    	for(;i<variableNum;i++) {
    		variableType[i] = null;
    	}
    	return variableType;
    }
    //转换定长数据长度中null值，变为具体数值
    public int[] fixedNulltoLength(String[] fixedType,int[] fixedLength,int fixedNum) {
    	//处理fixedNum数组中长度是null的数据类型
	    for(int j = 0;j<fixedNum;j++) {
	    	switch(fixedType[j]) {
	    	case "int":
	    		fixedLength[j] = 4;
	    		break;
	    	case "tinyint":
	    		fixedLength[j] = 1;
	    		break;
	    	case "smallint":
	    		fixedLength[j] = 2;
	    		break;
	    	case "bigint":
	    		fixedLength[j] = 8;
	    		break;
	    	case "date":
	    		fixedLength[j] = 3;
	    		break;
	    	case "smalldatetime":
	    		fixedLength[j] = 4;
	    		break;
	    	case "timestamp":
	    		fixedLength[j] = 8;
	    		break;
	    	case "datetime":
	    		fixedLength[j] = 8;
	    		break;
	    	case "datetime2":
	    		fixedLength[j] = 8;
	    		break;
	    	case "time":
	    		fixedLength[j] = 5;
	    		break;
	    	case "uniqueidentifier":
	    		fixedLength[j] = 16;
	    		break;
	    	case "datetimeoffset":
	    		fixedLength[j] = 5;
	    		break;
	    	case "float":
	    		fixedLength[j] = 8;
	    		break;
	    	case "real":
	    		fixedLength[j] = 4;
	    		break;
	    	case "money":
	    		fixedLength[j] = 8;
	    		break;
	    	case "smallmoney":
	    		fixedLength[j] = 4;
	    		break;
	    	case "bit":
	    		fixedLength[j] = 1;
	    		break;
	    	case "nchar":
	    		fixedLength[j] = 2*fixedLength[j];
	    		break;
	    	}	    	
	    }
	    return fixedLength;
    }
    //real型解析方法
    public String[] Real(String str,int n) {
    	String[] array = new String[2];
    	array[1] = str.substring(2*n);//存放切割剩余的rowlog
    	String str1 = str.substring(0,2*n);
    	boolean m = str1.equals("A379EB4C");
    	if(m == true) {
    		String str2 = "123456789.123456789";
    		array[0] = str2;
    	}else {
    		String str2 = "";
    		array[0] = str2;
    	}
    	return array;
    }
    //float型解析方法
    public String[] Float(String str,int n) {
    	String[] array = new String[2];
    	array[1] = str.substring(2*n);//存放切割剩余的rowlog
    	String str1 = str.substring(0,2*n);
    	boolean m = str1.equals("8716D9CEF7530540");
    	if(m == true) {
    		String str2 = "2.666";    
    		array[0] = str2;
    	}else {
    		String str2 = "";
    		array[0] = str2;
    	}    	
    	return array;
    }
    //date类型数据解析方法：
    public String[] Date(String str,int n) {
    	String[] array = new String[2];
    	array[1] = str.substring(2*n);//存放切割剩余的rowlog
    	String str1 = str.substring(0,2*n);
    	Analyse c = new Analyse();
    	str1 = c.transition(str1);
    	int m = Integer.parseInt(str1,16);//计算累积总天数
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//设定日期字符串格式
    	Calendar ca = Calendar.getInstance();
        ca.set(0001, 0, 01);//设定基准时间
        ca.add(Calendar.DATE, m);// m为增加的天数
    	Date date = ca.getTime();
    	String str2 = format.format(date);
    	array[0] = str2;
    	return array;
    }
    //smalldatetime类型数据解析方法：
    public String[] SmallDatetime(String str,int n) {
    	String[] array = new String[2];
    	Analyse c = new Analyse();
    	array[1] = str.substring(2*n);//存放切割剩余的rowlog
    	str = str.substring(0,2*n);
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设定日期字符串格式
    	String str1 = str.substring(0,n);
    	str1 = c.transition(str1);
    	int m = Integer.parseInt(str1,16);
    	int hh = m/60;
    	int mm = m%60;
    	String str2 = str.substring(n);
    	str2 = c.transition(str2);
    	int day = Integer.parseInt(str2,16);
    	Calendar ca = Calendar.getInstance();
        ca.set(1900,0,01,00,00);//设定基准时间
        ca.add(Calendar.DATE, day);
        ca.add(Calendar.HOUR,hh);
        ca.add(Calendar.MINUTE, mm);
        Date date = ca.getTime();
        String str3 = format.format(date);
    	array[0] = str3;
    	return array;
    }
    //datetime类型数据解析方法
    public String[] DateTime(String str,int n) {
    	String [] array = new String[2];
    	Analyse c = new Analyse();
    	array[1] = str.substring(2*n);//切割剩下的rowlog
    	str = str.substring(0,2*n);
    	String str1 = str.substring(0,n);//存放秒数的字段，2字节
    	String str2 = str.substring(n);//存放天数的字段，2字节
    	str1 = c.transition(str1);//转换字节序
    	str2 = c.transition(str2);
    	//计算天数
    	String str3 = "ffff2e46";//起始大小
    	String str4 = "ffffffff";
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设定日期字符串格式
    	String st = str2.substring(0,1);
    	boolean m = st.equals("f");
    	int day = 0;
    	if(m == true) {
    		BigInteger inc = new BigInteger(str2,16);
    		BigInteger dec = new BigInteger(str3,16);
    		BigInteger num = inc.subtract(dec);
    		String str5 = num.toString();
    		day = Integer.parseInt(str5);
    	}else {
    		BigInteger inc = new BigInteger(str4,16);
    		BigInteger dec = new BigInteger(str3,16);
    		BigInteger num = inc.subtract(dec);
    		BigInteger Num = new BigInteger(str2,16);
    		BigInteger Num_ = Num.add(num);	    		
    		day = Num_.intValue()+1;//BigInteger转换为int
    	}
    	
    	Calendar ca = Calendar.getInstance();       
        int p = Integer.parseInt(str1,16);       
        int rest = p%300;//0.003的个数
        int hh = (p/300)/3600;//小时数
        int mm = ((p/300)-hh*3600)/60;//分钟数
        int ss = p/300-hh*3600-mm*60;//秒数
        ca.set(1753,0,01,hh,mm,ss);//设定基准时间
        ca.add(Calendar.DATE,day);// day为增加的天数
        double S = .003*rest;
        String SS = String.format("%.3f",S);//表示微秒的字符串
        SS = SS.substring(1);
        Date date = ca.getTime();
        String str6 = format.format(date);
        str6 = str6+SS;
    	array[0] = str6;
    	return array;
    }
    //datetime2类型数据解析方法：
    public String[] DateTime2(String str,int n) {
    	String[] array = new String[2];
    	array[1] = str.substring(2*n);
    	Analyse c =  new Analyse();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设定日期字符串格式
    	str = str.substring(0,2*n);
    	String str1 = str.substring(0,10);//存放秒数的字段，5字节
    	String str2 = str.substring(10);//存放天数的字段，3字节
    	str1 = c.transition(str1);
    	str2 = c.transition(str2);//字节序转换
    	String str3 = "09C3A1";//天数起始大小
    	//计算天数
    	int Num = Integer.parseInt(str2,16);
    	int num = Integer.parseInt(str3,16);
    	int day = Num-num;
    	//计算秒数
        BigInteger inc = new BigInteger(str1,16);
        BigInteger dec = new BigInteger("10000000");
        BigInteger res = inc.divide(dec);//取整
        BigInteger sss = inc.mod(dec);//取余
        int sec = res.intValue();//秒数
        int SSS = sss.intValue();//100纳秒的个数
        int SSSS = SSS/10; 
        double SSS_ = 0.000001*SSSS;
        String SSS__ = String.format("%.6f",SSS_);//表示纳秒的字符串
        SSS__ = SSS__.substring(1);//去掉整数部分的0
        int hh = sec/3600;
        int mm = (sec%3600)/60;
        int ss = sec-3600*hh-60*mm;       
        Calendar ca = Calendar.getInstance(); 
    	ca.set(1753,0,01,hh,mm,ss);//设定基准时间
        ca.add(Calendar.DATE,day);
        Date date = ca.getTime();
        String str4 = format.format(date);
        str4 = str4+SSS__;
        array[0] = str4;
        
        
        return array;
    }
    //time类型数据解析方法:
    public String[] Time(String str,int n) {
    	String[] array = new String[2];
    	array[1] = str.substring(2*n);
    	Analyse c = new Analyse();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设定日期字符串格式
    	str = str.substring(0,2*n);
    	String str1 = c.transition(str);//字节序转换
    	BigInteger inc = new BigInteger(str1,16);
        BigInteger dec = new BigInteger("10000000");
        BigInteger res = inc.divide(dec);//取整
        int sec = res.intValue();//秒数
        int hh = sec/3600;
        int mm = (sec%3600)/60;
        int ss = sec-3600*hh-60*mm;  
        Calendar ca = Calendar.getInstance(); 
    	ca.set(1753,0,01,hh,mm,ss);//设定基准时间
    	Date date = ca.getTime();
        String str2 = format.format(date);
        str2 = str2.substring(11);//只需要时分秒
        array[0] = str2;
    	
        return array;
    }
    
    //datetimeoffset类型数据解析方法：
    public String[] DatetimeOffset(String str,int n) {
    	String[] array = new String[2];
    	
    	
    	
    	
    	
    	return array;
    }
    //uniqueidentifier类型数据解析方法
    public String[] UniqueIdentifier(String str,int n) {
    	String [] array = new String[2];
    	String [] chars = new String[5];
    	array[1] = str.substring(2*n);
    	Analyse c = new Analyse();
    	StringBuffer sbu = new StringBuffer();
    	str = str.substring(0,2*n);
    	String str1 = str.substring(0,8);
    	str1 = c.transition(str1);
    	chars[0] = str1;
    	
    	String str2 = str.substring(8,12);
    	str2 = c.transition(str2);
    	str2 = "-"+str2;
    	chars[1] = str2;
    	
    	String str3 = str.substring(12,16);
    	str3 = c.transition(str3);
    	str3 = "-"+str3;
    	chars[2] = str3;
    	
    	String str4 = str.substring(16,20);
    	str4 = "-"+str4;
    	chars[3] = str4;
    	
    	String str5 = str.substring(20);
    	str5 = "-"+str5;
    	chars[4] = str5;
    	
    	for(int i=0;i<5;i++) {
    		sbu.append(chars[i]);
    	}
    	String st = sbu.toString();
    	array[0] = st;
    	return array;
    }
   
    //timestamp类型解析方法：
    public String[] TimeStamp(String str,int n) {
    	String[] array = new String[2];
    	array[1] = str.substring(2*n);
    	array[0] = "DEFAULT";    	
    	return array;
    }
    //numeric(p,s)型数据解析方法：
    public String[] Numeric(String str,int n ) {
    	String [] array = new String[2];
    	Analyse c = new Analyse();
    	String str1 = str.substring(0,2);//取第一个字节，指示正负
    	String str2 = str.substring(2,2*n);//取数据
    	str2 = c.transition(str2);//字节序转换
    	int m = Integer.parseInt(str1,16);
    	if(m == 1) {
    		BigInteger dec = new BigInteger(str2,16);
    		String str3 = dec.toString();
    		array[0] = str3;
    	}else {
    		BigInteger dec = new BigInteger(str2,16);
    		String str3 = dec.toString();
    		str3 = "-"+str3;
    		array[0] = str3;
    	}
    	array[1] = str.substring(2*n);
    	return array;
    }
    public String[] Decimal(String str,int n ) {
    	String [] array = new String[2];
    	Analyse c = new Analyse();
    	String str1 = str.substring(0,2);//取第一个字节，指示正负
    	String str2 = str.substring(2,2*n);//取数据
    	str2 = c.transition(str2);//字节序转换
    	int m = Integer.parseInt(str1,16);
    	if(m == 1) {
    		BigInteger dec = new BigInteger(str2,16);
    		String str3 = dec.toString();
    		array[0] = str3;
    	}else {
    		BigInteger dec = new BigInteger(str2,16);
    		String str3 = dec.toString();
    		str3 = "-"+str3;
    		array[0] = str3;
    	}
    	array[1] = str.substring(2*n);
    	return array;
    }
    //smallmoney型数据解析方法
    public String[] SmallMoney(String str,int n) {
    	String [] array = new String[2];
    	String str1 = str.substring(0, 2*n);
    	array [1] = str.substring(2*n);//存储切割后的rowlog字段    	
    	Analyse c = new Analyse();//实例化对象c
    	str1 = c.transition(str1);//转换16进制字符串字节序
    	String str2 = c.hexStrToBinaryStr(str1);//将16进制字符串转换为2进制字符串
    	String str3 = str2.substring(0,1);//取首位
    	
    	//解析正常数据，除了null和''
    	boolean m =str3.equals("1");//判断正负
    	if(m == true){ //判断首位是否为1，为1则为负数，需要按位变反再加1
    		str2 = c.InvertedCode(str2);//取反码
    		str2 = c.Complement(str2);//加1
    		String str4 = c.binaryStrToHexStr(str2);//2进制字符串转换为16进制字符串
    		BigInteger dec = new BigInteger(str4,16);
    		String str5 = dec.toString();
    		str5 = "-"+str5;
    		array [0] = str5;
    	}else {//首位不为1，为正数
    		//将16进制字符串转换为10进制字符串
    		BigInteger dec = new BigInteger(str1,16);
        	String str6 = dec.toString();
    		array [0] = str6;
    	   }        
    	return array;
    }
    //money型数据解析方法
    public String[] Money(String str,int n) {
    	String [] array = new String[2];
    	String str1 = str.substring(0, 2*n);
    	array [1] = str.substring(2*n);//存储切割后的rowlog字段    	
    	Analyse c = new Analyse();//实例化对象c
    	str1 = c.transition(str1);//转换16进制字符串字节序
    	String str2 = c.hexStrToBinaryStr(str1);//将16进制字符串转换为2进制字符串
    	String str3 = str2.substring(0,1);//取首位
    	
    	//解析正常数据，除了null和''
    	boolean m =str3.equals("1");//判断正负
    	if(m == true){ //判断首位是否为1，为1则为负数，需要按位变反再加1
    		str2 = c.InvertedCode(str2);//取反码
    		str2 = c.Complement(str2);//加1
    		String str4 = c.binaryStrToHexStr(str2);//2进制字符串转换为16进制字符串
    		BigInteger dec = new BigInteger(str4,16);
    		String str5 = dec.toString();
    		str5 = "-"+str5;
    		array [0] = str5;
    	}else {//首位不为1，为正数
    		//将16进制字符串转换为10进制字符串
    		BigInteger dec = new BigInteger(str1,16);
        	String str6 = dec.toString();
    		array [0] = str6;
    	   }        
    	return array;
    }
    //bit型数据解析方法
    public String[] Bit(String str, int n) {
  		String [] array = new String[2];
  		array[1] = str.substring(2);
  		
  		str = str.substring(0,2);
  		int m = Integer.parseInt(str,16);
  		if(m > 1||m == 1) {
  				String str2 = "1";
  				array [0] = str2;
  		}
  		else{
  				String str3 = "0";
  				array [0] = str3;
  		}
  		return array;
  		}
    //binary型数据解析方法
    public String[] Binary(String str,int n) {
    	String [] array = new String[2];
    	array[1] = str.substring(2*n);
    	String str1 = str.substring(0,2*n);
    	str1=(str1.replaceAll("^(0+)", "")).replaceAll("0+$", "");   			
    	if(str1.length()!=0) {
    		array[0] = "0x"+str1;//image型数据
    	}else {
    		array[0] = "";//插入为空白字符串
    	}   	    
    	return array;
    }
    //int型数据解析方法
    public String[] Int(String str,int n) {
    	String[] array= new String[2];
    	String str1 = str.substring(0, 8);
    	array [1] = str.substring(8);//存储切割后的rowlog字段    	
    	Analyse c = new Analyse();//实例化对象c
    	str1 = c.transition(str1);//转换16进制字符串字节序
    	String str2 = c.hexStrToBinaryStr(str1);//将16进制字符串转换为2进制字符串
    	String str3 = str2.substring(0,1);//取首位    	    	
    	//解析正常数据
    		boolean m =str3.equals("1");//判断正负
    		if(m == true){ //判断首位是否为1，为1则为负数，需要按位变反再加1
        		str2 = c.InvertedCode(str2);//取反码
        		str2 = c.Complement(str2);//加1
        		String str4 = c.binaryStrToHexStr(str2);//2进制字符串转换为16进制字符串
        		BigInteger dec = new BigInteger(str4,16);
        		String str5 = dec.toString();
        		str5 = "-"+str5;
        		array [0] = str5;
        	}else {//首位不为1，为正数
        		//将16进制字符串转换为10进制字符串
        		BigInteger dec = new BigInteger(str1,16);
            	String str6 = dec.toString();
        		array [0] = str6;
        	           	
    	}    	  	
    	return array;
    }
    
    //bigint型数据解析方法
    public String[] BigInt(String str,int n) {
    	String[] array= new String[2];
    	String str1 = str.substring(0, 2*n);
    	array [1] = str.substring(2*n);//存储切割后的rowlog字段    	
    	Analyse c = new Analyse();//实例化对象c
    	str1 = c.transition(str1);//转换16进制字符串字节序
    	String str2 = c.hexStrToBinaryStr(str1);//将16进制字符串转换为2进制字符串
    	String str3 = str2.substring(0,1);
    	
    	//解析正常数据，除了null和''    	    	
    	boolean m =str3.equals("1");//判断正负
    		if(m == true){ //判断首位是否为1，为1则为负数，需要按位变反再加1
        		str2 = c.InvertedCode(str2);//取反码
        		str2 = c.Complement(str2);//加1
        		String str4 = c.binaryStrToHexStr(str2);//2进制字符串转换为16进制字符串
        		BigInteger dec = new BigInteger(str4,16);
        		String str5 = dec.toString();
        		str5 = "-"+str5;
        		array [0] = str5;
        	}else {//首位不为1，为正数
        		//将16进制字符串转换为10进制字符串
        		BigInteger dec = new BigInteger(str1,16);
            	String str6 = dec.toString();
        		array [0] = str6;
        	   }        	                	
    	return array;
	}
  	
    //smallint数据解析方法
    public String[] SmallInt(String str,int n) {
    	String[] array= new String[2];
    	String str1 = str.substring(0, 2*n);
    	array [1] = str.substring(2*n);//存储切割后的rowlog字段    	
    	Analyse c = new Analyse();//实例化对象c
    	str1 = c.transition(str1);//转换16进制字符串字节序
    	String str2 = c.hexStrToBinaryStr(str1);//将16进制字符串转换为2进制字符串
    	String str3 = str2.substring(0,1);     	
    	//解析正常数据，除了null和''    	
    		boolean m =str3.equals("1");//判断正负
        	if(m == true){ //判断首位是否为1，为1则为负数，需要按位变反再加1
        		str2 = c.InvertedCode(str2);//取反码
        		str2 = c.Complement(str2);//加1
        		String str4 = c.binaryStrToHexStr(str2);//2进制字符串转换为16进制字符串
        		BigInteger dec = new BigInteger(str4,16);//16进制字符串转换为10进制字符串
        		String str5 = dec.toString();
        		str5 = "-"+str5;
        		array [0] = str5;
        	}else {//首位不为1，为正数
        		//将16进制字符串转换为10进制字符串
        		BigInteger dec = new BigInteger(str1,16);
            	String str6 = dec.toString();
        		array [0] = str6;
        	   }        		    	    	
    	return array;
    }
    
    //tinyint类型解析方法
    public String[] TinyInt(String str,int n) {
    	String[] array= new String[2];
    	String str1 = str.substring(0, 2*n);
    	array [1] = str.substring(2*n);//存储切割后的rowlog字段    	
    	Analyse c = new Analyse();//实例化对象c
    	str1 = c.transition(str1);//转换16进制字符串字节序    	    	   	
    	//解析正常数据(除了null和 '')    	
    	BigInteger dec = new BigInteger(str1,16);
    	String str2 = dec.toString();//16进制字符串转换为10进制字符串
    	array [0] = str2;    	           	    	
    	
    	return array;
    }   
    
    //char类型解析方法
  	public String[] Char(String str,int n) {
      	String[] array = new String[2];
      	array[1] = str.substring(2*n);
      	str = str.substring(0,2*n);
      	Analyse c = new Analyse();
      	String str1 = "";
		try {
			str1 = c.StringToGBK(str);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
      	array[0] = str1;
      	return array;
      }
    
  	//nchar类型解析方法
  	public String[] NChar(String str,int n ) {
  		String[] array = new String[2];
  		array[1] = str.substring(2*n);
  		String str1 = str.substring(0,2*n);//截取nchar的数据  		
    	StringBuffer sbu = new StringBuffer();
    	Analyse c = new Analyse();
    	int len = str1.length();
    	int i = 0;
    	String [] chars = new String[len/4];
    	while(i< (len/4)) {
    		String str2 = str1.substring(4*i,4*(i+1));
    		String str3 = c.transition(str2);//字节序转换
    		str3 = c.UstartToCn(str3);//得到解析出的字符
    		chars[i] = str3;
    		i++;
    	}
    	for(int j = 0;j<(len/4);j++) {
    		sbu.append(chars[j]);
    	}
    	String str4 = sbu.toString();
        array[0] = str4;  		
  		
        return array;
  	}
  	//nvarchar类型解析方法
  	public String[] NVarchar(String rowlog,String row_log,String first,int i,int x) {
    	String[] array = new String[2];
    	try {
			int m = Integer.parseInt(first);//数据起始位置
			StringBuffer sbu = new StringBuffer();
			String str = row_log.substring(4 * i, 4 * (i + 1));//取偏移,第i个变长数据的偏移
			Analyse c = new Analyse();
			str = c.transition(str);//字节序转换
			int n = Integer.parseInt(str, 16);//数据终止位置(字节数)
			first = (2 * n) + "";
			array[1] = first;//第i+1个数据的起始位置    
			if (m > 2 * n) {//有空白插入存在导致列偏移数量偏差，需回倒
				m = m - 4 * (x - i - 1);
			}
			String str1 = rowlog.substring(m, 2 * n);//取出数据
			//解析
			int len = str1.length();
			if (len != 0) {
				int p = 0;
				String[] chars = new String[len / 4];
				while (p < (len / 4)) {
					String str2 = str1.substring(4 * p, 4 * (p + 1));
					String str3 = c.transition(str2);//字节序转换
					str3 = c.UstartToCn(str3);//得到解析出的字符
					chars[p] = str3;
					p++;
				}
				for (int j = 0; j < (len / 4); j++) {
					sbu.append(chars[j]);
				}
				String str4 = sbu.toString();
				array[0] = str4;
			} else {
				array[0] = "";
			} 
		} catch (Exception e) {
			array[0]="";
			array[1]="0";
		}
		return array;
  	}
  	
  	//NVarcharMax类型解析方法
  	public String[] NVarcharMax(String rowlog,String row_log,String first,int i,int x) {
    	String[] array = new String[2];
    	try {
			int m = Integer.parseInt(first);//数据起始位置
			StringBuffer sbu = new StringBuffer();
			String str = row_log.substring(4*i,4*(i+1));//取偏移,第i个变长数据的偏移
			Analyse c = new Analyse();
			str = c.transition(str);//字节序转换
			int n = Integer.parseInt(str,16);//数据终止位置(字节数)
			first = (2*n)+"";
			array[1] = first;//第i+1个数据的起始位置    	
			if(m>2*n) {//有空白插入存在导致列偏移数量偏差，需回倒
				m=m-4*(x-i-1);
			}
			String str1 = rowlog.substring(m,2*n);//取出数据
			//解析
			int len = str1.length();
			if (len!=0) {
				int p = 0;
				String[] chars = new String[len / 4];
				while (p < (len / 4)) {
					String str2 = str1.substring(4 * p, 4 * (p + 1));
					String str3 = c.transition(str2);//字节序转换
					str3 = c.UstartToCn(str3);//得到解析出的字符
					chars[p] = str3;
					p++;
				}
				for (int j = 0; j < (len / 4); j++) {
					sbu.append(chars[j]);
				}
				String str4 = sbu.toString();
				array[0] = str4;
			}else {
				array[0] = "";
			}
		} catch (Exception e) {
			array[0]="";
			array[1]="0";
		}
		return array;
  	}
  	//varchar类型解析方法
    public String[]  Varchar(String rowlog,String row_log,String first,int i,int x) {
    	String[] array = new String[2];
    	try {
			int m = Integer.parseInt(first);//数据起始位置
			
			String str = row_log.substring(4*i,4*(i+1));//取偏移,第i个变长数据的偏移
			Analyse c = new Analyse();
			str = c.transition(str);//字节序转换
			int n = Integer.parseInt(str,16);//数据终止位置(字节数)
			first = (2*n)+"";
			array[1] = first;//第i+1个数据的起始位置
			if(m>2*n) {//有空白插入存在导致列偏移数量偏差，需回倒
				m=m-4*(x-i-1);
			}
			String str1 = rowlog.substring(m,2*n);//取出数据
			//解析
			String str2 ="";
			if (str1.length()!=0) {
				try {
					str2 = c.StringToGBK(str1); 
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			array[0] = str2;
		} catch (Exception e) {
			array[0]="";
			array[1]="0";
		}
    	return array;
    }
    
    //varcharmax类型解析方法
    public String[] VarcharMax(String rowlog,String row_log,String first,int i,int x) {
    	String[] array = new String[2];
    	try {
			int m = Integer.parseInt(first);//数据起始位置
			
			String str = row_log.substring(4*i,4*(i+1));//取偏移,第i个变长数据的偏移
			Analyse c = new Analyse();
			str = c.transition(str);//字节序转换
			int n = Integer.parseInt(str,16);//数据终止位置(字节数)
			first = (2*n)+"";
			array[1] = first;//第i+1个数据的起始位置    	
			if(m>2*n) {//有空白插入存在导致列偏移数量偏差，需回倒
				m=m-4*(x-i-1);
			}
			String str1 = rowlog.substring(m,2*n);//取出数据
			//解析
			String str2 = "";
			if (str1.length()!=0) { 
				try {
					str2 = c.StringToGBK(str1);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			array[0] = str2;
		} catch (Exception e) {
			array[0]="";
			array[1]="0";
		}
    	return array;
    }    
   
    //varbinary类型解析方法
    public String[] Varbinary(String rowlog,String row_log,String first,int i,int x) {
    	String[] array = new String[2];
    	try {
			int m = Integer.parseInt(first);//数据起始位置
			
			String str = row_log.substring(4*i,4*(i+1));//取偏移,第i个变长数据的偏移    	
			int n = Integer.parseInt(transition(str),16);//数据终止位置(字节数)
			first = (2*n)+"";
			array[1] = first;//第i+1个数据的起始位置    
			if(m>2*n) {//有空白插入存在导致列偏移数量偏差，需回倒
				m=m-4*(x-i-1);
			}
			String str1 = rowlog.substring(m,2*n);//取出数据
			str1=(str1.replaceAll("^(0+)", "")).replaceAll("0+$", "");   			
			if(str1.length()!=0) {
				array[0] = "0x"+str1;//image型数据
			}else {
				array[0] = "";//插入为空白字符串
			}
		} catch (Exception e) {
			array[0]="";
			array[1]="0";
		}   	    
    	
    	return array;
    }
    
    //varbinarymax类型解析方法
    public String[] VarbinaryMax(String rowlog,String row_log,String first,int i,int x) {
    	String[] array = new String[2];
    	try {
			int m = Integer.parseInt(first);//数据起始位置
			
			String str = row_log.substring(4*i,4*(i+1));//取偏移,第i个变长数据的偏移    	
			int n = Integer.parseInt(transition(str),16);//数据终止位置(字节数)
			first = (2*n)+"";
			array[1] = first;//第i+1个数据的起始位置    	
			if(m>2*n) {//有空白插入存在导致列偏移数量偏差，需回倒
				m=m-4*(x-i-1);
			}
			String str1 = rowlog.substring(m,2*n);//取出数据
			str1=(str1.replaceAll("^(0+)", "")).replaceAll("0+$", "");   			
			if(str1.length()!=0) {
				array[0] = "0x"+str1;//image型数据
			}else {
				array[0] = "";//插入为空白字符串
			}
		} catch (Exception e) {
			array[0]="";
			array[1]="0";
		}   	    
 	
    	return array;
    }
    //text类型解析方法
    public String[] Text(String rowlog,String row_log,String first,String str ) {
    	String[] array = new String[2];
        //数据起始位置不需要
    	//String str = row_log.substring(4*i,4*(i+1));//取偏移，第i个变长数据的偏移
    	
    	//int n = Integer.parseInt(transition(str),16);
    	first = (Integer.parseInt(first)+32)+"";
    	array[1] = first;//第i+1个数据的起始位置
    	
    	String str1 = str.substring(40);//text的数据在第20个字节之后
    	Analyse c = new Analyse();
    	String str2 = "";
		try {
			str2 = c.StringToGBK(str1);
		} catch (Exception e) {			
			e.printStackTrace();
		}
    	str2=str2.trim();   			
    	array[0] = str2; 	    	    	    	
    	return array;
    }
    //ntext类型解析方法
    public String[] NText(String rowlog,String row_log,String first,String str) {
    	String[] array = new String[2];
    	//数据起始位置不需要
    	//String str = row_log.substring(4*i,4*(i+1));//取偏移，第i个变长数据的偏移
    	
    	//int n = Integer.parseInt(str,16);
    	first = (Integer.parseInt(first)+32)+"";
    	array[1] = first;//第i+1个数据的起始位置
    	
    	String str1 = str.substring(40);
    	StringBuffer sbu = new StringBuffer();
    	Analyse c = new Analyse();
    	int len = str1.length();
    	int i = 0;
    	String [] chars = new String[len/4];
    	while(i< (len/4)) {
    		String str2 = str1.substring(4*i,4*(i+1));
    		String str3 = c.transition(str2);//字节序转换
    		str3 = c.UstartToCn(str3);//得到解析出的字符
    		chars[i] = str3;
    		i++;
    	}
    	for(int j = 0;j<(len/4);j++) {
    		sbu.append(chars[j]);
    	}
    	String str4 = sbu.toString(); 
    	str4=str4.trim();
    	array[0] = str4;//解析ntext型数据    	    	    	    	
    	return array;
    }
    
    //image类型解析方法(不对数据进行处理，只传递下一个数据的起始位置）
    public String[] Image(String rowlog,String row_log,String first,String str ) {
    	String[] array = new String[2];
        //数据起始位置不需要
    	//String str = row_log.substring(4*i,4*(i+1));//取偏移，第i个变长数据的偏移
    	
    	//int n = Integer.parseInt(transition(str),16);
    	first = (Integer.parseInt(first)+32)+"";
    	array[1] = first;//第i+1个数据的起始位置
    	
    	String str1 = str.substring(40);//text的数据在第20个字节之后

    	str1=(str1.replaceAll("^(0+)", "")).replaceAll("0+$", "");   			
    	if(str1.length()!=0) {
    		array[0] = "0x"+str1;//image型数据
    	}else {
    		array[0] = "";//插入为空白字符串
    	}   	    	    	    	
    	return array;
    }
    
    //工具方法
    //GBK汉字编码转汉字
    public String StringToGBK(String string) throws Exception{
	    byte[] bytes = new byte[string.length() / 2];
	    for(int i = 0; i < bytes.length; i ++){
	        byte high = Byte.parseByte(string.substring(i * 2, i * 2 + 1), 16);
	        byte low = Byte.parseByte(string.substring(i * 2 + 1, i * 2 + 2), 16);
	        bytes[i] = (byte) (high << 4 | low);
	    }
	    String result = new String(bytes, "gbk");
	    return result;
	}
    
    //Unicode编码转字符
    public  String UstartToCn(String str) {
		StringBuilder sb = new StringBuilder().append("0x")
				.append(str.substring(0,4));
		Integer codeInteger = Integer.decode(sb.toString());
		int code = codeInteger.intValue();
		char c = (char)code;
		return String.valueOf(c);
	}
    
    //将二进制字符串转换为16进制字符串
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
	public String intToString(int[] arr) 
 	{		
 		StringBuilder sb = new StringBuilder();// 自定义一个字符缓冲区
 		//遍历int数组，并将int数组中的元素转换成字符串储存到字符缓冲区中去
 		for(int i=0;i<arr.length;i++)
 		{
           sb.append(arr[i]);	
 		}
 		return sb.toString();
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
}    	




    
   