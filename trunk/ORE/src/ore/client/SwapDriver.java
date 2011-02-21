package ore.client;

public class SwapDriver {
	
	public static void main(String[] args) throws Exception {
		Machine.createMachines(Config.IPs, Config.httpPorts, Config.jmsPorts);
		Machine one = Machine.getMachine(Config.IPs[0] + ":" + Config.httpPorts[0]);
		Machine two = Machine.getMachine(Config.IPs[1] + ":" + Config.httpPorts[1]);
		one.redirect(two);
		two.redirect(one);
	}

}
