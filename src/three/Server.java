package three;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import three.User.UserData;


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

public class Server {
	
	public class ServerData{
		BigInteger D;
		int B;
	}
	
	private int q;
	private int n;
	private int k4;
	private int[] b;
	private BigInteger ri;
	private BigInteger [] D;
	private ServerData serverdata;

	public ServerData getServerdata() {
		return serverdata;
	}
	
	public int[] getB() {
		return b;
	}
	
	public Server(int q, int n, int k4) {
		// TODO Auto-generated constructor stub
		this.q=q;
		this.n=n;
		this.k4=k4;
	}
	
	//generate vectors from b_1 to b_n 
	public void generate_b()
	{
		b=new int[n+2];
		for(int i=0;i<n;i++)
		{
			b[i]=getRandomFromFq();
		}
		b[n]=0;
		b[n+1]=0;
	}
	
	
	//return a random integer in F_q
	private int getRandomFromFq(){
		int r=new Random().nextInt(q);
        return r;
	}
	
	//process data
	public void processData(UserData userdata)
	{
		serverdata=new ServerData();
		ri=new BigInteger(k4, new SecureRandom());
		D=new BigInteger[n+2];
		for(int i=0;i<n+2;i++)
		{
			if(b[i]==0)
			{
				D[i]=ri.multiply(userdata.C[i]).mod(userdata.p);
			}
			else
			{
				D[i]=BigInteger.valueOf(b[i]).multiply(userdata.alpha).multiply(userdata.C[i]).mod(userdata.p);
			}
		}
		serverdata.B=0;
		serverdata.D=BigInteger.ZERO;
		
		for(int i=0;i<n;i++)
		{
			serverdata.B=serverdata.B+(b[i]*b[i]);
		}
		
		for(int i=0;i<n+2;i++)
		{
			serverdata.D=serverdata.D.add(D[i]);
		}
		serverdata.D=serverdata.D.mod(userdata.p);
	}
}
