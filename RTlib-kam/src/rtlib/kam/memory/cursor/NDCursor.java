package rtlib.kam.memory.cursor;

public interface NDCursor
{
	public int getDimension();

	public long[] getDimensions();

	public long getMinPosition(final int pDimensionIndex);

	public long getMaxPosition(final int pDimensionIndex);

	public long getLengthInElements();

	public long getVolume();

	public boolean isVectorized();

	public long[] getCursorPosition();

	public long getCurrentFlatIndex();

	public long getCurrentVectorIndex();

	public long getCursorPosition(int pDimensionIndex);

	public void setCursorPosition(int pDimensionIndex,
																final long pNewPosition);

	public void incrementCursorPosition(final int pDimensionIndex);

	public void decrementCursorPosition(final int pDimensionIndex);

	public void reset();
}
