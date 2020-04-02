package three;
import java.math.BigInteger;

import three.Server.ServerData;
import three.User.UserData;


/* 
 * Acknowledgment
 * Copyright (c) 2015 Cheng Huang (xdhuangcheng@gmail.com)
 * 
 */

public class Algorithms {
	
	private void step1(User user){
		user.processData();
	}
	
	private void step2(UserData userdata,Server server){
		server.processData(userdata);
	}
	
	private double step3(ServerData serverdata, User user){
		return user.calculate(serverdata);
	}
	
	//privacy preserving computing
	public double test(User user, Server server) {
		step1(user);
		step2(user.getUserdata(), server);
		double cosresult=step3(server.getServerdata(), user);
		return cosresult;
		//System.out.println(cosresult);
	}
	
	//computing real result
	public double testreal(int[] a, int[] b)
	{
		int A=0;
		int B=0;
		for(int i=0;i<a.length;i++)
		{
			A=A+(a[i]*a[i]);
			B=B+(b[i]*b[i]);
		}
		
		double ab=0;
		for(int i=0;i<a.length;i++)
		{
			ab=ab+a[i]*b[i];
		}
		double result=ab/(Math.sqrt(A)*Math.sqrt(B));
		return result;
		//System.out.println(result);
	}
	
	//computing paillier result
	public double testpaillier(int[] a, int[] b) throws Exception
	{
		int A=0;
		int B=0;
		for(int i=0;i<a.length;i++)
		{
			A=A+(a[i]*a[i]);
			B=B+(b[i]*b[i]);
		}
		
		Paillier paillier=new Paillier(1024);
		BigInteger[] Ea=new BigInteger[a.length];
		BigInteger[] Eab=new BigInteger[a.length];
		
		for(int i=0;i<a.length;i++)
		{
			Ea[i]=paillier.encrypt(BigInteger.valueOf(a[i]));
			Eab[i]=Ea[i].modPow(BigInteger.valueOf(b[i]), paillier.getNsquare());
		}
		BigInteger ab=BigInteger.ONE;
		for(int i=0;i<a.length;i++)
		{
			ab=ab.multiply(Eab[i]).mod(paillier.getNsquare());
		}
		
		double decryptab=paillier.decrypt(ab).doubleValue();
		return decryptab/(Math.sqrt(A)*Math.sqrt(B));
	}
	
	public static void main(String[] args) throws Exception {
		User user=new User(2, 1000, 512, 200, 128);
		Server server=new Server(2, 1000, 128);
		user.generate_a();
		server.generate_b();
		Algorithms algs=new Algorithms();
		long time1,time2,time3,time4;
		time1=System.currentTimeMillis();
		System.out.println("Plain Text: "+algs.testreal(user.getA(), server.getB()));
		time2=System.currentTimeMillis();
		System.out.println("Plain Test cost time: "+(time2-time1)+"ms");
		
		
		System.out.println("Paillier :"+algs.testpaillier(user.getA(), server.getB()));
		time3=System.currentTimeMillis();
		System.out.println("Paillier cost time: "+(time3-time2)+"ms");
		
		
		System.out.println("Efficient computing :"+algs.test(user, server));
		time4=System.currentTimeMillis();
		System.out.println("Efficient computing cost time: "+(time4-time3)+"ms");
	}
}
