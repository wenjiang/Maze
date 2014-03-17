import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mobile {
	private byte[] CONNECT_COMMAND = { (byte) 0xA0, (byte) 0 };
	private String mIP;
	private Map<Integer, Baffle> mBaffleMap;
	private TargetObject mTarget;
	private List<TargetObject> mTargetList;

	public Mobile() {
		mBaffleMap = new HashMap<Integer, Baffle>();
		mTargetList = new ArrayList<TargetObject>();
	}

	public byte[] connect(Gateway gate) {
		byte[] response = gate.response(CONNECT_COMMAND);
		mIP = gate.getIP();

		CONNECT_COMMAND[1]++;
		if (CONNECT_COMMAND[1] > (byte) 99) {
			CONNECT_COMMAND[1] = (byte) 0;
		}
		return response;
	}

	public List<TargetObject> getTargets() {
		return mTargetList;
	}

	public Map<Integer, Baffle> getBaffles() {
		return mBaffleMap;
	}

	public String getIP() {
		return mIP;
	}

	public String getTargetDirection() {
		String direction = "您打算做什么";
		int length = mTargetList.size();
		if (length < 2) {
			return direction;
		}

		direction = getDirection(length);

		return direction;
	}

	private String getDirection(int length) {
		int afterX = mTargetList.get(length - 1).getX();
		int afterY = mTargetList.get(length - 1).getY();
		int aheadX = mTargetList.get(length - 2).getX();
		int aheadY = mTargetList.get(length - 2).getY();

		int distanceX = afterX - aheadX;
		int distanceY = afterY - aheadY;

		if ((distanceX == 0) && (distanceY > 0)) {
			return "您正向北行动";
		}

		if ((distanceX == 0) && (distanceY < 0)) {
			return "您正向南行动";
		}

		if ((distanceY == 0) && (distanceX > 0)) {
			return "您正向东行动";
		}

		if ((distanceY == 0) && (distanceX < 0)) {
			return "您正向西行动";
		}

		double angle = Math
				.toDegrees(Math.atan((double) distanceY / distanceX));
		long angleValue = Math.round(angle);
		if ((distanceX > 0) && (distanceY > 0)) {
			return "您正向东北" + angleValue + "度行动";
		}

		if ((distanceX > 0) && (distanceY < 0)) {
			return "您正向东南" + angleValue + "度行动";
		}

		if ((distanceX < 0) && (distanceY < 0)) {
			return "您正向西南" + angleValue + "度行动";
		}

		if ((distanceX < 0) && (distanceY > 0)) {
			return "您正向西北" + angleValue + "度行动";
		}
		return "您打算做什么";
	}

	public String getAdvie() {
		return null;
	}

	public void receiveBaffleInfoIn(byte[] command) {
		int number = command[3];
		Baffle baffle = new Baffle(number);
		baffle.setLocation(
				Integer.parseInt((command[4] * 256 + command[5]) + ""),
				Integer.parseInt((command[6] * 256 + command[7]) + ""));

		if (command[8] == 1) {
			baffle.setCircle("圆形", command[9]);
			mBaffleMap.put(number, baffle);
			return;
		}

		if (command[8] == 2) {
			baffle.setRect("矩形", command[9], command[10]);
			mBaffleMap.put(number, baffle);
			return;
		}
	}

	public void receiveTaregetInfoIn(byte[] command) {
		mTarget = new TargetObject(Integer.parseInt((command[4] + command[5])
				+ ""), Integer.parseInt((command[6] + command[7]) + ""));
		mTargetList.add(mTarget);
	}
}
