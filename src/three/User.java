package three;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import three.Server.ServerData;


/*  Copyright (c) 2015 Cheng Huang (xdhuangcheng@gmail.com)
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


public class User {
	
	private final static int CERTAINTY=80;
	
	//Data send to Server
	public class UserData{
		BigInteger alpha;	//large prime alpha
		BigInteger p;		//large prime p
		BigInteger []C; 	//C1 C2 ... C_n from 1 to n+2
		
		public UserData(int n) {
			// TODO Auto-generated constructor stub
			C=new BigInteger[n+2];
		}
	}
	private int q;			//F_q
	private int n;			//the vectors length
	private int k1;			//security parameter k_1
	private int k2;			//security parameter k_2
	private int k3;			//security parameter k_3
	private int[] a;	//the vector a_1 a_2 ... a_n a_n+1 a_n+2
	private BigInteger[] c; // random number c_i (i from 1-n+2)
	private UserData userdata;
	private BigInteger s;	//large random number s(Z_p)
	private BigInteger sinverse; //s inverse (mod p)
	private int A; //the sum of (a_i)^2 (i from 1 to n)
	
	public UserData getUserdata() {
		return userdata;
	}
	
	public int[] getA() {
		return a;
	}
	
	public User(int q, int n, int k1, int k2, int k3) {
		// TODO Auto-generated constructor stub
		this.q=q;
		this.n=n;
		this.k1=k1;
		this.k2=k2;
		this.k3=k3;
	}
	
	//generate vectors from a_1 to a_n+2
	public void generate_a()
	{
		a=new int[n+2];
		for(int i=0;i<n;i++)
		{
			a[i]=getRandomFromFq();
		}
		a[n]=0;
		a[n+1]=0;
	}
	
	//return a random integer in F_q
	private int getRandomFromFq(){
		int r=new Random().nextInt(q);
        return r;
	}
	
	//return a random integer in Z_p
	private BigInteger getRandomFromZp(){
		int modLength=userdata.p.bitLength();
        BigInteger r;
        do
        {
            r = new BigInteger(modLength, new SecureRandom());
        }
        while (r.compareTo(userdata.p) >= 0 || r.gcd(userdata.p).intValue() != 1);
        return r;
	}
	
	//privacy-preserving process data
	public void processData()
	{
		userdata=new UserData(n);
		userdata.p=new BigInteger(k1, CERTAINTY, new SecureRandom());
		userdata.alpha=new BigInteger(k2, CERTAINTY, new SecureRandom());
		s=getRandomFromZp();
		c=new BigInteger[n+2];
		for(int i=0;i<n+2;i++)
		{
			c[i]=new BigInteger(k3, new SecureRandom());
		}
		
		for(int i=0;i<n+2;i++)
		{
			if(a[i]==0)
			{
				userdata.C[i]=s.multiply(c[i]).mod(userdata.p);
			}
			else
			{
				userdata.C[i]=s.multiply(BigInteger.valueOf(a[i]).multiply(userdata.alpha).add(c[i])).mod(userdata.p);
			}
		}
		
		A=0;
		for(int i=0;i<n;i++)
		{
			A=A+(a[i]*a[i]);
		}
		sinverse=s.modInverse(userdata.p);
	}
	
	//privacy-preserving calculate vectors a product b
	public double calculate(ServerData serverdata)
	{
		BigInteger E=sinverse.multiply(serverdata.D).mod(userdata.p);
		BigInteger alphasquare=userdata.alpha.multiply(userdata.alpha);
		double ab=E.subtract(E.mod(alphasquare)).divide(alphasquare).doubleValue();
		return ab/(Math.sqrt(A)*Math.sqrt(serverdata.B));
	}
	
}	
