import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class MobileTest extends TestCase {
	private Mobile mMobile;
	private Gateway mGateway;

	public MobileTest() {
		mMobile = new Mobile();
		mGateway = new Gateway(mMobile);
	}

	// 测试模块：手机搜索网关

	// 测试手机是否能向局域网内所有IP发送指令，指令格式为：指令类型1(0xA0) +
	// 指令序号1(0-99),每发送一条指令，序号加1，到99之后又从0开始

	// 测试用例：测试网关是否能够收到手机发出的指令并应答
	// 应答格式：0xA0 + 指令序号(0-99) + 1 + 指令执行结果(0x55表示准备就绪，
	// 0xAA表示还未准备好)，为了模拟现实的网关，需要创建一个模型，该模型包含一个数据
	// 域IP地址，192.168.1.11，为String类型，然后在接收到指令后随机决定准备好还是未
	// 准备的状态,手机在收到应答指令后，无论成功与否，都要保存网关的IP地址

	// 测试用例：网关单独应答一条指令
	public void testSingleResponse() {
		byte[] response = mMobile.connect(mGateway);
		byte[] readyResponse = { (byte) 0xA0, (byte) 0, (byte) 1, (byte) 0x55 };
		for (int i = 0, length = readyResponse.length; i < length; ++i) {
			assertEquals(readyResponse[i], response[i]);
		}

		assertEquals("192.168.1.11", mGateway.getIP());
	}

	// 测试用例：网关应答多条指令
	public void testMultiResponse() {
		List<byte[]> bytesList = new ArrayList<byte[]>();
		for (int i = 0; i < 100; ++i) {
			byte[] command = { (byte) 0xA0, (byte) i, (byte) 1, (byte) 0x55 };
			bytesList.add(command);
		}
		bytesList
				.add(new byte[] { (byte) 0xA0, (byte) 0, (byte) 1, (byte) 0x55 });

		byte[] response;
		for (int i = 0; i < 101; ++i) {
			response = mMobile.connect(mGateway);
			byte[] bytes = bytesList.get(i);
			assertEquals(bytes[1], response[1]);
		}

		assertEquals("192.168.1.11", mGateway.getIP());
	}

	// 测试用例：网关向手机发送障碍物信息,手机是否能够正确的读取出来

	// 测试用例：障碍物为矩形，指令的第5和第6个字节表示X坐标值，第7和第8个字节表示Y坐标值，当位置的信息超出
	// 255就会向前借一位。
	public void testRectBaffle() {
		byte[] command = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 0, (byte) 0,
				(byte) 15, (byte) 0, (byte) 13, (byte) 2, (byte) 3, (byte) 4 };

		mGateway.sendBaffleInfoIn(command);

		int xPiexl = 15;
		int yPiexl = 13;
		String shape = "矩形";
		int height = 4;
		int width = 3;
		int number = 0;

		Map<Integer, Baffle> baffles = mMobile.getBaffles();

		assertEquals(xPiexl, baffles.get(0).getX());
		assertEquals(yPiexl, baffles.get(0).getY());
		assertEquals(shape, baffles.get(0).getShape());
		assertEquals(height, baffles.get(0).getHeight());
		assertEquals(width, baffles.get(0).getWidth());
		assertEquals(number, baffles.get(0).getNumber());
	}

	// 测试用例：障碍物为圆形
	public void testCircleBaffle() {
		byte[] command = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 1, (byte) 0,
				(byte) 34, (byte) 0, (byte) 23, (byte) 1, (byte) 3, (byte) 3 };
		mGateway.sendBaffleInfoIn(command);

		int xPiexl = 34;
		int yPiexl = 23;
		String shape = "圆形";
		int radius = 3;
		int number = 1;

		Map<Integer, Baffle> baffles = mMobile.getBaffles();

		assertEquals(xPiexl, baffles.get(1).getX());
		assertEquals(yPiexl, baffles.get(1).getY());
		assertEquals(shape, baffles.get(1).getShape());
		assertEquals(radius, baffles.get(1).getRadius());
		assertEquals(number, baffles.get(1).getNumber());
	}

	// 测试用例：障碍物的位置信息X坐标值超过255，Y坐标值超过255
	public void testBaffleLocation() {
		byte[] command = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 0, (byte) 2,
				(byte) 0, (byte) 2, (byte) 0, (byte) 2, (byte) 3, (byte) 4 };

		mGateway.sendBaffleInfoIn(command);

		int xPiexl = 512;
		int yPiexl = 512;
		String shape = "矩形";
		int height = 4;
		int width = 3;
		int number = 0;

		Map<Integer, Baffle> baffles = mMobile.getBaffles();

		assertEquals(xPiexl, baffles.get(0).getX());
		assertEquals(yPiexl, baffles.get(0).getY());
		assertEquals(shape, baffles.get(0).getShape());
		assertEquals(height, baffles.get(0).getHeight());
		assertEquals(width, baffles.get(0).getWidth());
		assertEquals(number, baffles.get(0).getNumber());
	}

	// 测试用例：测试手机是否能够自动更新对应编号障碍物的信息
	public void testUpdateInfo() {
		byte[] command = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 0, (byte) 1,
				(byte) 5, (byte) 1, (byte) 3, (byte) 2, (byte) 4, (byte) 3 };

		mGateway.sendBaffleInfoIn(command);

		byte[] newCommand = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 0,
				(byte) 0, (byte) 34, (byte) 0, (byte) 34, (byte) 2, (byte) 3,
				(byte) 4 };

		mGateway.sendBaffleInfoIn(newCommand);

		Map<Integer, Baffle> baffles = mMobile.getBaffles();

		assertEquals(34, baffles.get(0).getX());
		assertEquals(34, baffles.get(0).getY());
		assertEquals(0, baffles.get(0).getNumber());
	}

	// 测试用例：网关向手机发送导航目标信息，手机是否能够读取

	// 测试用例：手机是否能够读取导航目标信息
	public void testTargetInfo() {
		byte[] command = { (byte) 0xA2, (byte) 0, (byte) 5, (byte) 0, (byte) 0,
				(byte) 34, (byte) 0, (byte) 13 };

		mGateway.sendTargetInfoIn(command);

		int xPiexl = 34;
		int yPiexl = 13;

		List<TargetObject> targets = mMobile.getTargets();
		int length = targets.size();

		assertEquals(xPiexl, targets.get(length - 1).getX());
		assertEquals(yPiexl, targets.get(length - 1).getY());
	}

	// 测试用例：手机是否能够根据最近两次的位置信息判断目标的移动方向
	public void testTargetMove() {
		byte[] command = { (byte) 0xA2, (byte) 0, (byte) 5, (byte) 0, (byte) 0,
				(byte) 0, (byte) 0, (byte) 0 };
		mGateway.sendTargetInfoIn(command);
		byte[] newCommand = { (byte) 0xA2, (byte) 0, (byte) 5, (byte) 0,
				(byte) 0, (byte) 1, (byte) 0, (byte) 1 };
		mGateway.sendTargetInfoIn(newCommand);

		assertEquals("您正向东北45度行动", mMobile.getTargetDirection());
	}

	// 测试用例：手机是否能够显示出两个障碍物和目标的位置

	// 测试用例：假设两个障碍物都是正方形，一个坐标是(5, 5), 边长是2，另一个坐标是
	// (2, 2), 边长是1，而人假设中心坐标为（0,0),边长都是5分米。
	public void testDoubleBaffleMaze() {
		List<Baffle> baffleList = new ArrayList<Baffle>();
		Baffle baffle0 = new Baffle(0);
		baffle0.setRect("矩形", 2, 2);
		baffle0.setLocation(5, 5);
		Baffle baffle1 = new Baffle(1);
		baffle1.setRect("矩形", 1, 1);
		baffle1.setLocation(2, 2);
		Baffle baffle2 = new Baffle(2);
		baffle2.setRect("矩形", 1, 1);
		baffle2.setLocation(3, 3);
		TargetObject target = new TargetObject(0, 0);
		Maze maze = new Maze();
		maze.addBaffle(baffle0);
		maze.addBaffle(baffle1);
		maze.addBaffle(baffle2);
		maze.addTarget(target);
		maze.draw();
	}

	// 测试用例：手机是否能够根据最近障碍物和目标的信息提出合理的建议

	// 测试用例：假设障碍物是上面的圆形和矩形，然后人的平均肩膀宽度是5分米。
	public void testAdvice() {
		byte[] circleLocation = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 1,
				(byte) 0, (byte) 34, (byte) 0, (byte) 23, (byte) 1, (byte) 3,
				(byte) 3 };
		mGateway.sendBaffleInfoIn(circleLocation);

		byte[] rectLocation = { (byte) 0xA1, (byte) 0, (byte) 7, (byte) 0,
				(byte) 0, (byte) 15, (byte) 0, (byte) 13, (byte) 2, (byte) 3,
				(byte) 4 };

		mGateway.sendBaffleInfoIn(rectLocation);

		byte[] targetLocation = { (byte) 0xA2, (byte) 0, (byte) 5, (byte) 0,
				(byte) 0, (byte) 34, (byte) 0, (byte) 13 };
		mGateway.sendTargetInfoIn(targetLocation);

		assertEquals("向左走", mMobile.getAdvie());
	}
}
