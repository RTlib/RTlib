package rtlib.stack.sourcesink.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.imglib2.Cursor;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.PlanarCursor;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import rtlib.core.variable.ObjectVariable;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.sourcesink.LocalFileStackSink;
import rtlib.stack.sourcesink.LocalFileStackSource;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.recycling.BasicRecycler;

public class LocalFileStackTests
{

	private static final long cDiv = 4;

	private static final long cSizeX = 2048 / cDiv;
	private static final long cSizeY = 2048 / cDiv;
	private static final long cSizeZ = 512 / cDiv;
	private static final int cBytesPerVoxel = 2;

	private static final int cNumberOfStacks = 2;
	private static final int cMaximalNumberOfAvailableStacks = 20;

	@Test
	public void testWriteSpeed() throws IOException
	{

		for (int r = 0; r < 10; r++)
		{
			/*final File lRootFolder = new File(File.createTempFile("test",
																														"test")
																						.getParentFile(),
																				"LocalFileStackTests" + Math.random());/**/

			final File lRootFolder = new File("E:/Temp.testWriteSpeed");

			lRootFolder.mkdirs();
			System.out.println(lRootFolder);

			final LocalFileStackSink<UnsignedShortType, ShortOffHeapAccess> lLocalFileStackSink = new LocalFileStackSink<UnsignedShortType, ShortOffHeapAccess>(new UnsignedShortType(),
																																																																													lRootFolder,
																																																																													"testSink");

			@SuppressWarnings("unchecked")
			final OffHeapPlanarStack lStack = OffHeapPlanarStack.createStack(	cSizeX,
																																				cSizeY,
																																				cSizeZ);

			assertEquals(	cSizeX * cSizeY * cSizeZ,
										lStack.getNumberOfVoxels());

			assertEquals(	cSizeX * cSizeY * cSizeZ * cBytesPerVoxel,
										lStack.getSizeInBytes());

			System.out.println("generating data...");

			ContiguousMemoryInterface lContiguousMemory = lStack.getContiguousMemory();

			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemory);
			int i = 0;
			while (lBuffer.hasRemaining())
			{
				lBuffer.writeChar((char) i++);
			}

			System.out.println("done generating data...");

			System.out.println("start");
			long lStart = System.nanoTime();
			assertTrue(lLocalFileStackSink.appendStack(lStack));
			long lStop = System.nanoTime();
			System.out.println("stop");

			double lElapsedTimeInSeconds = (lStop - lStart) * 1e-9;

			double lSpeed = (lStack.getSizeInBytes() * 1e-6) / lElapsedTimeInSeconds;

			System.out.format("speed: %g \n", lSpeed);

			lLocalFileStackSink.close();

			try
			{
				FileUtils.deleteDirectory(lRootFolder);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}

	}

	@Test
	public void testSinkAndSource() throws IOException
	{

		final File lRootFolder = new File(File.createTempFile("test",
																													"test")
																					.getParentFile(),
																			"LocalFileStackTests" + Math.random());/**/

		// final File lRootFolder = new File("/Volumes/External/Temp");

		lRootFolder.mkdirs();
		System.out.println(lRootFolder);

		{
			final LocalFileStackSink<UnsignedShortType, ShortOffHeapAccess> lLocalFileStackSink = new LocalFileStackSink<UnsignedShortType, ShortOffHeapAccess>(new UnsignedShortType(),
																																																																													lRootFolder,
																																																																													"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSink.getMetaDataVariableBundle();

			lVariableBundle.addVariable(new ObjectVariable<Double>(	"doublevar1",
																															312.0));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));

			@SuppressWarnings("unchecked")
			final OffHeapPlanarStack lStack = OffHeapPlanarStack.createStack(	cSizeX,
																																				cSizeY,
																																				cSizeZ);

			assertEquals(	cSizeX * cSizeY * cSizeZ,
										lStack.getNumberOfVoxels());
			// System.out.println(lStack.mNDimensionalArray.getLengthInElements()
			// *
			// 2);

			assertEquals(	cSizeX * cSizeY * cSizeZ * cBytesPerVoxel,
										lStack.getSizeInBytes());

			for (int i = 0; i < cNumberOfStacks; i++)
			{

				final PlanarCursor<UnsignedShortType> lCursor = lStack.getPlanarImage()
																															.cursor();

				while (lCursor.hasNext())
				{
					final UnsignedShortType lUnsignedShortType = lCursor.next();
					lUnsignedShortType.set(i);
				}

				lCursor.reset();

				while (lCursor.hasNext())
				{
					final UnsignedShortType lUnsignedShortType = lCursor.next();
					assertEquals(i & 0xFFFF, lUnsignedShortType.get());
				}

				assertTrue(lLocalFileStackSink.appendStack(lStack));
			}

			assertEquals(	cNumberOfStacks,
										lLocalFileStackSink.getNumberOfStacks());

			lLocalFileStackSink.close();
		}

		{
			final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

			final BasicRecycler<StackInterface, StackRequest> lStackRecycler = new BasicRecycler<StackInterface, StackRequest>(	lOffHeapPlanarStackFactory,
																																																													cMaximalNumberOfAvailableStacks);

			final LocalFileStackSource<UnsignedShortType, ShortOffHeapAccess> lLocalFileStackSource = new LocalFileStackSource<UnsignedShortType, ShortOffHeapAccess>(new UnsignedShortType(),
																																																																																lStackRecycler,
																																																																																lRootFolder,
																																																																																"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSource.getMetaDataVariableBundle();
			lVariableBundle.addVariable(new ObjectVariable<Double>(	"doublevar1",
																															312.0));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));
			final VariableInterface<Double> lVariable1 = lVariableBundle.getVariable("doublevar1");
			// System.out.println(lVariable1.get());
			assertEquals(312, lVariable1.get(), 0.5);

			final VariableInterface<String> lVariable2 = lVariableBundle.getVariable("stringvar1");
			// System.out.println(lVariable2.get());
			assertEquals("123", lVariable2.get());

			StackInterface lStack;

			lLocalFileStackSource.update();

			assertEquals(	cNumberOfStacks,
										lLocalFileStackSource.getNumberOfStacks());

			for (int i = 0; i < cNumberOfStacks; i++)
			{
				lStack = lLocalFileStackSource.getStack(i);
				final Cursor<UnsignedShortType> lCursor = lStack.getImage()
																												.cursor();

				while (lCursor.hasNext())
				{
					final UnsignedShortType lValue = lCursor.next();
					// System.out.println("size=" + lValue);
					assertEquals(i, lValue.get());
				}
			}

			lLocalFileStackSource.close();
		}

		try
		{
			FileUtils.deleteDirectory(lRootFolder);
		}
		catch (Exception e)
		{
		}

	}
}
