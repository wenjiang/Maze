public class Gateway {
	private static String IP = "192.168.1.11";

	private Mobile mMobile;

	public Gateway(Mobile mobile) {
		mMobile = mobile;
	}

	public String getIP() {
		return IP;
	}

	public byte[] response(byte[] command) {
		byte[] response = { (byte) 0xA0, (byte) 0, (byte) 1, (byte) 0x55 };
		response[1] = command[1];
		return response;
	}

	public void sendBaffleInfoIn(byte[] command) {
		mMobile.receiveBaffleInfoIn(command);
	}

	public void sendTargetInfoIn(byte[] command) {
		mMobile.receiveTaregetInfoIn(command);
	}
}
