public class Baffle {
	private int mHeight;
	private int mRadius;
	private String mShape;
	private int mNumber;
	private int mWidth;
	private int mXPiexl;
	private int mYPiexl;

	public Baffle(int number) {
		mNumber = number;
	}

	public int getHeight() {
		return mHeight;
	}

	public int getNumber() {
		return mNumber;
	}

	public int getRadius() {
		return mRadius;
	}

	public String getShape() {
		return mShape;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getX() {
		return mXPiexl;
	}

	public int getY() {
		return mYPiexl;
	}

	public void setCircle(String shape, int radius) {
		mShape = shape;
		mRadius = radius;
	}

	public void setLocation(int x, int y) {
		mXPiexl = x;
		mYPiexl = y;
	}

	public void setRect(String shape, int width, int height) {
		mShape = shape;
		mWidth = width;
		mHeight = height;
	}
}
