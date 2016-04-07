package rtlib.stack.sourcesink.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.sourcesink.RandomStackSource;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class RandomStackSourceTests
{

	@Test
	public void test() throws IOException
	{
		final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		final RecyclerInterface<StackInterface, StackRequest> lRecycler = new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
																																																											10);
		RandomStackSource lRandomStackSource = new RandomStackSource(	100L,
																																	101L,
																																	103L,
																																	lRecycler);

		for (int i = 0; i < 100; i++)
		{
			StackInterface lStack = lRandomStackSource.getStack(i);

			lStack.getContiguousMemory().setByte(1, (byte) i);
			assertTrue(lStack.getContiguousMemory().getByte(1) == (byte) i);

			lStack.release();
		}

	}

}
