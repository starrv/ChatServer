package main;

public class OneTimePad {
	private static final char[] abcVal = {
			'X','T','V','L','U','Q','G','M',
			'I','R','Z','D','W','S','J','Y',
			'F','O','N','B','E','A','H','C',
			'P','K', 'x', 't','v','l','u','q','g',
			'm','i','r','z','d','w','s','j','y',
			'f','o','n','b','e','a','h','c','p',
			'k',' ','.','!','?','$','(',')',',','/',
			'-','+','*','"','^','=',';','0','1','2',
			'3','4','5','6','7','8','9','\'',':',
			'\n'
		};
		
		private String plainMessage ="";
		private String encryptedMessage = "";
		private String currentKey="";
		
		public OneTimePad() {
			// TODO Auto-generated constructor stub
			plainMessage="NO MESSAGE";
			currentKey= this.generateKey(plainMessage);
			
		}
		
		public OneTimePad(String msg) {
			// TODO Auto-generated constructor stub
			plainMessage=msg;
			currentKey= generateKey(msg);
			encryptedMessage = encrypt(msg);
		}
		
		public String getPlainMessage()
		{
			return plainMessage;
		}
		
		public void setPlainMessage(String msg)
		{
			plainMessage=msg;
			currentKey= generateKey(msg);
			encryptedMessage = encrypt(msg);
		}
		
		public String getCurrentKey()
		{
			return currentKey;
		}
		
		public String getEncryptedMessage()
		{
			return encryptedMessage;
		}
		
//		public static void main(String[] args) {
//			// TODO Auto-generated method stub
//			OneTimePad otp = new OneTimePad();
//			Functions.printMessage("The Key:"+ otp.generateKey("HELLO"));
//			OneTimePad otpS = new OneTimePad("Hola que tal!343.$4*5=$20 and 5+2=7!!??");
//			Functions.printMessage("Plain message is "+otpS.plainMessage);
//			Functions.printMessage("Current key is "+otpS.currentKey);
//			Functions.printMessage("Encrypted message is "+otpS.encryptedMessage);
//			Functions.printMessage("Decrypted message is  "+otpS.decrypt(otpS.encryptedMessage));
//			Functions.printMessage("Result is "+mod(4,45));
//		}
	//	
		public String generateKey(String msg){
			String key ="";
			for(int i=0; i<msg.length(); i++){
				key= key+(int)(Math.random()*10);
			}
			return key;
		}
		
		private String encrypt(String plainMsg)
		{
			String encMsg="";
			for(int i=0; i<plainMsg.length(); i++){
				if(plainMsg.charAt(i)=='~')
				{
					encMsg+='~';
				}
				else
				{
					//Functions.printMessage("inside encrypt for loop at "+i+" value is "+plainMsg.charAt(i));
					//assign numerical value to each character in the plain message
					int plainCharIndex = getNumberForChar(plainMsg.charAt(i));
					int keyDigit=Integer.parseInt(currentKey.substring(i, i+1));
					//Functions.printMessage("PlainChar index for value "+plainMsg.charAt(i)+ " is "+ plainCharIndex);//for debugging
					int encryptedIndex =modPlus(plainCharIndex, (int)Math.pow(keyDigit, 2), abcVal.length);
					//Functions.printMessage("log base e of key digit "+keyDigit+ " is "+(int)Math.log(keyDigit));	
					//add the key to the plain message 
					encMsg = encMsg+ abcVal[encryptedIndex];
				}
			}
			return encMsg;
		}
		
		
		private static int getNumberForChar(char c){
			int n = -1;
			for(int i=0; i<abcVal.length; i++){
				if(c == (abcVal[i])){
					return i;
				}
			}
			return n;
		}
		
		public static String decryptMessage(String msg, String key)
		{
			String decMsg="";
			for(int i=0; i<msg.length(); i++){
				if(msg.charAt(i)=='~')
				{
					decMsg+='~';
				}
				else
				{
					//Functions.printMessage("inside encrypt for loop at "+i+" value is "+plainMsg.charAt(i));
					//assign numerical value to each character in the plain message
					int encCharIndex = getNumberForChar(msg.charAt(i));
					int keyDigit=Integer.parseInt(key.substring(i, i+1));
					int decryptedIndex =modMinus(encCharIndex, (int)Math.pow(keyDigit, 2), abcVal.length);
					decMsg = decMsg+ abcVal[decryptedIndex];
				}
			}
			return decMsg;
		}
		
		
		protected String decrypt(String encMsg)
		{
			String decMsg="";
			for(int i=0; i<encMsg.length(); i++){
				//Functions.printMessage("inside encrypt for loop at "+i+" value is "+plainMsg.charAt(i));
				//assign numerical value to each character in the plain message
				int encCharIndex = getNumberForChar(encryptedMessage.charAt(i));
				int keyDigit=Integer.parseInt(currentKey.substring(i, i+1));
				//Functions.printMessage("Encypted Char index for value "+encMsg.charAt(i)+ " is "+ encCharIndex);//for debugging
				int decryptedIndex =modMinus(encCharIndex, (int)Math.pow(keyDigit, 2), abcVal.length);
				//Functions.printMessage("log base e of key digit "+keyDigit+ " is "+(int)Math.log(keyDigit));
				//add the key to the plain message 
				decMsg = decMsg+ abcVal[decryptedIndex];
			}
			return decMsg;
		}
			
		private static int modPlus(int a, int b, int n)
		{
			int result=(a+b)%n;
			while(result<0)
			{
				result+=n;
			}
			return result;
		}
		
		private static int modMinus(int a, int b, int n)
		{
			int result=(a-b)%n;
			while(result<0)
			{
				result+=n;
			}
			return result;
		}
		
		protected static int mod(int a, int n)
		{
			int quotient=a/n;
			int diff=a-n*quotient;
			while(diff<0)
			{
				diff+=n;
			}
			return diff;
		}
}
