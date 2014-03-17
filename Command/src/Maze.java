import java.util.ArrayList;
import java.util.List;

public class Maze {
	private List<String> mPointList;
	private List<Baffle> mBaffleList;
	private int mMaxSideX = 0;;
	private int mMaxSideY = 0;
	private int mTargetX;
	private int mTargetY;
	private static int DESPLACEMENT = 3;

	public Maze() {
		mPointList = new ArrayList<String>();
		mBaffleList = new ArrayList<Baffle>();
	}

	public void addBaffle(Baffle baffle) {
		int sideX = baffle.getX() + baffle.getWidth() / 2;
		if (mMaxSideX < sideX) {
			mMaxSideX = sideX;
		}

		int sideY = baffle.getY() + baffle.getHeight() / 2;
		if (mMaxSideY < sideY) {
			mMaxSideY = sideY;
		}

		mBaffleList.add(baffle);
	}

	private void drawBaffle(Baffle baffle) {
		if (baffle.getHeight() >= 2 && baffle.getWidth() >= 2) {
			int factor = baffle.getY() + baffle.getHeight() / 2 + DESPLACEMENT;
			int startX = baffle.getX() - baffle.getWidth() / 2;
			int startY = baffle.getY() - baffle.getHeight() / 2;
			int endX = baffle.getX() + baffle.getWidth() / 2 + 1;
			int endY = baffle.getY() + baffle.getHeight() / 2 + 1;
//			for (int i = endX; i > startX; --i) {
//				for (int j = startY; j < endY; ++j) {
//					mPointList.set(i * factor + j, " + ");
//				}
//			}
			for (int i = startX; i < endX; ++i) {
				for (int j = startY; j < endY; ++j) {
					mPointList.set(i * factor + j, " + ");
				}
			}
		} else if (baffle.getHeight() < 2 && baffle.getWidth() < 2) {
			mPointList.set(
					(mMaxSideX + DESPLACEMENT) * baffle.getY() + baffle.getX(),
					" + ");
		}
	}

	public void draw() {
		int x = mMaxSideX + DESPLACEMENT - 1;
		int y = mMaxSideY + DESPLACEMENT - 1;
		for (int i = 0; i < y; ++i) {
			for (int j = 0; j < x; ++j) {
				mPointList.add(" - ");
			}
			mPointList.add("" + "\n");
		}

		for (Baffle baffle : mBaffleList) {
			drawBaffle(baffle);
		}

		mPointList.set(mTargetX * (y + 1) + mTargetY, " * ");

		for (String point : mPointList) {
			System.out.print(point);
		}
	}

	public void addTarget(TargetObject target) {
		mTargetX = target.getX();
		mTargetY = target.getY();
	}
}
